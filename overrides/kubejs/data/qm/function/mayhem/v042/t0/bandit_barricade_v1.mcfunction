# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Митниця просто неба", "color": "red"}
tellraw @s {"text": "Документи? Ресурси? Запасне обличчя?", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~6 ~ ~-6 ~-6 ~5 ~6 minecraft:air replace
fill ~3 ~-1 ~-6 ~-3 ~-1 ~6 minecraft:coarse_dirt replace
fill ~-1 ~ ~-6 ~-1 ~2 ~-6 minecraft:oak_log replace
fill ~-1 ~ ~-5 ~-1 ~3 ~-5 minecraft:oak_log replace
fill ~-1 ~ ~-4 ~-1 ~2 ~-4 minecraft:oak_log replace
fill ~-1 ~ ~-3 ~-1 ~3 ~-3 minecraft:oak_log replace
fill ~-1 ~ ~-2 ~-1 ~2 ~-2 minecraft:oak_log replace
setblock ~-1 ~ ~-1 minecraft:spruce_fence_gate[facing=east] replace
setblock ~-1 ~ ~ minecraft:spruce_fence_gate[facing=east] replace
setblock ~-1 ~ ~1 minecraft:spruce_fence_gate[facing=east] replace
fill ~-1 ~ ~2 ~-1 ~2 ~2 minecraft:oak_log replace
fill ~-1 ~ ~3 ~-1 ~3 ~3 minecraft:oak_log replace
fill ~-1 ~ ~4 ~-1 ~2 ~4 minecraft:oak_log replace
fill ~-1 ~ ~5 ~-1 ~3 ~5 minecraft:oak_log replace
fill ~-1 ~ ~6 ~-1 ~2 ~6 minecraft:oak_log replace
setblock ~1 ~ ~4 minecraft:chest[facing=east]{"LootTable":"qm:mayhem/t0/gear_cache"} replace
summon minecraft:pillager ~1 ~ ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:crossbow","count":1},{}],"HandDropChances":[0.0,0.0]}
summon minecraft:pillager ~1 ~ ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:crossbow","count":1},{}],"HandDropChances":[0.0,0.0]}
summon minecraft:pillager ~1 ~ ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:crossbow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~8 ~-1 ~ minecraft:cobblestone replace
fill ~8 ~ ~ ~8 ~2 ~ minecraft:air replace
tp @s ~8.5 ~0.1 ~0.5
