# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Павуче засідання", "color": "red"}
tellraw @s {"text": "У кожного учасника вісім заперечень.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:spider ~3 ~ ~3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon minecraft:spider ~3 ~ ~-3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon minecraft:spider ~-3 ~ ~3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon minecraft:spider ~-3 ~ ~-3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
effect give @s minecraft:slowness 8 1 true
particle minecraft:cloud ~ ~1 ~ 3 0.4 3 0.01 45 normal
loot give @s loot qm:mayhem/t1/materials
