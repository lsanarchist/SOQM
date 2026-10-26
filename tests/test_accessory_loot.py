"""Check the reviewed accessory and WDA routing contracts; native behavior is probed in Java."""
import json
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]


def read(name):
    return json.loads((ROOT / name).read_text(encoding="utf-8"))


class AccessoryLootTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.source = read("metadata/accessory_loot.json")
        cls.arise = read("metadata/when_dungeons_arise_loot.json")
        cls.policy = read("src/compat/chest-balance/resources/qm_chest_policy.json")

    def test_reviewed_variety_and_utility_subset(self):
        pools = self.source["pools"]
        self.assertEqual(len(pools["general"]), 46)
        self.assertEqual(len(set(pools["general"])), 46)
        self.assertEqual(sum(item.startswith("artifacts:") for item in pools["general"]), 37)
        self.assertEqual(sum(item.startswith("relics:") for item in pools["general"]), 9)
        self.assertEqual(len(pools["utility"]), 21)
        self.assertTrue(set(pools["utility"]) <= set(pools["general"]))
        self.assertTrue(set(pools["general"]) <= set(self.source["recognized_items"]))

    def test_no_renewable_food_or_dimension_gated_relics_in_added_pool(self):
        added = set(self.source["pools"]["general"])
        self.assertFalse(added & set(self.policy["forbidden_exact"]))
        self.assertFalse(any(item.startswith(tuple(self.policy["forbidden_prefixes"])) for item in added))
        native_only = {"relics:" + name for name in (
            "chorus_staff", "clot_of_time", "piglin_mask", "sphere_of_self_sacrifice",
            "midnight_mantle", "glitchy_mantle", "ghostly_mantle", "kinetic_belt",
            "reflective_necklace", "ring_of_the_seven_deadly_sins", "shield_of_retaliation")}
        self.assertFalse(added & native_only)
        # Native unique loot remains recognized/protected even when it is not
        # eligible for the new general additions.
        self.assertTrue(native_only <= set(self.source["recognized_items"]))

    def test_modest_single_bonus_and_complete_profile_coverage(self):
        self.assertEqual(self.source["bonus_max"], 1)
        self.assertEqual(self.source["bonus_below_distinct"], 2)
        self.assertEqual(set(self.source["profiles"]), set(self.policy["profiles"]))
        expected = {"settlement": .02, "workshop": .04, "expedition": .08,
                    "stronghold": .12, "relic_vault": .16, "dungeon_basic": .08,
                    "dungeon_hard": .12, "dungeon_vault": .16,
                    "dungeon_citadel_supplies": .10, "dungeon_citadel_treasure": .16,
                    "dungeon_abyss_supplies": .12, "dungeon_abyss_treasure": .16}
        for name, chance in expected.items():
            self.assertEqual(self.source["profiles"][name]["bonus_chance"], chance)
            self.assertIn(self.source["profiles"][name]["pool"], self.source["pools"])

    def test_packaged_policy_inputs_are_complete(self):
        for key in ("recognized_items", "pools", "profiles", "bonus_max", "bonus_below_distinct"):
            self.assertEqual(self.policy["accessories"][key], self.source[key])

    def test_wda_is_finite_outer_container_routing(self):
        routes = self.arise["routes"]
        self.assertEqual(len(routes), 128)
        actual = {key: value for key, value in self.policy["routes"].items() if key.startswith("dungeons_arise:")}
        self.assertEqual(actual, routes)
        allowed = {"settlement", "workshop", "expedition", "stronghold", "relic_vault"}
        for table, profile in routes.items():
            self.assertTrue(table.startswith("dungeons_arise:chests/"))
            self.assertIn(profile, allowed)
            self.assertNotIn("vault", table)
            self.assertNotIn("archaeology", table)
        self.assertEqual(self.arise["audit"]["outer_container_kinds"], ["minecraft:chest", "minecraft:barrel"])
        self.assertFalse(set(routes) & set(self.arise["audit"]["dangling_legacy_references"]))

    def test_wda_pin_matches_pack_lock(self):
        lock = read("metadata/mods.lock.json")
        rows = lock["mods"] if isinstance(lock, dict) else lock
        row = next(row for row in rows if row.get("file_id") == 7150870 or row.get("fileID") == 7150870)
        self.assertEqual(row["sha256"], self.arise["source"]["sha256"])
        self.assertEqual(self.arise["source"]["curseforge_project_id"], 442508)


if __name__ == "__main__":
    unittest.main()
