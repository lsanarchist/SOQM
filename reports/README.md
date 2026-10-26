# Verification status

## SOQM 0.4.22 — combat treasure and two authored expeditions

The optional DD branch now includes Iron Citadel and Obsidian Abyss, with
18 original room templates and separate chest reward profiles. The production
payload contains **1,912 files**. All **131 third-party dependency pins** remain
identical to main commit `5ac13e8d339cc72031cce7aad2776e0a062b9b2b`.
The 15 authored modules remain the same set; only chest balance and dungeon
encounters advance to 0.4.22. Quest UI remains the reviewed 0.4.8 artifact.

Before publication, the presentation-only main update
`a080e9582576c582d1462ec65ce80d2ac89b3648` was integrated. Its new README
gallery, grouped mod list, banner and gallery packaging are retained. This
integration leaves the native-tested runtime payload and its manifest hash
unchanged.

The current quest/task/reward objects and dependency graph are byte-identical
to that baseline. Only the existing advanced-dungeon explanation and its two
language fragments change. Totals remain **1,654 quests, 1,940 tasks and 1,655
outer rewards** across 37 chapters; no claimed reward gets a new identity.

Validation on **2026-10-25** passed the complete `./scripts/check` gate:
**205 Python tests without skips**, both JavaScript policy suites, all generated
data/localization freshness checks, source/binary provenance, installation ZIP
CRC and a byte-identical rebuild. All 12 coordinate previews also pass their
freshness check. The local run used the complete pinned dependency cache and
Temurin Java 21.0.13+9; CI may omit dependency-backed checks without that cache.

Both changed production modules were compiled twice from the final sources
against the pinned mapped NeoForge 21.1.262 server and exact native mod APIs:

| Module | Bytes | SHA-256 |
|---|---:|---|
| `qm-chest-balance-0.4.22.jar` | 13,777 | `0eba44710130ae6e41890bb753f09af41e446f2aba8d8dbb0d44be64f7563c45` |
| `qm-dungeon-encounters-0.4.22.jar` | 28,064 | `b1bb66720e46d8221619815f00f61a197a26c0018a441b284271041320b9a46c` |

The chest policy is limited to the exact DD routes. Guaranteed primary combat
prizes survive the final fill policy; ordinary supplies retain finite budgets.
The new room generator checks byte freshness, unique templates, finite markers,
conservative spawn clearance, connected exits and native return-portal positions.
The authored-theme load correction restores only matching Citadel/Abyss room
records in DD's completed and pending maps; other records keep native behavior.

### Native expedition, treasure and persistence gate

The complete isolated full-pack server gate passed on **2026-10-25** against
the locked production payload, without `--allow-unlocked-payload`. The manifest
SHA-256 is
`091aedefd8b2479f4fc02f4ea5bd861070f316a395f7dfac873db1312624a6f8`.
The detailed local result is `reports/dungeon-encounter-probe.json`, SHA-256
`a981fb383a3874ea9ca65be1434edaa4df6e462ac98849d71e59d8b5ff38dbc1`.
Its fixture-source hashes, verified dependency pins, console hash and copied
production-file hashes identify the exact tested inputs.

The run completed in **81.96 seconds** with **75,765 independent assertions**:

| Fixture | Assertions | Native coverage |
|---|---:|---|
| Existing encounter suite | 335 | Real Elite abilities, Apotheosis tier application, Divine selection and boss policies, bounded activation, native entity NBT/load and resource reload |
| Original DD loot preservation | 39,657 | 12 original routes, 20 protected item types, three native activated-key variants, 1,536 final-fill cases and 768 loaded-table rolls |
| Combat rewards and personal Lootr | 30,427 | 15 combat-bearing outer tables, all five WorldTiers, 3,600 loaded rolls, native reward components, final compaction and personal inventory persistence |
| New authored expeditions | 5,346 | All 18 templates in four rotations, native guards and locks, walking routes, earned-key generation, portal travel and room-data persistence |

The reward counter is **30,427 cumulatively after reload**. Its initial
30,422 assertions are included in that value and are not added again.
The two loot route sets overlap: the 15 combat-bearing tables include the four
new expedition routes; they are not 15 additional routes beyond the original 12.

The combat sample preserved **3,208 generated prizes** through the real final
fill: **1,480 actual Apotheosis affix items** and **1,728 valid native gems**.
Their loaded rarity, gem and purity weights were checked against the actual
opening player's WorldTier. Only the temporary reward marker was removed;
unrelated custom data and native item components survived. Existing native DD
valuables were retained, and ordinary settlement retention kept its prior policy.

The same 3,600-roll sample produced **317 earned expedition keys** through the
actual NeoForge modifier/helper-table path. Advanced hard treasure produced
52 Citadel keys, its legacy alias 58, Citadel treasure 49 repeat-Citadel and
97 Abyss keys, and Abyss treasure 61 repeat-Abyss keys. Crazy and other ordinary
routes produced no out-of-gate theme-4/5 keys. Separately, **48 native blank-theme
rituals and 48 native offhand reward rolls** covered only original themes 1–3.

All **72 native room placements** passed actual spawned-entity collision checks
for **224 hostiles**, door connectivity in every rotation, and climbable recovery
from **2,176 pit-floor positions**. The **16 locked end-room variants/rotations**
each had one native guard carrying the matching room key. Actual lethal player
damage produced exactly one matching key. Lootr's native conversion preserved
the DD lock, and its real access/open handlers rejected empty or wrong hands
before accepting that key. Two survival-player objects received distinct
personal inventories for each locked chest, and reopening preserved their loot.
Two additional reward-probe personal inventories retained their exact contents
through a real resource reload. Already generated personal loot was not rerolled;
the improved tables apply to fresh personal rolls.

