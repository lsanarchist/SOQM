#!/usr/bin/env python3
"""Store quest translations by chapter and assemble FTB Quests runtime locales.

FTB Quests 2101.1.36 reads one lang/<locale>.snbt file per language. The repository
keeps small, reviewable JSON fragments instead; packaging and instance staging
merge them in memory without requiring generated files in the source checkout.
"""

from __future__ import annotations

import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
LANGUAGES = ("uk_ua", "en_us")
MAX_SHARD_BYTES = 200 * 1024
RUNTIME_DIRECTORY = "config/ftbquests/quests/lang"
KEY = re.compile(r"(file|chapter_group|chapter|quest|task|reward|image)\.([0-9A-F]{16})\.([a-z_]+)")
FIELDS = {
    "file": {"title"}, "chapter_group": {"title"},
    "chapter": {"title", "chapter_subtitle"},
    "quest": {"title", "quest_subtitle", "quest_desc"},
    "task": {"title"}, "reward": {"title"}, "image": {"title"},
}


def serialize(data: dict) -> str:
    return json.dumps(data, ensure_ascii=False, sort_keys=True, indent=2) + "\n"


def source_directory(language: str, root: Path = ROOT) -> Path:
    if language not in LANGUAGES:
        raise ValueError(f"Unsupported quest locale: {language}")
    return root / "metadata/quest_locales" / language


def _no_symlink(path: Path, root: Path) -> None:
    relative = path.relative_to(root)
    current = root
    for part in relative.parts:
        current /= part
        if current.is_symlink():
            raise ValueError(f"Locale source symlink rejected: {current}")


def _unique_object(pairs: list[tuple]) -> dict:
    result = {}
    for key, value in pairs:
        if key in result:
            raise ValueError(f"Duplicate locale JSON key: {key}")
        result[key] = value
    return result


def _read(path: Path, root: Path, *, shard: bool = False) -> dict:
    _no_symlink(path, root)
    if not path.is_file():
        raise ValueError(f"Missing locale source file: {path}")
    if shard and path.stat().st_size > MAX_SHARD_BYTES:
        raise ValueError(f"Locale shard exceeds {MAX_SHARD_BYTES} bytes: {path}")
    value = json.loads(path.read_text(encoding="utf-8"), object_pairs_hook=_unique_object)
    if not isinstance(value, dict):
        raise ValueError(f"Locale source must be a JSON object: {path}")
    return value


def read_chapters(root: Path = ROOT) -> list[dict]:
    directory = root / "overrides/config/ftbquests/quests/chapters"
    _no_symlink(directory, root)
    chapters = []
    for path in sorted(directory.glob("*.snbt")):
        chapter = _read(path, root)
        if chapter.get("filename") != path.stem:
            raise ValueError(f"Chapter filename does not match its path: {path}")
        chapters.append(chapter)
    return chapters


def _owners(chapters: list[dict]) -> tuple[dict, set[str]]:
    owners = {}
    filenames = {"_global.json"}

    def add(kind: str, ident: str, filename: str) -> None:
        if not isinstance(ident, str) or not re.fullmatch(r"[0-9A-F]{16}", ident):
            raise ValueError(f"Invalid locale owner ID: {kind}.{ident}")
        key = (kind, ident)
        if key in owners:
            raise ValueError(f"Duplicate locale owner: {kind}.{ident}")
        owners[key] = filename

    for chapter in chapters:
        stem = chapter.get("filename")
        if not isinstance(stem, str) or not re.fullmatch(r"[a-z0-9][a-z0-9_]*", stem):
            raise ValueError(f"Unsafe locale chapter filename: {stem}")
        filename = stem + ".json"
        if filename in filenames:
            raise ValueError(f"Duplicate locale chapter filename: {stem}")
        filenames.add(filename)
        add("chapter", chapter["id"], filename)
        for image in chapter.get("images", []):
            add("image", image["id"], filename)
        for quest in chapter.get("quests", []):
            add("quest", quest["id"], filename)
            for kind, field in (("task", "tasks"), ("reward", "rewards")):
                for child in quest.get(field, []):
                    add(kind, child["id"], filename)
    return owners, filenames


def _owner(key: str, value, owners: dict) -> str:
    match = KEY.fullmatch(key)
    if not match or match[3] not in FIELDS[match[1]]:
        raise ValueError(f"Invalid quest locale key: {key}")
    if not (isinstance(value, str) or
            isinstance(value, list) and all(isinstance(line, str) for line in value)):
        raise ValueError(f"Locale value must be text or a list of text: {key}")
    kind, ident, _ = match.groups()
    if kind in {"file", "chapter_group"}:
        return "_global.json"
    if (kind, ident) not in owners:
        raise ValueError(f"Unknown quest locale owner: {key}")
    return owners[(kind, ident)]


def expected_outputs(locale: dict, language: str, chapters: list[dict],
                     root: Path = ROOT) -> dict[Path, str]:
    """Return canonical chapter fragments, preserving every key and text value."""
    directory = source_directory(language, root)
    owners, filenames = _owners(chapters)
    parts = {name: {} for name in sorted(filenames)}
    for key, value in locale.items():
        parts[_owner(key, value, owners)][key] = value
    outputs = {directory / name: serialize(part) for name, part in parts.items()}
    for path, content in outputs.items():
        _no_symlink(path, root)
        if len(content.encode("utf-8")) > MAX_SHARD_BYTES:
            raise ValueError(f"Locale shard exceeds {MAX_SHARD_BYTES} bytes: {path}")
    return outputs


def load(language: str, root: Path = ROOT) -> dict:
    """Read one locale, rejecting ambiguous, missing or misplaced fragments."""
    directory = source_directory(language, root)
    _no_symlink(directory, root)
    if not directory.is_dir():
        raise ValueError(f"Missing quest locale directory: {directory}")
    owners, filenames = _owners(read_chapters(root))
    paths = sorted(directory.iterdir())
    for path in paths:
        _no_symlink(path, root)
    actual = {path.name for path in paths}
    if actual != filenames:
        raise ValueError(f"Locale shard set mismatch for {language}: "
                         f"missing={sorted(filenames - actual)}, extra={sorted(actual - filenames)}")
    result, sources = {}, {}
    for path in paths:
        for key, value in _read(path, root, shard=True).items():
            if key in result:
                raise ValueError(f"Duplicate locale key across shards: {key} "
                                 f"({sources[key]}, {path.name})")
            result[key], sources[key] = value, path.name
    for key, value in result.items():
        expected = _owner(key, value, owners)
        if sources[key] != expected:
            raise ValueError(f"Locale key belongs in {expected}, not {sources[key]}: {key}")
    return result


def runtime_payload(root: Path = ROOT) -> dict[str, bytes]:
    """Compile the two native SNBT files without writing into the checkout."""
    locales = {language: load(language, root) for language in LANGUAGES}
    first = set(locales[LANGUAGES[0]])
    for language in LANGUAGES[1:]:
        keys = set(locales[language])
        if keys != first:
            raise ValueError(f"Quest locale key mismatch for {language}: "
                             f"missing={sorted(first - keys)[:10]}, extra={sorted(keys - first)[:10]}")
    return {f"{RUNTIME_DIRECTORY}/{language}.snbt": serialize(locale).encode("utf-8")
            for language, locale in locales.items()}
