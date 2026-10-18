# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Контактний зоопарк", "color": "red"}
tellraw @s {"text": "Контакт ініціюють експонати.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-6 ~ ~-6 ~6 ~8 ~6 minecraft:air replace
fill ~-5 ~-1 ~-4 ~-3 ~3 ~-2 minecraft:iron_bars hollow
fill ~-5 ~-1 ~-4 ~-3 ~-1 ~-2 minecraft:stone_bricks replace
summon minecraft:skeleton ~-4 ~ ~-3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~-4 ~4 ~-3 minecraft:lantern replace
fill ~3 ~1 ~-4 ~5 ~5 ~-2 minecraft:iron_bars hollow
fill ~3 ~1 ~-4 ~5 ~1 ~-2 minecraft:stone_bricks replace
summon minecraft:skeleton ~4 ~2 ~-3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~4 ~6 ~-3 minecraft:lantern replace
fill ~-5 ~3 ~2 ~-3 ~7 ~4 minecraft:iron_bars hollow
fill ~-5 ~3 ~2 ~-3 ~3 ~4 minecraft:stone_bricks replace
summon minecraft:skeleton ~-4 ~4 ~3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~-4 ~8 ~3 minecraft:lantern replace
fill ~3 ~ ~2 ~5 ~4 ~4 minecraft:iron_bars hollow
fill ~3 ~ ~2 ~5 ~ ~4 minecraft:stone_bricks replace
summon minecraft:skeleton ~4 ~1 ~3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~4 ~5 ~3 minecraft:lantern replace
setblock ~ ~ ~ minecraft:chest[facing=north]{"LootTable":"qm:mayhem/t4/gear_cache"} replace
setblock ~ ~-1 ~-8 minecraft:cobblestone replace
fill ~ ~ ~-8 ~ ~2 ~-8 minecraft:air replace
tp @s ~0.5 ~0.1 ~-7.5
