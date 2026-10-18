# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Вертикальний аргумент", "color": "gold"}
tellraw @s {"text": "Просив руди? Тепер поясни їй, як спускатися.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~3 ~ ~-3 ~-3 ~10 ~3 minecraft:air replace
fill ~2 ~-1 ~-2 ~-2 ~-1 ~2 minecraft:deepslate_bricks replace
setblock ~ ~ ~ mekanism:deepslate_osmium_ore replace
setblock ~ ~ ~-1 minecraft:tuff replace
setblock ~ ~ ~1 minecraft:tuff replace
setblock ~1 ~ ~ minecraft:tuff replace
setblock ~-1 ~ ~ minecraft:tuff replace
setblock ~ ~1 ~ mekanism:deepslate_osmium_ore replace
setblock ~ ~2 ~ mekanism:uranium_ore replace
setblock ~ ~3 ~ mekanism:deepslate_osmium_ore replace
setblock ~ ~4 ~ mekanism:deepslate_osmium_ore replace
setblock ~ ~4 ~-1 minecraft:tuff replace
setblock ~ ~4 ~1 minecraft:tuff replace
setblock ~1 ~4 ~ minecraft:tuff replace
setblock ~-1 ~4 ~ minecraft:tuff replace
setblock ~ ~5 ~ mekanism:uranium_ore replace
setblock ~ ~6 ~ mekanism:deepslate_osmium_ore replace
setblock ~ ~7 ~ mekanism:deepslate_osmium_ore replace
setblock ~ ~8 ~ mekanism:uranium_ore replace
setblock ~ ~8 ~-1 minecraft:tuff replace
setblock ~ ~8 ~1 minecraft:tuff replace
setblock ~1 ~8 ~ minecraft:tuff replace
setblock ~-1 ~8 ~ minecraft:tuff replace
setblock ~ ~9 ~ mekanism:deepslate_osmium_ore replace
setblock ~ ~10 ~ mekanism:deepslate_osmium_ore replace
fill ~1 ~ ~-1 ~1 ~10 ~-1 minecraft:scaffolding replace
setblock ~ ~11 ~ minecraft:lightning_rod replace
setblock ~4 ~-1 ~ minecraft:cobblestone replace
fill ~4 ~ ~ ~4 ~2 ~ minecraft:air replace
tp @s ~4.5 ~0.1 ~0.5
