# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "ЩОСЬ ПІШЛО НЕ ТАК", "color": "red", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
effect give @s minecraft:darkness 10 0 true
effect give @s minecraft:wither 8 1 true
summon minecraft:wither_skeleton ~3 ~ ~3
