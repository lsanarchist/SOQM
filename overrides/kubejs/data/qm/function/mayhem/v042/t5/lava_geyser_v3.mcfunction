# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Центральне опалення", "color": "red"}
tellraw @s {"text": "Термостат залишився в іншій збірці.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-5 ~ ~5 ~5 ~9 ~-5 minecraft:air replace
fill ~-5 ~-2 ~5 ~5 ~-2 ~-5 minecraft:basalt replace
fill ~-4 ~-1 ~4 ~4 ~-1 ~-4 minecraft:air replace
fill ~-3 ~-1 ~3 ~3 ~-1 ~-3 minecraft:magma_block replace
fill ~-3 ~ ~3 ~3 ~ ~-3 minecraft:magma_block replace
fill ~-2 ~1 ~2 ~2 ~1 ~-2 minecraft:magma_block replace
fill ~-2 ~2 ~2 ~2 ~2 ~-2 minecraft:magma_block replace
fill ~-1 ~3 ~1 ~1 ~3 ~-1 minecraft:magma_block replace
fill ~ ~ ~ ~ ~8 ~ minecraft:lava replace
playsound minecraft:entity.generic.explode player @s ~ ~ ~ 0.9 0.7
summon minecraft:magma_cube ~3 ~ ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Size":2}
setblock ~-8 ~-1 ~ minecraft:cobblestone replace
fill ~-8 ~ ~ ~-8 ~2 ~ minecraft:air replace
tp @s ~-7.5 ~0.1 ~0.5
