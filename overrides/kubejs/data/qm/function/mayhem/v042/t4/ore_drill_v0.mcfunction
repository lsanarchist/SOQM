# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Бур без оператора", "color": "gold"}
tellraw @s {"text": "Машину не видали. Декорацію до твоєї зміни — будь ласка.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-4 ~-8 ~-4 ~4 ~10 ~4 minecraft:air replace
fill ~-4 ~-1 ~-4 ~4 ~-1 ~4 minecraft:stone_bricks replace
fill ~-1 ~-8 ~-1 ~1 ~-2 ~1 minecraft:deepslate replace
fill ~ ~-8 ~ ~ ~-2 ~ ad_astra:moon_desh_ore replace
setblock ~ ~-1 ~ minecraft:iron_trapdoor[open=true,facing=north] replace
fill ~-3 ~ ~-3 ~-3 ~9 ~-3 minecraft:scaffolding replace
fill ~-3 ~ ~3 ~-3 ~9 ~3 minecraft:scaffolding replace
fill ~3 ~ ~-3 ~3 ~9 ~-3 minecraft:scaffolding replace
fill ~3 ~ ~3 ~3 ~9 ~3 minecraft:scaffolding replace
fill ~-3 ~9 ~-3 ~3 ~9 ~3 minecraft:spruce_slab replace
fill ~ ~ ~ ~ ~8 ~ minecraft:chain replace
setblock ~ ~8 ~1 minecraft:grindstone[face=ceiling] replace
setblock ~3 ~ ~ minecraft:chest[facing=north]{"LootTable":"qm:mayhem/t4/prospector_cache"} replace
setblock ~ ~-1 ~-5 minecraft:cobblestone replace
fill ~ ~ ~-5 ~ ~2 ~-5 minecraft:air replace
tp @s ~0.5 ~0.1 ~-4.5
