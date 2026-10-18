# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Спорядження за чужий рахунок", "color": "gold"}
tellraw @s {"text": "Розмір приблизний. Гарантія закінчилась під час відкриття.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
loot give @s loot qm:mayhem/t6/gear_cache
