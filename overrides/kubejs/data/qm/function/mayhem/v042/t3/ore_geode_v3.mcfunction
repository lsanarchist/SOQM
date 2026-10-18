# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Камінь із характером", "color": "gold"}
tellraw @s {"text": "Всередині блищить. Зовні теж доведеться попрацювати.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-5 ~ ~5 ~5 ~7 ~-5 minecraft:air replace
fill ~-2 ~-2 ~2 ~2 ~-2 ~-2 minecraft:smooth_basalt replace
fill ~-4 ~-1 ~4 ~4 ~-1 ~-4 minecraft:smooth_basalt replace
fill ~-5 ~ ~5 ~5 ~ ~-5 minecraft:smooth_basalt replace
fill ~-5 ~1 ~5 ~5 ~1 ~-5 minecraft:smooth_basalt replace
fill ~-5 ~2 ~5 ~5 ~2 ~-5 minecraft:smooth_basalt replace
fill ~-4 ~3 ~4 ~4 ~3 ~-4 minecraft:smooth_basalt replace
fill ~-4 ~4 ~4 ~4 ~4 ~-4 minecraft:smooth_basalt replace
fill ~-3 ~5 ~3 ~3 ~5 ~-3 minecraft:smooth_basalt replace
fill ~-1 ~6 ~1 ~1 ~6 ~-1 minecraft:smooth_basalt replace
fill ~-3 ~-1 ~3 ~3 ~4 ~-3 minecraft:calcite hollow
fill ~-2 ~ ~2 ~2 ~3 ~-2 minecraft:air replace
setblock ~-1 ~1 ~3 divinerpg:bloodgem_ore replace
setblock ~1 ~1 ~-3 minecraft:nether_quartz_ore replace
setblock ~1 ~4 ~1 divinerpg:bloodgem_ore replace
setblock ~-1 ~-1 ~-1 minecraft:nether_quartz_ore replace
setblock ~3 ~2 ~2 divinerpg:bloodgem_ore replace
setblock ~-3 ~2 ~-2 minecraft:nether_quartz_ore replace
setblock ~ ~ ~ minecraft:amethyst_cluster replace
fill ~-5 ~ ~ ~-2 ~1 ~ minecraft:air replace
setblock ~1 ~ ~-2 minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t3/prospector_cache"} replace
setblock ~-8 ~-1 ~ minecraft:cobblestone replace
fill ~-8 ~ ~ ~-8 ~2 ~ minecraft:air replace
tp @s ~-7.5 ~0.1 ~0.5
