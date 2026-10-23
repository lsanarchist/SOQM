#!/usr/bin/env python3
"""Check ordinary crafting choices across the pack's actual JS recipe handlers.

This is a static item/tag matching check, not a Minecraft serializer or game
test. The reviewed fixture records exact pinned tag membership and the original
ambiguous recipes. Native serializers that inspect components, double-chest
state or another machine's input are explicitly outside this item-only model.
No downloads, config mutations, server start or EULA acceptance are performed.
"""

from __future__ import annotations

import argparse
from collections import defaultdict
from dataclasses import dataclass
import hashlib
import json
from pathlib import Path
import re
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]
FIXTURE = ROOT / "tests/fixtures/recipe_collisions.json"
SHAPED = {"minecraft:crafting_shaped", "mekanism:mek_data"}
SHAPELESS = {"minecraft:crafting_shapeless"}


def authored_tag_inputs(root: Path = ROOT) -> dict[str, str]:
    """Bind the snapshot to all authored item tags and their script callbacks."""
    hashes = {}
    for path in sorted((root / "overrides/kubejs/data").glob("*/tags/item/**/*.json")):
        hashes[path.relative_to(root).as_posix()] = hashlib.sha256(path.read_bytes()).hexdigest()
    for path in sorted((root / "overrides/kubejs/server_scripts").glob("*.js")):
        if re.search(r"\bServerEvents\s*\.\s*tags\s*\(", path.read_text()):
            hashes[path.relative_to(root).as_posix()] = hashlib.sha256(path.read_bytes()).hexdigest()
    for path in sorted((root / "overrides/mods").glob("*.jar")):
        with zipfile.ZipFile(path) as archive:
            for name in sorted(archive.namelist()):
                if re.fullmatch(r"data/[^/]+/tags/item/.+\.json", name):
                    hashes[path.relative_to(root).as_posix() + "!" + name] = hashlib.sha256(archive.read(name)).hexdigest()
    return hashes


def load_fixture(path: Path = FIXTURE, root: Path = ROOT) -> dict:
    fixture = json.loads(path.read_text())
    if fixture["schema_version"] != 1:
        raise ValueError("Unknown crafting-collision fixture schema")
    actual = hashlib.sha256((root / "metadata/mods.lock.json").read_bytes()).hexdigest()
    if actual != fixture["provenance"]["mods_lock_sha256"]:
        raise ValueError("Pinned mods changed; review crafting tag membership before updating the fixture")
    if authored_tag_inputs(root) != fixture["provenance"]["authored_tag_inputs"]:
        raise ValueError("Authored item tags or tag handlers changed; review crafting tag membership before updating the fixture")
    return fixture


def capture_recipes(root: Path = ROOT, native_ids: list[str] | tuple[str, ...] = ()) -> dict:
    sources = []
    for path in sorted((root / "overrides/kubejs/server_scripts").glob("*.js")):
        text = path.read_text()
        if re.search(r"\bServerEvents\s*\.\s*recipes\s*\(", text):
            sources.append({"name": path.name, "text": text})
    result = subprocess.run(
        ["node", str(ROOT / "scripts/capture_recipe_handlers.cjs")],
        input=json.dumps({"sources": sources, "native_ids": list(native_ids)}),
        text=True, capture_output=True, timeout=30, check=False,
    )
    if result.returncode:
        raise ValueError("Recipe handler capture failed: " + result.stderr.strip())
    return json.loads(result.stdout)


def data_recipes(root: Path = ROOT) -> list[dict]:
    result = []
    base = root / "overrides/kubejs/data"
    for path in sorted(base.glob("*/recipe/**/*.json")):
        parts = path.relative_to(base).parts
        result.append({"id": parts[0] + ":" + Path(*parts[2:]).as_posix()[:-5],
                       "source": path.relative_to(root).as_posix(), "recipe": json.loads(path.read_text())})
    return result


def active_rows(capture: dict, fixture: dict, root: Path = ROOT) -> list[dict]:
    """Include native fixture survivors, data overrides and all live additions."""
    originals = {row["id"]: row for row in fixture["native_recipes"]}
    originals.update({row["id"]: row for row in data_recipes(root)})
    for removal in capture["removals"]:
        originals.pop(removal["id"], None)
    added = [row for row in capture["rows"] if not row["removed"]]
    for row in added:
        originals[row["id"]] = row
    return list(originals.values())


def legacy_condition_ids(rows: list[dict]) -> list[str]:
    invalid = []
    for row in rows:
        recipe = row["recipe"]
        while True:
            if "conditions" in recipe:
                invalid.append(row["id"])
                break
            if recipe.get("type") != "mcjtylib:copy_components":
                break
            recipe = recipe["recipe"]
    return sorted(invalid)


