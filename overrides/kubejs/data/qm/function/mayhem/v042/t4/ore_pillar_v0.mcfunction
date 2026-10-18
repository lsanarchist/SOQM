# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Вертикальний аргумент", "color": "gold"}
tellraw @s {"text": "Просив руди? Тепер поясни їй, як спускатися.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-3 ~ ~-3 ~3 ~16 ~3 minecraft:air replace
fill ~-2 ~-1 ~-2 ~2 ~-1 ~2 minecraft:stone_bricks replace
setblock ~ ~ ~ ad_astra:moon_desh_ore replace
setblock ~-1 ~ ~ minecraft:tuff replace
setblock ~1 ~ ~ minecraft:tuff replace
setblock ~ ~ ~-1 minecraft:tuff replace
setblock ~ ~ ~1 minecraft:tuff replace
setblock ~ ~1 ~ ad_astra:moon_desh_ore replace
setblock ~ ~2 ~ ad_astra:mars_ostrum_ore replace
setblock ~ ~3 ~ ad_astra:moon_desh_ore replace
setblock ~ ~4 ~ ad_astra:moon_desh_ore replace
setblock ~-1 ~4 ~ minecraft:tuff replace
setblock ~1 ~4 ~ minecraft:tuff replace
setblock ~ ~4 ~-1 minecraft:tuff replace
setblock ~ ~4 ~1 minecraft:tuff replace
setblock ~ ~5 ~ ad_astra:mars_ostrum_ore replace
setblock ~ ~6 ~ ad_astra:moon_desh_ore replace
setblock ~ ~7 ~ ad_astra:moon_desh_ore replace
setblock ~ ~8 ~ ad_astra:mars_ostrum_ore replace
setblock ~-1 ~8 ~ minecraft:tuff replace
setblock ~1 ~8 ~ minecraft:tuff replace
setblock ~ ~8 ~-1 minecraft:tuff replace
setblock ~ ~8 ~1 minecraft:tuff replace
setblock ~ ~9 ~ ad_astra:moon_desh_ore replace
setblock ~ ~10 ~ ad_astra:moon_desh_ore replace
setblock ~ ~11 ~ ad_astra:mars_ostrum_ore replace
setblock ~ ~12 ~ ad_astra:moon_desh_ore replace
setblock ~-1 ~12 ~ minecraft:tuff replace
setblock ~1 ~12 ~ minecraft:tuff replace
setblock ~ ~12 ~-1 minecraft:tuff replace
setblock ~ ~12 ~1 minecraft:tuff replace
setblock ~ ~13 ~ ad_astra:moon_desh_ore replace
setblock ~ ~14 ~ ad_astra:mars_ostrum_ore replace
setblock ~ ~15 ~ ad_astra:moon_desh_ore replace
setblock ~ ~16 ~ ad_astra:moon_desh_ore replace
setblock ~-1 ~16 ~ minecraft:tuff replace
setblock ~1 ~16 ~ minecraft:tuff replace
setblock ~ ~16 ~-1 minecraft:tuff replace
setblock ~ ~16 ~1 minecraft:tuff replace
fill ~-1 ~ ~-1 ~-1 ~16 ~-1 minecraft:scaffolding replace
setblock ~ ~17 ~ minecraft:lightning_rod replace
setblock ~ ~-1 ~-4 minecraft:cobblestone replace
fill ~ ~ ~-4 ~ ~2 ~-4 minecraft:air replace
tp @s ~0.5 ~0.1 ~-3.5
