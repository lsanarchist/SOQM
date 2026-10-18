# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Міст у світле завтра", "color": "gold"}
tellraw @s {"text": "Завтра могло почекати, але міст уже тут.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~2 ~ ~2 ~-2 ~4 ~-2 minecraft:air replace
fill ~9 ~-1 ~2 ~-9 ~-1 ~-2 minecraft:polished_basalt[axis=x] replace
fill ~9 ~ ~2 ~-9 ~ ~2 minecraft:blackstone_wall replace
fill ~9 ~ ~-2 ~-9 ~ ~-2 minecraft:blackstone_wall replace
fill ~9 ~ ~1 ~-9 ~3 ~-1 minecraft:air replace
fill ~8 ~-7 ~2 ~8 ~-2 ~2 minecraft:basalt replace
setblock ~8 ~1 ~2 minecraft:lantern replace
fill ~8 ~-7 ~-2 ~8 ~-2 ~-2 minecraft:basalt replace
setblock ~8 ~1 ~-2 minecraft:lantern replace
fill ~4 ~-7 ~2 ~4 ~-2 ~2 minecraft:basalt replace
setblock ~4 ~1 ~2 minecraft:lantern replace
fill ~4 ~-7 ~-2 ~4 ~-2 ~-2 minecraft:basalt replace
setblock ~4 ~1 ~-2 minecraft:lantern replace
fill ~ ~-7 ~2 ~ ~-2 ~2 minecraft:basalt replace
setblock ~ ~1 ~2 minecraft:lantern replace
fill ~ ~-7 ~-2 ~ ~-2 ~-2 minecraft:basalt replace
setblock ~ ~1 ~-2 minecraft:lantern replace
fill ~-4 ~-7 ~2 ~-4 ~-2 ~2 minecraft:basalt replace
setblock ~-4 ~1 ~2 minecraft:lantern replace
fill ~-4 ~-7 ~-2 ~-4 ~-2 ~-2 minecraft:basalt replace
setblock ~-4 ~1 ~-2 minecraft:lantern replace
fill ~-8 ~-7 ~2 ~-8 ~-2 ~2 minecraft:basalt replace
setblock ~-8 ~1 ~2 minecraft:lantern replace
fill ~-8 ~-7 ~-2 ~-8 ~-2 ~-2 minecraft:basalt replace
setblock ~-8 ~1 ~-2 minecraft:lantern replace
setblock ~-8 ~ ~ minecraft:chest[facing=east]{"LootTable":"qm:mayhem/t4/prospector_cache"} replace
setblock ~8 ~-1 ~ minecraft:cobblestone replace
fill ~8 ~ ~ ~8 ~2 ~ minecraft:air replace
tp @s ~8.5 ~0.1 ~0.5
