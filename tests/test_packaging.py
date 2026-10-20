"""Exercise release boundaries in disposable miniature pack workspaces."""

from contextlib import redirect_stdout
import hashlib
import importlib.util
import io
import json
import os
from pathlib import Path
import shutil
import tempfile
import unittest
from unittest.mock import patch
from zipfile import ZipFile


REPOSITORY = Path(__file__).resolve().parents[1]
SPEC = importlib.util.spec_from_file_location("soqm_packaging_under_test", REPOSITORY / "scripts/soqm.py")
SOQM = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(SOQM)


def write_json(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def fixture(root):
    """Use the real approved small compatibility JARs, never third-party mods."""
    root.mkdir(parents=True, exist_ok=True)
    pack = {
        "name": "SOQM packaging test", "version": "0.1.0-alpha.1", "author": "fixture",
        "minecraft": "1.21.1", "neoforge": "21.1.262", "java_major": 21,
    }
    write_json(root / "soqm.json", pack)
    write_json(root / "metadata/mods.lock.json", {
        "minecraft": "1.21.1", "neoforge": "21.1.262", "mods": [{
            "filename": "test-public-mod.jar", "project_id": 123, "file_id": 456,
            "sha256": "0" * 64, "bytes": 123,
            "source_url": "https://www.curseforge.com/minecraft/mc-mods/example/files/456",
        }],
    })
    authored = json.loads((REPOSITORY / "metadata/authored-mods.json").read_text(encoding="utf-8"))
    write_json(root / "metadata/authored-mods.json", authored)
    for row in authored:
        target = root / "overrides/mods" / row["filename"]
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(REPOSITORY / "overrides/mods" / row["filename"], target)
    write_json(root / "overrides/config/pack.json", {"title": "Тест SOQM"})
    (root / "LICENSE").write_text("Fixture license\n", encoding="utf-8")
    (root / "README.md").write_text("Disposable packaging fixture.\n", encoding="utf-8")
    (root / "scripts").mkdir()
    shutil.copyfile(REPOSITORY / "scripts/audit_storage_upgrade.py", root / "scripts/audit_storage_upgrade.py")
    return root


class ReleasePackagingTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="soqm-package-test-")
        self.addCleanup(self.temporary.cleanup)
        self.root = fixture(Path(self.temporary.name) / "pack-a")
        self.patches = patch.multiple(SOQM, ROOT=self.root, PAYLOAD=self.root / "overrides")
        self.patches.start()
        self.addCleanup(self.patches.stop)
        self.quiet(SOQM.lock)

    def quiet(self, function):
        with redirect_stdout(io.StringIO()):
            return function()

    def test_unsafe_archive_paths_are_rejected_before_writing(self):
        unsafe = ["", "/absolute.json", "../escape.json", "a/../../escape.json",
                  "a/../escape.json", "./relative.json", "a//b.json", "a/./b.json",
                  "a\\b.json", "C:/escape.json", "C:\\escape.json", "a/"]
        for name in unsafe:
            with self.subTest(name=name):
                with self.assertRaises(ValueError):
                    SOQM.safe_name(name)
                with self.assertRaises(ValueError):
                    SOQM.zip_bytes({name: b"untrusted"}, io.BytesIO())
        for name in ["manifest.json", "overrides/config/pack.json", "docs/notes.uk.md"]:
            self.assertEqual(SOQM.safe_name(name), name)

    def test_payload_symlinks_and_case_collisions_are_rejected(self):
        source = self.root / "overrides/config/pack.json"
        link = self.root / "overrides/config/linked.json"
        link.symlink_to(source)
        with self.assertRaisesRegex(ValueError, "symlink"):
            SOQM.payload_hashes()
        link.unlink()
        duplicate = source.with_name("PACK.JSON")
        duplicate.write_text("{}", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "Case-colliding"):
            SOQM.payload_hashes()

    def test_build_rejects_changed_missing_and_unlocked_payload_files(self):
        original = self.root / "overrides/config/pack.json"
        for change in ("changed", "missing", "new"):
            with self.subTest(change=change):
                saved = original.read_bytes()
                extra = original.with_name("unreviewed.json")
                try:
                    if change == "changed":
                        original.write_text('{"title":"changed"}\n', encoding="utf-8")
                    elif change == "missing":
                        original.unlink()
                    else:
                        extra.write_text("{}\n", encoding="utf-8")
                    with self.assertRaisesRegex(ValueError, "Payload changed"):
                        self.quiet(SOQM.build)
                    self.assertFalse(SOQM.package_path().exists())
                finally:
                    original.write_bytes(saved)
                    extra.unlink(missing_ok=True)

    def test_updating_payload_lock_does_not_authorize_a_third_party_jar(self):
        for extension in (".jar", ".JAR"):
            with self.subTest(extension=extension):
                foreign = self.root / "overrides/mods" / ("unapproved-third-party" + extension)
                foreign.write_bytes(b"This file must never enter a release.")
                try:
                    self.quiet(SOQM.lock)
                    with self.assertRaisesRegex(ValueError, "authored JAR"):
                        self.quiet(SOQM.build)
                    self.assertFalse(SOQM.package_path().exists())
                finally:
                    foreign.unlink()
                    self.quiet(SOQM.lock)

    def test_package_verification_does_not_certify_a_stale_payload_lock(self):
        source = self.root / "overrides/config/pack.json"
        source.write_text('{"title":"unreviewed payload"}\n', encoding="utf-8")
        target = SOQM.package_path()
        target.parent.mkdir(parents=True)
        # Simulate an externally assembled archive matching the current files,
        # while the reviewed lock still describes the previous payload.
        SOQM.zip_bytes(SOQM.archive_entries(), target)
        with self.assertRaisesRegex(ValueError, "Payload changed"):
            self.quiet(SOQM.check_package)

    def test_download_verification_rejects_traversal_before_cache_or_network(self):
        lock_path = self.root / "metadata/mods.lock.json"
        lock = json.loads(lock_path.read_text(encoding="utf-8"))
        lock["mods"][0]["filename"] = "../escape.jar"
        write_json(lock_path, lock)
        with patch.object(SOQM, "urlopen", side_effect=AssertionError("Network must not be used")) as network:
            with self.assertRaisesRegex(ValueError, "Unsafe relative path"):
                self.quiet(SOQM.verify_downloads)
        network.assert_not_called()
        self.assertFalse((self.root / ".cache").exists(), "Rejected metadata must not mutate the cache")

    def test_artifact_is_identical_after_relocation_and_metadata_changes(self):
        self.quiet(SOQM.build)
        first = SOQM.package_path().read_bytes()
        second_root = fixture(Path(self.temporary.name) / "different-parent/deep/pack-b")
        for source in (second_root / "overrides").rglob("*"):
            if source.is_file():
                os.utime(source, (1_500_000_000, 1_500_000_000))
                source.chmod(0o755)
        with patch.multiple(SOQM, ROOT=second_root, PAYLOAD=second_root / "overrides"):
            self.quiet(SOQM.lock)
            self.quiet(SOQM.build)
            second = SOQM.package_path().read_bytes()
            self.quiet(SOQM.check_package)
            checksum = (SOQM.package_path().parent / "SHA256SUMS").read_text(encoding="utf-8")
            self.assertEqual(checksum, f"{hashlib.sha256(second).hexdigest()}  {SOQM.package_path().name}\n")
        self.assertEqual(first, second, "Packaging must not depend on checkout path, file mtime or executable bits")
        with ZipFile(io.BytesIO(first)) as archive:
            self.assertIsNone(archive.testzip())
            self.assertEqual(len(archive.namelist()), len(set(archive.namelist())))
            for entry in archive.infolist():
                self.assertEqual(SOQM.safe_name(entry.filename), entry.filename)
                self.assertEqual(entry.date_time, (2026, 1, 1, 0, 0, 0))
                self.assertEqual(entry.create_system, 3)
                self.assertEqual(entry.external_attr >> 16, 0o100644)
                self.assertNotIn(self.temporary.name.encode(), archive.read(entry.filename))
            self.assertEqual(json.loads(archive.read("manifest.json"))["overrides"], "overrides")
            self.assertEqual(archive.read("tools/audit_storage_upgrade.py"),
                             (REPOSITORY / "scripts/audit_storage_upgrade.py").read_bytes())


if __name__ == "__main__":
    unittest.main()
