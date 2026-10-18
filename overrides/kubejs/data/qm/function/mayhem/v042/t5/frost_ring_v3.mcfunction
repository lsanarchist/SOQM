# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Льодовий лабіринт", "color": "light_purple"}
tellraw @s {"text": "Шлях короткий. Гальмівний шлях — ні.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-6 ~ ~6 ~6 ~5 ~-6 minecraft:air replace
fill ~-6 ~-1 ~6 ~6 ~-1 ~-6 minecraft:blue_ice replace
fill ~-6 ~ ~6 ~6 ~3 ~-6 minecraft:packed_ice hollow
fill ~-3 ~ ~3 ~3 ~2 ~-3 ecologics:ice_bricks hollow
fill ~-6 ~ ~1 ~-6 ~2 ~-1 minecraft:air replace
fill ~ ~ ~-3 ~1 ~2 ~-3 minecraft:air replace
setblock ~ ~ ~ minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t5/explorer_cache"} replace
summon minecraft:stray ~3 ~ ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
effect give @s minecraft:slowness 12 0 true
setblock ~-8 ~-1 ~ minecraft:cobblestone replace
fill ~-8 ~ ~ ~-8 ~2 ~ minecraft:air replace
tp @s ~-7.5 ~0.1 ~0.5
