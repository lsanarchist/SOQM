# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Вечеря при свічках", "color": "red"}
tellraw @s {"text": "Свічки шиплять. Бронювання скасувати вже складно.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:creeper ~-5 ~ ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Fuse":45,"ExplosionRadius":2}
summon minecraft:creeper ~5 ~ ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Fuse":45,"ExplosionRadius":2}
summon minecraft:creeper ~ ~ ~5 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Fuse":45,"ExplosionRadius":2}
particle minecraft:heart ~ ~1 ~ 2 0.5 2 0.1 12 normal
