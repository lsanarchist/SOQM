# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Житло після ремонту", "color": "light_purple"}
tellraw @s {"text": "Попередній власник теж відкривав тут куби.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-5 ~ ~5 ~5 ~8 ~-5 minecraft:air replace
fill ~-3 ~-1 ~4 ~3 ~-1 ~-4 minecraft:mossy_stone_bricks replace
fill ~-3 ~ ~4 ~3 ~3 ~-4 ecologics:azalea_planks hollow
fill ~-3 ~ ~4 ~-3 ~3 ~4 ecologics:azalea_log replace
fill ~3 ~ ~4 ~3 ~3 ~4 ecologics:azalea_log replace
fill ~-3 ~ ~-4 ~-3 ~3 ~-4 ecologics:azalea_log replace
fill ~3 ~ ~-4 ~3 ~3 ~-4 ecologics:azalea_log replace
fill ~-4 ~4 ~5 ~4 ~4 ~5 minecraft:spruce_stairs[facing=north] replace
fill ~-4 ~4 ~-5 ~4 ~4 ~-5 minecraft:spruce_stairs[facing=south] replace
fill ~-4 ~5 ~4 ~4 ~5 ~4 minecraft:spruce_stairs[facing=north] replace
fill ~-4 ~5 ~-4 ~4 ~5 ~-4 minecraft:spruce_stairs[facing=south] replace
fill ~-4 ~6 ~3 ~4 ~6 ~3 minecraft:spruce_stairs[facing=north] replace
fill ~-4 ~6 ~-3 ~4 ~6 ~-3 minecraft:spruce_stairs[facing=south] replace
fill ~-4 ~7 ~2 ~4 ~7 ~2 minecraft:spruce_stairs[facing=north] replace
fill ~-4 ~7 ~-2 ~4 ~7 ~-2 minecraft:spruce_stairs[facing=south] replace
fill ~-4 ~7 ~1 ~4 ~7 ~-1 minecraft:spruce_slab replace
fill ~-1 ~1 ~4 ~1 ~2 ~4 minecraft:glass_pane replace
fill ~-1 ~1 ~-4 ~1 ~2 ~-4 minecraft:glass_pane replace
setblock ~-3 ~ ~ minecraft:cherry_door[half=lower,facing=west] replace
setblock ~-3 ~1 ~ minecraft:cherry_door[half=upper,facing=west] replace
setblock ~1 ~ ~2 minecraft:red_bed[part=foot,facing=east] replace
setblock ~2 ~ ~2 minecraft:red_bed[part=head,facing=east] replace
setblock ~2 ~ ~-3 minecraft:crafting_table replace
setblock ~2 ~ ~-2 minecraft:furnace replace
setblock ~ ~2 ~ minecraft:lantern[hanging=true] replace
setblock ~-1 ~ ~-3 minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t3/explorer_cache"} replace
fill ~-4 ~1 ~-1 ~1 ~7 ~-5 minecraft:air replace
setblock ~2 ~2 ~3 minecraft:cobweb replace
setblock ~2 ~ ~-1 minecraft:moss_block replace
summon cannibalvillages:canibal ~ ~ ~-2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~-5 ~-1 ~ minecraft:cobblestone replace
fill ~-5 ~ ~ ~-5 ~2 ~ minecraft:air replace
tp @s ~-4.5 ~0.1 ~0.5
