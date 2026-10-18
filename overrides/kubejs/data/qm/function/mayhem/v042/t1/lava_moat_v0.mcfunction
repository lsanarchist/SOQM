# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Приватна територія", "color": "red"}
tellraw @s {"text": "Сусіди тепер заходять рідше. Пошта теж.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-6 ~ ~-6 ~6 ~4 ~6 minecraft:air replace
fill ~-6 ~-2 ~-6 ~6 ~-2 ~6 minecraft:polished_blackstone replace
fill ~-5 ~-1 ~-5 ~5 ~-1 ~5 minecraft:lava replace
fill ~-2 ~-1 ~-2 ~2 ~-1 ~2 minecraft:stone_bricks replace
fill ~ ~-1 ~-6 ~ ~-1 ~-3 minecraft:crimson_planks replace
setblock ~1 ~ ~1 minecraft:chest[facing=north]{"LootTable":"qm:mayhem/t1/mage_cache"} replace
summon minecraft:magma_cube ~-1 ~ ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"Size":1}
setblock ~ ~-1 ~ minecraft:cobblestone replace
fill ~ ~ ~ ~ ~2 ~ minecraft:air replace
tp @s ~0.5 ~0.1 ~0.5
