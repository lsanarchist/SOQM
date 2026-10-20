"""Add reviewed minimum construction kits without replacing saved objectives.

The data in metadata/multiblock_materials.json records exact pinned-mod evidence,
the original objectives and the explicit count changes. Item checks are required
and non-consuming. Native formation/operation tasks keep their IDs and settings.
"""

from __future__ import annotations

from collections import Counter
from copy import deepcopy
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "metadata/multiblock_materials.json"


def load() -> dict:
    return json.loads(DATA.read_text(encoding="utf-8"))


def task_id(quest_id: str, item: str) -> str:
    """Reserve a stable positive long ID for each newly introduced material."""
    return "515012" + hashlib.sha256(f"{quest_id}/{item}".encode()).hexdigest()[:10].upper()


def apply_structure(chapters: list[dict]) -> dict:
    """Run BEFORE campaign_progression so new materials participate in gates."""
    quests = {q["id"]: q for chapter in chapters for q in chapter["quests"]}
    all_ids = {obj["id"] for q in quests.values() for obj in [q, *q["tasks"], *q.get("rewards", [])]}
    report = Counter()
    for entry in load()["quests"]:
        quest = quests[entry["quest_id"]]
        by_item = {t["item"]["id"]: t for t in quest["tasks"] if t["type"] == "item" and isinstance(t.get("item"), dict)}
        for material in entry["materials"]:
            item = material["item"]
            if item in by_item:
                task = by_item[item]
                if task.get("count", 1) != material["count"]:
                    report["counts_corrected"] += 1
            else:
                ident = task_id(quest["id"], item)
                if ident in all_ids:
                    raise ValueError(f"Multiblock task ID collision: {ident}")
                task = {
                    "type": "item", "item": {"id": item, "count": 1},
                    "count": material["count"], "consume_items": False,
                    "only_from_crafting": False, "match_components": "none", "id": ident,
                }
                quest["tasks"].append(task)
                by_item[item] = task
                all_ids.add(ident)
                report["tasks_added"] += 1
            task["count"] = material["count"]
            task["consume_items"] = False
            task["only_from_crafting"] = False
            task["match_components"] = "none"
        # Put construction materials before the unchanged native completion check.
        order = {m["item"]: i for i, m in enumerate(entry["materials"])}
        quest["tasks"].sort(key=lambda t: order.get(t.get("item", {}).get("id") if isinstance(t.get("item"), dict) else None, len(order)))
        tags = quest.setdefault("tags", [])
        if "multiblock_materials" not in tags:
            tags.append("multiblock_materials")
        report["quests_with_kits"] += 1
    return dict(report)


def apply_locale(chapters: list[dict], locale: dict, language: str = "uk_ua") -> dict:
    """Run after general lessons/localization; does not change quest structure."""
    quests = {q["id"]: q for chapter in chapters for q in chapter["quests"]}
    entries = load()["quests"]
    for entry in entries:
        quest = quests[entry["quest_id"]]
        translation = entry[language]
        if "title" in translation:
            locale[f"quest.{quest['id']}.title"] = translation["title"]
        locale[f"quest.{quest['id']}.quest_desc"] = deepcopy(translation["description"])
        material_map = {m["item"]: m for m in entry["materials"]}
        for task in quest["tasks"]:
            item = task.get("item", {}).get("id") if isinstance(task.get("item"), dict) else None
            if item in material_map:
                material = material_map[item]
                name = material["name"]
                suffix = "без вилучення" if language == "uk_ua" else "not consumed"
                locale[f"task.{task['id']}.title"] = f"{material['count']} × {name} — {suffix}"
    return {"quests": len(entries)}


def added_task_ids() -> set[str]:
    data = load()
    return set(data["migration"]["added_task_ids"])
