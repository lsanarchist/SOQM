# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Митниця просто неба", "color": "red"}
tellraw @s {"text": "Документи? Ресурси? Запасне обличчя?", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
fill ~6 ~ ~6 ~-6 ~5 ~-6 minecraft:air replace
fill ~6 ~-1 ~3 ~-6 ~-1 ~-3 minecraft:coarse_dirt replace
fill ~6 ~ ~-1 ~6 ~2 ~-1 ecologics:walnut_log replace
fill ~5 ~ ~-1 ~5 ~3 ~-1 ecologics:walnut_log replace
fill ~4 ~ ~-1 ~4 ~2 ~-1 ecologics:walnut_log replace
fill ~3 ~ ~-1 ~3 ~3 ~-1 ecologics:walnut_log replace
fill ~2 ~ ~-1 ~2 ~2 ~-1 ecologics:walnut_log replace
setblock ~1 ~ ~-1 minecraft:spruce_fence_gate[facing=south] replace
setblock ~ ~ ~-1 minecraft:spruce_fence_gate[facing=south] replace
setblock ~-1 ~ ~-1 minecraft:spruce_fence_gate[facing=south] replace
fill ~-2 ~ ~-1 ~-2 ~2 ~-1 ecologics:walnut_log replace
fill ~-3 ~ ~-1 ~-3 ~3 ~-1 ecologics:walnut_log replace
fill ~-4 ~ ~-1 ~-4 ~2 ~-1 ecologics:walnut_log replace
fill ~-5 ~ ~-1 ~-5 ~3 ~-1 ecologics:walnut_log replace
fill ~-6 ~ ~-1 ~-6 ~2 ~-1 ecologics:walnut_log replace
setblock ~-4 ~ ~1 minecraft:chest[facing=south]{"LootTable":"qm:mayhem/t2/gear_cache"} replace
summon cannibalvillages:canibal ~4 ~ ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon cannibalvillages:canibal ~ ~ ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon cannibalvillages:canibal ~-4 ~ ~1 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
setblock ~ ~-1 ~8 minecraft:cobblestone replace
fill ~ ~ ~8 ~ ~2 ~8 minecraft:air replace
tp @s ~0.5 ~0.1 ~8.5
