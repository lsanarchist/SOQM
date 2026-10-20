# Multiblock tasks, quest chronology, map lines and rewards

**Date:** 2026-10-20. **Baseline:**
[`b77b8586e13902fa7c7b61aa6425bccff13b8ce4`](https://github.com/lsanarchist/SOQM/commit/b77b8586e13902fa7c7b61aa6425bccff13b8ce4).

This update addresses the four reported questbook problems. The figures below
come from the baseline and generated chapter files, not from a simulated
playthrough. The change adds no mods and does not rebuild or replace mod JARs.

## Reviewed result

| Measure | Before | After |
| --- | ---: | ---: |
| Chapters | 37 | 37 |
| Quest IDs | 1,626 | 1,626 |
| Task IDs | 1,773 | 1,911 |
| Outer reward IDs | 1,627 | 1,627 |
| Personal Mayhem Cube milestones | 14 | 193 |
| Personal progressive reward packs | 1,612 | 1,433 |
| Draws at production levels 0–6 | 2 / 2 / 3 / 3 / 3 / 4 / 4 | 3 / 3 / 4 / 4 / 4 / 5 / 5 |
| Idle-visible local connections, entire book | 590 | 297 |
| Idle-visible connections, Mekanism | 108 | 39 |

### Construction tasks

**35 assembly quests** now list a reviewed small construction in Tasks, with
**138 additive item tasks** and **14 corrected quantities**. Every new item
check is required and non-consuming. Existing native formation and custom
operation tasks retain their exact definitions and IDs.

The coverage is 15 Immersive Engineering, three Modern Foundry, three Railcraft,
seven Mekanism/Generators, three Extreme Reactors, three Botania constructions
and the two endpoints of an AE2 Quantum Bridge. Definitions distinguish a kit
checked in the inventory from a native check of a formed or operating machine.

Quantities were checked against pinned JAR hashes, NBT structure templates,
included manuals and validator code. For example, IE Coke Oven/Blast Furnace
use 27 bricks, whereas Railcraft Coke Oven uses 26. Rounded Fusion and SPS
shells are counted without duplicating corners or counting casing ingredients
consumed while crafting ports. The ER turbine uses an explicit compact 3×4×4
layout; it is described as the smallest reviewed separate-layer design rather
than a mathematical minimum over permissive validator edge cases.

Full quantities, source versions, the ER coordinates and detection limits are
in [MULTIBLOCK_MATERIALS.md](../docs/MULTIBLOCK_MATERIALS.md).

### Chronology and explanations

The first Industrial Chassis now follows the machines and materials that
actually supply it: Coke Oven → coke/creosote → Blast Furnace/treated wood →
steel plate, with copper coils and the iron component also taught before the
chassis. Basic IE equipment no longer waits for an unrelated Precision
Mechanism. Actual recipe gates are still applied: MV Capacitor and Item Sorter
continue to require Precision through their modified recipes. Water and wind
remain alternatives.

All **1,626 generic stage/role subtitles** are replaced in Ukrainian and
English by the actual section topic or a device-specific hint. The reported
Rainbow Core tooltip now identifies its Rainbow Furnace purpose, the water
wheel explains its dynamo connection, and the Blast Furnace names its
27-brick construction. Skill bonuses name their real materials; the last one
correctly names antimatter rather than the separate EMC campaign milestone.
Neither locale retains numbered-stage boilerplate in quest descriptions.

Other ordering corrections put salvaging before the first Apotheosis Invader,
the combined exploration goal after its event prerequisites, the Ad Astra fan
with starter components, the oxygen distributor after lunar Desh, rune-based
Botania devices after their runes, and the full diving suit after its materials.

### Map lines

The renderer now selects short local incoming groups after positioning nodes.
Distant or obstructed prerequisites remain hidden until hover. FTB's
`dep_control_pts` routes **174 visible connections** around text using its
native curves. All five links between the six main material milestones remain
visible in a gutter beside the cards.

The drawing policy does not remove dependencies or change unlock semantics.
The separate chronology changes above are authored in the branch/progression
sources. The SVG previews reproduce FTB's background images/text → connection
paths → foreground icons order, so labels cannot conceal a collision.

The [map review](../docs/QUEST_LAYOUT_READABILITY.md) records the comparison
with ATM10's selective visibility and Craftoria's separated Mekanism routes,
plus the exact pinned FTB source supporting hover and curve behavior.

### Reward value

**179 existing reward slots** become one personal Mayhem Cube, bringing the
total to 193. They cover substantial branch completions, assemblies and
adventure goals. Each remaining pack makes 3–5 draws. If eligible
uncommon-or-better prizes exist, its first draw is reserved for those groups;
otherwise it uses the ordinary eligible pool. Duplicate results are combined.

Basic supplies are useful batches: for example six iron ingots, eight copper
ingots, 16 redstone/glass, four basic circuits/alloys and eight cables in their
reviewed entries. Low-value nugget entries are replaced. Rare/epic group
weights rise, but actual item probabilities still depend on which groups and
items the team has unlocked. Expensive items require real manufacture of that
exact item, and reward delivery cannot create production evidence. Plastic,
HDPE and nested cubes remain excluded. See [REWARDS.md](../docs/REWARDS.md) for
the complete catalogue, exact odds and comparisons with ATM10/Craftoria.

## Existing saves and immutable historical checks

Every previous quest/task/reward ID keeps its owner. All 1,627 reward IDs keep
their personal claim scope, manual-claim setting and one-time behavior. An
already claimed pack does not become a new cube claim. Existing unclaimed
rewards use the revised content when claimed.

`metadata/questbook_compatibility.json` records the exact approved task outputs
and 179 reward replacements. Together with the recorded original tasks,
`campaign_compatibility.previous_questbook()` reconstructs all old task/reward
objects exactly. Tests still verify the earlier immutable fingerprints,
including the historical branch-content contract and shared-to-personal claim
migration, rather than replacing those checks with a new hash alone.

| Contract | SHA-256 |
| --- | --- |
| Current persistent identity, including 138 new task IDs | `17b49fe75ee12d853c098b43bf98f080f1393780e818e7854555a63263fd245d` |
| Restored baseline persistent identity | `a5b2531829776eeb7cf1be81bed301d40948b9f800eb2a8ab880874ca3646148` |
| Historical exact task/reward definitions, before the optional Box addition | `cbc3edbaf2ff9373a3ca8fb2e33488bc589e80e90859ee4e1cbda90934b87be9` |

The pinned FTB `TeamData` keeps completed timestamps independently of task
definitions. Adding the item objectives therefore does not itself clear an
already completed quest. This behavior was checked in source, not by a new
native save-upgrade run. New tasks are not automatically completed. For an
unfinished or manually reset assembly quest, already placed blocks may need
to be returned temporarily to the inventory for detection.

## Verification performed

`./scripts/check` passed **95 Python tests**, both Node policy suites,
generator/localization freshness, payload pins and installation packaging.
The package check verifies contents, CRC, hashes and a byte-identical rebuild
on the same runtime. The pack retains **125 pinned third-party mods, 12
authored modules and 1,837 locked payload files**.

The new regression coverage checks original objective preservation, minimum
geometry and quantities, prerequisite chronology, the real skill material,
all caption/heading/icon obstacles, shared-endpoint crossings, native control
types and transforms, reward eligibility, the reserved quality draw and exact
historical claim/object ownership. Both 20-segment and finer 80-segment curve
samples are checked. The Node suite exercises the actual generated 14-pool /
1,433-pack policy, including deterministic low RNG, locked prizes, fallbacks,
partial delivery, duplicate combination and read-only commands.

All ten committed coordinate previews passed
`python3 scripts/render_campaign.py --check`. Main Route, the reported
Mekanism sections and the IE bootstrap were rasterized from the generated
files and visually inspected. These previews retain coordinates, captions
and curves but use numbered markers in place of item icons.

**Not performed:** a fresh native server run, graphical Minecraft client,
formation of these constructions in a world, or a survival balance playthrough.
The updated Java runtime reward probe is a fixture change, not evidence of a
new successful native run. Font rendering, hover interaction and reward feel
at the player's actual UI scale remain in-game validation items. The successful
historical runtime reports below do not certify this payload.

The PR workflow builds the installation ZIP and `dist/SHA256SUMS`. This report
is included in the archive; its archive hash is deliberately not embedded here.
