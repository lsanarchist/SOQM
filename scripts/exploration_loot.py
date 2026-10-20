#!/usr/bin/env python3
"""Build bounded exploration additions and their native first-fill policy."""
from __future__ import annotations

import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "metadata/exploration_loot.json"
POLICY = ROOT / "src/compat/chest-balance/resources/qm_chest_policy.json"


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


def outputs():
    source = json.loads(SOURCE.read_text(encoding="utf-8"))
    policy = json.loads(POLICY.read_text(encoding="utf-8"))
    policy["schema"] = 2
    policy["version"] = source["version"]
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
