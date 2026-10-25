#!/usr/bin/env python3
"""Validate the optional DD quest branch through the pinned native FTB APIs.

Uses a fresh full-pack localhost server and the existing reviewed-payload harness.
The baseline defaults to the last commit before the additive quest branch.
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
sys.dont_write_bytecode = True
sys.path.insert(0, str(REPOSITORY / "scripts"))
import runtime_probe
from smoke_server import ROOT, NATIVE_SHA256, SmokeFailure, remaining, require, sha


SOURCE = Path(__file__).with_name("DungeonQuestProbe.java")
REPORT = ROOT / "reports/dungeon-quest-probe.json"
BASELINE = "46b9bdab3a87271360e4588e4f370300510be63e"


def compile_probe(java, runtime, workspace, deadline):
    native = runtime / "libraries/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-server.jar"
    require(native.is_file() and sha(native) == NATIVE_SHA256, "Native server differs from its verified pin")
    javac = Path(java).with_name("javac")
    require(javac.is_file(), "The test-only fixture needs a Java 21 JDK")
    classes = workspace / "probe-classes"
    classes.mkdir()
    jars = [native, *sorted((runtime / "libraries").rglob("*.jar")),
            *sorted((ROOT / ".cache/mods").glob("*.jar")),
            *sorted((ROOT / "overrides/mods").glob("*.jar"))]
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
                        and "\\" not in name and name in archive.namelist(), "Unexpected embedded dependency path")
                target = embedded / (sha(parent)[:12] + "-" + Path(name).name)
                target.write_bytes(archive.read(name))
                jars.append(target)
    compiled = subprocess.run(
        [str(javac), "--release", "21", "-encoding", "UTF-8", "-proc:none",
         "-classpath", ":".join(map(str, jars)), "-d", str(classes),
         *map(str, sorted(SOURCE.parent.glob("*.java")))],
        capture_output=True, text=True, timeout=min(90, remaining(deadline)))
    (workspace / "compile.log").write_text(compiled.stdout + compiled.stderr, encoding="utf-8")
    require(compiled.returncode == 0, "Dungeon quest fixture did not compile; inspect compile.log")
    target = workspace / "soqm-dungeon-quest-probe-test-only.jar"
    metadata = '''modLoader="javafml"
loaderVersion="[4,)"
license="MIT"
[[mods]]
modId="soqm_dungeon_quest_probe"
version="0.0.0"
displayName="SOQM isolated dungeon quest regressions"
description="Test-only native FTB quest, DD advancement and saved-progress fixtures; never ship."
[[dependencies.soqm_dungeon_quest_probe]]
modId="ftbquests"
type="required"
versionRange="[2101.1.36]"
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
    parser.add_argument("--baseline-ref", default=BASELINE)
    parser.add_argument("--port", type=int, default=25595)
    parser.add_argument("--timeout", type=int, default=900)
    parser.add_argument("--allow-unlocked-payload", action="store_true")
    args = parser.parse_args()
    started = time.monotonic()
    report = {"passed": False,
              "scope": "Native optional DD quest goals, advancement triggers, personal reward handlers, pre-branch progress migration and disk/data reload; no graphical client or dungeon-clear claim.",
              "quest_harness_sha256": sha(Path(__file__)), "quest_fixture_sha256": sha(SOURCE)}
    try:
        require(120 <= args.timeout <= 1800 and 1024 <= args.port <= 65535, "Invalid timeout or localhost port")
        runtime_probe.run(args, report, started + args.timeout, compile_fn=compile_probe,
                          probe_source=SOURCE, workspace_kind="dungeon-quest-probe")
        log = ROOT / report["workspace"] / "server/logs/kubejs/server.log"
        require(log.is_file(), "Native reward log is absent")
        warnings = [line for line in log.read_text(encoding="utf-8", errors="replace").splitlines()
                    if "[SOQM rewards]" in line]
        report["reward_policy_log_gate"] = {"warning_count": len(warnings), "warnings": warnings,
                                           "kubejs_server_log_sha256": sha(log)}
        require(not warnings, "SOQM reward policy reported a warning or fallback; Expedition selection is not validated")
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
