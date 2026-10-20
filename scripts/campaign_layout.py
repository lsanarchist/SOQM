#!/usr/bin/env python3
"""Lay out the existing campaign objects without allocating new persistent IDs.

The reviewed blueprint assigns every quest and caption to a named section.
Coordinates, role markers and bilingual headings are derived from that blueprint
and the actual dependency graph, rather than from a previous rendering's pixels.
"""

from __future__ import annotations

from collections import Counter
import copy
import itertools
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
    "uk_ua": "Короткі лінії: послідовність · наведи на квест: інші передумови",
    "en_us": "Short lines: sequence · hover a quest: other prerequisites",
}
FLOW_LEGEND = {
    "uk_ua": "Читай зверху вниз · наведи на квест, щоб побачити всі зв’язки",
    "en_us": "Read top to bottom · hover a quest to see all its connections",
}
FLOW_COLUMNS = (-9.2, -4.6, 0.0, 4.6, 9.2)
FLOW_ROW_SPACING = 3.1
MAX_LOCAL_LINE = 6.5
LINE_CLEARANCE = .10
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


def ordered_members(members: list[str], quests: dict) -> list[str]:
    """Keep nearby steps together without putting a dependent before its input.

    The authored order breaks ties. Cosmetic route/practice/optional roles never
    move an advanced machine in front of the component it requires.
    """
    pending, result = set(members), []
    while pending:
        ready = [ident for ident in members if ident in pending
                 and not pending.intersection(quests[ident].get("dependencies", []))]
        if not ready:
            raise ValueError("A section contains cyclic quest dependencies")
        ident = next((ident for ident in ready
                      if result and result[-1] in quests[ident].get("dependencies", [])), ready[0])
        result.append(ident)
        pending.remove(ident)
    return result


def flow_positions(section: dict, quests: dict) -> dict[str, tuple[int, int]]:
    """Retain authored rows, then align related steps in nearby lanes.

    Five lanes have at most 120 arrangements per row. The score prefers short
    links; a small displacement penalty makes ties stable across regeneration.
    """
    positions = {}
    members = set(section["quests"])
    for row_index, row in enumerate(section["rows"]):
        original = {ident: column for column, ident in enumerate(row) if ident is not None}
        current = list(original)
        best = None
        for columns in itertools.permutations(range(len(FLOW_COLUMNS)), len(current)):
            trial = {ident: (row_index, column) for ident, column in zip(current, columns)}
            all_positions = positions | trial
            backward, distance = 0, 0.0
            for ident in current:
                r, c = trial[ident]
                for parent in quests[ident].get("dependencies", []):
                    if parent in members and parent in all_positions:
                        pr, pc = all_positions[parent]
                        backward += pr > r or pr == r and pc >= c
                        distance += math.hypot(FLOW_COLUMNS[c] - FLOW_COLUMNS[pc],
                                               (r - pr) * FLOW_ROW_SPACING)
            displacement = sum(abs(trial[q][1] - original[q]) for q in current)
            score = (backward, round(distance + displacement * .05, 6), columns)
            if best is None or score < best[0]:
                best = score, trial
        positions.update(best[1])
    return positions


def segment_hits_box(a: dict, b: dict, box: dict, padding: float = .04) -> bool:
    """Segment/rectangle clipping, including a small ordinary-zoom clearance."""
    low, high = 0.0, 1.0
    for axis, dimension in (("x", "width"), ("y", "height")):
        radius = box[dimension] / 2 + padding
        minimum, maximum = box[axis] - radius, box[axis] + radius
        delta = b[axis] - a[axis]
        if abs(delta) < 1e-10:
            if not minimum <= a[axis] <= maximum:
                return False
        else:
            enter, leave = sorted(((minimum - a[axis]) / delta, (maximum - a[axis]) / delta))
            low, high = max(low, enter), min(high, leave)
    return low < high


