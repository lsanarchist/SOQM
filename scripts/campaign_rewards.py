#!/usr/bin/env python3
"""Compile original SOQM supply pools into native FTB Quests random rewards.

``apply(chapters, locale)`` changes only existing reward objects in memory. The
campaign editor owns chapter and locale writes. ``--write`` writes reward tables
only; no quest progress, player inventory, chapters or translations are touched.
"""

from __future__ import annotations

import argparse
from collections import Counter
from fractions import Fraction
import hashlib
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
QUESTS = ROOT / "overrides/config/ftbquests/quests"
POLICY_PATH = ROOT / "metadata/reward_pools.json"
TABLES_PATH = QUESTS / "reward_tables"
BOOK_QUEST = "514D010000000001"
ID_PATTERN = re.compile(r"[0-9A-F]{16}\Z")

# These are ordinary resources, never machines, manufactured mod components,
# progression trophies, permanent equipment or nested loot rolls. Small XP and
# food prizes join Nether supplies after stage 2; emeralds are finale-only.
SAFE_ITEMS = {
    "minecraft:bread": 0, "minecraft:torch": 0, "minecraft:oak_planks": 0,
    "minecraft:leather": 0, "minecraft:paper": 0, "minecraft:iron_nugget": 0,
    "minecraft:coal": 0, "minecraft:copper_ingot": 0, "minecraft:iron_ingot": 0,
    "minecraft:gold_nugget": 0, "minecraft:redstone": 0, "minecraft:glass": 0,
    "minecraft:string": 0, "minecraft:lapis_lazuli": 0,
    "minecraft:amethyst_shard": 0, "minecraft:sugar": 0,
    "minecraft:arrow": 0, "minecraft:white_wool": 0, "minecraft:brick": 0,
    "minecraft:gold_ingot": 0, "minecraft:sand": 0, "minecraft:gravel": 0,
    "minecraft:cobblestone": 0, "minecraft:clay_ball": 0,
    "minecraft:bone": 0, "minecraft:cooked_beef": 0,
    "minecraft:quartz": 2, "minecraft:glowstone_dust": 2,
    "minecraft:experience_bottle": 2, "minecraft:golden_carrot": 2,
    "minecraft:name_tag": 3,
    "minecraft:emerald": 5,
}


def load_policy() -> dict:
    policy = json.loads(POLICY_PATH.read_text(encoding="utf-8"))
    validate_policy(policy)
    return policy


def validate_policy(policy: dict) -> None:
    if policy.get("schema") != 1 or policy.get("max_items_per_roll") != 4:
        raise ValueError("Unknown reward policy or unreviewed quantity limit")
    pools = policy["pools"]
    if len(pools) != len({pool["key"] for pool in pools}):
        raise ValueError("Duplicate pool key")
    pool_keys = {pool["key"] for pool in pools}
    ids = []
    for pool in pools:
        if not ID_PATTERN.fullmatch(pool["id"]):
            raise ValueError("Invalid reward table ID")
        # Bare JSON numbers are parsed as IntTag by the pinned SNBT reader.
        # Large unsuffixed long IDs would fail Integer.parseInt at game load.
        if not 0x6A510001 <= int(pool["id"], 16) <= 0x6A51FFFF:
            raise ValueError("Table ID outside the reserved JSON-safe range")
        ids.append(pool["id"])
        if pool.get("fallback") not in pool_keys:
            raise ValueError("Unknown fallback pool")
        if type(pool["min_stage"]) is not int or not 0 <= pool["min_stage"] <= 6:
            raise ValueError("Invalid pool stage")
        if not 4 <= len(pool["entries"]) <= 8:
            raise ValueError("Pools need a compact, visible set of distinct prizes")
        if len({entry["item"] for entry in pool["entries"]}) != len(pool["entries"]):
            raise ValueError("Duplicate prize disguises its effective probability")
        for entry in pool["entries"]:
            item = entry["item"]
            if item not in SAFE_ITEMS or SAFE_ITEMS[item] > pool["min_stage"]:
                raise ValueError(f"Unsafe item or premature reward: {item}")
            if type(entry["count"]) is not int or not 1 <= entry["count"] <= 4:
                raise ValueError("A reward must be one small stack of 1–4 items")
            if type(entry["weight"]) is not int or entry["weight"] <= 0:
                raise ValueError("Zero weight auto-grants in FTB; all weights must be positive")
            if set(entry.get("name", {})) != {"uk_ua", "en_us"}:
                raise ValueError("Every prize needs reviewed names for the budget document")
            ids.append(entry_id(pool["key"], item))
        if sum(e["weight"] for e in pool["entries"]) != 100:
            raise ValueError("Weights must total 100 for transparent percentages")
        if expected_iron(pool) > Fraction(2, 5):
            raise ValueError("Pool exceeds 0.4 expected iron ingots per claim")
        for language in ("uk_ua", "en_us"):
            if not pool["title"].get(language):
                raise ValueError("Missing reward pool title")
    if len(ids) != len(set(ids)):
        raise ValueError("Reward table or inner reward ID collision")
    if not set(policy["chapter_pools"].values()) <= pool_keys:
        raise ValueError("Unknown chapter pool")
    by_key = {pool["key"]: pool for pool in pools}
    for pool in pools:
        seen = set()
        current = pool
        while current["min_stage"] > 0:
            if current["key"] in seen:
                raise ValueError("Reward fallback cycle")
            seen.add(current["key"])
            current = by_key[current["fallback"]]
    for qid, milestone in policy["milestones"].items():
        if not ID_PATTERN.fullmatch(qid) or not 0 <= milestone["tier"] <= 6:
            raise ValueError("Invalid milestone reward")


