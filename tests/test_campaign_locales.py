"""Keep split locale sources lossless and deliver FTB Quests' native format."""

from copy import deepcopy
import json
from pathlib import Path
import sys
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_locales as locales


def write_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def fixture(root):
    chapters = []
    for digit, name in (("1", "chapter_a"), ("2", "chapter_b")):
        prefix = digit * 15
        chapter = {
            "filename": name, "id": prefix + "1",
            "images": [{"id": prefix + "5"}],
            "quests": [{"id": prefix + "2", "tasks": [{"id": prefix + "3"}],
                        "rewards": [{"id": prefix + "4"}]}],
        }
        chapters.append(chapter)
        write_json(root / "overrides/config/ftbquests/quests/chapters" / (name + ".snbt"), chapter)
    values = {}
    for language in locales.LANGUAGES:
        locale = {
            "file.0000000000000001.title": "SOQM · §aТекст" if language == "uk_ua" else "SOQM · §aText",
            "chapter_group.3333333333333333.title": "Розділи" if language == "uk_ua" else "Chapters",
        }
        directory = root / "metadata/quest_locales" / language
        write_json(directory / "_global.json", locale)
        for chapter in chapters:
            prefix = chapter["id"][:15]
            entries = {
                f"chapter.{prefix}1.title": chapter["filename"],
                f"quest.{prefix}2.title": "Мегафакел" if language == "uk_ua" else "Mega Torch",
                f"quest.{prefix}2.quest_desc": [
                    "Перший абзац · 64 блоки." if language == "uk_ua" else "First paragraph · 64 blocks.",
                    "§aДругий\nрядок з \"лапками\"." if language == "uk_ua" else "§aSecond\nline with \"quotes\".",
                ],
                f"task.{prefix}3.title": "1 × torchmaster:megatorch",
                f"reward.{prefix}4.title": "Reward",
                f"image.{prefix}5.title": "Caption",
            }
            locale.update(entries)
            write_json(directory / (chapter["filename"] + ".json"), entries)
        values[language] = locale
    return chapters, values


class CampaignLocaleTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="soqm-locales-test-")
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        self.chapters, self.values = fixture(self.root)

    def shard(self, name="chapter_a.json", language="uk_ua"):
        return self.root / "metadata/quest_locales" / language / name

    def test_merge_retains_unicode_paragraph_order_and_every_owner_kind(self):
        runtime = locales.runtime_payload(root=self.root)
        self.assertEqual(set(runtime), {
            "config/ftbquests/quests/lang/uk_ua.snbt",
            "config/ftbquests/quests/lang/en_us.snbt",
        })
        for language in locales.LANGUAGES:
            with self.subTest(language=language):
                self.assertEqual(locales.load(language, root=self.root), self.values[language])
                data = runtime[f"config/ftbquests/quests/lang/{language}.snbt"]
                self.assertEqual(json.loads(data), self.values[language])
                self.assertEqual(data, (json.dumps(self.values[language], sort_keys=True,
                                                  ensure_ascii=False, indent=2) + "\n").encode("utf-8"))
        self.assertFalse((self.root / "overrides/config/ftbquests/quests/lang").exists())

    def test_one_chapter_edit_changes_only_its_source_fragment(self):
        original = self.values["uk_ua"]
        updated = deepcopy(original)
        updated["quest.1111111111111112.quest_desc"].append("Новий абзац.")
        before = locales.expected_outputs(original, "uk_ua", self.chapters, root=self.root)
        after = locales.expected_outputs(updated, "uk_ua", self.chapters, root=self.root)
        self.assertEqual(set(before), set(after))
        self.assertEqual({path.name for path in before if before[path] != after[path]}, {"chapter_a.json"})
        for path, content in after.items():
            path.write_text(content, encoding="utf-8")
        self.assertEqual(locales.load("uk_ua", root=self.root), updated)

    def test_runtime_bytes_do_not_depend_on_source_key_order(self):
        before = locales.runtime_payload(root=self.root)
        for language in locales.LANGUAGES:
            for path in sorted((self.root / "metadata/quest_locales" / language).glob("*.json")):
                value = json.loads(path.read_text(encoding="utf-8"))
                write_json(path, dict(reversed(list(value.items()))))
        self.assertEqual(locales.runtime_payload(root=self.root), before)

    def test_missing_unexpected_and_wrongly_owned_fragments_fail(self):
        source = self.shard()
        original = source.read_bytes()
        source.unlink()
        with self.assertRaises(ValueError):
            locales.load("uk_ua", root=self.root)
        source.write_bytes(original)
        unexpected = self.shard("leftover.json")
        write_json(unexpected, {})
        with self.assertRaises(ValueError):
            locales.load("uk_ua", root=self.root)
        unexpected.unlink()
        owner = json.loads(source.read_text(encoding="utf-8"))
        moved = owner.pop("quest.1111111111111112.title")
        write_json(source, owner)
        other = self.shard("chapter_b.json")
        other_locale = json.loads(other.read_text(encoding="utf-8"))
        other_locale["quest.1111111111111112.title"] = moved
        write_json(other, other_locale)
        with self.assertRaises(ValueError):
            locales.load("uk_ua", root=self.root)

    def test_duplicate_keys_are_rejected_within_and_across_fragments(self):
        source = self.shard()
        original = source.read_bytes()
        source.write_text('{"quest.1111111111111112.title":"first",'
                          '"quest.1111111111111112.title":"second"}\n', encoding="utf-8")
        with self.assertRaises(ValueError):
            locales.load("uk_ua", root=self.root)
        source.write_bytes(original)
        other = self.shard("chapter_b.json")
        values = json.loads(other.read_text(encoding="utf-8"))
        values["quest.1111111111111112.title"] = "Repeated key"
        write_json(other, values)
        with self.assertRaises(ValueError):
            locales.load("uk_ua", root=self.root)

    def test_malformed_keys_values_and_oversized_shards_are_rejected(self):
        source = self.shard()
        original = json.loads(source.read_text(encoding="utf-8"))
        for value in (None, 7, True, {"nested": "object"}, ["text", 7]):
            with self.subTest(value=value):
                invalid = dict(original, **{"quest.1111111111111112.title": value})
                write_json(source, invalid)
                with self.assertRaises(ValueError):
                    locales.load("uk_ua", root=self.root)
        for key in ("quest.invalid.title", "quest.9999999999999999.title", "unknown.1111111111111112.title"):
            with self.subTest(key=key):
                invalid = dict(original)
                invalid[key] = "Unknown owner or key format"
                write_json(source, invalid)
                with self.assertRaises(ValueError):
                    locales.load("uk_ua", root=self.root)
        invalid = deepcopy(self.values["uk_ua"])
        invalid["quest.1111111111111112.quest_desc"] = ["x" * (200 * 1024)]
        with self.assertRaises(ValueError):
            locales.expected_outputs(invalid, "uk_ua", self.chapters, root=self.root)

    def test_language_key_mismatch_is_not_silently_packaged(self):
        source = self.shard(language="en_us")
        value = json.loads(source.read_text(encoding="utf-8"))
        value.pop("quest.1111111111111112.title")
        write_json(source, value)
        with self.assertRaises(ValueError):
            locales.runtime_payload(root=self.root)

    def test_symlinked_source_file_or_language_directory_is_rejected(self):
        source = self.shard()
        saved = source.with_suffix(".saved")
        source.rename(saved)
        source.symlink_to(saved)
        with self.assertRaises(ValueError):
            locales.load("uk_ua", root=self.root)
        source.unlink()
        saved.rename(source)
        directory = source.parent
        elsewhere = self.root / "linked-uk-locale"
        directory.rename(elsewhere)
        directory.symlink_to(elsewhere, target_is_directory=True)
        with self.assertRaises(ValueError):
            locales.load("uk_ua", root=self.root)

    def test_repository_sources_are_complete_bounded_chapter_files(self):
        chapters = sorted((ROOT / "overrides/config/ftbquests/quests/chapters").glob("*.snbt"))
        expected = {"_global.json", *(path.stem + ".json" for path in chapters)}
        merged = {}
        for language in locales.LANGUAGES:
            paths = list((ROOT / "metadata/quest_locales" / language).glob("*.json"))
            self.assertEqual({path.name for path in paths}, expected)
            for path in paths:
                self.assertLessEqual(path.stat().st_size, 200 * 1024, path.name)
            merged[language] = locales.load(language, root=ROOT)
        self.assertEqual(set(merged["uk_ua"]), set(merged["en_us"]))
        for name, data in locales.runtime_payload(root=ROOT).items():
            language = Path(name).stem
            self.assertEqual(json.loads(data), merged[language])


if __name__ == "__main__":
    unittest.main()