def segments_cross(a: dict, b: dict, c: dict, d: dict) -> bool:
    def turn(p, q, r):
        return (q["x"] - p["x"]) * (r["y"] - p["y"]) - (q["y"] - p["y"]) * (r["x"] - p["x"])

    t1, t2, t3, t4 = turn(a, b, c), turn(a, b, d), turn(c, d, a), turn(c, d, b)
    if all(abs(value) < 1e-9 for value in (t1, t2, t3, t4)):
        return all(max(min(a[k], b[k]), min(c[k], d[k])) <= min(max(a[k], b[k]), max(c[k], d[k]))
                   for k in ("x", "y"))
    return t1 * t2 <= 0 and t3 * t4 <= 0


def connection_controls(parent: dict, child: dict) -> tuple[dict, dict] | None:
    """Return controls in map coordinates for the pinned native renderer.

    QuestButton.positionControlPoints() subtracts half the *child's* button size
    from both stored controls. SOQM's theme sets quest_spacing to zero, so the
    correction is exactly size / 2 in both axes, independently of zoom.
    """
    data = child.get("dep_control_pts", {}).get(parent["id"])
    if data is None:
        return None
    offset = child["size"] / 2
    return ({"x": data[0] - offset, "y": data[1] - offset},
            {"x": data[2] - offset, "y": data[3] - offset})


def connection_points(parent: dict, child: dict, steps: int = 20) -> list[dict]:
    controls = connection_controls(parent, child)
    if controls is None:
        return [parent, child]
    first, second = controls
    result = []
    for index in range(steps + 1):
        t = index / steps
        result.append({axis: ((1 - t) ** 3 * parent[axis] + 3 * (1 - t) ** 2 * t * first[axis]
                              + 3 * (1 - t) * t ** 2 * second[axis] + t ** 3 * child[axis])
                       for axis in ("x", "y")})
    return result


def path_bounds(points: list[dict]) -> dict:
    low_x, high_x = min(p["x"] for p in points), max(p["x"] for p in points)
    low_y, high_y = min(p["y"] for p in points), max(p["y"] for p in points)
    return {"x": (low_x + high_x) / 2, "y": (low_y + high_y) / 2,
            "width": high_x - low_x, "height": high_y - low_y}


def boxes_overlap(first: dict, second: dict, padding: float = 0) -> bool:
    return all(abs(first[axis] - second[axis]) <= (first[dimension] + second[dimension]) / 2 + padding
               for axis, dimension in (("x", "width"), ("y", "height")))


def path_hits_box(points: list[dict], box: dict, padding: float = LINE_CLEARANCE) -> bool:
    if not boxes_overlap(path_bounds(points), box, padding):
        return False
    return any(segment_hits_box(a, b, box, padding) for a, b in zip(points, points[1:]))


def paths_cross(first: tuple[dict, dict, list[dict]], second: tuple[dict, dict, list[dict]]) -> bool:
    a, b, first_points = first
    c, d, second_points = second
    if not boxes_overlap(path_bounds(first_points), path_bounds(second_points)):
        return False
    shared = [node for node in (a, b) if node["id"] in (c["id"], d["id"])]
    for p, q in zip(first_points, first_points[1:]):
        for r, s in zip(second_points, second_points[1:]):
            if not segments_cross(p, q, r, s):
                continue
            if not shared:
                return True
            dx, dy, ex, ey = q["x"] - p["x"], q["y"] - p["y"], s["x"] - r["x"], s["y"] - r["y"]
            determinant = dx * ey - dy * ex
            if abs(determinant) > 1e-9:
                t = ((r["x"] - p["x"]) * ey - (r["y"] - p["y"]) * ex) / determinant
                intersections = [{"x": p["x"] + t * dx, "y": p["y"] + t * dy}]
            else:
                # Collinear overlap: its endpoints are drawn from the original
                # four endpoints. A shared node may mask only the part inside it.
                intersections = [point for point in (p, q, r, s) if all(
                    max(min(p[axis], q[axis]), min(r[axis], s[axis])) - 1e-9 <= point[axis]
                    <= min(max(p[axis], q[axis]), max(r[axis], s[axis])) + 1e-9 for axis in ("x", "y"))]
            if any(not any(all(abs(point[axis] - node[axis]) <= node["size"] / 2 + .03
                               for axis in ("x", "y")) for node in shared) for point in intersections):
                return True
    return False


