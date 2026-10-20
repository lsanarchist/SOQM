"""Guarantee real recipe costs, personal saved IDs and explicit player guidance."""
from copy import deepcopy
import json
from pathlib import Path
import sys
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_refunds as refunds
import campaign_rewards as rewards
import campaign_progression as progression


class RecipeRefundTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.policy = json.loads(refunds.POLICY_PATH.read_text())
        cls.rows = cls.policy["refunds"]
        cls.recipes = progression.recipe_rows()
        cls.chapters = rewards.chapters_from_disk()

    def test_real_recipes_and_single_primary_quest_back_every_fixed_return(self):
        refunds.validate(self.policy, self.chapters)
        self.assertEqual(len(self.rows), 41)
        self.assertEqual(len({row["output"] for row in self.rows}), 41)
        partial = [prize for row in self.rows for prize in row["items"] if prize["basis"] == "partial_ingredient"]
        indivisible = [prize for row in self.rows for prize in row["items"] if prize["basis"] == "one_component"]
        self.assertEqual((len(partial), len(indivisible)), (34, 7))
        self.assertTrue(all(0.25 <= p["count"] / p["minimum_consumed"] <= 0.5 for p in partial))
        self.assertTrue(all(p["count"] == p["minimum_consumed"] == 1 for p in indivisible))
        self.assertFalse(any("ironchest:" in str(row) or "plastic" in str(row) or "hdpe" in str(row) for row in self.rows))

    def test_refunds_are_added_to_the_same_personal_claim_and_all_cubes_survive(self):
        chapters = deepcopy(self.chapters)
        before = rewards.persistent_ids(chapters)
        task_before = [deepcopy(q["tasks"]) for c in chapters for q in c["quests"]]
        policy = rewards.load_policy()
        report = rewards.apply(chapters, {})
        runtime = rewards.runtime_policy(chapters)
        self.assertEqual(before, rewards.persistent_ids(chapters))
        self.assertEqual(task_before, [q["tasks"] for c in chapters for q in c["quests"]])
        self.assertEqual(report["personal_rewards"], 1627)
        self.assertEqual(report["progressive_rewards"], 1433)
        self.assertEqual(report["milestone_cubes"], 193)
        by_quest = {q["id"]: q for c in chapters for q in c["quests"]}
        for row in self.rows:
            reward = by_quest[row["quest_id"]]["rewards"][0]
            mapping = runtime["reward_mappings"][reward["id"]]
            self.assertFalse(reward["team_reward"])
            self.assertEqual(reward["auto"], "disabled")
            self.assertIn(refunds.TAG, reward["tags"])
            self.assertEqual(mapping["refund"], refunds.runtime(row))
            self.assertIn(row["output"], runtime["production_items"])
            if row["quest_id"] in policy["milestones"]:
                self.assertEqual(mapping["cube"], f"kubejs:mayhem_cube_t{policy['milestones'][row['quest_id']]['tier']}")
        after = deepcopy(chapters)
        self.assertEqual(rewards.apply(chapters, {}), report)
        self.assertEqual(chapters, after)

    def test_changed_recipe_quantity_and_missing_alternative_fail_before_generation(self):
        policy = deepcopy(self.policy)
        policy["refunds"][0]["recipes"][0]["sha256"] = "0" * 64
        with self.assertRaisesRegex(ValueError, "recipe changed"):
            refunds.validate(policy)
        policy = deepcopy(self.policy)
        row = next(row for row in policy["refunds"] if len(row["recipes"]) > 1)
        row["recipes"].pop()
        with self.assertRaisesRegex(ValueError, "every authored alternative"):
            refunds.validate(policy)
        policy = deepcopy(self.policy)
        row = next(row for row in policy["refunds"] if row["output"] == "ae2:drive")
        row["items"][0]["count"] = 3
        with self.assertRaisesRegex(ValueError, "exceeds"):
            refunds.validate(policy)

    def test_a_new_cheaper_recipe_cannot_keep_an_old_output_only_refund(self):
        policy, recipes = deepcopy(self.policy), deepcopy(self.recipes)
        row = next(row for row in policy["refunds"] if row["output"] == "ae2:drive")
        original = next(recipe for recipe in recipes if recipe["id"] == row["recipes"][0]["id"])
        alternative = deepcopy(original)
        alternative["id"] += "_cheap_alternative"
        alternative["recipe"]["key"]["S"] = {"tag": "c:ingots/iron"}
        recipes.append(alternative)
        row["recipes"].append({"id": alternative["id"], "source": alternative["source"],
                               "sha256": refunds.recipe_hash(alternative["recipe"])})
        with self.assertRaisesRegex(ValueError, "exact common ingredient"):
            refunds.validate(policy, recipes=recipes)

    def test_shaped_positions_and_mcjty_wrappers_count_actual_spent_items(self):
        by_id = {row["id"]: row["recipe"] for row in self.recipes}
        recipe = by_id["kubejs:expert_v042/rftoolspower/cell2"]
        self.assertEqual(refunds.recipe_output(recipe), "rftoolspower:cell2")
        self.assertEqual(refunds.direct_items(recipe)["rftoolspower:power_core2"], 3)
        self.assertEqual(refunds.direct_items(by_id["kubejs:expert/ae2/network/blocks/storage_drive"])["ae2:engineering_processor"], 2)

    def test_a_refund_cannot_use_a_filler_or_repeat_the_crafted_output(self):
        for mutation in ("unreviewed", "output", "duplicate"):
            policy = deepcopy(self.policy)
            row = policy["refunds"][0]
            if mutation == "unreviewed": row["items"][0]["item"] = "minecraft:cobblestone"
            elif mutation == "output": row["items"][0]["item"] = row["output"]
            else: policy["refunds"].append(deepcopy(row))
            with self.subTest(mutation=mutation), self.assertRaises(ValueError):
                refunds.validate(policy)

    def test_guidance_is_visible_bilingual_and_removes_stale_refund_paragraphs(self):
        for language in ("uk_ua", "en_us"):
            stale = ["Keep this useful explanation.", "Guaranteed return of consumed components: old.",
                     "Claim the refund after you or your team actually manufacture old."]
            locale = {"quest.NOT_A_REFUND.quest_desc": stale}
            refunds.apply_locale(locale, language)
            self.assertEqual(locale["quest.NOT_A_REFUND.quest_desc"], [stale[0]])
            for row in self.rows:
                paragraphs = locale[f"quest.{row['quest_id']}.quest_desc"]
                self.assertEqual(len(paragraphs), 2)
                for prize in row["items"]:
                    self.assertIn(f"{prize['count']} × ", paragraphs[0])
                    self.assertIn(self.policy["ingredients"][prize["item"]][language], paragraphs[0])
                self.assertIn(row["output_name"][language], paragraphs[1])
            before = deepcopy(locale)
            refunds.apply_locale(locale, language)
            self.assertEqual(before, locale)


if __name__ == "__main__":
    unittest.main()
