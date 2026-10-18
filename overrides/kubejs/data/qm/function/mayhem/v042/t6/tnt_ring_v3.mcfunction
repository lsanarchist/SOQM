# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Круглий стіл", "color": "red"}
tellraw @s {"text": "На обговорення виділено шість секунд.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:tnt ~ ~ ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"fuse":150}
summon minecraft:tnt ~ ~ ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"fuse":150}
summon minecraft:tnt ~-4 ~ ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"fuse":150}
summon minecraft:tnt ~4 ~ ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"fuse":150}
playsound minecraft:entity.tnt.primed player @s ~ ~ ~ 1 0.8
