# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Амбулаторія сюрпризів", "color": "red"}
tellraw @s {"text": "Лікарі точно щось змішали. Тобі теж дістанеться.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:witch ~ ~ ~-4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon minecraft:witch ~ ~ ~4 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
effect give @s minecraft:regeneration 12 0 true
effect give @s minecraft:nausea 10 0 true
loot give @s loot qm:mayhem/t4/book
