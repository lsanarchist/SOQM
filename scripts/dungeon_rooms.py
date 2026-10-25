#!/usr/bin/env python3
"""Build original, deterministic SOQM expedition rooms for Dimensional Dungeons.

All geometry is authored here. No upstream NBT, textures or room assets are copied.
Room directions follow DD's native unrotated RoomType contract. Ordinary rooms
use a finite set of native data markers, never spawners or ticking commands.
"""
from __future__ import annotations

import argparse
import gzip
import math
from pathlib import Path
import struct

ROOT = Path(__file__).resolve().parents[1]
DESTINATION = ROOT / "overrides/kubejs/data/qm/structure/dimensional_dungeons"
SIZE = (16, 13, 16)
DOORS = {
    "entrance": "NEW", "fourway": "NSEW", "threeway": "SEW",
    "hallway": "NS", "corner": "NE", "end": "S",
}
ROOMS = (
    ("entrance_1", "entrance", 0),
    ("fourway_1", "fourway", 0), ("fourway_2", "fourway", 1),
    ("threeway_1", "threeway", 0),
    ("hallway_1", "hallway", 0), ("hallway_2", "hallway", 1),
    ("corner_1", "corner", 0),
    ("end_1", "end", 0), ("end_2", "end", 1),
)


def _string(value):
    raw = value.encode("utf-8")
    return struct.pack(">H", len(raw)) + raw


def _payload(tag, value):
    if tag == 3:
        return struct.pack(">i", value)
    if tag == 8:
        return _string(value)
    if tag == 9:
        kind, entries = value
        return bytes([kind]) + struct.pack(">i", len(entries)) + b"".join(_payload(kind, entry) for entry in entries)
    if tag == 10:
        return b"".join(bytes([kind]) + _string(name) + _payload(kind, child)
                        for name, (kind, child) in value.items()) + b"\0"
    raise ValueError(f"Unsupported authored NBT type: {tag}")


def block(name, **properties):
    return name, tuple(sorted(properties.items()))


AIR = block("minecraft:air")


