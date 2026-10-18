# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Підвальний open space", "color": "red"}
tellraw @s {"text": "Колеги тихі. Резюме передають стрілами.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-5 ~-7 ~-5 ~5 ~3 ~5 minecraft:air replace
fill ~-4 ~-7 ~-4 ~4 ~-2 ~4 minecraft:deepslate_bricks hollow
fill ~-1 ~-6 ~-4 ~1 ~1 ~-3 minecraft:air replace
fill ~ ~-6 ~-4 ~ ~1 ~-4 minecraft:deepslate_bricks replace
fill ~ ~-6 ~-3 ~ ~1 ~-3 minecraft:ladder[facing=south] replace
fill ~-2 ~-6 ~-1 ~-2 ~-6 ~2 minecraft:polished_deepslate_slab replace
summon minecraft:skeleton ~-2 ~-5 ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
fill ~2 ~-6 ~-1 ~2 ~-6 ~2 minecraft:polished_deepslate_slab replace
summon minecraft:skeleton ~2 ~-5 ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~ ~-6 ~3 minecraft:chest[facing=north]{"LootTable":"qm:mayhem/t4/mage_cache"} replace
setblock ~ ~-2 ~ minecraft:soul_lantern[hanging=true] replace
setblock ~ ~-1 ~-5 minecraft:cobblestone replace
fill ~ ~ ~-5 ~ ~2 ~-5 minecraft:air replace
tp @s ~0.5 ~0.1 ~-4.5
