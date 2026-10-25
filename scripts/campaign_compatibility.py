"""Verify the reviewed additive task/reward update against the old save contract.

This is build-time validation, not a player-data migration. Existing FTB object
IDs and claim scopes remain unchanged. Restoring the recorded old objectives in
a copy lets tests keep checking the immutable historical content fingerprints.
"""

from __future__ import annotations

from copy import deepcopy
import hashlib
import json
from pathlib import Path

import campaign_multiblocks

ROOT = Path(__file__).resolve().parents[1]
SNAPSHOT = ROOT / "metadata/questbook_compatibility.json"
CONTENT_UPGRADE = ROOT / "metadata/storage_refund_compatibility.json"
HARDCORE_UPGRADE = ROOT / "metadata/hardcore_workshop_compatibility.json"
HARDCORE_BASE = "95421eeda09f2e3447e6e9d3189a1e43d9c41381"
HARDCORE_IDENTITY = "b883503b48a5d90e32085663878d7097d371b96478a2a0143bc266036e0dd0ca"
DUNGEON_UPGRADE = ROOT / "metadata/dimensional_dungeons_quest_compatibility.json"
DUNGEON_BASE = "46b9bdab3a87271360e4588e4f370300510be63e"
DUNGEON_IDENTITY = "609df13b427602821a5e8dfd0577f6df352b16248b608806bc8f7710e743cd87"
DUNGEON_CONTRACT = "116ec912172687d0306ab9496ec119deeaca608a8f10db22020af68af22e9b9a"


def digest(value) -> str:
    return hashlib.sha256(json.dumps(value, sort_keys=True, separators=(",", ":")).encode()).hexdigest()


def previous_dimensional_dungeons(chapters: list[dict]) -> list[dict]:
    """Remove only the twelve reviewed DD additions and verify the prior book.

    The old contract includes dependencies and optional flags as well as exact
    objectives, reward claims and chapter ownership. New exploration quests
    must not become a hidden prerequisite of an existing production route.
    This verifies definitions only; no player progress is read or rewritten.
    """
    from campaign_refresh import persistent_identity

    snapshot = json.loads(DUNGEON_UPGRADE.read_text(encoding="utf-8"))
    quest_ids = {f"514E11{number:010X}" for number in range(1, 13)}
    image_ids = ({f"514E14{number:010X}" for number in range(1, 13)}
                 | {f"514E15{number:010X}" for number in range(1, 10)})
    if (snapshot.get("schema") != 1 or snapshot.get("base_commit") != DUNGEON_BASE
            or snapshot.get("identity_sha256") != DUNGEON_IDENTITY
            or snapshot.get("contract_sha256") != DUNGEON_CONTRACT
            or set(snapshot.get("added_quests", {})) != quest_ids
            or set(snapshot.get("added_images", {})) != image_ids):
        raise ValueError("Unknown Dimensional Dungeons additive compatibility snapshot")
    result = deepcopy(chapters)

    def owner(ident, field):
        found = [chapter for chapter in result
                 if any(obj["id"] == ident for obj in chapter.get(field, []))]
        if len(found) != 1:
            raise ValueError(f"A reviewed DD {field} ID is missing or duplicated: {ident}")
        return found[0]

    for chapter in result:
        for quest in chapter["quests"]:
            if quest["id"] not in quest_ids and quest_ids.intersection(quest.get("dependencies", [])):
                raise ValueError(f"An existing quest depends on the optional DD branch: {quest['id']}")
    for ident, addition in snapshot["added_quests"].items():
        chapter = owner(ident, "quests")
        quest = next(q for q in chapter["quests"] if q["id"] == ident)
        if (chapter["filename"] != addition["chapter"]
                or digest(quest["tasks"]) != addition["tasks_sha256"]
                or digest(quest["rewards"]) != addition["rewards_sha256"]
                or quest.get("dependencies", []) != addition["dependencies"]
                or quest.get("optional", False) is not True
                or quest.get("can_repeat", False) or quest.get("repeatable", False)
                or [t["id"] for t in quest["tasks"]] != addition["task_ids"]
                or [r["id"] for r in quest["rewards"]] != addition["reward_ids"]):
            raise ValueError(f"Unreviewed additive DD quest: {ident}")
        chapter["quests"].remove(quest)
    for ident, filename in snapshot["added_images"].items():
        chapter = owner(ident, "images")
        if chapter["filename"] != filename:
            raise ValueError(f"An additive DD image moved: {ident}")
        chapter["images"] = [image for image in chapter["images"] if image["id"] != ident]
    contract = {q["id"]: {"chapter": chapter["filename"], "tasks": q["tasks"],
                           "rewards": q["rewards"], "dependencies": q.get("dependencies", []),
                           "optional": q.get("optional", False), "can_repeat": q.get("can_repeat", False)}
                for chapter in result for q in chapter["quests"]}
    if digest(contract) != DUNGEON_CONTRACT:
        raise ValueError("Dimensional Dungeons update changed an unreviewed original objective, owner, dependency, optional status or reward")
    groups = json.loads((ROOT / "overrides/config/ftbquests/quests/chapter_groups.snbt")
                        .read_text(encoding="utf-8"))["chapter_groups"]
    if persistent_identity(result, groups) != DUNGEON_IDENTITY:
        raise ValueError("Dimensional Dungeons update changed an unreviewed original saved identity")
    return result


