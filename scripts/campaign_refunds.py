"""Validate guaranteed refunds against the exact authored crafting recipes.

The existing reward slot is retained. This catalogue only describes a fixed
payload and the exact output whose real manufacture permits that personal claim.
"""
from __future__ import annotations

from collections import Counter, defaultdict
import hashlib
import json
from pathlib import Path
import re

import campaign_progression

ROOT = Path(__file__).resolve().parents[1]
POLICY_PATH = ROOT / "metadata/recipe_refunds.json"
TAG = "soqm:recipe_refund"
ITEM = re.compile(r"[a-z0-9_.-]+:[a-z0-9_./-]+\Z")
IDENT = re.compile(r"[0-9A-F]{16}\Z")
GRID_TYPES = {"minecraft:crafting_shaped", "minecraft:crafting_shapeless", "mekanism:mek_data",
              "enderio:shaped_entity_storage"}


def recipe_hash(recipe: dict) -> str:
    return hashlib.sha256(json.dumps(recipe, ensure_ascii=False, sort_keys=True,
                                    separators=(",", ":")).encode("utf-8")).hexdigest()


def recipe_body(recipe: dict) -> dict:
    if recipe.get("type") == "mcjtylib:copy_components":
        return recipe["recipe"]
    return recipe


def recipe_output(recipe: dict) -> str | None:
    return campaign_progression.recipe_output(recipe_body(recipe))


def direct_items(recipe: dict) -> Counter:
    """Count consumed grid positions; a tag or alternative is not an exact item."""
    recipe = recipe_body(recipe)
    if recipe.get("type") not in GRID_TYPES:
        raise ValueError("Refunds need an observed crafting-grid recipe")
    if "pattern" in recipe:
        symbols = Counter("".join(recipe["pattern"]))
        ingredients = [(value, symbols[symbol]) for symbol, value in recipe["key"].items()]
    elif "ingredients" in recipe:
        ingredients = [(value, 1) for value in recipe["ingredients"]]
    else:
        raise ValueError("Recipe has no reviewed grid ingredients")
    result = Counter()
    for ingredient, count in ingredients:
        if isinstance(ingredient, dict) and set(ingredient) == {"item"}:
            result[ingredient["item"]] += count
    return result


def output_count(recipe: dict) -> int:
    recipe = recipe_body(recipe)
    output = recipe.get("result", recipe.get("output"))
    return output.get("count", 1) if isinstance(output, dict) else 1


def valid_item(item: str) -> bool:
    return isinstance(item, str) and bool(ITEM.fullmatch(item)) and item != "minecraft:air" and not any(
        word in item for word in ("plastic", "hdpe", "mayhem_cube", "ironchest:"))


def load(chapters: list[dict] | None = None) -> dict[str, dict]:
    policy = json.loads(POLICY_PATH.read_text(encoding="utf-8"))
    validate(policy, chapters)
    return {row["quest_id"]: row for row in policy["refunds"]}


