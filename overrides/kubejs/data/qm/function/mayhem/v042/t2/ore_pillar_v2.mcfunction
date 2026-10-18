# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Вертикальний аргумент", "color": "gold"}
tellraw @s {"text": "Просив руди? Тепер поясни їй, як спускатися.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~3 ~ ~3 ~-3 ~13 ~-3 minecraft:air replace
fill ~2 ~-1 ~2 ~-2 ~-1 ~-2 minecraft:polished_tuff replace
setblock ~ ~ ~ mekanism:deepslate_fluorite_ore replace
setblock ~1 ~ ~ minecraft:tuff replace
setblock ~-1 ~ ~ minecraft:tuff replace
setblock ~ ~ ~1 minecraft:tuff replace
setblock ~ ~ ~-1 minecraft:tuff replace
setblock ~ ~1 ~ mekanism:deepslate_fluorite_ore replace
setblock ~ ~2 ~ minecraft:redstone_ore replace
setblock ~ ~3 ~ mekanism:deepslate_fluorite_ore replace
setblock ~ ~4 ~ mekanism:deepslate_fluorite_ore replace
setblock ~1 ~4 ~ minecraft:tuff replace
setblock ~-1 ~4 ~ minecraft:tuff replace
setblock ~ ~4 ~1 minecraft:tuff replace
setblock ~ ~4 ~-1 minecraft:tuff replace
setblock ~ ~5 ~ minecraft:redstone_ore replace
setblock ~ ~6 ~ mekanism:deepslate_fluorite_ore replace
setblock ~ ~7 ~ mekanism:deepslate_fluorite_ore replace
setblock ~ ~8 ~ minecraft:redstone_ore replace
setblock ~1 ~8 ~ minecraft:tuff replace
setblock ~-1 ~8 ~ minecraft:tuff replace
setblock ~ ~8 ~1 minecraft:tuff replace
setblock ~ ~8 ~-1 minecraft:tuff replace
setblock ~ ~9 ~ mekanism:deepslate_fluorite_ore replace
setblock ~ ~10 ~ mekanism:deepslate_fluorite_ore replace
setblock ~ ~11 ~ minecraft:redstone_ore replace
setblock ~ ~12 ~ mekanism:deepslate_fluorite_ore replace
setblock ~1 ~12 ~ minecraft:tuff replace
setblock ~-1 ~12 ~ minecraft:tuff replace
setblock ~ ~12 ~1 minecraft:tuff replace
setblock ~ ~12 ~-1 minecraft:tuff replace
setblock ~ ~13 ~ mekanism:deepslate_fluorite_ore replace
fill ~1 ~ ~1 ~1 ~13 ~1 minecraft:scaffolding replace
setblock ~ ~14 ~ minecraft:lightning_rod replace
setblock ~ ~-1 ~4 minecraft:cobblestone replace
fill ~ ~ ~4 ~ ~2 ~4 minecraft:air replace
tp @s ~0.5 ~0.1 ~4.5
