#!/usr/bin/env python3
"""Exercise native dungeon expeditions, loot, locks and encounter persistence.

Uses the existing verified full-pack runtime harness and a fresh localhost world.
The fixture JAR is test-only and is never copied into the production payload.
"""
from __future__ import annotations

import argparse
import json
from pathlib import Path
import subprocess
import sys
import time
from zipfile import ZIP_DEFLATED, ZipFile


REPOSITORY = Path(__file__).resolve().parents[3]
sys.path.insert(0, str(REPOSITORY / "scripts"))
import runtime_probe
from smoke_server import ROOT, NATIVE_SHA256, SmokeFailure, remaining, require, sha


SOURCE = Path(__file__).with_name("EncounterValidationProbe.java")
REPORT = ROOT / "reports/dungeon-encounter-probe.json"


def compile_probe(java, runtime, workspace, deadline):
    native = runtime / "libraries/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-server.jar"
    require(native.is_file() and sha(native) == NATIVE_SHA256, "Native server differs from its verified pin")
    javac = Path(java).with_name("javac")
    require(javac.is_file(), "A Java 21 JDK with javac is required")
    classes = workspace / "probe-classes"
    classes.mkdir()
    jars = [native, *sorted((runtime / "libraries").rglob("*.jar")),
            *sorted((ROOT / ".cache/mods").glob("*.jar")),
            *sorted((ROOT / "overrides/mods").glob("*.jar"))]
    # javac does not resolve jar-in-jar APIs. Only declared nested dependencies
    # from already verified parent artifacts are exposed to this compiler.
    embedded = workspace / "embedded-api-jars"
    embedded.mkdir()
    for parent in list(jars):
        with ZipFile(parent) as archive:
            if "META-INF/jarjar/metadata.json" not in archive.namelist():
                continue
            for entry in json.loads(archive.read("META-INF/jarjar/metadata.json")).get("jars", []):
                name = entry["path"]
                require(isinstance(name, str) and name.startswith(("META-INF/jarjar/", "META-INF/jars/"))
                        and name.endswith(".jar") and ".." not in Path(name).parts
                        and "\\" not in name and name in archive.namelist(),
                        "Unexpected embedded dependency path")
                target = embedded / (sha(parent)[:12] + "-" + Path(name).name)
                target.write_bytes(archive.read(name))
                jars.append(target)
    compiled = subprocess.run(
        [str(javac), "--release", "21", "-encoding", "UTF-8", "-proc:none",
         "-classpath", ":".join(map(str, jars)), "-d", str(classes),
         *map(str, sorted(SOURCE.parent.glob("*.java")))],
        capture_output=True, text=True, timeout=min(90, remaining(deadline)))
    (workspace / "compile.log").write_text(compiled.stdout + compiled.stderr, encoding="utf-8")
    require(compiled.returncode == 0, "Dungeon encounter fixture did not compile; inspect compile.log")
    target = workspace / "soqm-dungeon-encounter-probe-test-only.jar"
    metadata = '''modLoader="javafml"
loaderVersion="[4,)"
license="MIT"
[[mods]]
modId="soqm_dungeon_encounter_probe"
version="0.0.0"
displayName="SOQM isolated dungeon encounter regressions"
description="Test-only native expeditions, personal loot, locks, abilities and persistence; never ship."
[[dependencies.soqm_dungeon_encounter_probe]]
modId="qm_dungeon_encounters"
type="required"
versionRange="[0.4.22,)"
ordering="AFTER"
side="SERVER"
'''
    with ZipFile(target, "w", ZIP_DEFLATED) as output:
        output.writestr("META-INF/neoforge.mods.toml", metadata)
        output.write(ROOT / "LICENSE", "LICENSE")
        for path in sorted(classes.rglob("*.class")):
            output.write(path, path.relative_to(classes).as_posix())
    return target


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", required=True)
    parser.add_argument("--java", default="java")
    parser.add_argument("--accept-eula", action="store_true")
    parser.add_argument("--baseline-ref", default="HEAD", help="Harness provenance only; no quest fixture runs")
    parser.add_argument("--port", type=int, default=25594)
    parser.add_argument("--timeout", type=int, default=900)
    parser.add_argument("--allow-unlocked-payload", action="store_true")
    args = parser.parse_args()
    started = time.monotonic()
    report = {
        "passed": False,
        "scope": "Native full-pack WDA structure-bound encounters and accessory variety, authored expeditions, rotated geometry, earned keys, portal entry/return, locked personal Lootr chests, Apotheosis rewards, real Elite abilities and NBT/data reload; no graphical-client, survival-playthrough or TPS benchmark.",
        "encounter_harness_sha256": sha(Path(__file__)),
        "encounter_fixture_sha256": sha(SOURCE),
    }
    try:
        require(120 <= args.timeout <= 1800 and 1024 <= args.port <= 65535,
                "Invalid timeout or localhost port")
        runtime_probe.run(args, report, started + args.timeout, compile_fn=compile_probe,
                          probe_source=SOURCE, workspace_kind="dungeon-encounter-probe")
        console = (ROOT / report["workspace"] / "console.log").read_text(encoding="utf-8", errors="replace")
        dungeon_errors = {text: console.count(text) for text in (
            "UNHANDLED DATA BLOCK WITH name =", "DIMDUNGEONS ERROR UNABLE TO PLACE",
            "DIMDUNGEONS FATAL ERROR", "DIMDUNGEONS WARNING: why is there no exit portal here?",
        )}
        report["dungeon_generation_error_counts"] = dungeon_errors
        require(not any(dungeon_errors.values()), "Native DD reported an invalid marker, room placement or exit doorway")
        report["passed"] = True
    except (SmokeFailure, OSError, subprocess.SubprocessError) as error:
        report["failure"] = str(error)
    report["elapsed_seconds"] = round(time.monotonic() - started, 2)
    REPORT.parent.mkdir(exist_ok=True)
    REPORT.write_text(json.dumps(report, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(json.dumps({"passed": report["passed"], "failure": report.get("failure"),
                      "report": REPORT.relative_to(ROOT).as_posix(), "workspace": report.get("workspace")}), flush=True)
    return 0 if report["passed"] else 1


if __name__ == "__main__":
    sys.exit(main())
