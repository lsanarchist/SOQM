# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Промисловий димар", "color": "gold"}
tellraw @s {"text": "Завод уже схожий на завод. Залишилося побудувати завод.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~5 ~ ~5 ~-5 ~16 ~-5 minecraft:air replace
fill ~5 ~-1 ~4 ~-5 ~-1 ~-4 minecraft:bricks replace
fill ~2 ~ ~2 ~-2 ~14 ~-2 minecraft:bricks hollow
fill ~1 ~ ~2 ~-1 ~1 ~2 minecraft:air replace
setblock ~ ~ ~ minecraft:campfire[lit=true] replace
setblock ~4 ~ ~1 minecraft:furnace[facing=south] replace
setblock ~4 ~ ~-1 minecraft:blast_furnace[facing=north] replace
setblock ~-4 ~ ~1 minecraft:furnace[facing=south] replace
setblock ~-4 ~ ~-1 minecraft:blast_furnace[facing=north] replace
setblock ~-3 ~ ~2 minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t1/prospector_cache"} replace
setblock ~ ~-1 ~8 minecraft:cobblestone replace
fill ~ ~ ~8 ~ ~2 ~8 minecraft:air replace
tp @s ~0.5 ~0.1 ~8.5
