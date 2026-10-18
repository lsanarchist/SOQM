# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Квартира-студія", "color": "red"}
tellraw @s {"text": "Вікна є. Двері — факультатив.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-4 ~ ~4 ~4 ~5 ~-4 minecraft:air replace
fill ~-3 ~-1 ~3 ~3 ~-1 ~-3 minecraft:mossy_stone_bricks replace
fill ~-3 ~ ~3 ~3 ~3 ~-3 minecraft:iron_bars hollow
fill ~-3 ~4 ~3 ~3 ~4 ~-3 minecraft:mossy_stone_bricks replace
setblock ~2 ~ ~-2 minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t6/gear_cache"} replace
summon cannibalvillages:canibal ~2 ~ ~2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~ ~-1 ~ minecraft:cobblestone replace
fill ~ ~ ~ ~ ~2 ~ minecraft:air replace
tp @s ~0.5 ~0.1 ~0.5
