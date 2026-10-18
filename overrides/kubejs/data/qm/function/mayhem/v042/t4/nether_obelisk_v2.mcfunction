# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Пекельна антена", "color": "red"}
tellraw @s {"text": "Ловить три канали: вогонь, паніку і ще вогонь.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~5 ~ ~5 ~-5 ~16 ~-5 minecraft:air replace
fill ~4 ~-1 ~4 ~-4 ~-1 ~-4 minecraft:polished_blackstone_bricks replace
fill ~3 ~ ~3 ~-3 ~ ~-3 minecraft:polished_blackstone_bricks replace
fill ~2 ~1 ~2 ~-2 ~1 ~-2 minecraft:polished_blackstone_bricks replace
fill ~1 ~2 ~1 ~-1 ~12 ~-1 minecraft:chiseled_polished_blackstone replace
setblock ~ ~13 ~ minecraft:gilded_blackstone replace
setblock ~3 ~1 ~3 minecraft:soul_campfire replace
setblock ~3 ~1 ~-3 minecraft:soul_campfire replace
setblock ~-3 ~1 ~3 minecraft:soul_campfire replace
setblock ~-3 ~1 ~-3 minecraft:soul_campfire replace
setblock ~ ~2 ~2 minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t4/mage_cache"} replace
summon minecraft:wither_skeleton ~3 ~1 ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"HandItems":[{"id":"minecraft:stone_sword","count":1},{}],"HandDropChances":[0.0,0.0]}
summon minecraft:blaze ~-3 ~2 ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~ ~-1 ~8 minecraft:cobblestone replace
fill ~ ~ ~8 ~ ~2 ~8 minecraft:air replace
tp @s ~0.5 ~0.1 ~8.5
