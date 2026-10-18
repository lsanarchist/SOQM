# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Промисловий димар", "color": "gold"}
tellraw @s {"text": "Завод уже схожий на завод. Залишилося побудувати завод.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-5 ~ ~5 ~5 ~16 ~-5 minecraft:air replace
fill ~-4 ~-1 ~5 ~4 ~-1 ~-5 minecraft:bricks replace
fill ~-2 ~ ~2 ~2 ~15 ~-2 minecraft:bricks hollow
fill ~-2 ~ ~1 ~-2 ~1 ~-1 minecraft:air replace
setblock ~ ~ ~ minecraft:campfire[lit=true] replace
setblock ~-1 ~ ~4 minecraft:furnace[facing=west] replace
setblock ~1 ~ ~4 minecraft:blast_furnace[facing=east] replace
setblock ~-1 ~ ~-4 minecraft:furnace[facing=west] replace
setblock ~1 ~ ~-4 minecraft:blast_furnace[facing=east] replace
setblock ~-2 ~ ~-3 minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t2/prospector_cache"} replace
setblock ~-8 ~-1 ~ minecraft:cobblestone replace
fill ~-8 ~ ~ ~-8 ~2 ~ minecraft:air replace
tp @s ~-7.5 ~0.1 ~0.5
