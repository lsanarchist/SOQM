"""Offline migration, progression and budget checks for native FTB supply rewards.

These checks cover the data contract, not a substitute implementation of FTB's
random generator. The pinned Java classes and in-game checks are documented in
docs/REWARDS.md. No Minecraft installation or network is needed for this suite.
"""

from collections import Counter
from copy import deepcopy
from fractions import Fraction
import json
from pathlib import Path
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_rewards as rewards


def read_json(path):
    return json.loads(path.read_text(encoding="utf-8"))


def quests(chapters):
    return [quest for chapter in chapters for quest in chapter["quests"]]


def outer_rewards(chapters):
    return [reward for quest in quests(chapters) for reward in quest["rewards"]]


def without_rewards(chapters):
    result = deepcopy(chapters)
    for quest in quests(result):
        del quest["rewards"]
    return result


class RewardMigrationTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.original = [read_json(path) for path in sorted((rewards.QUESTS / "chapters").glob("*.snbt"))]
        cls.migrated = deepcopy(cls.original)
        cls.locale = {"unrelated.key": "Existing translation"}
        cls.summary = rewards.apply(cls.migrated, cls.locale)
        cls.policy = rewards.load_policy()
        cls.tables = rewards.build_tables(cls.policy)
        cls.tables_by_id = {int(table["id"], 16): table for table in cls.tables}

    def test_migration_preserves_every_persistent_object_and_completion_condition(self):
        self.assertEqual(rewards.persistent_ids(self.original), rewards.persistent_ids(self.migrated))
        self.assertEqual(without_rewards(self.original), without_rewards(self.migrated))
        self.assertEqual(len(self.migrated), 37)
        self.assertEqual(len(quests(self.migrated)), 1625)
        self.assertEqual(sum(len(q["tasks"]) for q in quests(self.migrated)), 1772)
        self.assertEqual(len(outer_rewards(self.migrated)), 1626)

    def test_outer_claim_identity_and_manual_team_behavior_are_unchanged(self):
        """FTB claim keys use outer reward ID and personal/team scope, not type."""
        for old, new in zip(outer_rewards(self.original), outer_rewards(self.migrated)):
            with self.subTest(reward=old["id"]):
                self.assertEqual((old["id"], old.get("team_reward", True)),
                                 (new["id"], new.get("team_reward", True)))
                self.assertEqual(old.get("auto", "disabled"), new.get("auto", "disabled"))
                self.assertEqual(new["auto"], "disabled")
        for quest in quests(self.migrated):
            self.assertFalse(quest.get("can_repeat", False), quest["id"])
            self.assertFalse(quest.get("repeatable", False), quest["id"])

    def test_only_reviewed_milestones_keep_one_cube_and_intro_book_stays_personal(self):
        seen_milestones = Counter()
        books = []
        for quest in quests(self.migrated):
            for reward in quest["rewards"]:
                if reward["type"] == "random":
                    self.assertTrue(reward["team_reward"])
                    continue
                self.assertEqual(reward["type"], "item")
                if reward["item"]["id"] == "ftbquests:book":
                    self.assertEqual(quest["id"], rewards.BOOK_QUEST)
                    self.assertFalse(reward["team_reward"])
                    books.append(reward)
                else:
                    self.assertIn(quest["id"], self.policy["milestones"])
                    milestone = self.policy["milestones"][quest["id"]]
                    self.assertEqual(reward["item"]["id"], f"kubejs:mayhem_cube_t{milestone['tier']}")
                    self.assertEqual(reward["count"], 1)
                    self.assertEqual(reward["random_bonus"], 0)
                    self.assertTrue(reward["team_reward"])
                    seen_milestones[quest["id"]] += 1
        self.assertEqual(seen_milestones, Counter({qid: 1 for qid in self.policy["milestones"]}))
        self.assertEqual(len(books), 1)
        old_book = next(r for r in outer_rewards(self.original)
                        if r.get("item", {}).get("id") == "ftbquests:book")
        self.assertEqual(books[0], old_book)
        self.assertEqual(self.summary["milestone_cubes"], 14)
        self.assertEqual(self.summary["random_rewards"], 1611)
        self.assertEqual(self.summary["personal_books"], 1)

    def test_random_reward_references_fit_pinned_json_snbt_integer_parser(self):
        used = set()
        for reward in outer_rewards(self.migrated):
            if reward["type"] == "random":
                ident = reward["table_id"]
                self.assertIs(type(ident), int, "RandomReward.readData uses getLong, not a hex string")
                self.assertGreater(ident, 0)
                self.assertLessEqual(ident, 2**31 - 1, "Bare JSON longs fail pinned SNBT Integer.parseInt")
                self.assertIn(ident, self.tables_by_id)
                self.assertNotIn("item", reward)
                self.assertNotIn("count", reward)
                self.assertNotIn("command", reward)
                used.add(ident)
        self.assertEqual(used, set(self.tables_by_id), "Unused tables need an explicit design review")

    def test_pool_themes_and_fallbacks_respect_each_quests_stage(self):
        self.assertEqual(set(self.policy["chapter_pools"]), {c["filename"] for c in self.migrated})
        pools = {int(pool["id"], 16): pool for pool in self.policy["pools"]}
        for chapter in self.migrated:
            for quest in chapter["quests"]:
                for reward in quest["rewards"]:
                    if reward["type"] == "random":
                        pool = pools[reward["table_id"]]
                        with self.subTest(quest=quest["id"], pool=pool["key"]):
                            self.assertLessEqual(pool["min_stage"], rewards.quest_stage(quest))
                            for entry in pool["entries"]:
                                self.assertLessEqual(rewards.SAFE_ITEMS[entry["item"]], rewards.quest_stage(quest))
        # Concrete boundary: early magic objectives get research supplies, not Nether materials.
        magic = {"filename": "qm_v030_15_botania"}
        self.assertEqual(rewards.choose_pool(magic, {"tags": ["stage_0"]}, self.policy)["key"], "scholar")
        self.assertEqual(rewards.choose_pool(magic, {"tags": ["stage_2"]}, self.policy)["key"], "arcane")
        finale = {"filename": "qm_v030_26_avaritia"}
        self.assertEqual(rewards.choose_pool(finale, {"tags": ["stage_0"]}, self.policy)["key"], "workshop")
        self.assertEqual(rewards.choose_pool(finale, {"tags": ["stage_5"]}, self.policy)["key"], "masterwork")

    def test_intro_iron_tool_rewards_cannot_recreate_ninety_six_ingot_payout(self):
        """The former 32+32+32 iron payout is removed, independently of pool EV."""
        tool_quests = {"513011209CCD8C68", "513011350308FBB4", "51301129D6E9E5E6"}
        actual = [q for q in quests(self.migrated) if q["id"] in tool_quests]
        self.assertEqual(len(actual), 3)
        for quest in actual:
            reward, = quest["rewards"]
            self.assertEqual(reward["type"], "random")
            table = self.tables_by_id[reward["table_id"]]
            self.assertTrue(all(r["item"]["id"] not in {"minecraft:iron_ingot", "minecraft:iron_nugget"}
                                for r in table["rewards"]))
            self.assertTrue(all(r["count"] <= 4 for r in table["rewards"]))

    def test_ordinary_rewards_cannot_supply_a_quest_unlock_or_nested_cube(self):
        actual_items = {r["item"]["id"] for table in self.tables for r in table["rewards"]}
        forbidden = {
            "minecraft:bucket", "minecraft:diamond", "minecraft:netherite_ingot",
            "minecraft:blaze_rod", "minecraft:ender_pearl", "minecraft:nether_star",
            "mekanism:hdpe_pellet", "ae2:logic_processor", "ae2:calculation_processor",
            "ae2:engineering_processor", "kubejs:industrial_chassis",
            "kubejs:precision_mechanism", "kubejs:dimensional_matrix", "kubejs:stellar_alloy",
            "kubejs:mythic_circuit", "kubejs:emc_core", "kubejs:quantum_processor",
        }
        self.assertFalse(actual_items & forbidden)
        self.assertLessEqual(actual_items, set(rewards.SAFE_ITEMS))
        self.assertTrue(all(item.startswith("minecraft:") for item in actual_items))
        for table in self.tables:
            self.assertTrue(all(r["type"] == "item" for r in table["rewards"]), "No nested random rolls")

    def test_apply_is_idempotent_and_locale_only_adds_pool_titles(self):
        again = deepcopy(self.migrated)
        locale = deepcopy(self.locale)
        report = rewards.apply(again, locale)
        self.assertEqual(again, self.migrated)
        self.assertEqual(locale, self.locale)
        self.assertEqual(report, self.summary)
        self.assertEqual(locale["unrelated.key"], "Existing translation")
        expected = rewards.locale_entries("uk_ua")
        self.assertEqual(set(locale), set(expected) | {"unrelated.key"})
        self.assertEqual(set(expected), set(rewards.locale_entries("en_us")))
        self.assertEqual(len(expected), 14)

    def test_stage_budgets_are_complete_and_early_claims_have_no_late_supplies(self):
        budgets = self.summary["stage_budgets"]
        self.assertEqual(sum(b["claims"] for b in budgets.values()), 1611)
        self.assertAlmostEqual(sum(b["expected_supply_items"] for b in budgets.values()),
                               self.summary["expected_supply_items"], places=3)
        for key in ("expected_golden_carrots", "expected_xp_bottles", "expected_name_tags", "expected_emeralds"):
            self.assertEqual(budgets["early_0_1"][key], 0)
        self.assertEqual(budgets["mid_2_3"]["expected_name_tags"], 0)
        self.assertEqual(budgets["mid_2_3"]["expected_emeralds"], 0)
        self.assertGreater(budgets["late_4_6"]["expected_name_tags"], 0)
        self.assertGreater(budgets["late_4_6"]["expected_emeralds"], 0)
        for budget in budgets.values():
            self.assertLessEqual(budget["expected_supply_items"], budget["claims"] * 4)
            self.assertLessEqual(budget["expected_iron_ingots"], budget["claims"] * 0.4)

    def test_all_table_and_inner_ids_are_unique_and_do_not_overlap_saved_ids(self):
        groups = read_json(rewards.QUESTS / "chapter_groups.snbt")["chapter_groups"]
        ids = rewards.persistent_ids(self.migrated) + [group["id"] for group in groups]
        new_ids = [table["id"] for table in self.tables]
        new_ids.extend(r["id"] for table in self.tables for r in table["rewards"])
        self.assertFalse(set(ids) & set(new_ids))
        self.assertEqual(len(ids + new_ids), len(set(ids + new_ids)))
        self.assertEqual(len(new_ids), 84)
        self.assertTrue(all(rewards.ID_PATTERN.fullmatch(ident) for ident in new_ids))

    def test_bad_existing_claim_flags_or_ids_require_review(self):
        changed = deepcopy(self.original)
        reward = next(r for r in outer_rewards(changed) if r.get("team_reward", True))
        reward["auto"] = "enabled"
        with self.assertRaisesRegex(ValueError, "non-manual"):
            rewards.apply(changed, {})
        collision = deepcopy(self.original)
        collision[0]["id"] = self.tables[0]["id"]
        before = deepcopy(collision)
        with self.assertRaisesRegex(ValueError, "collide"):
            rewards.apply(collision, {})
        self.assertEqual(collision, before)


class RewardBudgetTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.policy = rewards.load_policy()

    def test_one_small_positive_weighted_draw_and_visible_probabilities(self):
        for table in rewards.build_tables(self.policy):
            with self.subTest(table=table["id"]):
                self.assertEqual(table["loot_size"], 1)
                self.assertEqual(table["empty_weight"], 0)
                self.assertFalse(table["hide_tooltip"])
                self.assertTrue(table["use_title"])
                self.assertEqual(len(table["rewards"]), 5)
                self.assertEqual(sum(r["weight"] for r in table["rewards"]), 100)
                for entry in table["rewards"]:
                    self.assertGreater(entry["weight"], 0, "Zero-weight FTB entries are automatic grants")
                    self.assertGreaterEqual(entry["count"], 1)
                    self.assertLessEqual(entry["count"], 4)
                    self.assertEqual(entry["random_bonus"], 0)

    def test_weighted_supply_and_iron_budget_with_exact_arithmetic(self):
        iron_units = {"minecraft:iron_ingot": Fraction(1), "minecraft:iron_nugget": Fraction(1, 9)}
        for pool in self.policy["pools"]:
            with self.subTest(pool=pool["key"]):
                self.assertLessEqual(rewards.expected_units(pool), 4)
                self.assertGreaterEqual(rewards.expected_units(pool), 1)
                self.assertLessEqual(rewards.expected_iron(pool), Fraction(2, 5))
                # Even the luckiest individual supply claim is at most one ingot.
                self.assertLessEqual(max(e["count"] * iron_units.get(e["item"], 0) for e in pool["entries"]), 1)
        workshop = next(pool for pool in self.policy["pools"] if pool["key"] == "workshop")
        self.assertEqual(rewards.expected_iron(workshop), Fraction(17, 60))
        self.assertEqual(rewards.expected_units(workshop), Fraction(51, 20))

    def test_policy_rejects_unsafe_budget_schema_and_progression_changes(self):
        def mutate(path, value):
            policy = deepcopy(self.policy)
            current = policy
            for key in path[:-1]:
                current = current[key]
            current[path[-1]] = value
            return policy

        cases = [
            (("pools", 0, "entries", 0, "weight"), 0, "Zero weight"),
            (("pools", 0, "entries", 0, "item"), "mekanism:hdpe_pellet", "Unsafe item"),
            (("pools", 0, "entries", 0, "item"), "minecraft:glowstone_dust", "premature"),
            (("pools", 0, "entries", 0, "count"), 16, "small stack"),
            (("pools", 0, "entries", 0, "count"), True, "small stack"),
            (("pools", 0, "entries", 0, "weight"), 29, "total 100"),
            (("pools", 0, "id"), "0414572B7C36F04F", "JSON-safe range"),
            (("pools", 0, "id"), self.policy["pools"][1]["id"], "ID collision"),
            (("pools", 2, "fallback"), "engineering", "fallback cycle"),
        ]
        for path, value, error in cases:
            with self.subTest(path=path, value=value):
                with self.assertRaisesRegex(ValueError, error):
                    rewards.validate_policy(mutate(path, value))
        iron_heavy = deepcopy(self.policy)
        workshop = iron_heavy["pools"][1]
        next(e for e in workshop["entries"] if e["item"] == "minecraft:iron_ingot")["count"] = 4
        with self.assertRaisesRegex(ValueError, "expected iron"):
            rewards.validate_policy(iron_heavy)

    def test_committed_tables_match_reviewed_policy_exactly(self):
        expected = rewards.expected_files(self.policy)
        committed = {path.name: path.read_text(encoding="utf-8")
                     for path in rewards.TABLES_PATH.glob("*.snbt")}
        self.assertEqual(committed, expected, "Regenerate with python scripts/campaign_rewards.py --write")
        for filename, content in committed.items():
            self.assertEqual(json.loads(content)["id"], Path(filename).stem)

    def test_table_writer_is_deterministic_and_refuses_silent_id_removal(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            self.assertEqual(rewards.write_tables(directory), 14)
            first = {p.name: p.read_bytes() for p in directory.iterdir()}
            self.assertEqual(rewards.write_tables(directory), 14)
            self.assertEqual(first, {p.name: p.read_bytes() for p in directory.iterdir()})
            stale = directory / "000000006A51FFFF.snbt"
            stale.write_text("{}\n", encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "stale table IDs"):
                rewards.write_tables(directory)
            self.assertTrue(stale.exists())


if __name__ == "__main__":
    unittest.main()
