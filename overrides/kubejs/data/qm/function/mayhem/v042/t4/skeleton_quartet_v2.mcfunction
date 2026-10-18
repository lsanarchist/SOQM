# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Камерний оркестр", "color": "red"}
tellraw @s {"text": "Грають на нервах. Смички підозріло схожі на луки.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:skeleton ~5 ~ ~5 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0],"ArmorItems":[{},{},{},{"id":"minecraft:leather_helmet","count":1}],"ArmorDropChances":[0.0,0.0,0.0,0.0]}
summon minecraft:skeleton ~5 ~ ~-5 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0],"ArmorItems":[{},{},{},{"id":"minecraft:leather_helmet","count":1}],"ArmorDropChances":[0.0,0.0,0.0,0.0]}
summon minecraft:stray ~-5 ~ ~5 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0],"ArmorItems":[{},{},{},{"id":"minecraft:leather_helmet","count":1}],"ArmorDropChances":[0.0,0.0,0.0,0.0]}
summon minecraft:stray ~-5 ~ ~-5 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0],"ArmorItems":[{},{},{},{"id":"minecraft:leather_helmet","count":1}],"ArmorDropChances":[0.0,0.0,0.0,0.0]}
