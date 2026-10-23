#!/usr/bin/env python3
"""Build the client JEI source-slot correction against the pinned native APIs."""
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
UNIVERSAL_SHA256 = "4070305f97a08409d9b4e714f619f0ec2d97d29c9939ef43a89d927259942f98"
MIXIN_SHA256 = "921a2ca066d82d15d2939efd5eb22754e308db74b1e4277130a76625c3638dfd"
MIXIN_EXTRAS_SHA256 = "f0c3e3c1ad835b80e70cb3f78359b71f107f74689523b4fb391f160ee487cfbf"
MOD_PINS = {
    "sophisticatedcore-1.21.1-1.5.14.2413.jar":
        "3abac9ba876a2eb19ce7ab9c080b81a5383f286d6e54b7d0912b29401aca9c2c",
    "jei-1.21.1-neoforge-19.64.0.455.jar":
        "f7a9de3d7ee07eed8d293a1a1a0f6afaac556eb43c103523457bd07ebb551e7b",
}
TARGET_CLASS = (
    "net/p3pp3rf1y/sophisticatedcore/compat/recipeviewers/jei/CraftingSpecCategoryExtension.class"
)
TARGET_SHA256 = "422d00aabf7a4a36d155ba7aaa121c927ec0ba0ea202a41dddc37ecbbeec1386"
MANIFEST = (
    "Manifest-Version: 1.0\r\n"
    "FMLModType: MOD\r\n"
    "Automatic-Module-Name: qm.storage.jei.compat\r\n\r\n"
).encode("ascii")


def sha256(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def require_hash(path, expected, description):
    if not path.is_file() or sha256(path) != expected:
        raise ValueError("Missing or mismatched " + description + ": " + str(path))


def compile_dependencies(runtime, mods, temporary):
    libraries = runtime / "libraries"
    native = libraries / "net/neoforged/neoforge" / NEOFORGE / f"neoforge-{NEOFORGE}-server.jar"
    universal = libraries / "net/neoforged/neoforge" / NEOFORGE / f"neoforge-{NEOFORGE}-universal.jar"
    mixin = (libraries / "net/fabricmc/sponge-mixin/0.16.4+mixin.0.8.7" /
             "sponge-mixin-0.16.4+mixin.0.8.7.jar")
    require_hash(native, NATIVE_SHA256, "mapped NeoForge server 21.1.262")
    require_hash(universal, UNIVERSAL_SHA256, "NeoForge universal 21.1.262")
    require_hash(mixin, MIXIN_SHA256, "Sponge Mixin 0.8.7")
    dependencies = [native, *[p for p in sorted(libraries.rglob("*.jar")) if p != native]]
    lock = {row["filename"]: row for row in
            json.loads((REPOSITORY / "metadata/mods.lock.json").read_text())["mods"]}
    for filename, expected in MOD_PINS.items():
        if filename not in lock or lock[filename]["sha256"] != expected:
            raise ValueError("Unsupported dependency pin: " + filename)
        path = mods / filename
        require_hash(path, expected, "dependency archive")
        dependencies.append(path)
    with ZipFile(mods / "sophisticatedcore-1.21.1-1.5.14.2413.jar") as archive:
        if hashlib.sha256(archive.read(TARGET_CLASS)).hexdigest() != TARGET_SHA256:
            raise ValueError("Unsupported CraftingSpecCategoryExtension implementation")
    with ZipFile(universal) as archive:
        entry = "META-INF/jarjar/mixinextras-neoforge-0.5.5.jar"
        contents = archive.read(entry)
        if hashlib.sha256(contents).hexdigest() != MIXIN_EXTRAS_SHA256:
            raise ValueError("Unsupported MixinExtras implementation")
        extras = temporary / "mixinextras-neoforge-0.5.5.jar"
        extras.write_bytes(contents)
        dependencies.append(extras)
    return dependencies


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, required=True,
                        help="NeoForge 21.1.262 server installation (libraries/ parent); no world is started")
    parser.add_argument("--mods", type=Path, default=REPOSITORY / ".cache/mods",
                        help="Directory containing the pinned Sophisticated Core and JEI archives")
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
              f"qm-storage-jei-compat-{mod_version}.jar").resolve()

    with tempfile.TemporaryDirectory(prefix="soqm-storage-jei-compile-") as directory:
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