class Room:
    def __init__(self, theme, kind, variant):
        self.theme, self.kind, self.variant = theme, kind, variant
        self.blocks = {(x, y, z): (AIR, None) for y in range(SIZE[1])
                       for z in range(SIZE[2]) for x in range(SIZE[0])}
        self.markers = {}
        self.wall = "minecraft:stone_bricks" if theme == "citadel" else "minecraft:polished_blackstone_bricks"
        self.floor = "minecraft:polished_andesite" if theme == "citadel" else "minecraft:deepslate_tiles"
        self.accent = "minecraft:chiseled_stone_bricks" if theme == "citadel" else "minecraft:obsidian"

    def put(self, x, y, z, name, nbt=None, **properties):
        assert 0 <= x < 16 and 0 <= y < 13 and 0 <= z < 16
        self.blocks[x, y, z] = (block(name, **properties), nbt)

    def fill(self, x0, y0, z0, x1, y1, z1, name, **properties):
        for y in range(y0, y1 + 1):
            for z in range(z0, z1 + 1):
                for x in range(x0, x1 + 1):
                    self.put(x, y, z, name, **properties)

    def marker(self, x, y, z, name):
        self.put(x, y, z, "minecraft:structure_block",
                 {"id": (8, "minecraft:structure_block"), "mode": (8, "DATA"),
                  "metadata": (8, name)}, mode="data")
        self.markers[x, y, z] = name

    def chest(self, x, z, locked=False):
        self.put(x, 5, z, "minecraft:chest", {"id": (8, "minecraft:chest")},
                 facing="south", type="single", waterlogged="false")
        self.marker(x, 6, z, "LockWithCode" if locked else "ChestLoot1")

    def shell(self):
        self.fill(0, 0, 0, 15, 3, 15, self.wall)
        self.fill(0, 4, 0, 15, 4, 15, self.floor)
        # Inlaid crossing paths and a coffered ceiling distinguish the chambers
        # without placing decoration in any passage or spawn head volume.
        inlay = "minecraft:smooth_stone" if self.theme == "citadel" else "minecraft:polished_blackstone"
        for x in range(1, 15):
            for z in range(1, 15):
                if x in (6, 9) or z in (6, 9):
                    self.put(x, 4, z, inlay)
        self.fill(0, 12, 0, 15, 12, 15, self.wall)
        for coordinate in (3, 12):
            self.fill(coordinate, 11, 1, coordinate, 11, 14, self.accent)
            self.fill(1, 11, coordinate, 14, 11, coordinate, self.accent)
        for x0, z0, x1, z1 in ((0, 0, 15, 0), (0, 15, 15, 15), (0, 0, 0, 15), (15, 0, 15, 15)):
            self.fill(x0, 5, z0, x1, 11, z1, self.wall)
        for x in (0, 15):
            for z in (0, 15):
                self.fill(x, 4, z, x, 12, z, self.accent)
        for x, z in ((2, 2), (13, 2), (2, 13), (13, 13)):
            self.put(x, 10, z, "minecraft:sea_lantern" if self.theme == "citadel" else "minecraft:shroomlight")
        # Four-block-wide passages accommodate the reviewed Divine enemies.
        for direction in DOORS[self.kind]:
            if direction == "N":
                self.fill(6, 5, 0, 9, 8, 0, "minecraft:air")
            elif direction == "S":
                self.fill(6, 5, 15, 9, 8, 15, "minecraft:air")
            elif direction == "W":
                self.fill(0, 5, 6, 0, 8, 9, "minecraft:air")
            else:
                self.fill(15, 5, 6, 15, 8, 9, "minecraft:air")

    def entrance(self):
        for x in (3, 12):
            self.fill(x, 5, 10, x, 10, 10, self.accent)
        self.fill(6, 4, 13, 9, 4, 15, "minecraft:gold_block")
        self.fill(6, 5, 14, 6, 8, 14, self.accent)
        self.fill(9, 5, 14, 9, 8, 14, self.accent)
        self.fill(6, 8, 14, 9, 8, 14, self.accent)
        # Native key warp is local (8, 55.1, 12.5); reprogramExistingExitDoorway
        # expects these exact six portal blocks at (7..8, 55..57, 14).
        for x in (7, 8):
            for y in (5, 6, 7):
                self.marker(x, y, 14, "ReturnPortal")
        for z in range(2, 13):
            self.put(7, 4, z, "minecraft:red_concrete" if self.theme == "citadel" else "minecraft:purple_concrete")

    def citadel(self):
        if self.kind in {"fourway", "threeway", "corner"}:
            for x, z in ((4, 4), (11, 4), (4, 11), (11, 11)):
                self.fill(x, 5, z, x, 10, z, self.accent)
            if self.variant:
                # Two staggered barricades break line of sight without sealing a route.
                self.fill(5, 5, 3, 10, 6, 3, self.wall)
                self.fill(5, 5, 12, 10, 6, 12, self.wall)
            for x, z in ((3, 7), (12, 8), (7, 5 if self.variant else 3)):
                self.marker(x, 5, z, "SummonEnemy1")
            self.marker(8, 5, 10 if self.variant else 12, "SummonEnemy2")
            if self.kind == "fourway" and self.variant:
                self.chest(2, 2)
        elif self.kind == "hallway":
            if self.variant == 0:
                self.fill(2, 5, 5, 10, 7, 5, self.wall)
                self.fill(5, 5, 10, 13, 7, 10, self.wall)
                points = ((12, 5, 4), (3, 5, 9), (8, 5, 12))
            else:
                # Open firing lanes with four independent cover blocks.
                for x, z in ((4, 5), (11, 5), (4, 10), (11, 10)):
                    self.fill(x, 5, z, x, 6, z, self.accent)
                points = ((3, 5, 3), (12, 5, 12), (8, 5, 8))
                self.chest(2, 8)
            for i, point in enumerate(points):
                self.marker(*point, "SummonEnemy2" if i == 2 else "SummonEnemy1")
        else:
            for x in (4, 11):
                self.fill(x, 5, 4, x, 9, 4, self.accent)
            if self.variant:
                for x in (3, 12):
                    self.fill(x, 5, 7, x, 6, 10, self.wall)
            self.chest(7, 2, locked=True)
            self.marker(8, 5, 5, "SummonKeyholder")
            for point in ((3, 5, 8), (12, 5, 8), (7, 5, 12)):
                if self.variant and point[0] in (3, 12):
                    point = (5 if point[0] == 3 else 10, point[1], point[2])
                self.marker(*point, "SummonEnemy1")

    def abyss(self):
        # A recoverable four-block-deep trench surrounds broad crossing paths.
        # There is no void fall, lava, spawner or permanent timer.
        for x in range(2, 14):
            for z in range(2, 14):
                if not (6 <= x <= 9 or 6 <= z <= 9):
                    self.fill(x, 1, z, x, 4, z, "minecraft:air")
                    self.put(x, 0, z, "minecraft:magma_block" if (x + z) % 3 == 0 else self.floor)
        for x in (3, 12):
            for z, facing in ((1, "south"), (14, "north")):
                for y in range(1, 5):
                    self.put(x, y, z, "minecraft:ladder", facing=facing, waterlogged="false")
        # Perimeter ledges and cross bridges remain connected at floor height.
        if self.kind in {"hallway", "corner"} and self.variant:
            self.fill(6, 4, 3, 9, 4, 12, "minecraft:packed_ice")
        for x, z in ((4, 7), (11, 8)):
            self.fill(x, 5, z, x, 7, z, self.accent)
        if self.kind == "end":
            self.fill(5, 4, 1, 10, 4, 5, self.floor)
            if self.variant:
                self.fill(6, 4, 8, 9, 4, 12, "minecraft:packed_ice")
                self.fill(2, 5, 3, 2, 7, 5, self.accent)
                self.fill(13, 5, 3, 13, 7, 5, self.accent)
            self.chest(7, 2, locked=True)
            self.marker(8, 5, 5, "SummonKeyholder")
            points = ((3, 5, 9), (13, 5, 7), (7, 5, 11))
        else:
            points = ((7, 5, 3), (8, 5, 12), (13, 5, 7))
            if self.kind == "fourway" and self.variant:
                self.chest(1, 1)
            if self.kind == "hallway" and self.variant:
                self.chest(1, 8)
        for i, point in enumerate(points):
            self.marker(*point, "SummonEnemy2" if i >= 2 else "SummonEnemy1")

    def encode(self):
        palette = []
        indexes = {}
        blocks = []
        for pos, (state, nbt) in sorted(self.blocks.items(), key=lambda row: (row[0][1], row[0][2], row[0][0])):
            if state not in indexes:
                indexes[state] = len(palette)
                name, properties = state
                entry = {"Name": (8, name)}
                if properties:
                    entry["Properties"] = (10, {key: (8, value) for key, value in properties})
                palette.append(entry)
            entry = {"pos": (9, (3, list(pos))), "state": (3, indexes[state])}
            if nbt:
                entry["nbt"] = (10, nbt)
            blocks.append(entry)
        root = {"DataVersion": (3, 3955), "size": (9, (3, list(SIZE))),
                "palette": (9, (10, palette)), "blocks": (9, (10, blocks)), "entities": (9, (10, []))}
        return gzip.compress(b"\x0a\x00\x00" + _payload(10, root), compresslevel=9, mtime=0)

    def validate_spawns(self):
        """Reserve a conservative 2×3×2 body for every finite hostile marker."""
        for (x, y, z), marker in self.markers.items():
            if not marker.startswith("Summon"):
                continue
            for bx in range(math.floor(x + .5 - 1), math.ceil(x + .5 + 1)):
                for bz in range(math.floor(z + .5 - 1), math.ceil(z + .5 + 1)):
                    for by in range(y, y + 3):
                        state, nbt = self.blocks[bx, by, bz]
                        if state != AIR and not self.markers.get((bx, by, bz), "").startswith("Summon"):
                            raise ValueError(f"Blocked spawn {marker} at {(x, y, z)}: {state} at {(bx, by, bz)}")


def build_rooms():
    for theme in ("citadel", "abyss"):
        for suffix, kind, variant in ROOMS:
            room = Room(theme, kind, variant)
            room.shell()
            if kind == "entrance":
                room.entrance()
            elif theme == "citadel":
                room.citadel()
            else:
                room.abyss()
            room.validate_spawns()
            yield f"{theme}/{suffix}", room


def outputs():
    return {DESTINATION / f"{name}.nbt": room.encode() for name, room in build_rooms()}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    stale = []
    for path, content in outputs().items():
        if args.check:
            if not path.is_file() or path.read_bytes() != content:
                stale.append(path.relative_to(ROOT).as_posix())
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(content)
    if stale:
        raise SystemExit("Stale original expedition rooms: " + ", ".join(stale))
    print("18 original expedition templates are synchronized.")


if __name__ == "__main__":
    main()
