# Exploration structure additions

## Exact compatible files

These additions target SOQM's existing Minecraft 1.21.1 / NeoForge 21.1.262
runtime. The official downloads were inspected as ZIP/JAR files, and their byte
counts and SHA-256 hashes are pinned in `metadata/mods.lock.json`. Their JARs are
downloaded by the launcher, not redistributed in `overrides`.

| Mod | Version inside the JAR | Official project / file | Required content already in SOQM |
| --- | --- | --- | --- |
| Twilight Treehouses | 1.0.2 | [1227052 / 6341110](https://www.curseforge.com/minecraft/mc-mods/twilight-treehouses/files/6341110) | The Twilight Forest 4.8.3345 |
| Ad Astra: More Structures | 1.0.2 | [1311898 / 8762665](https://www.curseforge.com/minecraft/mc-mods/ad-astra-more-structures/files/8762665) | Ad Astra 1.16.26 |
| Ember's Underground Rooms | 2.3.2-neoforge | [1197753 / 8924057](https://www.curseforge.com/minecraft/mc-mods/embers-underground-rooms/files/8924057) | No additional library |

Ember's actual NeoForge requirement is **21.1.234 or newer**, satisfied by
21.1.262. Ad Astra: More Structures explicitly requires `ad_astra` and Minecraft
`[1.21,1.21.2)`. Treehouses' loader metadata has a broad Minecraft version range,
so the official 1.21.1 file and its actual data references were also checked.
Its required Twilight Forest content is stated by the author even though it is
not declared as a required dependency in the JAR's TOML.

All referenced template files were found in the respective JARs. The new Ad
Astra structures' referenced planetary biomes and biome tag, and the two
addons' modded template blockstates, exist in SOQM's pinned base mods.

## Generation and bounded work

The additions use native jigsaw structures. Their inspected entrypoint classes
and resources contain no recurring tick handler or `minecraft:tick` function;
there are also no embedded command blocks in the Ad Astra and Ember templates.
Generating and populating additional chunks still costs CPU time. This is not
a claim that their world generation has zero cost or a measured TPS guarantee.

Treehouses retain the original shared 48-chunk candidate grid and generate in
Twilight Forest biomes. Ad Astra's structures retain their planetary biome
restrictions and original candidate grids. Neither addon adds its structures to
ordinary Overworld biomes.

Ember's five multi-room structure sets use 48-chunk grids with 24-chunk
separation. The originals used 30 or 40 chunks. This reduces candidate density
for those sets without changing jigsaw depth or duplicating structure templates.
The original 65-chunk single-room set remains unchanged. Spacing is a candidate
grid size, not a guaranteed distance between structures or a spawn guarantee.

The jail placement also has two explicit data adjustments:

- The upstream salt `8763763781` exceeds a signed 32-bit integer. The native
  1.21.1 codec silently reads it as `173829189`; the override spells out that
  same effective value, without relying on integer truncation. This does not
  change the seed salt and is not a fix for a demonstrated loading crash.
- The original jail starts at surface height minus 140. In snowy plains near
  sea level that places its start below the Overworld minimum Y. The SOQM
  override starts at fixed Y = -32 without surface projection, keeping its
  initial room underground and above the bottom of the world. Actual joined
  room placement remains subject to native jigsaw and terrain checks.

These settings affect **newly generated chunks**. They do not regenerate,
delete, or repair existing terrain.

## Loot compatibility fixes

The pinned Ad Astra addon contains a fully prefilled 27-slot chest in
`ad_astra_more_structures:moon_tower_dungeon`. Fixed `Items` bypass ordinary loot
table balancing. A native `minecraft:rule` processor is attached only to this
template's pool. Four `blockstate_match` rules preserve each dry, single chest's
facing and assign the same deferred moon dungeon table used by its five other
chests: `ad_astra:chests/dungeon/moon/dungeon_chest`.

This uses `minecraft:append_loot`; no copied or edited third-party NBT asset is
shipped. Native `ChestBlockEntity` loading ignores `Items` when `LootTable` is
present, and its next save writes the deferred table rather than the old items.
The five original randomized chests retain their table; the prefilled chest now
rolls balanced loot as well. Other structures and player-placed chests do not
run this processor. Already generated prefilled chests are unchanged.

Ember's JAR stores `underground_rooms:chests/undead_dungeon_loot` under the old
`loot_tables` directory, while Minecraft 1.21.1 loads `loot_table`. Four template
chests refer to that missing table. SOQM supplies a new authored table under the
correct singular directory and routes it through the dungeon loot policy. Other
Ember chests use vanilla tables, apart from two deliberately prefilled story
books and brewing-stand contents, which are left intact.

Treehouses' four chest tables and the Ad Astra addon's common/rare chest tables
are covered by the exploration loot policy. The addon contains nether stars in
its original rare chest pool; those chest contents pass through SOQM's bounded
replacement policy. Its five entity boss-drop tables remain separate.

## Native object verification and remaining checks

`tests/runtime/ExplorationStructuresProbe.java` can run without creating a world
or launching a server. It was compiled with Java 21 and executed against native
Minecraft 1.21.1 classes and the matching official runtime libraries. It checks:

- The actual processor JSON decodes with the native processor codec.
- All four chest facings are preserved by the actual rule processor.
- A control chest loads all 27 stacks, while processing that same NBT produces
  zero prefilled items, retains the deferred loot table, and drops `Items` on
  save.
- Existing randomized moon chests keep their table, and four examples of
  unmatched blocks/states remain untouched.
- All five Ember placement overrides decode with the native placement codec.
- The authored undead chest table decodes with `LootTable.DIRECT_CODEC` and the
  actual vanilla item registry. Its `minecraft:trim_templates` tag exists in
  native 1.21.1 resources, and all 18 vanilla members resolve to real items.

This is an API/data compatibility check. It does not substitute for a complete
pack launch, a multi-seed terrain generation test, or client rendering checks.
Before release, visit a new treehouse, a new moon tower and a new Ember dungeon
in a disposable test world; verify room connections, loot, spawners and tick
time while exploring. Do not reset a player's existing chunks for this check.
