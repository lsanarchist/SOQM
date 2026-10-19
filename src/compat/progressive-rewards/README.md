# SOQM production evidence, 0.4.8

This small authored NeoForge adapter records actual manufacture for the
progressive quest rewards. FTB Quests remains responsible for each player's
claim; the KubeJS CustomReward handler chooses and delivers the supplies. This
adapter neither issues items nor changes a quest's task progress.

## Supported manufacture

| Route | Native commit point |
| --- | --- |
| Crafting table, player crafting grid, manual ME terminal | NeoForge `PlayerEvent.ItemCraftedEvent`, emitted by the native result slot after a positive output-take count |
| AE2 Inscriber | Successful output insertion followed by consumption of its middle input, in `tickingRequest` |
| AE2 Molecular Assembler | Native recipe assembly followed by output acceptance and replacement of all inputs with their recipe remainders |
| Mekanism Metallurgic Infuser | `CachedRecipe.setOnFinish`, after native `finishProcessing` |
| Mekanism Infusing Factory, all four tiers | The same post-finish callback, restricted to `FactoryType.INFUSING` |
| Sophisticated Storage upgrade in the world | Successful native tier replacement followed by actual consumption of the tier-upgrade item |

Custom serializers that use an ordinary crafting grid, including Sophisticated
storage/backpack upgrades and Immersive Engineering shaped recipes, use the
same result-slot event. There is no polling of players, chunks, machines, or
output inventories.

An input with the same item ID as the output makes a recipe ineligible. This
deliberately excludes repainting, resets, renames, and identity pass-through,
including Inscriber Name Press. Mekanism accepts only one unambiguous fixed
output and rejects any same-item ingredient alternative. The catalog is limited
to routes this observer can attest; this is not a universal machine tracker.

Pickup, inventory contents, recipe-book discovery, quest completion, reward
delivery, AE2 storage changes, and AE2 CPU job completion are not evidence.
AE2 processing jobs can be fulfilled by existing items, so CPU completion is
specifically unsuitable. The assembler's anonymous FakePlayer crafting event is
rejected; its native machine finish supplies the attribution instead. Creative,
spectator, and FakePlayer crafting never records evidence.

## Persistence and team boundaries

The personal FTB Teams record stores only a player's own manufacture. A party
stores its historical union. Both are under `soqm_production_v1`, with schema 1.
Own item keys and actor eligibility are scoped by player UUID; union keys are
scoped by party UUID. FTB Teams copies extraData on party creation, so explicit
UUID scopes also prevent an inherited old union from opening a new party.

Joining a party contributes only the member's own facts to its union and exposes
the current party's discoveries. Reading that union does not copy it into the
personal record. Leaving retains own facts; the former party keeps its history.
The public API is read-only:

```java
ProductionEvidence.hasProduced(ServerPlayer player, String itemId)
ProductionEvidence.getProducedItems(ServerPlayer player) // immutable effective set
ProductionEvidence.getTrackedItems()                    // immutable policy set
```

Machine placement persists the real producer UUID and their team UUID in the
block entity's NeoForge persistent data. A finish counts only if the producer
still belongs to that captured team. Moving to another team cannot redirect an
old factory's future discoveries. A real survival placement of the machine
binds its new owner/team; creative or fake placement persists an explicit denial
so native-owner fallback cannot override it.

Existing worlds have no historic adapter binding. At their first eligible real
finish, existing AE2 or Mekanism machines may bootstrap from the mod's persisted
native owner. This cannot reconstruct the owner's team before this feature was
installed; the captured-team guarantee begins when a binding is created.
Unknown offline owners fail closed until a legitimate actor state is known.
Offline manufacture by an already bound owner uses their persisted last real
survival/adventure eligibility and current FTB membership. No nearby-player
heuristic is used.

The FTB Teams party join/leave hooks run after the native FTB Quests merge. They
clear progress caches and mark the affected personal/current quest TeamData
dirty, including an offline leave by UUID. They do not translate, remove, or
reissue claim records.

## One policy and resource reload

Both the native adapter and KubeJS read `kubejs/config/soqm_rewards.json`.
The native observer uses its exact `production_items` allowlist. It requires
integer schema 2, distinct valid item IDs, and a nonempty list; plastic, HDPE,
Mayhem Cubes, air, and unregistered IDs are rejected. Any failure disables new
production recording and effective unlock queries, with an explanatory log.

Server startup loads the policy. Resource reload prepares a complete immutable
replacement and installs it atomically after item registration. Registries are
checked on the apply thread. `/reload` does not erase earned facts; removed
allowlist items become inaccessible while their historic NBT remains intact.
No second datapack policy or per-tick cache is maintained.

The installed policy also holds an immutable set of registered `Item` identities.
Crafting and machine callbacks reject untracked outputs before scanning recipe
ingredients, copying a pending output, or reading an AE2 node owner. The string
allowlist and identity set are replaced together during startup/reload and cleared
at server stop. This caches only the configured item catalogue, never a player's
production facts, current team, actor eligibility, or a machine's captured owner.
Tracked outputs still pass the same manufacture and accepted-output checks.

`tests/test_production_observer.py` executes the unchanged callback/filter methods
with counting inputs to check avoided work and retained manufacture rules. The
native fixture compares the identity filter with the exact string allowlist for
every registered item before and after reload, alongside the existing actual
manufacture, denied-actor, blocked-output, ownership and team-transition cases.
These checks establish less observer work; they do not measure TPS or FPS.

## Build

Run from the checkout root with an installed pinned server runtime and Java 21:

```sh
python3 src/compat/progressive-rewards/build.py \
  --runtime /absolute/path/to/server \
  --javac /absolute/path/to/jdk-21/bin/javac
```

`--mods` defaults to `.cache/mods`. Eight API JARs are verified against
`metadata/mods.lock.json`; the mapped NeoForge 21.1.262 server JAR is verified by
SHA-256 and precedes the unpatched Minecraft JAR on the compilation classpath.
Mixin targets use pinned production Mojmap names with `remap=false` and required
injections. Compilation disables annotation processors. ZIP entry ordering,
timestamps, permissions, and compression settings are fixed. The output is
`overrides/mods/qm-progressive-rewards-0.4.9.jar`.

`tests/runtime/ProductionEvidenceProbe.java` supplies a staged test-only native
fixture through the pack's runtime probe. It uses real recipe resolution and
output takes, real machine inputs/power/finishes, native storage tier upgrades,
party membership changes, and independent FTB disk readers. Fixtures use a fresh
test world and supplied materials; they do not write manufacture facts directly
and are not included in the shipped mod. Synthetic players use an in-memory
connection and a test-only listener that discards outbound UI packets without
fabricating a client handshake or delivery acknowledgements. This verifies the
server behavior, not graphical client interaction. The broader runner records the actual
verification result; compilation alone is not evidence of a successful game run.
