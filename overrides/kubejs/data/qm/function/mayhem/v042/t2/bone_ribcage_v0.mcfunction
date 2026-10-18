# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Анатомія невдачі", "color": "light_purple"}
tellraw @s {"text": "Власник цієї кістки теж вважав себе ендгеймом.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-6 ~ ~-6 ~6 ~9 ~6 minecraft:air replace
fill ~-6 ~-1 ~-4 ~6 ~-1 ~4 minecraft:soul_soil replace
fill ~-5 ~ ~ ~5 ~ ~ minecraft:bone_block[axis=x] replace
fill ~-4 ~1 ~-3 ~-4 ~4 ~-3 minecraft:bone_block replace
fill ~-4 ~1 ~3 ~-4 ~4 ~3 minecraft:bone_block replace
setblock ~-4 ~5 ~-2 minecraft:bone_block[axis=z] replace
setblock ~-4 ~5 ~2 minecraft:bone_block[axis=z] replace
setblock ~-4 ~6 ~-1 minecraft:bone_block[axis=z] replace
setblock ~-4 ~6 ~1 minecraft:bone_block[axis=z] replace
fill ~-2 ~1 ~-3 ~-2 ~4 ~-3 minecraft:bone_block replace
fill ~-2 ~1 ~3 ~-2 ~4 ~3 minecraft:bone_block replace
setblock ~-2 ~5 ~-2 minecraft:bone_block[axis=z] replace
setblock ~-2 ~5 ~2 minecraft:bone_block[axis=z] replace
setblock ~-2 ~6 ~-1 minecraft:bone_block[axis=z] replace
setblock ~-2 ~6 ~1 minecraft:bone_block[axis=z] replace
fill ~ ~1 ~-3 ~ ~4 ~-3 minecraft:bone_block replace
fill ~ ~1 ~3 ~ ~4 ~3 minecraft:bone_block replace
setblock ~ ~5 ~-2 minecraft:bone_block[axis=z] replace
setblock ~ ~5 ~2 minecraft:bone_block[axis=z] replace
setblock ~ ~6 ~-1 minecraft:bone_block[axis=z] replace
setblock ~ ~6 ~1 minecraft:bone_block[axis=z] replace
fill ~2 ~1 ~-3 ~2 ~4 ~-3 minecraft:bone_block replace
fill ~2 ~1 ~3 ~2 ~4 ~3 minecraft:bone_block replace
setblock ~2 ~5 ~-2 minecraft:bone_block[axis=z] replace
setblock ~2 ~5 ~2 minecraft:bone_block[axis=z] replace
setblock ~2 ~6 ~-1 minecraft:bone_block[axis=z] replace
setblock ~2 ~6 ~1 minecraft:bone_block[axis=z] replace
fill ~4 ~1 ~-3 ~4 ~4 ~-3 minecraft:bone_block replace
fill ~4 ~1 ~3 ~4 ~4 ~3 minecraft:bone_block replace
setblock ~4 ~5 ~-2 minecraft:bone_block[axis=z] replace
setblock ~4 ~5 ~2 minecraft:bone_block[axis=z] replace
setblock ~4 ~6 ~-1 minecraft:bone_block[axis=z] replace
setblock ~4 ~6 ~1 minecraft:bone_block[axis=z] replace
fill ~5 ~ ~-1 ~6 ~2 ~1 minecraft:bone_block replace
setblock ~6 ~2 ~-1 minecraft:black_concrete replace
setblock ~6 ~2 ~1 minecraft:black_concrete replace
setblock ~-5 ~1 ~ minecraft:chest[facing=north]{"LootTable":"qm:mayhem/t2/mage_cache"} replace
summon minecraft:spider ~-2 ~1 ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon minecraft:spider ~2 ~1 ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~ ~-1 ~-6 minecraft:cobblestone replace
fill ~ ~ ~-6 ~ ~2 ~-6 minecraft:air replace
tp @s ~0.5 ~0.1 ~-5.5
