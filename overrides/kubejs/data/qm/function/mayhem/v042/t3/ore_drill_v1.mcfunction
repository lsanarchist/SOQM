# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Бур без оператора", "color": "gold"}
tellraw @s {"text": "Машину не видали. Декорацію до твоєї зміни — будь ласка.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~4 ~-8 ~-4 ~-4 ~10 ~4 minecraft:air replace
fill ~4 ~-1 ~-4 ~-4 ~-1 ~4 minecraft:deepslate_bricks replace
fill ~1 ~-8 ~-1 ~-1 ~-2 ~1 minecraft:deepslate replace
fill ~ ~-8 ~ ~ ~-2 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~-1 ~ minecraft:iron_trapdoor[open=true,facing=east] replace
fill ~3 ~ ~-3 ~3 ~9 ~-3 minecraft:scaffolding replace
fill ~-3 ~ ~-3 ~-3 ~9 ~-3 minecraft:scaffolding replace
fill ~3 ~ ~3 ~3 ~9 ~3 minecraft:scaffolding replace
fill ~-3 ~ ~3 ~-3 ~9 ~3 minecraft:scaffolding replace
fill ~3 ~9 ~-3 ~-3 ~9 ~3 minecraft:spruce_slab replace
fill ~ ~ ~ ~ ~8 ~ minecraft:chain replace
setblock ~-1 ~8 ~ minecraft:grindstone[face=ceiling] replace
setblock ~ ~ ~3 minecraft:chest[facing=east]{"LootTable":"qm:mayhem/t3/prospector_cache"} replace
setblock ~5 ~-1 ~ minecraft:cobblestone replace
fill ~5 ~ ~ ~5 ~2 ~ minecraft:air replace
tp @s ~5.5 ~0.1 ~0.5
