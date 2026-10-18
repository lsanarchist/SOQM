# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "ЩОСЬ ПІШЛО НЕ ТАК", "color": "red", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
tellraw @s {"text":"П’ять секунд до вибуху!","color":"red"}
summon minecraft:tnt ~ ~ ~ {fuse:100s}