def condition_matches(condition: dict, fixture: dict) -> bool:
    kind = condition["type"]
    if kind == "neoforge:not":
        return not condition_matches(condition["value"], fixture)
    if kind == "neoforge:tag_empty":
        if condition["tag"] not in fixture["tags"]:
            raise ValueError("Unreviewed tag-empty predicate: " + condition["tag"])
        return not fixture["tags"][condition["tag"]]
    if kind == "neoforge:mod_loaded":
        if condition["modid"] not in fixture["condition_mods"]:
            raise ValueError("Unreviewed mod-loaded predicate: " + condition["modid"])
        return fixture["condition_mods"][condition["modid"]]
    if kind == "neoforge:item_exists":
        if condition["item"] not in fixture["condition_items"]:
            raise ValueError("Unreviewed item-exists predicate: " + condition["item"])
        return fixture["condition_items"][condition["item"]]
    if kind == "sophisticatedcore:item_enabled":
        # Exact pinned EnabledItems bytecode defaults unlisted entries to true.
        # A user's edited instance config is not represented by this fixture.
        return fixture["sophisticated_item_enabled_default"]
    if kind == "neoforge:true":
        return True
    if kind == "neoforge:false":
        return False
    raise ValueError("Unreviewed recipe condition: " + kind)


def ingredient_items(ingredient: dict | list, fixture: dict) -> frozenset[str]:
    if isinstance(ingredient, list):
        return frozenset().union(*(ingredient_items(value, fixture) for value in ingredient))
    if "type" in ingredient:
        raise ValueError("component/custom ingredient: " + ingredient["type"])
    if set(ingredient) == {"item"}:
        return frozenset((ingredient["item"],))
    if set(ingredient) == {"tag"}:
        tag = ingredient["tag"]
        if tag not in fixture["tags"]:
            raise ValueError("Unreviewed ingredient tag: " + tag)
        return frozenset(fixture["tags"][tag])
    raise ValueError("Unreviewed ordinary ingredient: " + json.dumps(ingredient, sort_keys=True))


@dataclass(frozen=True)
class Craft:
    id: str
    output: str
    # JSON result signature includes count/components, not only the item ID.
    outcome: str
    width: int
    height: int
    cells: tuple[frozenset[str], ...]
    shaped: bool = True

    def variants(self) -> tuple[tuple[frozenset[str], ...], ...]:
        mirror = tuple(self.cells[y * self.width + self.width - x - 1]
                       for y in range(self.height) for x in range(self.width))
        return (self.cells,) if mirror == self.cells else (self.cells, mirror)

    def witness(self) -> tuple[str, ...]:
        return tuple(min(cell) if cell else "" for cell in self.cells)


