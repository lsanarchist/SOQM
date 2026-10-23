#!/usr/bin/env python3
"""Build the Foundry slot correction against the pinned native runtime."""
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
MOD_PINS = {
    "ModernFoundry-1.21.1-4.1.6-NeoForge.jar":
        "0b0026db1c27a1d08b1e3d3eba8fd2cc470d4cb534eb08d74633adc5be5691f1",
    "Hilt-1.21.1-1.14.jar":
        "9250b06f442885807c14b1173350a6eb5984c6279748a2d343b44dd229e84e36",
}
TARGET_CLASS = (
    "modernmods/modernfoundry/tables/block/entity/table/CraftingStationBlockEntity.class"
)
TARGET_SHA256 = "0707ddb5efd94481ef2f35a10df9f61ef12b49e9bbe452a2e7ac90b50a5c757d"
MANIFEST = (
    "Manifest-Version: 1.0\r\n"
    "FMLModType: MOD\r\n"
    "Automatic-Module-Name: qm.foundry.compat\r\n\r\n"
).encode("ascii")


def sha256(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def compile_dependencies(runtime, mods, temporary):
    libraries = runtime / "libraries"
    native = libraries / "net/neoforged/neoforge" / NEOFORGE / f"neoforge-{NEOFORGE}-server.jar"
    if not native.is_file() or sha256(native) != NATIVE_SHA256:
        raise ValueError("Mapped NeoForge server differs from the 21.1.262 pin")
    dependencies = [native, *[p for p in sorted(libraries.rglob("*.jar")) if p != native]]
    lock = {row["filename"]: row for row in
            json.loads((REPOSITORY / "metadata/mods.lock.json").read_text())["mods"]}
    for filename, expected in MOD_PINS.items():
        path = mods / filename
        if filename not in lock or lock[filename]["sha256"] != expected:
            raise ValueError("Unsupported dependency pin: " + filename)
        if not path.is_file() or sha256(path) != expected:
            raise ValueError("Missing or mismatched dependency: " + filename)
        dependencies.append(path)
    with ZipFile(mods / "ModernFoundry-1.21.1-4.1.6-NeoForge.jar") as archive:
        if hashlib.sha256(archive.read(TARGET_CLASS)).hexdigest() != TARGET_SHA256:
            raise ValueError("Unsupported Crafting Station implementation")
    universal = libraries / "net/neoforged/neoforge" / NEOFORGE / f"neoforge-{NEOFORGE}-universal.jar"
    with ZipFile(universal) as archive:
        entry = "META-INF/jarjar/mixinextras-neoforge-0.5.5.jar"
        extras = temporary / "mixinextras-neoforge-0.5.5.jar"
        extras.write_bytes(archive.read(entry))
        dependencies.append(extras)
    return dependencies


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, required=True,
                        help="NeoForge 21.1.262 server installation (libraries/ parent)")
    parser.add_argument("--mods", type=Path, default=REPOSITORY / ".cache/mods",
                        help="Directory containing the pinned Modern Foundry and Hilt archives")
    parser.add_argument("--javac", required=True, help="Java 21 javac executable")
    parser.add_argument("--output", type=Path,
                        help="Defaults to the versioned JAR under overrides/mods/")
    args = parser.parse_args()

    version = subprocess.run([args.javac, "-version"], check=True,
                             capture_output=True, text=True)
    compiler = (version.stdout + version.stderr).strip()
    if not re.fullmatch(r"javac 21(?:\.[^\s]+)?", compiler):
        parser.error("A Java 21 compiler is required")

    metadata = tomllib.loads((MODULE / "resources/META-INF/neoforge.mods.toml").read_text())
    mod_version = metadata["mods"][0]["version"]
    output = (args.output or REPOSITORY / "overrides/mods" /
              f"qm-foundry-compat-{mod_version}.jar").resolve()

    with tempfile.TemporaryDirectory(prefix="soqm-foundry-compile-") as directory:
        temporary = Path(directory)
        dependencies = compile_dependencies(args.runtime.resolve(), args.mods.resolve(), temporary)
        classes = temporary / "classes"
        classes.mkdir()
        sources = sorted((MODULE / "src").rglob("*.java"))
        subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-proc:none",
                        "-classpath", os.pathsep.join(map(str, dependencies)), "-d", str(classes),
                        *map(str, sources)], check=True)

        entries = {"META-INF/MANIFEST.MF": MANIFEST, "LICENSE": (MODULE / "LICENSE").read_bytes()}
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
                      "target_sha256": TARGET_SHA256, "filename": output.name,
                      "bytes": output.stat().st_size, "sha256": sha256(output)}, indent=2))


if __name__ == "__main__":
    main()
