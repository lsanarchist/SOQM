# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Прозорий натяк", "color": "light_purple"}
tellraw @s {"text": "Скарб видно. Тепер побачимо твоє терпіння.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~4 ~ ~4 ~-4 ~7 ~-4 minecraft:air replace
fill ~3 ~-1 ~3 ~-3 ~4 ~-3 minecraft:tinted_glass hollow
fill ~3 ~-1 ~3 ~-3 ~-1 ~-3 minecraft:polished_tuff replace
fill ~3 ~ ~3 ~3 ~4 ~3 minecraft:obsidian replace
fill ~3 ~ ~-3 ~3 ~4 ~-3 minecraft:obsidian replace
fill ~-3 ~ ~3 ~-3 ~4 ~3 minecraft:obsidian replace
fill ~-3 ~ ~-3 ~-3 ~4 ~-3 minecraft:obsidian replace
setblock ~ ~ ~ minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t2/gear_cache"} replace
summon minecraft:vindicator ~-1 ~ ~-1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:iron_axe","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~ ~-1 ~5 minecraft:cobblestone replace
fill ~ ~ ~5 ~ ~2 ~5 minecraft:air replace
tp @s ~0.5 ~0.1 ~5.5
