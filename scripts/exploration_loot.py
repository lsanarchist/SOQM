#!/usr/bin/env python3
"""Build bounded exploration additions and their native first-fill policy."""
from __future__ import annotations

import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "metadata/exploration_loot.json"
POLICY = ROOT / "src/compat/chest-balance/resources/qm_chest_policy.json"
DUNGEONS = ROOT / "metadata/dimensional_dungeons.json"
DUNGEON_LOOT = ROOT / "metadata/dungeon_loot.json"
DATA = ROOT / "overrides/kubejs/data"


def is_dungeon_table(table):
    return table.startswith(("dimdungeons:", "qm:chest/dimensional_dungeons/"))


def item_entry(row):
    item, weight, minimum, maximum = row
    count = minimum if minimum == maximum else {
        "type": "minecraft:uniform", "min": minimum, "max": maximum,
    }
    return {"type": "minecraft:item", "name": item, "weight": weight, "quality": 0,
            "functions": [{"function": "minecraft:set_count", "add": False, "count": count}]}


def pool(profile, kind, rows, chance=1):
    result = {"name": f"qm_exploration_{profile}_{kind}", "rolls": 1, "bonus_rolls": 0,
              "entries": [item_entry(row) for row in rows]}
    if chance < 1:
        result["conditions"] = [{"condition": "minecraft:random_chance", "chance": chance}]
    return result


def dungeon_supply_entry(row):
    result = item_entry(row[:4])
    if len(row) == 5:
        result["functions"].append({"function": "minecraft:set_potion", "id": row[4]})
    return result


def dungeon_reward_marker(source):
    # SetCustomDataFunction merges this owned key instead of replacing a mod's
    # existing custom data. The first-fill compactor removes only this key.
    return {"function": "minecraft:set_custom_data", "tag": "{" + source["reward_marker"] + ":1b}"}


def dungeon_pool(name, kind, entries, chance=1, rolls=1):
    result = {"name": f"qm_{name}_{kind}", "rolls": rolls, "bonus_rolls": 0, "entries": entries}
    if chance < 1:
        result["conditions"] = [{"condition": "minecraft:random_chance", "chance": chance}]
    return result


def dungeon_outputs(source, dungeon_routes, policy):
    result = {}
    marker = dungeon_reward_marker(source)
    for table, native_table in source["outer_tables"].items():
        if not table.startswith("qm:chest/dimensional_dungeons/") or ".." in table or table not in dungeon_routes:
            raise ValueError(f"Unscoped authored dungeon chest: {table}")
        if not native_table.startswith("dimdungeons:chest/"):
            raise ValueError(f"Authored dungeon chest lost its native source loot: {table}")
        result[DATA / "qm/loot_table" / (table.split(":", 1)[1] + ".json")] = {
            "type": "minecraft:chest", "pools": [{"rolls": 1, "bonus_rolls": 0,
                "entries": [{"type": "minecraft:loot_table", "value": native_table}]}],
        }
    for name, profile in source["profiles"].items():
        routes = sorted(table for table, route in dungeon_routes.items() if route == name)
        if not routes:
            raise ValueError(f"Dungeon combat profile has no outer chest route: {name}")
        gear_table = f"qm:dungeon_rewards/gear/{name}"
        gear_entries = []
        for item, weight in source["gear_pools"][profile["gear_pool"]]:
            gear_entries.append({"type": "minecraft:item", "name": item, "weight": weight,
                                 "functions": [
                                     {"function": "minecraft:enchant_with_levels",
                                      "levels": profile["enchant_level"],
                                      "options": "#minecraft:on_random_loot"},
                                     {"function": "apotheosis:reforge_item"}, marker,
                                 ]})
        # No explicit rarities/purities: native 8.9.0 GenContext.forLoot uses
        # the opening player, and the native tiered weights remain in charge.
        result[DATA / f"qm/loot_table/dungeon_rewards/gear/{name}.json"] = {
            "type": "minecraft:chest", "pools": [dungeon_pool(name, "gear", gear_entries)]}
        pools = []
        for index, reward in enumerate(profile["combat"]):
            kind = reward["kind"]
            entries = []
            if kind in ("gear", "mixed"):
                entries.append({"type": "minecraft:loot_table", "value": gear_table,
                                "weight": 45 if kind == "mixed" else 1})
            if kind in ("gem", "mixed"):
                entries.append({"type": "apotheosis:random_gem",
                                "weight": 55 if kind == "mixed" else 1, "functions": [marker]})
            if not entries:
                raise ValueError(f"Unknown dungeon combat reward: {name}/{kind}")
            pools.append(dungeon_pool(name, f"combat_{index}", entries, reward["chance"]))
        supply_rows = source["supply_pools"][profile["supplies"]["pool"]]
        pools.append(dungeon_pool(name, "supplies", [dungeon_supply_entry(row) for row in supply_rows],
                                  rolls=profile["supplies"]["rolls"]))
        dust = profile["gem_dust"]
        pools.append(dungeon_pool(name, "gem_dust", [item_entry(
            ["apotheosis:gem_dust", 1, dust["min"], dust["max"]])], dust["chance"]))
        for index, key in enumerate(profile.get("keys", [])):
            key_pool = dungeon_pool(name, f"key_{index}", [{
                "type": "minecraft:item", "name": "dimdungeons:item_portal_key",
                "functions": [{"function": "qm_dungeon_encounters:activate_expedition_key",
                               "theme": key["theme"]}],
            }], key["chance"])
            if "tables" in key:
                if not set(key["tables"]) <= set(routes):
                    raise ValueError(f"A key reward escapes its reviewed chest routes: {name}")
                key_pool.setdefault("conditions", []).append({"condition": "minecraft:any_of", "terms": [
                    {"condition": "neoforge:loot_table_id", "loot_table_id": table} for table in key["tables"]
                ]})
            pools.append(key_pool)
        result[DATA / f"qm/loot_table/dungeon_rewards/{name}.json"] = {"type": "minecraft:chest", "pools": pools}
        result[DATA / f"qm/loot_modifiers/dungeon_rewards/{name}.json"] = {
            "type": "neoforge:add_table", "table": f"qm:dungeon_rewards/{name}",
            "conditions": [{"condition": "minecraft:any_of", "terms": [
                {"condition": "neoforge:loot_table_id", "loot_table_id": table} for table in routes
            ]}],
        }
        policy["profiles"][name] = profile["limits"]
        policy["profile_items"][name] = sorted({row[0] for row in supply_rows} | {"apotheosis:gem_dust"})
        policy["find_items"][name] = []

    global_path = DATA / "neoforge/loot_modifiers/global_loot_modifiers.json"
    global_modifiers = json.loads(global_path.read_text(encoding="utf-8"))
    global_modifiers["entries"] = [entry for entry in global_modifiers["entries"]
                                   if not entry.startswith("qm:dungeon_rewards/")]
    global_modifiers["entries"].extend(f"qm:dungeon_rewards/{name}" for name in source["profiles"])
    result[global_path] = global_modifiers
    return result


