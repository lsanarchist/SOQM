# Multiblock construction objectives

The campaign now lists construction blocks and quantities in **Tasks** for 35 existing assembly quests. It adds 138 mandatory, non-consuming item checks, corrects 14 existing quantities, and keeps every previous task ID and native formation/operation check. Item-only kit quests explicitly describe material preparation; they do not claim that possession proves a working machine.

## Data and generation

`metadata/multiblock_materials.json` is the reviewed source of truth. Each entry contains its quest ID, placed component counts, translated construction instructions, archive hash/source entries and the reasoning behind variable-size layouts. `migration.original_tasks` records the pre-change objectives; `migration.changed_counts` lists the reviewed count changes and `migration.added_task_ids` enumerates additive IDs. Quantities count finished components placed or used during construction, not ingredients consumed while crafting those components.

`scripts/campaign_multiblocks.py` has two integration points:

1. Call `apply_structure(chapters)` **before** `campaign_progression.apply`. Material gates must see the new item checks on the first refresh.
2. Call `apply_locale(chapters, locale, language)` after general lesson/localization text and before final layout/guidance. Both `uk_ua` and `en_us` are authored explicitly, including titles. The locale pass does not mutate structure.

IDs for new checks derive from quest ID plus item registry ID. Repeated refreshes reuse those IDs, add no extra tasks, and make no additional quantity changes. Construction tasks appear before the retained native check. Engineer's Hammer is included as a reusable, non-consumed formation tool for Immersive Engineering.

## Reviewed construction kits

Dimensions are the exterior bounding box; directions are stated explicitly for asymmetric layouts. Glass, ports and controllers occupy shell positions rather than adding a second shell. Modern Foundry casting attachments and external power/fuel connections are described separately. These are small starter builds, not recommended production capacities.

