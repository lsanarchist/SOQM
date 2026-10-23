# Workshop recipe conflicts in SOQM 0.4.16

SOQM 0.4.16 corrects three groups of overlapping manual recipes. The previous
ordinary shaped recipes each matched their own supplied inputs, but multiple
different outputs matched the same grid. Checking recipes only by ID therefore
missed the defect.

## Cause and changes

| Previous identical inputs | Competing outputs | Correction |
| --- | --- | --- |
| Four iron plates in a cross around a BuildCraft Stone Gear | BuildCraft, Ender IO, Industrial Foregoing and Railcraft iron gears | Give the four outputs distinct layouts. |
| Four gold plates in a cross around a BuildCraft Iron Gear | BuildCraft, Industrial Foregoing and Railcraft gold gears | Give the three outputs distinct layouts. |
| Eight iron blocks around cobblestone | Stone-to-Iron and Copper-to-Iron Furnace Upgrade | Restore the copper-ingot centre for Copper-to-Iron. |

The authoritative changes are in
[`metadata/hardcore_workshop.json`](../metadata/hardcore_workshop.json), rendered
into `overrides/kubejs/server_scripts/qm_expert_hardcore_workshop.js` by
`scripts/hardcore_workshop.py`. Recipe IDs, outputs and output counts remain
the same. All gear variants still consume four plates and the same previous-tier
gear; both iron upgrade tokens still consume eight iron blocks.

BuildCraft keeps its existing cross. Ender IO iron and Industrial Foregoing gold
use the four corners. Industrial Foregoing iron uses two plates at the upper
left and two at the lower right. Railcraft iron and gold use three plates in the
top row and one at the bottom centre. The gear remains in the centre in every
case. These layouts remain distinct after horizontal mirroring.

The pinned Minecraft `RecipeManager` returns the first matching crafting recipe
for an ordinary lookup without a recipe hint. Recipe-book placement may supply
a matching ID hint. The prior overlap therefore made ordinary manual selection
ambiguous; it did not establish that every alternate output was impossible
through every interface. Each corrected input grid must have exactly one
matching recipe, independent of recipe ordering or a remembered hint.

Three Iron Furnaces definitions also used the old `conditions` field. The
pinned NeoForge conditional decoder reads `neoforge:conditions`. The Copper
Upgrade, Copper-to-Iron Upgrade and Silver-to-Gold Upgrade now use that field.
Their copper/silver tags are present in this pack; correcting the field ensures
the intended nonempty-tag guard is actually evaluated.

## Updating an existing instance

Install the built 0.4.16 candidate on both client and server and restart them.
For an incremental update, copy the candidate's KubeJS files to the corresponding
instance directories while stopped. The two changed server scripts are
`qm_expert_hardcore_workshop.js` and `qm_expert_v040.js`.

Record the five changed gear layouts and Copper-to-Iron upgrade again in AE2
or other existing autocrafting systems. AE2 stores both recipe IDs and input
grids; keeping an ID does not update a previously encoded grid. Existing
BuildCraft cross patterns keep their layout. No quest IDs or reward claims
change.

The candidate also retains `qm-foundry-compat-0.4.15.jar` on client and server
for the pinned Modern Foundry 4.1.6. That module fixes ingredient consumption
in the Crafting Station; see the separate
[sandstone duplication report](CRAFTING_STATION_FIX.md). The recipe edits
and station fix address different defects and both are included in 0.4.16.

## Verification scope

The regression suite checks competing input grids across the authored
shaped recipe catalogue with supported ingredient matching, rather than relying
only on each recipe's own matcher.
It accounts for script priority and explicit removal of queued KubeJS recipes,
normalizes ingredient meanings rather than pattern letters, and considers
horizontal mirrors and intersections between tag alternatives. Historical
collision examples are negative controls. The current offline catalogue includes
474 shaped recipes and 26 ordinary shapeless recipes. This covers ordinary shaped
matching, Mekanism's shaped data recipes and supported wrapped shaped recipes;
component-sensitive ingredients and storage-specific serializers are excluded.
It does not simulate their state transfer. All nine affected grids are also
checked against the supported shapeless recipes.

The eight focused regressions pass. Restoring the actual old recipe definitions
reproduces all ten conflicting pairs across the three groups, while the current
definitions have none. The 149-tag membership fixture is bound to the exact
third-party lock and authored tag resources/handlers, so a change requires a
fresh review of that evidence.

An isolated run of the actual Minecraft 1.21.1 recipe codecs, pattern matching
and `RecipeManager` also passes. It loads the nine affected recipes, maps mod
items to distinct vanilla items, and explicitly supplies the reviewed tag
alternatives. The old definitions produce **748 ambiguous witness grids** and
**552 selections of a different output**. The corrected definitions produce
the intended output uniquely in **all 748 cases**, including mirrors and mixed
plate alternatives, with the expected material quantities. These are explicit
item/tag fixtures in a nine-recipe pool, not a fully loaded modpack.

To reproduce the offline gate:

```sh
python3 scripts/recipe_collisions.py
python3 -m unittest discover -s tests -p 'test_recipe_collisions.py' -v
```

With an existing NeoForge 21.1.262 installation and Java 21 JDK, reproduce the
isolated native check without starting a server or world:

```sh
python3 scripts/recipe_lookup_probe.py --runtime /path/to/neoforge-server \
  --javac /path/to/jdk21/bin/javac
```

`tests/runtime/HardcoreWorkshopProbe.java` now additionally checks all nine
affected recipes through the full server's `RecipeManager`, searches the entire
loaded crafting collection for competing matches, and tests real ingredient
alternatives and mirrored grids. It asserts material costs and exact selected
outputs. This fixture requires a separately authorized full-server run; compiling
it does not establish that it passed in a loaded pack.

Graphical Ctrl input, JEI transfer, encoded-pattern replacement and the exact
client/server interaction still need the recorded
[English](PLAYTEST.en.md#workshop-recipe-selection) or
[Ukrainian](PLAYTEST.uk.md#вибір-рецептів-майстерні) playtest. Neither offline
validation nor the earlier by-ID server checks prove every KubeJS recipe across
an entire survival playthrough.
