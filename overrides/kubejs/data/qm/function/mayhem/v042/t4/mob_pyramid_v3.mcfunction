# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Кар’єрна ієрархія", "color": "red"}
tellraw @s {"text": "Той, хто зверху, теж не знає, що відбувається.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:spider ~ ~ ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Passengers":[{"id":"minecraft:zombie","Tags":["qm_lucky_event"],"Passengers":[{"id":"minecraft:witch","Tags":["qm_lucky_event"]}]}]}
summon minecraft:spider ~ ~ ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Passengers":[{"id":"minecraft:skeleton","Tags":["qm_lucky_event"],"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}]}
