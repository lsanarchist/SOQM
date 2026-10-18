# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Пружна вечірка", "color": "light_purple"}
tellraw @s {"text": "Гостей небагато. Поки що.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:slime ~2 ~ ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Size":1}
summon minecraft:slime ~2 ~ ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Size":1}
summon minecraft:slime ~-4 ~ ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Size":1}
loot give @s loot qm:mayhem/t0/materials
