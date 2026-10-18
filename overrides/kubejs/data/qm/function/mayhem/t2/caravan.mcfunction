#0.4.2: physical structures replace their bounded local footprint.
fill ~-3 ~ ~-3 ~3 ~5 ~3 minecraft:air replace
# Questing Mayhem: authored for Minecraft 1.21.1.
title @s actionbar {"text": "Крамар між світами", "color": "gold", "bold": false}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.7 1.0
fill ~-2 ~-1 ~-2 ~2 ~-1 ~2 minecraft:deepslate_bricks replace
fill ~-2 ~ ~-2 ~-2 ~2 ~-2 minecraft:oak_fence replace
fill ~-2 ~ ~2 ~-2 ~2 ~2 minecraft:oak_fence replace
fill ~2 ~ ~-2 ~2 ~2 ~-2 minecraft:oak_fence replace
fill ~2 ~ ~2 ~2 ~2 ~2 minecraft:oak_fence replace
fill ~-2 ~3 ~-2 ~2 ~3 ~2 minecraft:cyan_wool replace
summon minecraft:wandering_trader ~ ~ ~ {"CustomName":"{\"text\": \"Крамар між світами\", \"color\": \"gold\"}","NoAI":true,"DespawnDelay":6000,"Offers":{"Recipes":[{"buy":{"id":"minecraft:emerald","count":10},"sell":{"id":"artifacts:digging_claws","count":1},"maxUses":1,"uses":0,"rewardExp":false,"priceMultiplier":0.0},{"buy":{"id":"minecraft:emerald","count":5},"sell":{"id":"minecraft:experience_bottle","count":8},"maxUses":1,"uses":0,"rewardExp":false,"priceMultiplier":0.0},{"buy":{"id":"minecraft:emerald","count":6},"sell":{"id":"minecraft:golden_apple","count":3},"maxUses":1,"uses":0,"rewardExp":false,"priceMultiplier":0.0}]}}
tp @s ~0.5 ~0.1 ~0.5
