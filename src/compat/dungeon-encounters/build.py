#!/usr/bin/env python3
"""Deterministically build in-place dungeon encounters against pinned native APIs."""
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
API_PINS = {
    "dimdungeons-210-neoforge-1.21.1.jar": "8cc55efc96ad15ec4b9ab7ea649f8a281f19b527e3692c17cf912dac91f94f41",
    "elitemobs-6.2.3+neoforge-1.21.1.jar": "b31217d07b60c542ad8e1aab03c052fef517d59f8310a99869ebe378a1d1cf88",
    "Apotheosis-1.21.1-8.9.0.jar": "23a8b4a21d0d8744f097c77c8a96fcba14c4b95f3b1bb542412f40dab272872e",
    "ApothicAttributes-1.21.1-2.11.0.jar": "17c42bb6dd5c0d820fe090e1bb5959578cd5ab7b70caf98a2a59b319fcc7e1ba",
    "Placebo-1.21.1-9.9.4.jar": "d8952c019d6e54befe37155bc9876985ec57bf73dd33872115c7a65ac6bceba0",
    "divinerpg-1.10.10.jar": "3ffc8939ab335fa1613a7aca52faadb206f85ba44898fd69e61b1a8a0042f0ba",
}
HOOK_PINS = {
    "dimdungeons-210-neoforge-1.21.1.jar": {
        "com/catastrophe573/dimdungeons/structure/DungeonPlacement.class": "0c6312ba7b9ca87147c15acc2429348a7b05813886e450d784dbebd231c97d9a",
        "com/catastrophe573/dimdungeons/item/ItemBlankThemeKey.class": "22f3ab4dbec439c69f69ea175638746a5d9e48bc1200042f71a3ad7ccb0b4a49",
        "com/catastrophe573/dimdungeons/block/BlockGoldPortal.class": "a25107744fd77d76f532c76dfb170d5db834e616133147c379eec16211f9e99e",
        "com/catastrophe573/dimdungeons/block/BlockPortalKeyhole.class": "bb779b1a78421d0be5ca22c073b115580fdd2f95a8c89a165052947f6b1a0c80",
        "com/catastrophe573/dimdungeons/dimension/DungeonData.class": "ab673145990ce36069d35158dbfbedc1fa697e9726f8a60d97ec2d2fa924b7fe",
    },
    "elitemobs-6.2.3+neoforge-1.21.1.jar": {
        "net/tonicarter/elitemobs/EliteMobManager.class": "ab054531a734ede58c6be0afe8fd27c136ba35ed8862cc95edbd4f94b533f59d",
        "net/tonicarter/elitemobs/affixes/AffixState.class": "403f485522a054b8d89f5f8a822833e52422d273b9421df6c3095ab98b891a4f",
    },
}
MOB_COMPAT_SHA256 = "c99c3a9d50931d999c47f302fcae60781a14cf4797373911007e620748cafe66"


def digest(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def require_hash(path, expected):
    if not path.is_file() or digest(path) != expected:
        raise ValueError("Missing or mismatched pinned API: " + str(path))


def compile_dependencies(runtime, mods):
    libraries = runtime / "libraries"
    native = libraries / "net/neoforged/neoforge" / NEOFORGE / f"neoforge-{NEOFORGE}-server.jar"
    require_hash(native, NATIVE_SHA256)
    dependencies = [native, *[p for p in sorted(libraries.rglob("*.jar")) if p != native]]
    lock = {row["filename"]: row for row in
            json.loads((REPOSITORY / "metadata/mods.lock.json").read_text())["mods"]}
    for filename, expected in API_PINS.items():
        if filename not in lock or lock[filename]["sha256"] != expected:
            raise ValueError("mods.lock.json does not contain the supported pin: " + filename)
        path = mods / filename
        require_hash(path, expected)
        dependencies.append(path)
        with ZipFile(path) as archive:
            for name, wanted in HOOK_PINS.get(filename, {}).items():
                if hashlib.sha256(archive.read(name)).hexdigest() != wanted:
                    raise ValueError("Pinned hook target changed: " + name)
    compat = REPOSITORY / "overrides/mods/qm-mob-compat-0.4.5.jar"
    require_hash(compat, MOB_COMPAT_SHA256)
    dependencies.append(compat)
    return dependencies


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, required=True,
                        help="NeoForge 21.1.262 server installation (libraries/ parent)")
    parser.add_argument("--mods", type=Path, default=REPOSITORY / ".cache/mods")
    parser.add_argument("--javac", required=True, help="Java 21 javac executable")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    version = subprocess.run([args.javac, "-version"], check=True, capture_output=True, text=True)
    compiler = (version.stdout + version.stderr).strip()
    if not re.fullmatch(r"javac 21(?:\.[^\s]+)?", compiler):
        parser.error("A Java 21 compiler is required")
    dependencies = compile_dependencies(args.runtime.resolve(), args.mods.resolve())
    metadata = tomllib.loads((MODULE / "resources/META-INF/neoforge.mods.toml").read_text())
    mod_version = metadata["mods"][0]["version"]
    output = (args.output or REPOSITORY / "overrides/mods" /
              f"qm-dungeon-encounters-{mod_version}.jar").resolve()
    with tempfile.TemporaryDirectory(prefix="soqm-dungeon-encounters-") as directory:
        classes = Path(directory) / "classes"
        classes.mkdir()
        sources = sorted((MODULE / "src").rglob("*.java"))
        subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-proc:none",
                        "-classpath", os.pathsep.join(map(str, dependencies)), "-d", str(classes),
                        *map(str, sources)], check=True)
        entries = {
            "META-INF/MANIFEST.MF": ("Manifest-Version: 1.0\r\nFMLModType: MOD\r\n"
                "Automatic-Module-Name: qm.dungeon.encounters\r\n\r\n").encode("ascii"),
            "LICENSE": (MODULE / "LICENSE").read_bytes(),
        }
        for root in (classes, MODULE / "resources"):
            for path in sorted(root.rglob("*")):
                if path.is_file():
                    entries[path.relative_to(root).as_posix()] = path.read_bytes()
        output.parent.mkdir(parents=True, exist_ok=True)
        with ZipFile(output, "w", compression=ZIP_DEFLATED, compresslevel=9) as archive:
            for name, content in sorted(entries.items()):
                info = ZipInfo(name, (1980, 1, 1, 0, 0, 0))
                info.create_system = 3
                info.external_attr = 0o100644 << 16
                info.compress_type = ZIP_DEFLATED
                archive.writestr(info, content, compresslevel=9)
    print(json.dumps({"compiler": compiler, "native_sha256": NATIVE_SHA256,
                      "hook_pins": HOOK_PINS, "filename": output.name,
                      "bytes": output.stat().st_size, "sha256": digest(output)}, indent=2))


if __name__ == "__main__":
    main()