| Assembly | Exterior size | Required components |
|---|---|---|
| Coke Oven | 3×3×3 | 27 × Coke Brick; 1 × Engineer's Hammer |
| Blast Furnace | 3×3×3 | 27 × Blast Brick; 1 × Engineer's Hammer |
| Alloy Kiln | 2×2×2 | 8 × Kiln Brick; 1 × Engineer's Hammer |
| Metal Press | 3×3×1 | 1 × Redstone Engineering Block; 1 × Piston; 1 × Heavy Engineering Block; 2 × Steel Scaffolding; 2 × Conveyor Belt; 1 × Engineer's Hammer |
| Crusher | 5×3×3 | 10 × Light Engineering Block; 1 × Redstone Engineering Block; 10 × Steel Scaffolding; 8 × Steel Fence; 9 × Hopper; 1 × Engineer's Hammer |
| Improved Blast Furnace | 3×4×3 | 27 × Reinforced Blast Brick; 1 × Hopper; 1 × Engineer's Hammer |
| Mixer | 3×3×3 | 4 × Light Engineering Block; 4 × Iron Sheetmetal; 1 × Redstone Engineering Block; 5 × Steel Scaffolding; 1 × Steel Fence; 3 × Fluid Pipe; 1 × Engineer's Hammer |
| Silo | 3×7×3 | 50 × Iron Sheetmetal; 4 × Treated Wood Fence; 1 × Engineer's Hammer |
| Sheetmetal Tank | 3×5×3 | 34 × Iron Sheetmetal; 4 × Treated Wood Fence; 1 × Engineer's Hammer |
| Assembler | 3×3×3 | 2 × Redstone Engineering Block; 2 × Light Engineering Block; 9 × Iron Sheetmetal; 6 × Steel Scaffolding; 6 × Iron Sheetmetal Slab; 2 × Conveyor Belt; 1 × Engineer's Hammer |
| Bottling Machine | 3×3×2 | 2 × Iron Sheetmetal; 1 × Redstone Engineering Block; 2 × Light Engineering Block; 3 × Steel Scaffolding; 3 × Conveyor Belt; 2 × Fluid Pump; 1 × Engineer's Hammer |
| Sawmill | 5×3×3 | 6 × Light Engineering Block; 4 × Iron Sheetmetal; 2 × Heavy Engineering Block; 1 × Redstone Engineering Block; 8 × Steel Scaffolding; 4 × Conveyor Belt; 1 × Engineer's Hammer |
| Automated Workbench | 3×2×3 | 4 × Light Engineering Block; 1 × Redstone Engineering Block; 2 × Heavy Engineering Block; 5 × Steel Scaffolding; 2 × Treated Wood Slab; 4 × Conveyor Belt; 1 × Engineer's Hammer |
| Arc Furnace | 5×5×5 | 8 × Steel Sheetmetal; 6 × Block of Steel; 5 × Heavy Engineering Block; 1 × Redstone Engineering Block; 10 × Light Engineering Block; 27 × Reinforced Blast Brick; 14 × Steel Sheetmetal Slab; 5 × Steel Scaffolding; 1 × Cauldron; 1 × Engineer's Hammer |
| Excavator and Bucket Wheel | 3×7×8 | 9 × Block of Steel; 16 × Steel Sheetmetal; 3 × Radiator Block; 9 × Light Engineering Block; 1 × Redstone Engineering Block; 4 × Heavy Engineering Block; 26 × Steel Scaffolding; 1 × Engineer's Hammer |
| Seared Melter station | 2×2×1 | 1 × Seared Melter; 1 × Seared Heater; 1 × Seared Faucet; 1 × Seared Casting Table |
| Smeltery | 3×2×3 | 1 × Smeltery Controller; 2 × Seared Bricks; 1 × Seared Fuel Tank; 1 × Seared Drain; 1 × Seared Faucet; 1 × Seared Casting Basin |
| Foundry | 3×2×3 | 1 × Foundry Controller; 14 × Scorched Bricks; 1 × Scorched Fuel Tank; 1 × Scorched Drain; 1 × Scorched Faucet; 1 × Scorched Casting Basin |
| Railcraft Coke Oven | 3×3×3 | 26 × Coke Oven Bricks |
| Railcraft Blast Furnace | 3×4×3 | 34 × Blast Furnace Bricks |
| Low-pressure Steam Boiler | 1×2×1 | 1 × Low Pressure Steam Boiler Tank; 1 × Solid Fueled Firebox |
| Thermal Evaporation Plant | 4×3×4 | 1 × Thermal Evaporation Controller; 2 × Thermal Evaporation Valve; 33 × Thermal Evaporation Block |
| Fission Reactor | 3×4×3 | 30 × Fission Reactor Casing; 4 × Fission Reactor Port; 1 × Fission Fuel Assembly; 1 × Control Rod Assembly |
| Industrial Turbine | 5×5×5 | 44 × Turbine Casing; 51 × Structural Glass; 2 × Turbine Valve; 1 × Turbine Vent; 1 × Turbine Rotor; 1 × Turbine Blade; 1 × Rotational Complex; 8 × Pressure Disperser; 1 × Electromagnetic Coil |
| Fusion Reactor | 5×5×5 | 1 × Fusion Reactor Controller; 36 × Fusion Reactor Frame; 3 × Fusion Reactor Port; 1 × Laser Focus Matrix; 25 × Reactor Glass |
| Supercritical Phase Shifter | 7×7×7 | 60 × SPS Casing; 3 × SPS Port; 1 × Supercharged Coil; 123 × Structural Glass |
| Induction Matrix | 3×4×3 | 1 × Basic Induction Provider; 1 × Basic Induction Cell; 24 × Induction Casing; 2 × Induction Port; 8 × Structural Glass |
| Teleporter Portal | 3×4×1 | 1 × Teleporter; 9 × Teleporter Frame |
| Basic Reactor | 3×3×3 | 1 × Reactor Controller (Basic); 22 × Reactor Casing (Basic); 1 × Reactor Fuel Rod (Basic); 1 × Reactor Control Rod (Basic); 1 × Reactor Solid Access Port (Basic); 1 × Reactor Active Forge Energy Power Tap (Basic) |
| Reinforced Reactor | 3×3×3 | 1 × Reactor Controller (Reinforced); 22 × Reactor Casing (Reinforced); 1 × Reactor Fuel Rod (Reinforced); 1 × Reactor Control Rod (Reinforced); 1 × Reactor Solid Access Port (Reinforced); 1 × Reactor Active Forge Energy Power Tap (Reinforced) |
| Reinforced Turbine | 3×4×4 | 1 × Turbine Controller (Reinforced); 39 × Turbine Housing (Reinforced); 1 × Turbine Rotor Bearing (Reinforced); 2 × Turbine Active Forge Fluid Port (Reinforced); 1 × Turbine Active Forge Energy Power Tap (Reinforced); 2 × Turbine Rotor Shaft (Reinforced); 1 × Turbine Rotor Blade (Reinforced); 1 × Block of Iron |
| Terra Plate | 3×2×3 | 5 × Livingrock; 4 × Block of Lapis Lazuli; 1 × Terrestrial Agglomeration Plate |
| Alfheim Portal | 5×5×3 | 8 × Livingwood; 3 × Glimmering Livingwood; 1 × Elven Gateway Core; 2 × Natura Pylon; 2 × Mana Pool |
| Gaia Ritual | 9×3×9 | 9 × Block of Iron; 1 × Beacon; 4 × Gaia Pylon |
| Quantum Network Bridge pair | 3×3×1 | 2 × Quantum Entangled Singularity; 16 × Quantum Ring; 2 × Quantum Link Chamber |

