#!/usr/bin/env python3
"""Read-only check for legacy Iron Chests references before the 0.4.10 update.

Run against a backup of a STOPPED world, using ordinary Python 3. No files are
changed. This is a conservative reference audit, not a container converter.
Unknown compression, malformed chunks and unreadable files fail closed.
"""

from __future__ import annotations

import argparse
import json
import os
from pathlib import Path
import re
import stat
import struct
import sys
import zlib

LIMIT = 64 * 1024 * 1024
NBT_DEPTH_LIMIT = 512
NBT_WORK_LIMIT = 1_000_000
NBT_FILES = {".mca", ".mcc", ".dat", ".nbt"}
LEGACY = re.compile(rb"ironchest:[a-z0-9_./-]+")
REGION = re.compile(r"r\.(-?\d+)\.(-?\d+)\.mca\Z")


def decode(data: bytes, codec: int) -> bytes:
    if len(data) > LIMIT:
        raise ValueError("Compressed record exceeds the audit size limit")
    if codec == 3:
        return data
    if codec not in (1, 2):
        raise ValueError(f"Unsupported chunk compression {codec}; the audit is incomplete")
    inflater = zlib.decompressobj(31 if codec == 1 else 15)
    result = inflater.decompress(data, LIMIT + 1)
    if len(result) > LIMIT or not inflater.eof or inflater.unused_data:
        raise ValueError("Oversized, truncated or trailing compressed data")
    return result


class NbtReader:
    """Validate serialized NBT without allocating its compound/list contents."""

    def __init__(self, data: bytes):
        if len(data) > LIMIT:
            raise ValueError("NBT record exceeds the audit size limit")
        self.data = data
        self.position = 0

    def skip(self, length: int) -> int:
        start = self.position
        if length < 0 or length > len(self.data) - start:
            raise ValueError("Truncated NBT payload or invalid length")
        self.position += length
        return start

    def number(self, format: str, size: int) -> int:
        return struct.unpack_from(format, self.data, self.skip(size))[0]

    def string(self):
        length = self.number(">H", 2)
        position = self.skip(length)
        end = position + length
        # Java NBT uses modified UTF-8: NUL may use C0 80 and UTF-16
        # surrogates use three bytes. Reject invalid/overlong ASCII encodings
        # so a malformed string cannot hide a registry ID from the raw scan.
        while position < end:
            first = self.data[position]
            if first < 128:
                width = 1
            elif first == 0xC0 and position + 1 < end and self.data[position + 1] == 0x80:
                width = 2
            elif 0xC2 <= first <= 0xDF:
                width = 2
            elif 0xE0 <= first <= 0xEF:
                width = 3
            else:
                raise ValueError("Invalid modified UTF-8 in NBT string")
            if position + width > end or any(not 0x80 <= self.data[index] <= 0xBF
                    for index in range(position + 1, position + width)):
                raise ValueError("Truncated or invalid modified UTF-8 in NBT string")
            if first == 0xE0 and self.data[position + 1] < 0xA0:
                raise ValueError("Overlong modified UTF-8 in NBT string")
            position += width

    def validate(self):
        if self.number(">B", 1) != 10:
            raise ValueError("Expected a root NBT compound")
        self.string()
        # A negative count is an open compound frame. Other frames represent
        # repeated payloads; large lists never create large Python lists.
        pending = [(10, -1, 0)]
        fixed = {1: 1, 2: 2, 3: 4, 4: 8, 5: 4, 6: 8}
        minimum = {**fixed, 7: 4, 8: 2, 9: 5, 10: 1, 11: 4, 12: 4}
        work = 0
        while pending:
            kind, count, depth = pending.pop()
            work += 1
            if depth > NBT_DEPTH_LIMIT or work > NBT_WORK_LIMIT:
                raise ValueError("NBT nesting or structural work exceeds the audit limit")
            if kind == 10 and count == -1:
                child = self.number(">B", 1)
                if child == 0:
                    continue
                if child not in minimum:
                    raise ValueError(f"Unknown NBT tag type {child}")
                self.string()
                pending.append((10, -1, depth))
                pending.append((child, 1, depth + 1))
                continue
            if count == 0:
                continue
            if kind in fixed:
                self.skip(fixed[kind] * count)
                continue
            if count > 1:
                pending.append((kind, count - 1, depth))
            if kind in (7, 11, 12):
                length = self.number(">i", 4)
                self.skip(length * {7: 1, 11: 4, 12: 8}[kind])
            elif kind == 8:
                self.string()
            elif kind == 9:
                element = self.number(">B", 1)
                length = self.number(">i", 4)
                if length < 0 or element not in range(13) or (element == 0 and length != 0):
                    raise ValueError("Invalid NBT list element type or length")
                if length:
                    if length > (len(self.data) - self.position) // minimum[element]:
                        raise ValueError("NBT list length exceeds its available payload")
                    pending.append((element, length, depth + 1))
            elif kind == 10:
                pending.append((10, -1, depth))
        if self.position != len(self.data):
            raise ValueError("Trailing data after the root NBT compound")


def references(data: bytes) -> list[str]:
    NbtReader(data).validate()
    return sorted({value.decode("ascii") for value in LEGACY.findall(data)})


