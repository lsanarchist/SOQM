#!/usr/bin/env python3
"""Refresh the SOQM campaign from reviewed tutorials, layout and reward policy.

Existing chapters remain the source for objectives. This command is idempotent,
checks the pre-update persistent identity, and never reads or writes player data.
Run this command, localize_english.py, then soqm.py lock after editing metadata.
"""

from __future__ import annotations

import argparse
from collections import Counter
import hashlib
import json
from pathlib import Path
import re

import campaign_layout
import campaign_progression
import campaign_rewards

ROOT = Path(__file__).resolve().parents[1]
QUESTS = ROOT / "overrides/config/ftbquests/quests"
INTRO = "514D010000000001"
OBSOLETE_PREFIXES = ("Крафт 0.4.3:", "Матеріальна підготовка:", "Спільний рецепт кампанії:", "Нагорода:")
GATE_TITLES = dict(zip(campaign_layout.GATES, (
    "Промислове шасі", "Точний механізм", "Вимірна матриця",
    "Зоряний композит", "Міфічна схема", "Ядро EMC",
)))
TITLE_ALIASES = dict(zip((
    "Шасі: тепер у ліні є каркас", "Механізм точний. Ти наступний",
    "Матриця після відряджень", "Місяць виставив рахунок Eden",
    "Схема з легендарним кошторисом", "EMC: заслужена кнопка легше",
), GATE_TITLES.values()))


def load_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def serialize(data) -> str:
    return json.dumps(data, ensure_ascii=False, indent=2) + "\n"


def persistent_identity(chapters: list[dict], groups: list[dict]) -> str:
    data = {"groups": sorted(g["id"] for g in groups), "chapters": {}, "quests": {}}
    for chapter in chapters:
        data["chapters"][chapter["id"]] = sorted(i["id"] for i in chapter.get("images", []))
        for quest in chapter["quests"]:
            data["quests"][quest["id"]] = {
                "tasks": sorted(t["id"] for t in quest["tasks"]),
                "rewards": sorted([[r["id"], r.get("team_reward", True)] for r in quest["rewards"]]),
                "can_repeat": quest.get("can_repeat", False),
            }
    return hashlib.sha256(json.dumps(data, sort_keys=True, separators=(",", ":")).encode()).hexdigest()


def edit_text(chapters: list[dict], locale: dict) -> dict:
    quests = [q for c in chapters for q in c["quests"]]
    openings = Counter(locale[f"quest.{q['id']}.quest_desc"][0] for q in quests)
    report = Counter()
    for quest in quests:
        ident = quest["id"]
        key = f"quest.{ident}.quest_desc"
        lines = list(locale[key])
        # The humor tag marks the original pass. Removing it after this edit
        # makes repeat runs safe: newly leading mechanical text is not stripped.
        if "humor" in quest.get("tags", []):
            if openings[lines[0]] > 1 and len(lines) > 1:
                lines.pop(0)
                report["repeated_openings_removed"] += 1
            title_key = f"quest.{ident}.title"
            title = locale[title_key].split(" · ", 1)[0]
            if title != locale[title_key]:
                locale[title_key] = title
                report["titles_shortened"] += 1
            quest["tags"] = [tag for tag in quest["tags"] if tag != "humor"]
        filtered = [line for line in lines if not line.lstrip().startswith(OBSOLETE_PREFIXES)]
        report["obsolete_paragraphs_removed"] += len(lines) - len(filtered)
        if not filtered:
            raise ValueError(f"Editorial cleanup left an empty description: {ident}")
        locale[key] = [re.sub(r"глав(?:а|и|у|і) 27", "«Головний маршрут»", line, flags=re.I) for line in filtered]
        for old, new in TITLE_ALIASES.items():
            locale[key] = [line.replace(old, new) for line in locale[key]]

    for ident, title in GATE_TITLES.items():
        locale[f"quest.{ident}.title"] = title
    locale[f"quest.{INTRO}.title"] = "Початок кампанії"
    locale[f"quest.{INTRO}.quest_desc"] = [
        "Почни з «Основи бази», а наступну спільну ціль шукай у першій главі «00 · Головний маршрут». Шість матеріальних рубежів поєднують промисловість, магію й експедиції.",
        "Перший рубіж — промислове шасі: сталь, оброблене дерево та інженерні компоненти з глави 03. Після нього сплави Mekanism і Ender IO та процесори AE2 ведуть до точного механізму. Компактна ME-мережа й 1k-автокрафт доступні на цьому етапі.",
        "Далі: Botania, Aether і Сутінковий ліс → вимірна матриця; Місяць та Eden → зоряний композит; Mortum, Gaia й Ultimate-схеми → міфічна схема; антиматерія та Calorite → ядро EMC. Додаткові експедиції й колекції позначено колами.",
        "Шестикутники показують передумови фіналу в книзі; більші вузли — матеріальні рубежі. Квадрати відкривають практичні уроки й розвиток мода. Відкритий опис квесту показує всі залежності, навіть якщо їхні лінії приховано на загальній карті.",
        "Предметні задачі перевіряють інвентар без вилучення: потримай потрібні речі до встановлення чи витрати. Досягнення, події та відвідування перевіряють відповідну дію. Три інженерні задачі позначено як ручне підтвердження — їх відмічають після власної перевірки установки.",
        "Пам’ятка книги: ПКМ із Quest Book; клавіша Open Quests налаштовується в Керуванні → FTB Quests. Над предметом R відкриває рецепт, U — застосування. У рецептах користуйся JEI поточної збірки; там показано актуальні інгредієнти.",
        "Звичайна нагорода — один приз із тематичного пулу. Наведи на неї курсор, щоб побачити склад і шанси. Нагороди одноразові для команди; книгу кожен отримує особисто. Вибрані важливі рубежі дають один Mayhem Cube: для його подій виділи майданчик подалі від бази.",
    ]
    return dict(report)


