# Runtime optimization patch for SOQM 0.4.7

This patch builds on the updated **0.4.7 questbook**. It reduces repeated terrain
queries, fixes the lifetime of Mayhem guardian records, and skips completed
quest evidence and avoidable Dungeons & Taverns polling. Quest definitions,
identifiers, layout, text, reward pools, recipes, and recorded FTB quest progress
are preserved.

| Area | Change | Intended result |
| --- | --- | --- |
| Abandoned Tunnels | Reuse repeated height samples during one placement search | Fewer calls to the terrain generator with the original search and random sequence |
| Mayhem guardians | Native lifecycle events and bounded deadline indices | Expired or removed encounters cannot leave permanently polled server records |
| Quest automation | Check current team evidence before reading accessories or recipe output | Completed observations stop doing unnecessary work |
| Dungeons & Taverns | Event-based root criteria, direct entity-tag predicate, tracked scheduled entities, trader-local reset | Preserve the dungeon mechanics while reducing repeat checks and broad selectors |

The patch targets server work. It does not establish a client FPS improvement,
an overall TPS percentage, or a measured travel-time speedup. Millénaire village
scheduling and EvilCraft particle rendering are outside this change.

## Install the complete matching payload

Use **Minecraft 1.21.1**, **NeoForge 21.1.262**, **Java 21**, and the dependency
versions in [mods.lock.json](../metadata/mods.lock.json). The new runtime module
is pinned to Abandoned Tunnels **1.0.0**, Dungeons & Taverns **1-v4.4.4**, and
KubeJS **2101.7.2-build.377**. Its mixins target the reviewed implementations;
updating an upstream mod requires a new compatibility review and build.

Install the package built from this PR's commit on the client and dedicated
server. For a manual update of an already matching SOQM instance, stop the
instance first and apply all of the following together:

| Repository payload | Destination inside the instance |
| --- | --- |
| `overrides/mods/qm-runtime-optimizations-0.4.7.jar` | `mods/qm-runtime-optimizations-0.4.7.jar` |
| `overrides/mods/qm-quest-automation-0.4.3.jar` | `mods/qm-quest-automation-0.4.3.jar` |
| `overrides/kubejs/server_scripts/qm_mayhem_cubes.js` | `kubejs/server_scripts/qm_mayhem_cubes.js` |
| `overrides/kubejs/data/nova_structures/function/tick.mcfunction` | `kubejs/data/nova_structures/function/tick.mcfunction` |
| `overrides/kubejs/data/nova_structures/function/choose_dnt_quest_trader.mcfunction` | `kubejs/data/nova_structures/function/choose_dnt_quest_trader.mcfunction` |

**Remove `mods/qm-quest-automation-0.4.2.jar`** when installing 0.4.3. The two
quest automation versions use the same mod ID and must not coexist. Both new
modules belong on the client and server, together with their corresponding
KubeJS payload. The guardian script calls the new native class, and the D&T
function calls a command registered by that module, so a partial installation
is not supported. Adding or replacing a JAR requires a full restart; `/reload`
alone does not install native code.

The [installation guide](INSTALL.en.md) covers a complete profile update and
backup. Use the current payload from this commit to retain the updated
questbook; an older pack archive is not an equivalent source of these files.
The authored JAR names and hashes are recorded in
[authored-mods.json](../metadata/authored-mods.json), and the complete override
payload is covered by [payload.sha256.json](../metadata/payload.sha256.json).

## Abandoned Tunnels: cache one search, preserve the algorithm

[TunnelHeightCache](../src/compat/runtime-optimizations/src/qm/performance/worldgen/TunnelHeightCache.java)
wraps the original `TunnelStructure.findGenerationPoint` call. Repeated
`getFirstFreeHeight` queries share their result only inside that invocation.
`WORLD_SURFACE_WG` and `OCEAN_FLOOR_WG` use separate maps, keyed by both complete
32-bit coordinates. Generator, height accessor, and random-state identity must
also match before a cached value can be used.

The original attempts, sample positions, scoring, random draws, chosen tunnel
pieces, and placement code continue to run. A cache miss calls the original
height method. The cache holds at most **2,048 entries across both maps**; extra
queries continue through the original implementation. Worker threads have
separate frames, nested calls restore their previous frame, and `finally`
removes the active frame after either success or an exception. Results do not
survive into another placement search, dimension, or world.

This removes duplicate work rather than reducing the number of locations the
algorithm considers. Many height samples are distinct and remain necessary.
The implementation therefore makes no fixed speedup claim for generating a
new chunk or traversing a particular route.