def previous_hardcore_workshop(chapters: list[dict]) -> list[dict]:
    """Check the additive lessons, chapter moves and 29 extra cube units.

    This reconstructs the immutable 0.4.11 definitions only for validation.
    Player saves are never rewritten and a changed reward count keeps its old
    personal claim key. The exact reviewed lists prevent a new whole-book hash
    from hiding an accidental removal, task replacement or reward reissue.
    """
    snapshot = json.loads(HARDCORE_UPGRADE.read_text(encoding="utf-8"))
    if (snapshot.get("schema") != 1 or snapshot.get("base_commit") != HARDCORE_BASE
            or snapshot.get("identity_sha256") != HARDCORE_IDENTITY):
        raise ValueError("Unknown hardcore workshop compatibility snapshot")
    result = previous_dimensional_dungeons(chapters)
    owners = {chapter["filename"]: chapter for chapter in result}

    def owner(ident, field):
        found = [chapter for chapter in result
                 if any(obj["id"] == ident for obj in chapter.get(field, []))]
        if len(found) != 1:
            raise ValueError(f"A reviewed {field} ID is missing or duplicated: {ident}")
        return found[0]

    for ident, addition in snapshot["added_quests"].items():
        chapter = owner(ident, "quests")
        quest = next(q for q in chapter["quests"] if q["id"] == ident)
        if (chapter["filename"] != addition["chapter"]
                or digest(quest["tasks"]) != addition["tasks_sha256"]
                or digest(quest["rewards"]) != addition["rewards_sha256"]
                or quest.get("can_repeat", False)):
            raise ValueError(f"Unreviewed additive hardcore quest: {ident}")
        chapter["quests"].remove(quest)
    for ident, filename in snapshot["added_images"].items():
        chapter = owner(ident, "images")
        if chapter["filename"] != filename:
            raise ValueError(f"An additive hardcore image moved: {ident}")
        chapter["images"] = [image for image in chapter["images"] if image["id"] != ident]
    for field in ("quests", "images"):
        for ident, move in snapshot["moved_" + field].items():
            chapter = owner(ident, field)
            if (chapter["filename"] != move["after"] or move["before"] not in owners
                    or move["before"] == move["after"]):
                raise ValueError(f"Unreviewed hardcore {field} ownership change: {ident}")
            obj = next(obj for obj in chapter[field] if obj["id"] == ident)
            chapter[field].remove(obj)
            owners[move["before"]][field].append(obj)
    quests = {q["id"]: q for chapter in result for q in chapter["quests"]}
    for field in ("tasks", "rewards"):
        for ident, change in snapshot[field].items():
            if ident not in quests or digest(quests[ident][field]) != change["after_sha256"]:
                raise ValueError(f"Unreviewed hardcore {field} change: {ident}")
            current, before = quests[ident][field], change["before"]
            key = (lambda obj: (obj["id"], obj.get("team_reward", True))) if field == "rewards" else (lambda obj: obj["id"])
            if [key(obj) for obj in current] != [key(obj) for obj in before]:
                raise ValueError(f"A saved hardcore {field} ID or claim scope changed: {ident}")
            quests[ident][field] = deepcopy(before)
    contract = {q["id"]: {"chapter": chapter["filename"], "tasks": q["tasks"],
                           "rewards": q["rewards"], "can_repeat": q.get("can_repeat", False)}
                for chapter in result for q in chapter["quests"]}
    if digest(contract) != snapshot["contract_sha256"]:
        raise ValueError("Hardcore update changed an unreviewed original objective, owner or reward")
    return result


