# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Підвищення без зарплати", "color": "light_purple"}
tellraw @s {"text": "Ти тепер вище проблем. Приблизно на дванадцять блоків.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~3 ~12 ~3 ~-3 ~12 ~-3 minecraft:quartz_bricks replace
fill ~3 ~13 ~3 ~-3 ~16 ~-3 minecraft:air replace
fill ~3 ~13 ~3 ~3 ~16 ~3 minecraft:quartz_pillar replace
fill ~3 ~13 ~-3 ~3 ~16 ~-3 minecraft:quartz_pillar replace
fill ~-3 ~13 ~3 ~-3 ~16 ~3 minecraft:quartz_pillar replace
fill ~-3 ~13 ~-3 ~-3 ~16 ~-3 minecraft:quartz_pillar replace
setblock ~ ~17 ~ minecraft:sea_lantern replace
setblock ~-2 ~13 ~-2 minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t4/mage_cache"} replace
effect give @s minecraft:slow_falling 45 0 true
setblock ~ ~12 ~ minecraft:cobblestone replace
fill ~ ~13 ~ ~ ~15 ~ minecraft:air replace
tp @s ~0.5 ~13.1 ~0.5
