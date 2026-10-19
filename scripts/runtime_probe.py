#!/usr/bin/env python3
"""Exercise native FTB claims, progress upgrades and an actual AE2 terminal craft.

This optional integration gate needs the Java 21 JDK, the verified dependency
cache and a NeoForge installation produced by smoke_server.py. It compiles a
test-only mod and runs it in a fresh localhost world; the test JAR never enters
the pack. --baseline-ref identifies the version whose nonempty quest progress
and claimed rewards must survive the upgrade. No real player data is read.
"""
from __future__ import annotations

import argparse
from datetime import datetime, timezone
import json
from pathlib import Path
import re
import shutil
import socket
import subprocess
import sys
import time
import zipfile

from smoke_server import (
    ROOT, NATIVE_SHA256, SmokeFailure, hashes, inspect_errors,
    java_network_flags, read, remaining, require, sha,
)


REPORT = ROOT / "reports/runtime-probe.json"
QUESTS = Path("overrides/config/ftbquests/quests")
SOURCE = ROOT / "tests/runtime/SoqmRuntimeProbe.java"


def baseline_quests(ref, target, deadline):
    require(ref and not ref.startswith("-"), "Provide a valid baseline Git reference")
    resolved = subprocess.run(["git", "rev-parse", "--verify", ref + "^{commit}"], cwd=ROOT,
                              capture_output=True, text=True, timeout=min(10, remaining(deadline)))
    commit = resolved.stdout.strip()
    require(resolved.returncode == 0 and re.fullmatch(r"[a-f0-9]{40}", commit), "Baseline commit is unavailable")
    listing = subprocess.run(["git", "ls-tree", "-r", "--name-only", commit, "--", QUESTS.as_posix()],
                             cwd=ROOT, capture_output=True, text=True, timeout=min(10, remaining(deadline)))
    require(listing.returncode == 0 and listing.stdout.strip(), "Baseline contains no quest files")
    for filename in listing.stdout.splitlines():
        relative = Path(filename).relative_to(QUESTS)
        require(".." not in relative.parts, "Unsafe baseline quest path")
        blob = subprocess.run(["git", "show", commit + ":" + filename], cwd=ROOT,
                              capture_output=True, timeout=min(10, remaining(deadline)))
        require(blob.returncode == 0, "Could not read baseline quest file")
        path = target / relative
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_bytes(blob.stdout)
    return commit


def compile_probe(java, runtime, workspace, deadline):
    native = runtime / "libraries/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-server.jar"
    require(native.is_file() and sha(native) == NATIVE_SHA256, "Installed NeoForge native server differs from its pin")
    javac = Path(java).with_name("javac")
    require(javac.is_file(), "Provide a Java 21 JDK; javac is needed for the test-only mod")
    classes = workspace / "probe-classes"
    classes.mkdir()
    jars = [native, *sorted((runtime / "libraries").rglob("*.jar")), *sorted((ROOT / ".cache/mods").glob("*.jar"))]
    compiled = subprocess.run([str(javac), "-proc:none", "--release", "21", "-cp", ":".join(map(str, jars)),
                               "-d", str(classes), str(SOURCE)], capture_output=True, text=True,
                              timeout=min(60, remaining(deadline)))
    (workspace / "compile.log").write_text(compiled.stdout + compiled.stderr, encoding="utf-8")
    require(compiled.returncode == 0, "Native API probe did not compile; inspect compile.log")
    jar = workspace / "soqm-runtime-probe-test-only.jar"
    metadata = '''modLoader="javafml"
loaderVersion="[4,)"
license="MIT"
[[mods]]
modId="soqm_runtime_probe"
version="0.0.0"
displayName="SOQM isolated runtime validation fixture"
description="Test-only synthetic player/team fixtures; never ship in the pack."
'''
    with zipfile.ZipFile(jar, "w", zipfile.ZIP_DEFLATED) as output:
        output.writestr("META-INF/neoforge.mods.toml", metadata)
        output.write(ROOT / "LICENSE", "LICENSE")
        for path in sorted(classes.rglob("*.class")):
            output.write(path, path.relative_to(classes).as_posix())
    return jar


