# Skill advancement optimization

This configuration patch replaces nine `minecraft:tick` criteria that queried
player NBT with explicit grants from the installed Pufferfish Skills mod. It is
based on SOQM commit `e71db8398ddc9f07e85981fac942f52dd23f447d` and requires no
new JAR or JavaScript handler.

## Why this changes the expensive path

The old criteria checked tags such as `qm_skill_guardian_1` through a player
NBT predicate. Minecraft's `NbtPredicate.getEntityTagToCompare` serializes the
entity for this check, including mod attachments, although the criterion only
needs one tag. In the two supplied spark captures this method accounted for
10,400 / 117,144 ms (8.88%) and 9,132 / 117,180 ms (7.79%) of sampled server-thread
time. These are inclusive sampled stack times, not additional wall time or a
measured speedup. The profiles do not identify individual advancement IDs, so
the entire NBT cost cannot be attributed to these nine criteria.

Sources: [first spark profile](https://spark.lucko.me/4VzPbrUrnA),
[second spark profile](https://spark.lucko.me/4SfpElO9Ce), and the
[original criteria](https://github.com/lsanarchist/SOQM/tree/e71db8398ddc9f07e85981fac942f52dd23f447d/overrides/kubejs/data/qm/advancement/skills).

## Implementation

Each milestone definition keeps its existing rewards and appends one
`puffish_skills:command` reward. For example:

```json
{
  "type": "puffish_skills:command",
  "data": {
    "unlock_command": "execute unless entity @s[advancements={qm:skills/guardian=true}] run advancement grant @s only qm:skills/guardian unlocked"
  }
}
```

The corresponding criterion becomes `{"trigger": "minecraft:impossible"}`.
It remains grantable by the command and no longer subscribes to the tick
trigger. The selector reads advancement completion; it does not query NBT.

| Skill definition | Advancement | Named criterion |
| --- | --- | --- |
| `root` | `qm:skills/root` | `unlocked` |
| `guardian_1` | `qm:skills/guardian` | `unlocked` |
| `warrior_1` | `qm:skills/warrior` | `unlocked` |
| `explorer_1` | `qm:skills/explorer` | `unlocked` |
| `engineer_1` | `qm:skills/engineer` | `unlocked` |
| `guardian_6` | `qm:skills/capstone` | `guardian` |
| `warrior_6` | `qm:skills/capstone` | `warrior` |
| `explorer_6` | `qm:skills/capstone` | `explorer` |
| `engineer_6` | `qm:skills/capstone` | `engineer` |

## Existing worlds and respec

- Advancement IDs and criterion names stay the same, so previously earned
  criteria retain their saved history. Capstone keeps its single OR group:
  completing any one branch at level 6 is sufficient.
- The completion guard skips already completed advancements, including
  capstone after one branch. Each command grants only its named criterion.
- Pufferfish reapplies rewards for saved native `unlocked_skills` on player
  login and after data reload. Adding an `unlock_command` therefore reconciles
  an allocated skill whose milestone has not yet been recorded. This includes
  the free root with zero spent points. Arbitrary manually added player tags
  without a corresponding native skill allocation are not a migration source.
- `unlock_command` is intentional: the alternative `command` runs only on a
  direct unlock action and does not cover this reconciliation. No
  `lock_command` is added, so respec removes active tags/attributes through the
  existing rewards without revoking historical advancement completion.
- The six `points_stage_*` advancements and their source-specific two-point
  functions, skill costs, attribute/tag rewards, category layout, retraining
  script and FTB Quests IDs are unchanged.

## Version evidence and validation

The [official command reward documentation](https://puffish.net/skillsmod/docs/creators/configuration/rewards/built-in/command)
documents the reward format. The lifecycle behavior above was additionally
checked against the exact pinned
[`puffish_skills-0.19.2-1.21-neoforge.jar`](https://www.curseforge.com/minecraft/mc-mods/puffish-skills/files/9101377),
SHA-256 `d96e4b5735bd05c7210892f75bf078a0a4cac67a8d99f28076e5a4a57fd4e2cd`.

In that JAR, `CommandReward.update` runs `unlock_command` when the player's
unlocked count increases; the command source is bound to that player. The
permission level is the server's function permission level, as documented
upstream. `SkillsMod.showCategory` calls `updateRewards`; player join calls
`updateAllCategories`, while reload resets rewards, reloads configuration and
updates all online players. NeoForge's login and global datapack-sync hooks
invoke those paths. The exact NeoForge 21.1.262 `PlayerList` patch posts global
datapack sync after `PlayerAdvancements.reload`, so reconciliation sees the
reloaded advancement progress. There is no extra per-tick or periodic handler
in this patch.

The NeoForge ordering was checked in `patches/net/minecraft/server/players/PlayerList.java.patch`
inside the [21.1.262 userdev archive](https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-userdev.jar),
SHA-256 `d30f0b5acd70334d2d628f589e0db6977226c9ec2f353cc1165b4efdee62bf3d`.

`tests/test_skills.py` checks the real shipped JSON: all nine command mappings,
criterion-specific guarded grants, absence of milestone NBT/tick predicates,
the saved criterion names and OR requirement, free root, existing tags and the
six independent point sources. These are offline configuration regressions,
not a simulated Minecraft lifecycle. Run the complete repository gate with:

```sh
./scripts/check
```

See [verification status](../reports/README.md) for the completed checks. No
new world/client playthrough or before/after TPS benchmark was performed for
this patch. It removes these advancement polling predicates; it does not
resolve unrelated world-generation, entity-AI or rendering costs.

## Applying the patch

For the source repository, apply the patch on the base commit above (or merge
its PR), then run `./scripts/check` to build the installation package.

For an existing SOQM game/server instance using the pinned version, copy the
following seven patched files from `overrides/` into the instance directory:

```text
config/puffish_skills/categories/mayhem/definitions.json
kubejs/data/qm/advancement/skills/root.json
kubejs/data/qm/advancement/skills/guardian.json
kubejs/data/qm/advancement/skills/warrior.json
kubejs/data/qm/advancement/skills/explorer.json
kubejs/data/qm/advancement/skills/engineer.json
kubejs/data/qm/advancement/skills/capstone.json
```

Replace all seven together and restart the game/server. On a dedicated server,
apply them to its instance; in singleplayer, apply them to the local instance.
No save-file editing, advancement reset or new JAR is needed. Existing custom
edits to these same files must be merged instead of overwritten.
