#!/usr/bin/env python3
"""Run native exploration object tests without starting a Minecraft server/world."""
from __future__ import annotations

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
MINECRAFT = "1.21.1-20240808.144430"


def digest(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", required=True, type=Path)
    parser.add_argument("--java", required=True, type=Path)
    parser.add_argument("--version-json", type=Path,
                        help="Official 1.21.1 library manifest; defaults to runtime/versions/1.21.1/1.21.1.json")
    parser.add_argument("--chest-jar", type=Path,
                        help="Defaults to the authored chest module pinned in metadata/authored-mods.json")
    args = parser.parse_args()
    java = args.java.resolve()
    javac = java.with_name("javac")
    result = subprocess.run([str(java), "-version"], capture_output=True, text=True, check=True)
    if not re.search(r'version "21\.', result.stdout + result.stderr):
        parser.error("Java 21 is required")
    runtime = args.runtime.resolve()
    manifest_path = args.version_json or runtime / "versions/1.21.1/1.21.1.json"
    manifest = json.loads(manifest_path.read_text())
    if manifest.get("id") != "1.21.1":
        parser.error("Only the pinned Minecraft 1.21.1 manifest is supported")
    base = runtime / "libraries"
    # Use the official game library list. Installer/build-tool libraries also
    # live under libraries and may contain incompatible older Guava/Gson.
    libraries = [base / f"net/minecraft/server/{MINECRAFT}/server-{MINECRAFT}-{suffix}.jar"
                 for suffix in ("srg", "extra")]
    for library in manifest["libraries"]:
        artifact = library.get("downloads", {}).get("artifact")
        if artifact:
            candidate = base / artifact["path"]
            if candidate.is_file():
                libraries.append(candidate)
    authored = json.loads((ROOT / "metadata/authored-mods.json").read_text())
    row = next(row for row in authored if row["filename"].startswith("qm-chest-balance-"))
    chest = (args.chest_jar or ROOT / "overrides/mods" / row["filename"]).resolve()
    if not args.chest_jar and (not chest.is_file() or digest(chest) != row["sha256"]):
        parser.error("Packaged chest module differs from its reviewed hash")
    for library in [chest, *libraries]:
        if not library.is_file():
            parser.error("Missing native probe dependency: " + str(library))
    classpath = os.pathsep.join(map(str, [chest, *libraries]))
    sources = [ROOT / "tests/runtime/SparseChestLootProbe.java", ROOT / "tests/runtime/ExplorationStructuresProbe.java"]
    with tempfile.TemporaryDirectory(prefix="soqm-exploration-probe-") as directory:
        compiled = Path(directory)
        subprocess.run([str(javac), "--release", "21", "-proc:none", "-cp", classpath,
                        "-d", str(compiled), *map(str, sources)], check=True, timeout=60)
        for name in ("SparseChestLootProbe", "ExplorationStructuresProbe"):
            subprocess.run([str(java), "-Xmx512M", "-cp", str(compiled) + os.pathsep + classpath,
                            "soqm.validation." + name, str(ROOT)], cwd=compiled, check=True, timeout=60)
    print("Native object tests passed. These do not launch a modded server/client or generate terrain.")


if __name__ == "__main__":
    main()
