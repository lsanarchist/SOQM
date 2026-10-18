# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Валіза професійного блукальця", "color": "gold"}
tellraw @s {"text": "Куди йти — не написано. Зате тепер є з чим.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
loot give @s loot qm:mayhem/t5/explorer_cache
