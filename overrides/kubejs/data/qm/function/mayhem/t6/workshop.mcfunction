#0.4.2: physical structures replace their bounded local footprint.
fill ~-3 ~ ~-3 ~3 ~5 ~3 minecraft:air replace
# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "Майстерня з нізвідки", "color": "gold", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
fill ~-3 ~-1 ~-3 ~3 ~-1 ~3 minecraft:polished_blackstone_bricks replace
fill ~-3 ~ ~-3 ~-3 ~3 ~-3 minecraft:gilded_blackstone replace
fill ~-3 ~ ~3 ~-3 ~3 ~3 minecraft:gilded_blackstone replace
fill ~3 ~ ~-3 ~3 ~3 ~-3 minecraft:gilded_blackstone replace
fill ~3 ~ ~3 ~3 ~3 ~3 minecraft:gilded_blackstone replace
fill ~-3 ~4 ~-3 ~3 ~4 ~3 minecraft:yellow_concrete replace
setblock ~-2 ~ ~2 minecraft:crafting_table replace
setblock ~2 ~ ~2 minecraft:furnace[facing=north] replace
setblock ~-2 ~ ~-2 minecraft:lantern replace
setblock ~2 ~ ~-2 minecraft:lantern replace
setblock ~1 ~ ~2 minecraft:anvil replace
loot give @s loot qm:mayhem/t6/workshop
tp @s ~0.5 ~0.1 ~0.5
