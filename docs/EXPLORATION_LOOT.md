# Exploration loot in SOQM 0.4.23

The generic profiles below remain in use outside Dimensional Dungeons.
SOQM 0.4.22 moves DD combat chests to dedicated combat profiles; its material
kit still uses Settlement. See [Dimensional Dungeons](DIMENSIONAL_DUNGEONS.md)
for the new treasure, earned expedition keys and two original challenges.

Exploration should occasionally save a useful crafting step, provide supplies
and produce memorable finds. Factories remain the dependable source of bulk
materials. This pass keeps small chests and adds variety without handing out
finished machines or the pack's dimension/quantum components.

## What a new chest rolls

The source structure keeps its own loot table. One exact matching global
modifier adds a weighted material bundle, a chance of a utility bundle, a
chance of one useful component, and the existing small Chance Cube chance.
The native first-fill policy then merges duplicate stacks and applies the
budget before Minecraft distributes items into slots.

| Profile | Examples | Ordinary slot limit, chosen per chest | Total ordinary item budget | Useful-component bonus roll | Native valuable retention |
| --- | --- | --- | --- | --- | --- |
| Settlement | Village homes, small supply containers | 3–6 | 16 | 8% | 10%, at most one |
| Expedition | Small dungeons, mineshafts, treehouses | 4–7 | 20 | 16% | 25%, at most one |
| Workshop | Smithies, engineering rooms, early planetary supplies | 3–6 | 18 | 20% | 20%, at most one |
| Stronghold | Dangerous dungeons, Ancient Cities, some planetary caches | 5–9 | 24 | 28% | 40%, at most two |
| Relic vault | End treasure and later planetary vaults | 6–10 | 28 | 36% | 55%, at most two |

These are **limits and bonus-roll probabilities**, not promised chest contents.
Source loot can supply the same find; at most one such find survives the final
selection. Keys and other protected items take space first, and a partly full
container can have fewer available slots. A Chance Cube can also lose to the
remaining slot/item budget. Native valuable retention is applied only to an
already-generated source treasure; it is not a chance to create an extra
diamond in every chest.

Before this change, all ordinary source valuables had the same 10% retention
chance regardless of dungeon difficulty. Modded additions also heavily repeated
the same basic cables, pipes, silicon and binder. The new weighted utility pools
include travel food, torches, pearls and enchanting supplies alongside useful
modded materials. Materials are small bundles; curated component finds are
single items.

Examples of the component finds include control circuits, printed/finished AE2
processors, capacitors, filter upgrades, energy/speed upgrades and modest
Botania supplies. The exact weights and tier placement live in
[`metadata/exploration_loot.json`](../metadata/exploration_loot.json), and
`python3 scripts/exploration_loot.py` regenerates both the five addition tables
and the matching native item classification. `--check` detects stale output.

## Progression and item protection

Random chest loot excludes plastic/HDPE, renewable Artifacts food, nether stars,
rockets, completed high-end machines, custom `kubejs:` progression parts and
the existing endgame namespaces. Elite and ultimate Mekanism circuits are also
excluded: SOQM's elite recipe consumes the custom Dimensional Matrix and Quantum
Processor. An old elite-circuit entry in the vault pool is replaced with an
advanced circuit.

Some mid-tier finds intentionally save one ordinary crafting step, such as an
advanced circuit or double-layer capacitor. They are rare single items, not
large bundles. Loot pickup does not provide the manufacture evidence required
by SOQM's production-gated quest reward system.

Existing source maps, keys, lore, music discs, cosmetic templates, artifacts
and boss trophies keep their protection. Aether's three native treasure tables
also keep their own Aether reward items. Protected loot consumes the ordinary
budget first and may exceed its soft limit when necessary, but never exceeds
available container space. Boss entity drops are outside this exact chest
policy.

Authored Dimensional Dungeons combat prizes retain their original Apotheosis
components through Aether's glove loot modifier. Aether may normally replace
chest armor with newly created gloves, copying only vanilla enchantments; that
would erase a guaranteed prize's affixes. Version 0.4.23 excludes only SOQM's
temporarily marked combat rewards from this conversion. Unmarked armor still
uses Aether's normal glove roll, and the temporary reward marker is removed
during final chest filling.

The policy covers **541 exact table IDs**, including 17 top-level tables
from Twilight Treehouses, Ad Astra: More Structures and Ember's Underground
Rooms. The addition modifiers use disjoint exact routes; special dimension
predicates are retained. NeoForge's `add_table` draws the added table through
`getRandomItemsRaw`, so these additions do not recursively invoke global loot
modifiers.

## Accessory variety and When Dungeons Arise