### Geometry details that prevent common counting errors

- Immersive Engineering Coke Oven and Blast Furnace each use **27** bricks, with no hollow middle. Railcraft Coke Oven uses **26**, while its taller Blast Furnace uses **34**. Their native structure templates differ.
- The minimum Modern Foundry Smeltery has one floor block and four side positions. Controller, tank and drain replace three side blocks, leaving **two** Seared Bricks. The Foundry requires its frame and corners: nine floor positions plus eight wall positions minus those three devices leave **14** Scorched Bricks. Each kit also includes a faucet and casting basin for a usable casting output.
- Mekanism evaporation uses the minimum 4×3×4 layout. Its bottom has 16 blocks, middle ring 12, and top ring 8 because the top corners are omitted. Controller and two valves occupy three of those 36 positions, leaving **33** evaporation blocks.
- A minimum Fission Reactor has an outer 3×4×3 shell: 36 total positions minus two inner positions gives 34 shell blocks. Four ports leave **30** casings. The inside contains one fuel assembly and its control rod. The separate safety quest still covers the logic adapter and operating safeguards.
- Induction Matrix needs two interior positions for a cell and provider, so its kit uses 3×4×3: **24** edge casings, **two** ports and **eight** glass panels. It includes both internal components.
- The 5×5×5 Mekanism turbine has 98 shell positions: **44** frame casings, **51** glass panels, **two** valves and **one** vent. Its three interior heights hold the rotor, complex/disperser layer, and coil. One blade meets the validator's positive-blade-count check. A condenser is an optional water-recovery upgrade.
- The rounded fusion reactor is not an ordinary cubic shell. Enumerating the six validator faces and deduplicating shared edges gives **36** compulsory frame positions plus **30** replaceable positions. Controller, laser matrix and three ports leave **25** reactor glass panels.
- SPS similarly has **60** placed casing blocks and **126** replaceable shell positions. Three ports leave **123** glass panels; one supercharged coil goes inside. A commonly quoted crafting shopping list includes additional casings consumed while making ports; these must not be demanded again as placed blocks.
- A Teleporter uses four frame pairs plus its top cap: **nine** frame blocks and one teleporter machine. The kit covers one endpoint. The description explains using another endpoint or a portable teleporter to travel.
- A Quantum Bridge link needs two endpoints: **16** rings, **two** link chambers and the **two matching** entangled singularities. Inventory checks verify registry type/count; they cannot prove that the two singularities have matching frequency data. The instructions require using the same produced pair.

### Extreme Reactors turbine: explicit reviewed layout

The reinforced turbine kit uses the smallest reviewed layout with separate blade and coil layers: **3×4×4 (X×Y×Z)**. Its shell contains 44 blocks: 39 casings plus a bearing, controller, two fluid ports and an FE tap. Relative to the lower corner `(0,0,0)`, use the following positions:

| Component | Position | Orientation/purpose |
|---|---|---|
| Bearing | `(1,3,1)` | Faces down |
| First shaft | `(1,2,1)` | Continuous rotor axis |
| Second shaft | `(1,1,1)` | Continuous rotor axis |
| Blade | `(1,2,2)` | Next to the first shaft |
| Iron-block coil | `(1,1,2)` | Next to the second shaft |
| End casing | `(1,0,1)` | Required casing at the rotor's far end |
| Controller | `(0,1,1)` | Non-edge shell position |
| Steam input | `(0,2,1)` | Non-edge shell position |
| Water output | `(2,1,1)` | Non-edge shell position |
| FE tap | `(2,2,1)` | Non-edge shell position |

All other shell positions are casings. The pinned turbine validator allows an off-centre bearing and a rectangular cross section. Iron is registered as a coil material; `TurbineData.update` includes the single coil block and `TurbineLogic` can generate from that positive coil size. The exact 2.4.28 validator clears its collected coil set before rotor validation, so this review does **not** assert a mathematical minimum over layouts accepted through that permissive edge case. The quest explains the compact, separate-layer design instead of claiming an untested global minimum. The coordinate fixture is checked offline; an in-game formation/FE smoke test remains a release validation step.

