# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Кар’єрний ліфт", "color": "light_purple"}
tellraw @s {"text": "Кар’єрне зростання цього разу спрямоване вниз.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-3 ~-11 ~3 ~3 ~3 ~-3 minecraft:air replace
fill ~-3 ~-11 ~3 ~3 ~-1 ~-3 minecraft:deepslate_bricks hollow
fill ~-2 ~-10 ~2 ~2 ~1 ~-2 minecraft:air replace
fill ~-2 ~-10 ~2 ~-2 ~-8 ~2 ecologics:azalea_log replace
fill ~-2 ~-10 ~-2 ~-2 ~-8 ~-2 ecologics:azalea_log replace
fill ~-2 ~-8 ~2 ~-2 ~-8 ~-2 ecologics:azalea_log[axis=z] replace
setblock ~-2 ~-9 ~-1 minecraft:lantern[hanging=true] replace
fill ~-2 ~-6 ~2 ~-2 ~-4 ~2 ecologics:azalea_log replace
fill ~-2 ~-6 ~-2 ~-2 ~-4 ~-2 ecologics:azalea_log replace
fill ~-2 ~-4 ~2 ~-2 ~-4 ~-2 ecologics:azalea_log[axis=z] replace
setblock ~-2 ~-5 ~-1 minecraft:lantern[hanging=true] replace
fill ~-2 ~-2 ~2 ~-2 ~ ~2 ecologics:azalea_log replace
fill ~-2 ~-2 ~-2 ~-2 ~ ~-2 ecologics:azalea_log replace
fill ~-2 ~ ~2 ~-2 ~ ~-2 ecologics:azalea_log[axis=z] replace
setblock ~-2 ~-1 ~-1 minecraft:lantern[hanging=true] replace
fill ~2 ~-10 ~ ~2 ~1 ~ minecraft:ladder[facing=west] replace
fill ~-1 ~-10 ~1 ~ ~-10 ~-1 minecraft:gold_ore replace
setblock ~1 ~-9 ~-1 minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t1/prospector_cache"} replace
summon minecraft:spider ~1 ~-9 ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~-4 ~-1 ~ minecraft:cobblestone replace
fill ~-4 ~ ~ ~-4 ~2 ~ minecraft:air replace
tp @s ~-3.5 ~0.1 ~0.5
