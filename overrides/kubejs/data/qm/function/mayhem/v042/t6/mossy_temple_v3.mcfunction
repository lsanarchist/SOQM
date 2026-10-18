# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Храм відкладених справ", "color": "light_purple"}
tellraw @s {"text": "Боги чекали героя. Ти хоча б прийшов.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-6 ~ ~6 ~6 ~8 ~-6 minecraft:air replace
fill ~-6 ~-1 ~6 ~6 ~-1 ~-6 minecraft:mossy_stone_bricks replace
fill ~-5 ~ ~5 ~5 ~ ~-5 minecraft:mossy_stone_bricks replace
fill ~-4 ~1 ~4 ~4 ~1 ~-4 minecraft:mossy_stone_bricks replace
fill ~-3 ~2 ~3 ~3 ~2 ~-3 minecraft:mossy_stone_bricks replace
fill ~-2 ~3 ~2 ~-2 ~6 ~2 minecraft:chiseled_stone_bricks replace
fill ~2 ~3 ~2 ~2 ~6 ~2 minecraft:chiseled_stone_bricks replace
fill ~-2 ~3 ~-2 ~-2 ~6 ~-2 minecraft:chiseled_stone_bricks replace
fill ~2 ~3 ~-2 ~2 ~6 ~-2 minecraft:chiseled_stone_bricks replace
fill ~-3 ~7 ~3 ~3 ~7 ~-3 minecraft:mossy_stone_bricks replace
setblock ~ ~3 ~ minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t6/mage_cache"} replace
summon minecraft:husk ~ ~3 ~2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~-8 ~-1 ~ minecraft:cobblestone replace
fill ~-8 ~ ~ ~-8 ~2 ~ minecraft:air replace
tp @s ~-7.5 ~0.1 ~0.5
