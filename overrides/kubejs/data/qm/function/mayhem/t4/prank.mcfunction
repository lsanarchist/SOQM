# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "Куб мав інші плани", "color": "red", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
summon minecraft:allay ~-2 ~1 ~2
summon minecraft:allay ~-1 ~1 ~2
playsound minecraft:entity.villager.no player @s ~ ~ ~ 1 0.7
tellraw @s {"text":"Доля передумала. Цього разу без скарбу.","color":"gray"}
