# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Душ із доставкою", "color": "light_purple"}
tellraw @s {"text": "Теплу воду доведеться придумати самостійно.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~5 ~ ~-5 ~-5 ~9 ~5 minecraft:air replace
fill ~5 ~-2 ~-5 ~-5 ~-2 ~5 minecraft:tuff replace
fill ~4 ~-1 ~-4 ~-4 ~-1 ~4 minecraft:air replace
fill ~3 ~-1 ~-3 ~-3 ~-1 ~3 minecraft:calcite replace
fill ~3 ~ ~-3 ~-3 ~ ~3 minecraft:calcite replace
fill ~2 ~1 ~-2 ~-2 ~1 ~2 minecraft:calcite replace
fill ~2 ~2 ~-2 ~-2 ~2 ~2 minecraft:calcite replace
fill ~1 ~3 ~-1 ~-1 ~3 ~1 minecraft:calcite replace
fill ~ ~ ~ ~ ~7 ~ minecraft:water replace
setblock ~ ~-1 ~ minecraft:soul_sand replace
loot give @s loot qm:mayhem/t4/explorer_cache
setblock ~8 ~-1 ~ minecraft:cobblestone replace
fill ~8 ~ ~ ~8 ~2 ~ minecraft:air replace
tp @s ~8.5 ~0.1 ~0.5
