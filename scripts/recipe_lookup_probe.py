#!/usr/bin/env python3
"""Run native Minecraft recipe lookup for nine repaired recipes without a world.

Uses the installed NeoForge 21.1.262 Minecraft 1.21.1 mapped vanilla JARs.
Mod items are explicit vanilla proxies; reviewed tag witnesses come from the
collision fixture. No server, mod loader, world or EULA interaction occurs.
"""
from __future__ import annotations

import argparse
from datetime import datetime, timezone
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import tempfile

from recipe_collisions import FIXTURE, load_fixture

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "tests/runtime/recipes/RecipeLookupProbe.java"
POLICY = ROOT / "metadata/hardcore_workshop.json"
MAIN = "soqm.validation.recipes.RecipeLookupProbe"
NEOFORM = "1.21.1-20240808.144430"
NATIVE_PINS = {
    "srg": "26ca9c40d7e1681190b428583c38816852218e78df3f8bdb60a59a78503aec71",
    "extra": "24a5d2d162cfad2a1a574c4d552e99dc6c6303a49d1e68b43a7b638f3b0930fd",
}


def sha(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def run(command, cwd):
    result = subprocess.run(command, cwd=cwd, capture_output=True, text=True, timeout=60)
    output = result.stdout + result.stderr
    if result.returncode:
        raise ValueError(f"Native recipe probe failed ({result.returncode}):\n{output}")
    return output


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, required=True, help="Installed NeoForge server libraries/ parent")
    parser.add_argument("--javac", type=Path, required=True, help="Java 21 JDK compiler")
    parser.add_argument("--output", type=Path, default=ROOT / "reports/recipe-lookup-probe.json")
    args = parser.parse_args()
    fixture = load_fixture()  # Fail closed if pinned mods or authored tag resources changed.
    runtime, javac = args.runtime.resolve(), args.javac.resolve()
    compiler = run([str(javac), "-version"], ROOT).strip()
    if not re.fullmatch(r"javac 21(?:\.[^\s]+)?", compiler):
        raise ValueError("The native recipe probe requires a Java 21 JDK")
    pack = json.loads((ROOT / "soqm.json").read_text())
    if (pack["minecraft"], pack["neoforge"]) != ("1.21.1", "21.1.262"):
        raise ValueError("Review the native recipe probe before changing Minecraft/NeoForge")
    native = [runtime / f"libraries/net/minecraft/server/{NEOFORM}/server-{NEOFORM}-{kind}.jar"
              for kind in NATIVE_PINS]
    for kind, path in zip(NATIVE_PINS, native):
        if sha(path) != NATIVE_PINS[kind]:
            raise ValueError("Native Minecraft JAR differs from its reviewed pin: " + str(path))
    arguments = runtime / "libraries/net/neoforged/neoforge/21.1.262/unix_args.txt"
    legacy = [line.removeprefix("-DlegacyClassPath=") for line in arguments.read_text().splitlines()
              if line.startswith("-DlegacyClassPath=")]
    if len(legacy) != 1:
        raise ValueError("Expected the installed NeoForge legacy classpath exactly once")
    dependencies = list(native)
    for name in legacy[0].split(":"):
        relative = Path(name)
        if relative.is_absolute() or ".." in relative.parts or relative.suffix != ".jar":
            raise ValueError("Unexpected installed classpath entry: " + name)
        path = runtime / relative
        if not path.is_file():
            raise ValueError("Missing installed classpath dependency: " + str(path))
        dependencies.append(path)
    # Installer-only obsolete Guava/Gson JARs must not enter this classpath.
    classpath = os.pathsep.join(map(str, dependencies))
    with tempfile.TemporaryDirectory(prefix="soqm-recipe-lookup-") as directory:
        temporary = Path(directory)
        baseline = temporary / "baseline.json"
        baseline.write_text(json.dumps({"recipes": fixture["baseline_recipes"]}))
        classes = temporary / "classes"
        classes.mkdir()
        compilation = run([str(javac), "--release", "21", "-encoding", "UTF-8", "-proc:none",
                           "-cp", classpath, "-d", str(classes), str(SOURCE)], temporary)
        output = run([str(javac.with_name("java")), "-cp", os.pathsep.join([str(classes), classpath]),
                      MAIN, str(POLICY), str(baseline)], temporary)
    counts = {}
    for label, marker in (("baseline", "RECIPE_LOOKUP_NATIVE_OLD_COLLISIONS"), ("current", "RECIPE_LOOKUP_NATIVE_OK")):
        match = re.search(marker + r" recipes=(\d+) witness_grids=(\d+) ambiguous_grids=(\d+) other_selected_outputs=(\d+)", output)
        if not match:
            raise ValueError("Missing native result marker: " + marker)
        counts[label] = dict(zip(("recipes", "witness_grids", "ambiguous_grids", "other_selected_outputs"), map(int, match.groups())))
    before, after = counts["baseline"], counts["current"]
    if not (before["recipes"] == after["recipes"] == 9 and before["witness_grids"] == after["witness_grids"] > 0
            and before["ambiguous_grids"] > 0 and before["other_selected_outputs"] > 0
            and after["ambiguous_grids"] == after["other_selected_outputs"] == 0):
        raise ValueError("Incomplete negative-control or repaired-recipe evidence")
    tag_match = re.search(r"RECIPE_LOOKUP_TAG_WITNESSES (\{[^\n]+\})", output)
    native_tags = json.loads(tag_match[1]) if tag_match else {}
    expected_tags = {tag: fixture["tags"][tag] for tag in ("c:plates/iron", "c:plates/gold", "c:storage_blocks/iron")}
    if {key: sorted(value) for key, value in native_tags.items()} != {key: sorted(value) for key, value in expected_tags.items()}:
        raise ValueError("Java tag witnesses differ from the reviewed collision fixture")
    report = {
        "passed": True, "checked_at_utc": datetime.now(timezone.utc).isoformat(),
        "kind": "native-vanilla-codecs-pattern-and-lookup-with-explicit-mod-item-proxies",
        "compiler": compiler, "minecraft": "1.21.1", "neoforge_installation": "21.1.262",
        **counts, "tag_witnesses": native_tags,
        "source_hashes": {path.relative_to(ROOT).as_posix(): sha(path) for path in
                          (SOURCE, Path(__file__), POLICY, FIXTURE, ROOT / "metadata/mods.lock.json")},
        "native_pins": NATIVE_PINS, "installed_arguments_sha256": sha(arguments),
        "classpath_sha256": {path.relative_to(runtime).as_posix(): sha(path) for path in dependencies},
        "compiler_output": compilation.splitlines(), "output": output.splitlines(),
        "limits": ["Only the nine repaired recipes are loaded, not the complete pack CRAFTING collection.",
                   "Mod items use distinct vanilla proxies; tag membership is a reviewed fixture, not a live mod registry.",
                   "Vanilla Minecraft codecs, ShapedRecipePattern and RecipeManager execute without NeoForge/mod transformations.",
                   "No server, world, EULA, UI or network synchronization is tested; the full-registry fixture is separate."],
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(report, indent=2) + "\n")
    print(f"Native recipe lookup: {after['recipes']} recipes, {after['witness_grids']} item combinations/mirrors, zero collisions.")
    print(f"Negative control: {before['ambiguous_grids']} ambiguous grids, {before['other_selected_outputs']} other selected outputs.")
    print("Scope: nine-recipe native vanilla pool with explicit mod-item/tag proxies; no game server.")
    print("Evidence written to " + str(args.output))


if __name__ == "__main__":
    try:
        main()
    except (ValueError, OSError, subprocess.TimeoutExpired) as error:
        raise SystemExit(str(error))