def inventory(world: Path) -> tuple[list[Path], dict, list[dict]]:
    """Inspect every directory explicitly; globbing silently skips some errors."""
    paths, stamps, errors = [], {}, []
    pending = [world]
    while pending:
        path = pending.pop()
        relative = path.relative_to(world).as_posix()
        try:
            info = path.lstat()
            stamps[path] = (info.st_dev, info.st_ino, info.st_mode, info.st_size,
                            info.st_mtime_ns, info.st_ctime_ns)
            if stat.S_ISLNK(info.st_mode):
                raise ValueError("Symlink files and directories are not followed; the audit is incomplete")
            if stat.S_ISDIR(info.st_mode):
                with os.scandir(path) as entries:
                    pending.extend(sorted((Path(entry.path) for entry in entries), reverse=True))
            elif stat.S_ISREG(info.st_mode):
                if path.suffix in NBT_FILES:
                    paths.append(path)
            else:
                raise ValueError("Non-regular world entry cannot be audited")
        except (OSError, ValueError) as error:
            errors.append({"file": relative, "error": str(error)})
    return sorted(paths), stamps, errors


def region_records(path: Path, world: Path, external_seen: set[Path]):
    match = REGION.fullmatch(path.name)
    if match is None:
        raise ValueError("Unknown region filename")
    rx, rz = map(int, match.groups())
    length = path.stat().st_size
    # Pinned Minecraft 1.21.1 RegionFile accepts immediate EOF with zero
    # offsets, and close() leaves a never-written region at exactly zero bytes.
    if length == 0:
        return
    if length < 8192 or length % 4096:
        raise ValueError("Truncated region file")
    used = {0, 1}
    with path.open("rb") as source:
        header = source.read(4096)
        for index in range(1024):
            location = int.from_bytes(header[index * 4:index * 4 + 4], "big")
            if location == 0:
                continue
            sector, count = location >> 8, location & 255
            allocated = set(range(sector, sector + count))
            if not count or sector < 2 or (sector + count) * 4096 > length or used & allocated:
                raise ValueError(f"Invalid or overlapping chunk sectors at header entry {index}")
            used.update(allocated)
            source.seek(sector * 4096)
            size = struct.unpack(">I", source.read(4))[0]
            if not 1 <= size <= count * 4096 - 4:
                raise ValueError(f"Invalid chunk length at header entry {index}")
            codec = struct.unpack(">B", source.read(1))[0]
            cx, cz = rx * 32 + index % 32, rz * 32 + index // 32
            if codec & 128:
                external = path.with_name(f"c.{cx}.{cz}.mcc")
                if size != 1 or not external.is_file() or external.is_symlink():
                    raise ValueError(f"Missing or malformed external chunk {cx},{cz}")
                if external.stat().st_size > LIMIT:
                    raise ValueError("External chunk exceeds the audit size limit")
                external_seen.add(external)
                payload = external.read_bytes()
            else:
                payload = source.read(size - 1)
            yield {"file": path.relative_to(world).as_posix(), "chunk": [cx, cz]}, decode(payload, codec & 127)


def audit(world: Path) -> dict:
    world = world.resolve(strict=True)
    if not world.is_dir() or not (world / "level.dat").is_file():
        raise ValueError("Provide a world directory containing level.dat")
    paths, before, traversal_errors = inventory(world)
    result = {"schema": 1, "scope": "Read-only conservative Iron Chests reference scan of a stopped-world backup; no conversion or deletion.",
              "files": len(paths), "nbt_records": 0, "references": [], "errors": traversal_errors}
    external_seen: set[Path] = set()
    for path in paths:
        relative = path.relative_to(world).as_posix()
        try:
            if path.is_symlink() or not path.is_file():
                raise ValueError("Symlinks and non-regular files are not followed")
            info = path.stat()
            if path.suffix == ".mcc":
                continue
            if path.suffix == ".mca":
                records = region_records(path, world, external_seen)
            else:
                if info.st_size > LIMIT:
                    raise ValueError("NBT file exceeds the audit size limit")
                data = path.read_bytes()
                records = [({"file": relative}, decode(data, 1 if data.startswith(b"\x1f\x8b") else 3))]
            for location, record in records:
                found = references(record)
                result["nbt_records"] += 1
                if found:
                    result["references"].append({**location, "ids": found})
        except (OSError, ValueError, zlib.error, struct.error) as error:
            result["errors"].append({"file": relative, "error": str(error)})
    for path in paths:
        try:
            if path.suffix == ".mcc" and path not in external_seen:
                raise ValueError("Unreferenced external chunk was not decoded")
        except (OSError, ValueError) as error:
            result["errors"].append({"file": path.relative_to(world).as_posix(), "error": str(error)})
    _, after, traversal_errors = inventory(world)
    for error in traversal_errors:
        if error not in result["errors"]:
            result["errors"].append(error)
    for path in sorted(before.keys() | after.keys()):
        if before.get(path) != after.get(path):
            result["errors"].append({"file": path.relative_to(world).as_posix(),
                                     "error": "World paths or metadata changed during the audit; stop the game and use a stable backup"})
    result["complete"] = not result["errors"]
    result["legacy_references_found"] = bool(result["references"])
    result["ready_for_removal"] = result["complete"] and not result["legacy_references_found"]
    return result


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--world", type=Path, required=True, help="Backup directory of a stopped Minecraft world")
    args = parser.parse_args()
    try:
        result = audit(args.world)
    except (OSError, ValueError) as error:
        parser.exit(3, f"Storage audit incomplete: {error}\n")
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 3 if not result["complete"] else 2 if result["legacy_references_found"] else 0


if __name__ == "__main__":
    sys.exit(main())
