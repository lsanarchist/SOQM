# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Анатомія невдачі", "color": "light_purple"}
tellraw @s {"text": "Власник цієї кістки теж вважав себе ендгеймом.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~6 ~ ~-6 ~-6 ~9 ~6 minecraft:air replace
fill ~4 ~-1 ~-6 ~-4 ~-1 ~6 minecraft:soul_soil replace
fill ~ ~ ~-5 ~ ~ ~5 minecraft:bone_block[axis=z] replace
fill ~3 ~1 ~-4 ~3 ~4 ~-4 minecraft:bone_block replace
fill ~-3 ~1 ~-4 ~-3 ~4 ~-4 minecraft:bone_block replace
setblock ~2 ~5 ~-4 minecraft:bone_block[axis=x] replace
setblock ~-2 ~5 ~-4 minecraft:bone_block[axis=x] replace
setblock ~1 ~6 ~-4 minecraft:bone_block[axis=x] replace
setblock ~-1 ~6 ~-4 minecraft:bone_block[axis=x] replace
fill ~3 ~1 ~-2 ~3 ~4 ~-2 minecraft:bone_block replace
fill ~-3 ~1 ~-2 ~-3 ~4 ~-2 minecraft:bone_block replace
setblock ~2 ~5 ~-2 minecraft:bone_block[axis=x] replace
setblock ~-2 ~5 ~-2 minecraft:bone_block[axis=x] replace
setblock ~1 ~6 ~-2 minecraft:bone_block[axis=x] replace
setblock ~-1 ~6 ~-2 minecraft:bone_block[axis=x] replace
fill ~3 ~1 ~ ~3 ~4 ~ minecraft:bone_block replace
fill ~-3 ~1 ~ ~-3 ~4 ~ minecraft:bone_block replace
setblock ~2 ~5 ~ minecraft:bone_block[axis=x] replace
setblock ~-2 ~5 ~ minecraft:bone_block[axis=x] replace
setblock ~1 ~6 ~ minecraft:bone_block[axis=x] replace
setblock ~-1 ~6 ~ minecraft:bone_block[axis=x] replace
fill ~3 ~1 ~2 ~3 ~4 ~2 minecraft:bone_block replace
fill ~-3 ~1 ~2 ~-3 ~4 ~2 minecraft:bone_block replace
setblock ~2 ~5 ~2 minecraft:bone_block[axis=x] replace
setblock ~-2 ~5 ~2 minecraft:bone_block[axis=x] replace
setblock ~1 ~6 ~2 minecraft:bone_block[axis=x] replace
setblock ~-1 ~6 ~2 minecraft:bone_block[axis=x] replace
fill ~3 ~1 ~4 ~3 ~4 ~4 minecraft:bone_block replace
fill ~-3 ~1 ~4 ~-3 ~4 ~4 minecraft:bone_block replace
setblock ~2 ~5 ~4 minecraft:bone_block[axis=x] replace
setblock ~-2 ~5 ~4 minecraft:bone_block[axis=x] replace
setblock ~1 ~6 ~4 minecraft:bone_block[axis=x] replace
setblock ~-1 ~6 ~4 minecraft:bone_block[axis=x] replace
fill ~1 ~ ~5 ~-1 ~2 ~6 minecraft:bone_block replace
setblock ~1 ~2 ~6 minecraft:black_concrete replace
setblock ~-1 ~2 ~6 minecraft:black_concrete replace
setblock ~ ~1 ~-5 minecraft:chest[facing=east]{"LootTable":"qm:mayhem/t4/mage_cache"} replace
summon minecraft:spider ~-1 ~1 ~-2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon minecraft:spider ~-1 ~1 ~2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~6 ~-1 ~ minecraft:cobblestone replace
fill ~6 ~ ~ ~6 ~2 ~ minecraft:air replace
tp @s ~6.5 ~0.1 ~0.5