## Mayhem guardians: authoritative tickets with a bounded lifetime

The old script enumerated every saved guardian every 200 ticks and looked up
its UUID in its original dimension. A mob removed without a death callback
could leave a record that was examined forever. Unloaded and dimension-moved
entities were also indistinguishable from permanently missing entities in
that lookup.

[GuardianLifecycle](../src/compat/runtime-optimizations/src/qm/performance/guardian/GuardianLifecycle.java)
replaces that sweep. A new guardian receives an original-UUID ticket with a
deadline **12,000 overworld game ticks** after its successful spawn. A small
persistent entity record carries the same deadline and original owner UUID.
Entity data never creates reward authority by itself.

Two native ordered indices track authoritative ticket deadlines and observed
entity deadlines. Each key has one current entry; repeated joins or deadline
replacement cannot accumulate stale heap entries. Each server tick handles at
most **128 due tickets** and **128 due entities**, plus the bounded migration
work below. Empty indices take a constant empty check. Expiry does not perform
a full ledger scan, a dimension search, or a chunk-loading request.

The reward path retains the existing one-tick death check. The native claim
also rejects a living or revived entity, checks the original expiry, and
consumes its ticket before the script spawns arena loot. Duplicate claims and
new-UUID copies of entity data cannot produce another reward. During a large
expiry burst, removing every due entity can span several ticks; reward claims
still stop at the exact original deadline.

### Removal, capture, and dimension changes

| Situation | Behavior |
| --- | --- |
| Loaded guardian reaches its deadline | Discard the guardian and consume its ticket without arena loot |
| Guardian is unloaded or captured | Preserve its original ticket only until the original deadline |
| Same UUID returns before the deadline | Retain its remaining lifetime and reward authority |
| Entity returns after the deadline | Retire it on its deferred natural join; its entity data cannot recreate a ticket |
| A copy receives a different UUID | Preserve the copied deadline, but grant no authoritative reward ticket |
| Guardian changes dimension | Observe the returning entity directly; expiry does not depend on the original dimension field |
| Death is canceled or the mob revives before the delayed check | Preserve its ticket and reject a premature reward |
| Actual Peaceful despawn occurs | Retire the ticket at the Peaceful branch of `Mob.checkDespawn` |

Capture tools may also call `discard()`, including while the current difficulty
is Peaceful. The patch therefore does not infer the cause from a generic
`DISCARDED` removal. Its targeted Peaceful hook runs after NeoForge's despawn
decision and the mob's own `shouldDespawnInPeaceful` check. Other absent tickets
expire through their deadline even if their entity never returns.

### Existing worlds and migration

The authority remains under `qm_mayhem_guardians` in the existing
`kubejs_persistent_data.nbt`. Its value becomes a **format 2** envelope with
`active` and `pending` buckets. There is no second authority file whose
independent migration save could restore an already consumed legacy ticket.

The old flat compound is wrapped without copying or enumerating all its keys.
On subsequent starts, a previous active bucket moves to pending for index
reconstruction. Migration drains at most **64 records per tick**. Direct UUID
lookups include pending buckets, so an old guardian can load, die, or be
captured before the migration cursor reaches its record.

The pending cursor is retained between ticks to avoid repeatedly scanning the
empty prefix of the underlying hash map. A consumed pending value becomes a
small non-authoritative marker; only the cursor removes pending keys. New
spawns write to the active bucket. These rules allow reward claims, removals,
and new spawns to interleave with migration without invalidating its iterator.
Saving and restarting midway preserves both unprocessed tickets and consumed
markers. The same bounded pass removes the markers and abandoned expired
records.

Old guardians did not have an entity-owned deadline. On natural join, a valid
legacy ticket supplies its original deadline. An entity carrying the reserved
`qm_mayhem_guardian` tag with neither a ticket nor a native lifetime is an
expired legacy encounter or an unauthorized legacy copy; it is retired on the
deferred join without loot. Entities without that reserved tag are unaffected.
Join handling is deferred because NeoForge can announce an entity before its
underlying chunk reaches `FULL` status.

Script/data reload does not recreate the Java event listeners, reset encounter
deadlines, or rebuild authority from entity NBT. Server stop clears transient
references; pending and active authority remain in the normal saved data.

### Rollback requires the matching save

Keep a complete pre-update world and profile backup. Once the world saves
format 2, the old flat-ledger script cannot read it. Rolling back only the JARs
or script is not a supported downgrade, and removing only the runtime JAR also
leaves native calls in the installed KubeJS data. Restore the matching
pre-update world and pack together when reverting this change. An automatic
format 2-to-flat downgrade is not included.

