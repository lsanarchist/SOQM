# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "Усмішка долі", "color": "gold", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
effect give @s minecraft:regeneration 12 0 true
effect give @s minecraft:resistance 30 0 true
effect give @s minecraft:haste 45 0 true
experience add @s 25 points
particle minecraft:totem_of_undying ~ ~1 ~ 0.6 0.8 0.6 0.2 30 normal @a[distance=..24]
