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


def digest(value) -> str:
    return hashlib.sha256(json.dumps(value, sort_keys=True, separators=(",", ":")).encode()).hexdigest()


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
    result = previous_storage_refunds(previous_torchmaster(chapters))
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
