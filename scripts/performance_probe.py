#!/usr/bin/env python3
"""Run native optimization regressions in a fresh full-pack localhost server.

Reuses the campaign probe's pinned staging, startup, reload and clean-shutdown
harness. The additional Java fixtures and original-bytecode comparison loader
are test-only and never enter the production package.
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

SOURCE = ROOT / "tests/runtime/performance/PerformanceProbe.java"
REPORT = ROOT / "reports/performance-probe.json"


def sources():
    return [path for name in ("performance", "worldgen", "guardian", "questauto", "dungeons")
            for path in sorted((ROOT / "tests/runtime" / name).glob("*.java"))]


def compile_probe(java, runtime, workspace, deadline):
    native = runtime / "libraries/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-server.jar"
    javac = Path(java).with_name("javac")
    classes = workspace / "probe-classes"
    classes.mkdir()
    jars = [native, *sorted((runtime / "libraries").rglob("*.jar")),
            *sorted((ROOT / ".cache/mods").glob("*.jar")), *sorted((ROOT / "overrides/mods").glob("*.jar"))]
    compiled = subprocess.run([str(javac), "--release", "21", "-encoding", "UTF-8", "-proc:none",
                               "-classpath", ":".join(map(str, jars)), "-d", str(classes), *map(str, sources())],
                              capture_output=True, text=True, timeout=min(90, remaining(deadline)))
    (workspace / "compile.log").write_text(compiled.stdout + compiled.stderr, encoding="utf-8")
    require(compiled.returncode == 0, "Performance fixtures did not compile; inspect compile.log")
    jar = workspace / "soqm-performance-probe-test-only.jar"
    metadata = '''modLoader="javafml"
loaderVersion="[4,)"
license="MIT"
[[mods]]
modId="soqm_performance_probe"
version="0.0.0"
displayName="SOQM isolated performance regressions"
description="Test-only synthetic fixtures; never ship in the pack."
'''
    with ZipFile(jar, "w", ZIP_DEFLATED) as output:
        output.writestr("META-INF/neoforge.mods.toml", metadata)
        output.write(ROOT / "src/compat/runtime-optimizations/LICENSE", "LICENSE")
        for path in sorted(classes.rglob("*.class")):
            output.write(path, path.relative_to(classes).as_posix())
    return jar


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", required=True)
    parser.add_argument("--java", default="java")
    parser.add_argument("--accept-eula", action="store_true")
    parser.add_argument("--baseline-ref", default="HEAD")
    parser.add_argument("--port", type=int, default=25591)
    parser.add_argument("--timeout", type=int, default=600)
    parser.add_argument("--allow-unlocked-payload", action="store_true")
    args = parser.parse_args()
    started = time.monotonic()
    report = {"passed": False, "scope": "Native behavioral and work-count regressions; no FPS/TPS benchmark.",
              "performance_harness_sha256": sha(Path(__file__)),
              "fixture_sources_sha256": {p.relative_to(ROOT).as_posix(): sha(p) for p in sources()}}
    try:
        require(120 <= args.timeout <= 1800 and 1024 <= args.port <= 65535, "Invalid timeout or localhost port")
        runtime_probe.run(args, report, started + args.timeout, compile_fn=compile_probe,
                          probe_source=SOURCE, workspace_kind="performance-probe")
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
