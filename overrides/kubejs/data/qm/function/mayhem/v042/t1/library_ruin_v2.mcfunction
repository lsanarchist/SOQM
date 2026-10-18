# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Бібліотека практичних помилок", "color": "gold"}
tellraw @s {"text": "Книги пережили читачів. Статистика обнадіює.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~5 ~ ~5 ~-5 ~6 ~-5 minecraft:air replace
fill ~4 ~-1 ~4 ~-4 ~4 ~-4 minecraft:mossy_stone_bricks hollow
fill ~4 ~-1 ~4 ~-4 ~-1 ~-4 ecologics:walnut_planks replace
fill ~-1 ~3 ~4 ~-4 ~4 ~-1 minecraft:air replace
fill ~3 ~ ~3 ~-3 ~2 ~3 minecraft:bookshelf replace
fill ~3 ~ ~-3 ~-3 ~2 ~-3 minecraft:bookshelf replace
fill ~1 ~ ~4 ~-1 ~2 ~3 minecraft:air replace
setblock ~ ~ ~ minecraft:lectern replace
setblock ~-3 ~ ~ minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t1/mage_cache"} replace
setblock ~ ~-1 ~6 minecraft:cobblestone replace
fill ~ ~ ~6 ~ ~2 ~6 minecraft:air replace
tp @s ~0.5 ~0.1 ~6.5
