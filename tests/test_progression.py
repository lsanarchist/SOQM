"""Behavioral regressions for the repaired campaign and early ME bootstrap."""

from collections import defaultdict
from copy import deepcopy
import json
from pathlib import Path
import re
import sys
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_progression as progression
import campaign_locales


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def flatten(chapters):
    return {quest["id"]: quest for chapter in chapters for quest in chapter["quests"]}


def ancestors(quests, ident):
    seen = set()
    pending = list(quests[ident].get("dependencies", []))
    while pending:
        ident = pending.pop()
        if ident not in seen:
            seen.add(ident)
            pending.extend(quests[ident].get("dependencies", []))
    return seen


class ProgressionRepairTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        directory = ROOT / "overrides/config/ftbquests/quests"
        cls.before = [read(path) for path in sorted((directory / "chapters").glob("*.snbt"))]
        cls.chapters = deepcopy(cls.before)
        cls.locale = campaign_locales.load("uk_ua")
        cls.report = progression.apply(cls.chapters, cls.locale)
        cls.quests = flatten(cls.chapters)

    def test_world_progress_identity_and_rewards_are_preserved(self):
        """A content refresh cannot reset IDs or mint replacement rewards."""
        before = flatten(self.before)
        self.assertEqual(set(before), set(self.quests))
        for ident, quest in self.quests.items():
            with self.subTest(quest=ident):
                self.assertEqual([t["id"] for t in before[ident]["tasks"]], [t["id"] for t in quest["tasks"]])
                self.assertEqual(before[ident]["rewards"], quest["rewards"])
                if ident != "513011AAD4652E6F":
                    self.assertEqual(before[ident]["tasks"], quest["tasks"])
        self.assertEqual(len(self.chapters), len(self.before))
        progression.assert_valid_graph(self.quests)
        self.assertEqual(self.report["warnings"], [])

    def test_refresh_is_idempotent(self):
        chapters = deepcopy(self.chapters)
        locale = deepcopy(self.locale)
        progression.apply(chapters, locale)
        self.assertEqual(chapters, self.chapters)
        self.assertEqual(locale, self.locale)

    def test_first_chassis_is_taught_after_its_own_steel_and_creosote(self):
        """The first material gate must not hide the machines supplying it."""
        preparation = (
            "5130110C9CAE442B",  # Engineer's Manual
            "51301140C9328072",  # hammer
            "513011A899EC23A8",  # wire cutters
            "5130115B313C6A81",  # coke bricks
            "51301157FF888779",  # formed Coke Oven
            "5130114F2594B696",  # coke
            "513011AE9CF26AB0",  # formed Blast Furnace
            "51301167DCA596B9",  # steel plate
            "5130117FBB68BE2D",  # treated planks
            "513011B6D00A0F88",  # copper wire coils
        )
        for ident in preparation:
            with self.subTest(quest=ident):
                self.assertNotIn(progression.CHASSIS, ancestors(self.quests, ident))
                self.assertEqual(progression.quest_stage(self.quests[ident]), 0)
        chassis = ancestors(self.quests, progression.CHASSIS)
        self.assertTrue(set(preparation) <= chassis)
        self.assertIn("51301157FF888779", ancestors(self.quests, "5130117FBB68BE2D"))
        self.assertIn("513011AE9CF26AB0", ancestors(self.quests, "51301167DCA596B9"))
        # Water and wind remain alternative generation branches, not a demand
        # to build both machines before wiring the first supply.
        self.assertNotIn("5130114CEA71331A", ancestors(self.quests, "513011647FD31AF9"))
        self.assertNotIn("513011647FD31AF9", ancestors(self.quests, "5130114CEA71331A"))

    def test_processor_workshop_precedes_precision(self):
        """The ingredients of a campaign gate must be teachable before it."""
        for ident in ("513011CFBA3A9CF5", "5130110AD7EC7A65", "513011E6243A5B01"):
            with self.subTest(quest=ident):
                self.assertNotIn(progression.PRECISION, ancestors(self.quests, ident))
                self.assertLess(progression.quest_stage(self.quests[ident]), 2)
        self.assertIn("513011CFBA3A9CF5", ancestors(self.quests, "513011E6243A5B01"))
        self.assertIn("513011E6243A5B01", ancestors(self.quests, progression.PRECISION))
        self.assertIn("513011AA8F084F8E", ancestors(self.quests, "513011540D59EE53"))
        self.assertIn("513011020C4B1739", ancestors(self.quests, "513011BBBBE02633"))

    def test_small_me_setup_is_available_before_dimension_scaling(self):
        for ident in (
            "513011F797E11624", "5130115B3F92B69A", "513011E89C668AA7",
            "513011562F81FD24", "5130112D00658B23", "513011B5F7F592D8",
            "513011DEEBB429E9", "51301130F8B93E94",
        ):
            with self.subTest(quest=ident):
                self.assertNotIn(progression.MATRIX, ancestors(self.quests, ident))
                self.assertEqual(progression.quest_stage(self.quests[ident]), 2)
        for ident in ("5130119BD717861F", "5130114905904323", "513011BBCA06FEA6"):
            self.assertIn(progression.MATRIX, ancestors(self.quests, ident))

    def test_first_me_craft_is_observed_without_claiming_autocrafting(self):
        quest = self.quests["513011AAD4652E6F"]
        self.assertEqual(quest["tasks"], [{
            "type": "advancement", "advancement": "qm:ae2/first_me_craft",
            "criterion": "", "id": "5130123B461D5BCE",
        }])
        workshop = self.quests["51301130F8B93E94"]
        self.assertEqual({task["type"] for task in workshop["tasks"]}, {"item"})
        self.assertEqual(self.locale["quest.51301130F8B93E94.title"], "Комплект для ME-майстерні")
        self.assertIn("не доводить", " ".join(self.locale["quest.51301130F8B93E94.quest_desc"]))

    def test_preflight_equipment_is_before_moon_and_stellar_alloy(self):
        for ident in ("5130114FE09DA6CE", "5130115956283BA9", "513011984F5B40C8", "513011A99D6EA3A8"):
            with self.subTest(quest=ident):
                required = ancestors(self.quests, ident)
                self.assertNotIn(progression.MOON, required)
                self.assertNotIn(progression.STELLAR, required)
                self.assertEqual(progression.quest_stage(self.quests[ident]), 3)
        moon = ancestors(self.quests, progression.MOON)
        self.assertIn("5130114FE09DA6CE", moon)
        self.assertIn("5130115956283BA9", moon)
        self.assertNotIn(progression.STELLAR, ancestors(self.quests, "513011FE61D9DE0F"))

    def test_furnace_and_reactor_stage_labels_match_material_availability(self):
        expected = {
            "514011DDB6B69693": (2, progression.PRECISION),
            "514011AAAA9555C0": (3, progression.MATRIX),
            "514011F9E7C41342": (3, progression.MATRIX),
            "51401118B1E42386": (3, progression.MATRIX),
            "513011AB19DDDE4C": (4, progression.MOON),
        }
        for ident, (stage, gate) in expected.items():
            with self.subTest(quest=ident):
                self.assertEqual(progression.quest_stage(self.quests[ident]), stage)
                self.assertIn(gate, ancestors(self.quests, ident))
        for ident, quest in self.quests.items():
            for dependency in quest.get("dependencies", []):
                self.assertGreaterEqual(progression.quest_stage(quest), progression.quest_stage(self.quests[dependency]), ident)

    def test_planet_resource_quests_reference_their_destination(self):
        for ident in ("514011676C5FD3C1", "5140117C0F7B94F5"):
            self.assertIn(progression.MERCURY, self.quests[ident]["dependencies"])
            self.assertNotIn(progression.VENUS, self.quests[ident]["dependencies"])
        self.assertIn(progression.MARS, ancestors(self.quests, "51301152E78DE39E"))
        self.assertIn(progression.VENUS, ancestors(self.quests, "5130117C7AD68848"))
        self.assertIn("51301144D2BF0879", ancestors(self.quests, "5130111C890D830A"))


class EarlyMERecipeTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.rows = progression.recipe_rows()
        cls.recipes = {row["id"]: row["recipe"] for row in cls.rows}

    def test_bootstrap_keeps_cross_mod_crafters_without_late_materials(self):
        expected = {
            "kubejs:expert/ae2/network/crafting/cpu_crafting_unit": "enderio:crafter",
            "kubejs:expert/ae2/network/crafting/molecular_assembler": "rftoolsutility:crafter1",
        }
        by_output = defaultdict(list)
        for recipe in self.recipes.values():
            by_output[progression.recipe_output(recipe)].append(recipe)

        def reachable_ingredients(item, seen=None):
            seen = set() if seen is None else seen
            if item in seen:
                return set()
            seen.add(item)
            result = set()
            for recipe in by_output.get(item, []):
                direct = progression.ingredient_items(recipe)
                result.update(direct)
                for ingredient in direct:
                    result.update(reachable_ingredients(ingredient, seen))
            return result

        blocked = {
            "kubejs:dimensional_matrix", "kubejs:quantum_processor",
            "kubejs:stellar_alloy", "kubejs:radiation_shield",
            "kubejs:mythic_circuit", "kubejs:emc_core",
        }
        for recipe_id, crafter in expected.items():
            recipe = self.recipes[recipe_id]
            inputs = progression.ingredient_items(recipe)
            with self.subTest(recipe=recipe_id):
                self.assertIn(crafter, inputs)
                self.assertIn("kubejs:industrial_chassis", inputs)
                self.assertIn("kubejs:precision_mechanism", inputs)
                self.assertIn("kubejs:control_assembly", inputs)
                self.assertEqual(recipe["result"]["count"], 1)
                self.assertEqual(reachable_ingredients(progression.recipe_output(recipe)) & blocked, set())

    def test_larger_components_gate_both_crafting_and_in_world_cpu_upgrades(self):
        """Gating only the CPU recipe would leave AE2's right-click upgrade open."""
        source = (ROOT / "overrides/kubejs/server_scripts/qm_expert_v042.js").read_text()
        match = re.search(r"var removeIds\s*=\s*", source)
        removed, _ = json.JSONDecoder().raw_decode(source[match.end():])
        for size in ("4k", "16k"):
            native = f"ae2:network/cells/item_storage_components_cell_{size}_part"
            replacement = self.recipes["kubejs:expert_v042/" + native.replace(":", "/", 1)]
            with self.subTest(size=size):
                self.assertIn(native, removed)
                self.assertIn("kubejs:dimensional_matrix", progression.ingredient_items(replacement))
                self.assertEqual(replacement["result"], {"count": 1, "id": f"ae2:cell_component_{size}"})
                self.assertNotIn(f"ae2:crafting_unit_upgrade/{size}_crafting_storage", removed)
                self.assertNotIn(f"ae2:network/crafting/{size}_cpu_crafting_storage", removed)
        self.assertNotIn("ae2:network/cells/item_storage_components_cell_1k_part", removed)


