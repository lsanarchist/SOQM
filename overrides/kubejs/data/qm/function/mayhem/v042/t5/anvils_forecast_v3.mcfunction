# Questing Mayhem Expert0.4.2: authored bounded local event.
title @s actionbar {"text": "Опади з металу", "color": "red"}
tellraw @s {"text": "Прогноз точний. Каска — ні.", "color": "gray"}
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 0.8 0.8
summon minecraft:falling_block ~ ~10 ~2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"BlockState":{"Name":"minecraft:anvil"},"Time":1,"DropItem":false,"HurtEntities":true,"FallHurtAmount":3.0,"FallHurtMax":30}
summon minecraft:falling_block ~ ~14 ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"BlockState":{"Name":"minecraft:anvil"},"Time":1,"DropItem":false,"HurtEntities":true,"FallHurtAmount":3.0,"FallHurtMax":30}
summon minecraft:falling_block ~ ~12 ~-2 {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"BlockState":{"Name":"minecraft:anvil"},"Time":1,"DropItem":false,"HurtEntities":true,"FallHurtAmount":3.0,"FallHurtMax":30}
summon minecraft:falling_block ~2 ~16 ~ {"Tags":["qm_lucky_event"],"PersistenceRequired":false,"CanPickUpLoot":false,"BlockState":{"Name":"minecraft:anvil"},"Time":1,"DropItem":false,"HurtEntities":true,"FallHurtAmount":3.0,"FallHurtMax":30}
loot give @s loot qm:mayhem/t5/components
