# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Липка пропозиція", "color": "light_purple"}
tellraw @s {"text": "Від цієї угоди так просто не відійти.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~4 ~ ~4 ~-4 ~7 ~-4 minecraft:air replace
fill ~4 ~-1 ~4 ~-4 ~-1 ~-4 minecraft:honeycomb_block replace
fill ~3 ~ ~3 ~-3 ~ ~-3 minecraft:honey_block replace
fill ~3 ~1 ~3 ~3 ~4 ~-3 minecraft:honeycomb_block replace
fill ~-3 ~1 ~3 ~-3 ~4 ~-3 minecraft:honeycomb_block replace
fill ~3 ~5 ~3 ~-3 ~5 ~-3 minecraft:yellow_stained_glass replace
setblock ~ ~1 ~-2 minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t0/explorer_cache"} replace
summon minecraft:bee ~2 ~2 ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon minecraft:bee ~-2 ~2 ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
effect give @s minecraft:slowness 12 1 true
setblock ~ ~ ~ minecraft:cobblestone replace
fill ~ ~1 ~ ~ ~3 ~ minecraft:air replace
tp @s ~0.5 ~1.1 ~0.5
