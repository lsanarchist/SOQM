# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Іпотека за один клік", "color": "gold"}
tellraw @s {"text": "Будинок твій. Землю під ним ніхто не питав.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-5 ~ ~-5 ~5 ~8 ~5 minecraft:air replace
fill ~-4 ~-1 ~-3 ~4 ~-1 ~3 minecraft:stone_bricks replace
fill ~-4 ~ ~-3 ~4 ~3 ~3 minecraft:spruce_planks hollow
fill ~-4 ~ ~-3 ~-4 ~3 ~-3 minecraft:spruce_log replace
fill ~-4 ~ ~3 ~-4 ~3 ~3 minecraft:spruce_log replace
fill ~4 ~ ~-3 ~4 ~3 ~-3 minecraft:spruce_log replace
fill ~4 ~ ~3 ~4 ~3 ~3 minecraft:spruce_log replace
fill ~-5 ~4 ~-4 ~-5 ~4 ~4 minecraft:spruce_stairs[facing=east] replace
fill ~5 ~4 ~-4 ~5 ~4 ~4 minecraft:spruce_stairs[facing=west] replace
fill ~-4 ~5 ~-4 ~-4 ~5 ~4 minecraft:spruce_stairs[facing=east] replace
fill ~4 ~5 ~-4 ~4 ~5 ~4 minecraft:spruce_stairs[facing=west] replace
fill ~-3 ~6 ~-4 ~-3 ~6 ~4 minecraft:spruce_stairs[facing=east] replace
fill ~3 ~6 ~-4 ~3 ~6 ~4 minecraft:spruce_stairs[facing=west] replace
fill ~-2 ~7 ~-4 ~-2 ~7 ~4 minecraft:spruce_stairs[facing=east] replace
fill ~2 ~7 ~-4 ~2 ~7 ~4 minecraft:spruce_stairs[facing=west] replace
fill ~-1 ~7 ~-4 ~1 ~7 ~4 minecraft:spruce_slab replace
fill ~-4 ~1 ~-1 ~-4 ~2 ~1 minecraft:glass_pane replace
fill ~4 ~1 ~-1 ~4 ~2 ~1 minecraft:glass_pane replace
setblock ~ ~ ~-3 minecraft:spruce_door[half=lower,facing=north] replace
setblock ~ ~1 ~-3 minecraft:spruce_door[half=upper,facing=north] replace
setblock ~-2 ~ ~1 minecraft:red_bed[part=foot,facing=south] replace
setblock ~-2 ~ ~2 minecraft:red_bed[part=head,facing=south] replace
setblock ~3 ~ ~2 minecraft:crafting_table replace
setblock ~2 ~ ~2 minecraft:furnace replace
setblock ~ ~2 ~ minecraft:lantern[hanging=true] replace
setblock ~3 ~ ~-1 minecraft:chest[facing=north]{"LootTable":"qm:mayhem/t2/explorer_cache"} replace
setblock ~ ~-1 ~-5 minecraft:cobblestone replace
fill ~ ~ ~-5 ~ ~2 ~-5 minecraft:air replace
tp @s ~0.5 ~0.1 ~-4.5
