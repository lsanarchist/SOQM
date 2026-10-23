"""Prove resource-only updates keep the reviewed client bytecode and fail closed.

Small archives exercise packaging and rejection paths; they are not native
Minecraft fixtures. The shipped-JAR check uses the real reviewed class digest.
"""
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import tomllib
from types import ModuleType
import unittest
from unittest.mock import patch
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parents[1]
MODULE = ROOT / "src/compat/quest-ui"
build = ModuleType("quest_ui_resource_builder")
build.__file__ = str(MODULE / "build.py")
# Do not create __pycache__ in the provenance-fingerprinted module source tree.
exec(compile((MODULE / "build.py").read_bytes(), build.__file__, "exec"), build.__dict__)


class QuestUiResourceBuildTests(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory(prefix="soqm-ui-resource-test-")
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        self.module = self.root / "module"
        self.source = self.module / "src/qm/Example.java"
        self.source.parent.mkdir(parents=True)
        self.source.write_text("package qm; class Example {}\n")
        (self.module / "LICENSE").write_text("Fixture license\n")
        self.resources = self.module / "resources"
        initial_resources = {
            "META-INF/neoforge.mods.toml": '[[mods]]\nmodId="qm_quest_ui"\nversion="0.4.5"\n',
            "qm_quest_ui.mixins.json": '{"client": ["ExampleMixin"]}\n',
            "assets/qm_quest_ui/lang/en_us.json": '{"draws": "old"}\n',
            "assets/qm_quest_ui/lang/uk_ua.json": '{"draws": "старий"}\n',
            "assets/qm_quest_ui/reward_catalog.json": '{"items": ["millenaire:masa"]}\n',
        }
        for name, content in initial_resources.items():
            self.write_resource(name, content)
        override = patch.object(build, "MODULE", self.module)
        override.start()
        self.addCleanup(override.stop)
        self.classes = {"qm/Example.class": b"reviewed outer class bytes",
                        "qm/Example$Nested.class": b"reviewed nested class bytes"}
        self.baseline = self.root / "reviewed-0.4.5.jar"
        build.write_jar(self.baseline, {**self.classes, **build.resource_entries()})
        pins = patch.multiple(build, RESOURCE_BASELINE_HASH=build.sha(self.baseline),
                              RESOURCE_BASELINE_SOURCES_HASH=build.entries_hash(build.directory_entries(self.module / "src")),
                              RESOURCE_BASELINE_CLASSES_HASH=build.entries_hash(self.classes))
        pins.start()
        self.addCleanup(pins.stop)
        self.write_resource("META-INF/neoforge.mods.toml", '[[mods]]\nmodId="qm_quest_ui"\nversion="0.4.6"\n')
        self.write_resource("assets/qm_quest_ui/lang/en_us.json", '{"draws": "one copy per claim"}\n')
        self.write_resource("assets/qm_quest_ui/lang/uk_ua.json", '{"draws": "одна копія"}\n')
        self.write_resource("assets/qm_quest_ui/reward_catalog.json", '{"items": ["minecraft:bread"]}\n')
        self.output = self.root / "updated-0.4.6.jar"

    def write_resource(self, name, content):
        path = self.resources / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")

    def test_repack_is_deterministic_preserves_all_classes_and_replaces_resources(self):
        first = build.resource_only(self.baseline, self.output)
        other = self.root / "second.jar"
        second = build.resource_only(self.baseline, other)
        self.assertEqual(self.output.read_bytes(), other.read_bytes())
        self.assertEqual(first["sha256"], second["sha256"])
        self.assertEqual(first["build_mode"], "resource_only")
        self.assertNotIn("compiler", first)
        self.assertNotIn("client_sha256", first)
        self.assertEqual(first["version"], "0.4.6")
        self.assertEqual(first["class_count"], 2)
        expected = {**self.classes, **build.resource_entries()}
        with ZipFile(self.output) as archive:
            self.assertEqual(archive.namelist(), sorted(expected))
            self.assertEqual({name: archive.read(name) for name in archive.namelist()}, expected)
            self.assertNotIn(b"millenaire:masa", archive.read("assets/qm_quest_ui/reward_catalog.json"))
            for info in archive.infolist():
                self.assertEqual(info.date_time, (1980, 1, 1, 0, 0, 0))
                self.assertEqual(info.external_attr >> 16, 0o100644)

    def test_rejects_unpinned_archive_without_writing_output(self):
        with self.baseline.open("ab") as stream:
            stream.write(b"tampered")
        with self.assertRaisesRegex(ValueError, "SHA256-pinned"):
            build.resource_only(self.baseline, self.output)
        self.assertFalse(self.output.exists())

    def test_rejects_changed_added_or_removed_java_source(self):
        original = self.source.read_bytes()
        extra = self.source.with_name("New.java")
        for mutation in ("change", "add", "remove"):
            with self.subTest(mutation=mutation):
                if mutation == "change":
                    self.source.write_bytes(original + b"// changed\n")
                elif mutation == "add":
                    extra.write_text("package qm; class New {}\n")
                else:
                    self.source.unlink()
                with self.assertRaisesRegex(ValueError, "Java source tree changed"):
                    build.resource_only(self.baseline, self.output)
                self.assertFalse(self.output.exists())
                self.source.write_bytes(original)
                extra.unlink(missing_ok=True)

    def test_rejects_changed_mixin_configuration(self):
        self.write_resource("qm_quest_ui.mixins.json", '{"client": ["OtherMixin"]}\n')
        with self.assertRaisesRegex(ValueError, "cannot change qm_quest_ui.mixins.json"):
            build.resource_only(self.baseline, self.output)
        self.assertFalse(self.output.exists())

    def test_rejects_dependency_edits_and_resource_path_changes(self):
        descriptor = self.resources / "META-INF/neoforge.mods.toml"
        original = descriptor.read_text()
        descriptor.write_text(original + '[[dependencies.qm_quest_ui]]\nmodId="other"\n')
        with self.assertRaisesRegex(ValueError, "only the mod version"):
            build.resource_only(self.baseline, self.output)
        descriptor.write_text(original)
        self.write_resource("extra.json", "{}\n")
        with self.assertRaisesRegex(ValueError, "add or remove resource paths"):
            build.resource_only(self.baseline, self.output)
        (self.resources / "extra.json").unlink()
        (self.resources / "assets/qm_quest_ui/lang/en_us.json").unlink()
        with self.assertRaisesRegex(ValueError, "add or remove resource paths"):
            build.resource_only(self.baseline, self.output)
        self.assertFalse(self.output.exists())

    def test_rejects_class_resource_override_and_baseline_overwrite(self):
        self.write_resource("qm/Example.class", "replacement class")
        with self.assertRaisesRegex(ValueError, "must not contain class files"):
            build.resource_only(self.baseline, self.output)
        self.assertFalse(self.output.exists())
        with self.assertRaisesRegex(ValueError, "baseline separate"):
            build.resource_only(self.baseline, self.baseline)


class ShippedQuestUiResourceTests(unittest.TestCase):
    def test_shipped_classes_equal_the_reviewed_baseline_and_resources_match_sources(self):
        metadata = tomllib.loads((MODULE / "resources/META-INF/neoforge.mods.toml").read_text())
        version = metadata["mods"][0]["version"]
        jars = list((ROOT / "overrides/mods").glob("qm-quest-ui-*.jar"))
        self.assertEqual([p.name for p in jars], [f"qm-quest-ui-{version}.jar"])
        with ZipFile(jars[0]) as archive:
            classes = {name: archive.read(name) for name in archive.namelist() if name.endswith(".class")}
            resources = {name: archive.read(name) for name in archive.namelist() if not name.endswith(".class")}
        self.assertEqual(len(classes), 41)
        self.assertEqual(build.entries_hash(classes), build.RESOURCE_BASELINE_CLASSES_HASH)
        self.assertEqual(build.entries_hash(build.directory_entries(MODULE / "src")), build.RESOURCE_BASELINE_SOURCES_HASH)
        self.assertEqual(resources, build.resource_entries())
        catalogue = json.loads(resources["assets/qm_quest_ui/reward_catalog.json"])
        prizes = {entry["item"] for pool in catalogue["pools"] for entry in pool["entries"]}
        prizes.update(entry["item"] for claim in catalogue["rewards"].values() for entry in claim["guaranteed"])
        self.assertNotIn("millenaire:masa", prizes)
        for item in prizes:
            self.assertNotRegex(item.split(":", 1)[1], r"(?:^|[_/])(backpack|boat|raft)$", item)
        for locale in ("en_us", "uk_ua"):
            language = json.loads(resources[f"assets/qm_quest_ui/lang/{locale}.json"])
            self.assertEqual(language["qm_quest_ui.rewards.draws"].count("%s"), 2)

    def test_resource_cli_rejects_native_verification_flags(self):
        for arguments in (["--verify"], ["--runtime", "/not-used"], ["--javac", "/not-used"]):
            with self.subTest(arguments=arguments):
                result = subprocess.run([sys.executable, str(MODULE / "build.py"),
                                         "--resource-only-from", "/not-used.jar", *arguments],
                                        capture_output=True, text=True, timeout=10)
                self.assertNotEqual(result.returncode, 0)
                self.assertIn("does not compile or run native probes", result.stderr)


if __name__ == "__main__":
    unittest.main()