def refresh(chapters: list[dict], locale: dict, groups: list[dict]) -> dict:
    expected = load_json(ROOT / "metadata/campaign_identity.json")["sha256"]
    if persistent_identity(chapters, groups) != expected:
        raise ValueError("Persistent campaign identities changed; review migration before refreshing")
    report = {"editorial": edit_text(chapters, locale)}
    report["progression"] = campaign_progression.apply(chapters, locale)
    for ident, translations in load_json(ROOT / "metadata/campaign_route.json")["quests"].items():
        locale[f"quest.{ident}.title"] = translations["uk_ua"]["title"]
        locale[f"quest.{ident}.quest_desc"] = list(translations["uk_ua"]["description"])
    report["layout"] = campaign_layout.apply(chapters, locale)
    report["rewards"] = campaign_rewards.apply(chapters, locale)
    if persistent_identity(chapters, groups) != expected:
        raise ValueError("Refresh changed a saved quest/task/reward identity or team claim setting")
    campaign_progression.assert_valid_graph({q['id']: q for c in chapters for q in c['quests']})
    return report


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="fail if reviewed campaign data is stale")
    args = parser.parse_args()
    paths = sorted((QUESTS / "chapters").glob("*.snbt"))
    chapters = [load_json(path) for path in paths]
    locale_path = QUESTS / "lang/uk_ua.snbt"
    locale = load_json(locale_path)
    groups = load_json(QUESTS / "chapter_groups.snbt")["chapter_groups"]
    report = refresh(chapters, locale, groups)
    outputs = {path: serialize(chapter) for path, chapter in zip(paths, chapters)}
    outputs[locale_path] = serialize(locale)
    for table in campaign_rewards.build_tables():
        outputs[campaign_rewards.TABLES_PATH / f"{table['id']}.snbt"] = serialize(table)
    stale = [str(path.relative_to(ROOT)) for path, content in outputs.items()
             if not path.exists() or path.read_text(encoding="utf-8") != content]
    if args.check and stale:
        parser.exit(1, "Campaign data is stale; run python3 scripts/campaign_refresh.py\n" + "\n".join(stale) + "\n")
    if not args.check:
        for path, content in outputs.items():
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(content, encoding="utf-8")
    print(json.dumps({"mode": "check" if args.check else "write", "changed_files": len(stale), **report}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
