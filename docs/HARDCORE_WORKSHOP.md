# Hardcore workshop and Mekanism materials

This policy transfers the resource-heavy initial workshop of Questing Mayhem
1.7.10 into SOQM's installed mods, then makes Mekanism's production and circuit
tiers consume that workshop at scale. It changes 63 recipes and removes 80 exact
native or earlier pack recipe IDs. Food recipes and hunger settings are outside
this change.

The authoritative, machine-readable recipe payloads, source IDs and native
serializer evidence are in [`metadata/hardcore_workshop.json`](../metadata/hardcore_workshop.json).
Run `python3 scripts/hardcore_workshop.py` after editing that policy. The generated
server script has priority `-100`, so these explicit replacements follow the
priority-zero expert recipes. Pinned KubeJS only applies `event.remove({id})`
to native original recipes. Earlier scripted additions live in a separate
queue, so the policy also explicitly removes its 11 superseded authored recipes
from `event.addedRecipes` through native `KubeRecipe.remove()`. The policy uses the mod versions and SHA-256 pins
in `metadata/mods.lock.json`; no additional mod is needed.

## Initial workshop

All quantities in this table describe one output unless a batch is stated.
Metal blocks contain nine ingots.

| Output | New recipe or material cost | Reason |
| --- | --- | --- |
| Vanilla Furnace | 8 stone crafting materials around a vanilla Chest | Restores the old chest-in-the-furnace workshop cost. |
| Copper Furnace | 8 copper blocks + Furnace | 72 copper establishes the first upgraded furnace as a substantial purchase. |
| Iron Furnace | 8 iron blocks + Furnace, or 8 iron blocks + Copper Furnace | Every path pays 72 iron. The optional copper path retains its earlier copper investment. |
| Furnace upgrade items | 8 copper/iron blocks + cobblestone for Stone-to-Copper/Stone-to-Iron; 8 iron blocks + copper ingot for Copper-to-Iron | In-world upgrades pay the same metal-block cost and use distinct grids. |
| Iron/copper/gold/steel plate, manual | 2 corresponding ingots + Engineer's Hammer → 1 plate | The native hammer remainder is retained. The powered Metal Press still uses 1 ingot per plate. |
| Wooden Gear | 4 sticks + wooden button | A manual centre part, using available BuildCraft items. |
| Stone Gear | 4 smelted stone + Wooden Gear | Smelting stone is part of the mechanical workshop. |
| Iron Gear | 4 iron plates + Stone Gear | Applied to BuildCraft, Railcraft, Ender IO and Industrial Foregoing variants. |
| Copper Gear | 4 copper plates + Stone Gear | Railcraft supplies the installed copper gear used by the piston. |
| Gold Gear | 4 gold plates + BuildCraft Iron Gear | Applied to BuildCraft, Railcraft and Industrial Foregoing variants. |
| Diamond Gear | 4 diamonds + Gold Gear | Retains the complete previous-tier gear chain. |
| Piston | 3 wooden slabs + 4 stone crafting materials + IE iron rod + Railcraft Copper Gear | Manual rods and hammering are available before a piston or powered press is needed. |
| Hopper | 5 iron ingots + vanilla Chest + unused Basic-to-Iron Tier Upgrade | Pays for industrial storage materials while avoiding consumption of a populated storage item. |

A manually made Copper Gear costs eight copper ingots in its four plates,
before its Stone Gear and Wooden Gear. A native IE iron-rod craft produces four
rods from two iron ingots. Thus the piston now consumes a mechanical workshop;
its iron rod does not require the machine that the piston will help build.

Since 0.4.16, each mod's iron and gold gear has a distinct manual layout. Each
still consumes four corresponding plates and the same previous-tier BuildCraft
gear. BuildCraft retains the cross layout; use JEI for the other exact outputs.
The layouts also remain distinct when mirrored horizontally. The Copper-to-Iron
Furnace Upgrade uses a copper ingot in its centre, while Stone-to-Iron uses
cobblestone. Both still consume eight iron blocks. Existing autocrafting
patterns for the changed layouts need to be recorded again.

Native gear/plate manufacturing remains available through the powered IE Metal
Press and ModernFoundry's fuel-and-cast Smeltery. Smeltery casting does not
require FE; it is an alternative production branch before the powered press.
The eight-ingot Copper Gear figure above describes the manual hammer-and-grid
route, not the cheapest cost across every production process. Building a
production workshop therefore has a practical benefit when machines,
conveyors, storage and autocrafting begin consuming parts in quantity.

The recipe audit also removed `aether:skyroot_piston` and
`terralith:piston_alt`. Replacing only `minecraft:piston` would leave those
cheaper recipes available. All installed direct-output manual copper, iron and
gold gear alternatives were inspected, including the Industrial Foregoing
variants.