def route_options(parent: dict, child: dict, *, spine: bool = False):
    """Try a straight link, then native curves around the below-icon caption."""
    yield None
    dy, dx = child["y"] - parent["y"], child["x"] - parent["x"]
    if dy <= 0:
        return
    # The material spine uses the clear gutter left of the cards, including
    # their heading bars. Ordinary links remain inside their own section.
    sides = (-1,) if spine else ((1, -1) if dx >= 0 else (-1, 1))
    offsets = (4.4, 4.8, 5.4, 6.0) if spine else (2.4, 2.8, 3.2, 3.6, 4.0, 4.6)
    correction = child["size"] / 2
    for offset in offsets:
        for side in sides:
            shapes = ((side * offset, 0.0),) if spine else (
                (side * offset, -min(1.0, dy * .3)),
                (0.0, -dy * .4),
                (side * offset, 0.0),
            )
            for second_x, second_y in shapes:
                controls = (parent["x"] + side * offset, parent["y"],
                            child["x"] + second_x, child["y"] + second_y)
                # Floating-point values are intentional: the native SNBT reader
                # requests a list of DoubleTag, not IntTag.
                yield [round(float(value + correction), 5) for value in controls]


def visible_edges(chapter: dict) -> list[tuple[dict, dict]]:
    """The native idle map draws each incoming group, never a selected subset."""
    quests = {quest["id"]: quest for quest in chapter["quests"]}
    return [(quests[parent], quest) for quest in chapter["quests"]
            if not quest.get("hide_dependency_lines", chapter.get("default_hide_dependency_lines", False))
            for parent in quest.get("dependencies", []) if parent in quests
            and not quests[parent].get("hide_dependent_lines", False)]