def run(args, report, deadline):
    require(args.accept_eula, "Pass --accept-eula after accepting https://aka.ms/MinecraftEULA")
    java = shutil.which(args.java)
    require(java is not None, "Java executable not found")
    java = str(Path(java).resolve())
    version = subprocess.run([java, "-version"], capture_output=True, text=True, timeout=min(10, remaining(deadline)))
    match = re.search(r'version "(21[^"\n]*)"', version.stdout + version.stderr)
    require(version.returncode == 0 and match, "The integration probe needs Java 21")
    pack = read(ROOT / "soqm.json")
    require((pack["minecraft"], pack["neoforge"], pack["java_major"]) == ("1.21.1", "21.1.262", 21), "Unsupported runtime")
    runtime = Path(args.runtime).resolve()
    require((runtime / "libraries/net/neoforged/neoforge/21.1.262/unix_args.txt").is_file(),
            "--runtime must name an installed smoke-test server directory")
    payload = hashes(ROOT / "overrides")
    reviewed = payload == read(ROOT / "metadata/payload.sha256.json")
    require(reviewed or args.allow_unlocked_payload, "Payload differs from its reviewed lock")
    pins = read(ROOT / "metadata/mods.lock.json")["mods"]
    authored = read(ROOT / "metadata/authored-mods.json")
    authored_by_name = {row["filename"]: row for row in authored}
    require({p.name for p in (ROOT / "overrides/mods").glob("*.jar")} == set(authored_by_name), "Unexpected authored mod set")
    for row in authored:
        require(payload.get("mods/" + row["filename"]) == row["sha256"], "Authored mod differs from its pin")
    selected = []
    for row in pins:
        name = row["filename"]
        require(Path(name).name == name and name.endswith(".jar"), "Unsafe mod filename")
        path = ROOT / ".cache/mods" / name
        require(path.is_file() and not path.is_symlink() and path.stat().st_size == row["bytes"]
                and sha(path) == row["sha256"], "Pinned dependency cache is incomplete or mismatched")
        require(row.get("side", "both") in ("client", "server", "both"), "Unknown dependency side")
        if row.get("side") != "client":
            selected.append(row)
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as probe:
        try:
            probe.bind(("127.0.0.1", args.port))
        except OSError as error:
            raise SmokeFailure("Probe localhost port is already in use") from error
    stamp = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%S.%fZ")
    workspace = ROOT / ".work/runtime-probe" / ("run-" + stamp)
    server = workspace / "server"
    server.mkdir(parents=True)
    report.update(workspace=workspace.relative_to(ROOT).as_posix(), java_version=match.group(1),
                  pack_version=pack["version"], reviewed_payload_match=reviewed,
                  development_payload_allowed=args.allow_unlocked_payload, production_payload_sha256=payload,
                  third_party_mods_verified=len(pins), native_server_sha256=NATIVE_SHA256,
                  probe_source_sha256=sha(SOURCE), harness_sha256=sha(Path(__file__)))
    baseline = workspace / "baseline-quests"
    report["baseline_commit"] = baseline_quests(args.baseline_ref, baseline, deadline)
    report["baseline_quests_sha256"] = hashes(baseline)
    print("Compiling the test-only mod against the pinned native runtime...", flush=True)
    jar = compile_probe(java, runtime, workspace, deadline)
    # Runtime libraries are immutable reused dependencies; each tested payload,
    # configuration and world is an independent copy in a fresh directory.
    (server / "libraries").symlink_to(runtime / "libraries", target_is_directory=True)
    for source in (ROOT / "overrides").rglob("*"):
        if not source.is_file():
            continue
        relative = source.relative_to(ROOT / "overrides")
        if relative.parts[0] == "mods" and authored_by_name[source.name].get("side") == "client":
            continue
        target = server / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source, target)
        require(sha(target) == payload[relative.as_posix()], "Payload changed while staging")
    for row in selected:
        target = server / "mods" / row["filename"]
        require(not target.exists(), "Dependency filename collision")
        shutil.copyfile(ROOT / ".cache/mods" / row["filename"], target)
        require(sha(target) == row["sha256"], "Dependency staging mismatch")
    shutil.copyfile(jar, server / "mods" / jar.name)
    report["test_mod_sha256"] = sha(jar)
    (server / "soqm_probe_config.json").write_text(json.dumps({"baseline_quests": str(baseline)}), encoding="utf-8")
    (server / "eula.txt").write_text("# Explicitly accepted for this isolated test\neula=true\n", encoding="utf-8")
    settings = {
        "server-ip": "127.0.0.1", "server-port": str(args.port), "online-mode": "true", "max-players": "1",
        "view-distance": "4", "simulation-distance": "4", "level-name": "soqm_probe_world", "level-seed": "42424244",
        "enable-rcon": "false", "enable-query": "false", "allow-flight": "true", "max-tick-time": "180000",
        "pause-when-empty-seconds": "-1",
    }
    (server / "server.properties").write_text("".join(f"{key}={value}\n" for key, value in settings.items()), encoding="utf-8")
    report["server_settings"] = settings
    console = workspace / "console.log"
    command = [java, *java_network_flags(), "-Xms512M", "-Xmx3G", "-XX:ActiveProcessorCount=4",
               "@libraries/net/neoforged/neoforge/21.1.262/unix_args.txt", "nogui"]
    print("Starting full-pack localhost server with native player/team interaction fixtures...", flush=True)
    process = None
    startup_done = False
    initial_count = reload_count = None
    reload_offset = reload_at = stop_at = None
    forced = False
    observed = {}
    with console.open("w", encoding="utf-8") as output:
        try:
            process = subprocess.Popen(command, cwd=server, stdout=output, stderr=subprocess.STDOUT,
                                       stdin=subprocess.PIPE, text=True)
            while process.poll() is None:
                text = console.read_text(encoding="utf-8", errors="replace")
                startup_done |= bool(re.search(r'Done \([\d.]+s\)! For help, type "help"', text))
                result_path = server / "soqm_probe_result.json"
                if result_path.is_file():
                    try:
                        observed = read(result_path)
                    except (json.JSONDecodeError, OSError):
                        pass  # The server may be midway through its small result write.
                if startup_done and observed.get("phase") == "awaiting_reload" and reload_offset is None:
                    counts = re.findall(r"\bLoaded (\d+) recipes\b", text)
                    initial_count = int(counts[-1]) if counts else None
                    reload_offset = len(text)
                    process.stdin.write("reload\n")
                    process.stdin.flush()
                    print("Native interaction checks passed; reloading data and checking saved progress...", flush=True)
                if reload_offset is not None and reload_at is None:
                    counts = re.findall(r"\bLoaded (\d+) recipes\b", text[reload_offset:])
                    if counts:
                        reload_count = int(counts[-1])
                        reload_at = time.monotonic()
                if reload_at is not None and time.monotonic() - reload_at >= 5:
                    (server / "soqm_probe_reload_complete").touch()
                should_stop = observed.get("phase") == "finished" or time.monotonic() >= deadline - 60
                if should_stop and stop_at is None:
                    try:
                        process.stdin.write("stop\n")
                        process.stdin.flush()
                        stop_at = time.monotonic()
                    except (BrokenPipeError, OSError):
                        break
                if stop_at is not None and time.monotonic() - stop_at >= 45:
                    forced = True
                    process.terminate()
                    break
                time.sleep(0.5)
            try:
                process.wait(timeout=min(10, max(1, deadline - time.monotonic())))
            except subprocess.TimeoutExpired:
                forced = True
                process.kill()
                process.wait(timeout=5)
        finally:
            if process is not None and process.poll() is None:
                forced = True
                process.terminate()
                try:
                    process.wait(timeout=5)
                except subprocess.TimeoutExpired:
                    process.kill()
                    process.wait(timeout=5)
            if process is not None and process.stdin is not None:
                process.stdin.close()
    text = console.read_text(encoding="utf-8", errors="replace")
    errors, logs = inspect_errors(server, console)
    report.update(probe=observed, startup_done=startup_done, initial_recipe_count=initial_count,
                  reload_recipe_count=reload_count, server_exit_code=process.returncode, forced_termination=forced,
                  error_counts=errors, kubejs_logs_present=logs, console_sha256=sha(console),
                  production_payload_unchanged=hashes(ROOT / "overrides") == payload,
                  shutdown_complete="Stopping server" in text and "All dimensions are saved" in text)
    require(report["production_payload_unchanged"] or args.allow_unlocked_payload, "Payload changed during the test")
    require(startup_done and observed.get("passed"), "Native interaction probe failed; inspect the report and console")
    require(initial_count is not None and initial_count == reload_count, "Recipe set changed or reload was not observed")
    require(all(logs.values()) and not any(errors.values()), "Runtime reported a KubeJS or fatal server error")
    require(process.returncode == 0 and not forced and report["shutdown_complete"], "Server did not shut down cleanly")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--accept-eula", action="store_true")
    parser.add_argument("--runtime", required=True, help="Server directory from a successful smoke_server.py run")
    parser.add_argument("--baseline-ref", required=True, help="Original Git commit for the progress upgrade fixture")
    parser.add_argument("--java", default="java")
    parser.add_argument("--port", type=int, default=25590)
    parser.add_argument("--timeout", type=int, default=600)
    parser.add_argument("--allow-unlocked-payload", action="store_true", help="Development only; report records unreviewed payload")
    args = parser.parse_args()
    start = time.monotonic()
    report = {"passed": False, "scope": "Native dedicated-server interaction and persistence tests; no graphical client or survival playthrough."}
    try:
        require(120 <= args.timeout <= 1800 and 1024 <= args.port <= 65535, "Invalid bounded timeout or localhost port")
        run(args, report, start + args.timeout)
        report["passed"] = True
    except (SmokeFailure, OSError, subprocess.SubprocessError) as error:
        report["failure"] = str(error)
    report["elapsed_seconds"] = round(time.monotonic() - start, 2)
    REPORT.parent.mkdir(exist_ok=True)
    REPORT.write_text(json.dumps(report, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(json.dumps({"passed": report["passed"], "failure": report.get("failure"),
                      "report": REPORT.relative_to(ROOT).as_posix(), "workspace": report.get("workspace")}), flush=True)
    return 0 if report["passed"] else 1


if __name__ == "__main__":
    sys.exit(main())