## Storage costs and contents

Sophisticated Storage provides the installed equivalent of old QM's Copper and
Iron Chests. The policy covers Chest, Barrel, all four Limited Barrel shapes and
Shulker Box, including both direct and previous-tier recipes. It also covers all
three native double-chest tier recipes.

| Route | Metal added by the operation | Total metal represented by the resulting storage |
| --- | --- | --- |
| Basic single → Copper single | 72 copper | 72 copper |
| Copper single → Iron single | 72 iron | 72 copper + 72 iron |
| Basic single → Iron single | One complete Basic-to-Iron token | 72 copper + 72 iron |
| Basic double → Copper double | Two complete Basic-to-Copper tokens | 144 copper |
| Copper double → Iron double | Two complete Copper-to-Iron tokens | 144 copper + 144 iron |
| Basic double → Iron double | Two complete Basic-to-Iron tokens | 144 copper + 144 iron |

The token recipes are:

- **Basic-to-Copper:** eight copper blocks and a lever.
- **Copper-to-Iron:** eight iron blocks and a lever.
- **Basic-to-Iron:** eight iron blocks and a Basic-to-Copper token.

The old cheap `basic_to_iron_tier_from_basic_to_copper_tier` alias is removed.
Higher direct tokens already contain the lower-tier tokens, so they inherit the
new copper/iron bill. Metal parity is exact between direct, sequential and native
in-world upgrade paths. Token-based routes can additionally use a lever; the
table does not treat that small handle cost as a metal cost.

The hopper's unused Basic-to-Iron token therefore represents 72 copper and 72
iron, on top of its five loose iron ingots, wooden chest and token handle. It is
deliberately an item that cannot contain storage inventory.

The replacement recipes retain the native
`sophisticatedstorage:storage_tier_upgrade` and
`sophisticatedstorage:double_chest_tier_upgrade` serializers, native output
stacks and enabled-item conditions. These serializers transfer the original
storage components and contents identity, then update capacity to the new tier.
Using ordinary shaped recipes for these outputs would lose that behavior.

Same-tier Copper Chest → Copper Shulker and Iron Chest → Iron Shulker conversions
remain native: they already consume a paid storage tier and preserve its data.
The native runtime fixture checks packed wooden variants, names, colours,
locks, contents UUIDs, stored items, installed upgrades and single/double
capacities. It also rejects using a double chest in a single-price recipe.

## Mekanism costs

The first Steel Casing and Metallurgic Infuser consume IE steel and the workshop,
but neither depends on Infused Alloy, a control circuit or a precision mechanism.

| Output | New ingredients or change |
| --- | --- |
| Steel Casing | 4 steel blocks + 2 IE steel plates + Industrial Chassis + osmium block + cheap glass |
| Metallurgic Infuser | 2 iron blocks + 2 Iron Furnaces + 2 redstone blocks + Industrial Chassis + osmium block + Steel Casing |
| Enrichment Chamber | Existing drive, chassis, casing, basic circuits and basic alloy remain; its two iron-ingot slots become iron blocks. |
| Crusher | Existing drive, chassis, casing, basic circuits and lava remain; its two redstone slots become redstone blocks. |
| Precision Sawmill | Existing components remain; its two iron-ingot slots become iron blocks. |
| Electrolytic Separator | Existing sealed chamber, control assembly, infused alloy and Electrolytic Core remain; its two iron and two redstone slots become blocks. |
| Chemical Infuser | Its two Basic Chemical Tanks become Advanced Chemical Tanks. Native `mekanism:mek_data` remains. |
| Rotary Condensentrator | Its Basic Chemical Tank and Basic Fluid Tank become their Advanced variants. Native `mekanism:mek_data` remains. |

The two Iron Furnaces and two iron blocks in one Metallurgic Infuser already
represent **162 iron ingots**, before counting its chassis and steel materials.
The Steel Casing itself contains 36 steel in blocks, two more plates and a
chassis. Those costs intentionally make expanding production a meaningful
material commitment.

The two advanced tank types require infused alloys and their previous tank,
not a Chemical Infuser or Rotary Condensentrator. They can therefore be prepared
with the first Metallurgic Infuser. The native Infused Alloy operation is
**copper plus redstone infusion** in the pinned Mekanism version. This policy
does not alter that operation.

### Circuits and modules

The earlier expert recipes produced two next-tier circuits from two previous-tier
circuits. These recipes now produce **one**. This makes each next tier consume
two previous circuits before accounting for cross-mod components.

