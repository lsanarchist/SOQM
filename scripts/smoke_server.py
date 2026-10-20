#!/usr/bin/env python3
"""Run a bounded, isolated localhost dedicated-server smoke test.

First populate .cache/mods with `python3 scripts/soqm.py verify-downloads`.
This script requires an explicit --accept-eula flag and never opens a player
instance. A successful run proves server startup and clean shutdown only.
"""
from __future__ import annotations

import argparse
from datetime import datetime, timezone
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import socket
import subprocess
import sys
import time
from urllib.parse import urlsplit
from urllib.request import Request, urlopen

from soqm import payload_hashes, stage_payload


ROOT = Path(__file__).resolve().parents[1]
INSTALLER_NAME = "neoforge-21.1.262-installer.jar"
INSTALLER_URL = "https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.262/" + INSTALLER_NAME
INSTALLER_SHA256 = "e86f5d78ed310465bd1ecf79823abcbc58782699b068ac776f4c5e226d994a8f"
INSTALLER_BYTES = 6972019
NATIVE_SHA256 = "f57ce3018e21459d24b50508e8c1b4fd5c16324ec1dc5f316c192aa0130e7fe0"
PORT = 25589
REPORT = ROOT / "reports/smoke-server.json"


class SmokeFailure(RuntimeError):
    """A safe, public diagnostic without machine-specific paths."""


