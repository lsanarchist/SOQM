# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Статична премія", "color": "light_purple"}
tellraw @s {"text": "Шанс виграти є. Шанс стати громовідводом теж.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:lightning_bolt ~5 ~ ~-2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
summon minecraft:lightning_bolt ~-5 ~ ~-2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false}
loot give @s loot qm:mayhem/t5/materials
