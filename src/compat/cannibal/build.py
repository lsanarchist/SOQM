#!/usr/bin/env python3
"""Rebuild this module against an installed, pinned NeoForge server runtime."""
from __future__ import annotations

import argparse
import hashlib
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
MANIFEST = (
    "Manifest-Version: 1.0\r\n"
    "FMLModType: MOD\r\n"
    "Automatic-Module-Name: qm.cannibal.compat\r\n\r\n"
).encode("ascii")


def sha256(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, required=True,
                        help="NeoForge 21.1.262 server installation (libraries/ parent)")
    parser.add_argument("--javac", required=True, help="Java 21 javac executable")
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

    # Put patched Minecraft first; annotation processing is deliberately disabled
    # because all three injectors use the production Mojmap names (remap=false).
    dependencies = [native] + [p for p in sorted(libraries.rglob("*.jar")) if p != native]
    classpath = os.pathsep.join(str(p) for p in dependencies)
    metadata = tomllib.loads((MODULE / "resources/META-INF/neoforge.mods.toml").read_text())
    mod_version = metadata["mods"][0]["version"]
    output = (args.output or REPOSITORY / "overrides/mods" /
              f"qm-cannibal-compat-{mod_version}.jar").resolve()

    with tempfile.TemporaryDirectory(prefix="soqm-cannibal-compile-") as directory:
        classes = Path(directory) / "classes"
        classes.mkdir()
        sources = sorted((MODULE / "src").rglob("*.java"))
        subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8",
                        "-proc:none", "-classpath", classpath, "-d", str(classes),
                        *map(str, sources)], check=True)

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
