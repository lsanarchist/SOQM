"""Retarget retired chest objectives without changing saved FTB object IDs.

This edits quest definitions only. It never reads or converts a player's world;
existing Iron Chests must be emptied before installing the storage-only update.
"""

from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "metadata/storage_migration.json"


def load() -> dict:
    data = json.loads(SOURCE.read_text(encoding="utf-8"))
    if data.get("schema") != 1:
        raise ValueError("Unsupported storage objective migration")
    return data


def apply_structure(chapters: list[dict]) -> dict:
    quests = {quest["id"]: quest for chapter in chapters for quest in chapter["quests"]}
    changed = 0
    for entry in load()["quests"]:
        quest = quests[entry["quest_id"]]
        tasks = {task["id"]: task for task in quest["tasks"]}
        task = tasks[entry["task_id"]]
        before, after = entry["before_item"], entry["after_item"]
        if task["type"] != "item" or task["item"]["id"] not in {before, after}:
            raise ValueError(f"Unreviewed storage objective: {quest['id']}")
        changed += task["item"]["id"] != after
        task["item"]["id"] = after
        quest["icon"]["id"] = after
        quest["tags"] = [tag for tag in quest.get("tags", []) if not tag.startswith("item_ironchest_")]
        tag = "item_" + after.replace(":", "_")
        if tag not in quest["tags"]:
            quest["tags"].append(tag)
    return {"retargeted_objectives": changed, "reviewed_objectives": len(load()["quests"])}


def apply_locale(locale: dict, language: str) -> dict:
    entries = load()["quests"]
    for entry in entries:
        locale[f"task.{entry['task_id']}.title"] = entry["task_title"][language]
    return {"task_titles": len(entries)}
