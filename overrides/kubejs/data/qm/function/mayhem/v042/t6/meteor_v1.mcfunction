# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Посилка з орбіти", "color": "light_purple"}
tellraw @s {"text": "Кур’єр помилився тільки зі швидкістю.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~6 ~ ~-6 ~-6 ~7 ~6 minecraft:air replace
fill ~6 ~ ~-6 ~-6 ~1 ~6 minecraft:air replace
fill ~5 ~-1 ~-5 ~-5 ~ ~5 minecraft:air replace
fill ~4 ~-2 ~-4 ~-4 ~-1 ~4 minecraft:air replace
fill ~3 ~-3 ~-3 ~-3 ~-2 ~3 minecraft:air replace
fill ~2 ~-4 ~-2 ~-2 ~-3 ~2 minecraft:air replace
fill ~3 ~-5 ~-3 ~-3 ~-5 ~3 minecraft:blackstone replace
setblock ~4 ~-1 ~-5 minecraft:magma_block replace
setblock ~4 ~ ~-5 minecraft:fire replace
setblock ~5 ~-1 ~4 minecraft:magma_block replace
setblock ~5 ~ ~4 minecraft:fire replace
setblock ~-3 ~-1 ~5 minecraft:magma_block replace
setblock ~-3 ~ ~5 minecraft:fire replace
setblock ~-5 ~-1 ~-3 minecraft:magma_block replace
setblock ~-5 ~ ~-3 minecraft:fire replace
fill ~1 ~-4 ~-1 ~-1 ~-4 ~1 minecraft:basalt replace
fill ~2 ~-3 ~-2 ~-2 ~-3 ~2 minecraft:basalt replace
fill ~3 ~-2 ~-3 ~-3 ~-2 ~3 minecraft:basalt replace
fill ~3 ~-1 ~-3 ~-3 ~-1 ~3 minecraft:basalt replace
fill ~2 ~ ~-2 ~-2 ~ ~2 minecraft:basalt replace
fill ~1 ~1 ~-1 ~-1 ~1 ~1 minecraft:basalt replace
fill ~1 ~-3 ~-1 ~-1 ~-2 ~1 ad_astra:venus_calorite_ore replace
setblock ~ ~2 ~ minecraft:magma_block replace
particle minecraft:explosion_emitter ~ ~ ~ 0 0 0 0 1 normal
playsound minecraft:entity.generic.explode player @s ~ ~ ~ 1 0.7
setblock ~8 ~-1 ~ minecraft:cobblestone replace
fill ~8 ~ ~ ~8 ~2 ~ minecraft:air replace
tp @s ~8.5 ~0.1 ~0.5
