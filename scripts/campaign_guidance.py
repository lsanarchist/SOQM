"""Give quest tooltips their actual topic instead of internal stage labels.

Stage tags remain available to the progression and reward code. They are not a
player-facing sequence: two independent mods can share the same material gate.
The map's authored sections and a small set of concrete hints explain the goal.
"""

from __future__ import annotations

import json
from pathlib import Path

import campaign_layout

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "metadata/campaign_guidance.json"


def source() -> dict:
    data = json.loads(SOURCE.read_text(encoding="utf-8"))
    if data.get("schema") != 1:
        raise ValueError("Unsupported quest guidance schema")
    for ident, entry in data["quests"].items():
        if set(entry) - {"title", "subtitle", "task_titles"}:
            raise ValueError(f"Unknown quest guidance field: {ident}")
        for key in ("title", "subtitle"):
            if key in entry and (set(entry[key]) != {"uk_ua", "en_us"}
                                 or any(not value.strip() for value in entry[key].values())):
                raise ValueError(f"Both concrete tooltip translations are required: {ident}")
    return data


def apply(chapters: list[dict], locale: dict, language: str) -> dict:
    if language not in {"uk_ua", "en_us"}:
        raise ValueError(f"Unsupported guidance language: {language}")
    data = source()
    blueprints = campaign_layout.blueprint()
    quests = {q["id"]: q for c in chapters for q in c["quests"]}
    if not set(data["quests"]) <= set(quests):
        raise ValueError("Quest guidance references a missing quest")
    for ident, entry in data["quests"].items():
        if "title" in entry:
            locale[f"quest.{ident}.title"] = entry["title"][language]
        for task_id, title in entry.get("task_titles", {}).items():
            if task_id not in {task["id"] for task in quests[ident]["tasks"]}:
                raise ValueError(f"Guidance task belongs to another quest: {task_id}")
            locale[f"task.{task_id}.title"] = title[language]

    updated = set()
    for chapter in chapters:
        spec = blueprints[chapter["filename"]]
        for section in campaign_layout.sections_for(chapter, spec):
            for ident in section["quests"]:
                hint = data["quests"].get(ident, {}).get("subtitle", section["title"])
                locale[f"quest.{ident}.quest_subtitle"] = hint[language]
                updated.add(ident)
        chapter_title = locale[f"chapter.{chapter['id']}.title"]
        titles = {q["id"]: locale[f"quest.{q['id']}.title"] for q in chapter["quests"]}
        # Titles below nodes must match the corrected tooltip, including the
        # six material skill bonuses. Reuse all existing caption IDs.
        locale.update(campaign_layout.canvas_locale(chapter, language, titles, chapter_title, spec))
    if updated != set(quests):
        raise ValueError("Every quest needs a meaningful section or authored tooltip")
    return {"topic_subtitles": len(updated), "specific_hints": len(data["quests"])}
