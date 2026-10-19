"""Compile reviewed production quest lines into existing campaign objects.

The source fragments own only dependencies, stage labels, optional markers and
bilingual teaching text. Layout rows reuse existing panel/caption IDs. Tasks,
rewards, recipes and saved progress are outside this compiler's write surface.
"""

from __future__ import annotations

import copy
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
SOURCES = ROOT / "metadata/quest_branches"
LANGUAGES = {"uk_ua", "en_us"}
IDENT = re.compile(r"[0-9A-F]{16}\Z")
PATCH_FIELDS = {"dependencies", "stage", "optional", "title", "description"}


def translations(value, context: str, *, paragraphs: bool = False) -> None:
    if not isinstance(value, dict) or set(value) != LANGUAGES:
        raise ValueError(f"Both authored languages are required: {context}")
    for language, text in value.items():
        if paragraphs:
            if not isinstance(text, list) or not text or any(not isinstance(p, str) or not p.strip() for p in text):
                raise ValueError(f"Nonempty teaching paragraphs are required: {context}/{language}")
        elif not isinstance(text, str) or not text.strip():
            raise ValueError(f"Nonempty localized text is required: {context}/{language}")


def sources() -> dict:
    chapters, routes, evidence = {}, [], []
    route_keys = set()
    for path in sorted(SOURCES.glob("*.json")):
        source = json.loads(path.read_text(encoding="utf-8"))
        if source.get("schema") != 1 or not isinstance(source.get("chapters"), dict):
            raise ValueError(f"Invalid production-branch source: {path.name}")
        for filename, spec in source["chapters"].items():
            if filename in chapters or not isinstance(spec, dict):
                raise ValueError(f"Duplicate or invalid chapter owner: {filename}")
            translations(spec["summary"], filename)
            if not isinstance(spec.get("quests"), dict):
                raise ValueError(f"Quest patches are required: {filename}")
            for ident, patch in spec["quests"].items():
                if not IDENT.fullmatch(ident) or not isinstance(patch, dict) or set(patch) - PATCH_FIELDS:
                    raise ValueError(f"Unsupported production-quest patch: {ident}")
                if "dependencies" in patch:
                    deps = patch["dependencies"]
                    if not isinstance(deps, list) or len(deps) != len(set(deps)) or any(not isinstance(d, str) or not IDENT.fullmatch(d) or d == ident for d in deps):
                        raise ValueError(f"Invalid branch prerequisites: {ident}")
                if "stage" in patch and (type(patch["stage"]) is not int or not 0 <= patch["stage"] <= 6):
                    raise ValueError(f"Invalid branch stage: {ident}")
                if "optional" in patch and type(patch["optional"]) is not bool:
                    raise ValueError(f"Invalid optional marker: {ident}")
                if "title" in patch:
                    translations(patch["title"], ident)
                if "description" in patch:
                    translations(patch["description"], ident, paragraphs=True)
            for section in spec.get("sections", []):
                if type(section.get("slot")) is not int:
                    raise ValueError(f"Existing panel slot required: {filename}")
                translations(section["title"], filename)
                rows = section.get("rows")
                if not isinstance(rows, list) or not rows:
                    raise ValueError(f"Explicit branch rows required: {filename}")
                for row in rows:
                    if not isinstance(row, list) or not 1 <= len(row) <= 5 or not any(row):
                        raise ValueError(f"A branch row needs one to five lanes: {filename}")
                    if any(q is not None and (not isinstance(q, str) or not IDENT.fullmatch(q)) for q in row):
                        raise ValueError(f"Invalid quest lane: {filename}")
            chapters[filename] = spec
        for route in source.get("routes", []):
            if not isinstance(route.get("key"), str) or not route["key"] or route["key"] in route_keys:
                raise ValueError(f"Duplicate or invalid branch route: {path.name}")
            translations(route["title"], route["key"])
            ids = route.get("quests")
            if not isinstance(ids, list) or len(ids) < 2 or len(ids) != len(set(ids)) or any(not isinstance(q, str) or not IDENT.fullmatch(q) for q in ids):
                raise ValueError(f"Invalid route milestones: {route['key']}")
            route_keys.add(route["key"])
            routes.append(route)
        evidence.extend(source.get("evidence", []))
    return {"chapters": chapters, "routes": routes, "evidence": evidence}


def layout_blueprints(base: dict) -> dict:
    result = copy.deepcopy(base)
    for filename, spec in sources()["chapters"].items():
        if filename not in result:
            raise ValueError(f"Branch chapter does not exist: {filename}")
        target = result[filename]
        target["branch_summary"] = spec["summary"]
        if "sections" not in spec:
            continue
        original = base[filename]["sections"]
        if sorted(s["slot"] for s in spec["sections"]) != list(range(len(original))):
            raise ValueError(f"Every original panel must be reused once: {filename}")
        target["logical_flow"] = True
        target["sections"] = []
        assigned = []
        for section in spec["sections"]:
            copied = copy.deepcopy(original[section["slot"]])
            copied.pop("display_order", None)
            copied["title"] = section["title"]
            copied["rows"] = section["rows"]
            copied["quests"] = [ident for row in section["rows"] for ident in row if ident is not None]
            assigned.extend(copied["quests"])
            target["sections"].append(copied)
        if sorted(assigned) != sorted(target["captions"]):
            raise ValueError(f"Branch rows omit, duplicate or introduce a quest: {filename}")
        # New teaching titles must also reach the stable labels below the nodes.
        for ident in spec["quests"]:
            target.get("caption_titles", {}).pop(ident, None)
    return result


def apply_structure(chapters: list[dict]) -> dict:
    by_filename = {c["filename"]: c for c in chapters}
    all_ids = {q["id"] for c in chapters for q in c["quests"]}
    report = {"chapters": 0, "quest_patches": 0, "dependencies_changed": 0}
    for filename, spec in sources()["chapters"].items():
        if filename not in by_filename:
            raise ValueError(f"Unknown production chapter: {filename}")
        quests = {q["id"]: q for q in by_filename[filename]["quests"]}
        for ident, patch in spec["quests"].items():
            if ident not in quests:
                raise ValueError(f"Branch quest belongs to another chapter: {filename}/{ident}")
            quest = quests[ident]
            if "dependencies" in patch:
                if not set(patch["dependencies"]) <= all_ids:
                    raise ValueError(f"Branch references a missing prerequisite: {ident}")
                report["dependencies_changed"] += quest.get("dependencies", []) != patch["dependencies"]
                quest["dependencies"] = list(patch["dependencies"])
            if "stage" in patch:
                quest["tags"] = [t for t in quest.get("tags", []) if not re.fullmatch(r"stage_[0-9]+", t)] + [f"stage_{patch['stage']}"]
            if "optional" in patch:
                quest["optional"] = patch["optional"]
            report["quest_patches"] += 1
        report["chapters"] += 1
    return report


def apply_locale(locale: dict, language: str) -> int:
    count = 0
    for spec in sources()["chapters"].values():
        for ident, patch in spec["quests"].items():
            if "title" in patch:
                locale[f"quest.{ident}.title"] = patch["title"][language]
            if "description" in patch:
                locale[f"quest.{ident}.quest_desc"] = list(patch["description"][language])
                count += 1
    return count


def quest_patches() -> dict:
    return {ident: patch for spec in sources()["chapters"].values() for ident, patch in spec["quests"].items()}
