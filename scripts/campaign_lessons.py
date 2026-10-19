"""Load reviewed bilingual lessons without changing quest objectives or titles.

These description-only overlays run after the progression/branch generators.
Keeping their sources separate makes an editorial fix survive regeneration.
"""

from __future__ import annotations

import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
LESSONS = ROOT / "metadata/tutorial_expansions"
LANGUAGES = {"uk_ua", "en_us"}


def _unique_object(pairs: list[tuple]) -> dict:
    result = {}
    for key, value in pairs:
        if key in result:
            raise ValueError(f"Duplicate lesson source key: {key}")
        result[key] = value
    return result


def load_lessons(directory: Path = LESSONS) -> dict:
    lessons = {}
    for path in sorted(directory.glob("*.json")):
        source = json.loads(path.read_text(encoding="utf-8"), object_pairs_hook=_unique_object)
        if set(source) != {"schema", "lessons"} or source["schema"] != 1:
            raise ValueError(f"Unsupported lesson source: {path.name}")
        if not isinstance(source["lessons"], dict):
            raise ValueError(f"Expected a lesson map: {path.name}")
        for ident, lesson in source["lessons"].items():
            if not re.fullmatch(r"[0-9A-F]{16}", ident):
                raise ValueError(f"Invalid lesson quest ID: {ident}")
            if ident in lessons:
                raise ValueError(f"Duplicate lesson owner: {ident}")
            if not isinstance(lesson, dict) or set(lesson) != {"description", "sources"}:
                raise ValueError(f"Lessons may only supply descriptions and sources: {ident}")
            descriptions = lesson["description"]
            if not isinstance(descriptions, dict) or set(descriptions) != LANGUAGES:
                raise ValueError(f"Lesson needs both supported languages: {ident}")
            for language, paragraphs in descriptions.items():
                if (not isinstance(paragraphs, list) or not paragraphs
                        or any(not isinstance(p, str) or not p.strip() for p in paragraphs)):
                    raise ValueError(f"Empty or invalid lesson: {ident}/{language}")
            sources = lesson["sources"]
            if (not isinstance(sources, list) or not sources
                    or any(not isinstance(s, str) or not s.strip() for s in sources)):
                raise ValueError(f"Lesson needs reviewable sources: {ident}")
            lessons[ident] = lesson
    if not lessons:
        raise ValueError(f"No reviewed lessons found in {directory}")
    return lessons


def apply_locale(locale: dict, language: str, lessons: dict | None = None) -> int:
    if language not in LANGUAGES:
        raise ValueError(f"Unsupported lesson language: {language}")
    if lessons is None:
        lessons = load_lessons()
    missing = [ident for ident in lessons
               if f"quest.{ident}.quest_desc" not in locale or f"quest.{ident}.title" not in locale]
    if missing:
        raise ValueError(f"Lessons reference unknown quests: {', '.join(sorted(missing))}")
    for ident, lesson in lessons.items():
        locale[f"quest.{ident}.quest_desc"] = list(lesson["description"][language])
    return len(lessons)
