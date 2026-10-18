# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Рейс без посадкової смуги", "color": "light_purple"}
tellraw @s {"text": "Багаж приїхав. Решта авіакомпанії — теж.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~7 ~ ~7 ~-7 ~12 ~-7 minecraft:air replace
fill ~6 ~-1 ~2 ~-6 ~-1 ~-2 ecologics:walnut_planks replace
fill ~5 ~ ~3 ~-4 ~2 ~3 ecologics:walnut_planks replace
fill ~5 ~ ~-3 ~-4 ~2 ~-3 ecologics:walnut_planks replace
fill ~6 ~ ~2 ~6 ~2 ~-2 ecologics:walnut_log replace
fill ~4 ~5 ~2 ~-4 ~9 ~-2 minecraft:lime_wool hollow
fill ~3 ~10 ~1 ~-3 ~10 ~-1 minecraft:white_wool replace
fill ~3 ~1 ~2 ~3 ~5 ~2 minecraft:chain replace
fill ~3 ~1 ~-2 ~3 ~5 ~-2 minecraft:chain replace
fill ~-3 ~1 ~2 ~-3 ~5 ~2 minecraft:chain replace
fill ~-3 ~1 ~-2 ~-3 ~5 ~-2 minecraft:chain replace
fill ~-3 ~ ~3 ~-7 ~8 ~ minecraft:air replace
setblock ~-5 ~ ~-2 minecraft:campfire replace
setblock ~4 ~ ~ minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t1/explorer_cache"} replace
summon minecraft:pillager ~-1 ~ ~-1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:crossbow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~ ~-1 ~5 minecraft:cobblestone replace
fill ~ ~ ~5 ~ ~2 ~5 minecraft:air replace
tp @s ~0.5 ~0.1 ~5.5
