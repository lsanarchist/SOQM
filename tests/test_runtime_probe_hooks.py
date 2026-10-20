"""Keep the 0.4.8 reward fixtures and performance probe on one staging harness."""

from contextlib import redirect_stdout
import importlib.util
import io
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import time
from types import SimpleNamespace
import unittest
from unittest.mock import Mock, patch
import zipfile


REPOSITORY = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(REPOSITORY / "scripts"))
import smoke_server


class CompilerReached(Exception):
    """Stop after real staging/preflight, before a JVM or server can be launched."""


class RuntimeProbeHooksTests(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory(prefix="soqm-probe-hooks-")
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        self.runtime = self.root / "installed-server"
        self.java = self.root / "jdk/bin/java"
        self.native = self.runtime / "libraries/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-server.jar"
        self.write(self.java, "test executable path")
        self.write(self.java.with_name("javac"), "test compiler path")
        self.write_jar(self.native, {"fixture.txt": b"test native runtime pin"})
        self.write(self.native.with_name("unix_args.txt"), "test runtime arguments")
        self.write(self.root / "soqm.json", {
            "minecraft": "1.21.1", "neoforge": "21.1.262", "java_major": 21, "version": "0.4.8",
        })
        self.authored = self.root / "overrides/mods/test-progressive-rewards.jar"
        self.embedded_bytes = io.BytesIO()
        with zipfile.ZipFile(self.embedded_bytes, "w") as nested:
            nested.writestr("fixture-api.txt", "test embedded API")
        self.write_jar(self.authored, {
            "META-INF/jarjar/metadata.json": json.dumps({"jars": [{
                "path": "META-INF/jarjar/fixture-api.jar"
            }]}).encode(),
            "META-INF/jarjar/fixture-api.jar": self.embedded_bytes.getvalue(),
        })
        self.write(self.root / "metadata/authored-mods.json", [{
            "filename": self.authored.name, "sha256": smoke_server.sha(self.authored),
        }])
        self.write(self.root / "metadata/mods.lock.json", {"mods": []})
        self.write(self.root / "metadata/payload.sha256.json", smoke_server.hashes(self.root / "overrides"))
        self.evidence = self.root / "metadata/reward_item_evidence.json"
        self.write(self.evidence, {"test_recipe_evidence": ["minecraft:crafting"]})
        self.default_source = self.root / "tests/runtime/SoqmRuntimeProbe.java"
        self.reward_source = self.default_source.with_name("ProgressiveRewardsProbe.java")
        self.custom_source = self.default_source.parent / "performance/PerformanceProbe.java"
        for source in (self.default_source, self.reward_source, self.custom_source):
            self.write(source, "// fixture source: " + source.name)

        # Load independently so the public default arguments are established
        # against this disposable root, exactly as on ordinary script import.
        spec = importlib.util.spec_from_file_location("runtime_probe_under_test", REPOSITORY / "scripts/runtime_probe.py")
        self.probe = importlib.util.module_from_spec(spec)
        with patch.multiple(smoke_server, ROOT=self.root, NATIVE_SHA256=smoke_server.sha(self.native)):
            spec.loader.exec_module(self.probe)
        self.commands = []
        self.baseline_path = "overrides/config/ftbquests/quests/chapters/saved.snbt"
        self.baseline_bytes = b'{"quests":[{"id":"0123456789ABCDEF"}]}\n'
        self.baseline_commit = "a" * 40
        self.args = SimpleNamespace(accept_eula=True, java=str(self.java), runtime=str(self.runtime),
                                    allow_unlocked_payload=False, port=25591, baseline_ref="before-upgrade")
        for replacement in (
            patch.object(self.probe.shutil, "which", return_value=str(self.java)),
            patch.object(self.probe.socket, "socket"),
            patch.object(self.probe.subprocess, "run", side_effect=self.command),
            patch.object(self.probe.subprocess, "Popen", side_effect=AssertionError("No server may start in this test")),
        ):
            replacement.start()
            self.addCleanup(replacement.stop)

    def write(self, path, contents):
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(json.dumps(contents) if isinstance(contents, (dict, list)) else contents, encoding="utf-8")

    def write_jar(self, path, entries):
        path.parent.mkdir(parents=True, exist_ok=True)
        with zipfile.ZipFile(path, "w") as archive:
            for name, content in entries.items():
                archive.writestr(name, content)

    def command(self, command, **kwargs):
        self.commands.append(command)
        if command == [str(self.java), "-version"]:
            return subprocess.CompletedProcess(command, 0, "", 'openjdk version "21.0.12"\n')
        if command[:3] == ["git", "rev-parse", "--verify"]:
            return subprocess.CompletedProcess(command, 0, self.baseline_commit + "\n", "")
        if command[:3] == ["git", "ls-tree", "-r"]:
            return subprocess.CompletedProcess(command, 0, self.baseline_path + "\n", "")
        if command[:2] == ["git", "show"]:
            return subprocess.CompletedProcess(command, 0, self.baseline_bytes, b"")
        if command[0] == str(self.java.with_name("javac")):
            raise CompilerReached
        self.fail(f"Unexpected external command: {command}")

    def invoke(self, report, **hooks):
        with redirect_stdout(io.StringIO()):
            self.probe.run(self.args, report, time.monotonic() + 120, **hooks)

    def test_performance_hooks_select_compiler_workspace_and_source_provenance(self):
        compiler = Mock(side_effect=CompilerReached)
        report = {}
        with self.assertRaises(CompilerReached):
            self.invoke(report, compile_fn=compiler, probe_source=self.custom_source, workspace_kind="performance-probe")
        workspace = self.root / report["workspace"]
        self.assertEqual(workspace.parent, self.root / ".work/performance-probe")
        compiler.assert_called_once()
        self.assertEqual(compiler.call_args.args[:3], (str(self.java), self.runtime, workspace))
        self.assertEqual(report["probe_source_sha256"], smoke_server.sha(self.custom_source))
        self.assertEqual(report["probe_sources_sha256"], smoke_server.hashes(self.custom_source.parent))
        self.assertEqual(report["pack_version"], "0.4.8")
        self.assertEqual(report["baseline_commit"], self.baseline_commit)
        self.assertEqual((workspace / "baseline-quests/chapters/saved.snbt").read_bytes(), self.baseline_bytes)
        self.assertEqual(report["baseline_quests_sha256"], smoke_server.hashes(workspace / "baseline-quests"))
        self.assertEqual(report["reward_recipe_evidence_sha256"], smoke_server.sha(self.evidence))
        self.assertEqual((workspace / "server/soqm_probe_recipe_evidence.json").read_bytes(), self.evidence.read_bytes())
        self.assertTrue(report["reviewed_payload_match"])
        self.assertFalse(any(command[0] == str(self.java.with_name("javac")) for command in self.commands))

    def test_default_invocation_preserves_progressive_reward_sources_and_authored_classpath(self):
        report = {}
        with self.assertRaises(CompilerReached):
            self.invoke(report)
        command = self.commands[-1]
        self.assertEqual(command[0], str(self.java.with_name("javac")))
        self.assertEqual({arg for arg in command if arg.endswith(".java")},
                         {str(self.default_source), str(self.reward_source)})
        self.assertIn(str(self.authored), command[command.index("-cp") + 1].split(":"))
        embedded = self.root / report["workspace"] / "embedded-api-jars"
        extracted = list(embedded.glob("*-fixture-api.jar"))
        self.assertEqual(len(extracted), 1)
        self.assertEqual(extracted[0].read_bytes(), self.embedded_bytes.getvalue())
        self.assertIn(str(extracted[0]), command[command.index("-cp") + 1].split(":"))
        self.assertEqual(list((self.root / report["workspace"] / "server/mods").glob("*")), [])
        self.assertEqual((self.root / report["workspace"]).parent, self.root / ".work/runtime-probe")
        self.assertEqual(report["probe_source_sha256"], smoke_server.sha(self.default_source))
        self.assertEqual(report["probe_sources_sha256"], smoke_server.hashes(self.default_source.parent))

    def test_custom_hook_keeps_baseline_and_payload_preflight_gates(self):
        compiler = Mock(side_effect=CompilerReached)
        self.args.baseline_ref = "--unsafe-option"
        with self.assertRaisesRegex(smoke_server.SmokeFailure, "valid baseline"):
            self.invoke({}, compile_fn=compiler, probe_source=self.custom_source)
        compiler.assert_not_called()
        self.args.baseline_ref = "before-upgrade"
        self.write(self.authored, "changed after review")
        with self.assertRaisesRegex(smoke_server.SmokeFailure, "reviewed lock"):
            self.invoke({}, compile_fn=compiler, probe_source=self.custom_source)
        compiler.assert_not_called()


if __name__ == "__main__":
    unittest.main()
