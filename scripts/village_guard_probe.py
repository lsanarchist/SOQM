#!/usr/bin/env python3
"""Run Village Guard behavior and work-count regressions in an isolated server.

The existing runtime harness verifies and stages the pinned full pack, performs
a data reload, and owns shutdown. Only VillageGuardProbe is compiled and run;
the general campaign/production fixtures are not executed. No user save is read.
"""
from __future__ import annotations

import argparse
import json
from pathlib import Path
import subprocess
import sys
import time
from zipfile import ZIP_DEFLATED, ZipFile

import runtime_probe
from smoke_server import ROOT, SmokeFailure, remaining, require, sha


SOURCE = ROOT / "tests/runtime/villageguard/VillageGuardProbe.java"
REPORT = ROOT / "reports/village-guard-probe.json"


def compile_probe(java, runtime, workspace, deadline):
    native = runtime / "libraries/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-server.jar"
    javac = Path(java).with_name("javac")
    classes = workspace / "probe-classes"
    classes.mkdir()
    jars = [native, *sorted((runtime / "libraries").rglob("*.jar")),
            *sorted((ROOT / ".cache/mods").glob("*.jar")),
            *sorted((ROOT / "overrides/mods").glob("*.jar"))]
    compiled = subprocess.run(
        [str(javac), "--release", "21", "-encoding", "UTF-8", "-proc:none",
         "-classpath", ":".join(map(str, jars)), "-d", str(classes), str(SOURCE)],
        capture_output=True, text=True, timeout=min(90, remaining(deadline)))
    (workspace / "compile.log").write_text(compiled.stdout + compiled.stderr, encoding="utf-8")
    require(compiled.returncode == 0, "Village Guard fixture did not compile; inspect compile.log")
    target = workspace / "soqm-village-guard-probe-test-only.jar"
    metadata = '''modLoader="javafml"
loaderVersion="[4,)"
license="MIT"
[[mods]]
modId="soqm_village_guard_probe"
version="0.0.0"
displayName="SOQM isolated Village Guard regressions"
description="Test-only native geometry, targeting and work-count fixtures; never ship."
'''
    with ZipFile(target, "w", ZIP_DEFLATED) as output:
        output.writestr("META-INF/neoforge.mods.toml", metadata)
        output.write(ROOT / "src/compat/village-guard/LICENSE", "LICENSE")
        for path in sorted(classes.rglob("*.class")):
            output.write(path, path.relative_to(classes).as_posix())
    return target


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", required=True)
    parser.add_argument("--java", default="java")
    parser.add_argument("--accept-eula", action="store_true")
    parser.add_argument("--baseline-ref", default="HEAD", help="Staging provenance only; no quest migration fixture runs")
    parser.add_argument("--port", type=int, default=25593)
    parser.add_argument("--timeout", type=int, default=600)
    parser.add_argument("--allow-unlocked-payload", action="store_true")
    args = parser.parse_args()
    started = time.monotonic()
    report = {
        "passed": False,
        "scope": "Native Village Guard regressions and work counts; no graphical client or FPS/TPS benchmark.",
        "village_guard_harness_sha256": sha(Path(__file__)),
        "village_guard_fixture_sha256": sha(SOURCE),
    }
    try:
        require(120 <= args.timeout <= 1800 and 1024 <= args.port <= 65535,
                "Invalid timeout or localhost port")
        runtime_probe.run(args, report, started + args.timeout, compile_fn=compile_probe,
                          probe_source=SOURCE, workspace_kind="village-guard-probe")
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
