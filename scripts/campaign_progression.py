"""Reconcile the quest route with recipes and apply authored learning material.

The pack uses the JSON subset of SNBT. This module mutates the supplied chapter
and Ukrainian locale dictionaries; it never writes generated quest files. Quest,
task and reward IDs are retained so existing worlds keep their progress.
"""

from __future__ import annotations

from collections import defaultdict, deque
import json
from pathlib import Path
import re

import campaign_branches

ROOT = Path(__file__).resolve().parents[1]
TUTORIALS = ROOT / "metadata/campaign_tutorials.json"
INTRO = "514D010000000001"
BASE = "514D010000000002"
CHASSIS = "513011E734464E5D"
PRECISION = "5130115E1ED084F6"
MATRIX = "513011AA869D0649"
STELLAR = "5130117F2402AE8A"
MYTHIC = "513011AEA75E4401"
EMC = "5130113398623BDE"
MOON = "5130113E390D02AA"
MARS = "51301138CDC4FC6E"
VENUS = "513011B86476F5E1"
MERCURY = "513011CDEACD7F3A"
GATE_STAGES = {CHASSIS: 1, PRECISION: 2, MATRIX: 3, STELLAR: 4, MYTHIC: 5, EMC: 6, MOON: 4}
MATERIAL_GATES = {
    "kubejs:industrial_chassis": CHASSIS,
    "kubejs:precision_mechanism": PRECISION,
    "kubejs:dimensional_matrix": MATRIX,
    "kubejs:stellar_alloy": STELLAR,
    "kubejs:mythic_circuit": MYTHIC,
    "kubejs:emc_core": EMC,
    # The shield needs Desh, but not Stellar Alloy or a running reactor.
    "ad_astra:desh_plate": MOON,
}
STAGE_TAG = re.compile(r"stage_[0-9]+\Z")


def quest_stage(quest: dict) -> int:
    return max((int(tag[6:]) for tag in quest.get("tags", []) if STAGE_TAG.fullmatch(tag)), default=0)


def recipe_rows(root: Path = ROOT) -> list[dict]:
    """Read declarative recipe arrays without executing JavaScript.

    Runtime-only recipes are deliberately not guessed. Only the mandatory gates
    shared by all authored alternatives for an output are used below.
    """
    rows = []
    decoder = json.JSONDecoder()
    for path in sorted((root / "overrides/kubejs/server_scripts").glob("qm_expert*.js")):
        source = path.read_text(encoding="utf-8")
        for match in re.finditer(r"\bvar\s+recipes\s*=\s*", source):
            entries, _ = decoder.raw_decode(source[match.end():])
            for entry in entries:
                rows.append({"source": path.name, **entry})
    return rows


def recipe_output(recipe: dict) -> str | None:
    value = recipe.get("result", recipe.get("output"))
    if isinstance(value, dict):
        return value.get("id", value.get("item"))
    return value if isinstance(value, str) else None


def ingredient_items(recipe: dict) -> set[str]:
    """Return explicit input items, excluding outputs and recipe conditions."""
    found: set[str] = set()

    def visit(value):
        if isinstance(value, dict):
            if isinstance(value.get("item"), str):
                found.add(value["item"])
            for key, child in value.items():
                if key not in {"result", "output", "results", "conditions"}:
                    visit(child)
        elif isinstance(value, list):
            for child in value:
                visit(child)

    visit(recipe)
    return found


