# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Міст у світле завтра", "color": "gold"}
tellraw @s {"text": "Завтра могло почекати, але міст уже тут.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~2 ~ ~-2 ~-2 ~4 ~2 minecraft:air replace
fill ~2 ~-1 ~-9 ~-2 ~-1 ~9 minecraft:polished_basalt[axis=z] replace
fill ~2 ~ ~-9 ~2 ~ ~9 minecraft:blackstone_wall replace
fill ~-2 ~ ~-9 ~-2 ~ ~9 minecraft:blackstone_wall replace
fill ~1 ~ ~-9 ~-1 ~3 ~9 minecraft:air replace
fill ~2 ~-7 ~-8 ~2 ~-2 ~-8 minecraft:basalt replace
setblock ~2 ~1 ~-8 minecraft:lantern replace
fill ~-2 ~-7 ~-8 ~-2 ~-2 ~-8 minecraft:basalt replace
setblock ~-2 ~1 ~-8 minecraft:lantern replace
fill ~2 ~-7 ~-4 ~2 ~-2 ~-4 minecraft:basalt replace
setblock ~2 ~1 ~-4 minecraft:lantern replace
fill ~-2 ~-7 ~-4 ~-2 ~-2 ~-4 minecraft:basalt replace
setblock ~-2 ~1 ~-4 minecraft:lantern replace
fill ~2 ~-7 ~ ~2 ~-2 ~ minecraft:basalt replace
setblock ~2 ~1 ~ minecraft:lantern replace
fill ~-2 ~-7 ~ ~-2 ~-2 ~ minecraft:basalt replace
setblock ~-2 ~1 ~ minecraft:lantern replace
fill ~2 ~-7 ~4 ~2 ~-2 ~4 minecraft:basalt replace
setblock ~2 ~1 ~4 minecraft:lantern replace
fill ~-2 ~-7 ~4 ~-2 ~-2 ~4 minecraft:basalt replace
setblock ~-2 ~1 ~4 minecraft:lantern replace
fill ~2 ~-7 ~8 ~2 ~-2 ~8 minecraft:basalt replace
setblock ~2 ~1 ~8 minecraft:lantern replace
fill ~-2 ~-7 ~8 ~-2 ~-2 ~8 minecraft:basalt replace
setblock ~-2 ~1 ~8 minecraft:lantern replace
setblock ~ ~ ~8 minecraft:chest[facing=north]{"LootTable":"qm:mayhem/t6/prospector_cache"} replace
setblock ~ ~-1 ~-8 minecraft:cobblestone replace
fill ~ ~ ~-8 ~ ~2 ~-8 minecraft:air replace
tp @s ~0.5 ~0.1 ~-7.5
