"""Player-facing route, save-contract and layout regressions for production lines."""

import hashlib
import json
import math
from pathlib import Path
import re
import sys
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_branches as branches
import campaign_compatibility as compatibility
import campaign_multiblocks as multiblocks
import campaign_layout as layout
import campaign_progression as progression
import campaign_refunds as refunds


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


class ProductionQuestBranchTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.directory = ROOT / "overrides/config/ftbquests/quests"
        cls.chapters = [read(p) for p in sorted((cls.directory / "chapters").glob("*.snbt"))]
        cls.by_filename = {c["filename"]: c for c in cls.chapters}
        cls.quests = {q["id"]: q for c in cls.chapters for q in c["quests"]}
        cls.source = branches.sources()
        cls.blueprint = layout.blueprint()
        cls.locales = {language: read(cls.directory / "lang" / (language + ".snbt")) for language in ("uk_ua", "en_us")}

    def test_existing_objectives_and_personal_rewards_are_unchanged(self):
        # One additive quest must not replace the original 0.4.9 save contract.
        additions = read(ROOT / "metadata/campaign_identity.json")["previous_content"]["added_quests"]
        self.assertEqual(additions, ["514A110000000001"])
        previous = compatibility.previous_questbook(self.chapters)
        contract = {q["id"]: {"tasks": q["tasks"], "rewards": q["rewards"], "can_repeat": q.get("can_repeat", False)}
                    for chapter in previous for q in chapter['quests'] if q["id"] not in additions}
        expected = read(ROOT / "metadata/quest_branch_identity.json")
        self.assertEqual(len(contract), expected["quests"])
        actual = hashlib.sha256(json.dumps(contract, sort_keys=True, separators=(",", ":")).encode()).hexdigest()
        self.assertEqual(expected["base_commit"], "abd73f1c0736247fc5b13e0e255f8a4ef6d71b34")
        self.assertEqual(actual, expected["sha256"], "A branch edit must not change an objective or reissue a personal reward")

    def test_cardboard_box_is_an_optional_sawmill_lesson_with_a_personal_reward(self):
        box = self.quests["514A110000000001"]
        self.assertIn(box, self.by_filename["qm_02_mek"]["quests"])
        self.assertEqual(box["icon"]["id"], "mekanism:cardboard_box")
        self.assertEqual(box["dependencies"], ["513011B3BB1DF1D9"])
        self.assertTrue(box["optional"])
        self.assertEqual(progression.quest_stage(box), 2)
        self.assertEqual(len(box["tasks"]), 1)
        task = box["tasks"][0]
        self.assertEqual(task["type"], "item")
        self.assertEqual(task["item"]["id"], "mekanism:cardboard_box")
        self.assertEqual(task["count"], 1)
        self.assertFalse(task["consume_items"])
        self.assertEqual(len(box["rewards"]), 1)
        self.assertFalse(box["rewards"][0]["team_reward"])
        self.assertIn("soqm:reward_pool:engineering", box["rewards"][0]["tags"])
        self.assertNotIn(box["id"], progression._ancestors(self.quests, layout.FINAL))
        self.assertNotIn("513011B3BB1DF1D9", progression._ancestors(self.quests, "513011156F29CC4E"),
                         "The PRC consumes Bio Fuel; its first chemistry route must not require sawdust production")

    def test_documented_routes_are_connected_in_the_actual_quest_graph(self):
        self.assertGreaterEqual(len(self.source["routes"]), 8)
        for route in self.source["routes"]:
            for previous, current in zip(route["quests"], route["quests"][1:]):
                with self.subTest(route=route["key"], previous=previous, current=current):
                    self.assertIn(previous, progression._ancestors(self.quests, current))

    def test_first_me_storage_and_autocrafting_remain_before_matrix(self):
        primitive = (
            "51301145AC657CE8",  # one-cell ME chest
            "513011AAD4652E6F",  # actual terminal craft
            "513011562F81FD24",  # pattern provider
            "5130112D00658B23",  # molecular assembler
            "513011DEEBB429E9",  # first 1k crafting storage
            "51301199219AE3FE",  # RFTools Crafter T1 ingredient
            "5130119DF4466A66",  # Ender IO Crafter ingredient
        )
        for ident in primitive:
            with self.subTest(quest=ident):
                ancestors = progression._ancestors(self.quests, ident)
                self.assertIn(progression.PRECISION, ancestors)
                self.assertNotIn(progression.MATRIX, ancestors)
                self.assertEqual(progression.quest_stage(self.quests[ident]), 2)
        terminal = progression._ancestors(self.quests, "513011AAD4652E6F")
        self.assertIn("51301145AC657CE8", terminal)
        self.assertNotIn("5130119BD717861F", terminal, "The controller is not needed by a primitive ME network")
        assembler = progression._ancestors(self.quests, "5130112D00658B23")
        self.assertIn("51301199219AE3FE", assembler)
        self.assertIn("5130119DF4466A66", progression._ancestors(self.quests, "513011DEEBB429E9"))

    def test_mechanical_sifting_does_not_require_every_mesh_or_diamond_hammer(self):
        chapter = self.by_filename["qm_v040_29_exdeorum"]
        by_item = {task["item"]["id"]: q["id"] for q in chapter["quests"] for task in q["tasks"] if task.get("type") == "item" and isinstance(task.get("item"), dict)}
        sieve = progression._ancestors(self.quests, "5140118233B3CC83")
        hammer = progression._ancestors(self.quests, "514011CA86FD67E8")
        self.assertNotIn(by_item["exdeorum:diamond_mesh"], sieve)
        self.assertNotIn(by_item["exdeorum:diamond_hammer"], hammer)
        chunks = progression._ancestors(self.quests, "514011D7E21C7052")
        self.assertIn("514011597C161302", chunks, "String Mesh can produce the first iron chunks")
        self.assertNotIn(by_item["exdeorum:iron_mesh"], chunks)

    def test_flux_upgrades_and_me_return_follow_their_actual_devices(self):
        flux = self.by_filename["qm_v040_30_exmachinis"]
        # The feed-line kit repeats an Item Buffer objective. Select the
        # dedicated one-item device lesson, not that later multi-part project.
        by_item = {task["item"]["id"]: q["id"] for q in flux["quests"] if len(q["tasks"]) == 1
                   for task in q["tasks"] if task.get("type") == "item" and isinstance(task.get("item"), dict)}
        comparator = progression._ancestors(self.quests, by_item["exmachinis:comparator_upgrade"])
        self.assertIn(by_item["exmachinis:item_buffer"], comparator)
        self.assertNotIn(by_item["exmachinis:flux_compactor"], comparator)
        self.assertIn("514011F45FF8C855", progression._ancestors(self.quests, "514011ADCD0AC819"))

    def test_flow_panels_show_the_authored_order_and_every_existing_node(self):
        flowing = [(name, spec) for name, spec in self.blueprint.items() if spec.get("logical_flow")]
        self.assertGreaterEqual(len(flowing), 7)
        for filename, spec in flowing:
            chapter = self.by_filename[filename]
            qmap = {q["id"]: q for q in chapter["quests"]}
            images = {image["id"]: image for image in chapter["images"]}
            ids = [q for section in spec["sections"] for row in section["rows"] for q in row if q]
            self.assertEqual(sorted(ids), sorted(qmap))
            last_bottom = -1.0
            for section in spec["sections"]:
                panel = images[section["panel"]]
                self.assertGreater(panel["y"] - panel["height"] / 2, last_bottom)
                last_bottom = panel["y"] + panel["height"] / 2
                previous_row_y = float('-inf')
                for row in section["rows"]:
                    members = [ident for ident in row if ident]
                    row_y = {qmap[ident]["y"] for ident in members}
                    self.assertEqual(len(row_y), 1, "Related row members stay together")
                    self.assertGreater(next(iter(row_y)), previous_row_y)
                    previous_row_y = next(iter(row_y))
                    self.assertEqual(len({qmap[ident]["x"] for ident in members}), len(members))
                    self.assertTrue(all(qmap[ident]["x"] in layout.FLOW_COLUMNS for ident in members))

    def test_both_languages_explain_the_actual_branch_and_detection_scope(self):
        materials = {entry['quest_id']: entry for entry in multiblocks.load()['quests']}
        for filename, spec in self.source["chapters"].items():
            chapter = self.by_filename[filename]
            for language, locale in self.locales.items():
                self.assertIn(spec["summary"][language], locale[f"chapter.{chapter['id']}.chapter_subtitle"])
                for ident, patch in spec["quests"].items():
                    if "description" in patch:
                        expected = (materials[ident][language]['description'] if ident in materials
                                    else patch['description'][language])
                        expected = list(expected) + refunds.guidance(language).get(ident, [])
                        self.assertEqual(locale[f"quest.{ident}.quest_desc"], expected)
                    if "title" in patch:
                        expected = (materials[ident][language].get('title', patch['title'][language])
                                    if ident in materials else patch['title'][language])
                        self.assertEqual(locale[f"quest.{ident}.title"], expected)

    def test_reactor_lessons_expose_the_custom_detector_requirements(self):
        """Operating a reactor must not depend on undisclosed observer setup."""
        source = (ROOT / "src/compat/quest-automation/src/qm/questauto/QmQuestAutomation.java").read_text()
        objectives = set(re.findall(r'new Objective\(0x([0-9A-F]+)L, 0x([0-9A-F]+)L, "([^"]+)"\)', source))
        duration = int(re.search(r"tick - began >= (\d+)", source).group(1))
        distance_squared = int(re.search(r"player\.distanceToSqr\(watch\.pos\.getCenter\(\)\) > (\d+)", source).group(1))
        radius = math.isqrt(distance_squared)
        self.assertEqual(radius * radius, distance_squared)
        temperature = int(re.search(r"heatCapacitor\.getTemperature\(\) < (\d+)", source).group(1))
        expected = {
            "514D11FF5A4363E6": ("514D122A765B9623", "fission_commission"),
            "514D113E1E8CF8B1": ("514D122185675E38", "fusion_runtime"),
        }
        for ident, (task_id, signal) in expected.items():
            self.assertIn((ident, task_id, signal), objectives)
            self.assertTrue(any(t["id"] == task_id and t["type"] == "custom" for t in self.quests[ident]["tasks"]))
            for language, locale in self.locales.items():
                with self.subTest(quest=ident, language=language):
                    description = " ".join(locale[f"quest.{ident}.quest_desc"])
                    self.assertIn(str(duration), description, "Explain the uninterrupted native tick interval")
                    self.assertIn(str(radius), description, "Explain the observer's distance limit")
                    self.assertIn("Shift", description)
                    for phrase in (("ПКМ", "основною рукою", "вимір") if language == "uk_ua" else ("right-click", "main hand", "dimension")):
                        self.assertIn(phrase, description)
                    if signal == "fission_commission":
                        self.assertIn(f"{temperature} K", description)
                        for phrase in (("охолоджувач", "пошкодження", "вимкни") if language == "uk_ua" else ("coolant", "damage", "shut down")):
                            self.assertIn(phrase, description)


if __name__ == "__main__":
    unittest.main()
