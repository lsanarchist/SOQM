#!/usr/bin/env python3
"""Deterministically build the client adapter against hash-pinned native client APIs."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import subprocess
import tempfile
import tomllib
from zipfile import ZipFile, ZipInfo, ZIP_DEFLATED

MODULE = Path(__file__).resolve().parent
ROOT = MODULE.parents[2]
CLIENT_HASH = "aff67d0c3bbd015ecd881bb37416b740ada2e1e854433501ec5bb9381dbdd1c2"


def sha(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, required=True, help="NeoForge 21.1.262 client installation")
    parser.add_argument("--javac", required=True)
    parser.add_argument("--mods", type=Path, default=ROOT / ".cache/mods")
    parser.add_argument("--output", type=Path)
    parser.add_argument("--verify", action="store_true", help="Run native Mixin transformations and geometry probes")
    args = parser.parse_args()
    compiler = subprocess.check_output([args.javac, "-version"], text=True).strip()
    if not compiler.startswith("javac 21."):
        parser.error("A Java 21 compiler is required")
    libraries = args.runtime.resolve() / "libraries"
    client = libraries / "net/neoforged/neoforge/21.1.262/neoforge-21.1.262-client.jar"
    if not client.is_file() or sha(client) != CLIENT_HASH:
        parser.error("Mapped NeoForge client does not match the 21.1.262 pin")
    dependencies = [client] + [p for p in sorted(libraries.rglob("*.jar"))
                               if p != client and not p.name.endswith("-server.jar")]
    for row in json.loads((ROOT / "metadata/mods.lock.json").read_text())["mods"]:
        path = args.mods / row["filename"]
        if not path.is_file() or path.stat().st_size != row["bytes"] or sha(path) != row["sha256"]:
            parser.error("Missing/mismatched pinned API: " + row["filename"])
        dependencies.append(path)
    version = tomllib.loads((MODULE / "resources/META-INF/neoforge.mods.toml").read_text())["mods"][0]["version"]
    output = args.output or ROOT / "overrides/mods" / f"qm-quest-ui-{version}.jar"
    with tempfile.TemporaryDirectory(prefix="soqm-hud-compile-") as folder:
        temporary = Path(folder)
        # XaeroLib and some public API dependencies are nested jars. They are
        # extracted only from the already verified parent mod artifacts.
        for jar in list(dependencies):
            with ZipFile(jar) as archive:
                for name in archive.namelist():
                    if name.startswith("META-INF/jarjar/") and name.endswith(".jar"):
                        dest = temporary / (hashlib.sha256(str(jar).encode()).hexdigest()[:12] + "-" + Path(name).name)
                        dest.write_bytes(archive.read(name)); dependencies.append(dest)
        classes = temporary / "classes"; classes.mkdir()
        subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-proc:none", "-classpath",
                        os.pathsep.join(map(str, dependencies)), "-d", str(classes),
                        *map(str, sorted((MODULE / "src").rglob("*.java")))], check=True)
        if args.verify:
            probes = temporary / "probe-classes"; probes.mkdir()
            classpath = os.pathsep.join(map(str, [probes, classes, MODULE / "resources", *dependencies]))
            subprocess.run([args.javac, "--release", "21", "-encoding", "UTF-8", "-proc:none", "-classpath",
                            classpath, "-d", str(probes),
                            *map(str, sorted((ROOT / "tests/runtime/hud").glob("*.java")))], check=True)
            services = probes / "META-INF/services"; services.mkdir(parents=True)
            (services / "org.spongepowered.asm.service.IMixinService").write_text("qm.hudprobe.HudMixinProbe$Service\n")
            (services / "org.spongepowered.asm.service.IGlobalPropertyService").write_text("qm.hudprobe.HudMixinProbe$Blackboard\n")
            java = str(Path(args.javac).with_name("java"))
            for probe in ("HudMixinProbe", "HudGeometryProbe"):
                subprocess.run([java, "-Dmixin.service=qm.hudprobe.HudMixinProbe$Service", "-classpath", classpath,
                                "qm.hudprobe." + probe], check=True)
        entries = {"META-INF/MANIFEST.MF": b"Manifest-Version: 1.0\r\nFMLModType: MOD\r\nAutomatic-Module-Name: qm.quest.ui\r\n\r\n",
                   "LICENSE": (MODULE / "LICENSE").read_bytes()}
        for directory in (classes, MODULE / "resources"):
            for path in sorted(directory.rglob("*")):
                if path.is_file(): entries[path.relative_to(directory).as_posix()] = path.read_bytes()
        output.parent.mkdir(parents=True, exist_ok=True)
        with ZipFile(output, "w", compression=ZIP_DEFLATED, compresslevel=9) as archive:
            for name, content in sorted(entries.items()):
                info = ZipInfo(name, (1980, 1, 1, 0, 0, 0))
                info.create_system = 3; info.external_attr = 0o100644 << 16; info.compress_type = ZIP_DEFLATED
                archive.writestr(info, content, compresslevel=9)
    print(json.dumps({"filename": output.name, "version": version, "sha256": sha(output),
                      "bytes": output.stat().st_size, "compiler": compiler, "client_sha256": CLIENT_HASH}, indent=2))


if __name__ == "__main__":
    try:
        main()
    except subprocess.CalledProcessError:
        raise SystemExit("Compilation/native verification failed; see diagnostics above.")
