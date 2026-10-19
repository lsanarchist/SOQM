"""Editorial overlays must survive generation without gaining mechanical scope."""

import copy
import json
from pathlib import Path
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_lessons as lessons


class TutorialExpansionTests(unittest.TestCase):
    def setUp(self):
        self.ident = "5130110000000001"
        self.lesson = {
            "description": {"uk_ua": ["Український урок."], "en_us": ["English lesson."]},
            "sources": ["metadata/campaign_route.json"],
        }

    def write_source(self, directory, name, data):
        path = Path(directory) / name
        path.write_text(json.dumps({"schema": 1, "lessons": data}), encoding="utf-8")

    def test_editorial_source_rejects_duplicate_owners_missing_languages_and_mechanics(self):
        with tempfile.TemporaryDirectory() as directory:
            self.write_source(directory, "first.json", {self.ident: self.lesson})
            self.write_source(directory, "second.json", {self.ident: self.lesson})
            with self.assertRaisesRegex(ValueError, "Duplicate lesson owner"):
                lessons.load_lessons(Path(directory))
            (Path(directory) / "second.json").unlink()
            bad = copy.deepcopy(self.lesson)
            del bad["description"]["en_us"]
            self.write_source(directory, "first.json", {self.ident: bad})
            with self.assertRaisesRegex(ValueError, "both supported languages"):
                lessons.load_lessons(Path(directory))
            bad = copy.deepcopy(self.lesson)
            bad["dependencies"] = ["5130110000000002"]
            self.write_source(directory, "first.json", {self.ident: bad})
            with self.assertRaisesRegex(ValueError, "only supply descriptions"):
                lessons.load_lessons(Path(directory))
            (Path(directory) / "first.json").write_text(
                '{"schema": 1, "lessons": {}, "lessons": {}}', encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "Duplicate lesson source key"):
                lessons.load_lessons(Path(directory))

    def test_overlay_changes_only_descriptions_and_rejects_unknown_quests_before_writing(self):
        locale = {f"quest.{self.ident}.title": "Retained title",
                  f"quest.{self.ident}.quest_desc": ["Old description"],
                  "task.5130120000000001.title": "Retained objective",
                  "reward.title": "Retained reward"}
        original = copy.deepcopy(locale)
        bad = {self.ident: self.lesson, "5130110000000002": self.lesson}
        with self.assertRaisesRegex(ValueError, "unknown quests"):
            lessons.apply_locale(locale, "en_us", bad)
        self.assertEqual(original, locale)
        lessons.apply_locale(locale, "en_us", {self.ident: self.lesson})
        original[f"quest.{self.ident}.quest_desc"] = ["English lesson."]
        self.assertEqual(original, locale)
        lessons.apply_locale(locale, "en_us", {self.ident: self.lesson})
        self.assertEqual(original, locale)

    def test_reviewed_lessons_reference_existing_quests_and_available_evidence(self):
        authored = lessons.load_lessons()
        for language in sorted(lessons.LANGUAGES):
            locale = json.loads((ROOT / "overrides/config/ftbquests/quests/lang" /
                                 f"{language}.snbt").read_text(encoding="utf-8"))
            lessons.apply_locale(locale, language, authored)
        for ident, lesson in authored.items():
            for source in lesson["sources"]:
                if source.startswith("https://"):
                    continue
                path = source.split("#", 1)[0]
                self.assertTrue((ROOT / path).is_file(), f"Missing lesson evidence: {ident}: {source}")


if __name__ == "__main__":
    unittest.main()