Two keys earned from the loaded reward tables also exercised native keyhole
insertion and complete generated layouts:

| Expedition | Configured room target | Actual sampled rooms | Locked end rooms | Native hostiles |
|---|---:|---:|---:|---:|
| Iron Citadel, theme 4 | 32 | 33 | 3 | 119 |
| Obsidian Abyss, theme 5 | 42 | 43 | 8 | 134 |

These are reproducible sampled layouts, not promises of an exact room count for
every key. DD uses a soft designer target and closes its remaining branches.
Both samples completed the native build queue, rejected incomplete portal
frames, accepted the full crowned frame, and passed actual gold-portal entry
and return. Every completed room rejected another build request, adding no new
mobs or regenerated treasure. Pending and completed room records retained their
themes through native serialization/loading. Another **16 save/load/save cases**
checked the scoped upstream repair: four matching new-theme records across the
two maps were restored; twelve foreign, legacy or mismatched records retained
native behavior. Key counters, coordinates, room types and rotations were kept.

Production files remained unchanged. Recipes reloaded **20,062 → 20,062**.
KubeJS startup/server errors and fatal-server errors were all **zero**, as were
DD unknown-marker, failed-placement and missing-return-doorway messages. The
server saved every dimension and exited with code **0**, without forced
termination. Upstream nonfatal warnings still exist; these results do not claim
an entirely warning-free console.

Reproduce from the repository root after restoring the verified dependency
cache and installed NeoForge 21.1.262 runtime:

```sh
PYTHONDONTWRITEBYTECODE=1 python3 tests/runtime/encounter-validation/run_probe.py \
  --runtime .work/native-runtime \
  --java .cache/tooling/jdk21/bin/java \
  --accept-eula --timeout 1200
```

The fixture JAR remains test-only and is excluded from the installation pack.
This gate uses actual `ServerPlayer` objects with an in-memory packet transport;
it does not perform a graphical-client handshake or GUI rendering check. Native
NBT constructors and entity load/join cycles are exercised without starting a
second JVM. The synchronous portal fixture models an expired cooldown through
the native API and acknowledges the player's completed dimension transition.
The run is not a survival or full boss-fight playthrough, long-term multiplayer
balance study, or whole-server TPS benchmark.

The installation archive includes this report; its final checksum is recorded
in the accompanying `SHA256SUMS`, avoiding a self-referential archive hash.

## SOQM 0.4.21 — optional Dimensional Dungeons quests

The additive chapter-23 branch contains **12 optional quests**, **13 tasks**
and **12 personal Expedition rewards**. The complete book now has **1,654
quests, 1,940 tasks and 1,655 outer rewards** across 37 chapters. There are
still 193 cube milestones, 222 guaranteed cube units and 41 crafting refunds.

Validation on **2026-10-25** passed `./scripts/check`: **196 Python tests
without skips**, both JavaScript policy suites, generator/localization
freshness, source/binary provenance and deterministic installation packaging.
The existing coordinate previews also pass their freshness check. All 131
third-party pins are byte-for-byte unchanged from the pre-quest commit.

The additive compatibility boundary reconstructs the exact previous book:

- Baseline commit: `46b9bdab3a87271360e4588e4f370300510be63e`.
- Prior saved identity: `609df13b427602821a5e8dfd0577f6df352b16248b608806bc8f7710e743cd87`.
- Prior full quest/task/reward/dependency contract: `116ec912172687d0306ab9496ec119deeaca608a8f10db22020af68af22e9b9a`.
- Current saved identity: `23e33eda8168fd752437188882c377d5bab857f54d5a91b9760770f2df9d402a`.

Old objectives, owners, personal claims, prerequisites and optional flags are
checked before older historical snapshots. Negative controls reject changed
old tasks/claims, a new dependency on the dungeon branch, and shared scope on
a new personal reward. Item tasks do not consume their inventory matches;
activation, entry and collection tasks use all required native advancement
criteria. The upstream-name reader preserves quoted URLs and comment-like
text while accepting the comments/literal newlines present in pinned DD assets.

Quest UI **0.4.8** is a deterministic resource update: both real rebuilds produced
the same **134,881-byte** JAR with SHA-256
`04d8651f16f83d8e82e63535ec70030c1a0d768730a06c2f47ae03942fcbb6d7`.
Its complete set of 41 classes remains byte-identical to the reviewed 0.4.5
baseline. The resource regression suite checks class/resource identity and
rejects changed Java, mixins, dependencies or resource paths. This update does
not claim a new graphical-client or Mixin-transformation run.

The locked production payload remains **1,861 files**. Its manifest SHA-256 is
`001c87b666747d77e215319d8b0d2009def27d6e7d89d33c12cf20235c933d25`.
The installation archive includes this completed report. Its final checksum
is recorded separately in the package's accompanying `SHA256SUMS` file.


### Native quest and save validation

The isolated full-pack server passed **7,908 assertions** against the locked
payload, with no development override. All 12 quests, 13 tasks and eight native
DD advancements loaded and completed through real inventory/criterion handlers.
The native recipe manager matched and assembled `recipe_item_guidebook_2` into
the exact DD Patchouli book. Another book was rejected; a correct book with an
extra custom name was accepted. The obsolete duplicate guide recipe still logs
an upstream warning, while its valid vanilla alternate provides the survival craft.

DD's actual basic-key ritual and basic/advanced teleport routines granted the
expected criteria. The native inventory predicates accepted all three charger
variants, rejected one crown before accepting two, and required all eight trophy
types acquired over time. Quest detection preserved every inventory component.

Two party members made **24 successful personal claims** through FTB's real
reward request handler. A premature request was rejected, as were **24 duplicate
requests before reload and 24 after an independent disk read/resource reload**.
The real Expedition command exposed 12 eligible entries for each recipient;
there was no silent pool fallback and no `[SOQM rewards]` warning in the
KubeJS server log.

