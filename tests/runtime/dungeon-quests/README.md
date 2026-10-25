# Optional Dimensional Dungeons quest integration gate

This independent test-only mod uses the existing verified full-pack runtime
harness. It does not change or run the older manufacturing/reward fixtures, and
it never enters the released mod payload or reads a real player's save.

```bash
python tests/runtime/dungeon-quests/run_probe.py \
  --runtime .work/native-runtime \
  --java .cache/tooling/jdk21/bin/java \
  --accept-eula --timeout 1200
```

The default migration baseline is the actual pre-branch commit
`46b9bdab3a87271360e4588e4f370300510be63e`. Do not replace it with a post-branch
HEAD when claiming existing-save migration coverage. A release gate must pass
without `--allow-unlocked-payload`.

The probe checks all 12 optional quests, 13 stable tasks, 12 personal rewards,
the native prerequisite graph, and the absence of new prerequisites on old
campaign quests. It exercises FTB's inventory detector and task/reward request
handlers, including their prerequisite and completion checks. Native request
work runs on the server thread with real synthetic player/team objects and an
in-memory transport that captures system-chat evidence and discards outbound UI
packets. The preexisting root is seeded through FTB's native completion event;
this preserves its flexible-progression reconciliation of inventory progress
recorded before prerequisites.

Goal checks match and assemble the registered vanilla DD guide recipe and use
its actual Patchouli guide component, DD's key activation ritual,
vanilla inventory advancement criteria and DD's portal teleport routine. The
portal fixture supplies a minimal basic/advanced room descriptor in native
`DungeonData`; it validates destination classification and advancement wiring,
not generation or completion of a whole dungeon. The two-crown threshold, all
three charger variants, and eight trophy types acquired over time are checked.
Task detection must preserve inventory contents and components.

Rewards are requested through native `ClaimRewardMessage.handle`, not directly
through the lower-level `TeamData.claimReward` method that lacks the initial
quest-completion check. Both players claim each reward once; duplicate requests
are checked before and after an independent disk read and resource reload.
The actual `/soqm rewards pool expedition` command must resolve registered,
eligible Expedition prizes without a silent fallback for both recipients. The
wrapper also rejects every `[SOQM rewards]` warning from the native KubeJS log,
so emergency supply delivery cannot make the reward gate pass.

The migration fixture reads the old questbook through FTB's native loader,
preserves every old object ID and a nonempty snapshot of completed/partial
progress and personal claim timestamps, and checks that new goals and rewards
start empty. Player advancements are also saved and read by an independent
native reader after reload.

Evidence is written to `reports/dungeon-quest-probe.json` and the reported
`.work/dungeon-quest-probe/` directory. This gate does not certify client layout,
player input, a complete dungeon playthrough or performance.