def outputs():
    source = json.loads(SOURCE.read_text(encoding="utf-8"))
    policy = json.loads(POLICY.read_text(encoding="utf-8"))
    dungeons = json.loads(DUNGEONS.read_text(encoding="utf-8"))
    dungeon_loot = json.loads(DUNGEON_LOOT.read_text(encoding="utf-8"))
    dungeon_routes = dict(dungeons["loot_routes"])
    dungeon_routes.update({old: alias["profile"] for old, alias in dungeons["legacy_loot_aliases"].items()})
    # Exact outer chest fills only. Nested books, trophies and material tables
    # must not independently roll additional exploration supplies.
    policy["routes"] = {table: profile for table, profile in policy["routes"].items()
                        if not is_dungeon_table(table)}
    for table, profile in dungeon_routes.items():
        if profile not in source["profiles"] and profile not in dungeon_loot["profiles"]:
            raise ValueError(f"Unknown dungeon chest profile: {table} -> {profile}")
        policy["routes"][table] = profile
    policy["routes"] = dict(sorted(policy["routes"].items()))
    existing_protected = [item for item in policy["protected_exact"] if not item.startswith("dimdungeons:")]
    policy["protected_exact"] = existing_protected + sorted(dungeons["protected_items"])
    policy["schema"] = 3
    policy["version"] = dungeon_loot["version"]
    policy["dungeon_reward_marker"] = dungeon_loot["reward_marker"]
    for field in ("profiles", "profile_items"):
        policy[field] = {key: value for key, value in policy[field].items() if not key.startswith("dungeon_")}
    policy["find_items"] = {}
    result = {}
    for name, profile in source["profiles"].items():
        policy["profiles"][name] = profile["limits"]
        policy["profile_items"][name] = sorted({row[0] for key in ("materials", "utility", "finds")
                                                  for row in profile[key]} | {"chancecubes:chance_cube"})
        policy["find_items"][name] = sorted(row[0] for row in profile["finds"])
        result[ROOT / f"overrides/kubejs/data/qm/loot_table/exploration/{name}.json"] = {
            "type": "minecraft:chest", "pools": [
                pool(name, "materials", profile["materials"]),
                pool(name, "utility", profile["utility"], profile["utility_chance"]),
                pool(name, "pleasant_find", profile["finds"], profile["find_chance"]),
                pool(name, "rare_cube", [["chancecubes:chance_cube", 10, 1, 1]], profile["cube_chance"]),
            ]}
        modifier_path = ROOT / f"overrides/kubejs/data/qm/loot_modifiers/exploration/{name}.json"
        modifier = json.loads(modifier_path.read_text(encoding="utf-8"))
        terms = modifier["conditions"][0]["terms"]
        terms = [term for term in terms if not is_dungeon_table(term.get("loot_table_id", ""))]
        terms.extend({"condition": "neoforge:loot_table_id", "loot_table_id": table}
                     for table, route in sorted(dungeon_routes.items()) if route == name)
        # Existing entries include dimension-qualified all_of conditions.
        # Keep those intact rather than flattening their original scope.
        modifier["conditions"][0]["terms"] = terms
        result[modifier_path] = modifier
    result.update(dungeon_outputs(dungeon_loot, dungeon_routes, policy))
    policy["curated_items"] = sorted({item for items in policy["profile_items"].values() for item in items})
    result[POLICY] = policy
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    stale = []
    for path, value in outputs().items():
        content = json.dumps(value, ensure_ascii=False, indent=2) + "\n"
        if args.check:
            if not path.is_file() or path.read_text(encoding="utf-8") != content:
                stale.append(path.relative_to(ROOT).as_posix())
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(content, encoding="utf-8")
    if stale:
        raise SystemExit("Stale exploration loot: " + ", ".join(stale))
    print("Exploration loot and native policy are synchronized.")


if __name__ == "__main__":
    main()
