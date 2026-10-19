execute as @n[type=villager,tag=!trader,tag=dnt_quest_trader] run function nova_structures:add_trade

# The advancement reward runs as the player who traded.
advancement revoke @s only minecraft:give_quest_trader_trade
