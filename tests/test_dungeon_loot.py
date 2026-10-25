"""Keep DD combat rewards scoped, progression-safe and tied to native player tiers."""
from __future__ import annotations

import hashlib
import importlib.util
import json
from pathlib import Path
import unittest
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "overrides/kubejs/data"
SOURCE = json.loads((ROOT / "metadata/dungeon_loot.json").read_text())
DUNGEONS = json.loads((ROOT / "metadata/dimensional_dungeons.json").read_text())
POLICY = json.loads((ROOT / "src/compat/chest-balance/resources/qm_chest_policy.json").read_text())


def walk(value):
    if isinstance(value, dict):
        yield value
        for child in value.values():
            yield from walk(child)
    elif isinstance(value, list):
        for child in value:
            yield from walk(child)


def table_ids(value):
    return [node["loot_table_id"] for node in walk(value) if node.get("condition") == "neoforge:loot_table_id"]


def load(path):
    return json.loads((DATA / path).read_text())


class DungeonCombatLootTests(unittest.TestCase):
    def test_existing_world_profiles_keep_their_reviewed_balance(self):
        existing = json.loads((ROOT / "metadata/exploration_loot.json").read_text())
        canonical = json.dumps(existing["profiles"], sort_keys=True, separators=(",", ":")).encode()
        self.assertEqual(hashlib.sha256(canonical).hexdigest(),
                         "31b3d0d59798d858d5ee609cbab49c385cd4a1084811d92456fde84ebbc5ecc8")
        for name, profile in existing["profiles"].items():
            self.assertEqual(POLICY["profiles"][name], profile["limits"])
            self.assertNotIn("combat_rewards", POLICY["profiles"][name])
            routes = table_ids(load(f"qm/loot_modifiers/exploration/{name}.json"))
            self.assertFalse(any(table.startswith("dimdungeons:") for table in routes
                                 if table != "dimdungeons:chest/kit_random"))

    def test_dedicated_profiles_have_exact_disjoint_outer_routes(self):
        expected = dict(DUNGEONS["loot_routes"])
        expected.update({old: row["profile"] for old, row in DUNGEONS["legacy_loot_aliases"].items()})
        seen = {}
        for name in SOURCE["profiles"]:
            modifier = load(f"qm/loot_modifiers/dungeon_rewards/{name}.json")
            self.assertEqual(modifier["type"], "neoforge:add_table")
            self.assertEqual(modifier["table"], f"qm:dungeon_rewards/{name}")
            routes = table_ids(modifier["conditions"])
            self.assertTrue(routes)
            self.assertEqual(len(routes), len(set(routes)))
            for table in routes:
                self.assertNotIn(table, seen, "One chest must receive one dedicated reward profile")
                self.assertTrue(table.startswith(("dimdungeons:chest/", "dimdungeons:chests/",
                                                  "qm:chest/dimensional_dungeons/")))
                self.assertEqual(expected[table], name)
                self.assertEqual(POLICY["routes"][table], name)
                seen[table] = name
        self.assertEqual(seen, {table: name for table, name in expected.items() if name in SOURCE["profiles"]})
        registry = load("neoforge/loot_modifiers/global_loot_modifiers.json")
        self.assertFalse(registry["replace"])
        self.assertEqual(len(registry["entries"]), len(set(registry["entries"])))
        for name in SOURCE["profiles"]:
            self.assertEqual(registry["entries"].count(f"qm:dungeon_rewards/{name}"), 1)
        for nested in ("chestbooks_1", "chestloot_1", "chestloot_2", "chestloot_3", "chestloot_4", "trophy_1"):
            self.assertNotIn("dimdungeons:chest/" + nested, seen, "Nested loot must not double reward a chest")
        self.assertEqual(len(SOURCE["outer_tables"]), 4)
        for table, native in SOURCE["outer_tables"].items():
            self.assertIn(table, seen)
            outer = load("qm/loot_table/" + table.split(":", 1)[1] + ".json")
            self.assertEqual(outer, {"type": "minecraft:chest", "pools": [{"rolls": 1, "bonus_rolls": 0,
                "entries": [{"type": "minecraft:loot_table", "value": native}]}]})

    def test_actual_gear_and_gems_use_native_unrestricted_tier_weights(self):
        marker = {"function": "minecraft:set_custom_data", "tag": "{qm_dungeon_reward:1b}"}
        for name, profile in SOURCE["profiles"].items():
            gear = load(f"qm/loot_table/dungeon_rewards/gear/{name}.json")
            for entry in gear["pools"][0]["entries"]:
                self.assertEqual(entry["type"], "minecraft:item")
                self.assertTrue(entry["name"].startswith("minecraft:"))
                self.assertIn({"function": "apotheosis:reforge_item"}, entry["functions"])
                self.assertIn(marker, entry["functions"])
                self.assertNotIn("netherite", entry["name"])
                self.assertEqual(len([fn for fn in entry["functions"] if fn["function"] == "apotheosis:reforge_item"]), 1)
            reward = load(f"qm/loot_table/dungeon_rewards/{name}.json")
            gems = [node for node in walk(reward) if node.get("type") == "apotheosis:random_gem"]
            self.assertTrue(gems)
            for gem in gems:
                self.assertNotIn("purities", gem, "Restricted pools can bypass native zero-weight tier boundaries")
                self.assertNotIn("gems", gem, "Let the native dimension/tier registry select eligible gems")
                self.assertIn(marker, gem["functions"])
            for node in walk([gear, reward]):
                self.assertNotIn("rarities", node, "Use the opening player's native rarity weights")
                self.assertNotEqual(node.get("type"), "apotheosis:random_affix_item", "Unbounded affix item pools can import gated mod gear")

    def test_harder_chests_have_real_guaranteed_rewards(self):
        expected = {
            "dungeon_basic": [("mixed", .35)],
            "dungeon_hard": [("mixed", 1.0)],
            "dungeon_vault": [("mixed", 1.0)],
            "dungeon_citadel_supplies": [("mixed", .45)],
            "dungeon_citadel_treasure": [("gear", 1.0), ("gem", 1.0)],
            "dungeon_abyss_supplies": [("mixed", .6)],
            "dungeon_abyss_treasure": [("gear", 1.0), ("gem", 1.0)],
        }
        for name, rewards in expected.items():
            profile = SOURCE["profiles"][name]
            self.assertEqual([(row["kind"], row["chance"]) for row in profile["combat"]], rewards)
            self.assertEqual(profile["limits"]["valuable_keep_chance"], 1.0)
            self.assertEqual(profile["limits"]["combat_rewards"], len(rewards))
            table = load(f"qm/loot_table/dungeon_rewards/{name}.json")
            for index, (_, chance) in enumerate(rewards):
                pool = table["pools"][index]
                self.assertEqual(pool["rolls"], 1)
                self.assertEqual(pool["bonus_rolls"], 0)
                self.assertTrue(pool["entries"])
                self.assertFalse(any(row["type"] == "minecraft:empty" for row in pool["entries"]))
                if chance == 1:
                    self.assertNotIn("conditions", pool, "A guaranteed reward cannot have another random roll")
                else:
                    self.assertEqual(pool["conditions"], [{"condition": "minecraft:random_chance", "chance": chance}])
        self.assertGreater(SOURCE["profiles"]["dungeon_abyss_treasure"]["gem_dust"]["min"],
                           SOURCE["profiles"]["dungeon_citadel_treasure"]["gem_dust"]["min"])

    def test_stage_keys_cannot_skip_the_reviewed_chest_path(self):
        allowed = {
            "dungeon_vault": [(4, .25)],
            "dungeon_citadel_treasure": [(4, .25), (5, .35)],
            "dungeon_abyss_treasure": [(5, .25)],
        }
        for name, profile in SOURCE["profiles"].items():
            self.assertEqual([(row["theme"], row["chance"]) for row in profile.get("keys", [])], allowed.get(name, []))
            table = load(f"qm/loot_table/dungeon_rewards/{name}.json")
            key_pools = [pool for pool in table["pools"] if any(
                node.get("function") == "qm_dungeon_encounters:activate_expedition_key" for node in walk(pool))]
            self.assertEqual(len(key_pools), len(allowed.get(name, [])))
            for pool in key_pools:
                entry = pool["entries"][0]
                self.assertEqual(entry["name"], "dimdungeons:item_portal_key")
                self.assertNotIn("components", entry, "Only native key activation may create destination data")
            if name == "dungeon_vault":
                self.assertEqual(set(table_ids(key_pools)), {
                    "dimdungeons:chest/chestloot_advanced_hard",
                    "dimdungeons:chests/chestloot_advanced_hard",
                })

    def test_no_machine_or_dimension_progression_items_are_added(self):
        blocked = set(POLICY["forbidden_exact"])
        prefixes = tuple(POLICY["forbidden_prefixes"])
        for path in (DATA / "qm/loot_table/dungeon_rewards").rglob("*.json"):
            for entry in walk(json.loads(path.read_text())):
                if entry.get("type") != "minecraft:item":
                    continue
                item = entry["name"]
                self.assertNotIn(item, blocked)
                self.assertFalse(item.startswith(prefixes))
                self.assertTrue(item.startswith(("minecraft:", "apotheosis:", "dimdungeons:")))
                self.assertNotIn(item, {"minecraft:crafter", "minecraft:hopper", "minecraft:piston",
                                        "minecraft:sticky_piston", "minecraft:furnace"})
        self.assertTrue(set(DUNGEONS["protected_items"]) <= set(POLICY["protected_exact"]))

    def test_generator_output_is_current(self):
        spec = importlib.util.spec_from_file_location("dd_combat_generator", ROOT / "scripts/exploration_loot.py")
        generator = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(generator)
        for path, value in generator.outputs().items():
            self.assertEqual(json.loads(path.read_text()), value, str(path))


class PinnedApotheosisLootCodecTests(unittest.TestCase):
    def test_entry_and_function_ids_match_the_pinned_native_registry(self):
        path = ROOT / ".cache/mods/Apotheosis-1.21.1-8.9.0.jar"
        if not path.exists():
            self.skipTest("Download pinned Apotheosis for codec provenance checks")
        lock = json.loads((ROOT / "metadata/mods.lock.json").read_text())["mods"]
        row = next(row for row in lock if row["filename"] == path.name)
        self.assertEqual(hashlib.sha256(path.read_bytes()).hexdigest(), row["sha256"])
        with ZipFile(path) as jar:
            self.assertIn(b"reforge_item", jar.read("dev/shadowsoffire/apotheosis/Apoth$LootFunctions.class"))
            self.assertIn(b"random_gem", jar.read("dev/shadowsoffire/apotheosis/Apoth$LootPoolEntries.class"))
            self.assertIn(b"forLoot", jar.read("dev/shadowsoffire/apotheosis/loot/functions/ContextualLootFunction.class"))
            self.assertIn(b"forLoot", jar.read("dev/shadowsoffire/apotheosis/loot/entry/ContextualLootPoolEntry.class"))


if __name__ == "__main__":
    unittest.main()
