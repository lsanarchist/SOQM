# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Вертикальний гуртожиток", "color": "light_purple"}
tellraw @s {"text": "Сходи безкоштовні. Кардіолог у комплект не входить.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~4 ~ ~4 ~-4 ~16 ~-4 minecraft:air replace
fill ~3 ~-1 ~3 ~-3 ~14 ~-3 minecraft:polished_tuff hollow
fill ~2 ~3 ~2 ~-2 ~3 ~-2 ecologics:walnut_planks replace
setblock ~ ~3 ~-2 minecraft:air replace
fill ~3 ~4 ~1 ~3 ~5 ~-1 minecraft:iron_bars replace
fill ~-3 ~4 ~1 ~-3 ~5 ~-1 minecraft:iron_bars replace
fill ~1 ~4 ~3 ~-1 ~5 ~3 minecraft:iron_bars replace
fill ~2 ~7 ~2 ~-2 ~7 ~-2 ecologics:walnut_planks replace
setblock ~ ~7 ~-2 minecraft:air replace
fill ~3 ~8 ~1 ~3 ~9 ~-1 minecraft:iron_bars replace
fill ~-3 ~8 ~1 ~-3 ~9 ~-1 minecraft:iron_bars replace
fill ~1 ~8 ~3 ~-1 ~9 ~3 minecraft:iron_bars replace
fill ~2 ~11 ~2 ~-2 ~11 ~-2 ecologics:walnut_planks replace
setblock ~ ~11 ~-2 minecraft:air replace
fill ~3 ~12 ~1 ~3 ~13 ~-1 minecraft:iron_bars replace
fill ~-3 ~12 ~1 ~-3 ~13 ~-1 minecraft:iron_bars replace
fill ~1 ~12 ~3 ~-1 ~13 ~3 minecraft:iron_bars replace
fill ~ ~ ~-2 ~ ~14 ~-2 minecraft:ladder[facing=south] replace
fill ~ ~ ~3 ~ ~1 ~3 minecraft:air replace
fill ~4 ~14 ~4 ~-4 ~14 ~-4 ecologics:walnut_planks replace
setblock ~ ~14 ~-2 minecraft:ladder[facing=south] replace
fill ~4 ~15 ~4 ~-4 ~15 ~-4 minecraft:oak_fence outline
setblock ~-2 ~15 ~-2 minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t3/gear_cache"} replace
summon minecraft:skeleton ~1 ~12 ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:bow","count":1},{}],"HandDropChances":[0.0,0.0]}
setblock ~ ~-1 ~5 minecraft:cobblestone replace
fill ~ ~ ~5 ~ ~2 ~5 minecraft:air replace
tp @s ~0.5 ~0.1 ~5.5