def previous_torchmaster(chapters: list[dict]) -> list[dict]:
    """Remove only the reviewed additive torch lesson from a copy of the book."""
    identity = json.loads((ROOT / "metadata/campaign_identity.json").read_text(encoding="utf-8"))
    snapshot = identity["previous_torchmaster"]
    quest_id, image_id = "514B110000000001", "514B140000000001"
    if (snapshot.get("base_commit") != "226485323c223a7bb6c01354fc88418d08c4836f"
            or snapshot.get("added_quests") != [quest_id]
            or snapshot.get("added_images") != [image_id]):
        raise ValueError("Unknown Torchmaster additive compatibility snapshot")
    result = deepcopy(chapters)
    quest_owners = [chapter for chapter in result
                    if any(quest["id"] == quest_id for quest in chapter["quests"])]
    image_owners = [chapter for chapter in result
                    if any(image["id"] == image_id for image in chapter.get("images", []))]
    if (len(quest_owners) != 1 or len(image_owners) != 1
            or quest_owners[0]["filename"] != "qm_v030_22_millenaire"
            or image_owners[0] is not quest_owners[0]):
        raise ValueError("The additive torch quest or caption was removed or moved")
    chapter = quest_owners[0]
    chapter["quests"] = [quest for quest in chapter["quests"] if quest["id"] != quest_id]
    chapter["images"] = [image for image in chapter["images"] if image["id"] != image_id]
    return result


def previous_storage_refunds(chapters: list[dict]) -> list[dict]:
    """Check the 0.4.10 edits before reconstructing the already reviewed 0.4.9."""
    snapshot = json.loads(CONTENT_UPGRADE.read_text(encoding="utf-8"))
    if snapshot.get("schema") != 1 or snapshot.get("base_commit") != "1a7ecd3b5691888bc807667eb90cbd913e191934":
        raise ValueError("Unknown storage/refund compatibility snapshot")
    result = deepcopy(chapters)
    quests = {q["id"]: q for chapter in result for q in chapter["quests"]}
    for field in ("tasks", "rewards"):
        for ident, change in snapshot[field].items():
            if ident not in quests or digest(quests[ident][field]) != change["after_sha256"]:
                raise ValueError(f"Unreviewed storage/refund {field} change: {ident}")
            current, before = quests[ident][field], change["before"]
            key = (lambda obj: (obj["id"], obj.get("team_reward", True))) if field == "rewards" else (lambda obj: obj["id"])
            if [key(obj) for obj in current] != [key(obj) for obj in before]:
                raise ValueError(f"A saved storage/refund {field} ID or claim scope changed: {ident}")
            quests[ident][field] = deepcopy(before)
    return result


def previous_questbook(chapters: list[dict]) -> list[dict]:
    """Validate the exact approved update and reconstruct its previous objects."""
    snapshot = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    if snapshot.get("schema") != 1:
        raise ValueError("Unknown questbook compatibility snapshot")
    originals = campaign_multiblocks.load()["migration"]["original_tasks"]
    result = previous_storage_refunds(previous_torchmaster(previous_hardcore_workshop(chapters)))
    quests = {q["id"]: q for chapter in result for q in chapter["quests"]}
    if not (set(snapshot["tasks_after"]) | set(snapshot["rewards"])) <= set(quests):
        raise ValueError("A migrated quest was removed or moved to a different ID")
    for ident, expected in snapshot["tasks_after"].items():
        quest = quests[ident]
        if digest(quest["tasks"]) != expected:
            raise ValueError(f"Unreviewed multiblock objective change: {ident}")
        old = originals[ident]
        current_ids = {task["id"] for task in quest["tasks"]}
        if not {task["id"] for task in old} <= current_ids:
            raise ValueError(f"A saved task was removed or replaced: {ident}")
        quest["tasks"] = deepcopy(old)
    for ident, change in snapshot["rewards"].items():
        quest = quests[ident]
        if digest(quest["rewards"]) != change["after_sha256"]:
            raise ValueError(f"Unreviewed reward replacement: {ident}")
        claim_keys = lambda rewards: [(r["id"], r.get("team_reward", True)) for r in rewards]
        if claim_keys(quest["rewards"]) != claim_keys(change["before"]):
            raise ValueError(f"A saved personal reward claim would change: {ident}")
        quest["rewards"] = deepcopy(change["before"])
    return result
