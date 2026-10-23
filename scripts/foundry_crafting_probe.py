#!/usr/bin/env python3
"""Apply real Mixins and execute pinned Foundry crafting bytecode without a world.

Requires an already installed NeoForge 21.1.262 library tree and Java 21 JDK.
Does not accept an EULA, start Minecraft, connect to a server or read player data.
The original/patched takeResult instructions and native recipe positioning run
against inventory/player/item/event fixtures, as documented in the Java probe.
"""
from __future__ import annotations

import argparse
from datetime import datetime, timezone
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import tempfile
import tomllib
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parents[1]
MODULE = ROOT / "src/compat/foundry-compat"
PROBE = ROOT / "tests/runtime/foundry/FoundryCraftingProbe.java"
MAIN = "qm.foundryprobe.FoundryCraftingProbe"
EXTRAS_SHA256 = "f0c3e3c1ad835b80e70cb3f78359b71f107f74689523b4fb391f160ee487cfbf"


def sha(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def run(command, *, cwd, timeout):
    result = subprocess.run(command, cwd=cwd, capture_output=True, text=True, timeout=timeout)
    output = result.stdout + result.stderr
    if result.returncode:
        raise ValueError(f"Native probe command failed with exit {result.returncode}:\n{output}")
    return output


def native_class(dependencies, entry):
    """Resolve the same first classpath entry used by the JVM, and record its hash."""
    for path in dependencies:
        with ZipFile(path) as archive:
            if entry in archive.namelist():
                content = archive.read(entry)
                return {"archive": path.name, "sha256": hashlib.sha256(content).hexdigest()}
    raise ValueError("Missing native API class: " + entry)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, required=True, help="Installed NeoForge server libraries/ parent")
    parser.add_argument("--mods", type=Path, default=ROOT / ".cache/mods")
    parser.add_argument("--javac", type=Path, required=True)
    parser.add_argument("--adapter", type=Path, help="Built compatibility module to verify")
    parser.add_argument("--output", type=Path, default=ROOT / "reports/foundry-crafting-probe.json")
    args = parser.parse_args()

    # Reuse the production builder's native API, mod and exact target class pins.
    spec = importlib.util.spec_from_file_location("soqm_foundry_build", MODULE / "build.py")
    builder = importlib.util.module_from_spec(spec)
    # The module directory is source-hash-pinned; importing the builder must not
    # leave a generated __pycache__ file that changes its provenance inventory.
    previous_bytecode_setting = sys.dont_write_bytecode
    try:
        sys.dont_write_bytecode = True
        spec.loader.exec_module(builder)
    finally:
        sys.dont_write_bytecode = previous_bytecode_setting
    metadata = tomllib.loads((MODULE / "resources/META-INF/neoforge.mods.toml").read_text())
    version = metadata["mods"][0]["version"]
    adapter = (args.adapter or ROOT / "overrides/mods" / f"qm-foundry-compat-{version}.jar").resolve()
    if not adapter.is_file():
        raise ValueError("Build the compatibility module before running this probe")
    javac = args.javac.resolve()
    compiler = run([str(javac), "-version"], cwd=ROOT, timeout=15).strip()
    if not re.fullmatch(r"javac 21(?:\.[^\s]+)?", compiler):
        raise ValueError("The probe requires a Java 21 JDK")

    with tempfile.TemporaryDirectory(prefix="soqm-foundry-probe-") as directory:
        temporary = Path(directory)
        dependencies = builder.compile_dependencies(args.runtime.resolve(), args.mods.resolve(), temporary)
        extras = temporary / "mixinextras-neoforge-0.5.5.jar"
        if sha(extras) != EXTRAS_SHA256:
            raise ValueError("MixinExtras differs from the pinned NeoForge distribution")
        classes = temporary / "classes"
        classes.mkdir()
        classpath = os.pathsep.join(map(str, [classes, adapter, *dependencies]))
        compilation = run([str(javac), "--release", "21", "-encoding", "UTF-8", "-proc:none",
                           "-classpath", classpath, "-d", str(classes), str(PROBE)],
                          cwd=temporary, timeout=60)
        services = classes / "META-INF/services"
        services.mkdir(parents=True)
        (services / "org.spongepowered.asm.service.IMixinService").write_text(MAIN + "$Service\n")
        (services / "org.spongepowered.asm.service.IGlobalPropertyService").write_text(MAIN + "$Blackboard\n")
        transformed = temporary / "transformed"
        output = run([str(javac.with_name("java")), "-Dmixin.service=" + MAIN + "$Service",
                      "-classpath", classpath, MAIN, str(transformed)], cwd=temporary, timeout=120)
        transformed_class = transformed / builder.TARGET_CLASS
        transform = re.search(r"FOUNDRY_MIXIN_TRANSFORM_OK native_write_sites=(\d+) transformed_write_sites=(\d+)", output)
        behavior = re.search(r"FOUNDRY_NATIVE_BEHAVIOR_OK cases=(\d+) assertions=(\d+)", output)
        if not transform or not behavior or "FOUNDRY_OLD_DUPLICATION_REPRODUCED" not in output:
            raise ValueError("Probe did not provide complete transform, negative-control and behavior evidence")
        native_entries = [builder.TARGET_CLASS,
                          "modernmods/modernfoundry/tables/block/entity/inventory/CraftingContainerWrapper.class",
                          "net/minecraft/world/item/crafting/CraftingInput.class",
                          "net/minecraft/world/item/crafting/CraftingInput$Positioned.class"]
        report = {
            "passed": True,
            "checked_at_utc": datetime.now(timezone.utc).isoformat(),
            "kind": "exact-native-bytecode-with-boundary-fixtures",
            "compiler": compiler,
            "neoforge": builder.NEOFORGE,
            "native_server_sha256": builder.NATIVE_SHA256,
            "adapter": {"filename": adapter.name, "sha256": sha(adapter), "bytes": adapter.stat().st_size},
            "mixinextras_sha256": sha(extras),
            "native_classes": {entry: native_class(dependencies, entry) for entry in native_entries},
            "transformed_target_sha256": sha(transformed_class),
            "native_write_sites": int(transform[1]),
            "transformed_write_sites": int(transform[2]),
            "behavior_cases": int(behavior[1]),
            "assertions": int(behavior[2]),
            "negative_controls": [
                "Unmodified 4.1.6 creates the exact screenshot grid [15,15,15,15,16,16,0,16,16] from 64 sand.",
                "Unmodified bottom-right single-item recipe produces repeated outputs without consuming its four ingredients.",
            ],
            "coverage": [
                "Actual Sponge/MixinExtras transformation of all three write sites with Positioned local capture.",
                "Actual original and transformed takeResult instructions; native CraftingInput positioning and wrapper methods.",
                "Every rectangular shape from 1x1 through 3x3 and every valid grid offset.",
                "Stack counts 1, 2, 16 and 64; empty, same-item and bucket remainders.",
                "Remainder delivery to inventory and item drops when the inventory fixture is full.",
                "Sixteen successive sandstone crafts at all four corners consume exactly 64 sand.",
                "Craft hooks remain once per craft and crafting-player context is cleared.",
            ],
            "limits": [
                "Inventory/item/player/recipe/event boundaries are fixtures; no game world or UI was launched.",
                "This does not test network synchronization, actual Ctrl input or interactions with every other pack Mixin.",
            ],
            "sources_sha256": {
                "scripts/foundry_crafting_probe.py": sha(Path(__file__)),
                "tests/runtime/foundry/FoundryCraftingProbe.java": sha(PROBE),
                "src/compat/foundry-compat/build.py": sha(MODULE / "build.py"),
            },
            "output": output.strip().splitlines(),
            "compiler_output": compilation.strip().splitlines(),
        }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(output.strip())
    print("Evidence written to " + str(args.output))


if __name__ == "__main__":
    try:
        main()
    except (ValueError, OSError, subprocess.TimeoutExpired) as error:
        raise SystemExit(str(error))