def mandatory_recipe_gates(rows: list[dict]) -> dict[str, set[str]]:
    """Infer only campaign gates proven by every authored recipe alternative.

    Secondary KubeJS components are followed transitively. Ordinary native items
    and tags have many possible sources, so this intentionally does not invent
    mandatory dependencies for them.
    """
    alternatives = defaultdict(list)
    for row in rows:
        recipe = row.get("recipe", row)
        output = recipe_output(recipe)
        if output:
            alternatives[output].append(ingredient_items(recipe))
    memo = {}

    def gates(item: str, visiting: frozenset[str] = frozenset()) -> set[str]:
        if item in MATERIAL_GATES:
            return {MATERIAL_GATES[item]}
        if item in memo:
            return memo[item]
        if item in visiting or item not in alternatives:
            return set()
        variants = []
        for ingredients in alternatives[item]:
            required = set()
            for ingredient in ingredients:
                if ingredient in MATERIAL_GATES or ingredient.startswith("kubejs:"):
                    required.update(gates(ingredient, visiting | {item}))
            variants.append(required)
        result = set.intersection(*variants) if variants else set()
        memo[item] = result
        return result

    return {item: gates(item) for item in alternatives}


def _ancestors(quests: dict[str, dict], ident: str) -> set[str]:
    result = set()
    pending = list(quests[ident].get("dependencies", []))
    while pending:
        item = pending.pop()
        if item not in result:
            result.add(item)
            pending.extend(quests[item].get("dependencies", []))
    return result


def assert_valid_graph(quests: dict[str, dict]) -> None:
    children = defaultdict(list)
    indegree = {}
    for ident, quest in quests.items():
        dependencies = quest.get("dependencies", [])
        if len(dependencies) != len(set(dependencies)):
            raise ValueError(f"Duplicate dependencies: {ident}")
        for dependency in dependencies:
            if dependency not in quests or dependency == ident:
                raise ValueError(f"Invalid dependency: {ident} -> {dependency}")
            children[dependency].append(ident)
        indegree[ident] = len(dependencies)
    roots = [ident for ident, degree in indegree.items() if degree == 0]
    if set(roots) != {INTRO}:
        raise ValueError(f"Unexpected campaign roots: {roots}")
    pending = deque(roots)
    visited = 0
    while pending:
        ident = pending.popleft()
        visited += 1
        for child in children[ident]:
            indegree[child] -= 1
            if indegree[child] == 0:
                pending.append(child)
    if visited != len(quests):
        raise ValueError("Progression repair introduced a dependency cycle")


