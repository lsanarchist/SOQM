# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Підвальний open space", "color": "red"}
tellraw @s {"text": "Колеги тихі. Резюме передають стрілами.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-5 ~-7 ~5 ~5 ~3 ~-5 minecraft:air replace
fill ~-4 ~-7 ~4 ~4 ~-2 ~-4 minecraft:deepslate_bricks hollow
fill ~-4 ~-6 ~1 ~-3 ~1 ~-1 minecraft:air replace
fill ~-4 ~-6 ~ ~-4 ~1 ~ minecraft:deepslate_bricks replace
fill ~-3 ~-6 ~ ~-3 ~1 ~ minecraft:ladder[facing=east] replace
fill ~-1 ~-6 ~2 ~2 ~-6 ~2 minecraft:polished_deepslate_slab replace
summon minecraft:skeleton ~1 ~-5 ~2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
fill ~-1 ~-6 ~-2 ~2 ~-6 ~-2 minecraft:polished_deepslate_slab replace
summon minecraft:skeleton ~1 ~-5 ~-2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~3 ~-6 ~ minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t3/mage_cache"} replace
setblock ~ ~-2 ~ minecraft:soul_lantern[hanging=true] replace
setblock ~-5 ~-1 ~ minecraft:cobblestone replace
fill ~-5 ~ ~ ~-5 ~2 ~ minecraft:air replace
tp @s ~-4.5 ~0.1 ~0.5
