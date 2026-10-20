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


def digest(value) -> str:
    return hashlib.sha256(json.dumps(value, sort_keys=True, separators=(",", ":")).encode()).hexdigest()


def previous_questbook(chapters: list[dict]) -> list[dict]:
    """Validate the exact approved update and reconstruct its previous objects."""
    snapshot = json.loads(SNAPSHOT.read_text(encoding="utf-8"))
    if snapshot.get("schema") != 1:
        raise ValueError("Unknown questbook compatibility snapshot")
    originals = campaign_multiblocks.load()["migration"]["original_tasks"]
    result = deepcopy(chapters)
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
