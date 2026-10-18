# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "ЗАСІДКА", "color": "red", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
summon minecraft:pillager ~-3 ~ ~-3 {"PersistenceRequired":false}
summon minecraft:pillager ~3 ~ ~-3 {"PersistenceRequired":false}
summon minecraft:vindicator ~-3 ~ ~3 {"PersistenceRequired":false}
