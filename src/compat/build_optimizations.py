#!/usr/bin/env python3
"""Rebuild SOQM runtime, quest, chest and village policies against pinned native APIs."""
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

ROOT = Path(__file__).resolve().parents[2]
NATIVE_SHA256 = "f57ce3018e21459d24b50508e8c1b4fd5c16324ec1dc5f316c192aa0130e7fe0"
NEOFORGE = "21.1.262"
MODULES = {
    "runtime-optimizations": ("qm-runtime-optimizations", "qm.runtime.optimizations"),
    "quest-automation": ("qm-quest-automation", "qm.quest.automation"),
    "chest-balance": ("qm-chest-balance", "qm.chest.balance"),
    "village-guard": ("qm-village-guard", "qm.village.guard"),
}
VILLAGE_GUARD_TARGETS = {
    "net/minecraft/world/entity/ai/Brain.class":
        "86c37e8dcc04a31571c9e83c8d8002aebf3136b00bd0b99f45dc97ee1d6ee5ba",
    "net/minecraft/world/level/storage/DimensionDataStorage.class":
        "f3b28651f2e95ec9e0e1ee9242b738c05b249be0ba2e18306d982dc1ca8586b9",
    "org/millenaire/entity/VillagerCombat.class":
        "95680c0e8574810c717c5c3452dc7006c6887fd90625844c5a6499a373a36ede",
    "org/millenaire/village/VillageManager.class":
        "5f169b8159e333ffe6c2e027976654c0c147e2338ed7f52c98512bdcd60e1687",
    "org/millenaire/village/Village.class":
        "102a92372b2b5a9632609e1a7d1c82e8f1604b7855b0d8ce05f12945e5c1566a",
    "org/millenaire/building/BuildingInstance.class":
        "186748151f96be103082d5be9a223f4fcbd81b8c04eea32f37956dfbc202f8bf",
    "org/millenaire/culture/ModCultures.class":
        "ab914e4969565320f91a8e97314ad287b199f587fe9b15dc42251456409615a2",
}


