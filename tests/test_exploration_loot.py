"""Guard finite chest scopes, material gates and the shipped first-fill policy."""
import importlib.util
import json
from pathlib import Path
import unittest
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "overrides/kubejs/data"
SOURCE = json.loads((ROOT / "metadata/exploration_loot.json").read_text())
POLICY_PATH = ROOT / "src/compat/chest-balance/resources/qm_chest_policy.json"
POLICY = json.loads(POLICY_PATH.read_text())


def table_ids(value):
    if isinstance(value, dict):
        if value.get("condition") == "neoforge:loot_table_id":
            yield value["loot_table_id"]
        for child in value.values():
            yield from table_ids(child)
    elif isinstance(value, list):
        for child in value:
            yield from table_ids(child)


class ExplorationLootTests(unittest.TestCase):
    def test_no_custom_progression_or_renewable_food_in_additions(self):
        blocked = set(POLICY["forbidden_exact"])
        prefixes = tuple(POLICY["forbidden_prefixes"])
        # These pack recipes consume custom quantum/dimension prerequisites.
        self.assertTrue({"mekanism:elite_control_circuit", "mekanism:ultimate_control_circuit",
                         "minecraft:nether_star", "industrialforegoing:plastic",
                         "artifacts:eternal_steak", "artifacts:everlasting_beef"} <= blocked)
        for name, profile in SOURCE["profiles"].items():
            for kind in ("materials", "utility", "finds"):
                ids = []
                for item, weight, minimum, maximum in profile[kind]:
                    with self.subTest(profile=name, kind=kind, item=item):
                        self.assertNotIn(item, blocked)
                        self.assertFalse(item.startswith(prefixes))
                        self.assertGreater(weight, 0)
                        self.assertTrue(1 <= minimum <= maximum <= profile["limits"]["stack_cap"])
                        if kind == "finds":
                            self.assertEqual((minimum, maximum), (1, 1))
                    ids.append(item)
                self.assertEqual(len(ids), len(set(ids)), "Do not accidentally weight a duplicate twice")

    def test_one_bonus_roll_and_small_budgets(self):
        for name, profile in SOURCE["profiles"].items():
            limits = profile["limits"]
            self.assertTrue(3 <= limits["min_slots"] <= limits["max_slots"] <= 10)
            self.assertLessEqual(limits["max_units"], 28)
            self.assertTrue(0 < profile["find_chance"] < .5)
            table = json.loads((DATA / f"qm/loot_table/exploration/{name}.json").read_text())
            self.assertEqual(len(table["pools"]), 4)
            for pool in table["pools"]:
                self.assertEqual(pool["rolls"], 1)
                self.assertEqual(pool["bonus_rolls"], 0)
            find = table["pools"][2]
            self.assertEqual(find["conditions"], [{"condition": "minecraft:random_chance", "chance": profile["find_chance"]}])

    def test_additions_have_disjoint_exact_chest_routes(self):
        seen = set()
        for name in SOURCE["profiles"]:
            modifier = json.loads((DATA / f"qm/loot_modifiers/exploration/{name}.json").read_text())
            self.assertEqual(modifier["type"], "neoforge:add_table")
            self.assertEqual(modifier["table"], f"qm:exploration/{name}")
            routes = list(table_ids(modifier["conditions"]))
            self.assertTrue(routes)
            self.assertEqual(len(routes), len(set(routes)))
            self.assertFalse(seen.intersection(routes), "A chest must not roll two bonus profiles")
            seen.update(routes)
            for table in routes:
                self.assertEqual(POLICY["routes"][table], name)
                self.assertNotIn(":entities/", table)
                self.assertFalse(table.startswith("qm:exploration/"), "No recursive addon-table routes")
        new = {table for table in POLICY["routes"]
               if table.startswith(("twilight_treehouses:", "ad_astra_more_structures:", "underground_rooms:"))}
        self.assertEqual(len(new), 17)
        self.assertTrue(new <= seen)
        self.assertTrue((DATA / "underground_rooms/loot_table/chests/undead_dungeon_loot.json").is_file())

    def test_generated_files_and_packaged_policy_match_source(self):
        spec = importlib.util.spec_from_file_location("exploration_loot_under_test", ROOT / "scripts/exploration_loot.py")
        generator = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(generator)
        for path, expected in generator.outputs().items():
            self.assertEqual(json.loads(path.read_text()), expected, str(path))
        authored = json.loads((ROOT / "metadata/authored-mods.json").read_text())
        row = next(row for row in authored if row["filename"].startswith("qm-chest-balance-"))
        with ZipFile(ROOT / "overrides/mods" / row["filename"]) as jar:
            self.assertEqual(json.loads(jar.read("qm_chest_policy.json")), POLICY)


if __name__ == "__main__":
    unittest.main()