def validate(policy: dict, chapters: list[dict] | None = None,
             recipes: list[dict] | None = None) -> None:
    if policy.get("schema") != 1 or not isinstance(policy.get("refunds"), list):
        raise ValueError("Expected recipe-refund catalogue schema 1")
    names = policy.get("ingredients", {})
    for item, name in names.items():
        if not valid_item(item) or set(name) != {"uk_ua", "en_us"} or not all(name.values()):
            raise ValueError("Reviewed refund ingredients need exact IDs and bilingual names")
    by_output = defaultdict(list)
    for row in campaign_progression.recipe_rows() if recipes is None else recipes:
        recipe = row.get("recipe", row)
        output = recipe_output(recipe)
        if output:
            by_output[output].append(row)
    seen_quests, seen_outputs, used_ingredients = set(), set(), set()
    by_quest = {q["id"]: q for c in chapters for q in c["quests"]} if chapters is not None else None
    for refund in policy["refunds"]:
        qid, output = refund["quest_id"], refund["output"]
        if not IDENT.fullmatch(qid) or qid in seen_quests or not valid_item(output) or output in seen_outputs:
            raise ValueError("Use one refund slot per crafted output and primary quest")
        seen_quests.add(qid)
        seen_outputs.add(output)
        if set(refund.get("output_name", {})) != {"uk_ua", "en_us"} or not all(refund["output_name"].values()):
            raise ValueError("Refund output needs a bilingual display name")
        rows = by_output[output]
        evidence = refund["recipes"]
        if not rows or {r["id"] for r in rows} != {r["id"] for r in evidence} or len(rows) != len(evidence):
            raise ValueError(f"Refund must review every authored alternative: {output}")
        by_id = {r["id"]: r for r in rows}
        costs = []
        for proof in evidence:
            row = by_id[proof["id"]]
            recipe = row["recipe"]
            if row["source"] != proof["source"] or recipe_hash(recipe) != proof["sha256"]:
                raise ValueError(f"Refund recipe changed; review its material cost: {proof['id']}")
            if output_count(recipe) != 1:
                raise ValueError("Refund output must be one unit per manufacture")
            costs.append(direct_items(recipe))
        if "world_upgrade" in refund:
            upgrade = refund["world_upgrade"]
            if (output, upgrade.get("from"), upgrade.get("item")) != (
                "ironfurnaces:netherite_furnace", "ironfurnaces:obsidian_furnace", "ironfurnaces:upgrade_netherite"
            ):
                raise ValueError("Unreviewed native world-upgrade manufacture route")
            upgrade_rows = by_output[upgrade["item"]]
            if {row["id"] for row in upgrade_rows} != {row["id"] for row in upgrade["recipes"]} or not upgrade_rows:
                raise ValueError("Refund must review every authored world-upgrade recipe")
            upgrade_by_id = {row["id"]: row for row in upgrade_rows}
            for proof in upgrade["recipes"]:
                row = upgrade_by_id[proof["id"]]
                if (row["source"] != proof["source"] or recipe_hash(row["recipe"]) != proof["sha256"]
                        or output_count(row["recipe"]) != 1):
                    raise ValueError("World-upgrade recipe changed; review its material cost")
                costs.append(direct_items(row["recipe"]))
        if not 1 <= len(refund["items"]) <= 4 or len({p["item"] for p in refund["items"]}) != len(refund["items"]):
            raise ValueError("Refund needs 1–4 distinct guaranteed stacks")
        for prize in refund["items"]:
            item, count, minimum = prize["item"], prize["count"], prize["minimum_consumed"]
            if item not in names or item == output or type(count) is not int or not 1 <= count <= 16:
                raise ValueError("Refund must use reviewed expensive ingredients, not the crafted output")
            actual = min(cost[item] for cost in costs)
            if type(minimum) is not int or actual != minimum or not 0 < count <= actual:
                raise ValueError(f"Refund exceeds the exact common ingredient cost: {output} / {item}")
            if prize["basis"] == "partial_ingredient":
                if count * 2 > actual:
                    raise ValueError("Repeated-ingredient refunds return at most half that ingredient")
            elif prize["basis"] == "one_component":
                # A single indivisible processor/secondary assembly can be one
                # component of a costly recipe. Require two other authored
                # campaign components to remain consumed in every alternative.
                if count != 1 or actual != 1 or any(
                    len({other for other in cost if other != item and other.startswith("kubejs:")}) < 2
                    for cost in costs
                ):
                    raise ValueError("A whole-component return needs a reviewed costly multi-component recipe")
            else:
                raise ValueError("Unknown refund budget basis")
            used_ingredients.add(item)
        if by_quest is not None:
            quest = by_quest.get(qid)
            if quest is None or len(quest.get("rewards", [])) != 1:
                raise ValueError(f"Refund needs its existing single personal reward slot: {qid}")
            items = {task.get("item", {}).get("id") for task in quest["tasks"] if task.get("type") == "item"}
            if output not in items:
                raise ValueError(f"Refund output is absent from its actual quest objective: {qid}")
            reward = quest["rewards"][0]
            if reward.get("team_reward", True) or reward.get("auto", "disabled") != "disabled":
                raise ValueError("A refund must retain the existing manual personal claim")
    if used_ingredients != set(names):
        raise ValueError("Refund ingredient review contains unused or missing entries")


def runtime(refund: dict) -> dict:
    return {"output": refund["output"],
            "items": [{"item": prize["item"], "count": prize["count"]} for prize in refund["items"]],
            "recipe_ids": [row["id"] for row in refund["recipes"]]}


def item_names() -> dict:
    return json.loads(POLICY_PATH.read_text(encoding="utf-8"))["ingredients"]


def description(refund: dict, language: str, names: dict | None = None) -> str:
    names = item_names() if names is None else names
    items = ", ".join(f"{prize['count']} × {names[prize['item']][language]}" for prize in refund["items"])
    if language == "uk_ua":
        return f"Гарантоване повернення витрачених компонентів: {items}."
    return f"Guaranteed return of consumed components: {items}."


def guidance(language: str = "uk_ua") -> dict[str, list[str]]:
    """Authored guidance for the shared questbook locale generator."""
    names = item_names()
    result = {}
    for qid, refund in load().items():
        output = refund["output_name"][language]
        condition = (f"Отримайте компенсацію після справжнього виготовлення предмета «{output}» вами або вашою командою. "
                     "Предмет у сховищі чи завершений квест самі по собі не підтверджують крафт."
                     if language == "uk_ua" else
                     f"Claim the refund after you or your team actually manufacture {output}. "
                     "An item in storage or an already completed quest alone does not prove crafting.")
        result[qid] = [description(refund, language, names), condition]
    return result


def apply_locale(locale: dict, language: str = "uk_ua") -> None:
    prefixes = ("Гарантоване повернення витрачених компонентів:",
                "Отримайте компенсацію після справжнього виготовлення",
                "Guaranteed return of consumed components:",
                "Claim the refund after you or your team actually manufacture")
    for key, lines in locale.items():
        if key.endswith(".quest_desc") and isinstance(lines, list):
            locale[key] = [line for line in lines if not isinstance(line, str) or not line.startswith(prefixes)]
    for qid, paragraphs in guidance(language).items():
        key = f"quest.{qid}.quest_desc"
        locale[key] = [*locale.get(key, []), *paragraphs]
