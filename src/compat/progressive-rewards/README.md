# SOQM production evidence and recipe refunds, 0.4.10

This small authored NeoForge adapter records actual manufacture for the
progressive quest rewards. FTB Quests remains responsible for each player's
claim; the KubeJS CustomReward handler chooses and delivers the random supplies.
For reviewed crafting-refund quests the adapter guards the native claim before
it is spent, then delivers the guaranteed components and any preserved cube.
It never changes task progress or infers manufacture from a reward payout.

## Supported manufacture

| Route | Native commit point |
| --- | --- |
| Crafting table, player crafting grid, manual ME terminal | NeoForge `PlayerEvent.ItemCraftedEvent`, emitted by the native result slot after a positive output-take count |
| AE2 Inscriber | Successful output insertion followed by consumption of its middle input, in `tickingRequest` |
| AE2 Molecular Assembler | Native recipe assembly followed by output acceptance and replacement of all inputs with their recipe remainders |
| Mekanism Metallurgic Infuser | `CachedRecipe.setOnFinish`, after native `finishProcessing` |
| Mekanism Infusing Factory, all four tiers | The same post-finish callback, restricted to `FactoryType.INFUSING` |
| Sophisticated Storage upgrade in the world | Successful native tier replacement followed by actual consumption of the tier-upgrade item |
| Iron Furnaces upgrade in the world | Pinned `ItemUpgrade.useOn` from-tier branch, actual `shrink(1)` of that upgrade, and verified resulting tier/block entity |

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

## Guaranteed refunds and the native claim boundary

`metadata/recipe_refunds.json` records 41 output-specific fixed returns, exact
custom recipe IDs, ingredient counts and canonical recipe SHA256 values. The
compiler adds `refund: {output, items, recipe_ids}` to the existing runtime
reward mapping. Nine of these slots also contain `cube`, retaining their original
single Mayhem Cube and tier. The other 32 retain their original random packs.
All 1627 outer claim IDs remain unchanged; there is no new persistent refund ID.

The [complete 41-quest table](../../../docs/REWARDS.md#повний-перелік-компенсацій--complete-refund-list)
records each output, returned ingredient, recipe consumption and preserved base
prize. Thirty-four quests return 25–50% of a repeated direct ingredient. Seven
return one indivisible secondary component while at least two other distinct
authored campaign components remain consumed. These are fractions of the named
ingredients, not estimates of the complete recipe's value. The fixed catalogue
totals 42 component units per player who completes and claims all 41 refunds.

The design follows fixed material rewards in the pinned
[Craftoria questbook](https://github.com/TeamAOF/Craftoria/tree/7d940d2111eb39e8b24c463c9e9e3b254ed151a0/config/ftbquests/quests/chapters)
and consumed-component refunds in the official
[Questing Mayhem 1.0.3 release for Minecraft 1.7.10](https://www.curseforge.com/minecraft/modpacks/questing-mayhem/files/3374264).
Exact upstream quest IDs, recipe examples and the Questing Mayhem archive SHA256
are in `metadata/recipe_refunds.json`; the full comparison and attribution limits
are in [REWARDS.md](../../../docs/REWARDS.md#первинні-приклади--primary-source-precedents).
SOQM validates its own pinned recipe costs and requires real output manufacture.

The authored outputs have real crafting-grid routes. Direct recipe positions are
counted across every authored alternative; tags are not guessed into exact
items. A live native test checks all active crafting alternatives. EnderIO reset
recipes consume the same item and cannot pass the production predicate. AE2 cell
disassembly returns components into inventory rather than producing a crafting
output, and does not grant manufacture evidence.

Pinned FTB Quests 2101.1.36 calls `markRewardAsClaimed` before `Reward.claim` in
the four-argument `TeamData.claimReward(ServerPlayer, Reward, boolean, long)`.
Its three-argument overload delegates to that method. Cancelling or refusing
only a KubeJS callback would therefore consume the claim without its refund.
`RefundClaimMixin` injects at that exact method's HEAD and applies only to
`soqm:recipe_refund` tags. It verifies the personal manual CustomReward, matching
parent quest, valid registered policy payload, real survival player and exact
output manufacture in the player's own/current-party evidence. Denial retains
the unclaimed state and sends a localized explanation. Unrelated rewards pass
through unchanged.

The same predicate is used when the successful native `Reward.claim` invocation
is entered. The server thread cannot apply a resource reload between the HEAD
check and its synchronous custom event. A scoped context permits the native
CustomRewardEvent listener to issue fixed prizes once through ordinary transient
`ItemReward` payloads. KubeJS's `beginRandomPack` consumes the original pack's
one-time permission inside that same context; cube bundles never roll a pack.
Repeated/direct custom events outside the context cannot pay again. `finally`
restores any enclosing context or removes it, including when a callback throws.
No second persistent claim ledger and no production writes occur during payout.

Native registration happens once at mod construction, never during reload. The
immutable mapping is rebuilt at startup/resource apply, stores IDs rather than
stale quest objects, and clears on server stop. Missing or invalid mappings fail
closed before the FTB claim. A broken JS random policy can still use its normal
basic-supply fallback, while fixed native prizes retain their independently
validated delivery path.

The Iron Furnaces hook verifies the pinned 4.3.2 bytecode's sole shrink site after
its successful from-tier replacement branches. That method ends with vanilla
`Item.useOn` and returns PASS even on success; `consumesAction()` is therefore not
used as production evidence. The observer checks exact consumed-item identity,
a positive one-item count delta, different from/to blocks, the actual final
block and native furnace block entity, plus the existing survival/team filters.
The direct Netherite Furnace recipe and the consumed upgrade recipe both cost
two Netherite Ingots; either supported manufacture route earns one back.

**Existing worlds:** already claimed slots remain claimed, without an automatic
top-up. Forty of the 41 refund outputs are newly observed; old inventory and
completed quests cannot prove historical manufacture. An old completed but
unclaimed quest for those outputs requires a new real craft or supported world
upgrade. The previously observed Ultimate Energy Cube can use its retained
production fact. Current-party manufacture enables each member's personal
claim, while joining/leaving never resets the saved player/reward claim key.

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

`--mods` defaults to `.cache/mods`. Nine API JARs are verified against
`metadata/mods.lock.json`; the mapped NeoForge 21.1.262 server JAR is verified by
SHA-256 and precedes the unpatched Minecraft JAR on the compilation classpath.
Mixin targets use pinned production Mojmap names with `remap=false` and required
injections. Compilation disables annotation processors. ZIP entry ordering,
timestamps, permissions, and compression settings are fixed. The output is
`overrides/mods/qm-progressive-rewards-0.4.10.jar`.

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

`ProgressiveRewardsProbe` additionally checks live recipe costs, early claims
remaining unspent, unknown/invalid mappings, correct parent ownership, unrelated
rewards during invalid refund policy, actual output manufacture, two independent
team-member refunds, exact cube preservation, reward delivery not unlocking its
ingredient, repeated event rejection, team transfer, disk persistence and fresh
post-reload claims. Fixture materials are supplied; actual crafting/output takes,
native claims and native item delivery are exercised.

The **0.4.10 native run passed**, including all 43 reviewed cost recipes,
positive/negative furnace upgrades, early-claim denial, two members' personal
refunds, preserved cubes, no payout-created production evidence, duplicate-event
rejection and a fresh member's post-reload claim. See the
[versioned verification report](../../../reports/verification-0.4.10.md) and
locally generated machine-readable results in `reports/runtime-probe.json`. These
results establish server behavior, not a graphical client or full playthrough.
