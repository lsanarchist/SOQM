"""Validate the finite dungeon pools, native key preservation and observed loot repairs."""
from __future__ import annotations

import gzip
import hashlib
import importlib.util
import json
from pathlib import Path
import re
import struct
import tomllib
import unittest
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
SOURCE = json.loads((ROOT / "metadata/dimensional_dungeons.json").read_text())
DATA = ROOT / "overrides/kubejs/data"
POLICY = json.loads((ROOT / "src/compat/chest-balance/resources/qm_chest_policy.json").read_text())
VANILLA_ENEMIES = {
    "minecraft:zombie", "minecraft:skeleton", "minecraft:vindicator", "minecraft:wither_skeleton",
    "minecraft:husk", "minecraft:drowned", "minecraft:witch",
}


def pools():
    yield from SOURCE["enemy_sets"].values()
    for theme in SOURCE["themes"].values():
        yield theme["enemy_set_1"]
        yield theme["enemy_set_2"]


def item_ids(value):
    if isinstance(value, dict):
        if value.get("type") in {"minecraft:item", "item"}:
            yield value["name"]
        for child in value.values():
            yield from item_ids(child)
    elif isinstance(value, list):
        for child in value:
            yield from item_ids(child)


class DungeonPolicyTests(unittest.TestCase):
    def test_every_pool_is_finite_nonempty_and_uses_reviewed_hostiles(self):
        seen = set()
        allowed = VANILLA_ENEMIES | set(SOURCE["modded_enemies"])
        for pool in pools():
            self.assertTrue(1 <= len(pool) <= 8)
            self.assertEqual(len(pool), len(set(pool)), "Repeated IDs silently alter spawn weights")
            self.assertTrue(set(pool) <= allowed)
            self.assertTrue(any(not enemy.startswith("minecraft:") for enemy in pool))
            seen.update(pool)
        self.assertTrue(set(SOURCE["modded_enemies"]) <= seen)
        for mob, evidence in SOURCE["modded_enemies"].items():
            self.assertNotIn("/boss/", evidence["class"])
            if mob.startswith("divinerpg:"):
                self.assertEqual(evidence["home"], "minecraft:overworld")
                self.assertIn("/vanilla/overworld/", evidence["class"])
        self.assertEqual(set(SOURCE["themes"]), {"2", "3"}, "Keep the Nether Fortress's native fire-safe pools")

    def test_native_config_is_generated_without_new_worldgen_or_extra_hp_multiplier(self):
        spec = importlib.util.spec_from_file_location("dd_generator", ROOT / "scripts/dimensional_dungeons.py")
        generator = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(generator)
        for path, expected in generator.outputs().items():
            self.assertEqual(path.read_text(), expected, str(path))
        self.assertFalse((ROOT / "overrides/config/dimdungeons-common.toml").exists(),
                         "DD 210 does not load the unversioned default config filename")
        config = tomllib.loads((ROOT / "overrides/config" / SOURCE["common_config"]).read_text())
        self.assertEqual(set(config), {"enemySets", "dungeonTheme2", "dungeonTheme3"})
        for field, enemies in SOURCE["enemy_sets"].items():
            self.assertEqual(config["enemySets"][field], enemies)
        self.assertEqual(config["enemySets"]["basicEnemyHealthScaling"], 1.0)
        self.assertEqual(config["enemySets"]["advancedEnemyHealthScaling"], 1.0)
        self.assertEqual(config["enemySets"]["numberOfThemes"], 3)
        for number in SOURCE["themes"]:
            self.assertEqual(config[f"dungeonTheme{number}"][f"themeEnemyHealthScaling{number}"], 1.0)

    def test_only_outer_chest_fills_receive_a_single_bounded_profile(self):
        expected = dict(SOURCE["loot_routes"])
        expected.update({old: row["profile"] for old, row in SOURCE["legacy_loot_aliases"].items()})
        actual = {table: profile for table, profile in POLICY["routes"].items() if table.startswith("dimdungeons:")}
        self.assertEqual(actual, expected)
        observed = {}
        for path in (DATA / "qm/loot_modifiers/exploration").glob("*.json"):
            modifier = json.loads(path.read_text())
            for term in modifier["conditions"][0]["terms"]:
                table = term.get("loot_table_id", "")
                if table.startswith("dimdungeons:"):
                    self.assertNotIn(table, observed, "A chest must not roll two supply profiles")
                    observed[table] = modifier["table"].split("/")[-1]
        self.assertEqual(observed, expected)
        for nested in ["chestloot_1", "chestloot_2", "chestloot_3", "chestloot_4", "trophy_1", "chestbooks_1", "kit_redstone"]:
            self.assertNotIn("dimdungeons:chest/" + nested, actual)
        self.assertTrue(set(SOURCE["protected_items"]) <= set(POLICY["protected_exact"]))
        for item in SOURCE["protected_items"]:
            self.assertNotIn(item, POLICY["forbidden_exact"], "Forbidden filtering runs before protection")
            self.assertFalse(item.startswith(tuple(POLICY["forbidden_prefixes"])))

    def test_workshop_kits_do_not_bypass_expensive_recipes(self):
        blocked = {"minecraft:piston", "minecraft:sticky_piston", "minecraft:hopper", "minecraft:crafter", "minecraft:furnace"}
        for table, kit in SOURCE["kit_overrides"].items():
            path = DATA / "dimdungeons/loot_table" / (table.split(":", 1)[1] + ".json")
            native = json.loads(path.read_text())
            self.assertFalse(blocked.intersection(item_ids(native)))
            self.assertEqual(native["pools"][0]["rolls"], 3)
            self.assertEqual(native["pools"][0]["bonus_rolls"], 0)
            for item, weight, minimum, maximum in kit["items"]:
                self.assertTrue(1 <= minimum <= maximum <= 4)
                self.assertGreater(weight, 0)
                self.assertNotIn(item, POLICY["forbidden_exact"])
                self.assertFalse(item.startswith(tuple(POLICY["forbidden_prefixes"])))


class PinnedDungeonArtifactTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.jars = {
            "dimdungeons": ROOT / ".cache/mods/dimdungeons-210-neoforge-1.21.1.jar",
            "divinerpg": ROOT / ".cache/mods/divinerpg-1.10.10.jar",
            "twilightforest": ROOT / ".cache/mods/twilightforest-1.21.1-4.8.3345-universal.jar",
        }
        if not all(path.is_file() for path in cls.jars.values()):
            raise unittest.SkipTest("Download the pinned mods to run native artifact checks")
        lock = json.loads((ROOT / "metadata/mods.lock.json").read_text())["mods"]
        for name, path in cls.jars.items():
            row = next((row for row in lock if row["filename"] == path.name), None)
            if row is None:
                raise AssertionError(f"Unpinned dungeon artifact: {path.name}")
            if row["version"] != SOURCE["pinned_mods"][name]:
                raise AssertionError(f"Dungeon policy uses the wrong version of {name}")
            if hashlib.sha256(path.read_bytes()).hexdigest() != row["sha256"]:
                raise AssertionError(f"Dungeon artifact hash mismatch: {name}")

    def test_all_curated_ids_have_native_classes_names_and_loot(self):
        for namespace in ("divinerpg", "twilightforest"):
            with ZipFile(self.jars[namespace]) as jar:
                names = set(jar.namelist())
                lang = json.loads(jar.read(f"assets/{namespace}/lang/en_us.json"))
                for mob, evidence in SOURCE["modded_enemies"].items():
                    mod, path = mob.split(":")
                    if mod != namespace:
                        continue
                    self.assertIn(evidence["class"] + ".class", names, mob)
                    self.assertIn(f"entity.{mod}.{path}", lang, mob)
                    self.assertIn(f"data/{mod}/loot_table/entities/{path}.json", names, mob)

    def test_observed_stale_room_loot_ids_have_forwarding_tables(self):
        unresolved = set()
        with ZipFile(self.jars["dimdungeons"]) as jar:
            native = set(jar.namelist())
            for name in native:
                if not name.startswith("data/dimdungeons/structure/") or not name.endswith(".nbt"):
                    continue
                raw = gzip.decompress(jar.read(name))
                for match in re.finditer(b"\x08\x00\x09LootTable", raw):
                    size = struct.unpack_from(">H", raw, match.end())[0]
                    start = match.end() + 2
                    table = raw[start:start + size].decode("utf-8")
                    if table.startswith("dimdungeons:"):
                        path = "data/dimdungeons/loot_table/" + table.split(":", 1)[1] + ".json"
                        if path not in native:
                            unresolved.add(table)
            self.assertEqual(unresolved, set(SOURCE["legacy_loot_aliases"]))
            for old, alias in SOURCE["legacy_loot_aliases"].items():
                target = "data/dimdungeons/loot_table/" + alias["target"].split(":", 1)[1] + ".json"
                self.assertIn(target, native)
                path = DATA / "dimdungeons/loot_table" / (old.split(":", 1)[1] + ".json")
                repaired = json.loads(path.read_text())
                self.assertEqual(repaired["pools"][0]["entries"], [{"type": "minecraft:loot_table", "value": alias["target"]}])

    def test_native_treasures_keep_their_keys_utilities_and_trophies(self):
        protected = set(POLICY["protected_exact"])
        with ZipFile(self.jars["dimdungeons"]) as jar:
            # ItemBuildKey's actual registration is item_key_build. Check the
            # pinned registrars rather than guessing IDs from Java class names.
            native_registrars = b"".join(jar.read(name) for name in (
                "com/catastrophe573/dimdungeons/item/ItemRegistrar.class",
                "com/catastrophe573/dimdungeons/block/BlockRegistrar.class",
                "com/catastrophe573/dimdungeons/block/BlockPortalCrown.class",
            ))
            for item in SOURCE["protected_items"]:
                path = item.split(":", 1)[1].encode()
                self.assertIn(struct.pack(">H", len(path)) + path, native_registrars,
                              f"Protected item has no matching native registration: {item}")
            for name in jar.namelist():
                if name.startswith("data/dimdungeons/loot_table/chest/") and name.endswith(".json"):
                    native_items = item_ids(json.loads(jar.read(name)))
                    for item in native_items:
                        if item.startswith("dimdungeons:"):
                            self.assertIn(item, protected, f"Native dungeon utility may be discarded: {item}")
            config_class = jar.read("com/catastrophe573/dimdungeons/DungeonConfig$CommonConfig.class")
            for field in SOURCE["enemy_sets"]:
                self.assertIn(field.encode(), config_class)
            self.assertIn(b"themeEnemySet1_", config_class)
            self.assertIn(b"themeEnemySet2_", config_class)
            main_class = jar.read("com/catastrophe573/dimdungeons/DimDungeons.class")
            self.assertIn(SOURCE["common_config"].encode(), main_class,
                          "The versioned native config filename must match registerConfig")


if __name__ == "__main__":
    unittest.main()
