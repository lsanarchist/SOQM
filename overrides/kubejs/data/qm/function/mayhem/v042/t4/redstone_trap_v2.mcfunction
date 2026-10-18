# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Інженерна премія", "color": "red"}
tellraw @s {"text": "Кнопка є. Інструкцію писали ті, хто не натискав.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~4 ~-2 ~4 ~-4 ~5 ~-4 minecraft:air replace
fill ~4 ~-2 ~4 ~-4 ~-1 ~-4 minecraft:polished_tuff replace
fill ~3 ~ ~3 ~-3 ~3 ~-3 minecraft:glass hollow
fill ~ ~ ~3 ~ ~1 ~3 minecraft:air replace
setblock ~ ~-1 ~1 minecraft:tnt replace
setblock ~ ~ ~1 minecraft:stone_pressure_plate replace
setblock ~ ~ ~-2 minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t4/gear_cache"} replace
setblock ~2 ~ ~-2 minecraft:redstone_lamp replace
setblock ~2 ~1 ~-2 minecraft:lever[face=floor,facing=south] replace
setblock ~ ~-1 ~5 minecraft:cobblestone replace
fill ~ ~ ~5 ~ ~2 ~5 minecraft:air replace
tp @s ~0.5 ~0.1 ~5.5
