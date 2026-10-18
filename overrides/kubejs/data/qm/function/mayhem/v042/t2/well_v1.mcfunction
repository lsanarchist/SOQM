# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Криниця побажань", "color": "gold"}
tellraw @s {"text": "Твоє бажання почули на мінус восьмому поверсі.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~3 ~-8 ~-3 ~-3 ~5 ~3 minecraft:air replace
fill ~2 ~-8 ~-2 ~-2 ~ ~2 minecraft:mossy_cobblestone hollow
fill ~1 ~-6 ~-1 ~-1 ~-2 ~1 minecraft:water replace
fill ~1 ~-1 ~-1 ~-1 ~1 ~1 minecraft:air replace
fill ~2 ~1 ~-2 ~2 ~3 ~-2 minecraft:oak_fence replace
fill ~-2 ~1 ~-2 ~-2 ~3 ~-2 minecraft:oak_fence replace
fill ~2 ~1 ~2 ~2 ~3 ~2 minecraft:oak_fence replace
fill ~-2 ~1 ~2 ~-2 ~3 ~2 minecraft:oak_fence replace
fill ~3 ~4 ~-3 ~-3 ~4 ~3 minecraft:spruce_slab replace
setblock ~ ~-7 ~ minecraft:chest[facing=east]{"LootTable":"qm:mayhem/t2/explorer_cache"} replace
fill ~-1 ~-7 ~ ~-1 ~1 ~ minecraft:ladder[facing=east] replace
setblock ~4 ~-1 ~ minecraft:cobblestone replace
fill ~4 ~ ~ ~4 ~2 ~ minecraft:air replace
tp @s ~4.5 ~0.1 ~0.5
