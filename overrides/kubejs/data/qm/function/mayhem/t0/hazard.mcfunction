# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "ЩОСЬ ПІШЛО НЕ ТАК", "color": "red", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
effect give @s minecraft:slowness 8 1 true
effect give @s minecraft:blindness 5 0 true
summon minecraft:spider ~2 ~ ~2