The pre-branch fixture retained all **1,642 old quests, 1,927 tasks and 1,643
reward IDs**. Its nonempty historical sample contained 24 completed quests,
40 partial tasks and 25 personal claim timestamps. Independent native reads
preserved that snapshot, all new completions, eight advancements and 24 new
personal claims. Recipes reloaded **20,062 → 20,062**; KubeJS startup/server
and fatal-server errors were all zero. The server saved and exited with code
**0**, without forced termination, in **80.81 seconds**. Production files
remained unchanged.

Reproduce with the pinned dependency cache and installed NeoForge runtime:

```sh
python3 tests/runtime/dungeon-quests/run_probe.py \
  --runtime <installed-neoforge-server> --java <java-21> \
  --accept-eula --timeout 1200
```

The detailed result is `reports/dungeon-quest-probe.json`; fixture and runner
are under `tests/runtime/dungeon-quests/` and never enter the installation ZIP.
This gate invokes native task/request handlers with real server objects and a
packet sink. It verifies disk reads and resource reload, without claiming a JVM
restart, automatic polling schedule, GUI/network transport or graphical playtest.
The teleport fixture supplies a minimal native room descriptor; it does not
claim a player-built portal, full dungeon generation or dungeon clearance.

## SOQM 0.4.21 — Dimensional Dungeons and DivineRPG elite encounters

The encounter and loot results in this section were recorded before the
12-quest addition, at commit `46b9bdab3a87271360e4588e4f370300510be63e`.
They apply to the unchanged encounter/chest modules and their reviewed
pre-quest payload; the current quest checks are documented separately.

Validation completed on **2026-10-25** against the locked installation payload:
**131** pinned third-party dependencies, **15** authored modules and **1,861**
effective payload files. Dimensional Dungeons 210 uses its original official JAR.
The pinned NeoForge/FML compatibility matrix accepts its Minecraft 1.21 metadata
on 1.21.1 without a dependency override or repacking the upstream file.

- `./scripts/check` passed **189 Python tests** with Java 21 available, both
  JavaScript policy suites, generator freshness, source/binary provenance and
  deterministic ZIP content/CRC/hash checks. All **131** downloaded mod JARs
  separately passed exact size, SHA-256 and archive checks. Campaign render
  freshness also passed.
- Both updated authored modules compiled against the real pinned Java 21 /
  Minecraft 1.21.1 / NeoForge 21.1.262 APIs. The encounter build validates the
  specific upstream Mixin targets and dependencies before compilation.
- The isolated full-pack dedicated-server run passed **335 encounter assertions**
  and **39,619 loot assertions** (**39,954** total). It used the reviewed payload
  without a development override, verified that production files stayed unchanged,
  reloaded **20,062 → 20,062 recipes**, recorded **zero KubeJS startup/server or
  fatal server errors**, saved all dimensions and exited with code **0**.
- Native DD `spawnEnemyHere` tests cover pre-generated rooms before a player
  arrives, basic/advanced scaling order, in-place Divine entity identity, AI,
  equipment, component-bearing keys and original drop chances. The actual Elite
  Mobs damage listener heals a Vampiric mob; native tick dispatch runs and saves
  a Regenerating ability's consumed one-shot state. Two native entity NBT/load
  cycles and an actual data reload preserve attributes, names and ability state.
- Selection uses **12%** for ordinary DivineRPG hostiles and **85%** for native
  DivineRPG bosses. Both policies ran in all **eight DivineRPG dimensions** and
  the Overworld. The pinned native boss tag and class audit cover all **23** boss
  types; actual activation and working ability tests exercise **Ancient Entity**,
  **The Watcher** and aquatic **Kitra**, covering the three distinct hierarchies.
  Kitra retains its native undead immunity and receives compatible Vampiric
  behavior instead of an ineffective regeneration effect.
- Observed boss HP was **800 → 1,000**, **950 → 1,024** and **750 → 937.5**,
  respectively. The configured +25% base-health modifier remains subject to
  Minecraft's native attribute cap; it does not promise an uncapped effective
  increase. Native boss AI, boss bars and loot remain in use. Damage hardcoded
  by an upstream attack is not rewritten by the attack-attribute modifier.
- Player-local activation respects its budget and distance, excludes fake
  players, preserves ordinary despawn rules, handles hidden/visible transitions
  and detaches removed entities. NPCs, pets, neutral whales and already enhanced
  encounters remain excluded; DD's own persistent enemies retain their flag.
- Loot validation used all **12** exact DD routes, **20** real registered protected
  item types and **three** keys activated by DD's own API. It exercised **1,536**
  native final-fill cases and **768** actual loaded-table rolls, including **85**
  with native DD treasures. Full key components and counts survived the policy.

Reproduce the native check after restoring the pinned dependency cache and an
installed NeoForge runtime:

```sh
python3 tests/runtime/encounter-validation/run_probe.py \
  --runtime <installed-neoforge-server> --java <java-21> \
  --accept-eula --timeout 1200
```

The detailed local result is `reports/dungeon-encounter-probe.json`; the source
fixture and runner are under `tests/runtime/encounter-validation/`. The test-only
mod is excluded from the installable pack. This is native server evidence;
there has been no graphical client playtest, full boss fight/playthrough,
long-term multiplayer balance run or whole-server TPS benchmark for this change.
Upstream nonfatal warnings still exist; zero fatal/KubeJS errors does not mean
an entirely warning-free console. See [the gameplay policy](../docs/DIMENSIONAL_DUNGEONS.md).

## SOQM 0.4.17 — Sophisticated Storage focus links in JEI

