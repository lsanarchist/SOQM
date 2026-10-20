"""World generation must not turn an optional visit into a campaign gate."""

from collections import defaultdict
from copy import deepcopy
import json
from pathlib import Path
import sys
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_layout as layout
import campaign_progression as progression
import campaign_refresh as refresh
import localize_english

QUESTS = ROOT / "overrides/config/ftbquests/quests"

# Verified against the pinned mods' native data and Millénaire descriptions.
# Other dimensions and real boss progression deliberately remain outside this
# policy. Warped Greatsword is an OVERWORLD structure despite its name.
OTHER_DIMENSION_STRUCTURES = {
    "structory_towers:nether/fortress_tower",
    "structory_towers:nether/warped_outpost",
    "structory_towers:nether/strange_outpost",
    "structory_towers:end/end_tower",
}
NATIVE_OVERWORLD_VISITS = {
    "modernfoundry:world/sky_island",
    "millenaire:firstcontact",
    "millenaire:explorer",
    "millenaire:marcopolo",
    "millenaire:magellan",
}


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def is_overworld_visit(quest):
    for task in quest["tasks"]:
        if task["type"] == "structure" and task["structure"] not in OTHER_DIMENSION_STRUCTURES:
            return True
        advancement = task.get("advancement", "")
        if (advancement in NATIVE_OVERWORLD_VISITS
                or advancement.startswith("nova_structures:adventure/find_")
                or advancement.startswith("qm:campaign_v040/biomes/")):
            return True
    return False


def reachable_without(quests, unavailable):
    """Complete every available objective with all its actual prerequisites.

    Optional flags do not remove an explicitly listed FTB dependency. Excluding
    the unavailable objectives models a save where those discoveries never
    occur, rather than merely checking the intended visual role of their nodes.
    """
    completed = set()
    while True:
        ready = {ident for ident, quest in quests.items()
                 if ident not in completed and ident not in unavailable
                 and set(quest.get("dependencies", [])) <= completed}
        if not ready:
            return completed
        completed.update(ready)


class OptionalExplorationTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.chapters = [read(path) for path in sorted((QUESTS / "chapters").glob("*.snbt"))]
        cls.quests = {q["id"]: q for c in cls.chapters for q in c["quests"]}
        cls.visits = {ident for ident, quest in cls.quests.items() if is_overworld_visit(quest)}

    def test_all_overworld_visits_are_rewarded_optional_leaves(self):
        children = defaultdict(set)
        for ident, quest in self.quests.items():
            for dependency in quest.get("dependencies", []):
                children[dependency].add(ident)
        self.assertGreaterEqual(len(self.visits), 67, "Keep the full audited visit coverage")
        for ident in self.visits:
            with self.subTest(visit=ident):
                self.assertTrue(self.quests[ident].get("optional", False))
                self.assertTrue(self.quests[ident]["rewards"], "Exploration keeps its existing reward")
                self.assertEqual(children[ident], set(), "No later quest may require this random location")

    def test_campaign_can_progress_with_every_overworld_visit_unavailable(self):
        reachable = reachable_without(self.quests, self.visits)
        self.assertEqual(reachable, set(self.quests) - self.visits)
        self.assertIn(layout.FINAL, reachable)
        self.assertTrue(set(layout.GATES) <= reachable)

    def test_preparation_does_not_require_trading_or_opening_a_random_cube(self):
        unavailable = self.visits | {
            "514D11E0ED42C1FF",  # vanilla villager trade
            "514D010000000007",  # Millénaire trade
            "514D010000000008",  # Millénaire supply
            "514D113C4B748351",  # Chance Cube item
            "514D1118E4523B29",  # opening a Chance Cube
        }
        reachable = reachable_without(self.quests, unavailable)
        for ident, quest in self.quests.items():
            if not quest.get("optional", False):
                self.assertIn(ident, reachable, "Optional travel activities must not block required work")
        for ident in ("514D11A7ED9AA3BB", "514D118E5C7AB43A", "514D01000000000C"):
            self.assertIn(ident, reachable)
            self.assertIn(progression.CHASSIS, progression._ancestors(self.quests, ident))
        trade = progression._ancestors(self.quests, "514D11E0ED42C1FF")
        self.assertIn("514D111AFD4E2789", trade)
        self.assertFalse(trade & self.visits)

    def test_world_visit_policy_does_not_bypass_materials_or_bosses(self):
        twilight_lich = progression._ancestors(self.quests, "513011FC22300C21")
        self.assertIn("513011F638E9FBA0", twilight_lich)  # Naga
        self.assertIn("513011BD18533AD4", twilight_lich)  # enter Twilight Forest
        self.assertIn(progression.PRECISION, twilight_lich)
        moon = progression._ancestors(self.quests, progression.MOON)
        self.assertIn("513011ABD424EA8F", moon)  # first rocket
        self.assertIn("5130114FE09DA6CE", moon)  # spacesuit
        self.assertIn("5130115956283BA9", moon)  # oxygen preparation
        for previous, current in zip(layout.GATES, layout.GATES[1:]):
            self.assertIn(previous, progression._ancestors(self.quests, current))

    def test_regeneration_repairs_old_visit_edges_without_replacing_objectives(self):
        chapters = deepcopy(self.chapters)
        quests = {q["id"]: q for c in chapters for q in c["quests"]}
        quests["514D11A7ED9AA3BB"]["dependencies"] = [
            "514D11628A7DD0E1", "514D11CDA1129F03", progression.CHASSIS,
        ]
        quests["514D11E0ED42C1FF"]["dependencies"] = ["514D11CDA1129F03"]
        quests["514D01000000000C"]["dependencies"].append("514D010000000008")
        progression.apply(chapters, read(QUESTS / "lang/uk_ua.snbt"))
        self.assertTrue(set(self.quests) - self.visits <= reachable_without(quests, self.visits))
        for ident in ("514D11A7ED9AA3BB", "514D11E0ED42C1FF", "514D01000000000C"):
            self.assertEqual(quests[ident]["tasks"], self.quests[ident]["tasks"])
            self.assertEqual(quests[ident]["rewards"], self.quests[ident]["rewards"])
            self.assertEqual(quests[ident]["dependencies"], self.quests[ident]["dependencies"])

    def test_summary_task_labels_do_not_restore_obsolete_visit_requirements(self):
        """A correct quest description must not hide a contradictory task label."""
        summaries = {
            "514D11A7ED9AA3BB": {
                "task": "514D12480266404E",
                "dependencies": {"514D111AFD4E2789", progression.CHASSIS},
                "obsolete": "Автоматично: міст, підземне село та майстерня відкриті",
                "uk_ua": "Автоматично: спорядження й майстерня готові",
                "en_us": "Automatic: Equipment and Workshop Ready",
            },
            "514D01000000000C": {
                "task": "514D02000000000F",
                "dependencies": {"514D010000000006", "514D01000000000A", "514D01000000000B"},
                "obsolete": "Автоматично: чотири вступні напрямки завершено",
                "uk_ua": "Автоматично: три вступні напрямки завершено",
                "en_us": "Automatic: Ready for Greater Expeditions",
            },
        }
        published = {language: read(QUESTS / "lang" / (language + ".snbt"))
                     for language in ("uk_ua", "en_us")}
        repaired = deepcopy(published["uk_ua"])
        for ident, row in summaries.items():
            self.assertEqual(set(self.quests[ident]["dependencies"]), row["dependencies"])
            self.assertIn(row["task"], {task["id"] for task in self.quests[ident]["tasks"]})
            repaired[f"task.{row['task']}.title"] = row["obsolete"]
        refresh.refresh(deepcopy(self.chapters), repaired, read(QUESTS / "chapter_groups.snbt")["chapter_groups"])
        regenerated_english = json.loads(localize_english.generate()[0])
        for row in summaries.values():
            key = f"task.{row['task']}.title"
            self.assertEqual(repaired[key], row["uk_ua"], "Regeneration must repair stale Ukrainian instructions")
            self.assertEqual(regenerated_english[key], row["en_us"])
            for language, locale in published.items():
                self.assertEqual(locale[key], row[language], "Published labels must agree with the current route")


if __name__ == "__main__":
    unittest.main()
