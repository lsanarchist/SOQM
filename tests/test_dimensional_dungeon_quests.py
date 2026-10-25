"""Protect optional dungeon goals, personal prizes and the pre-existing save contract."""

from copy import deepcopy
import hashlib
import json
from pathlib import Path
import sys
import unittest
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_compatibility as compatibility
import campaign_layout as layout
import campaign_progression as progression
import campaign_refresh as refresh

QUEST_IDS = {f"514E11{number:010X}" for number in range(1, 13)}
DD_JAR = ROOT / ".cache/mods/dimdungeons-210-neoforge-1.21.1.jar"
ADVANCEMENTS = {
    4: "activate_basic_key", 5: "enter_basic_dungeon",
    7: "find_key_charger", 8: "find_bell_1", 9: "find_advanced_key",
    10: "find_crown", 11: "enter_advanced_dungeon", 12: "all_trophies",
}


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


class DimensionalDungeonQuestTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        directory = ROOT / "overrides/config/ftbquests/quests"
        cls.chapters = [read(path) for path in sorted((directory / "chapters").glob("*.snbt"))]
        cls.groups = read(directory / "chapter_groups.snbt")["chapter_groups"]
        cls.quests = {q["id"]: q for chapter in cls.chapters for q in chapter["quests"]}
        cls.branch = {ident: cls.quests[ident] for ident in QUEST_IDS}

    def quest(self, number):
        return self.branch[f"514E11{number:010X}"]

    def test_dungeon_chain_is_additive_optional_and_never_gates_existing_progression(self):
        owners = {chapter["filename"] for chapter in self.chapters
                  if any(q["id"] in QUEST_IDS for q in chapter["quests"])}
        self.assertEqual(owners, {"qm_07_exploration"})
        self.assertEqual(self.quest(1)["dependencies"], ["514D010000000002"])
        self.assertEqual(set(self.quest(11)["dependencies"]),
                         {self.quest(9)["id"], self.quest(10)["id"]})
        for ident, quest in self.quests.items():
            if ident in QUEST_IDS:
                self.assertTrue(quest["optional"])
                self.assertFalse(quest.get("can_repeat", False))
            else:
                self.assertFalse(QUEST_IDS.intersection(quest.get("dependencies", [])), ident)
        self.assertFalse(QUEST_IDS.intersection(progression._ancestors(self.quests, layout.FINAL)))

    def test_inventory_goals_keep_items_and_identify_the_correct_book(self):
        items = [task for quest in self.branch.values() for task in quest["tasks"]
                 if task["type"] == "item"]
        self.assertEqual(len(items), 5)
        for task in items:
            self.assertFalse(task["consume_items"])
            self.assertFalse(task.get("only_from_crafting", False))
        book = self.quest(1)["tasks"][0]
        self.assertEqual(book["item"]["id"], "patchouli:guide_book")
        self.assertEqual(book["item"]["components"], {"patchouli:book": "dimdungeons:guide_book"})
        self.assertEqual(book["match_components"], "fuzzy")
        components = {task["item"]["id"]: task["count"] for task in self.quest(2)["tasks"]}
        self.assertEqual(components, {"dimdungeons:block_portal_keyhole": 1,
                                      "dimdungeons:block_gilded_portal": 2})
        key = self.quest(3)["tasks"][0]
        self.assertEqual(key["item"]["id"], "dimdungeons:item_portal_key")
        self.assertEqual(key["match_components"], "none",
                         "A key goal must not bind players to one random dungeon address")
        self.assertEqual(self.quest(6)["tasks"][0]["item"]["id"], "dimdungeons:item_homeward_pearl")

    def test_activation_entry_and_collection_require_whole_native_advancements(self):
        self.assertEqual(sum(len(quest["tasks"]) for quest in self.branch.values()), 13)
        for number, name in ADVANCEMENTS.items():
            with self.subTest(goal=name):
                tasks = self.quest(number)["tasks"]
                self.assertEqual(len(tasks), 1)
                self.assertEqual(tasks[0]["type"], "advancement")
                self.assertEqual(tasks[0]["advancement"], "dimdungeons:dungeons/" + name)
                self.assertEqual(tasks[0]["criterion"], "",
                                 "One criterion must not complete an entire collection")

    def test_each_personal_expedition_claim_has_the_same_server_and_client_preview(self):
        policy = read(ROOT / "overrides/kubejs/config/soqm_rewards.json")
        preview = read(ROOT / "src/compat/quest-ui/resources/assets/qm_quest_ui/reward_catalog.json")
        for number in range(1, 13):
            quest = self.quest(number)
            self.assertEqual(len(quest["rewards"]), 1)
            reward = quest["rewards"][0]
            ident = f"514E13{number:010X}"
            self.assertEqual(reward["id"], ident)
            self.assertEqual(reward["type"], "custom")
            self.assertFalse(reward["team_reward"])
            self.assertEqual(reward["auto"], "disabled")
            self.assertEqual(policy["reward_mappings"][ident],
                             {"pool": "expedition", "quest_id": quest["id"]})
            entry = preview["rewards"][ident]
            self.assertEqual(entry["quest_id"], quest["id"])
            self.assertEqual(entry["random_pool"], "expedition")
            self.assertEqual(entry["guaranteed"], [])
            self.assertIsNone(entry["requires_manufacture"])

    def test_compatibility_boundary_rejects_old_progress_or_claim_changes(self):
        previous = compatibility.previous_dimensional_dungeons(self.chapters)
        self.assertEqual(sum(len(chapter["quests"]) for chapter in previous), 1642)
        self.assertEqual(refresh.persistent_identity(previous, self.groups),
                         "609df13b427602821a5e8dfd0577f6df352b16248b608806bc8f7710e743cd87")
        original = next(q for chapter in previous for q in chapter["quests"]
                        if q["tasks"] and q["tasks"][0]["type"] == "item")
        for mutation in ("old_task", "old_claim", "old_dependency", "new_claim"):
            changed = deepcopy(self.chapters)
            quests = {q["id"]: q for chapter in changed for q in chapter["quests"]}
            quest = quests[original["id"]]
            if mutation == "old_task":
                quest["tasks"][0]["count"] += 1
            elif mutation == "old_claim":
                quest["rewards"][0]["team_reward"] = True
            elif mutation == "old_dependency":
                quest.setdefault("dependencies", []).append(self.quest(1)["id"])
            else:
                quests[self.quest(1)["id"]]["rewards"][0]["team_reward"] = True
            with self.subTest(mutation=mutation), self.assertRaises(ValueError):
                compatibility.previous_dimensional_dungeons(changed)

    @unittest.skipUnless(DD_JAR.is_file(), "Pinned DD archive is not in the local dependency cache")
    def test_pinned_advancement_conditions_support_the_authored_goals(self):
        self.assertEqual(hashlib.sha256(DD_JAR.read_bytes()).hexdigest(),
                         "8cc55efc96ad15ec4b9ab7ea649f8a281f19b527e3692c17cf912dac91f94f41")
        with ZipFile(DD_JAR) as archive:
            # Upstream display text contains literal newlines accepted by Gson.
            goals = {name: json.loads(archive.read(f"data/dimdungeons/advancement/dungeons/{name}.json"),
                                     strict=False) for name in ADVANCEMENTS.values()}
        crown = goals["find_crown"]["criteria"]["crown"]["conditions"]["items"][0]
        self.assertEqual(crown["count"]["min"], 2)
        charger = goals["find_key_charger"]
        self.assertEqual(charger["requirements"], [["charger_1", "charger_2", "charger_3"]])
        trophies = goals["all_trophies"]
        self.assertEqual(len(trophies["criteria"]), 8)
        self.assertEqual(trophies.get("requirements", [[key] for key in trophies["criteria"]]),
                         [[key] for key in trophies["criteria"]])
        self.assertEqual(goals["enter_basic_dungeon"]["criteria"]["entered_dungeon"]["trigger"],
                         "minecraft:changed_dimension")
        self.assertEqual(goals["enter_advanced_dungeon"]["criteria"]["advanced_dungeon"]["trigger"],
                         "minecraft:impossible")


if __name__ == "__main__":
    unittest.main()
