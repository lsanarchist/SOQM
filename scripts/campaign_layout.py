#!/usr/bin/env python3
"""Lay out the existing campaign objects without allocating new persistent IDs.

The reviewed blueprint assigns every quest and caption to a named section.
Coordinates, role markers and bilingual headings are derived from that blueprint
and the actual dependency graph, rather than from a previous rendering's pixels.
"""

from __future__ import annotations

from collections import Counter
import copy
import json
import math
from pathlib import Path
import re
import textwrap

import campaign_branches

ROOT = Path(__file__).resolve().parents[1]
BLUEPRINT = ROOT / "metadata/campaign_layout.json"
ROUTE_CHAPTER = "qm_v030_27_convergence"
FINAL = "513011BD65027BE4"
GATES = (
    "513011E734464E5D", "5130115E1ED084F6", "513011AA869D0649",
    "5130117F2402AE8A", "513011AEA75E4401", "5130113398623BDE",
)
ROLE_NAMES = {
    "uk_ua": {"route": "Головний маршрут", "practice": "Практика",
              "optional": "Додаткова гілка", "reference": "Розвиток мода"},
    "en_us": {"route": "Main route", "practice": "Practice",
              "optional": "Optional branch", "reference": "Mod development"},
}
ROLE_STYLE = {
    "route": ("hexagon", 1.22), "practice": ("rsquare", 1.06),
    "optional": ("circle", 0.8), "reference": ("rsquare", 0.9),
}
LEGEND = {
    "uk_ua": "Шестикутник: маршрут · квадрат: розвиток · коло: додаткове",
    "en_us": "Hexagon: route · square: development · circle: optional",
}
FLOW_LEGEND = {
    "uk_ua": "Зверху вниз: робочі етапи · бічні гілки: розвиток і варіанти",
    "en_us": "Top to bottom: working stages · side branches: upgrades and options",
}
FLOW_COLUMNS = (-9.2, -4.6, 0.0, 4.6, 9.2)
STAGE_COLORS = ["#E8E5B85C", "#E873C5B6", "#E890A8E8", "#E8B58CDB", "#E8E393A7", "#E8F1C96B"]


def blueprint() -> dict:
    return campaign_branches.layout_blueprints(json.loads(BLUEPRINT.read_text(encoding="utf-8"))["chapters"])


def stage(quest: dict) -> int:
    return max((int(t[6:]) for t in quest.get("tags", [])
                if re.fullmatch(r"stage_[0-6]", t)), default=0)


def ancestors(quests: dict, ident: str) -> set[str]:
    result = set()
    pending = list(quests[ident].get("dependencies", []))
    while pending:
        parent = pending.pop()
        if parent not in result:
            result.add(parent)
            pending.extend(quests[parent].get("dependencies", []))
    return result


def role(quest: dict) -> str:
    found = [t.removeprefix("soqm_role_") for t in quest.get("tags", []) if t.startswith("soqm_role_")]
    if len(found) != 1 or found[0] not in ROLE_STYLE:
        raise ValueError(f"Quest needs exactly one reviewed visual role: {quest['id']}")
    return found[0]


def sections_for(chapter: dict, spec: dict) -> list[dict]:
    sections = copy.deepcopy(spec["sections"])
    if spec.get("logical_flow"):
        return sections
    quests = {q["id"]: q for q in chapter["quests"]}
    if chapter["filename"] == ROUTE_CHAPTER:
        # The small ME workshop is now a stage-2 convenience. Cards follow the
        # repaired material stage, so the visual map cannot keep it in stage 3.
        for section in sections:
            section["quests"] = []
        for quest in chapter["quests"]:
            sections[max(1, stage(quest)) - 1]["quests"].append(quest["id"])
        for index, section in enumerate(sections):
            section["quests"].sort(key=lambda ident: (ident != GATES[index], ident == FINAL,
                                                      chapter["quests"].index(quests[ident])))
    elif chapter["filename"] == "qm_v030_18_divine":
        # Follow the island progression first; the separate Iceika/Arcana/Vethea
        # expeditions retain their own cards and all their actual dependencies.
        sections = [sections[i - 1] for i in (1, 2, 4, 5, 6, 7, 8, 11, 12, 13, 3, 9, 10, 14, 15)]
    elif chapter["filename"] == "qm_v030_12_ae2":
        sections = [sections[i - 1] for i in (1, 2, 3, 4, 8, 9, 5, 6, 7, 10, 11, 12)]
    return sections


def caption(title: str, *, width: int = 13, limit: int = 29) -> str:
    title = re.sub(r"§[0-9a-fk-or]", "", title, flags=re.I).replace("\n", " ")
    title = re.sub(r"^(?:Obtain|Craft) (?:1 × )?", "", title)
    if len(title) > 32 and ": " in title:
        prefix = title.split(": ", 1)[0]
        if 4 <= len(prefix) <= 24:
            title = prefix
    title = textwrap.shorten(title, width=limit, placeholder="…")
    lines = textwrap.wrap(title, width=width, break_long_words=False, break_on_hyphens=False)
    if len(lines) > 2:
        lines = [lines[0], textwrap.shorten(" ".join(lines[1:]), width=width + 3, placeholder="…")]
    return "\n".join(lines) or title