def select_dependency_lines(chapter: dict, spec: dict) -> None:
    """Show short, clear local chains; preserve all actual unlock requirements.

    FTB Quests 2101.1.36 reveals hidden incoming lines while the quest is hovered.
    There is no per-edge visibility setting: if any local prerequisite would
    clutter the map, the whole incoming group must wait for hover. Deleting a
    prerequisite to draw a nicer map would silently change progression.
    """
    quests = {q["id"]: q for q in chapter["quests"]}
    images = {image["id"]: image for image in chapter["images"]}
    section_list = sections_for(chapter, spec)
    sections = {ident: index for index, section in enumerate(section_list)
                for ident in section["quests"]}
    # FTB's ChapterImageButton always uses DrawLayer.BACKGROUND. Even an opaque
    # label with positive image order is drawn BEFORE every connection line.
    # All text boxes therefore obstruct a line, including its source caption.
    text_boxes = [image for image in images.values() if image.get("text_on_image", False)]
    chapter["default_hide_dependency_lines"] = True
    candidates = []
    for quest in quests.values():
        quest["hide_dependency_lines"] = True
        quest.pop("dep_control_pts", None)
        parents = [quests[parent] for parent in quest.get("dependencies", []) if parent in quests]
        material_spine = chapter["filename"] == ROUTE_CHAPTER and quest["id"] in GATES
        if material_spine:
            candidates.append((0, 0, 0, quest["y"], quest["x"], quest["id"], parents))
            continue
        if not parents or len(parents) > 2:
            continue
        if any(sections[parent["id"]] != sections[quest["id"]] for parent in parents):
            continue
        if any((parent["y"], parent["x"]) >= (quest["y"], quest["x"]) for parent in parents):
            continue
        distance = max(math.hypot(parent["x"] - quest["x"], parent["y"] - quest["y"]) for parent in parents)
        if distance > MAX_LOCAL_LINE:
            continue
        candidates.append((1, len(parents), distance, quest["y"], quest["x"], quest["id"], parents))

    accepted, accepted_fine = [], []
    for priority, _, _, _, _, ident, parents in sorted(candidates):
        quest = quests[ident]
        panel = images[section_list[sections[ident]]["panel"]]
        incoming, incoming_fine, controls_by_parent = [], [], {}
        for parent in parents:
            obstacles = text_boxes + [dict(x=node["x"], y=node["y"], width=node["size"], height=node["size"])
                                      for node in quests.values() if node["id"] not in (parent["id"], ident)]
            for controls in route_options(parent, quest, spine=priority == 0):
                preview = quest | {"dep_control_pts": {parent["id"]: controls} if controls else {}}
                points = connection_points(parent, preview)
                path = (parent, quest, points)
                if priority != 0 and any(not (panel["x"] - panel["width"] / 2 + .12 <= point["x"]
                                             <= panel["x"] + panel["width"] / 2 - .12) for point in points):
                    continue
                if any(path_hits_box(points, obstacle) for obstacle in obstacles):
                    continue
                # Check both the minimum 20-segment native curve and a finer
                # sample; zoom may increase the native segment count.
                fine_points = connection_points(parent, preview, 80)
                if any(path_hits_box(fine_points, obstacle) for obstacle in obstacles):
                    continue
                if any(paths_cross(path, other) for other in accepted + incoming):
                    continue
                fine_path = (parent, quest, fine_points)
                if any(paths_cross(fine_path, other) for other in accepted_fine + incoming_fine):
                    continue
                incoming.append(path)
                incoming_fine.append(fine_path)
                if controls:
                    controls_by_parent[parent["id"]] = controls
                break
            else:
                break
        if len(incoming) != len(parents):
            if priority == 0:
                raise ValueError(f"Main route spine has no clear native curve: {ident}")
            continue
        quest["hide_dependency_lines"] = False
        if controls_by_parent:
            quest["dep_control_pts"] = controls_by_parent
        accepted.extend(incoming)
        accepted_fine.extend(incoming_fine)


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
    roles = Counter()
    for quest in quests.values():
        current = ("route" if quest["id"] in critical else "optional" if quest.get("optional")
                   else "practice" if any(t["type"] != "item" for t in quest["tasks"]) else "reference")
        quest["tags"] = [t for t in quest.get("tags", []) if not t.startswith("soqm_role_")] + [f"soqm_role_{current}"]
        quest["shape"], quest["size"] = ROLE_STYLE[current]
        if quest["id"] in GATES or quest["id"] == FINAL:
            quest["size"] = 1.48
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
                positions = flow_positions(section, qmap)
                height = round(3.65 + (len(rows) - 1) * FLOW_ROW_SPACING, 3)
                panel, accent, heading = (images[section[key]] for key in ("panel", "accent", "heading"))
                panel.update(x=0.0, y=round(top + height / 2, 3), width=24.9, height=height,
                             image="color:#FF1D2A3A")
                accent.update(x=-12.395, y=panel["y"], height=height, image="color:" + STAGE_COLORS[index % len(STAGE_COLORS)])
                heading.update(x=.08, y=round(top + .46, 3), width=24.52, height=.66)
                for ident, (row_index, column) in positions.items():
                    quest = qmap[ident]
                    quest.update(x=FLOW_COLUMNS[column], y=round(top + 1.85 + row_index * FLOW_ROW_SPACING, 3))
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
            select_dependency_lines(chapter, spec)
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
                if not main:
                    members = ordered_members(members, qmap)
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
        select_dependency_lines(chapter, spec)
    locale["chapter_group.5141042000000007.title"] = "Трансмутація й фінал"
    return {"roles": dict(roles), "sections": sum(len(s['sections']) for s in specs.values()),
            "main_route": "00 · Головний маршрут", "logical_flow_chapters": sum(bool(s.get("logical_flow")) for s in specs.values()),
            "persistent_objects_added": 0}