Version 0.4.23 changes the final first-fill selection for Artifacts and Relics.
Every distinct native accessory type is reserved before choosing replacements.
The first copy keeps its complete original components, name and item identity;
extra copies of the same item ID become different reviewed accessories, selected
without replacement. All original distinct rewards and progression items take
priority over replacement copies if the physical container is too small.

The broader reviewed pool contains **46 types: 37 Artifacts and nine Relics**.
Settlement and Workshop additions use a 21-item utility subset. Renewable food
remains blocked, and End/Nether/Bastion-only Relics remain native rewards rather
than new general additions. The first native copy of such a reward is retained.
Exact pools and exclusions live in
[`metadata/accessory_loot.json`](../metadata/accessory_loot.json).

There is also a modest chance of **one additional accessory**, only when fewer
than two different accessory types are present and the ordinary slot/item budget
has room. This is an independent accessory chance, separate from the useful
component chances in the earlier table.

| Profile | Accessory bonus chance |
| --- | ---: |
| Settlement | 2% |
| Workshop | 4% |
| Expedition / basic dungeon | 8% |
| Stronghold / hard dungeon | 12% |
| Relic vault / dungeon vault | 16% |
| Citadel supplies / treasure | 10% / 16% |
| Abyss supplies / treasure | 12% / 16% |

Different chests can still contain the same type; this system has no global
collection memory. Already-opened chests, player storage and saved Lootr personal
inventories are unchanged, even if those saved contents contain duplicates.

WDA adds **128 exact chest/barrel routes**, verified against the valid loot tables
referenced by the pinned 2.1.68 structure templates. The audit excludes twelve
dangling legacy references. Native vault blocks, trial-spawner rewards,
archaeology, decorated pots and entity drops are outside this policy.
WDA's named/modelled special rewards, maps, keys and native enchantments keep
their full components. The route audit is in
[`metadata/when_dungeons_arise_loot.json`](../metadata/when_dungeons_arise_loot.json).

The [structure compatibility notes](EXPLORATION_STRUCTURE_COMPAT.md) explain
the separate moon-tower prefilled-chest fix and Ember's missing 1.21 table.

## Save behavior and runtime cost

Changes apply when a covered chest or a player's new Lootr inventory first
generates its contents. Already generated loot, existing player storage and
already claimed rewards are untouched. New structures require new chunks.
Update both client and server and perform a full restart after installing the
new authored module.

No periodic inventory scan, player tick handler, structure search or player-data
cache was added. Lookup is by an exact table map; selection runs only during
loot filling. The algorithm still merges a finite generated stack list, bounds
ordinary output and releases all stack references when the call returns.

The full-server `AccessoryLootProbe` exercises actual registered Artifacts and
Relics, original component preservation, duplicate replacement in small
containers, seeded bonus rates, native WDA loot tables and saved Lootr contents
through a resource reload. Its fixture belongs to the disposable native encounter
test server; it is never shipped in the modpack.

## Verification

`./scripts/check` validates generated tables, disjoint routes, small quantities,
forbidden material gates and equality between source policy and packaged JAR.
All 49 modded item IDs in the final five profiles were checked against item
models in the exact pinned mod JARs; relevant custom recipes were inspected for
dimension/quantum bypasses.

The native object checks execute the **packaged production compactor** with
Minecraft 1.21.1 ItemStacks and seeded RandomSource instances:

```bash
python3 scripts/probe_exploration.py \
  --runtime /path/to/installed-native-runtime \
  --java /path/to/jdk21/bin/java
```

The runtime must contain the mapped 1.21.1 `server-…-srg.jar`, its `extra.jar`
and official libraries. The official version library manifest defaults to
`versions/1.21.1/1.21.1.json`; pass `--version-json` if stored elsewhere.

The probe checks 50,000 seeded bounded chest cases, 50,000 single-treasure rolls,
small-container limits, duplicate merging, forbidden nether stars, protected
source items, one-find selection, deterministic results and untouched unlisted
tables. It also executes the native moon structure processor and placement
codecs. This does not establish a loaded modpack session, graphical inventory
behavior, a Lootr multiplayer playthrough or measured chunk-generation speed.

## Reference direction

[Craftoria's loot integration settings](https://github.com/TeamAOF/Craftoria/blob/main/config/lootintegrations.json)
favor modded finds, while its
[loot policy](https://github.com/TeamAOF/Craftoria/blob/main/kubejs/server_scripts/Mods/Craftoria/Loot.js)
also excludes renewable Artifacts food. That broad design direction and the
user's requested Questing Mayhem spirit informed this pass. SOQM's tables,
weights, budgets and code here are independently authored; this is not a copy
of another pack's drop rates.