Correct the compact ingredient index used to select a physical JEI display
slot in the pinned Sophisticated Core adapter. The client-only module uses
JEI's public centring API and preserves the original focus-link conditions,
recipes, costs and storage state transfer. The supplied log identifies eleven
storage display entries matching this defect; three other errors mentioned
in its total have no IDs or traces in the extract.

The real Sponge/MixinExtras transformation changes **one argument** while
retaining both native `List.get` calls. Actual Core and JEI methods pass
**830 cases / 14,544 assertions**: **415 correct source/output links** and
**415 preserved no-link conditions**. Coverage includes all **36 shaped
source positions** across widths/heights 1–3, all **45 shapeless positions**
for 1–9 ingredients, either-dimension normalization, grouped/ungrouped displays,
both focus directions, sparse slots and component-bearing vanilla items.
With the original Core class, the shaped negative control produces **15 exact
exceptions**, **8 silent wrong-source links** and **13 already-correct layouts**;
the reported `US` and `USU` failures are required to reproduce. Applying the
configuration on the server side leaves the target class unchanged.

Two clean native API builds produce the same **3,520-byte** module. The
repository gate passes **170 Python tests without skips**, both Node suites,
generator/localization checks and deterministic package validation. All **12**
coordinate previews remain fresh. All **129 third-party pins** and the earlier
Foundry/recipe-conflict fixes remain unchanged.

This probe starts at native Core display specifications with vanilla item
witnesses and an ItemStack identity helper. It does not register the complete
Sophisticated Storage mod, initialize actual storage components, launch a
graphical client or execute crafting, transfer or multiplayer synchronization.
The full-client playtest remains open.

