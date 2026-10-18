# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Кар’єрний ліфт", "color": "light_purple"}
tellraw @s {"text": "Кар’єрне зростання цього разу спрямоване вниз.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-3 ~-11 ~-3 ~3 ~3 ~3 minecraft:air replace
fill ~-3 ~-11 ~-3 ~3 ~-1 ~3 minecraft:deepslate_bricks hollow
fill ~-2 ~-10 ~-2 ~2 ~1 ~2 minecraft:air replace
fill ~-2 ~-10 ~-2 ~-2 ~-8 ~-2 minecraft:spruce_log replace
fill ~2 ~-10 ~-2 ~2 ~-8 ~-2 minecraft:spruce_log replace
fill ~-2 ~-8 ~-2 ~2 ~-8 ~-2 minecraft:spruce_log[axis=x] replace
setblock ~1 ~-9 ~-2 minecraft:lantern[hanging=true] replace
fill ~-2 ~-6 ~-2 ~-2 ~-4 ~-2 minecraft:spruce_log replace
fill ~2 ~-6 ~-2 ~2 ~-4 ~-2 minecraft:spruce_log replace
fill ~-2 ~-4 ~-2 ~2 ~-4 ~-2 minecraft:spruce_log[axis=x] replace
setblock ~1 ~-5 ~-2 minecraft:lantern[hanging=true] replace
fill ~-2 ~-2 ~-2 ~-2 ~ ~-2 minecraft:spruce_log replace
fill ~2 ~-2 ~-2 ~2 ~ ~-2 minecraft:spruce_log replace
fill ~-2 ~ ~-2 ~2 ~ ~-2 minecraft:spruce_log[axis=x] replace
setblock ~1 ~-1 ~-2 minecraft:lantern[hanging=true] replace
fill ~ ~-10 ~2 ~ ~1 ~2 minecraft:ladder[facing=north] replace
fill ~-1 ~-10 ~-1 ~1 ~-10 ~ divinerpg:arlemite_ore replace
setblock ~1 ~-9 ~1 minecraft:chest[facing=north]{"LootTable":"qm:mayhem/t2/prospector_cache"} replace
summon minecraft:cave_spider ~-1 ~-9 ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~ ~-1 ~-4 minecraft:cobblestone replace
fill ~ ~ ~-4 ~ ~2 ~-4 minecraft:air replace
tp @s ~0.5 ~0.1 ~-3.5
