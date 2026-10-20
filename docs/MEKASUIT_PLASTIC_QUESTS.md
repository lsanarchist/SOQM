# MekaSuit, plastic and the equipment workshop

The 0.4.12 campaign exposes MekaSuit in chapter **07 · MekaSuit, Modules and Equipment** (Ukrainian: **07 · MekaSuit, модулі та оснащення**) with the bodyarmor icon. Chapter 08 keeps nuclear production; its polonium lesson points to the equipment chapter.

## What the player follows

Chapter 07 now has 36 quests in seven sections: tools, HDPE production, the four armor pieces and Meka-Tool, the modification workshop, helmet modules, movement, and Meka-Tool upgrades. The four armor objectives are parallel. The player can choose a useful piece and its modules before collecting the complete set.

The two new HDPE objectives explain Substrate and HDPE Pellet before the existing eight-sheet objective. In pinned Mekanism 10.7.19.85, the initial reaction consumes 2 Bio Fuel, 10 mB water and 100 mB hydrogen, producing 1 Substrate and 100 mB ethene. The pellet reaction consumes 1 Substrate, 50 mB liquid ethene and 10 mB oxygen for 1 pellet. Enrichment consumes 3 pellets for 1 sheet, so the existing eight-sheet task represents 24 pellets. This branch remains at stage 2, before polonium.

Module Base follows the Elite Control Circuit at stage 3. The harder workshop recipe produces one base from 2 HDPE Sheets, 2 tin blocks, 2 bronze ingots, 2 Immersive Engineering steel plates and 1 Elite Control Circuit. The suit and Modification Station follow polonium at stage 5; gravitational flight and the optional maximum loadout follow antimatter at stage 6. Equipment completion does not become a prerequisite for nuclear construction or Main Route.

Module lessons identify compatible equipment and separate crafting from installation. Dosimeter, Geiger and Charge Distribution use bodyarmor; Magnetic Attraction uses boots. The native slot registrations were inspected in `mekanism.common.Mekanism.imcQueue`. Item tasks detect a finished Unit without consuming it. Their text tells the player to install it with the Modification Station and configure it with Module Tweaker.

## Actual upgrade detection

Two new advancement tasks use the pinned mod’s own criteria:

| Quest | Native advancement | What it verifies |
| --- | --- | --- |
| Complete equipment inventory | `mekanism:mekasuit` | All four armor pieces and the Meka-Tool are in inventory. It does not require the armor to be worn. |
| Maximum MekaSuit and Meka-Tool upgrades | `mekanism:upgraded_mekasuit` | Every supported module is installed at its maximum copy count in each of those five items. |

The second advancement uses `mekanism:maxed_module_container` for every item. Inspection of `MaxedModuleContainerItemPredicate.matches` confirms that it verifies the equipment type, a real module container, all supported module types, and each module’s installed count against its maximum stack size. Loose modules cannot satisfy it. The teaching branch covers commonly useful modules; its finale explicitly directs players to the complete compatibility list, including any supported modules added by other installed mods.

## Plastic and Cardboard Box

Industrial Foregoing’s named plastic chain stays in its own industrial chapter. With the pinned 3.6.39 archive, Latex Processing Unit consumes 750 mB latex and 500 mB water and produces **Dry Rubber directly**. Its `LatexProcessingUnitTile.onFinish` implementation selects `ModuleCore.DRY_RUBBER`. Tiny Dry Rubber is deprecated and is not inserted into the quest path. The native `data/industrialforegoing/recipe/plastic.json` smelts one Dry Rubber into one Plastic.

The Plastic objective explains both valid routes: latex → Dry Rubber → smelting, or chapter 07 HDPE Sheet → the SOQM recipe yielding 3 Plastic. Its prerequisites do not force both production methods.

The existing Cardboard Box quest stays in chapter 06 with the title **Mekanism: Cardboard Box** / **Mekanism: Картонна коробка**. Precision Sawmill and the box are moved to the early factory section. The original four-sawdust teaching, packing instructions and quest identity remain intact.

## Save compatibility and authoring

`metadata/equipment_workshop_migration.json` enumerates all moves and additions against commit `95421eeda09f2e3447e6e9d3189a1e43d9c41381`: 14 existing quests and 17 images move from chapter 08 to 07, and 15 quests with their tasks/rewards plus 24 images are added. Existing quest, task, reward and caption IDs remain unchanged, as does reward claim scope. The global compatibility snapshot separately reviews reward pool and quantity updates.

Objectives reside in chapter sources; `metadata/quest_branches/equipment.json` and `industry.json` own layout assignments, dependencies and bilingual equipment/plastic teaching. Workshop costs are explained in the existing tutorial source files and `metadata/tutorial_expansions/hardcore_workshop.json`. Generated locale shards and runtime files must be refreshed through the standard campaign pipeline.

The focused regression suite checks optional progression, true advancement detection, module slot prerequisites, early HDPE, alternative plastic routes and the box’s stable identity. When the pinned Mekanism archive is cached, it also reads native advancement and recipe JSON:

```sh
python3 -m unittest discover -s tests -p test_equipment_workshop.py -v
```

Source evidence: pinned `Mekanism-1.21.1-10.7.19.85.jar`, `industrialforegoing-1.21-3.6.39.jar`, their hashes in `metadata/mods.lock.json`, `qm_expert_progression.js`, `qm_expert_v042.js`, and the generated `qm_expert_hardcore_workshop.js`. No custom NBT format is invented for installation detection.
