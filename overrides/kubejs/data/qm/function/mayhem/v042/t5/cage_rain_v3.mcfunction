# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Контактний зоопарк", "color": "red"}
tellraw @s {"text": "Контакт ініціюють експонати.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-6 ~ ~6 ~6 ~8 ~-6 minecraft:air replace
fill ~-4 ~-1 ~5 ~-2 ~3 ~3 minecraft:iron_bars hollow
fill ~-4 ~-1 ~5 ~-2 ~-1 ~3 minecraft:mossy_stone_bricks replace
summon minecraft:skeleton ~-3 ~ ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~-3 ~4 ~4 minecraft:lantern replace
fill ~-4 ~1 ~-3 ~-2 ~5 ~-5 minecraft:iron_bars hollow
fill ~-4 ~1 ~-3 ~-2 ~1 ~-5 minecraft:mossy_stone_bricks replace
summon minecraft:skeleton ~-3 ~2 ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~-3 ~6 ~-4 minecraft:lantern replace
fill ~2 ~3 ~5 ~4 ~7 ~3 minecraft:iron_bars hollow
fill ~2 ~3 ~5 ~4 ~3 ~3 minecraft:mossy_stone_bricks replace
summon minecraft:skeleton ~3 ~4 ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~3 ~8 ~4 minecraft:lantern replace
fill ~2 ~ ~-3 ~4 ~4 ~-5 minecraft:iron_bars hollow
fill ~2 ~ ~-3 ~4 ~ ~-5 minecraft:mossy_stone_bricks replace
summon minecraft:skeleton ~3 ~1 ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~3 ~5 ~-4 minecraft:lantern replace
setblock ~ ~ ~ minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t5/gear_cache"} replace
setblock ~-8 ~-1 ~ minecraft:cobblestone replace
fill ~-8 ~ ~ ~-8 ~2 ~ minecraft:air replace
tp @s ~-7.5 ~0.1 ~0.5
