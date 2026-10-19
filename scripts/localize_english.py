#!/usr/bin/env python3
"""Generate the alpha English objective guide without changing quest mechanics.

The source SNBT files in this pack use the JSON subset. Ukrainian narrative is
not machine-translated: goals are derived from actual tasks and specific custom
conditions are translated by hand. See docs/LOCALIZATION.md for the boundary.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import zipfile
from pathlib import Path

import campaign_branches
import campaign_layout
import campaign_rewards

ROOT = Path(__file__).resolve().parents[1]
QUESTS = ROOT / "overrides/config/ftbquests/quests"
OUTPUT = QUESTS / "lang/en_us.snbt"

CHAPTERS = {
    "qm_intro": "01 · The Adventure Begins",
    "qm_v030_02_foundry": "02 · Foundry and Modular Tools",
    "qm_v030_03_immersive": "03 · Steel, Wires and Heavy Industry",
    "qm_v030_04_buildcraft": "04 · Pipes, Oil and Construction Machines",
    "qm_v030_05_railcraft": "05 · Railways for the Factory",
    "qm_02_mek": "06 · The Mekanism Factory",
    "qm_04_tools": "07 · Mekanism Equipment",
    "qm_03_nuclear": "08 · Fission, Fusion and High Energy",
    "qm_v030_09_enderio": "09 · Ender IO: Alloys, Souls and Conduits",
    "qm_v030_10_industrial": "10 · Industrial Foregoing without Crop Grinding",
    "qm_v030_11_storage": "11 · Storage, Logistics and Useful Devices",
    "qm_v030_12_ae2": "12 · Applied Energistics Networks",
    "qm_v030_13_rftools": "13 · RFTools: Building and Custom Worlds",
    "qm_v030_14_reactors": "14 · Extreme Reactors",
    "qm_v030_15_botania": "15 · Mana, Runes and Elven Engineering",
    "qm_v030_16_evilcraft": "16 · EvilCraft: Blood, Spirits and Weather",
    "qm_05_apoth": "17 · Apotheosis: The Equipment Workshop",
    "qm_v030_18_divine": "18 · DivineRPG: The Great Expedition",
    "qm_v030_19_twilight": "19 · Twilight Forest Progression",
    "qm_v030_20_aether": "20 · Aether: An Expedition in the Sky",
    "qm_v030_21_space": "21 · Ad Astra: Beyond the Atmosphere",
    "qm_v030_22_millenaire": "22 · Millénaire: Towns and Crafts",
    "qm_07_exploration": "23 · Ruins, Settlements and Surprises",
    "qm_06_accessories": "24 · Artifacts, Relics and Your Loadout",
    "qm_v030_25_projecte": "25 · ProjectE: Earned Transmutation",
    "qm_v030_26_avaritia": "26 · Avaritia: Final Industry",
    "qm_v030_27_convergence": "00 · Main Route",
    "qm_v040_28_ironfurnaces": "28 · The Iron Furnaces Ladder",
    "qm_v040_29_exdeorum": "29 · Ex Deorum: Resources through Processing",
    "qm_v040_30_exmachinis": "30 · Ex Machinis: Powered Processing",
    "qm_v040_31_mobgrinding": "31 · Mob Grinding Utils: Controlled Farming",
    "qm_v040_32_cannibal": "32 · Cannibal Villages: Tropics and Depths",
    "qm_v040_33_netherexoticism": "33 · Nether's Exoticism: Field Expedition",
    "qm_v040_34_terralith": "34 · Terralith: Routes through a Vast World",
    "qm_v040_35_travel": "35 · Waystones, Maps and Item Recovery",
    "qm_v040_36_skills": "36 · Pufferfish's Skills: Choose Your Growth",
    "qm_42_sophisticated": "37 · Sophisticated: Backpacks and Storage",
}

GROUPS = [
    "Starting Out and Routes", "Materials and Production", "Machines and Power",
    "Networks and Logistics", "Magic and Equipment", "Dimensions and Settlements",
    "Transmutation and the Finale",
]

# Task ID -> concise title and hand-translated mechanical requirements.
SPECIAL = {
    "514D12BDB225F47F": (
        "Complete the Chemical Processing Line",
        "Manually confirm: run a batch through dissolution, washing, crystallization and the remaining machines to produce at least 32 iron ingots. Observe automatic transfer through the whole line. The inventory task verifies ingots, not their origin or your pipe layout.",
    ),
    "514D124717B8D25F": (
        "Cooling before Ignition",
        "Manually confirm while the reactor is OFF: fill the cooling circuit, test coolant input and heated output, and configure an emergency shutdown. Match pumps, pipes and consumers to the intended operating rate. Item detection cannot prove that this system is safe.",
    ),
    "514D122A765B9623": (
        "A Controlled First Fission Cycle",
        "Form and cool a Fission Reactor; prepare fuel and waste handling. Right-click its casing to start observing that reactor and remain within 16 blocks. Run it for at least 10 seconds with real fuel burn, coolant present, temperature below 1200 K and zero damage. Then stop it with its normal GUI control and wait for zero burn rate. Leaving, dismantling or unsafe operation resets unfinished observation.",
    ),
    "514D122185675E38": (
        "Controlled Fusion",
        "Form and fuel the Fusion Reactor and prepare laser ignition. Fill a Hohlraum and start it using Mekanism's procedure. Open the formed controller and remain within 16 blocks for at least 10 seconds of actual fusion burn, positive power generation and energy in its internal buffer. Leaving or losing the formed reactor resets unfinished observation.",
    ),
    "514D12A7B657AB1D": (
        "Equip an Accessory",
        "Equip an Artifacts accessory or Relics relic in a compatible active Curios slot. Ordinary inventory, cosmetic slots and disabled slots do not count. Open Curios from the inventory button or its key binding in Controls.",
    ),
    "514D12A87813E335": (
        "Use a Relic's Ability",
        "Equip a suitable relic and perform an action that grants that relic its own ability experience. Artifacts use the Reliquified Artifacts integration. Player XP, opening a menu and pressing a key without a successful action do not count. Hold Left Shift over the relic in your inventory to inspect its own experience sources; active abilities use the Relics ability menu.",
    ),
    "514D12D73681C680": (
        "Upgrade a Relic Ability",
        "Raise one suitable relic to level 1 or higher, spend its ability point on an upgrade, and equip that same item in Curios. The automatic check requires both the relic level and spent ability points on one equipped item; an unspent point or an upgrade on a different item is insufficient.",
    ),
    "514D1219811BF438": (
        "An Expedition Loadout",
        "Complete the main relic cycle: find an accessory, equip it, earn its own experience and upgrade an ability. This summary completes automatically after its upgrade dependency; optional random collections do not block it.",
    ),
    "514D123334C1CCAE": (
        "Mine a Group with Ultimine",
        "Use an iron pickaxe and FTB Ultimine to successfully break at least two different blocks in one grouped operation. Ordinary mining does not count. The separate item tasks detect the pickaxe and 64 cobblestone without consuming them. Check Controls for FTB Ultimine and preview the selected area before breaking it.",
    ),
    "514D12480266404E": (
        "A Route between Known Places",
        "Complete the bridge visit, underground village visit and Industrial Chassis workshop dependencies. This summary completes automatically from those three quests. Mark the route on your map; road construction itself is your optional project.",
    ),
    "514D020000000001": (
        "Welcome to Shout Out to Questing Mayhem",
        "Start the SOQM Expert campaign. This entry completes automatically through the campaign integration.",
    ),
    "514D02000000000A": (
        "A Successful Millénaire Trade",
        "Find a real Millénaire settlement and complete one accepted sale or purchase through its own trading interface. Opening the window, a failed trade and trading with an ordinary vanilla villager do not count. Right-click its trader without sneaking and choose an available exchange; check Controls for Millénaire's information panel and nearby villages.",
    ),
    "514D02000000000B": (
        "Supply a Settlement",
        "Sell or donate a total of 64 accepted building-material units through Millénaire trading. Logs, planks, stone, cobblestone, stone bricks, sand, glass, clay, bricks and corresponding building blocks can qualify. Only the accepted amount counts; returned excess, purchases, food, seeds and money do not. Progress is cumulative for the team between sessions. Check the settlement's actual demand.",
    ),
    "514D020000000005": (
        "Put a Modular Pickaxe to Work",
        "Assemble a valid Modern Foundry pickaxe and use it to successfully break an ordinary Stone block. Its material combination can vary. A stored pickaxe, a hit without breaking and a protected failed attempt do not count. Consult Materials and You for the installed version's assembly and material rules.",
    ),
    "514D020000000006": (
        "Complete a Powered Machine Recipe",
        "Power a Mekanism electric machine such as an Energized Smelter, Enrichment Chamber or Crusher and finish at least one actual recipe. Remain within 16 blocks when it finishes. A charged empty machine or blocked output is insufficient.",
    ),
    "514D020000000008": (
        "Smelt 32 Iron Ingots with Power",
        "Produce a cumulative 32 iron ingots in a Mekanism Energized Smelter. Stay within 16 blocks when operations finish. The team machine counter persists between sessions. The separate item task also requires 32 ingots in your inventory without consumption; inventory presence and machine production are distinct checks.",
    ),
    "514D020000000009": (
        "Production without Hand Transfers",
        "Manually confirm: feed raw materials from storage into a machine and automatically move its output into other storage. Observe a complete batch producing 32 iron ingots without hand transfers between operations. Compatible transport from any installed mod is allowed. This checkmark does not detect pipes, machines or containers.",
    ),
    "514D02000000000E": (
        "Mana at the Base",
        "Build mana generation, transfer and a normal Botania Revived Mana Pool. Once it contains mana, right-click the pool. The automatic check requires real mana greater than zero; an empty pool or Creative Mana Pool does not qualify.",
    ),
    "514D02000000000F": (
        "Ready for Greater Expeditions",
        "Complete all four introductory routes: automatic production, settlement supply, DivineRPG hunting and first mana. This summary completes from its four dependencies without another manual check. Open Mayhem Cubes away from your base: their pools include dangerous events and structures.",
    ),
}

ACRONYMS = {"ae": "AE", "ae2": "AE2", "me": "ME", "emc": "EMC", "io": "IO", "xp": "XP", "sps": "SPS", "rf": "RF", "fe": "FE", "nasa": "NASA", "ie": "IE", "dna": "DNA", "mb": "Multiblock"}
FIXED_NAMES = {
    "minecraft:the_nether": "The Nether", "minecraft:the_end": "The End",
    "cannibalvillages:tubarao_branco": "Great White Shark",
    "cannibalvillages:lula_gigante": "Giant Squid", "cannibalvillages:xama": "Shaman",
    "cannibalvillages:ozgum": "Ozgum", "cannibalvillages:rutia": "Rutia",
}


def load_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


def refresh_names(mod_dir: Path) -> None:
    """Retain only relevant display names, never a whole upstream language file."""
    identifiers = set()
    advancement_ids = set()

    def collect(value):
        if isinstance(value, dict):
            for child in value.values():
                collect(child)
        elif isinstance(value, list):
            for child in value:
                collect(child)
        elif isinstance(value, str) and re.fullmatch(r"#?[a-z0-9_.-]+:[a-z0-9_./-]+", value):
            identifiers.add(value.lstrip("#"))

    for path in sorted((QUESTS / "chapters").glob("*.snbt")):
        data = load_json(path)
        collect(data)
        for quest in data["quests"]:
            for task in quest.get("tasks", []):
                if task["type"] == "advancement":
                    advancement_ids.add(task["advancement"])
    for path in sorted((ROOT / "overrides/kubejs/data/qm/tags/item/quest_accessories").glob("*.json")):
        collect(load_json(path))
    names = {}
    sources = []
    mods = load_json(ROOT / "metadata/mods.lock.json")["mods"]
    for mod in sorted(mods, key=lambda entry: entry["filename"]):
        path = mod_dir / mod["filename"]
        if not path.exists():
            continue
        digest = hashlib.sha256(path.read_bytes()).hexdigest()
        if digest != mod["sha256"]:
            raise ValueError(f"Unpinned mod file: {path.name}")
        used = False
        with zipfile.ZipFile(path) as jar:
            language = {}
            for member in sorted(jar.namelist()):
                if re.fullmatch(r"assets/[^/]+/lang/en_us.json", member):
                    language.update(json.loads(jar.read(member)))
            for identifier in sorted(identifiers):
                namespace, resource = identifier.split(":", 1)
                for kind in ("item", "block", "entity"):
                    value = language.get(f"{kind}.{namespace}.{resource.replace('/', '.')}")
                    if isinstance(value, str) and not contains_cyrillic(value):
                        names[identifier] = value
                        used = True
                        break
            for identifier in sorted(advancement_ids):
                namespace, resource = identifier.split(":", 1)
                for folder in ("advancement", "advancements"):
                    member = f"data/{namespace}/{folder}/{resource}.json"
                    if member not in jar.namelist():
                        continue
                    data = json.loads(jar.read(member))
                    component = data.get("display", {}).get("title", {})
                    if isinstance(component, dict):
                        value = language.get(component.get("translate"), component.get("text"))
                    elif isinstance(component, str):
                        value = component
                    else:
                        value = None
                    if isinstance(value, str) and not contains_cyrillic(value):
                        names[identifier] = value
                        used = True
        if used:
            sources.append({"filename": path.name, "sha256": digest})
    destination = ROOT / "metadata/quest_english_names.json"
    destination.write_text(json.dumps({
        "scope": "Relevant item, entity and advancement display names from pinned mod en_us assets; no narrative translation.",
        "sources": sources,
        "names": dict(sorted(names.items())),
    }, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def humanize(identifier: str) -> str:
    value = identifier.lstrip("#").split(":", 1)[-1].split("/")[-1]
    words = value.replace("_", " ").replace("-", " ").split()
    if len(words) > 1 and words[0] in {"ingot", "nugget", "dust", "ore"}:
        words = words[1:] + words[:1]
    return " ".join(ACRONYMS.get(word.lower(), word.capitalize()) for word in words)


def contains_cyrillic(value: str) -> bool:
    return bool(re.search(r"[\u0400-\u04ff]", value))


def build_names(uk: dict) -> dict:
    names = dict(FIXED_NAMES)
    for path in sorted((ROOT / "overrides/kubejs/assets").glob("*/lang/en_us.json")):
        for key, value in load_json(path).items():
            parts = key.split(".", 2)
            if len(parts) == 3 and parts[0] in {"item", "block", "entity"} and isinstance(value, str):
                names[f"{parts[1]}:{parts[2]}"] = value
    # Some source task captions already include the exact English item name.
    for path in sorted((QUESTS / "chapters").glob("*.snbt")):
        for quest in load_json(path)["quests"]:
            for task in quest.get("tasks", []):
                if task["type"] != "item":
                    continue
                caption = uk.get(f"task.{task['id']}.title", "")
                match = re.match(r"\d+ × (.+?) —", caption)
                if match and not contains_cyrillic(match[1]):
                    names.setdefault(task["item"]["id"], match[1])
    # Optional curated names can be refreshed from pinned upstream language data.
    lexicon = ROOT / "metadata/quest_english_names.json"
    if lexicon.exists():
        names.update(load_json(lexicon).get("names", {}))
    return names


def registry_name(identifier: str, names: dict) -> str:
    return re.sub(r"§[0-9a-fk-or]", "", names.get(identifier, humanize(identifier)), flags=re.IGNORECASE)


def qm_advancement(advancement: str, names: dict) -> tuple[str, str] | None:
    if not advancement.startswith("qm:"):
        return None
    path = advancement.split(":", 1)[1]
    source = ROOT / "overrides/kubejs/data/qm/advancement" / f"{path}.json"
    data = load_json(source)
    short = path.rsplit("/", 1)[-1]
    if "/biomes/" in path:
        biomes = []
        for criterion in data["criteria"].values():
            for condition in criterion["conditions"]["player"]:
                biomes.append(condition["predicate"]["location"]["biomes"])
        return ("Explore " + humanize(short), "Stand in any ONE of these biomes: " + ", ".join(biomes) + ". The check reads your actual biome; visiting the surface above a cave biome is insufficient.")
    if short.startswith("ie_multiblock_"):
        name = humanize(short.removeprefix("ie_multiblock_"))
        return ("Form the " + name, "Build the Immersive Engineering " + name + " using the Engineer's Manual, then form it with the Engineer's Hammer. Possessing its components alone does not count.")
    if short.startswith("enderio_conduit_"):
        name = humanize(short.removeprefix("enderio_conduit_")) + " Conduit"
        return (name, "Obtain the Ender IO " + name + ". This advancement checks the conduit type stored in the item's component; another conduit type does not count.")
    if short.startswith("trade_ae_"):
        name = humanize(short.removeprefix("trade_ae_"))
        return ("Trade with an AE2 " + name, "Complete a real trade with the AE2 Better Villagers " + name + " profession. Assign its correct workstation first; this check distinguishes the actual profession.")
    if path.startswith("quests/"):
        tag = data["criteria"]["find"]["conditions"]["items"][0]["items"]
        namespace, tag_path = tag.lstrip("#").split(":", 1)
        tag_file = ROOT / "overrides/kubejs/data" / namespace / "tags/item" / f"{tag_path}.json"
        values = load_json(tag_file)["values"] if tag_file.exists() else []
        labels = [registry_name(v, names) for v in values if isinstance(v, str)]
        title = "Find " + ("a First Accessory" if short == "first_accessory" else "a Native Relics Item" if short == "collect_native_relic" else "a " + humanize(short.removeprefix("collect_")) + " Accessory")
        detail = "Obtain any ONE item from " + tag + "."
        if labels:
            detail += " Accepted choices: " + ", ".join(labels) + "."
        return title, detail
    if path == "survival/grave_reclaimed":
        return "Recover Your Grave", "Successfully reclaim your OWN YIGD grave through the mod's normal interaction. Finding a grave, an unsuccessful attempt or another player's grave does not count. This is optional after a death; do not die deliberately for it."
    if path == "ae2/first_me_craft":
        return "First Craft in the ME Terminal", "Take a real crafting output from a powered ME Crafting Terminal whose active network contains stored items. Owning the terminal, crafting at a workbench, an unpowered network or an empty storage network does not count. This checks a terminal craft; the separate Pattern Provider tutorial explains autonomous jobs."
    if path.startswith("skills/"):
        if short.startswith("points_stage_"):
            item = data["criteria"]["stage_component"]["conditions"]["items"][0]["items"][0]
            return "Skill Points: Stage " + short[-1], "Obtain " + registry_name(item, names) + " for this stage's one-time two-point Pufferfish's Skills bonus. It is tied to the player and material: moving it repeatedly, relogging or farming normal XP does not grant extra bonuses."
        detail = {
            "root": "Open the Mayhem category in Pufferfish's Skills and unlock its starting node. The four branches share a maximum budget of 14 spendable points.",
            "capstone": "Unlock the sixth skill in any ONE of the four Mayhem branches. This is an OR condition; all four final skills are not required.",
        }.get(short, "Unlock the first skill in the Mayhem " + humanize(short) + " branch of Pufferfish's Skills. This is an optional specialization; you do not need all four branches.")
        return "Mayhem Skills: " + humanize(short), detail
    raise ValueError(f"Missing English condition for {advancement}")


def describe_task(task: dict, names: dict) -> tuple[str, str]:
    kind = task["type"]
    if kind in {"custom", "checkmark"}:
        title, description = SPECIAL[task["id"]]
        prefix = "Manual check: " if kind == "checkmark" else "Automatic: "
        return prefix + title, description
    if kind == "item":
        identifier = task["item"]["id"]
        name = registry_name(identifier, names)
        count = task.get("count", 1)
        crafting = task.get("only_from_crafting", False)
        consume = task.get("consume_items", False)
        title = f"{'Craft' if crafting else 'Obtain'} {count} × {name}"
        detail = f"{'Craft' if crafting else 'Have in your inventory'} {count} × {name} ({identifier})."
        detail += " The items are consumed when submitted." if consume else " The items are not consumed by this task."
        return title, detail
    if kind == "advancement":
        advancement = task["advancement"]
        local = qm_advancement(advancement, names)
        if local:
            return local
        title = registry_name(advancement, names)
        criterion = task.get("criterion")
        detail = f"Earn the native advancement {title} ({advancement})"
        detail += f", criterion {criterion}." if criterion else ". Follow its requirement in the Advancements screen or the mod's guide."
        return title, detail
    if kind == "structure":
        identifier = task["structure"]
        title = "Visit " + registry_name(identifier, names)
        return title, f"Visit the generated structure {registry_name(identifier, names)} ({identifier}); an item or a replica does not replace the location check."
    if kind == "dimension":
        identifier = task["dimension"]
        title = "Enter " + registry_name(identifier, names)
        return title, f"Travel into {registry_name(identifier, names)} ({identifier})."
    if kind == "kill":
        identifier = task["entity"]
        count = task.get("value", 1)
        title = f"Defeat {count} × {registry_name(identifier, names)}"
        return title, f"Defeat {count} × {registry_name(identifier, names)} ({identifier})."
    if kind == "stat":
        count = task.get("value", 1)
        identifier = task["stat"]
        return f"{registry_name(identifier, names)}: {count}", f"Reach {count} on the native statistic {identifier}. For Chance Cubes this records actually opened cubes."
    raise ValueError(f"Unsupported task type: {kind}")


def generate() -> tuple[str, dict]:
    uk = load_json(QUESTS / "lang/uk_ua.snbt")
    names = build_names(uk)
    result = {"file.0000000000000001.title": "Shout Out to Questing Mayhem · Expert · English alpha guide"}
    groups = load_json(QUESTS / "chapter_groups.snbt")["chapter_groups"]
    if len(groups) != len(GROUPS):
        raise ValueError("Chapter group count changed; review English group titles")
    for group, title in zip(groups, GROUPS):
        result[f"chapter_group.{group['id']}.title"] = title
    counts = {"chapters": 0, "quests": 0, "tasks": 0, "authored_tutorials": 0, "named_sections": 0}
    tutorials = load_json(ROOT / "metadata/campaign_tutorials.json")["quests"]
    route = load_json(ROOT / "metadata/campaign_route.json")["quests"]
    if set(route) & set(tutorials):
        raise ValueError("Main route and mod tutorials must have distinct owners")
    tutorials.update(route)
    branch_patches = campaign_branches.quest_patches()
    layout = campaign_layout.blueprint()
    gate_titles = dict(zip(campaign_layout.GATES, (
        "Industrial Chassis", "Precision Mechanism", "Dimensional Matrix",
        "Stellar Alloy", "Mythic Circuit", "EMC Core",
    )))
    chapter_files = sorted((QUESTS / "chapters").glob("*.snbt"))
    if {p.stem for p in chapter_files} != set(CHAPTERS):
        raise ValueError("Chapter set changed; review the English chapter map")
    for path in chapter_files:
        chapter = load_json(path)
        chapter_title = CHAPTERS[path.stem]
        prefix = f"chapter.{chapter['id']}"
        result[prefix + ".title"] = chapter_title
        result[prefix + ".chapter_subtitle"] = [
            f"{len(chapter['quests'])} quests · English objective guide (alpha).",
            campaign_layout.LEGEND["en_us"],
            "Open a quest to inspect all prerequisites. Its map role does not change its completion conditions.",
        ]
        if layout[path.stem].get("branch_summary"):
            result[prefix + ".chapter_subtitle"].insert(0, layout[path.stem]["branch_summary"]["en_us"])
        if layout[path.stem].get("logical_flow"):
            result[prefix + ".chapter_subtitle"] = [
                layout[path.stem]["branch_summary"]["en_us"],
                f"{len(chapter['quests'])} quests · {len(layout[path.stem]['sections'])} sequential production sections.",
                campaign_layout.FLOW_LEGEND["en_us"],
                "Open a quest to inspect all prerequisites, including links to other chapters. Item tasks check inventory; the description explains assembly and a test run.",
            ]
        counts["chapters"] += 1
        quest_titles = {}
        for quest in chapter["quests"]:
            qid = quest["id"]
            descriptions = []
            titles = []
            for task in quest.get("tasks", []):
                title, description = describe_task(task, names)
                result[f"task.{task['id']}.title"] = title
                titles.append(title.removeprefix("Automatic: ").removeprefix("Manual check: "))
                descriptions.append(description)
                counts["tasks"] += 1
            special_titles = [SPECIAL[task['id']][0] for task in quest.get("tasks", []) if task["type"] in {"custom", "checkmark"}]
            title = special_titles[0] if special_titles else titles[0] if len(titles) == 1 else " + ".join(titles[:2]) + (" + More" if len(titles) > 2 else "")
            if qid in tutorials:
                authored = tutorials[qid]["en_us"]
                title = authored["title"]
                descriptions = list(authored["description"])
                for task_id, task_title in authored.get("task_titles", {}).items():
                    if task_id not in {task["id"] for task in quest["tasks"]}:
                        raise ValueError(f"Tutorial task belongs to another quest: {qid}/{task_id}")
                    result[f"task.{task_id}.title"] = task_title
                counts["authored_tutorials"] += 1
            patch = branch_patches.get(qid, {})
            if "title" in patch:
                title = patch["title"]["en_us"]
            if "description" in patch:
                descriptions = list(patch["description"]["en_us"])
                if qid not in tutorials:
                    counts["authored_tutorials"] += 1
            title = gate_titles.get(qid, title)
            if qid == "514D010000000001":
                title = "Start the Campaign"
            quest_titles[qid] = title
            result[f"quest.{qid}.title"] = title
            stage = campaign_layout.stage(quest)
            visual_role = campaign_layout.ROLE_NAMES["en_us"][campaign_layout.role(quest)]
            result[f"quest.{qid}.quest_subtitle"] = f"Stage {stage} · {visual_role}"
            if qid == "514D010000000001":
                descriptions = [
                    "Start with the Base Foundation, then use the first chapter, 00 · Main Route, to find your next shared goal. Six material milestones connect industry, magic and expeditions.",
                    "Industrial Chassis needs steel, treated wood and engineering components from chapter 03. Mekanism and Ender IO alloys plus AE2 processors then lead to Precision Mechanism. A compact ME network with a 1k crafting CPU becomes available at this stage.",
                    "Botania, Aether and Twilight Forest lead to Dimensional Matrix; the Moon and Eden to Stellar Alloy; Mortum, Gaia and Ultimate circuits to Mythic Circuit; antimatter and Calorite to EMC Core. Optional expeditions and collections use circles.",
                    "Hexagons mark the quest prerequisites of the finale; larger nodes are material milestones. Squares cover practical lessons and mod development. Open any quest to inspect its full prerequisites, including lines hidden on the overview.",
                    "Item tasks detect inventory without consumption: hold the required items before placing or spending them. Advancements, events and locations check the described action. Three engineering tasks are explicitly manual confirmations after your own system test.",
                    "Book controls: right-click the Quest Book, or bind Open Quests under Controls → FTB Quests. Hover an item and press R for recipes or U for uses. JEI in the installed pack contains the current recipes.",
                    "Every member claims each quest reward personally once. An ordinary pack draws 2–4 prizes; the draw count and rare-prize weights improve as your team manufactures the six material milestones. Expensive items enter their pools only after actual production of that same item. Picking up a gift or a reward is not manufacturing evidence.",
                    "Use /soqm rewards to see your reward level, and /soqm rewards pool <theme> for eligible prizes, current per-draw chances and the conditions on locked items. Selected milestones give each member one Mayhem Cube; open it in a separate test area away from the base.",
                    "Personal rewards and production evidence have applied since 0.4.8. The quest-branch update in 0.4.9 preserves existing claims and unlocked production rewards. Only an upgrade from the old shared rewards gives every member one new personal claim; existing personal claims and the introductory book stay claimed. For items manufactured before the production ledger existed, make one new sample. After changing teams, pick up and place your production machines again so their next completed recipes count for the new team.",
                ]
            result[f"quest.{qid}.quest_desc"] = descriptions
            counts["quests"] += 1
        # Stable caption IDs bind to specific quests; moving a card cannot
        # accidentally borrow the neighbouring quest's English title.
        result.update(campaign_layout.canvas_locale(chapter, "en_us", quest_titles, chapter_title, layout[path.stem]))
        counts["named_sections"] += len(layout[path.stem]["sections"])
    result.update(campaign_rewards.locale_entries("en_us"))
    expected = set(uk)
    missing = expected - set(result)
    extra = set(result) - expected
    if missing or extra:
        raise ValueError(f"Locale key mismatch: missing={sorted(missing)[:10]}, extra={sorted(extra)[:10]}")
    if contains_cyrillic(json.dumps(result, ensure_ascii=False)):
        raise ValueError("English guide unexpectedly contains Cyrillic")
    return json.dumps(result, ensure_ascii=False, indent=2) + "\n", counts


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="fail if the committed English guide is stale")
    parser.add_argument("--mod-dir", type=Path, help="refresh relevant English display names from SHA-256-verified pinned JARs")
    args = parser.parse_args()
    if args.check and args.mod_dir:
        parser.error("--check cannot update the display-name lexicon; omit --mod-dir")
    if args.mod_dir:
        refresh_names(args.mod_dir)
    content, counts = generate()
    if args.check:
        if not OUTPUT.exists() or OUTPUT.read_text(encoding="utf-8") != content:
            parser.exit(1, "English objective guide is stale; run python3 scripts/localize_english.py\n")
    else:
        OUTPUT.parent.mkdir(parents=True, exist_ok=True)
        OUTPUT.write_text(content, encoding="utf-8")
    print(json.dumps({"output": str(OUTPUT.relative_to(ROOT)), "mode": "check" if args.check else "write", **counts}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
