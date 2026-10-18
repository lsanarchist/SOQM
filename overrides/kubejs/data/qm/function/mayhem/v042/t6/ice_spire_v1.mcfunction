# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Морозний кредит", "color": "light_purple"}
tellraw @s {"text": "Відсотки капають бурульками.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~5 ~ ~-5 ~-5 ~18 ~5 minecraft:air replace
fill ~4 ~-1 ~-4 ~-4 ~-1 ~4 ecologics:ice_bricks replace
fill ~4 ~ ~-4 ~-4 ~ ~4 minecraft:packed_ice replace
fill ~4 ~1 ~-4 ~-4 ~1 ~4 minecraft:packed_ice replace
fill ~4 ~2 ~-4 ~-4 ~2 ~4 ecologics:ice_bricks replace
fill ~3 ~3 ~-3 ~-3 ~3 ~3 minecraft:packed_ice replace
fill ~3 ~4 ~-3 ~-3 ~4 ~3 minecraft:packed_ice replace
fill ~3 ~5 ~-3 ~-3 ~5 ~3 ecologics:ice_bricks replace
fill ~3 ~6 ~-3 ~-3 ~6 ~3 minecraft:packed_ice replace
fill ~2 ~7 ~-2 ~-2 ~7 ~2 minecraft:packed_ice replace
fill ~2 ~8 ~-2 ~-2 ~8 ~2 ecologics:ice_bricks replace
fill ~2 ~9 ~-2 ~-2 ~9 ~2 minecraft:packed_ice replace
fill ~2 ~10 ~-2 ~-2 ~10 ~2 minecraft:packed_ice replace
fill ~1 ~11 ~-1 ~-1 ~11 ~1 ecologics:ice_bricks replace
fill ~1 ~12 ~-1 ~-1 ~12 ~1 minecraft:packed_ice replace
fill ~1 ~13 ~-1 ~-1 ~13 ~1 minecraft:packed_ice replace
fill ~1 ~14 ~-1 ~-1 ~14 ~1 ecologics:ice_bricks replace
fill ~ ~15 ~ ~ ~15 ~ minecraft:packed_ice replace
fill ~4 ~ ~-1 ~-1 ~2 ~1 minecraft:air replace
setblock ~ ~ ~ minecraft:chest[facing=east]{"LootTable":"qm:mayhem/t6/explorer_cache"} replace
summon minecraft:stray ~4 ~ ~3 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~8 ~-1 ~ minecraft:cobblestone replace
fill ~8 ~ ~ ~8 ~2 ~ minecraft:air replace
tp @s ~8.5 ~0.1 ~0.5
