# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Вертикальний аргумент", "color": "gold"}
tellraw @s {"text": "Просив руди? Тепер поясни їй, як спускатися.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~3 ~ ~-3 ~-3 ~14 ~3 minecraft:air replace
fill ~2 ~-1 ~-2 ~-2 ~-1 ~2 minecraft:deepslate_bricks replace
setblock ~ ~ ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~ ~-1 minecraft:tuff replace
setblock ~ ~ ~1 minecraft:tuff replace
setblock ~1 ~ ~ minecraft:tuff replace
setblock ~-1 ~ ~ minecraft:tuff replace
setblock ~ ~1 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~2 ~ minecraft:diamond_ore replace
setblock ~ ~3 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~4 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~4 ~-1 minecraft:tuff replace
setblock ~ ~4 ~1 minecraft:tuff replace
setblock ~1 ~4 ~ minecraft:tuff replace
setblock ~-1 ~4 ~ minecraft:tuff replace
setblock ~ ~5 ~ minecraft:diamond_ore replace
setblock ~ ~6 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~7 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~8 ~ minecraft:diamond_ore replace
setblock ~ ~8 ~-1 minecraft:tuff replace
setblock ~ ~8 ~1 minecraft:tuff replace
setblock ~1 ~8 ~ minecraft:tuff replace
setblock ~-1 ~8 ~ minecraft:tuff replace
setblock ~ ~9 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~10 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~11 ~ minecraft:diamond_ore replace
setblock ~ ~12 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~12 ~-1 minecraft:tuff replace
setblock ~ ~12 ~1 minecraft:tuff replace
setblock ~1 ~12 ~ minecraft:tuff replace
setblock ~-1 ~12 ~ minecraft:tuff replace
setblock ~ ~13 ~ divinerpg:rupee_ore_deepslate replace
setblock ~ ~14 ~ minecraft:diamond_ore replace
fill ~1 ~ ~-1 ~1 ~14 ~-1 minecraft:scaffolding replace
setblock ~ ~15 ~ minecraft:lightning_rod replace
setblock ~4 ~-1 ~ minecraft:cobblestone replace
fill ~4 ~ ~ ~4 ~2 ~ minecraft:air replace
tp @s ~4.5 ~0.1 ~0.5
