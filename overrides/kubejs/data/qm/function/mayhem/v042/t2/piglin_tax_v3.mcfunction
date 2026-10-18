# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Податкова перевірка", "color": "red"}
tellraw @s {"text": "Золото не пахне. Інспектори іноді хрюкають.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:piglin ~-2 ~ ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:golden_sword","count":1},{}],"HandDropChances":[0.0,0.0],"IsImmuneToZombification":true}
summon minecraft:piglin ~-2 ~ ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:golden_sword","count":1},{}],"HandDropChances":[0.0,0.0],"IsImmuneToZombification":true}
summon minecraft:piglin ~4 ~ ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:golden_sword","count":1},{}],"HandDropChances":[0.0,0.0],"IsImmuneToZombification":true}
loot give @s loot qm:mayhem/t2/components
