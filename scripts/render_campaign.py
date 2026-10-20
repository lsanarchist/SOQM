#!/usr/bin/env python3
"""Render deterministic SVG layout reviews from committed campaign coordinates.

This is a coordinate preview, not a Minecraft screenshot. Native image positions,
ARGB colors, localized captions, quest positions, shapes and sizes are retained.
Numbered neutral markers replace item icons. Only explicit dependencies between
shown quests whose dependency lines are enabled are drawn. Native Bézier controls
and the background-images -> connections -> quest-icons draw order are retained.
No assets are copied.

The renderer needs only Python's standard library. It writes SVGs under assets;
--check verifies them without writing. For visual review, rasterize a temporary
copy with Inkscape; do not commit that PNG. Run after campaign_refresh.py.
"""

from __future__ import annotations

import argparse
import hashlib
import html
import json
import math
from pathlib import Path
import re

import campaign_layout
import campaign_locales

ROOT = Path(__file__).resolve().parents[1]
QUESTS = ROOT / "overrides/config/ftbquests/quests"
BLUEPRINT = ROOT / "metadata/campaign_layout.json"
OUTPUTS = (
    ("qm_v030_27_convergence", None, "campaign-route.svg"),
    ("qm_v030_12_ae2", None, "campaign-ae2.svg"),
    ("qm_v030_11_storage", None, "campaign-storage.svg"),
    ("qm_v040_29_exdeorum", None, "campaign-ex-deorum.svg"),
    ("qm_v040_30_exmachinis", None, "campaign-ex-machinis.svg"),
    ("qm_02_mek", None, "campaign-mekanism.svg"),
    ("qm_03_nuclear", None, "campaign-nuclear.svg"),
    ("qm_v030_09_enderio", None, "campaign-ender-io.svg"),
    ("qm_v030_10_industrial", None, "campaign-industrial.svg"),
    ("qm_v030_14_reactors", None, "campaign-reactors.svg"),
    ("qm_v040_31_mobgrinding", None, "campaign-mob-grinding.svg"),
)
SCALE = 44.0
MARGIN = 34.0
TOP = 100.0
BOTTOM = 112.0
BACKGROUND = "#111923"
TEXT = "#E6EDF5"
MUTED = "#B5C3D3"
FONT = "DejaVu Sans, sans-serif"
FORMAT_CODES = re.compile(r"§[0-9a-fk-or]", re.I)