def require(condition, message):
    if not condition:
        raise SmokeFailure(message)


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def sha(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def hashes(directory):
    result = {}
    for path in sorted(directory.rglob("*")):
        require(not path.is_symlink(), "Payload symlinks are not supported")
        if path.is_file():
            result[path.relative_to(directory).as_posix()] = sha(path)
    return result


def production_hashes(root):
    """Hash the installable payload, including compiled quest translations."""
    try:
        return payload_hashes(root)
    except (ValueError, OSError) as error:
        raise SmokeFailure(str(error)) from error


def stage_production_payload(root, destination, expected_hashes, excluded=()):
    """Use the same effective files as the release archive for server fixtures."""
    try:
        stage_payload(destination, expected_hashes, root=root, excluded=excluded)
    except (ValueError, OSError) as error:
        raise SmokeFailure(str(error)) from error


def java_network_flags():
    """Use system trust and configured proxy hosts without recording secrets."""
    flags = []
    trust = Path("/etc/ssl/certs/java/cacerts")
    if trust.is_file():
        flags.append("-Djavax.net.ssl.trustStore=" + str(trust))
    for scheme in ("http", "https"):
        value = os.environ.get(scheme + "_proxy") or os.environ.get(scheme.upper() + "_PROXY")
        if value:
            parsed = urlsplit(value)
            if parsed.hostname and parsed.port:
                flags.extend(("-D" + scheme + ".proxyHost=" + parsed.hostname,
                              "-D" + scheme + ".proxyPort=" + str(parsed.port)))
    flags.append("-Dhttp.nonProxyHosts=localhost|127.*|[::1]")
    return flags


def remaining(deadline):
    seconds = deadline - time.monotonic()
    require(seconds > 0, "Overall smoke-test deadline exceeded")
    return seconds


def restore_installer(deadline):
    target = ROOT / ".cache/tooling" / INSTALLER_NAME
    target.parent.mkdir(parents=True, exist_ok=True)
    if not target.exists():
        partial = target.with_suffix(".part")
        request = Request(INSTALLER_URL, headers={"User-Agent": "SOQM-local-smoke/0.1"})
        try:
            with urlopen(request, timeout=min(60, remaining(deadline))) as response, partial.open("wb") as output:
                size = 0
                while chunk := response.read(1024 * 1024):
                    remaining(deadline)
                    size += len(chunk)
                    require(size <= INSTALLER_BYTES, "Installer exceeded its pinned size")
                    output.write(chunk)
            require(partial.stat().st_size == INSTALLER_BYTES and sha(partial) == INSTALLER_SHA256,
                    "Downloaded NeoForge installer does not match its pin")
            partial.replace(target)
        finally:
            partial.unlink(missing_ok=True)
    require(target.is_file() and target.stat().st_size == INSTALLER_BYTES and sha(target) == INSTALLER_SHA256,
            "Cached NeoForge installer does not match its pin")
    return target


def inspect_errors(server, console):
    categories = {}
    logs = {
        "kubejs_startup": server / "logs/kubejs/startup.log",
        "kubejs_server": server / "logs/kubejs/server.log",
    }
    for category, path in logs.items():
        text = path.read_text(encoding="utf-8", errors="replace") if path.is_file() else ""
        categories[category] = sum(bool(re.search(r"\[(?:ERROR|FATAL)\]|\b(?:ERROR|FATAL)\b", line)) for line in text.splitlines())
    text = console.read_text(encoding="utf-8", errors="replace") if console.is_file() else ""
    fatal_patterns = (
        "Failed to start the minecraft server", "Encountered an unexpected exception",
        "Exception in server tick loop", "Failed to load mods", "Failed to bind to port",
        "OutOfMemoryError", "ModLoadingException", "Reload failed; keeping old data",
    )
    categories["server_fatal"] = sum(any(pattern.casefold() in line.casefold() for pattern in fatal_patterns)
                                     for line in text.splitlines())
    return categories, {category: path.is_file() for category, path in logs.items()}


def run(args, report, deadline):
    require(args.accept_eula, "Pass --accept-eula only after accepting https://aka.ms/MinecraftEULA")
    pack = read(ROOT / "soqm.json")
    require((pack["minecraft"], pack["neoforge"], pack["java_major"]) == ("1.21.1", "21.1.262", 21),
            "This harness only supports the pinned Minecraft 1.21.1 / NeoForge 21.1.262 runtime")
    java = shutil.which(args.java)
    require(java is not None, "Java executable was not found; provide --java")
    version = subprocess.run([java, "-version"], capture_output=True, text=True, timeout=min(10, remaining(deadline)))
    match = re.search(r'version "(\d+)([^"\n]*)"', version.stdout + version.stderr)
    require(version.returncode == 0 and match is not None and int(match.group(1)) == 21,
            "The smoke server requires Java 21")
    report["java_version"] = match.group(1) + match.group(2)
    report["pack_version"] = pack["version"]
    payload = production_hashes(ROOT)
    require(payload == read(ROOT / "metadata/payload.sha256.json"), "Payload differs from its reviewed lock")
    pins = read(ROOT / "metadata/mods.lock.json")["mods"]
    authored = read(ROOT / "metadata/authored-mods.json")
    report["input_sha256"] = {name: sha(ROOT / name) for name in (
        "soqm.json", "metadata/mods.lock.json", "metadata/authored-mods.json", "metadata/payload.sha256.json")}
    report["production_payload_sha256"] = payload
    selected, excluded = [], []
    for row in pins:
        name = row["filename"]
        require(Path(name).name == name and name.endswith(".jar"), "Unsafe cached mod filename")
        path = ROOT / ".cache/mods" / name
        require(path.is_file() and not path.is_symlink(), "Pinned mod cache is incomplete")
        require(path.stat().st_size == row["bytes"] and sha(path) == row["sha256"], "Cached mod differs from its pin")
        require(row.get("side", "both") in ("client", "server", "both"), "Unknown mod side")
        (excluded if row.get("side") == "client" else selected).append(row)
    authored_by_name = {row["filename"]: row for row in authored}
    require(len(authored_by_name) == len(authored), "Duplicate authored mod pins")
    require({p.name for p in (ROOT / "overrides/mods").glob("*.jar")} == set(authored_by_name),
            "Unexpected authored JAR set")
    for name, row in authored_by_name.items():
        require(payload.get("mods/" + name) == row["sha256"], "Authored mod differs from its pin")
    report["third_party_mods_verified"] = len(pins)
    report["excluded_client_mods"] = [row["filename"] for row in excluded]
    report["excluded_client_authored_mods"] = [row["filename"] for row in authored if row.get("side") == "client"]
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as probe:
        try:
            probe.bind(("127.0.0.1", PORT))
        except OSError as error:
            raise SmokeFailure("Local smoke-test port is already in use") from error

    stamp = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%S.%fZ")
    workspace = ROOT / ".work/smoke" / ("run-" + stamp)
    require(not workspace.exists(), "Smoke workspace must be fresh")
    server = workspace / "server"
    server.mkdir(parents=True)
    report["workspace"] = workspace.relative_to(ROOT).as_posix()
    installer = restore_installer(deadline)
    report["installer_sha256"] = sha(installer)
    flags = java_network_flags()
    print("Installing the pinned NeoForge server in an isolated workspace...", flush=True)
    with (workspace / "installer.log").open("w", encoding="utf-8") as output:
        installed = subprocess.run([java, *flags, "-Xmx1G", "-jar", str(installer), "--installServer"],
                                   cwd=server, stdout=output, stderr=subprocess.STDOUT,
                                   timeout=min(360, remaining(deadline)))
    report["installer_exit_code"] = installed.returncode
    require(installed.returncode == 0, "NeoForge installation failed; inspect the local installer log")
    native = server / "libraries/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-server.jar"
    require(native.is_file() and sha(native) == NATIVE_SHA256, "Installed native server differs from its pin")
    report["native_server_sha256"] = sha(native)

    excluded_payload = {"mods/" + name for name, row in authored_by_name.items() if row.get("side") == "client"}
    stage_production_payload(ROOT, server, payload, excluded_payload)
    for row in selected:
        target = server / "mods" / row["filename"]
        require(not target.exists(), "Third-party and authored mod filenames collide")
        shutil.copyfile(ROOT / ".cache/mods" / row["filename"], target)
        require(sha(target) == row["sha256"], "Cached mod staging hash mismatch")
    report["staged_mods_sha256"] = {p.name: sha(p) for p in sorted((server / "mods").glob("*.jar"))}
    (server / "eula.txt").write_text("# Accepted explicitly for this isolated test via --accept-eula\neula=true\n", encoding="utf-8")
    properties = {
        "server-ip": "127.0.0.1", "server-port": str(PORT), "online-mode": "true",
        "max-players": "1", "view-distance": "4", "simulation-distance": "4",
        "level-name": "soqm_smoke_world", "level-seed": "42424244",
        "enable-rcon": "false", "enable-query": "false", "allow-flight": "true",
        "max-tick-time": "180000", "pause-when-empty-seconds": "-1",
    }
    (server / "server.properties").write_text("".join(f"{key}={value}\n" for key, value in properties.items()), encoding="utf-8")
    report["server_settings"] = properties
    report["eula_accepted_explicitly"] = True
    console = workspace / "console.log"
    command = [java, *flags, "-Xms512M", "-Xmx3G", "-XX:ActiveProcessorCount=4",
               "@libraries/net/neoforged/neoforge/21.1.262/unix_args.txt", "nogui"]
    print("Starting localhost smoke server (Java heap limit: 3 GiB)...", flush=True)
    process = None
    startup_at = time.monotonic()
    startup_done = False
    startup_done_at = None
    reload_sent = False
    reload_observed = False
    reload_complete_at = None
    initial_recipe_count = None
    reload_recipe_count = None
    reload_log_offset = 0
    stop_sent = False
    forced = False
    shutdown_done = False
    with console.open("w", encoding="utf-8") as output:
        try:
            process = subprocess.Popen(command, cwd=server, stdout=output, stderr=subprocess.STDOUT,
                                       stdin=subprocess.PIPE, text=True)
            while process.poll() is None:
                text = console.read_text(encoding="utf-8", errors="replace")
                if not startup_done and re.search(r'Done \([\d.]+s\)! For help, type "help"', text):
                    startup_done = True
                    startup_done_at = time.monotonic()
                    counts = re.findall(r"\bLoaded (\d+) recipes\b", text)
                    initial_recipe_count = int(counts[-1]) if counts else None
                if startup_done and not reload_sent and time.monotonic() - startup_done_at >= 5:
                    require(process.stdin is not None, "Smoke server command pipe is unavailable")
                    reload_log_offset = len(text)
                    process.stdin.write("reload\n")
                    process.stdin.flush()
                    reload_sent = True
                    print("Startup reached Done; testing one server data reload...", flush=True)
                if reload_sent and not reload_observed:
                    counts = re.findall(r"\bLoaded (\d+) recipes\b", text[reload_log_offset:])
                    if counts:
                        reload_recipe_count = int(counts[-1])
                        reload_observed = True
                        reload_complete_at = time.monotonic()
                # Leave a short interval for handlers after the observed reload.
                should_stop = reload_observed and time.monotonic() - reload_complete_at >= 5
                # Reserve one minute for an orderly shutdown inside the deadline.
                timed_out = time.monotonic() >= deadline - 60
                if not stop_sent and (should_stop or timed_out):
                    require(process.stdin is not None, "Smoke server command pipe is unavailable")
                    try:
                        process.stdin.write("stop\n")
                        process.stdin.flush()
                        stop_sent = True
                        stop_at = time.monotonic()
                    except (BrokenPipeError, OSError):
                        break
                if stop_sent and time.monotonic() - stop_at >= 45:
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
    shutdown_done = "Stopping server" in text and "All dimensions are saved" in text
    errors, kubejs_logs = inspect_errors(server, console)
    console_errors = sum(bool(re.search(r"\[(?:[^\]\r\n]*/)?ERROR\]", line)) for line in text.splitlines())
    warnings = []
    if "relics.mixins.json:ScreenMixin" in text and "invalid dist DEDICATED_SERVER" in text:
        warnings.append({"kind": "upstream_relics_client_mixin_skipped",
                         "detail": "Relics attempts its client Screen mixin on a dedicated server; startup continues.",
                         "diagnostic_mentions": text.count("Attempted to load class net/minecraft/client/gui/screens/Screen for invalid dist DEDICATED_SERVER")})
    corrected = sorted(set(re.findall(r"Configuration file .*?[/\\]((?:exdeorum|millenaire)-server\.toml) is not correct\. Correcting", text)))
    if corrected:
        warnings.append({"kind": "missing_optional_config_defaults_added", "filenames": corrected})
    if "was registered with MONSTER mob category but was added under CREATURE mob category" in text:
        warnings.append({"kind": "upstream_bloxy_mob_category_mismatch"})
    report.update(server_exit_code=process.returncode, startup_done=startup_done,
                  reload_command_sent=reload_sent, reload_recipe_load_observed=reload_observed,
                  initial_recipe_count=initial_recipe_count, reload_recipe_count=reload_recipe_count,
                  stop_command_sent=stop_sent, shutdown_complete=shutdown_done,
                  forced_termination=forced, error_counts=errors,
                  kubejs_logs_present=kubejs_logs,
                  console_error_count=console_errors, warnings=warnings,
                  server_elapsed_seconds=round(time.monotonic() - startup_at, 2),
                  console_sha256=sha(console))
    require(production_hashes(ROOT) == payload, "Production payload changed during the smoke test")
    require(all(sha(ROOT / name) == digest for name, digest in report["input_sha256"].items()),
            "Input metadata changed during the smoke test; repeat against a stable snapshot")
    require(startup_done, "Dedicated server did not reach the Done startup marker")
    require(reload_sent and reload_observed, "Dedicated server data reload was not observed")
    require(initial_recipe_count is not None and initial_recipe_count == reload_recipe_count,
            "Recipe count changed after the server data reload")
    require(stop_sent and shutdown_done and not forced and process.returncode == 0,
            "Dedicated server did not shut down cleanly")
    require(all(kubejs_logs.values()), "Expected KubeJS startup/server logs are missing")
    require(not any(errors.values()), "Server or KubeJS errors were detected; inspect the local logs")
    report["passed"] = True


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--accept-eula", action="store_true", help="Explicitly accept the Minecraft EULA for this isolated test")
    parser.add_argument("--java", default=os.environ.get("SOQM_JAVA", "java"), help="Java 21 executable")
    parser.add_argument("--timeout", type=int, default=600, help="Overall deadline in seconds, maximum 600")
    args = parser.parse_args()
    if not args.accept_eula:
        parser.error("--accept-eula is required; review https://aka.ms/MinecraftEULA")
    if not 90 <= args.timeout <= 600:
        parser.error("--timeout must be between 90 and 600 seconds")
    started = time.monotonic()
    report = {"schema_version": 1, "checked_at_utc": datetime.now(timezone.utc).isoformat(),
              "passed": False, "scope": "Fresh isolated dedicated localhost server startup, one observed data reload with stable recipe count, KubeJS error inspection and orderly shutdown. No historical audit probes, graphical client, players or full playthrough.",
              "timeout_seconds": args.timeout, "script_sha256": sha(Path(__file__))}
    try:
        run(args, report, started + args.timeout)
    except KeyboardInterrupt:
        report["failure"] = "Local smoke test interrupted; its owned server process was stopped"
    except SmokeFailure as error:
        report["failure"] = str(error)
    except Exception as error:
        # Public reports record only the exception type; raw diagnostics remain
        # in ignored local logs, where URLs, proxy details or host paths may occur.
        report["failure"] = type(error).__name__ + ": inspect local smoke logs"
    finally:
        report["elapsed_seconds"] = round(time.monotonic() - started, 2)
        REPORT.parent.mkdir(parents=True, exist_ok=True)
        temporary = REPORT.with_suffix(".tmp")
        temporary.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        temporary.replace(REPORT)
    print(json.dumps({key: report[key] for key in ("passed", "elapsed_seconds")}, indent=2))
    if not report["passed"]:
        print("SOQM smoke test failed: " + report["failure"], file=sys.stderr)
    return 0 if report["passed"] else 1


if __name__ == "__main__":
    raise SystemExit(main())
