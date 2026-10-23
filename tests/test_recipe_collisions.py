"""Guard the nine ambiguous table outputs using the whole authored catalogue.

The JavaScript handlers run as written, but matching below is an item/tag model,
not a replacement for the separate native serializer/runtime probes. The compact
fixture is tied to the exact mod lock and every authored tag resource/handler.
"""

from copy import deepcopy
import hashlib
import json
from pathlib import Path
import sys
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import recipe_collisions as choices


class RecipeCollisionTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.fixture = choices.load_fixture()
        cls.capture = choices.capture_recipes(native_ids=[row["id"] for row in cls.fixture["native_recipes"]])
        cls.rows = choices.active_rows(cls.capture, cls.fixture)
        cls.crafts, cls.excluded, cls.disabled = choices.compile_recipes(cls.rows, cls.fixture)

    def test_all_current_recipe_handlers_and_separate_removal_collections_are_used(self):
        self.assertEqual({row["source"] for row in self.capture["handlers"]}, {
            "qm_chancecubes_balance.js", "qm_expert_progression.js", "qm_expert_v040.js",
            "qm_expert_v042.js", "qm_expert_v043.js", "qm_late_emc.js", "qm_expert_hardcore_workshop.js",
        })
        self.assertEqual(len(self.capture["rows"]), 628)
        self.assertEqual(sum(not row["removed"] for row in self.capture["rows"]), 617)
        self.assertEqual(sum(row["removed"] for row in self.capture["rows"]), 11)
        self.assertEqual(self.capture["handlers"][-1]["source"], "qm_expert_hardcore_workshop.js")
        self.assertLess(self.capture["handlers"][-1]["priority"], 0)
        # The native originals must not survive beside their authored replacements.
        self.assertEqual(set(self.capture["removed_native_ids"]),
                         {row["id"] for row in self.fixture["native_recipes"]})
        self.assertFalse({row["id"] for row in self.fixture["native_recipes"]} & {row["id"] for row in self.rows})
        self.assertTrue(any(row["id"].startswith("kubejs:late_emc/") for row in self.rows))
        self.assertIn("qm:skills/retraining_manual", {row["id"] for row in self.rows})

    def test_all_authored_ordinary_shaped_recipes_have_unambiguous_outputs(self):
        self.assertEqual(sum(craft.shaped for craft in self.crafts), 474)
        self.assertEqual(choices.collisions(self.crafts), [])
        # Do not flatten single/double storage recipes or custom ingredient state.
        custom_ingredients = [row for row in self.excluded if row["reason"].startswith("component/custom ingredient:")]
        self.assertEqual(len(custom_ingredients), 5)
        self.assertIn("kubejs:hardcore_workshop/storage/double_iron_chest",
                      {row["id"] for row in self.excluded})

    def test_every_affected_grid_selects_its_output_from_the_whole_catalogue(self):
        targets = {row["id"] for row in self.fixture["baseline_recipes"]}
        selected = [craft for craft in self.crafts if craft.id in targets]
        self.assertEqual(len(selected), 9)
        for craft in selected:
            # The expected result is independent of catalogue order; query every
            # candidate recipe, including ordinary shapeless recipes, not the ID.
            expected = {craft.outcome}
            for cells in craft.variants():
                for dy in range(4 - craft.height):
                    for dx in range(4 - craft.width):
                        grid = [""] * 9
                        for y in range(craft.height):
                            for x in range(craft.width):
                                cell = cells[y * craft.width + x]
                                grid[(y + dy) * 3 + x + dx] = min(cell) if cell else ""
                        with self.subTest(recipe=craft.id, offset=(dx, dy)):
                            self.assertEqual(choices.matching_outputs(self.crafts, grid), expected)
                            self.assertEqual(choices.matching_outputs(list(reversed(self.crafts)), grid), expected)

    def test_original_nine_recipes_reproduce_all_three_collision_groups(self):
        old = {row["id"]: row for row in self.fixture["baseline_recipes"]}
        baseline = [deepcopy(old.get(row["id"], row)) for row in self.rows]
        crafts, _, _ = choices.compile_recipes(baseline, self.fixture)
        found = choices.collisions(crafts)
        expected_pairs = {frozenset((first, second))
                          for group in self.fixture["baseline_collision_groups"]
                          for index, first in enumerate(group) for second in group[index + 1:]}
        self.assertEqual({frozenset(row["ids"]) for row in found}, expected_pairs)
        self.assertEqual(len(found), 10)  # 3 gold pairs + 6 iron pairs + 1 upgrade pair.
        self.assertEqual({ident for row in found for ident in row["ids"]}, set(old))
        for collision in found:
            grid = [""] * 9
            for y in range(collision["height"]):
                for x in range(collision["width"]):
                    grid[y * 3 + x] = collision["grid"][y * collision["width"] + x]
            self.assertGreater(len(choices.matching_outputs(crafts, grid)), 1)

    def test_three_real_legacy_condition_examples_are_rejected(self):
        self.assertEqual(choices.legacy_condition_ids(self.rows), [])
        legacy = self.fixture["legacy_condition_recipes"]
        self.assertEqual(len(legacy), 3)
        self.assertEqual(choices.legacy_condition_ids(legacy), sorted(row["id"] for row in legacy))
        # The reviewed affected recipes now honor absent-material predicates.
        no_metals = deepcopy(self.fixture)
        no_metals["tags"]["c:ingots/copper"] = []
        no_metals["tags"]["c:ingots/silver"] = []
        ids = {row["id"] for row in legacy}
        _, _, disabled = choices.compile_recipes([row for row in self.rows if row["id"] in ids], no_metals)
        self.assertEqual(set(disabled), ids)

    def test_tag_intersections_padding_and_mirroring_are_not_token_equality(self):
        # Real pinned c:ingots/iron includes minecraft:iron_ingot. Two different
        # ingredient spellings and mirrored/padded patterns accept this same grid.
        rows = [
            {"id": "test:tag", "recipe": {"type": "minecraft:crafting_shaped", "pattern": ["AB"],
             "key": {"A": {"tag": "c:ingots/iron"}, "B": {"item": "minecraft:stick"}},
             "result": {"id": "minecraft:iron_sword"}}},
            {"id": "test:item", "recipe": {"type": "minecraft:crafting_shaped", "pattern": ["   ", " BA", "   "],
             "key": {"A": {"item": "minecraft:iron_ingot"}, "B": {"item": "minecraft:stick"}},
             "result": {"id": "minecraft:iron_pickaxe"}}},
        ]
        crafts, _, _ = choices.compile_recipes(rows, self.fixture)
        self.assertEqual(len(choices.collisions(crafts)), 1)
        grid = ["", "", "", "", "minecraft:stick", "minecraft:iron_ingot", "", "", ""]
        self.assertEqual(len(choices.matching_outputs(crafts, grid)), 2)

    def test_reviewed_resource_provenance_is_bound_to_the_pins_and_authored_tags(self):
        self.assertEqual(len(self.fixture["tags"]), 149)
        self.assertTrue(all(self.fixture["tags"].values()))
        self.assertEqual(choices.authored_tag_inputs(), self.fixture["provenance"]["authored_tag_inputs"])
        with patch.object(choices, "authored_tag_inputs", return_value={}):
            with self.assertRaisesRegex(ValueError, "Authored item tags or tag handlers changed"):
                choices.load_fixture()
        policy = json.loads((ROOT / "metadata/hardcore_workshop.json").read_text())
        evidence = {row["native_evidence"]["path"]: row["native_evidence"]["sha256"]
                    for row in policy["recipes"] if row.get("native_evidence")}
        for row in self.fixture["native_recipes"]:
            digest = hashlib.sha256(json.dumps(row["recipe"], sort_keys=True, separators=(",", ":")).encode()).hexdigest()
            self.assertEqual(digest, evidence[row["source"].split("!", 1)[1]], row["id"])

    def test_unreviewed_predicates_cannot_silently_hide_a_new_collision(self):
        for condition in ({"type": "neoforge:tag_empty", "tag": "c:ingots"},
                          {"type": "neoforge:mod_loaded", "modid": "example"},
                          {"type": "neoforge:item_exists", "item": "example:item"}):
            with self.subTest(condition=condition):
                with self.assertRaisesRegex(ValueError, "Unreviewed"):
                    choices.condition_matches(condition, self.fixture)


if __name__ == "__main__":
    unittest.main()