def read(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def num(value: float) -> str:
    return f"{value:.3f}".rstrip("0").rstrip(".")


def esc(value) -> str:
    return html.escape(str(value), quote=True)


def plain(value) -> str:
    if isinstance(value, list):
        value = "\n".join(value)
    return FORMAT_CODES.sub("", str(value))


def argb(image: dict) -> tuple[str, float]:
    source = image.get("image", "")
    if not re.fullmatch(r"color:#[0-9A-Fa-f]{8}", source):
        raise ValueError(f"Review unsupported image asset {image['id']}: {source}")
    encoded = source[7:]
    return "#" + encoded[2:], int(encoded[:2], 16) / 255


def bounds(image: dict) -> tuple[float, float, float, float]:
    return (image["x"] - image["width"] / 2, image["y"] - image["height"] / 2,
            image["x"] + image["width"] / 2, image["y"] + image["height"] / 2)


def inside(quest: dict, panel: dict) -> bool:
    x0, y0, x1, y1 = bounds(panel)
    return x0 <= quest["x"] <= x1 and y0 <= quest["y"] <= y1


def build_svg(chapter: dict, locale: dict, spec: dict, all_quests: dict,
              section_limit: int | None = None) -> str:
    images = {image["id"]: image for image in chapter["images"]}
    # Top edges preserve reading order even when two cards have different heights.
    sections = sorted(spec["sections"], key=lambda s: (
        round(bounds(images[s["panel"]])[1], 3), images[s["panel"]]["x"]))
    sections = sections if section_limit is None else sections[:section_limit]
    shown = [quest for quest in chapter["quests"]
             if any(inside(quest, images[section["panel"]]) for section in sections)]
    shown.sort(key=lambda quest: (quest["y"], quest["x"], quest["id"]))
    qmap = {quest["id"]: quest for quest in shown}
    if not shown or len(qmap) != len(shown):
        raise ValueError("Preview has no quests or duplicate persistent IDs")
    if section_limit is None and len(shown) != len(chapter["quests"]):
        raise ValueError("A quest lies outside the actual route panels")
    edges = [(parent, quest) for parent, quest in campaign_layout.visible_edges(chapter)
             if parent["id"] in qmap and quest["id"] in qmap]
    paths = {(parent["id"], quest["id"]): campaign_layout.connection_points(parent, quest)
             for parent, quest in edges}

    selected_ids = {spec[key] for key in ("header", "subtitle", "divider")}
    selected_ids.update(section[key] for section in sections for key in ("panel", "accent", "heading"))
    selected_ids.update(spec["captions"][quest["id"]] for quest in shown)
    selected = [images[ident] for ident in sorted(selected_ids)]
    rects = [bounds(image) for image in selected]
    # Main Route curves use a gutter outside the cards. Include the actual path
    # extent, just as the native quest panel has generous scrollable margins.
    rects.extend(bounds(campaign_layout.path_bounds(points)) for points in paths.values())
    left = min(rect[0] for rect in rects)
    top = min(rect[1] for rect in rects)
    right = max(rect[2] for rect in rects)
    bottom = max(rect[3] for rect in rects)
    width = (right - left) * SCALE + MARGIN * 2
    height = (bottom - top) * SCALE + TOP + BOTTOM

    def px(x):
        return MARGIN + (x - left) * SCALE

    def py(y):
        return TOP + (y - top) * SCALE

    def text(x, y, content, size=13, anchor="start", color=TEXT, weight="400", max_width=None):
        lines = plain(content).splitlines() or [""]
        # Conservative glyph estimate keeps native caption boxes legible without
        # changing their geometry. Full unshortened quest titles remain in <title>.
        if max_width and max(map(len, lines)):
            size = min(size, max_width / (max(map(len, lines)) * .62))
        line_height = size * 1.28
        start = y - (len(lines) - 1) * line_height / 2
        attrs = f'x="{num(x)}" text-anchor="{anchor}" font-size="{num(size)}" font-weight="{weight}" fill="{color}"'
        chunks = [f'<text {attrs}>']
        chunks.extend(f'<tspan x="{num(x)}" y="{num(start + i * line_height)}">{esc(line)}</tspan>'
                      for i, line in enumerate(lines))
        chunks.append('</text>')
        return "".join(chunks)

    title = plain(locale[f"chapter.{chapter['id']}.title"])
    heading_ids = {section["heading"] for section in sections}
    caption_ids = {spec["captions"][quest["id"]] for quest in shown}
    off_canvas = sum(parent not in qmap for quest in shown for parent in quest.get("dependencies", []))
    hidden = sum(parent in qmap for quest in shown for parent in quest.get("dependencies", [])) - len(edges)
    section_for = {}
    for quest in shown:
        containers = [s for s in sections if inside(quest, images[s["panel"]])]
        if len(containers) != 1:
            raise ValueError(f"Quest belongs to {len(containers)} panels: {quest['id']}")
        section_for[quest["id"]] = containers[0]

    metadata = {
        "kind": "coordinate-layout-preview-not-in-game-screenshot",
        "chapter": chapter["filename"], "language": "uk_ua", "units_to_pixels": SCALE,
        "section_count": len(sections), "shown_quests": [q["id"] for q in shown],
        "shown_images": sorted(selected_ids), "visible_local_dependencies": [[p["id"], q["id"]] for p, q in edges],
        "native_dependency_control_points": {q["id"]: q["dep_control_pts"] for q in shown if q.get("dep_control_pts")},
        "hidden_local_dependency_count": hidden, "off_canvas_dependency_count": off_canvas,
        "hidden_dependencies": "Retained unlock requirements; native FTB Quests reveals incoming lines on hover",
        "native_fields": "quest x/y/shape/size/dep_control_pts; image x/y/width/height/rotation/ARGB; localized image captions",
        "draw_order": "all chapter images and captions, then native connection paths, then quest icons",
        "preview_only": "font, neutral numbered markers, header/footer, neutral line color without animated texture",
    }
    svg = [
        '<?xml version="1.0" encoding="UTF-8"?>',
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{num(width)}" height="{num(height)}" viewBox="0 0 {num(width)} {num(height)}" role="img" aria-labelledby="preview-title preview-description">',
        f'<title id="preview-title">{esc(title)} — огляд координат і розкладки</title>',
        '<desc id="preview-description">Огляд фактичних координат квестбука SOQM. Це не скриншот Minecraft. '
        'Фігури й панелі відповідають даним глави; числа замінюють предметні іконки. '
        'Лінії показують лише ввімкнені залежності між показаними вузлами.</desc>',
        f'<metadata>{esc(json.dumps(metadata, ensure_ascii=False, sort_keys=True, separators=(",", ":")))}</metadata>',
        f'<rect width="100%" height="100%" fill="{BACKGROUND}"/>',
        f'<g font-family="{FONT}" dominant-baseline="middle">',
        text(MARGIN, 27, "SOQM  /  ОГЛЯД РОЗКЛАДКИ", 14, color=MUTED, weight="700"),
        text(MARGIN, 51, "Координати з квестбука · нейтральні номери замість іконок", 13, color=MUTED),
        text(MARGIN, 73, "Це не скриншот Minecraft. Позиції та вигини зв’язків відповідають даним глави.", 12, color=MUTED),
    ]

    def image_rect(image):
        color, opacity = argb(image)
        if opacity == 0:
            return ""
        x0, y0, _, _ = bounds(image)
        rotation = image.get("rotation", 0)
        transform = f' transform="rotate({num(rotation)} {num(px(image["x"]))} {num(py(image["y"]))})"' if rotation else ""
        radius = 0  # Native images are rectangles; do not round the stored panels.
        return (f'<rect data-image-id="{image["id"]}" x="{num(px(x0))}" y="{num(py(y0))}" '
                f'width="{num(image["width"] * SCALE)}" height="{num(image["height"] * SCALE)}" '
                f'rx="{radius}" fill="{color}" fill-opacity="{num(opacity)}"{transform}/>')

    # ChapterImageButton uses DrawLayer.BACKGROUND regardless of image order.
    # Labels cannot mask a badly routed line: the native game paints them first.
    for image in sorted(selected, key=lambda image: (image.get("order", 0), image["id"])):
        svg.append(image_rect(image))
        if not image.get("text_on_image", False):
            continue
        key = f"image.{image['id']}.title"
        if key not in locale:
            raise ValueError(f"Missing displayed text: {key}")
        anchor = {"start": "start", "middle": "middle", "end": "end"}[image.get("text_h_align", "middle")]
        x0, _, x1, _ = bounds(image)
        inset = image.get("text_inset", 0) * SCALE / 16
        x = px(x0) + inset if anchor == "start" else px(x1) - inset if anchor == "end" else px(image["x"])
        if image["id"] == spec["header"]:
            size, weight, color = 24, "700", TEXT
        elif image["id"] in heading_ids:
            size, weight, color = 14, "600", TEXT
        elif image["id"] in caption_ids:
            size, weight, color = 13, "400", TEXT
        else:
            size, weight, color = 12, "400", MUTED
        rendered = text(x, py(image["y"]), locale[key], size, anchor, color, weight,
                        max_width=image["width"] * SCALE - 2 * inset - 6)
        rotation = image.get("rotation", 0)
        if rotation:
            rendered = f'<g transform="rotate({num(rotation)} {num(px(image["x"]))} {num(py(image["y"]))})">{rendered}</g>'
        svg.append(rendered)

    # QuestButton.getConnectionPoints() draws from icon center to icon center.
    # Its cubic curves have at least 20 segments. Icons cover the ends below;
    # no preview-only route, clipping or caption overpainting hides collisions.
    for parent, quest in edges:
        points = paths[(parent["id"], quest["id"])]
        path = " ".join(("M" if index == 0 else "L") + f' {num(px(point["x"]))} {num(py(point["y"]))}'
                        for index, point in enumerate(points))
        svg.append(f'<path data-from="{parent["id"]}" data-to="{quest["id"]}" d="{path}" '
                   f'fill="none" stroke="#8294AA" stroke-width="{num(SCALE * .08)}" stroke-opacity="0.62"/>')

    for index, quest in enumerate(shown, 1):
        x, y = px(quest["x"]), py(quest["y"])
        size = quest.get("size", 1) * SCALE
        radius = size / 2
        shape = quest.get("shape", chapter.get("default_quest_shape", "rsquare"))
        section = section_for[quest["id"]]
        outline, _ = argb(images[section["accent"]])
        fill, _ = argb(images[section["heading"]])
        deps = [plain(locale[f"quest.{ident}.title"]) for ident in quest.get("dependencies", []) if ident in all_quests]
        quest_title = plain(locale[f"quest.{quest['id']}.title"])
        tooltip = f"{index:02d} · {quest_title}\nID: {quest['id']}\nКоординати: ({quest['x']}, {quest['y']}); розмір {quest.get('size', 1)}; {shape}"
        tooltip += "\nУсі передумови: " + ("; ".join(deps) if deps else "немає")
        svg.append(f'<g data-quest-id="{quest["id"]}"><title>{esc(tooltip)}</title>')
        style = f'fill="{fill}" stroke="{outline}" stroke-width="2"'
        if shape == "hexagon":
            points = [(x + radius * math.cos(a * math.pi / 3), y + radius * math.sin(a * math.pi / 3)) for a in range(6)]
            svg.append('<polygon points="' + " ".join(f"{num(xp)},{num(yp)}" for xp, yp in points) + f'" {style}/>')
        elif shape == "circle":
            svg.append(f'<circle cx="{num(x)}" cy="{num(y)}" r="{num(radius)}" {style}/>')
        elif shape in {"rsquare", "square"}:
            rounding = size * .17 if shape == "rsquare" else 0
            svg.append(f'<rect x="{num(x-radius)}" y="{num(y-radius)}" width="{num(size)}" height="{num(size)}" rx="{num(rounding)}" {style}/>')
        else:
            raise ValueError(f"Review unsupported quest shape: {shape}")
        svg.append(text(x, y + .5, f"{index:02d}", 15 if size < 50 else 17, "middle", TEXT, "700"))
        svg.append('</g>')

    footer = height - BOTTOM + 26
    scope = f"{len(shown)} вузлів · {len(sections)} секцій"
    if section_limit is not None:
        scope += f" · перші {section_limit} секцій глави"
    svg.extend([
        text(MARGIN, footer, scope, 13, weight="600"),
        text(MARGIN, footer + 23, f"Лінії: {len(edges)} наявних видимих зв’язків. Приховано у главі: {hidden}; передумов поза оглядом: {off_canvas}.", 12, color=MUTED),
        text(MARGIN, footer + 45, "У грі приховані зв’язки з’являються при наведенні. У SVG всі передумови — у підказці вузла.", 12, color=MUTED),
        text(MARGIN, footer + 67, "Джерело: " + chapter["filename"] + ".snbt · lang/uk_ua.snbt", 11, color=MUTED),
        '</g>', '</svg>', '',
    ])
    return "\n".join(svg)


def expected_files() -> dict[str, str]:
    chapters = [read(path) for path in sorted((QUESTS / "chapters").glob("*.snbt"))]
    by_filename = {chapter["filename"]: chapter for chapter in chapters}
    all_quests = {quest["id"]: quest for chapter in chapters for quest in chapter["quests"]}
    locale = campaign_locales.load("uk_ua", ROOT)
    specs = campaign_layout.blueprint()
    return {filename: build_svg(by_filename[chapter], locale, specs[chapter], all_quests, limit)
            for chapter, limit, filename in OUTPUTS}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="verify committed SVGs without writing")
    parser.add_argument("--output-dir", type=Path, default=ROOT / "assets")
    args = parser.parse_args()
    files = expected_files()
    stale = [name for name, content in files.items()
             if not (args.output_dir / name).exists() or (args.output_dir / name).read_text(encoding="utf-8") != content]
    if args.check and stale:
        parser.exit(1, "Layout previews are stale; run python3 scripts/render_campaign.py\n" + "\n".join(stale) + "\n")
    if not args.check:
        args.output_dir.mkdir(parents=True, exist_ok=True)
        for name, content in files.items():
            (args.output_dir / name).write_text(content, encoding="utf-8")
    print(json.dumps({"mode": "check" if args.check else "write", "files": {
        name: {"bytes": len(content.encode()), "sha256": hashlib.sha256(content.encode()).hexdigest()}
        for name, content in files.items()
    }}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