def compile_recipes(rows: list[dict], fixture: dict) -> tuple[list[Craft], list[dict], list[str]]:
    crafts, excluded, disabled = [], [], []
    for row in rows:
        recipe = row["recipe"]
        conditions = recipe.get("neoforge:conditions", [])
        if not all(condition_matches(value, fixture) for value in conditions):
            disabled.append(row["id"])
            continue
        while recipe.get("type") == "mcjtylib:copy_components":
            recipe = recipe["recipe"]
        kind = recipe.get("type")
        if kind not in SHAPED | SHAPELESS:
            excluded.append({"id": row["id"], "reason": "custom serializer: " + str(kind)})
            continue
        output = recipe["result"]
        output = {"id": output} if isinstance(output, str) else dict(output)
        output.setdefault("count", 1)
        try:
            if kind in SHAPED:
                pattern, key = recipe["pattern"], recipe["key"]
                points = [(x, y) for y, line in enumerate(pattern) for x, char in enumerate(line) if char != " "]
                if not points or len({len(line) for line in pattern}) != 1:
                    raise ValueError("Invalid shaped recipe rectangle: " + row["id"])
                xmin, xmax = min(x for x, y in points), max(x for x, y in points)
                ymin, ymax = min(y for x, y in points), max(y for x, y in points)
                width, height = xmax - xmin + 1, ymax - ymin + 1
                if width > 3 or height > 3:
                    raise ValueError("Oversized ordinary crafting recipe: " + row["id"])
                cells = tuple(frozenset() if char == " " else ingredient_items(key[char], fixture)
                              for line in pattern[ymin:ymax + 1] for char in line[xmin:xmax + 1])
            else:
                cells = tuple(ingredient_items(value, fixture) for value in recipe["ingredients"])
                width, height = len(cells), 1
        except ValueError as exc:
            if not str(exc).startswith("component/custom ingredient: "):
                raise
            excluded.append({"id": row["id"], "reason": str(exc)})
            continue
        if not all(cells[i] for i in range(len(cells)) if kind in SHAPELESS or
                   pattern[ymin + i // width][xmin + i % width] != " "):
            raise ValueError("Empty ingredient in enabled recipe: " + row["id"])
        crafts.append(Craft(row["id"], output.get("id", output.get("item")),
                            json.dumps(output, sort_keys=True), width, height, cells, kind in SHAPED))
    return crafts, excluded, disabled


def collision_witness(first: Craft, second: Craft) -> tuple[str, ...] | None:
    """Find a concrete matching grid, including tag intersections and mirrors."""
    if not first.shaped or not second.shaped:
        return None  # Global pair checking deliberately covers shaped recipes.
    if (first.width, first.height) != (second.width, second.height):
        return None
    for cells in second.variants():
        witness = []
        for left, right in zip(first.cells, cells):
            if not left and not right:
                witness.append("")
            elif left and right and left & right:
                witness.append(min(left & right))
            else:
                break
        else:
            return tuple(witness)
    return None


def collisions(crafts: list[Craft]) -> list[dict]:
    groups = defaultdict(list)
    for craft in crafts:
        if craft.shaped:
            mask = tuple(bool(cell) for cell in craft.cells)
            mirror = tuple(mask[y * craft.width + craft.width - x - 1]
                           for y in range(craft.height) for x in range(craft.width))
            groups[craft.width, craft.height, min(mask, mirror)].append(craft)
    result = []
    for group in groups.values():
        for index, first in enumerate(group):
            for second in group[index + 1:]:
                if first.outcome == second.outcome:
                    continue
                witness = collision_witness(first, second)
                if witness is not None:
                    result.append({"ids": [first.id, second.id], "outputs": [first.output, second.output],
                                   "width": first.width, "height": first.height, "grid": list(witness)})
    return result


def matching_outputs(crafts: list[Craft], grid: list[str] | tuple[str, ...], width: int = 3) -> set[str]:
    """Model an ordinary table's choice from the entire supplied catalogue."""
    occupied = [(i % width, i // width) for i, item in enumerate(grid) if item]
    if not occupied:
        return set()
    xmin, xmax = min(x for x, y in occupied), max(x for x, y in occupied)
    ymin, ymax = min(y for x, y in occupied), max(y for x, y in occupied)
    actual = tuple(grid[y * width + x] for y in range(ymin, ymax + 1) for x in range(xmin, xmax + 1))
    present = tuple(item for item in grid if item)
    outcomes = set()
    for craft in crafts:
        if craft.shaped:
            if (craft.width, craft.height) != (xmax - xmin + 1, ymax - ymin + 1):
                continue
            matches = any(all(item in cell if cell else not item for item, cell in zip(actual, variant))
                          for variant in craft.variants())
        else:
            if len(present) != len(craft.cells):
                continue
            # Ingredients can share members, so greedily removing a match is unsafe.
            def assign(remaining, available):
                if not remaining:
                    return True
                item = remaining[0]
                return any(item in craft.cells[index] and assign(remaining[1:], available - {index})
                           for index in available)
            matches = assign(present, set(range(len(craft.cells))))
        if matches:
            outcomes.add(craft.outcome)
    return outcomes


def audit(root: Path = ROOT, fixture_path: Path = FIXTURE) -> dict:
    fixture = load_fixture(fixture_path, root)
    capture = capture_recipes(root, [row["id"] for row in fixture["native_recipes"]])
    rows = active_rows(capture, fixture, root)
    legacy = legacy_condition_ids(rows)
    if legacy:
        raise ValueError("Legacy conditions are ignored by the pinned recipe loader; use neoforge:conditions: " + ", ".join(legacy))
    crafts, excluded, disabled = compile_recipes(rows, fixture)
    found = collisions(crafts)
    return {"handlers": len(capture["handlers"]), "queued": len(capture["rows"]),
            "live_additions": sum(not row["removed"] for row in capture["rows"]),
            "ordinary_shaped": sum(craft.shaped for craft in crafts),
            "ordinary_shapeless": sum(not craft.shaped for craft in crafts),
            "collisions": found, "excluded": excluded, "disabled": disabled,
            "scope": "Static item/tag grid matching with real JS handlers; component/stateful serializers and live Minecraft registry behavior are outside this check."}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=ROOT)
    parser.add_argument("--fixture", type=Path, default=FIXTURE)
    args = parser.parse_args()
    report = audit(args.root, args.fixture)
    if report["collisions"]:
        print(json.dumps(report["collisions"], indent=2))
        return 1
    print(f"Recipe choices: {report['handlers']} handlers, {report['live_additions']} live additions, "
          f"{report['ordinary_shaped']} authored ordinary shaped recipes, no different-output grid collisions")
    print(report["scope"])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