| Output | Inputs for one output |
| --- | --- |
| Advanced Control Circuit | 2 Basic Control Circuits + 4 Infused Alloys + Precision Mechanism + Control Assembly + redstone block |
| Elite Control Circuit | 2 Advanced Control Circuits + 4 Reinforced Alloys + Dimensional Matrix + Quantum Processor + diamond block |
| Ultimate Control Circuit | 2 Elite Control Circuits + 4 Atomic Alloys + Stellar Alloy + Radiation Shield + Quantum Processor |
| Module Base | 2 HDPE Sheets + 2 tin blocks + 2 bronze ingots + 2 IE steel plates + Elite Control Circuit |

The Basic Control Circuit infusion recipe remains native. All three native
crafting alternatives and all three cheap native circuit-infusion alternatives
stay removed. Circuit dependencies only descend to the previous tier and the
existing cross-mod production branches. The Module Base has no requirement for
another module, modification station, MekaSuit piece or nuclear fuel product.
Its elite circuit makes it a later workshop component; quest guidance must
teach that dependency explicitly.

These recipes also give HDPE ongoing demand through installed modules. The
different plastic chains remain distinct: Mekanism HDPE serves its polymer and
equipment recipes, while Industrial Foregoing Plastic serves IF production.

## Bootstrap and verification

The maintained dependency route is:

1. Basic resources and wooden storage supply the new Furnace, manual hammer,
   rods, plates and gears.
2. The native Coke Oven supplies coke and creosote. The native Blast Furnace
   supplies steel before the first Mekanism machine.
3. IE manual components and treated wood supply Industrial Chassis. The piston
   has already become available without a powered plate press.
4. The expensive Iron Furnaces, Steel Casing and chassis supply the first
   Metallurgic Infuser. A Heat Generator uses the same previous workshop.
5. Basic circuits and infused alloys unlock Enrichment, advanced tank preparation
   and subsequent Mekanism production. The early AE2 Inscriber is also reachable
   from this workshop.

`tests/test_hardcore_workshop.py` verifies the active replacement order, exact
metal costs across storage alternatives, manual-versus-powered plate costs,
positive first-workshop reachability and absence of circuit/module dependency
cycles. A small snapshot of pinned native recipes keeps those checks available
without downloads. When the complete pinned mod cache is present, the tests
also verify native recipe hashes and inspect every direct-output recipe source
for a cheaper alternative. Tag-output plate/gear processes are explicitly
reviewed and limited to their native Metal Press and Smeltery serializers.
The original audit covered 128 archives, 21,234
recipe files and 21,202 unique native recipe IDs.

`tests/runtime/HardcoreWorkshopProbe.java` is a separate test-only fixture for the
full pinned server. It uses native recipe matching and assembly for every new
recipe, checks that removed IDs are absent, exercises every storage serializer,
checks the hammer remainder and rejects the obsolete cheap grids. These tests
supply ingredients; they do not claim to measure a survival playthrough's time,
resource scarcity, mining rate or late-factory throughput.

Focused authoring checks:

```sh
python3 scripts/hardcore_workshop.py --check
python3 -m unittest discover -s tests -p 'test_hardcore_workshop.py' -v
node --check overrides/kubejs/server_scripts/qm_expert_hardcore_workshop.js
python3 scripts/recipe_collisions.py
python3 -m unittest discover -s tests -p 'test_recipe_collisions.py' -v
```

The recipe-choice regression checks all supported authored shaped grids for
different outputs accepting the same ingredients, including horizontal mirrors
and overlapping tags. It also verifies the nine formerly ambiguous gear/upgrade
outputs against the whole supported authored catalogue. The native server
fixture now checks these grids through the full `RecipeManager`, all competing
crafting recipes and real ingredient alternatives. See the
[conflict fix and verification scope](RECIPE_CONFLICT_FIX.md).

Campaign recipe analysis must honor script priorities and explicit
`removeAddedIds` before calculating active authored alternatives. The ordinary
`removeIds` array alone does not remove a queued authored recipe in this pin.
Crafting-refund evidence and tutorials
must reference the new `kubejs:hardcore_workshop/` IDs and the new batch sizes.
Chemical Infuser refunds also need to reflect its Advanced Chemical Tanks.

## Legacy references

The user-provided `FULL_CONTEXT_FOR_ASTRA.txt` contains the legacy recipe source
used for this adaptation: `Recipe 1.zs:286–289` for manual gear stages,
`:312` for Furnace, `:317` for Iron Furnace, `:372` for Hopper, `:381–382` for
Iron/Copper Chest, and `Recipe 4.zs:327` for Piston. Installed Sophisticated
Storage and Railcraft items replace old mod IDs while retaining the relevant
workshop costs and native stateful behavior.
