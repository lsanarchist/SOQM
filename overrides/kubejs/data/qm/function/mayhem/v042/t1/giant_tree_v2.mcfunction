# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Один саджанець із амбіціями", "color": "gold"}
tellraw @s {"text": "Екологія перемогла твоє планування ділянки.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~6 ~ ~6 ~-6 ~19 ~-6 minecraft:air replace
fill ~3 ~-1 ~3 ~-3 ~-1 ~-3 minecraft:rooted_dirt replace
fill ~1 ~ ~1 ~-1 ~13 ~-1 ecologics:walnut_log replace
fill ~5 ~10 ~5 ~-5 ~11 ~-5 minecraft:oak_leaves[persistent=true] replace
fill ~6 ~12 ~6 ~-6 ~13 ~-6 minecraft:oak_leaves[persistent=true] replace
fill ~5 ~14 ~5 ~-5 ~15 ~-5 minecraft:oak_leaves[persistent=true] replace
fill ~3 ~16 ~3 ~-3 ~17 ~-3 minecraft:oak_leaves[persistent=true] replace
fill ~ ~ ~ ~4 ~ ~ ecologics:walnut_log replace
fill ~ ~ ~ ~-4 ~ ~ ecologics:walnut_log replace
fill ~ ~ ~ ~ ~ ~4 ecologics:walnut_log replace
fill ~ ~ ~ ~ ~ ~-4 ecologics:walnut_log replace
fill ~ ~1 ~1 ~ ~3 ~-1 minecraft:air replace
setblock ~ ~1 ~ minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t1/explorer_cache"} replace
fill ~2 ~ ~1 ~2 ~13 ~1 minecraft:ladder[facing=east] replace
setblock ~ ~-1 ~8 minecraft:cobblestone replace
fill ~ ~ ~8 ~ ~2 ~8 minecraft:air replace
tp @s ~0.5 ~0.1 ~8.5
