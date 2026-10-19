#!/usr/bin/env python3
"""Deterministically build against the pinned NeoForge server and mod APIs."""
from __future__ import annotations

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import tempfile
import tomllib
from zipfile import ZIP_DEFLATED, ZipFile, ZipInfo


MODULE = Path(__file__).resolve().parent
REPOSITORY = MODULE.parents[2]
NEOFORGE = "21.1.262"
NATIVE_SHA256 = "f57ce3018e21459d24b50508e8c1b4fd5c16324ec1dc5f316c192aa0130e7fe0"
API_MODS = (
    "appliedenergistics2-19.2.18.jar",
    "Mekanism-1.21.1-10.7.19.85.jar",
    "ftb-teams-neoforge-2101.1.9.jar",
    "ftb-quests-neoforge-2101.1.36.jar",
    "ftb-library-neoforge-2101.1.37.jar",
    "architectury-13.0.8-neoforge.jar",
    "sophisticatedcore-1.21.1-1.5.14.2413.jar",
    "sophisticatedstorage-1.21.1-1.6.7.2184.jar",
)
MANIFEST = (
    "Manifest-Version: 1.0\r\n"
    "FMLModType: MOD\r\n"
    "Automatic-Module-Name: qm.progressive.rewards\r\n\r\n"
).encode("ascii")


def sha256(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, required=True,
                        help="Pinned NeoForge server installation containing libraries/")
    parser.add_argument("--javac", required=True, help="Java 21 javac executable")
    parser.add_argument("--mods", type=Path, default=REPOSITORY / ".cache/mods",
                        help="Downloaded pinned mod JARs (verified against mods.lock.json)")
    parser.add_argument("--output", type=Path,
                        help="Defaults to the versioned JAR under overrides/mods/")
    args = parser.parse_args()
    version = subprocess.run([args.javac, "-version"], check=True,
                             capture_output=True, text=True)
    compiler = (version.stdout + version.stderr).strip()
    if not re.fullmatch(r"javac 21(?:\.[^\s]+)?", compiler):
        parser.error("A Java 21 compiler is required")

    libraries = args.runtime.resolve() / "libraries"
    native = libraries / "net/neoforged/neoforge" / NEOFORGE / f"neoforge-{NEOFORGE}-server.jar"
    if not native.is_file() or sha256(native) != NATIVE_SHA256:
        parser.error("The mapped NeoForge server JAR does not match the pinned runtime")
    lock = json.loads((REPOSITORY / "metadata/mods.lock.json").read_text())
    by_filename = {row["filename"]: row for row in lock["mods"]}
    mods = []
    for filename in API_MODS:
        pin = by_filename[filename]
        path = args.mods.resolve() / filename
        if (not path.is_file() or path.stat().st_size != pin["bytes"]
                or sha256(path) != pin["sha256"]):
            parser.error(f"Missing or mismatched pinned mod API: {filename}")
        mods.append(path)

    # Patched Minecraft must precede the unpatched srg JAR. Production Mojmap
    # injectors are remap=false; no annotation processor or generated refmap.
    dependencies = [native] + [p for p in sorted(libraries.rglob("*.jar")) if p != native] + mods
    classpath = os.pathsep.join(str(p) for p in dependencies)
    metadata = tomllib.loads((MODULE / "resources/META-INF/neoforge.mods.toml").read_text())
    mod_version = metadata["mods"][0]["version"]
    output = (args.output or REPOSITORY / "overrides/mods" /
              f"qm-progressive-rewards-{mod_version}.jar").resolve()

    with tempfile.TemporaryDirectory(prefix="soqm-production-compile-") as directory:
        classes = Path(directory) / "classes"
        classes.mkdir()
        sources = sorted((MODULE / "src").rglob("*.java"))
        subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-proc:none",
                        "-classpath", classpath, "-d", str(classes), *map(str, sources)], check=True)
        entries = {"META-INF/MANIFEST.MF": MANIFEST,
                   "LICENSE": (MODULE / "LICENSE").read_bytes()}
        for root in (classes, MODULE / "resources"):
            for path in sorted(root.rglob("*")):
                if path.is_file():
                    entries[path.relative_to(root).as_posix()] = path.read_bytes()
        output.parent.mkdir(parents=True, exist_ok=True)
        with ZipFile(output, "w", compression=ZIP_DEFLATED, compresslevel=9) as archive:
            for name, data in sorted(entries.items()):
                info = ZipInfo(name, (1980, 1, 1, 0, 0, 0))
                info.create_system = 3
                info.external_attr = 0o100644 << 16
                info.compress_type = ZIP_DEFLATED
                archive.writestr(info, data, compresslevel=9)
    print(f"Compiler: {compiler}")
    print(f"Built {output.name}: {output.stat().st_size} bytes")
    print(f"SHA-256: {sha256(output)}")


if __name__ == "__main__":
    main()