[Cause, scope and reproduction](../docs/STORAGE_JEI_FIX.md) ·
[Module and exact build pins](../src/compat/storage-jei-compat/README.md) ·
[Installation in Ukrainian](../docs/INSTALL.uk.md#відображення-sophisticated-storage-у-jei).

## SOQM 0.4.16 — distinct gear and furnace-upgrade recipes

Correct three groups of overlapping manual grids across nine recipes, retaining
all gear material costs and both eight-iron-block upgrade routes. Five gear
layouts change; Copper-to-Iron regains its copper-ingot centre. Three Iron
Furnaces tag conditions now use the pinned loader's `neoforge:conditions` key.

The offline regression executes all seven recipe handlers, accounts for the
eleven removed queued definitions, and finds no different-output collisions
among **474 supported authored shaped recipes**. Its negative control restores
the actual old definitions and reproduces **all ten conflicting pairs** in the
three groups. The **149-tag** fixture is bound to dependency pins and authored
tag inputs.

Actual Minecraft codecs, pattern matching and no-hint `RecipeManager` selection
pass **748 witness/mirror cases** in an isolated nine-recipe pool with explicit
vanilla stand-ins for mod items. The old definitions yield 748 ambiguous grids
and 552 selections of another output; the current definitions yield zero of
each. The strengthened full-pack server probe compiles against the pinned APIs
but was not executed in a server.

The repository gate passes **170 Python tests with no skips**, both Node
suites, generator/localization checks and deterministic installation packaging.
All 12 coordinate previews remain fresh. The existing Crafting Station
compatibility JAR and all 129 third-party pins remain unchanged from 0.4.15.
No new graphical client, world or multiplayer run is claimed.

[Fix, reproduction and limits](../docs/RECIPE_CONFLICT_FIX.md) ·
[Installation](../docs/INSTALL.en.md#crafting-fixes-in-0417) ·
[Гравцям українською](../docs/INSTALL.uk.md#виправлення-крафтів-у-0417).

## SOQM 0.4.15 — Crafting Station ingredient consumption

The pinned Modern Foundry 4.1.6 incorrectly writes positioned recipe leftovers
to compact indices. The new Foundry compatibility module corrects all three
write arguments. Actual unmodified method bytecode reproduced the reported
eight-slot sand layout; the real Mixin transformation corrected all three sites.
The isolated behavior probe passed **868 cases / 12,306 assertions**, including
64 sand becoming exactly 16 sandstone in each corner of the station.

The module built byte-identically twice. The pack gate passed **162 Python
tests with no skips**, both Node suites and deterministic packaging. All
**129 third-party artifacts** passed hash, size and archive verification.
The code probe uses external-state fixtures; graphical Ctrl input, world
loading, multiplayer synchronization and all-mod interaction remain untested.

[Cause, evidence and scope](../docs/CRAFTING_STATION_FIX.md) ·
[Reproducible build and probe](../src/compat/foundry-compat/README.md).

## SOQM 0.4.14 — Village Guard

Village Guard 0.4.3 replaces all-mob tick polling with target hooks and checks of
ongoing village-target encounters. Settlement geometry updates after mutations
instead of rebuilding all villages every 200 ticks.

The final locked full-pack server run passed **8,939 assertions** in
**74.89 seconds**, with **20,056 → 20,056 recipes**, no KubeJS/fatal gate errors
and normal shutdown. Tests covered exact rotated geometry, real melee,
Brain/direct combat targets, boundary crossings, explosions, visibility,
listener teardown and both vanilla and native culture reloads. There were
**0 guard checks across 2,048 unrelated native mob/Brain fixture iterations**
and **0 rebuilds across 4,096 unchanged geometry queries**. These are operation
counts, not measured TPS/FPS gains.

The module rebuilt byte-identically twice. The repository gate passed
**162 Python tests without skips**, both Node suites, all **12 coordinate
previews** and deterministic packaging.

[Complete Village Guard verification](verification-village-guard.md) ·
[Module design and build](../src/compat/village-guard/README.md) ·
[Native reproduction](../docs/RUNTIME_VALIDATION.md#focused-village-guard-gate).

## SOQM 0.4.12 — hardcore workshop, MekaSuit and visible rewards

The local candidate adapts **63 recipe variants**, removes **80 old recipe IDs**,
adds **15 quests** and moves **14 existing equipment quests** with saved IDs
intact. The book contains **1,642 quests / 1,927 tasks / 1,643 personal rewards**.
Its 193 cube milestones now award **222 cubes (+15.0259%)**. Quest UI 0.4.5
shows guaranteed stacks and the complete possible-prize catalogue in the book.

The final gate passes **162 Python tests without skips**, both Node suites,
all **12 coordinate previews** and deterministic packaging. The client module
rebuilds byte-identically and passes **18 native Mixin targets**, **394 geometry
assertions**, **1,920 minimap cases**, tooltip layering and shortcut-state gates.

A fresh full-pack server run passes in **119.17 seconds**, with
**20,056 → 20,056 recipes**, zero KubeJS/fatal gate errors and normal shutdown.
It checks all 63 recipe assemblies, all 24 storage carryover variants, true
machine manufacture, personal rewards and nonempty progress across reload.
All **1,628 baseline personal claims**, **57 task values** and **14 moved
completed equipment quests** survive. Graphical client rendering, a full
survival playthrough and long-term economy remain outside these checks.

[Complete 0.4.12 verification](verification-0.4.12.md) ·
[Hardcore workshop](../docs/HARDCORE_WORKSHOP.md) ·
[MekaSuit and plastic quests](../docs/MEKASUIT_PLASTIC_QUESTS.md) ·
[Rewards](../docs/REWARDS.md).

## SOQM 0.4.11 — optional exploration, dungeon loot and equipment

The candidate adds three verified NeoForge structure mods (**127 pinned files**),
preserves all **1,626 quests**, and makes all **67 Overworld visit objectives**
optional rewarded leaves. It covers **397 exact chest tables** with small,
varied loot profiles and fixes tool/slot compatibility and structure data.

The local gate covers **128 Python tests**, both Node suites, generation,
translations and deterministic packaging. All dependency hashes are verified.
The production chest module rebuilt byte-identically and passed **50,000 native
mixed-loot cases** plus 50,000 treasure-retention rolls. Native processor,
chest load/save, placement and loot codecs passed. The equipment probe is
compiled but has not run in a loaded modpack. No fresh full-server, graphical
client or terrain-performance test is claimed for this candidate.

[Complete 0.4.11 verification](verification-0.4.11.md) ·
[Loot policy](../docs/EXPLORATION_LOOT.md) ·
[Structure compatibility](../docs/EXPLORATION_STRUCTURE_COMPAT.md).

## SOQM 0.4.10 — recipe refunds, HUD and Sophisticated Storage

Validated on **2026-10-20** against merged main
`1a7ecd3b5691888bc807667eb90cbd913e191934`. All **1,626 quests, 1,911 tasks and
1,627 personal reward IDs** are retained. **41 expensive crafting quests** gain
guaranteed recipe-ingredient returns alongside their existing pack or cube.
Iron Chests is removed; six existing tasks now teach Sophisticated Storage.
The pack contains **124 pinned third-party mods**, **12 authored modules** and
**1,837 locked game payload files**.

The final full-pack dedicated-server run passed in **110.43 seconds**, with
**20,055 → 20,055 recipes**, normal shutdown, zero KubeJS/fatal gate errors and
an unchanged reviewed payload. It exercised **166 multiblock material checks**,
native inventory debounce, **43 live refund recipe costs**, per-player claim
gates, real machine manufacture and saved-state reload. All old personal claims,
43 nonempty task values including 15 partials, and three completed branch quests
survived. The benchmark loaded no additional chunks; the complete distributions,
including a synthetic-scan timing outlier, are recorded in the linked report.

The HUD module compiled against the pinned native client; its production Mixins
transformed **15 classes**, and production geometry passed **394 assertions**
at three GUI sizes. Both changed modules rebuilt byte-identically. The final
offline gate passed **118 Python tests with no skips**, both Node suites,
generation/localization, all 11 coordinate previews and deterministic archive
checks. All 124 dependency downloads passed size/hash/ZIP verification.

A read-only storage audit also passed on a stopped native test world with
139 files and 1,097 NBT records. Existing worlds still need their Iron Chests
contents moved using the old pack before upgrading; there is no automatic
container conversion. The candidate ZIP includes the audit utility.

**Exact evidence, timing scope, hashes and reproduction:**
[verification-0.4.10.md](verification-0.4.10.md) ·
[storage migration](../docs/STORAGE_MIGRATION.md) ·
[HUD behavior and limitations](../docs/HUD_LAYOUT.md) ·
[refund catalogue](../docs/REWARDS.md).
No graphical-client screenshot QA, complete survival playthrough or long-term
multiplayer economy test is claimed.

## Multiblock tasks, chronology, map lines and rewards — after SOQM 0.4.9

Validated on **2026-10-20** against main
`b77b8586e13902fa7c7b61aa6425bccff13b8ce4`. The book retains **1,626 quests
and 1,627 personal reward IDs**. It now has **1,911 tasks**, including **138
additive material objectives** across 35 assemblies. There are **193 personal
Mayhem Cube milestones** and **1,433 progressive packs** with 3–5 draws.

`./scripts/check` passed **95 Python tests**, both Node suites, generators,
locale freshness, payload pins and deterministic installation packaging.
All **10 coordinate previews** passed freshness checks; generated Main Route,
Mekanism and IE previews were visually inspected. Baseline quest/task/reward
definitions and historical fingerprints remain independently verified through
the exact approved additive migration snapshot.

Pinned template/validator and FTB source inspection supports the construction
counts, curve behavior and existing completed-quest semantics. No new native
server run, graphical client check or survival playthrough was performed.
See the [complete verification record](verification-questbook.md) for the
before/after counts, source comparisons, save behavior and remaining limits.

## Inventory Profiles Next and English landing page — after SOQM 0.4.9

Validated on **2026-10-19** against merged main
`1dc7c21b00f94b4045466376e0b23e70ddaa6f71`. The pack now has **125 pinned
third-party dependencies**, **12 authored modules** and **1,837 locked payload
files**. The previous 122 dependency entries and existing game files are
unchanged; the only new game file is the IPN sorting configuration.

The exact Inventory Profiles Next **2.2.5**, libIPN **6.6.3** and Kotlin for
Forge **5.12.0** JARs were downloaded from the official CurseForge CDN and
checked for byte count, SHA-256, SHA-512 and ZIP integrity. Their embedded
NeoForge metadata supports the pinned Minecraft **1.21.1 / NeoForge 21.1.262**
runtime. Both IPN and libIPN still declare the Kotlin language loader in these
NeoForge builds, so Kotlin for Forge is included. See the exact file links in
[MODLIST.md](../MODLIST.md).

`./scripts/check` passed **72 Python tests**, both Node policy suites,
generator/localization freshness and deterministic installation packaging.
All **10 coordinate previews** also passed their freshness check. The
middle-button sorting override was checked against the exact IPN/libIPN
configuration deserializers; other shortcuts retain upstream defaults.

This verifies metadata, downloaded files and packaging. The graphical client
was not launched, and this addition does not repeat the historical download
audit of the 122 unchanged files or certify in-game sorting behavior.

## Cardboard Box quest and pack logic fixes — after SOQM 0.4.9

Validation on **2026-10-19** passed against merged main
`6dbd50b03285dc844a5a3bc423ecbe17c31d0145`. One optional Cardboard Box quest
is added without replacing any of the existing **1,625 quests, 1,772 tasks
or 1,626 reward objects**. The current totals are **1,626 / 1,773 / 1,627**.
The audit fixes the AE2 CPU capture duplication path, fusion completion under
full energy export, the sawdust/PRC route and missing reactor instructions.

The final payload passed **68 Python tests**, both Node policy suites, all ten
coordinate previews, generator/localization freshness and deterministic
installation packaging. The authored quest-automation 0.4.4 JAR rebuilt
byte-identically twice; the original sources also reproduced the old 0.4.3 JAR.

A fresh full-pack native run passed in **115.60 seconds**. Mekanism's actual
capture refused all eight CPU blocks and preserved upstream exclusions, while
ordinary chest inventory and flawless budding quartz survived capture/unpack.
Native fusion data passed the production detector with positive generation
and an empty buffer, retaining the 200-tick interval and stopped-state guards.
All **1,626 old personal claims**, **43 task values including 15 partials** and
three completed branch quests survived. All **1,612 custom rewards** passed
native serialization and exact mapping checks. ME crafting, manufacture,
personal reward/team migration, disk persistence and reload gates passed.
Recipes remained **20,077 → 20,077**, with zero KubeJS/fatal gate errors and
normal server exit. All 122 third-party pins remain unchanged.

See [the verification record](verification-box-and-logic.md) for fingerprints,
reproduction and exact fixture limits, and [the Ukrainian audit](../docs/PACK_LOGIC_AUDIT.md)
for player impact and source evidence. This is a dedicated-server validation
with synthetic players, not a graphical-client or complete survival run.

## SOQM 0.4.9 — logical production questlines

Validation on **2026-10-19** passed against the merged 0.4.8 baseline
`abd73f1c0736247fc5b13e0e255f8a4ef6d71b34`. This update reorganizes **417 existing
quests into 63 sections across nine chapters**, plus one supporting RFTools
quest: **418 patches, 29 declared routes and 268 bilingual descriptions**.

The **1,625 quest / 1,772 task / 1,626 reward** saved-object contract remains
unchanged. The same **122 third-party pins, 12 authored JARs and 1,835 payload
paths** are retained; only eleven chapter files and two locales change.
Manufacture-gated personal rewards and the earlier runtime optimizations
remain intact.

The restored final payload passed **63 Python tests**, both Node policy suites,
generator/localization freshness, all **10** coordinate-preview checks and
deterministic installation packaging. A new full-pack native run then passed
in **113.26 seconds**: real ME Chest/1k-cell terminal crafting, manufacture
evidence, personal rewards, late joining and team migration, disk persistence,
resource reload and clean shutdown. All **1,626 existing personal claims**,
**43 task values including 15 partial tasks**, and three completed branch
quests survived; no replacement personal entitlements were issued.
Recipes remained **20,077 → 20,077**, with zero KubeJS/fatal gate errors.

See [verification-0.4.9.md](verification-0.4.9.md) for the final executed input
fingerprints, native results, map geometry, reproduction and scope limits.
The PR workflow builds and checks the final installation candidate including
this record. Native validation is a separate local gate, not a CI or graphical
survival-playthrough claim.

## SOQM 0.4.8 — combined runtime optimizations and personal rewards

After integrating main `fc5328d`, validation on **2026-10-19** passed against
**122 pinned third-party dependencies**, **12 authored modules** and **1,835
locked payload files**. All **41 current questbook files** and the progressive
reward policy/source/JAR remain byte-identical to that main revision.

Both fresh full-pack native gates passed: tunnel equivalence/work counts,
guardian lifecycle, quest observation and D&T behavior; then the preserved
0.4.8 manufacture, personal-claim and nonempty progress-migration fixture.
Each server reloaded **20,077 → 20,077 recipes**, logged zero KubeJS/fatal
errors, saved all dimensions and exited with code 0. Both optimization JARs
rebuilt byte-identically.

`./scripts/check` passed **56 Python regressions**, both Node policy suites,
deterministic generators and installation-package checks. Coordinate previews
also passed. The merge's missing module/payload registration and broken native
harness dispatch are repaired with focused regression coverage.

The follow-up native fix also passed real visibility loss/return without a new
join event, canceled fresh/legacy joins and duplicate-UUID insertion. Hidden
entities resume their dungeon behavior when visible; rejected candidates execute
no effects and leave no retained dispatcher entry.

See [the current combined verification](verification-pr5.md) for
exact hashes, native results, limitations and reproduction commands. These
work-count checks do not establish a total chunk-generation, TPS or client-FPS
speedup. The [earlier integration record](verification-performance.md) retains
the native results and hashes before the entity-membership fix.

## Historical SOQM 0.4.8 reward gate before the optimization merge

Validation completed on **2026-10-19** against the exact locked production
payload, with upgrade baseline `72bd75d`. **53 Python tests**, both Node policy
regressions, deterministic generators, all **122 third-party pins**,
**11 authored-module pins**, **1,832 payload hashes** and installation-package
CRC/content/hash/rebuild checks passed.

The final dedicated-server run loaded all **240 catalogue items** and
**226 cited production recipe IDs**, serialized all **1,611 native custom
rewards**, and exercised actual AE2, Mekanism, crafting-grid and Sophisticated
Storage manufacture. It verified independent personal claims, 2/3/4-draw
delivery, expensive-item exclusion before manufacture, late joining, old shared
claim migration, party ownership, disk-backed leave/join and reload persistence.
The nonempty upgrade fixture retained **41 task values**, including **15 partial
tasks**, and rejected **3,250 repeat personal claims** across two migrated
members. Existing personal book claims stayed used.

The server reloaded **20,077 → 20,077 recipes**, saved its dimensions and exited
with code 0, with **zero KubeJS startup/server or fatal-server errors**. The
production payload was unchanged during execution. The new adapter JAR was
rebuilt byte-identically. Graphical rendering, live two-client interaction,
survival pacing and long-term multiplayer economy were not tested.

**Exact scope, migration rules, hashes and reproduction:**
[verification-0.4.8.md](verification-0.4.8.md) ·
[reward catalogue](../docs/REWARDS.md) ·
[native harness guide](../docs/RUNTIME_VALIDATION.md).
Successful CI provides an installation candidate and its checksum; a PR
candidate is distinct from a separately published release. Raw native reports
and test worlds remain local and are not part of the game payload.

## Historical skill optimization checks before the 0.4.8 reward update

The [skill patch notes](../docs/SKILLS_OPTIMIZATION.md) describe the replacement
of nine tick/NBT criteria with native Pufferfish Skills command rewards in
seven existing JSON files. The installed Pufferfish Skills 0.19.2 JAR was
checked by SHA-256 and inspected to verify command parsing, player context,
login/reload reward reconciliation and respec behavior.

Validation completed on **2026-10-19**:

- All 19 Python tests passed, including five skill-configuration regressions
  for the nine mappings, saved criterion names, capstone's any-one-branch
  requirement, the free root, existing tags and six independent point stages.
- A semantic comparison against `e71db83` verified that removing the nine
  appended rewards restores the original skill definitions exactly. The six
  milestone JSONs differ only in their criteria. Seventeen related point,
  layout, retraining and quest files are byte-identical to the base revision.
- The JavaScript equipment/loot policy regression, Ukrainian locale coverage,
  English-guide freshness, all 122 dependency pins, 10 authored module pins
  and 1827 payload hashes passed the repository gate. Only seven payload hashes
  changed; the set of payload files is unchanged.
- The installation ZIP passed exact-content, CRC, hash and deterministic
  rebuild checks: SHA-256
  `feffded32a1eaff50c2f7fcaeacf437b84216619f24e5d59985b87bceabc5295`.
- The exact NeoForge 21.1.262 `PlayerList` patch places global datapack sync
  after advancement-progress reload, so the Pufferfish reconciliation hook
  reads the newly loaded advancement definitions and progress.

These offline checks and source inspections do not execute Minecraft login,
reload or respec with a player. A fresh full-pack world/client test and a
before/after TPS benchmark have not been performed for this configuration patch.

## SOQM 0.4.7 — historical campaign baseline

Validation completed on **2026-10-19**. The exact final game payload passed
**48 Python regressions**, the Node equipment/loot policy regression,
deterministic campaign/English/SVG regeneration, and the native FTB/AE2
interaction gate. All **122** pinned third-party dependencies verified.

The final native run created a fresh localhost world, reloaded
**20,077 → 20,077 recipes**, saved all dimensions and exited normally with
**zero KubeJS startup/server or fatal-server errors**. It also checked:

- **1,611** random rewards through native NBT and network serialization;
- actual one-time inventory delivery from **all 14** thematic pools;
- preservation of **all 1,626 old reward claims** and **42 task values**, including
  **6 partial tasks**, after upgrading the nonempty native save fixture;
- **1,625 team claims** shared with a second UUID and **one personal book** kept
  separately claimable for that player;
- a real AE2 output click that grants the ME advancement and satisfies the
  existing native FTB task, while ordinary crafting, unpowered networks and
  empty ME storage fail;
- independent advancement and nonempty TeamData disk round trips plus reload.

That release's final report matched its payload lock and verified that no
production file changed during the run. The fixture uses synthetic players,
creative power and direct block setup to isolate the tested conditions.
Graphical rendering, survival pacing, long-term multiplayer economy and
measured travel performance remain separate validation work.

**Exact hashes, runtime, scope and reproduction:**
[verification-0.4.7.md](verification-0.4.7.md) ·
[native harness documentation](../docs/RUNTIME_VALIDATION.md).
The successful CI workflow for a commit additionally provides the installation
ZIP and checksum. A candidate archive is distinct from a separately published
release. Generated raw JSON reports and native test worlds remain local.

A standard smoke also passed before the final coordinate/caption adjustments.
That earlier report is not presented as the exact final payload test; the final
interaction run includes its own fresh-world startup, reload and saved shutdown.

## Historical Cannibal 0.4.1 hotfix validation (before 0.4.7)

The Cannibal compatibility module is rebuilt as
`qm-cannibal-compat-0.4.1.jar`, SHA-256
`b9e26d11bcb42a7b99889a4c5bc11c60398e2989d2849def0e88f428099517e8`.
The [module notes](../src/compat/cannibal/README.md) describe the chunk lookup
change, exact supported versions, installation and reproducible build command.

Validation of this hotfix completed on **2026-10-18**:

- All four module classes compiled against the real Minecraft 1.21.1 /
  NeoForge 21.1.262 APIs with Java 21. Two independent executions of the build
  command produced byte-identical JARs with the SHA-256 above.
- A real NeoForge 21.1.262 bootstrap with Cannibal Villages 2.4.9 and Lithium
  0.15.4 loaded all three target classes through the Sponge Mixin transformer.
  Inspection of the exported classes confirmed that the discovery scanner's
  single blocking `getChunk` call was replaced by one redirect to `getChunkNow`.
  Both existing chest-conversion cancellation mixins were also applied.
- The transformed discovery code retains its tick cadence, persistent flag
  write, 80-block distance limit, title and sound calls.
- All 14 Python tests, the JavaScript equipment/loot policy regression,
  Ukrainian locale coverage, English-guide freshness, payload validation and
  deterministic installation-ZIP packaging passed for the updated payload.

The bootstrap used a temporary test-only class-loading probe before the normal
EULA gate; the probe is not part of the shipped JAR. The process exited normally
with `eula=false` and created no world. This verifies actual Mixin application,
not gameplay, discovery notifications with a player, or a measured improvement
in TPS or chunk generation. The full-pack world/reload smoke below belongs to
the unpatched 0.4.6 baseline and was not repeated for this hotfix.

## SOQM 0.4.6 baseline

The current source uses the supplied Questing Mayhem Expert **0.4.6** archive:
`5c17c9030296cbbc4ce8a5c0e075c19f10366524a17f95f7fbcbe12c25604061`.
It has 122 pinned third-party dependencies and 10 authored modules. Expected
campaign preservation is 1625 quests, 37 chapters, 1772 tasks and 1626 reward IDs.

Run `./scripts/check` for local validation and packaging, and
`python3 scripts/soqm.py verify-downloads` for pinned download integrity.
Generated JSON reports under this directory are local output ignored by Git.
A report applies only when its recorded payload/metadata hashes match the files
being assessed. The 0.4.6 source archive's historical audits describe its own
inputs; they do not establish a fresh SOQM run.

Baseline local validation completed on **2026-10-18** for SOQM **0.4.6**,
before the Cannibal 0.4.1 hotfix:

- All 14 Python tests, the JavaScript equipment/loot policy regression,
  complete Ukrainian locale coverage and English-guide freshness passed.
- All 122 pinned third-party files passed SHA-256, size and archive checks.
- The installation ZIP passed CRC, exact-content/hash checks and a
  deterministic rebuild on the same Python/zlib runtime.
- A fresh isolated localhost dedicated server started, reloaded
  **20,077 → 20,077 recipes**, logged zero KubeJS startup/server errors,
  saved all dimensions and exited normally with code 0. Client-only files
  were excluded. The upstream console still includes the known nonfatal
  Relics client-mixin diagnostic; this was not a warning-free console.

The ignored `reports/downloads.json` and `reports/smoke-server.json` contain
current local evidence and exact input hashes. A local MultiMC file migration
establishes installation state; graphical startup and gameplay remain separate.

Still required: graphical launcher/client startup, rendering and keys, actual
play through progression, multiplayer balance, long-term economy and measured
client performance. English is an objective guide rather than an editorial
translation of the full Ukrainian narrative and humor. Independent recompilation
of all ten authored production JARs has not been demonstrated.

## Historical SOQM 0.1.0-alpha.1

The following checks ran on 2026-10-18 for the published alpha at commit
[`6c4c7c4`](https://github.com/lsanarchist/SOQM/commit/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833).
They apply to that earlier 0.4.4-based payload, not to current source revisions.

- **Offline:** 14 Python tests, JavaScript equipment/loot policy regression,
  Ukrainian locale coverage and deterministic English-guide freshness. The
  1625-quest graph had 3905 edges with no cycles, missing dependencies or
  duplicate persistent IDs.
- **Packaging:** payload hashes, authored-JAR allowlist, ZIP CRC/exact contents
  and deterministic rebuilding on the same Python/zlib runtime and changed paths.
- **Downloads:** [historical downloads.json](https://github.com/lsanarchist/SOQM/blob/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833/reports/downloads.json)
  verified all 111 exact third-party files by SHA-256 and size.
- **Native server:** [historical smoke-server.json](https://github.com/lsanarchist/SOQM/blob/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833/reports/smoke-server.json)
  recorded startup, a data reload with **20,074 → 20,074 recipes**, no KubeJS
  startup/server errors, saved shutdown and Java exit 0. The server included
  100 compatible third-party files and seven authored modules; client-only
  files were excluded.

The [historical release record](https://github.com/lsanarchist/SOQM/blob/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833/reports/release.json)
pins that archive and its evidence hashes. Its console also recorded a nonfatal
Relics client-mixin diagnostic and a Bloxy's Structures mob-category warning;
it was not a completely warning-free upstream console.