def digest(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def compile_dependencies(runtime, mods, temporary):
    libraries = runtime / "libraries"
    native = libraries / "net/neoforged/neoforge" / NEOFORGE / f"neoforge-{NEOFORGE}-server.jar"
    if not native.is_file() or digest(native) != NATIVE_SHA256:
        raise ValueError("Mapped NeoForge server differs from the 21.1.262 pin")
    jars = [native, *[p for p in sorted(libraries.rglob("*.jar")) if p != native]]
    for row in json.loads((ROOT / "metadata/mods.lock.json").read_text())["mods"]:
        jar = mods / row["filename"]
        if not jar.is_file() or jar.stat().st_size != row["bytes"] or digest(jar) != row["sha256"]:
            raise ValueError("Missing or mismatched dependency: " + row["filename"])
        jars.append(jar)
    universal = libraries / "net/neoforged/neoforge" / NEOFORGE / f"neoforge-{NEOFORGE}-universal.jar"
    with ZipFile(universal) as archive:
        name = "META-INF/jarjar/mixinextras-neoforge-0.5.5.jar"
        extras = temporary / "mixinextras-neoforge-0.5.5.jar"
        extras.write_bytes(archive.read(name))
        jars.append(extras)
    # Fail before compilation if the two upstream worldgen callsites changed.
    with ZipFile(mods / "abandonedtunnels-1.0.0.jar") as archive:
        target = archive.read("com/manyfunadventures/abandonedtunnels/TunnelStructure.class")
        if hashlib.sha256(target).hexdigest() != "954bc585c4705fbbf4d40df08b81c4063fa5a6dfb9c5be24ca7b0b7191711748":
            raise ValueError("Unsupported Abandoned Tunnels implementation")
    return jars


def verify_module_targets(module_name, runtime, mods):
    """Validate this module's native hook sites without adding gates to others."""
    if module_name != "village-guard":
        return {}
    # These vanilla targets are unchanged by NeoForge, so their mapped bytecode
    # lives in the base server SRG archive rather than the small patch archive.
    brain_archive = (runtime / "libraries/net/minecraft/server/1.21.1-20240808.144430"
                     / "server-1.21.1-20240808.144430-srg.jar")
    millenaire_archive = mods / "millenaire-9.0.2.jar"
    checked = {}
    for jar, namespace in ((brain_archive, "net/minecraft/"),
                           (millenaire_archive, "org/millenaire/")):
        if not jar.is_file():
            raise ValueError("Missing Village Guard target archive: " + jar.name)
        with ZipFile(jar) as archive:
            for name, expected in VILLAGE_GUARD_TARGETS.items():
                if not name.startswith(namespace):
                    continue
                try:
                    actual = hashlib.sha256(archive.read(name)).hexdigest()
                except KeyError as error:
                    raise ValueError("Missing Village Guard hook target: " + name) from error
                if actual != expected:
                    raise ValueError("Unsupported Village Guard hook target: " + name)
                checked[name] = actual
    return checked


def build(module_name, javac, dependencies, temporary, output_dir):
    module = ROOT / "src/compat" / module_name
    filename, automatic_name = MODULES[module_name]
    metadata = tomllib.loads((module / "resources/META-INF/neoforge.mods.toml").read_text())
    version = metadata["mods"][0]["version"]
    classes = temporary / module_name
    classes.mkdir()
    sources = sorted((module / "src").rglob("*.java"))
    subprocess.run([javac, "--release", "21", "-encoding", "UTF-8", "-proc:none", "-classpath",
                    os.pathsep.join(map(str, dependencies)), "-d", str(classes), *map(str, sources)], check=True)
    entries = {
        "META-INF/MANIFEST.MF": ("Manifest-Version: 1.0\r\nFMLModType: MOD\r\n"
                                 f"Automatic-Module-Name: {automatic_name}\r\n\r\n").encode("ascii"),
        "LICENSE": (module / "LICENSE").read_bytes(),
    }
    for directory in (classes, module / "resources"):
        for path in sorted(directory.rglob("*")):
            if path.is_file():
                entries[path.relative_to(directory).as_posix()] = path.read_bytes()
    output_dir.mkdir(parents=True, exist_ok=True)
    output = output_dir / f"{filename}-{version}.jar"
    with ZipFile(output, "w", compression=ZIP_DEFLATED, compresslevel=9) as archive:
        for name, content in sorted(entries.items()):
            info = ZipInfo(name, (1980, 1, 1, 0, 0, 0))
            info.create_system = 3
            info.external_attr = 0o100644 << 16
            info.compress_type = ZIP_DEFLATED
            archive.writestr(info, content, compresslevel=9)
    return {"module": module_name, "filename": output.name, "bytes": output.stat().st_size,
            "sha256": digest(output), "version": version}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", required=True, type=Path)
    parser.add_argument("--javac", required=True)
    parser.add_argument("--mods", type=Path, default=ROOT / ".cache/mods")
    parser.add_argument("--module", choices=["all", *MODULES], default="all")
    parser.add_argument("--output-dir", type=Path, default=ROOT / "overrides/mods")
    args = parser.parse_args()
    version = subprocess.run([args.javac, "-version"], check=True, capture_output=True, text=True)
    compiler = (version.stdout + version.stderr).strip()
    if not re.fullmatch(r"javac 21(?:\.[^\s]+)?", compiler):
        parser.error("A Java 21 JDK is required")
    with tempfile.TemporaryDirectory(prefix="soqm-optimizations-") as folder:
        temporary = Path(folder)
        dependencies = compile_dependencies(args.runtime.resolve(), args.mods.resolve(), temporary)
        results = []
        for name in (MODULES if args.module == "all" else [args.module]):
            targets = verify_module_targets(name, args.runtime.resolve(), args.mods.resolve())
            result = build(name, args.javac, dependencies, temporary, args.output_dir.resolve())
            if targets:
                result["target_sha256"] = targets
            results.append(result)
    print(json.dumps({"compiler": compiler, "native_sha256": NATIVE_SHA256, "builds": results}, indent=2))


if __name__ == "__main__":
    main()