def entry_id(pool_key: str, item_id: str) -> str:
    """Separate stable namespace; does not consume or alter campaign IDs."""
    digest = hashlib.sha256(f"soqm:reward:{pool_key}:{item_id}".encode()).hexdigest()
    return "5A52" + digest[:12].upper()


def quest_stage(quest: dict) -> int:
    stages = [int(tag[6:]) for tag in quest.get("tags", [])
              if re.fullmatch(r"stage_[0-6]", tag)]
    return max(stages, default=0)


def choose_pool(chapter: dict, quest: dict, policy: dict) -> dict:
    pools = {pool["key"]: pool for pool in policy["pools"]}
    filename = chapter.get("filename", "")
    if filename not in policy["chapter_pools"]:
        raise ValueError(f"Review reward theme for new chapter {filename!r}")
    pool = pools[policy["chapter_pools"][filename]]
    stage = quest_stage(quest)
    while stage < pool["min_stage"]:
        pool = pools[pool["fallback"]]
    return pool


def build_tables(policy: dict | None = None) -> list[dict]:
    policy = load_policy() if policy is None else policy
    validate_policy(policy)
    tables = []
    for index, pool in enumerate(policy["pools"]):
        tables.append({
            "id": pool["id"], "order_index": index, "loot_size": 1,
            "empty_weight": 0, "hide_tooltip": False, "use_title": True,
            "rewards": [{
                "id": entry_id(pool["key"], entry["item"]),
                "type": "item", "item": {"id": entry["item"], "count": 1},
                "count": entry["count"], "weight": entry["weight"],
                "random_bonus": 0, "team_reward": True, "auto": "disabled",
            } for entry in pool["entries"]],
        })
    return tables


def locale_entries(language: str = "uk_ua", policy: dict | None = None) -> dict:
    if language not in {"uk_ua", "en_us"}:
        raise ValueError(f"Unreviewed reward locale: {language}")
    policy = load_policy() if policy is None else policy
    return {f"reward_table.{pool['id']}.title": pool["title"][language]
            for pool in policy["pools"]}


def persistent_ids(chapters: list[dict]) -> list[str]:
    ids = []
    for chapter in chapters:
        ids.append(chapter["id"])
        ids.extend(image["id"] for image in chapter.get("images", []) if "id" in image)
        for quest in chapter["quests"]:
            ids.append(quest["id"])
            ids.extend(t["id"] for t in quest["tasks"])
            ids.extend(r["id"] for r in quest["rewards"])
    return ids


def apply(chapters: list[dict], locale: dict) -> dict:
    """Replace excessive rewards; preserve every existing persistent identity.

    Call after stage corrections so a stage-dependent theme chooses its fallback
    from the updated quest. Repeat calls are idempotent. The personal introductory
    book is preserved exactly; selected milestones retain one gated Mayhem cube.
    """
    policy = load_policy()
    before_ids = persistent_ids(chapters)
    tables = build_tables(policy)
    table_ids = [table["id"] for table in tables]
    table_ids += [reward["id"] for table in tables for reward in table["rewards"]]
    if set(table_ids) & set(before_ids):
        raise ValueError("New table IDs collide with saved campaign objects")
    usage = Counter()
    books = cubes = total = 0
    for chapter in chapters:
        for quest in chapter["quests"]:
            pool = choose_pool(chapter, quest, policy)
            for reward in quest["rewards"]:
                total += 1
                is_book = (quest["id"] == BOOK_QUEST and reward.get("type") == "item"
                           and reward.get("item", {}).get("id") == "ftbquests:book")
                if is_book:
                    if reward.get("team_reward", True):
                        raise ValueError("Introductory quest book must remain personal")
                    books += 1
                    continue
                # Preserve the fields involved in one-time claim identity/behavior.
                kept = {key: reward[key] for key in (
                    "id", "team_reward", "auto", "exclude_from_claim_all",
                    "ignore_reward_blocking", "hide_tooltip"
                ) if key in reward}
                if kept.get("auto", "disabled") != "disabled":
                    raise ValueError("Review non-manual reward before migration")
                reward.clear()
                reward.update(kept)
                reward.setdefault("team_reward", True)
                reward.setdefault("auto", "disabled")
                milestone = policy["milestones"].get(quest["id"])
                if milestone:
                    reward.update({
                        "type": "item", "item": {
                            "id": f"kubejs:mayhem_cube_t{milestone['tier']}", "count": 1,
                        }, "count": 1, "random_bonus": 0,
                    })
                    cubes += 1
                else:
                    reward.update({"type": "random", "table_id": int(pool["id"], 16)})
                    usage[pool["key"]] += 1
    if persistent_ids(chapters) != before_ids:
        raise AssertionError("Reward migration changed persistent campaign IDs")
    locale.update(locale_entries("uk_ua", policy))
    by_key = {pool["key"]: pool for pool in policy["pools"]}
    iron = sum((expected_iron(by_key[key]) * count for key, count in usage.items()), Fraction())
    units = sum((expected_units(by_key[key]) * count for key, count in usage.items()), Fraction())
    return {
        "reward_objects": total, "random_rewards": sum(usage.values()),
        "milestone_cubes": cubes, "personal_books": books,
        "tables": len(policy["pools"]), "pool_usage": dict(sorted(usage.items())),
        "expected_supply_items": round(float(units), 3),
        "expected_supply_iron_ingots": round(float(iron), 3),
        "stage_budgets": stage_budgets(chapters, policy),
        "expectation_scope": "Ordinary supply pools only; fixed books and Mayhem event loot excluded.",
    }


