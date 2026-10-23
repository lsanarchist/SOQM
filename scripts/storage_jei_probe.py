#!/usr/bin/env python3
"""Execute native Core/JEI display focus links before/after the shipped Mixin.

Requires the pinned installed NeoForge libraries and Java 21. Does not start a
game, server, world or renderer, or accept an EULA. The real Core extension,
JEI grid/supplier/focus logic and vanilla ItemStack/components execute; Storage
registration and its full factory lifecycle remain outside this bounded probe.
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
MODULE = ROOT / "src/compat/storage-jei-compat"
SOURCE = ROOT / "tests/runtime/storage_jei/StorageJeiProbe.java"
MAIN = "qm.storagejeiprobe.StorageJeiProbe"
NEOFORM = "1.21.1-20240808.144430"
NATIVE_PINS = {
    "srg": "26ca9c40d7e1681190b428583c38816852218e78df3f8bdb60a59a78503aec71",
    "extra": "24a5d2d162cfad2a1a574c4d552e99dc6c6303a49d1e68b43a7b638f3b0930fd",
}


def sha(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def run(command, cwd, timeout=60):
    result = subprocess.run(command, cwd=cwd, capture_output=True, text=True, timeout=timeout)
    output = result.stdout + result.stderr
    if result.returncode:
        raise ValueError(f"Native storage JEI probe failed ({result.returncode}):\n{output}")
    return output


def native_class(dependencies, entry):
    for path in dependencies:
        with ZipFile(path) as archive:
            if entry in archive.namelist():
                return {"archive": path.name, "sha256": hashlib.sha256(archive.read(entry)).hexdigest()}
    raise ValueError("Missing native class: " + entry)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, required=True, help="Installed NeoForge 21.1.262 libraries/ parent")
    parser.add_argument("--mods", type=Path, default=ROOT / ".cache/mods")
    parser.add_argument("--javac", type=Path, required=True, help="Java 21 JDK compiler")
    parser.add_argument("--adapter", type=Path, help="Built compatibility module to execute")
    parser.add_argument("--output", type=Path, default=ROOT / "reports/storage-jei-probe.json")
    args = parser.parse_args()
    runtime, javac, mods = args.runtime.resolve(), args.javac.resolve(), args.mods.resolve()
    compiler = run([str(javac), "-version"], ROOT).strip()
    if not re.fullmatch(r"javac 21(?:\.[^\s]+)?", compiler):
        raise ValueError("The native storage JEI probe requires a Java 21 JDK")
    pack = json.loads((ROOT / "soqm.json").read_text())
    if (pack["minecraft"], pack["neoforge"]) != ("1.21.1", "21.1.262"):
        raise ValueError("Review the probe before changing Minecraft/NeoForge")
    metadata = tomllib.loads((MODULE / "resources/META-INF/neoforge.mods.toml").read_text())
    version = metadata["mods"][0]["version"]
    adapter = (args.adapter or ROOT / "overrides/mods" / f"qm-storage-jei-compat-{version}.jar").resolve()
    if not adapter.is_file():
        raise ValueError("Build the storage JEI compatibility module before running this probe")

    # Reuse production validation of exact Core, JEI, target bytecode, Mixin and
    # MixinExtras. Import without changing the module's source provenance tree.
    spec = importlib.util.spec_from_file_location("soqm_storage_jei_build", MODULE / "build.py")
    builder = importlib.util.module_from_spec(spec)
    previous = sys.dont_write_bytecode
    try:
        sys.dont_write_bytecode = True
        spec.loader.exec_module(builder)
    finally:
        sys.dont_write_bytecode = previous
    native = [runtime / f"libraries/net/minecraft/server/{NEOFORM}/server-{NEOFORM}-{kind}.jar"
              for kind in NATIVE_PINS]
    for kind, path in zip(NATIVE_PINS, native):
        builder.require_hash(path, NATIVE_PINS[kind], "mapped vanilla Minecraft " + kind)
    arguments = runtime / "libraries/net/neoforged/neoforge/21.1.262/unix_args.txt"
    legacy = [line.removeprefix("-DlegacyClassPath=") for line in arguments.read_text().splitlines()
              if line.startswith("-DlegacyClassPath=")]
    if len(legacy) != 1:
        raise ValueError("Expected exactly one installed NeoForge legacy classpath")
    dependencies = list(native)
    for name in legacy[0].split(":"):
        relative = Path(name)
        if relative.is_absolute() or ".." in relative.parts or relative.suffix != ".jar":
            raise ValueError("Unexpected installed classpath entry: " + name)
        path = runtime / relative
        if not path.is_file():
            raise ValueError("Missing installed classpath dependency: " + str(path))
        if path not in dependencies:
            dependencies.append(path)

    with tempfile.TemporaryDirectory(prefix="soqm-storage-jei-probe-") as directory:
        temporary = Path(directory)
        builder.compile_dependencies(runtime, mods, temporary)
        # The unpatched native registry bootstraps without a mod loader. Exact
        # legacy dependencies exclude obsolete installer-only Guava/Gson JARs.
        dependencies.extend(mods / filename for filename in builder.MOD_PINS)
        extras = temporary / "mixinextras-neoforge-0.5.5.jar"
        dependencies.append(extras)
        classes = temporary / "classes"
        classes.mkdir()
        classpath = os.pathsep.join(map(str, [classes, adapter, *dependencies]))
        compilation = run([str(javac), "--release", "21", "-encoding", "UTF-8", "-proc:none",
                           "-cp", classpath, "-d", str(classes), str(SOURCE)], temporary)
        services = classes / "META-INF/services"
        services.mkdir(parents=True)
        (services / "org.spongepowered.asm.service.IMixinService").write_text(MAIN + "$Service\n")
        (services / "org.spongepowered.asm.service.IGlobalPropertyService").write_text(MAIN + "$Blackboard\n")
        command = [str(javac.with_name("java")), "-Dmixin.service=" + MAIN + "$Service", "-cp", classpath, MAIN]
        server_output = run([*command, "--server-side-check"], temporary)
        output = run([*command, str(temporary / "transformed")], temporary)
        baseline = re.search(r"STORAGE_JEI_NATIVE_BASELINE exceptions=(\d+) wrong_source_links=(\d+) correct_links=(\d+)", output)
        fixed = re.search(r"STORAGE_JEI_NATIVE_OK cases=(\d+) linked=(\d+) unlinked=(\d+) shaped_positions=(\d+) shapeless_positions=(\d+) assertions=(\d+)", output)
        if ("STORAGE_JEI_MIXIN_TRANSFORM_OK native_list_gets=2 modified_arguments=1" not in output
                or "STORAGE_JEI_SERVER_SIDE_UNCHANGED" not in server_output or not baseline or not fixed):
            raise ValueError("Missing native transformation, side, baseline or behavior evidence")
        before = dict(zip(("exceptions", "wrong_source_links", "correct_links"), map(int, baseline.groups())))
        after = dict(zip(("cases", "linked", "unlinked", "shaped_positions", "shapeless_positions", "assertions"), map(int, fixed.groups())))
        if (any(value == 0 for value in before.values()) or sum(before.values()) != 36
                or (after["cases"], after["linked"], after["unlinked"], after["shaped_positions"], after["shapeless_positions"])
                != (830, 415, 415, 36, 45)):
            raise ValueError("Incomplete source-slot or focus-guard coverage")
        native_entries = [builder.TARGET_CLASS,
                          "net/p3pp3rf1y/sophisticatedcore/compat/recipeviewers/common/CraftingDisplaySpec.class",
                          "mezz/jei/library/gui/helpers/CraftingGridHelper.class",
                          "mezz/jei/library/gui/recipes/supplier/builder/IngredientSupplierBuilder.class",
                          "mezz/jei/library/ingredients/RecipeIngredientSupplier$FocusLink.class",
                          "net/minecraft/world/item/ItemStack.class",
                          "net/minecraft/world/item/crafting/ShapedRecipe.class"]
        report = {
            "passed": True, "checked_at_utc": datetime.now(timezone.utc).isoformat(),
            "kind": "native-core-jei-focus-links-with-vanilla-display-spec-fixtures",
            "compiler": compiler, "minecraft": "1.21.1", "neoforge_installation": "21.1.262",
            "adapter": {"filename": adapter.name, "sha256": sha(adapter), "bytes": adapter.stat().st_size},
            "baseline": before, "current": after, "server_side_unmodified": True,
            "native_pins": NATIVE_PINS, "mod_pins": builder.MOD_PINS,
            "mixinextras_sha256": sha(extras), "installed_arguments_sha256": sha(arguments),
            "native_classes": {entry: native_class(dependencies, entry) for entry in native_entries},
            "transformed_target_sha256": sha(temporary / "transformed" / builder.TARGET_CLASS),
            "classpath_sha256": {str(path.relative_to(runtime)) if path.is_relative_to(runtime) else path.name: sha(path)
                                 for path in dependencies},
            "sources_sha256": {path.relative_to(ROOT).as_posix(): sha(path) for path in
                               [SOURCE, Path(__file__), ROOT / "metadata/mods.lock.json", *sorted(path for path in MODULE.rglob("*") if path.is_file())]},
            "coverage": [
                "Real Sponge/MixinExtras transform of the shipped client Mixin; one physical-slot argument changes.",
                "Actual pinned Core setRecipe and JEI grid, ingredient supplier, focus-link validation and visibility methods.",
                "All 36 compact source positions across native shaped widths/heights 1..3, including US/USU and full 3x3.",
                "All 45 shapeless source positions for 1..9 ingredients, plus either-dimension normalization.",
                "Grouped and ungrouped displays, input/output component-sensitive focus, sparse inputs, global and multiple-output guards.",
                "Every linked input/output retains actual native ItemStack count and components; both focus directions select the pair.",
                "Original US/USU raises the exact logged exception; other layouts expose silent links to a material instead of source.",
            ],
            "limits": [
                "Storage display specs use vanilla item witnesses and an ItemStack identity-helper fixture, not registered StorageBlockItems.",
                "Storage's complete recipe factory, mod registries and real storage/color/content components are not initialized.",
                "No rendered client, complete pack plugin registration, recipe transfer, network synchronization or crafting execution is tested.",
                "Native Minecraft registry bootstrap runs without NeoForge transformations; only the shipped Core Mixin is applied.",
            ],
            "compiler_output": compilation.splitlines(), "output": (server_output + output).splitlines(),
        }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(report, indent=2) + "\n")
    print(f"Native storage JEI: {after['cases']} cases, {after['linked']} correct focus links, {after['unlinked']} preserved guards.")
    print(f"Negative control: {before['exceptions']} native exceptions and {before['wrong_source_links']} silent wrong-source links.")
    print("Scope: actual Core/JEI display methods with native vanilla fixtures; no rendered client or full Storage registry.")
    print("Evidence written to " + str(args.output))


if __name__ == "__main__":
    try:
        main()
    except (ValueError, OSError, subprocess.TimeoutExpired) as error:
        raise SystemExit(str(error))