def apply(chapters: list[dict], locale: dict) -> dict:
    """Repair progression in place and overlay the Ukrainian tutorial text."""
    quests = {quest["id"]: quest for chapter in chapters for quest in chapter["quests"]}
    assert_valid_graph(quests)
    report = {"stage_changes": [], "dependency_changes": [], "task_changes": [], "tutorial_updates": 0, "warnings": []}

    def stage(ident, value):
        quest = quests[ident]
        old = quest_stage(quest)
        quest["tags"] = [tag for tag in quest.get("tags", []) if not STAGE_TAG.fullmatch(tag)] + [f"stage_{value}"]
        if old != value:
            report["stage_changes"].append({"quest": ident, "from": old, "to": value})
        key = f"quest.{ident}.quest_subtitle"
        if isinstance(locale.get(key), str):
            locale[key] = re.sub(r"\AЕтап \d+", f"Етап {value}", locale[key])

    def dependencies(ident, wanted):
        wanted = list(dict.fromkeys(wanted))
        old = quests[ident].get("dependencies", [])
        if old != wanted:
            quests[ident]["dependencies"] = wanted
            report["dependency_changes"].append({"quest": ident, "from": old, "to": wanted})

    def move_before_gate(ident, value, gate):
        stage(ident, value)
        existing = [dep for dep in quests[ident].get("dependencies", []) if dep not in GATE_STAGES]
        dependencies(ident, [BASE, gate, *existing])

    # The presses and machines needed to make a Calculation Processor must be
    # taught before the Precision Mechanism that consumes that processor.
    early_inputs = {
        "5130112BC0CDCCBA", "513011FD8979C4DD", "5130110F99A1DF48",
        "513011540D59EE53", "513011AA8F084F8E", "513011BBBBE02633",
        "513011020C4B1739", "5130114314B68D20", "513011310DD7B8C1",
        "513011072CC6A803", "513011659DFC96EC", "513011D36DC0B032",
        "5130110AD7EC7A65", "513011CFBA3A9CF5", "513011E6243A5B01",
    }
    for ident in sorted(early_inputs):
        move_before_gate(ident, 1, CHASSIS)
    dependencies("513011AA8F084F8E", [BASE, CHASSIS])
    dependencies("513011540D59EE53", [BASE, CHASSIS, "513011AA8F084F8E", "5130110AD7EC7A65"])
    dependencies("5130110F99A1DF48", [BASE, CHASSIS, "513011AA8F084F8E"])
    dependencies("513011020C4B1739", [BASE, CHASSIS, "513011540D59EE53"])
    dependencies("513011BBBBE02633", [BASE, CHASSIS, "513011020C4B1739"])
    dependencies("513011E6243A5B01", [BASE, CHASSIS, "513011CFBA3A9CF5", "513011D36DC0B032", "513011310DD7B8C1", "5130114314B68D20"])

    # A compact controller-free network with one 1k CPU is an earned stage-2
    # convenience. The controller, 4k+ components and remote scaling stay later.
    first_network = {
        "513011F797E11624", "5130119E287D7DB8", "5130114BF00256A0",
        "5130115B3F92B69A", "5130116FD5A290E7", "5130118AC1E7A654",
        "513011E89C668AA7", "5130112A016321B8", "513011F60668F4F2",
        "513011562F81FD24", "5130112D00658B23", "513011B5F7F592D8",
        "513011DEEBB429E9", "51301130F8B93E94",
    }
    for ident in sorted(first_network):
        move_before_gate(ident, 2, PRECISION)
    dependencies("5130112A016321B8", [BASE, PRECISION, "513011E89C668AA7"])
    dependencies("513011F60668F4F2", [BASE, PRECISION, "513011E89C668AA7"])
    dependencies("513011DEEBB429E9", [BASE, PRECISION, "513011F797E11624", "513011B5F7F592D8"])
    dependencies("5130112D00658B23", [BASE, PRECISION, "51301166893DC2DA"])

    # This one task now detects an actual craft in a powered ME terminal. It
    # does not purport to observe an autonomous Pattern Provider workflow.
    terminal = quests["513011AAD4652E6F"]
    task_id = terminal["tasks"][0]["id"]
    replacement = {"type": "advancement", "advancement": "qm:ae2/first_me_craft", "criterion": "", "id": task_id}
    if terminal["tasks"][0] != replacement:
        terminal["tasks"][0] = replacement
        report["task_changes"].append({"quest": terminal["id"], "task": task_id, "advancement": replacement["advancement"]})

    # Protective equipment, oxygen and fuel are preparations for the first
    # lunar trip. The original generic stage-4 assignment put them after it.
    preflight = {"5130114FE09DA6CE", "5130115956283BA9", "513011984F5B40C8", "513011A99D6EA3A8"}
    for ident in sorted(preflight):
        move_before_gate(ident, 3, MATRIX)
    dependencies("5130114FE09DA6CE", [BASE, MATRIX, "51301154264E3947"])
    dependencies("5130115956283BA9", [BASE, MATRIX, "51301102879BA857", "513011D056923FAA", "5130112D843FED07"])
    dependencies("513011984F5B40C8", [BASE, MATRIX, "5130112D843FED07"])
    dependencies("513011A99D6EA3A8", [BASE, MATRIX])
    dependencies(MOON, [BASE, "513011ABD424EA8F", "5130114FE09DA6CE", "5130115956283BA9"])
    # Desh itself supplies the Stellar Alloy recipe; it cannot be taught only
    # after that alloy. It becomes available at the first lunar expedition.
    dependencies("513011FE61D9DE0F", [BASE, MOON])

    # Planet-specific surveys should name the planet actually visited.
    for ident in ("514011676C5FD3C1", "5140117C0F7B94F5"):
        dependencies(ident, [MERCURY if dep == VENUS else dep for dep in quests[ident]["dependencies"]])
    dependencies(MERCURY, [BASE, "513011F70694CD2B", MYTHIC])
    for ident in ("513011FBDEB88C4F", "5130111A30152678", "51301152E78DE39E", "513011AC257CA77E"):
        dependencies(ident, [*quests[ident]["dependencies"], MARS])
    for ident in ("5130117C7AD68848", "513011956693E047", "513011AA7F850AD0"):
        dependencies(ident, [*quests[ident]["dependencies"], VENUS])
    dependencies("5130111C890D830A", [BASE, "51301144D2BF0879", "513011AA7F850AD0"])
    stage("5130111C890D830A", 5)

    # Authored production lines supersede older catalogue-style prerequisites.
    # Apply them before recipe gates and stage propagation so an editorial
    # branch cannot accidentally bypass a real material requirement.
    report["branches"] = campaign_branches.apply_structure(chapters)
    # Copper wire coils are a real ingredient of Industrial Chassis. The early
    # IE branch now teaches their hand crafting before that chassis; the old
    # catalogue incorrectly locked both coils and steel production behind it.
    dependencies(CHASSIS, ["51301167DCA596B9", "5130117FBB68BE2D",
                          "513011D0113472B0", "513011B6D00A0F88", BASE])
    assert_valid_graph(quests)

    # Bring too-early item milestones in line with the campaign materials in
    # their real recipes (for example Gold/Diamond furnaces and reactor control).
    recipe_gates = mandatory_recipe_gates(recipe_rows())
    for ident, quest in sorted(quests.items()):
        needed = set()
        for task in quest.get("tasks", []):
            item = task.get("item", {})
            if task["type"] == "item" and isinstance(item, dict):
                needed.update(recipe_gates.get(item.get("id"), set()))
        needed.discard(ident)
        if not needed:
            continue
        minimum = max(GATE_STAGES[gate] for gate in needed)
        if minimum > quest_stage(quest):
            stage(ident, minimum)
        current_ancestors = _ancestors(quests, ident)
        for gate in sorted(needed, key=lambda item: (GATE_STAGES[item], item)):
            if gate in current_ancestors:
                continue
            if ident in _ancestors(quests, gate):
                # Some native sources are intentionally available before their
                # campaign summary. Keep the graph usable and report the case.
                report["warnings"].append({"quest": ident, "gate": gate, "reason": "recipe gate would create cycle"})
                continue
            dependencies(ident, [*quest.get("dependencies", []), gate])
            current_ancestors.update(_ancestors(quests, ident))

    # Dependency stage labels must not promise an item before prerequisites.
    # Iterate to a fixed point because input chapters need not be topologically
    # ordered; this changes labels, not recipes or progression dependencies.
    for _ in range(len(quests)):
        changed = False
        for ident, quest in quests.items():
            minimum = max((quest_stage(quests[dep]) for dep in quest.get("dependencies", [])), default=0)
            if minimum > quest_stage(quest):
                stage(ident, minimum)
                changed = True
        if not changed:
            break
    assert_valid_graph(quests)

    authored = json.loads(TUTORIALS.read_text(encoding="utf-8"))["quests"]
    for ident, translations in authored.items():
        if ident not in quests:
            raise ValueError(f"Tutorial references missing quest: {ident}")
        entry = translations["uk_ua"]
        locale[f"quest.{ident}.title"] = entry["title"]
        locale[f"quest.{ident}.quest_desc"] = list(entry["description"])
        for task, title in entry.get("task_titles", {}).items():
            if task not in {value["id"] for value in quests[ident]["tasks"]}:
                raise ValueError(f"Tutorial task does not belong to quest: {ident}/{task}")
            locale[f"task.{task}.title"] = title
        report["tutorial_updates"] += 1
    report["branch_tutorial_updates"] = campaign_branches.apply_locale(locale, "uk_ua")
    return report
