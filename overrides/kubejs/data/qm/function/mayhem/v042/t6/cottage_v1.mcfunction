# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Іпотека за один клік", "color": "gold"}
tellraw @s {"text": "Будинок твій. Землю під ним ніхто не питав.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~5 ~ ~-5 ~-5 ~8 ~5 minecraft:air replace
fill ~3 ~-1 ~-4 ~-3 ~-1 ~4 minecraft:deepslate_bricks replace
fill ~3 ~ ~-4 ~-3 ~3 ~4 minecraft:oak_planks hollow
fill ~3 ~ ~-4 ~3 ~3 ~-4 minecraft:oak_log replace
fill ~-3 ~ ~-4 ~-3 ~3 ~-4 minecraft:oak_log replace
fill ~3 ~ ~4 ~3 ~3 ~4 minecraft:oak_log replace
fill ~-3 ~ ~4 ~-3 ~3 ~4 minecraft:oak_log replace
fill ~4 ~4 ~-5 ~-4 ~4 ~-5 minecraft:spruce_stairs[facing=south] replace
fill ~4 ~4 ~5 ~-4 ~4 ~5 minecraft:spruce_stairs[facing=north] replace
fill ~4 ~5 ~-4 ~-4 ~5 ~-4 minecraft:spruce_stairs[facing=south] replace
fill ~4 ~5 ~4 ~-4 ~5 ~4 minecraft:spruce_stairs[facing=north] replace
fill ~4 ~6 ~-3 ~-4 ~6 ~-3 minecraft:spruce_stairs[facing=south] replace
fill ~4 ~6 ~3 ~-4 ~6 ~3 minecraft:spruce_stairs[facing=north] replace
fill ~4 ~7 ~-2 ~-4 ~7 ~-2 minecraft:spruce_stairs[facing=south] replace
fill ~4 ~7 ~2 ~-4 ~7 ~2 minecraft:spruce_stairs[facing=north] replace
fill ~4 ~7 ~-1 ~-4 ~7 ~1 minecraft:spruce_slab replace
fill ~1 ~1 ~-4 ~-1 ~2 ~-4 minecraft:glass_pane replace
fill ~1 ~1 ~4 ~-1 ~2 ~4 minecraft:glass_pane replace
setblock ~3 ~ ~ minecraft:oak_door[half=lower,facing=east] replace
setblock ~3 ~1 ~ minecraft:oak_door[half=upper,facing=east] replace
setblock ~-1 ~ ~-2 minecraft:red_bed[part=foot,facing=west] replace
setblock ~-2 ~ ~-2 minecraft:red_bed[part=head,facing=west] replace
setblock ~-2 ~ ~3 minecraft:crafting_table replace
setblock ~-2 ~ ~2 minecraft:furnace replace
setblock ~ ~2 ~ minecraft:lantern[hanging=true] replace
setblock ~1 ~ ~3 minecraft:chest[facing=east]{"LootTable":"qm:mayhem/t6/explorer_cache"} replace
setblock ~5 ~-1 ~ minecraft:cobblestone replace
fill ~5 ~ ~ ~5 ~2 ~ minecraft:air replace
tp @s ~5.5 ~0.1 ~0.5
