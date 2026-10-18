# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Інженерна премія", "color": "red"}
tellraw @s {"text": "Кнопка є. Інструкцію писали ті, хто не натискав.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-4 ~-2 ~4 ~4 ~5 ~-4 minecraft:air replace
fill ~-4 ~-2 ~4 ~4 ~-1 ~-4 minecraft:mossy_stone_bricks replace
fill ~-3 ~ ~3 ~3 ~3 ~-3 minecraft:glass hollow
fill ~-3 ~ ~ ~-3 ~1 ~ minecraft:air replace
setblock ~-1 ~-1 ~ minecraft:tnt replace
setblock ~-1 ~ ~ minecraft:stone_pressure_plate replace
setblock ~2 ~ ~ minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t1/gear_cache"} replace
setblock ~2 ~ ~2 minecraft:redstone_lamp replace
setblock ~2 ~1 ~2 minecraft:lever[face=floor,facing=west] replace
setblock ~-5 ~-1 ~ minecraft:cobblestone replace
fill ~-5 ~ ~ ~-5 ~2 ~ minecraft:air replace
tp @s ~-4.5 ~0.1 ~0.5
