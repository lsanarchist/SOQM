# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Рейс без посадкової смуги", "color": "light_purple"}
tellraw @s {"text": "Багаж приїхав. Решта авіакомпанії — теж.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~7 ~ ~-7 ~-7 ~12 ~7 minecraft:air replace
fill ~2 ~-1 ~-6 ~-2 ~-1 ~6 minecraft:oak_planks replace
fill ~3 ~ ~-5 ~3 ~2 ~4 minecraft:oak_planks replace
fill ~-3 ~ ~-5 ~-3 ~2 ~4 minecraft:oak_planks replace
fill ~2 ~ ~-6 ~-2 ~2 ~-6 minecraft:oak_log replace
fill ~2 ~5 ~-4 ~-2 ~9 ~4 minecraft:blue_wool hollow
fill ~1 ~10 ~-3 ~-1 ~10 ~3 minecraft:white_wool replace
fill ~2 ~1 ~-3 ~2 ~5 ~-3 minecraft:chain replace
fill ~-2 ~1 ~-3 ~-2 ~5 ~-3 minecraft:chain replace
fill ~2 ~1 ~3 ~2 ~5 ~3 minecraft:chain replace
fill ~-2 ~1 ~3 ~-2 ~5 ~3 minecraft:chain replace
fill ~3 ~ ~3 ~ ~8 ~7 minecraft:air replace
setblock ~-2 ~ ~5 minecraft:campfire replace
setblock ~ ~ ~-4 minecraft:chest[facing=east]{"LootTable":"qm:mayhem/t1/explorer_cache"} replace
summon minecraft:pillager ~-1 ~ ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:crossbow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~5 ~-1 ~ minecraft:cobblestone replace
fill ~5 ~ ~ ~5 ~2 ~ minecraft:air replace
tp @s ~5.5 ~0.1 ~0.5
