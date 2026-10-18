#0.4.2: physical structures replace their bounded local footprint.
fill ~-5 ~ ~-5 ~5 ~5 ~5 minecraft:air replace
# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "Виклик вартового", "color": "red", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
fill ~-5 ~-1 ~-5 ~5 ~-1 ~5 minecraft:polished_blackstone_bricks replace
fill ~-5 ~ ~-5 ~-5 ~2 ~-5 minecraft:gilded_blackstone replace
setblock ~-5 ~3 ~-5 minecraft:lantern replace
fill ~-5 ~ ~5 ~-5 ~2 ~5 minecraft:gilded_blackstone replace
setblock ~-5 ~3 ~5 minecraft:lantern replace
fill ~5 ~ ~-5 ~5 ~2 ~-5 minecraft:gilded_blackstone replace
setblock ~5 ~3 ~-5 minecraft:lantern replace
fill ~5 ~ ~5 ~5 ~2 ~5 minecraft:gilded_blackstone replace
setblock ~5 ~3 ~5 minecraft:lantern replace
qm_luck_guardian 6
tellraw @s {"text": "Переможи вартового за 10 хвилин — його особистий скарб випаде лише один раз.", "color": "yellow"}
tp @s ~0.5 ~0.1 ~0.5