def canvas_locale(chapter: dict, language: str, quest_titles: dict, chapter_title: str, spec: dict | None = None) -> dict:
    spec = blueprint()[chapter["filename"]] if spec is None else spec
    result = {
        f"image.{spec['header']}.title": chapter_title,
        f"image.{spec['subtitle']}.title": (FLOW_LEGEND if spec.get("logical_flow") else LEGEND)[language],
    }
    for index, section in enumerate(sections_for(chapter, spec), 1):
        section_title = section["title"][language]
        if spec.get("logical_flow"):
            section_title = re.sub(r"^\d+\s*[·•.)-]\s*", "", section_title)
        result[f"image.{section['heading']}.title"] = (
            section_title if chapter["filename"] == ROUTE_CHAPTER
            else f"{index:02d}  {section_title}"
        )
    for ident, image_id in spec["captions"].items():
        title = spec.get("caption_titles", {}).get(ident, {}).get(language, quest_titles[ident])
        result[f"image.{image_id}.title"] = (
            caption(title, width=18, limit=42) if spec.get("logical_flow")
            else caption(title, width=16, limit=36) if chapter["filename"] == ROUTE_CHAPTER
            else caption(title)
        )
    return result


def apply(chapters: list[dict], locale: dict) -> dict:
    specs = blueprint()
    if set(specs) != {c["filename"] for c in chapters}:
        raise ValueError("Review the layout blueprint when chapters change")
    quests = {q["id"]: q for c in chapters for q in c["quests"]}
    critical = ancestors(quests, FINAL) | {FINAL} | set(GATES)
    flow_ids = {q["id"] for c in chapters if specs[c["filename"]].get("logical_flow") for q in c["quests"]}
    roles = Counter()
    for quest in quests.values():
        current = ("route" if quest["id"] in critical else "optional" if quest.get("optional")
                   else "practice" if any(t["type"] != "item" for t in quest["tasks"]) else "reference")
        quest["tags"] = [t for t in quest.get("tags", []) if not t.startswith("soqm_role_")] + [f"soqm_role_{current}"]
        quest["shape"], quest["size"] = ROLE_STYLE[current]
        if quest["id"] in GATES or quest["id"] == FINAL:
            quest["size"] = 1.48
        quest["hide_dependency_lines"] = current in {"optional", "reference"} and quest["id"] not in flow_ids
        locale[f"quest.{quest['id']}.quest_subtitle"] = f"Етап {stage(quest)} · {ROLE_NAMES['uk_ua'][current]}"
        roles[current] += 1

    # These edges were already implied by the transitive prerequisites. Making
    # them explicit draws a readable spine without adding a new unlock rule.
    for previous, current in zip(GATES, GATES[1:]):
        if previous not in ancestors(quests, current):
            raise ValueError(f"Main route has lost its preceding material gate: {current}")
        if previous not in quests[current]["dependencies"]:
            quests[current]["dependencies"].append(previous)

    for chapter in chapters:
        filename = chapter["filename"]
        spec = specs[filename]
        images = {image["id"]: image for image in chapter["images"]}
        qmap = {q["id"]: q for q in chapter["quests"]}
        assigned = [qid for section in spec["sections"] for qid in section["quests"]]
        if sorted(assigned) != sorted(qmap) or set(spec["captions"]) != set(qmap):
            raise ValueError(f"Missing, duplicate or stale layout assignment: {filename}")
        if filename == ROUTE_CHAPTER:
            chapter["group"] = "5141042000000001"
            chapter["order_index"] = -100
            locale[f"chapter.{chapter['id']}.title"] = "00 · Головний маршрут"
        chapter["autofocus_id"] = spec["anchor"]
        images[spec["anchor"]].update(x=0.0, y=2.8)
        for name, y, height in (("header", -2.45, .8), ("subtitle", -1.53, .43), ("divider", -.83, .1)):
            images[spec[name]].update(x=0.0, y=y, width=24.9, height=height)

        sections = sections_for(chapter, spec)
        if spec.get("logical_flow"):
            top = 0.0
            for index, section in enumerate(sections):
                rows = section["rows"]
                height = round(4.1 + (len(rows) - 1) * 3.5, 3)
                panel, accent, heading = (images[section[key]] for key in ("panel", "accent", "heading"))
                panel.update(x=0.0, y=round(top + height / 2, 3), width=24.9, height=height,
                             image="color:#FF1D2A3A")
                accent.update(x=-12.395, y=panel["y"], height=height, image="color:" + STAGE_COLORS[index % len(STAGE_COLORS)])
                heading.update(x=.08, y=round(top + .46, 3), width=24.52, height=.66)
                for row_index, row in enumerate(rows):
                    for column, ident in enumerate(row):
                        if ident is None:
                            continue
                        quest = qmap[ident]
                        quest.update(x=FLOW_COLUMNS[column], y=round(top + 1.85 + row_index * 3.5, 3))
                        images[spec["captions"][ident]].update(
                            x=quest["x"], y=round(quest["y"] + quest["size"] / 2 + .47, 3), width=3.7, height=.72,
                            image=panel["image"])
                top = round(top + height + .72, 3)
            title = locale[f"chapter.{chapter['id']}.title"]
            locale.update(canvas_locale(chapter, "uk_ua", {q['id']: locale[f"quest.{q['id']}.title"] for q in chapter['quests']}, title, spec))
            locale[f"chapter.{chapter['id']}.chapter_subtitle"] = [
                spec["branch_summary"]["uk_ua"],
                f"{len(chapter['quests'])} квестів · {len(sections)} послідовних виробничих секцій.",
                FLOW_LEGEND["uk_ua"],
                "Відкрий квест, щоб перевірити всі передумови, зокрема переходи до інших глав. Предметна задача перевіряє речі в інвентарі; опис пояснює складання й пробний запуск.",
            ]
            continue
        top = 0.0
        for pair_start in range(0, len(sections), 1 if filename == ROUTE_CHAPTER else 2):
            group = sections[pair_start:pair_start + (1 if filename == ROUTE_CHAPTER else 2)]
            heights = []
            for column, section in enumerate(group):
                main = filename == ROUTE_CHAPTER
                panel_x, panel_width = (0.0, 24.9) if main else (-6.45 if column == 0 else 6.45, 12.0)
                members = section["quests"]
                if "display_order" in section:
                    if sorted(section["display_order"]) != sorted(members):
                        raise ValueError(f"Explicit card order must cover all its quests: {section['heading']}")
                    members = section["display_order"]
                elif not main:
                    original = {ident: index for index, ident in enumerate(members)}
                    members = sorted(members, key=lambda ident: (
                        {"route": 0, "practice": 1, "reference": 2, "optional": 3}[role(qmap[ident])],
                        stage(qmap[ident]), original[ident]))
                rows = max(1, math.ceil((len(members) - 1) / 4)) if main else math.ceil(len(members) / 4)
                height = round((5.9 if main else 4.1) + (rows - 1) * 2.8, 3)
                heights.append(height)
                panel, accent, heading = (images[section[key]] for key in ("panel", "accent", "heading"))
                panel.update(x=panel_x, y=round(top + height / 2, 3), width=panel_width, height=height)
                accent.update(x=round(panel_x - panel_width / 2 + .055, 3), y=panel["y"], height=height,
                              image="color:" + STAGE_COLORS[(pair_start + column) % len(STAGE_COLORS)])
                heading.update(x=round(panel_x + .08, 3), y=round(top + .46, 3),
                               width=round(panel_width - .38, 3), height=.66)
                for index, ident in enumerate(members):
                    quest = qmap[ident]
                    if main:
                        x = -10.3 if index == 0 else (-4.3, .2, 4.7, 9.2)[(index - 1) % 4]
                        row = 0 if index == 0 else (index - 1) // 4
                    else:
                        x = panel_x - 4.5 + (index % 4) * 3.0
                        row = index // 4
                    # Offset the fan-out below the stage gate. Straight native
                    # dependency lines then reach a project without passing
                    # through another project's icon on the same horizontal row.
                    fan_offset = 1.8 if main and index > 0 else 0.0
                    quest.update(x=round(x, 3), y=round(top + 1.85 + row * 2.8 + fan_offset, 3))
                    label = images[spec["captions"][ident]]
                    # Keep the full caption box below the largest node. Fixed
                    # two-line boxes prevent locale-dependent geometry changes.
                    label.update(x=quest["x"], y=round(quest["y"] + quest["size"] / 2 + .47, 3),
                                 width=3.55 if main else 2.72, height=.72)
            top = round(top + max(heights) + .72, 3)

        title = locale[f"chapter.{chapter['id']}.title"]
        locale.update(canvas_locale(chapter, "uk_ua", {q['id']: locale[f"quest.{q['id']}.title"] for q in chapter['quests']}, title))
        locale[f"chapter.{chapter['id']}.chapter_subtitle"] = [
            f"{len(chapter['quests'])} квестів · {len(sections)} тематичних секцій.", LEGEND["uk_ua"],
            "Відкрий квест, щоб перевірити всі передумови. Роль на карті не змінює його умови.",
        ]
        if spec.get("branch_summary"):
            locale[f"chapter.{chapter['id']}.chapter_subtitle"].insert(0, spec["branch_summary"]["uk_ua"])
    locale["chapter_group.5141042000000007.title"] = "Трансмутація й фінал"
    return {"roles": dict(roles), "sections": sum(len(s['sections']) for s in specs.values()),
            "main_route": "00 · Головний маршрут", "logical_flow_chapters": sum(bool(s.get("logical_flow")) for s in specs.values()),
            "persistent_objects_added": 0}
