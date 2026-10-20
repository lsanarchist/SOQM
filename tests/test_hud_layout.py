"""Run production HUD geometry and verify that the shipped client adapter is complete.

The full native gate is build.py --verify: it applies the actual Mixin config to
the hash-pinned mod bytecode. This small default-suite test needs only a JDK and
does not pretend to exercise an OpenGL Minecraft client.
"""
from pathlib import Path
import json
import shutil
import subprocess
import tempfile
import tomllib
import unittest
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parents[1]
MODULE = ROOT / "src/compat/quest-ui"


class HudLayoutTests(unittest.TestCase):
    @unittest.skipUnless(shutil.which("javac") and shutil.which("java"), "JDK 17+ required for pure HUD geometry")
    def test_real_geometry_handles_small_viewports_and_long_panels(self):
        with tempfile.TemporaryDirectory(prefix="soqm-hud-geometry-") as directory:
            compile_result = subprocess.run([
                shutil.which("javac"), "--release", "17", "-d", directory,
                str(MODULE / "src/qm/questui/client/HudGeometry.java"),
                str(ROOT / "tests/runtime/hud/HudGeometryProbe.java"),
            ], capture_output=True, text=True, timeout=30)
            self.assertEqual(compile_result.returncode, 0, compile_result.stdout + compile_result.stderr)
            result = subprocess.run([
                shutil.which("java"), "-XX:-UsePerfData", "-cp", directory, "qm.hudprobe.HudGeometryProbe",
            ], capture_output=True, text=True, timeout=30)
            self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
            self.assertIn("HUD_GEOMETRY_OK", result.stdout)
            self.assertIn("320x180,640x360,960x540", result.stdout)

    def test_shipped_adapter_has_matching_resources_and_all_client_mixins(self):
        resources = MODULE / "resources"
        config = json.loads((resources / "qm_quest_ui.mixins.json").read_text())
        metadata = tomllib.loads((resources / "META-INF/neoforge.mods.toml").read_text())
        version = metadata["mods"][0]["version"]
        jars = sorted((ROOT / "overrides/mods").glob("qm-quest-ui-*.jar"))
        self.assertEqual([path.name for path in jars], [f"qm-quest-ui-{version}.jar"])
        self.assertNotIn("mixins", config, "HUD injections must remain client-only")
        with ZipFile(jars[0]) as archive:
            for path in resources.rglob("*"):
                if path.is_file():
                    name = path.relative_to(resources).as_posix()
                    self.assertEqual(archive.read(name), path.read_bytes(), name)
            for mixin in config["client"]:
                name = config["package"].replace(".", "/") + "/" + mixin + ".class"
                bytecode = archive.read(name)
                self.assertEqual(bytecode[:4], b"\xca\xfe\xba\xbe", name)
                self.assertEqual(int.from_bytes(bytecode[6:8], "big"), 65, "Production adapter must target Java 21")
            self.assertFalse(any(name.startswith("qm/hudprobe/") for name in archive.namelist()),
                             "Native verification fixtures must not ship")


if __name__ == "__main__":
    unittest.main()