## Quest automation: stop observing completed evidence

[Quest automation 0.4.3](../src/compat/quest-automation/src/qm/questauto/QmQuestAutomation.java)
retains the existing task IDs, evidence caps, and the updated campaign's native
crafting and reactor conditions.

For accessories, the observer resolves the player's **current** team and checks
`curios_equipped`, `relic_experience`, and `relic_upgrade` before accessing
Curios. If all three are already recorded, it skips the inventory entirely.
Otherwise, it reads only missing evidence and stops when that evidence has
been found. Cosmetic and inactive slots remain excluded. Team membership is
not cached across passes, and completed task state does not replace evidence:
actions performed before a quest becomes available must still be recorded.

The Mekanism observer retains its existing machine hook, real-player filter,
exact 16-block spherical distance check, and one observation per nearby team.
It resolves machine position and saved evidence once per callback, skips
capped counters, and inspects smelter output only if an eligible nearby team
still needs the iron counter. It still iterates players in that dimension;
this patch does not introduce a new player spatial index or broaden the hook
to every Mekanism machine.

## Dungeons & Taverns: retain effects and scheduled behavior

The runtime applies small transformations to installed advancement and
enchantment JSON. It does not bundle complete replacement copies of those
upstream data files.

- **Root advancement:** the eight known polling criteria become event-driven
  criteria with the same IDs and requirements. Earning their source
  advancements grants the corresponding root criterion. Login and data reload
  reconcile previously earned history. A criterion is transformed only when
  it matches the reviewed upstream condition.
- **Invisible phantom boss:** the known `phantom_boss` NBT predicate becomes a
  direct `entity.getTags().contains(...)` loot condition. The phantom type,
  invisibility requirement, particle effect, equipment slots, and other
  enchantment data are preserved. The cheap type/effect checks retain their
  original ordering before the direct tag check.
- **Wither skeletons and lightning:** entity lifecycle events retain the
  relevant entities for the original five-tick `nova_structures:tick`
  schedule. The dispatcher calls D&T's existing `add_withering` and
  `find_allays` functions rather than repeating global selectors. It preserves
  the original Overworld scope and schedule phase. A still-active lightning
  bolt continues to check for allays entering its original six-block area.
  Data reload performs a one-time reconciliation of existing Overworld
  entities; that is not a periodic world scan.
- **Quest trader reset:** the reward function resets
  `minecraft:give_quest_trader_trade` for the trading executor with `@s`.
  The existing trader selection and trade function remain in place.

The D&T boss particle effect, equipment behavior, lightning/allay interaction,
discovery progression, and quest trader remain active. Increasing the tick
interval or disabling those mechanics is not the optimization used here.

## Build and validation entrypoints

The reproducible builder consumes the verified mod cache and an existing
mapped NeoForge server runtime:

```sh
python3 src/compat/build_optimizations.py \
  --runtime /absolute/path/to/pinned/server \
  --javac /absolute/path/to/jdk-21/bin/javac
```

It verifies dependency hashes and the reviewed Abandoned Tunnels target class,
then builds both authored JARs with fixed ZIP metadata. If source changes alter
the binaries, update their entries in `metadata/authored-mods.json` from the
builder output, refresh the payload lock, and run the normal repository gate:

```sh
python3 scripts/soqm.py lock
./scripts/check
```

The dedicated-server regression harness is separate from that static/package
gate. Its test mod is compiled and staged only in a fresh localhost world:

```sh
python3 scripts/performance_probe.py \
  --accept-eula \
  --java /absolute/path/to/jdk-21/bin/java \
  --runtime /absolute/path/to/pinned/server \
  --baseline-ref BASE_COMMIT
```

See [native runtime validation](RUNTIME_VALIDATION.md) for preparing the pinned
runtime and the harness's EULA, staging, reload, and shutdown behavior. Results
are recorded in `reports/performance-probe.json`; the report identifies the
fixture sources and exact staged payload. Test JARs, disposable worlds, and
original-bytecode comparison loaders are not installed in the production pack.

The optimization fixtures cover method-level tunnel result, piece-NBT and RNG
equivalence, repeated-query counts, cache isolation and cleanup, quest evidence
boundaries, guardian migration/removal/reward/reload cases, and the retained
D&T mechanics. These are behavioral and work-count checks. They do not replace
an independent travel benchmark, a whole-world block comparison, or a
graphical client session. Compare new exploration recordings using the same
seed, route, runtime settings, and hardware before attributing a wall-time
improvement to this patch.
