# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "ЩОСЬ ПІШЛО НЕ ТАК", "color": "red", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
effect give @s minecraft:levitation 5 2 true
effect give @s minecraft:slow_falling 8 0 true
summon minecraft:skeleton ~3 ~ ~3
