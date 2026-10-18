# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Кишеня геолога", "color": "gold"}
tellraw @s {"text": "Каміння з характером. Машини доведеться крафтити самому.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
loot give @s loot qm:mayhem/t0/prospector_cache
