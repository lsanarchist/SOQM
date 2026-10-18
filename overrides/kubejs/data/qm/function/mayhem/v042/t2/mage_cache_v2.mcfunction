# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Магічний секонд-хенд", "color": "gold"}
tellraw @s {"text": "Попередній власник розчинився. Речі — ні.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
loot give @s loot qm:mayhem/t2/mage_cache