class TutorialContentTests(unittest.TestCase):
    def test_bilingual_overlays_are_complete_and_mechanically_distinct(self):
        data = read(ROOT / "metadata/campaign_tutorials.json")
        self.assertGreaterEqual(len(data["quests"]), 50)
        for ident, translations in data["quests"].items():
            for language in ("uk_ua", "en_us"):
                with self.subTest(quest=ident, language=language):
                    entry = translations[language]
                    self.assertTrue(entry["title"])
                    self.assertTrue(entry["description"])
                    self.assertTrue(all(isinstance(line, str) and line.strip() for line in entry["description"]))
                    self.assertNotIn("Крафт 0.4.3:", " ".join(entry["description"]))
                    if language == "en_us":
                        self.assertIsNone(re.search(r"[А-Яа-яІіЇїЄєҐґ]", entry["title"] + " ".join(entry["description"])))
        devices = ("513011E0DFFECB6C", "513011578532ED51", "51301162B4562E25", "513011285F6711C0", "513011DF59EDD6B8", "513011F21184218C", "51301142E35FA369")
        for language in ("uk_ua", "en_us"):
            descriptions = [tuple(data["quests"][ident][language]["description"]) for ident in devices]
            self.assertEqual(len(set(descriptions)), len(devices))


if __name__ == "__main__":
    unittest.main()
