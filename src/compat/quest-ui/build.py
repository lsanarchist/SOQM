#!/usr/bin/env python3
"""Build against pinned native client APIs, or repack reviewed classes with new resources."""
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
RESOURCE_BASELINE_HASH = "66688e1cca5af8e5218afeaa57c21dc7251225b36b73eff82082444e2472b9c0"
RESOURCE_BASELINE_SOURCES_HASH = "7915a7e996a35e705dc7d7bd31aad7dc81d02174951383988d9d6fb562fadc60"
RESOURCE_BASELINE_CLASSES_HASH = "3c07145be4985d15aa8b53942e9936382088e0a7980beed98673e2bd4265cb58"
RESOURCE_UPDATES = frozenset({
    "META-INF/neoforge.mods.toml",
    "assets/qm_quest_ui/reward_catalog.json",
    "assets/qm_quest_ui/lang/en_us.json",
    "assets/qm_quest_ui/lang/uk_ua.json",
})


def sha(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def entries_hash(entries):
    """Hash sorted relative names and content hashes, including additions/removals."""
    manifest = {name: hashlib.sha256(content).hexdigest() for name, content in entries.items()}
    return hashlib.sha256(json.dumps(manifest, sort_keys=True, separators=(",", ":")).encode()).hexdigest()


def directory_entries(directory):
    return {path.relative_to(directory).as_posix(): path.read_bytes()
            for path in sorted(directory.rglob("*")) if path.is_file()}


def resource_entries():
    entries = {"META-INF/MANIFEST.MF": b"Manifest-Version: 1.0\r\nFMLModType: MOD\r\nAutomatic-Module-Name: qm.quest.ui\r\n\r\n",
               "LICENSE": (MODULE / "LICENSE").read_bytes()}
    resources = directory_entries(MODULE / "resources")
    if any(name.endswith(".class") for name in resources):
        raise ValueError("Source resources must not contain class files")
    entries.update(resources)
    return entries


def write_jar(output, entries):
    output.parent.mkdir(parents=True, exist_ok=True)
    with ZipFile(output, "w", compression=ZIP_DEFLATED, compresslevel=9) as archive:
        for name, content in sorted(entries.items()):
            info = ZipInfo(name, (1980, 1, 1, 0, 0, 0))
            info.create_system = 3; info.external_attr = 0o100644 << 16; info.compress_type = ZIP_DEFLATED
            archive.writestr(info, content, compresslevel=9)


def resource_only(baseline, output):
    if baseline.resolve() == output.resolve():
        raise ValueError("Keep the reviewed baseline separate from the resource-only output")
    if not baseline.is_file() or sha(baseline) != RESOURCE_BASELINE_HASH:
        raise ValueError("Resource-only builds require the SHA256-pinned qm-quest-ui-0.4.5.jar")
    source_hash = entries_hash(directory_entries(MODULE / "src"))
    if source_hash != RESOURCE_BASELINE_SOURCES_HASH:
        raise ValueError("Java source tree changed; use a native compilation instead of resource-only mode")
    with ZipFile(baseline) as archive:
        previous = {name: archive.read(name) for name in archive.namelist()}
    classes = {name: content for name, content in previous.items() if name.endswith(".class")}
    if entries_hash(classes) != RESOURCE_BASELINE_CLASSES_HASH:
        raise ValueError("Reviewed class names or bytes do not match the baseline pin")
    resources = resource_entries()
    old_resources = {name: content for name, content in previous.items() if not name.endswith(".class")}
    if resources.keys() != old_resources.keys():
        raise ValueError("Resource-only mode cannot add or remove resource paths")
    for name in resources.keys() - RESOURCE_UPDATES:
        if resources[name] != old_resources[name]:
            raise ValueError("Resource-only mode cannot change " + name)
    toml_path = "META-INF/neoforge.mods.toml"
    old_descriptor = tomllib.loads(old_resources[toml_path].decode())
    descriptor = tomllib.loads(resources[toml_path].decode())
    version = descriptor["mods"][0]["version"]
    old_descriptor["mods"][0]["version"] = version
    if descriptor != old_descriptor:
        raise ValueError("Resource-only mode may change only the mod version in neoforge.mods.toml")
    write_jar(output, {**classes, **resources})
    with ZipFile(output) as archive:
        written_classes = {name: archive.read(name) for name in archive.namelist() if name.endswith(".class")}
    if written_classes != classes:
        raise ValueError("Resource-only output changed the reviewed class-name set or class bytes")
    return {"filename": output.name, "version": version, "sha256": sha(output),
            "bytes": output.stat().st_size, "build_mode": "resource_only",
            "baseline_sha256": RESOURCE_BASELINE_HASH, "source_sha256": source_hash,
            "class_count": len(classes), "classes_sha256": entries_hash(classes)}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--runtime", type=Path, help="NeoForge 21.1.262 client installation")
    parser.add_argument("--javac")
    parser.add_argument("--mods", type=Path, default=ROOT / ".cache/mods")
    parser.add_argument("--output", type=Path)
    parser.add_argument("--verify", action="store_true", help="Run native Mixin, minimap sizing, geometry and reward catalogue probes")
    parser.add_argument("--resource-only-from", type=Path, metavar="BASELINE_JAR",
                        help="Reuse byte-identical classes from the pinned 0.4.5 JAR; update reviewed resources only")
    args = parser.parse_args()
    version = tomllib.loads((MODULE / "resources/META-INF/neoforge.mods.toml").read_text())["mods"][0]["version"]
    output = args.output or ROOT / "overrides/mods" / f"qm-quest-ui-{version}.jar"
    if args.resource_only_from is not None:
        if args.runtime is not None or args.javac is not None or args.verify:
            parser.error("Resource-only mode does not compile or run native probes; omit --runtime, --javac and --verify")
        try:
            result = resource_only(args.resource_only_from, output)
        except ValueError as error:
            parser.error(str(error))
        print(json.dumps(result, indent=2))
        return
    if args.runtime is None or args.javac is None:
        parser.error("Native compilation requires --runtime and --javac")
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
            for probe in ("HudMixinProbe", "HudGeometryProbe", "HudMinimapScaleProbe", "RewardCatalogueProbe"):
                subprocess.run([java, "-Dmixin.service=qm.hudprobe.HudMixinProbe$Service", "-classpath", classpath,
                                "qm.hudprobe." + probe], check=True)
        write_jar(output, {**directory_entries(classes), **resource_entries()})
    print(json.dumps({"filename": output.name, "version": version, "sha256": sha(output),
                      "bytes": output.stat().st_size, "build_mode": "native_compile",
                      "compiler": compiler, "client_sha256": CLIENT_HASH}, indent=2))


if __name__ == "__main__":
    try:
        main()
    except subprocess.CalledProcessError:
        raise SystemExit("Compilation/native verification failed; see diagnostics above.")