def expected_units(pool: dict) -> Fraction:
    return sum((Fraction(entry["weight"], 100) * entry["count"]
                for entry in pool["entries"]), Fraction())


def expected_iron(pool: dict) -> Fraction:
    value = {"minecraft:iron_ingot": Fraction(1), "minecraft:iron_nugget": Fraction(1, 9)}
    return sum((Fraction(entry["weight"], 100) * entry["count"] * value.get(entry["item"], 0)
                for entry in pool["entries"]), Fraction())


def stage_budgets(chapters: list[dict], policy: dict | None = None) -> dict:
    """Exact weighted totals, grouped by the corrected quest stage, not hours.

    Quantities measure direct supplies only. An iron-equivalent count includes
    nuggets at 1/9; it is not a universal item price or an estimate of Mayhem loot.
    """
    policy = load_policy() if policy is None else policy
    pools = {int(pool["id"], 16): pool for pool in policy["pools"]}
    totals = {band: Counter() for band in ("early_0_1", "mid_2_3", "late_4_6")}
    special = {
        "minecraft:golden_carrot": "expected_golden_carrots",
        "minecraft:experience_bottle": "expected_xp_bottles",
        "minecraft:name_tag": "expected_name_tags",
        "minecraft:emerald": "expected_emeralds",
    }
    for chapter in chapters:
        for quest in chapter["quests"]:
            stage = quest_stage(quest)
            band = "early_0_1" if stage <= 1 else "mid_2_3" if stage <= 3 else "late_4_6"
            values = totals[band]
            for reward in quest["rewards"]:
                if reward["type"] != "random":
                    continue
                pool = pools[reward["table_id"]]
                values["claims"] += 1
                values["expected_supply_items"] += expected_units(pool)
                values["expected_iron_ingots"] += expected_iron(pool)
                for entry in pool["entries"]:
                    if entry["item"] in special:
                        values[special[entry["item"]]] += Fraction(entry["weight"] * entry["count"], 100)
    keys = ["claims", "expected_supply_items", "expected_iron_ingots", *special.values()]
    return {band: {key: values[key] if key == "claims" else round(float(values[key]), 3)
                   for key in keys} for band, values in totals.items()}


def expected_files(policy: dict | None = None) -> dict[str, str]:
    return {table["id"] + ".snbt": json.dumps(table, ensure_ascii=False, indent=2) + "\n"
            for table in build_tables(policy)}


def write_tables(directory: Path = TABLES_PATH) -> int:
    directory.mkdir(parents=True, exist_ok=True)
    files = expected_files()
    stale = {path.name for path in directory.glob("000000006A51*.snbt")} - set(files)
    if stale:
        raise ValueError(f"Review stale table IDs before deleting: {sorted(stale)}")
    for name, content in files.items():
        (directory / name).write_text(content, encoding="utf-8")
    return len(files)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--write", action="store_true", help="write native reward tables only")
    parser.add_argument("--check", action="store_true", help="verify committed native reward tables")
    args = parser.parse_args()
    if args.write and args.check:
        parser.error("Choose --write or --check")
    files = expected_files()
    if args.write:
        write_tables()
    elif args.check:
        stale = [name for name, content in files.items()
                 if not (TABLES_PATH / name).exists()
                 or (TABLES_PATH / name).read_text(encoding="utf-8") != content]
        stale.extend(sorted({path.name for path in TABLES_PATH.glob("000000006A51*.snbt")} - set(files)))
        if stale:
            parser.exit(1, f"Reward tables stale: {stale}\n")
    print(json.dumps({"tables": len(files), "mode": "write" if args.write else "check" if args.check else "inspect"}))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