## Evidence and version pinning

Immersive Engineering, Modern Foundry, Railcraft, Mekanism, Extreme Reactors, ZeroCore and Botania archives were downloaded from their public distribution and checked against SHA-256 values in `metadata/mods.lock.json`. Templates/book data were read directly. Where counts depend on validators, relevant classes in the exact archive were inspected along with upstream sources. No mod binary was modified or executed by this review. Every material registry ID outside vanilla/AE2 was also found in the pinned archive's item/block model or blockstate assets.

| Mod | Pinned version | Primary evidence |
|---|---|---|
| Immersive Engineering | 12.4.2-194 | [Pinned archive](https://www.curseforge.com/minecraft/mc-mods/immersive-engineering/files/6733669), `data/immersiveengineering/structure/multiblocks/*.nbt`; each non-air palette entry counted, including the combined excavator/wheel structure |
| Modern Foundry | 4.1.6 | [Pinned archive](https://www.curseforge.com/minecraft/mc-mods/modern-foundry/files/8675380), `SmelteryMultiblock`, `FoundryMultiblock`, `MultiblockCuboid` and the included Foundry structure requirements book |
| Railcraft Reborn | 1.2.12 | Pinned archive's `assets/railcraft/patchouli_books/guide_book/en_us/entries/multiblock/` entries for coke oven, blast furnace and steam boiler; exact archive URL/hash recorded per kit |
| Mekanism / Generators | 10.7.19.85 | Exact pinned validator classes and [upstream source snapshot](https://github.com/mekanism/Mekanism/tree/bcd7a8bf594cff9614eb12238fe3776f19da24d9); [official turbine guide](https://wiki.aidancbrady.com/wiki/Industrial_Turbine), [fission guide](https://wiki.aidancbrady.com/wiki/Fission_Reactor) |
| Extreme Reactors | 2.4.28 | [Pinned archive](https://www.curseforge.com/minecraft/mc-mods/extreme-reactors/files/7344744), reactor introduction book and turbine validator, coil registry and variant classes; [upstream turbine source](https://github.com/ZeroNoRyouki/ExtremeReactors2/blob/1.21/src/main/java/it/zerono/mods/extremereactors/gamecontent/multiblock/turbine/MultiblockTurbine.java) |
| ZeroCore 2 | 2.4.21 | [Pinned archive](https://www.curseforge.com/minecraft/mc-mods/zerocore/files/7344742), multiblock base and dimension interfaces used by Extreme Reactors |
| Botania Revived | 1.0.2 | Pinned archive's `data/botania/structure/{terra_plate,alfheim_portal,gaia_ritual}.nbt`; creative pools in the portal's example structure replaced by ordinary survival mana pools |
| Applied Energistics 2 | 19.2.18 | [Official Minecraft 1.21.1 Quantum Bridge guide](https://guide.appliedenergistics.org/1.21.1/items-blocks-machines/quantum_bridge/) and [singularity guide](https://guide.appliedenergistics.org/1.21.1/items-blocks-machines/singularities/) |

Exact paths, archive hashes, source URLs and derivation notes are in the JSON. This document's upstream links are explanatory; archive hashes identify what was actually reviewed.

## Scope and intentionally separate quests

The 35 kits cover all assembly targets represented in the current book: 15 Immersive Engineering structures, three Modern Foundry structures/stations, three Railcraft structures, seven Mekanism structures, three Extreme Reactors structures, three Botania structures/ritual platforms and the two-endpoint AE2 bridge.

Component acquisition quests remain separate: individual engineering blocks, reactor rods/ports, induction cells, ME CPU parts and similar components do not each demand an entire second machine. The ME workshop already teaches a 1k crafting-storage block and the workshop's surrounding network components; a single storage block is a valid minimum CPU. Standalone machines, loosely connected machine networks and a rocket's ordinary launchpad are outside this assembly-template inventory. Fission's separate operating-cycle check does not duplicate the shell kit; the kit is its construction predecessor. Mekanism Dynamic Tank and Thermoelectric Boiler have no assembly quest in this book, so this change does not invent new quests or persistent quest IDs for them. Naturally occurring ritual ground, external power generation, fuel production and consumable process inputs remain in the relevant lessons/recipes rather than being counted as shell blocks.

## Existing-world behavior

FTB Quests **2101.1.36** stores completed quest timestamps separately from individual task progress. [`TeamData.isCompleted`](https://github.com/FTBTeam/FTB-Quests/blob/v2101.1.36/common/src/main/java/dev/ftb/mods/ftbquests/quest/TeamData.java) checks the persisted completed-ID map; adding tasks does not clear that map. Its serialization writes and restores the completed map. Progress-cache clearing does not remove those completion timestamps.

Therefore already completed quests stay completed unless explicitly reset. Existing reward claims and native task IDs remain intact. This does not retroactively satisfy new item tasks. An unfinished or manually reset quest must meet the new required checks; if its blocks are already placed, they may need to be recovered temporarily and held in inventory. A native formed/operation check by itself does not auto-complete the added materials. The description tells players to hold each material quantity before placing it, and none of these checks consumes the kit.

## Reviewed corrections to old quantities

These are the only quantity changes to pre-existing task IDs; all other material checks are additive.

| Quest | Item | Before | After |
|---|---|---:|---:|
| Thermal Evaporation Plant | `mekanism:thermal_evaporation_valve` | 4 | 2 |
| Thermal Evaporation Plant | `mekanism:thermal_evaporation_block` | 32 | 33 |
| Fission Reactor | `mekanismgenerators:fission_reactor_casing` | 32 | 30 |
| Fission Reactor | `mekanismgenerators:fission_fuel_assembly` | 4 | 1 |
| Industrial Turbine | `mekanismgenerators:turbine_casing` | 32 | 44 |
| Industrial Turbine | `mekanismgenerators:turbine_valve` | 4 | 2 |
| Industrial Turbine | `mekanismgenerators:turbine_rotor` | 4 | 1 |
| Industrial Turbine | `mekanismgenerators:electromagnetic_coil` | 4 | 1 |
| Fusion Reactor | `mekanismgenerators:fusion_reactor_frame` | 16 | 36 |
| Fusion Reactor | `mekanismgenerators:fusion_reactor_port` | 4 | 3 |
| Supercritical Phase Shifter | `mekanism:sps_casing` | 4 | 60 |
| Supercritical Phase Shifter | `mekanism:sps_port` | 2 | 3 |
| Gaia Ritual | `botania:gaia_pylon` | 1 | 4 |
| Quantum Network Bridge pair | `ae2:quantum_entangled_singularity` | 1 | 2 |

## Validation

Run `python -m unittest discover -s tests -p test_multiblock_materials.py -v`.

### Native detection and cost of the added tasks

The new material conditions use FTB Quests **2101.1.36**'s ordinary `ItemTask`
with `consume_items: false`, `only_from_crafting: false` and
`match_components: "none"`. They do not poll block entities, search a radius or
load chunks. A completed material condition is a recorded inventory quantity;
it does not become a requirement to keep those blocks in the inventory forever.

The pinned native inventory listener coalesces relevant inventory slot changes
using the pack's existing **20-tick** detection delay. Repeated changes keep one
pending check for that player without pushing its deadline back. On detection,
FTB builds a summary of the main inventory once and exact-item tasks look up
their relevant stacks in that summary. Completed tasks return immediately.
The pack's flexible progression can record items before prerequisites are
finished; this does not turn the material checks into formation evidence.

`tests/runtime/MultiblockMaterialsProbe.java` exercises all **166 live item
conditions** in the 35 kits against the pinned Java implementation. It checks
one-short quantities, repeated detection, legal multiple stacks, changed item
names, non-consumption, persistence, retained completion after placing blocks,
separate formation/operation conditions and old quest completion timestamps.
The same fixture calls the actual inventory listener and checks that 101 slot
changes share one pending detection. It records additional loaded chunks.

The timing fixture compares **1,469** existing item conditions with the current
**1,607**, including the 138 added conditions. Both variants run on the server
thread, with one inventory summary per scan, warm-up and alternating paired
samples. Empty inventory isolates task dispatch and item lookup; the complete
native listener and already completed tasks are measured separately. These
numbers characterize the checks, not a graphical client's FPS, whole-server
TPS or the running machines' own simulation cost. Executed measurements and
the exact tested payload are recorded in
[the 0.4.10 verification report](../reports/verification-0.4.10.md).

### Construction geometry coverage

The ten focused checks cover additive IDs, native-task preservation, the complete reviewed count-change inventory, deterministic repeated passes, both authored languages, pinned source hashes, different IE/Railcraft brick templates, deduplicated fusion/SPS geometry, complete Mekanism shells, the explicit ER turbine axis and shell positions, and the paired AE2 bridge. They do not replace running Minecraft with the pinned mod set. Before release, smoke-test one native formed quest, the Modern Foundry minimum layouts, the two turbine layouts and completion behavior in an existing save.
