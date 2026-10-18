#0.4.2: physical structures replace their bounded local footprint.
fill ~-3 ~ ~-3 ~3 ~9 ~3 minecraft:air replace
# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "Вежа мандрівника", "color": "gold", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
fill ~-3 ~-1 ~-3 ~3 ~-1 ~3 minecraft:stone_bricks replace
fill ~-3 ~0 ~-3 ~-3 ~2 ~-3 minecraft:stripped_spruce_log replace
fill ~-3 ~0 ~3 ~-3 ~2 ~3 minecraft:stripped_spruce_log replace
fill ~3 ~0 ~-3 ~3 ~2 ~-3 minecraft:stripped_spruce_log replace
fill ~3 ~0 ~3 ~3 ~2 ~3 minecraft:stripped_spruce_log replace
fill ~-3 ~3 ~-3 ~-3 ~5 ~-3 minecraft:stripped_spruce_log replace
fill ~-3 ~3 ~3 ~-3 ~5 ~3 minecraft:stripped_spruce_log replace
fill ~3 ~3 ~-3 ~3 ~5 ~-3 minecraft:stripped_spruce_log replace
fill ~3 ~3 ~3 ~3 ~5 ~3 minecraft:stripped_spruce_log replace
fill ~-3 ~6 ~-3 ~-3 ~8 ~-3 minecraft:stripped_spruce_log replace
fill ~-3 ~6 ~3 ~-3 ~8 ~3 minecraft:stripped_spruce_log replace
fill ~3 ~6 ~-3 ~3 ~8 ~-3 minecraft:stripped_spruce_log replace
fill ~3 ~6 ~3 ~3 ~8 ~3 minecraft:stripped_spruce_log replace
fill ~-3 ~6 ~-3 ~3 ~6 ~3 minecraft:spruce_planks replace
setblock ~3 ~0 ~-2 minecraft:ladder[facing=north] replace
setblock ~3 ~1 ~-2 minecraft:ladder[facing=north] replace
setblock ~3 ~2 ~-2 minecraft:ladder[facing=north] replace
setblock ~3 ~3 ~-2 minecraft:ladder[facing=north] replace
setblock ~3 ~4 ~-2 minecraft:ladder[facing=north] replace
setblock ~3 ~5 ~-2 minecraft:ladder[facing=north] replace
setblock ~3 ~6 ~-2 minecraft:ladder[facing=north] replace
setblock ~3 ~7 ~-2 minecraft:ladder[facing=north] replace
setblock ~-3 ~9 ~-3 minecraft:lantern replace
setblock ~-3 ~9 ~3 minecraft:lantern replace
setblock ~3 ~9 ~-3 minecraft:lantern replace
setblock ~3 ~9 ~3 minecraft:lantern replace
setblock ~ ~7 ~ minecraft:lantern replace
loot give @s loot qm:mayhem/t1/observatory
tp @s ~0.5 ~0.1 ~0.5
