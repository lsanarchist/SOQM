# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Митниця просто неба", "color": "red"}
tellraw @s {"text": "Документи? Ресурси? Запасне обличчя?", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~-6 ~ ~6 ~6 ~5 ~-6 minecraft:air replace
fill ~-3 ~-1 ~6 ~3 ~-1 ~-6 minecraft:coarse_dirt replace
fill ~1 ~ ~6 ~1 ~2 ~6 ecologics:azalea_log replace
fill ~1 ~ ~5 ~1 ~3 ~5 ecologics:azalea_log replace
fill ~1 ~ ~4 ~1 ~2 ~4 ecologics:azalea_log replace
fill ~1 ~ ~3 ~1 ~3 ~3 ecologics:azalea_log replace
fill ~1 ~ ~2 ~1 ~2 ~2 ecologics:azalea_log replace
setblock ~1 ~ ~1 minecraft:spruce_fence_gate[facing=west] replace
setblock ~1 ~ ~ minecraft:spruce_fence_gate[facing=west] replace
setblock ~1 ~ ~-1 minecraft:spruce_fence_gate[facing=west] replace
fill ~1 ~ ~-2 ~1 ~2 ~-2 ecologics:azalea_log replace
fill ~1 ~ ~-3 ~1 ~3 ~-3 ecologics:azalea_log replace
fill ~1 ~ ~-4 ~1 ~2 ~-4 ecologics:azalea_log replace
fill ~1 ~ ~-5 ~1 ~3 ~-5 ecologics:azalea_log replace
fill ~1 ~ ~-6 ~1 ~2 ~-6 ecologics:azalea_log replace
setblock ~-1 ~ ~-4 minecraft:chest[facing=west]{"LootTable":"qm:mayhem/t3/gear_cache"} replace
summon cannibalvillages:canibal ~-1 ~ ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon cannibalvillages:canibal ~-1 ~ ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon cannibalvillages:canibal ~-1 ~ ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~-8 ~-1 ~ minecraft:cobblestone replace
fill ~-8 ~ ~ ~-8 ~2 ~ minecraft:air replace
tp @s ~-7.5 ~0.1 ~0.5
