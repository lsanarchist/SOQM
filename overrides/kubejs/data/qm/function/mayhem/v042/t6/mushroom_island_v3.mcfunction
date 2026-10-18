# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Грибна нерухомість", "color": "gold"}
tellraw @s {"text": "Ні, город поливати не треба. Дах уже виріс.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-6 ~ ~6 ~6 ~10 ~-6 minecraft:air replace
fill ~-5 ~-2 ~5 ~5 ~-1 ~-5 minecraft:dirt replace
fill ~-5 ~-1 ~5 ~5 ~-1 ~-5 minecraft:mycelium replace
fill ~-2 ~ ~2 ~-2 ~5 ~2 minecraft:mushroom_stem replace
fill ~-4 ~5 ~4 ~ ~5 ~ minecraft:red_mushroom_block replace
fill ~-3 ~6 ~3 ~-1 ~6 ~1 minecraft:red_mushroom_block replace
fill ~2 ~ ~-2 ~2 ~7 ~-2 minecraft:mushroom_stem replace
fill ~ ~7 ~ ~4 ~7 ~-4 minecraft:brown_mushroom_block replace
fill ~1 ~8 ~-1 ~3 ~8 ~-3 minecraft:brown_mushroom_block replace
setblock ~3 ~ ~1 minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t6/explorer_cache"} replace
summon minecraft:mooshroom ~-2 ~ ~-3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~-8 ~-1 ~ minecraft:cobblestone replace
fill ~-8 ~ ~ ~-8 ~2 ~ minecraft:air replace
tp @s ~-7.5 ~0.1 ~0.5
