# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "ЩОСЬ ПІШЛО НЕ ТАК", "color": "red", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
tellraw @s {"text":"Тікай: дві бомби, п’ять секунд!","color":"red"}
summon minecraft:tnt ~2 ~ ~ {fuse:100s}
summon minecraft:tnt ~-2 ~ ~ {fuse:100s}
