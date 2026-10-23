# Sophisticated Storage focus-link display fix

SOQM 0.4.17 adds the client-only `qm-storage-jei-compat-0.4.17.jar` to correct
Sophisticated Core's source-slot selection when it creates a JEI focus link.
The original storage serializers, costs, inventory/component carryover and
KubeJS recipes remain intact. This is separate from the
[Foundry crafting fix](CRAFTING_STATION_FIX.md) and the
[workshop recipe conflict fix](RECIPE_CONFLICT_FIX.md).

## Report and cause

The supplied October 23, 2026 extract identifies SOQM 0.4.14, source commit
`44fad78b76903d6b1401db90bdf396e10d8e32d1`, JEI 19.64.0.455 and Sophisticated
Core 1.5.14.2413. It lists eleven storage display failures and contains three
selected traces reaching `CraftingSpecCategoryExtension.setRecipe`, line 45:

```text
java.lang.IllegalArgumentException:
All slots must have the same number of ingredients
in order to create a focus link.
```

Core has two different lists. `inputStacks` contains the compact recipe
ingredients; `inputSlots` is the list of all nine JEI display-slot builders.
JEI centres a small recipe within those nine slots. Core correctly checks
the source's alternative count in `inputStacks`, then incorrectly reuses
that compact index to choose a builder in `inputSlots`.

For a single-row storage recipe, with zero-based indices:

| Pattern | Source ingredient | Compact index | Correct JEI index | Old linked slot |
| --- | --- | --- | --- | --- |
| `US` | `S`, storage | 1 | 4, centre | 1, empty upper-middle |
| `USU` | `S`, storage | 1 | 4, centre | 1, empty upper-middle |
| Full 3x3, source in centre | storage | 4 | 4, centre | 4, correct |

The reported exception is therefore **zero alternatives in an empty display
slot versus one output**, even though Core's earlier compact-input check
found one source. It is unrelated to the number of iron blocks consumed by
crafting. The same affected recipes and adapter are present in 0.4.16.

The listed display IDs are the direct
`kubejs:hardcore_workshop/storage/iron_shulker_box` and the following suffixes
under `kubejs:tier_upgrade_grouped/hardcore_workshop/storage/`:

```text
double_copper_chest
double_iron_chest
double_iron_chest_from_copper_chest
iron_barrel
iron_chest
iron_shulker_box
limited_iron_barrel_1
limited_iron_barrel_2
limited_iron_barrel_3
limited_iron_barrel_4
```

The grouped recipes are generated display entries from Sophisticated Storage.
Their `kubejs` namespace does not make this a KubeJS parsing failure.

## Repair

The authored Mixin changes only the integer argument to the **second**
`List.get` in Core's `setRecipe` method, selecting the builder using
`ICraftingGridHelper.getCraftingIndex`. The first lookup continues to use the
compact ingredient index. For zero/negative dimensions, the adapter uses the
same input-count-based square dimensions as JEI's shapeless placement.

The original conditions deciding whether to create a focus link, focused
variant filtering, ingredient lists and output lists still run in Core.
The fix neither catches the exception nor removes the focus link.

The module is registered only on the physical client, and its Mixin is in
the `client` section. The server materializer excludes the module. Exact
dependency versions are declared, and the native build checks the Core/JEI
archive hashes and target-class hash. The Mixin requires exactly one matching
injection site. Core's loader version is `1.5.14`; the archive/build pin is
the full `1.5.14.2413`.

## Why updating these mods alone does not fix this report

As checked on October 23, the latest compatible Core **1.5.17.2426** and
Storage **1.6.8.2196** were published on October 21. The Core target class in
the newer release is byte-for-byte identical to the pinned class:

```text
CraftingSpecCategoryExtension.class SHA-256
422d00aabf7a4a36d155ba7aaa121c927ec0ba0ea202a41dddc37ecbbeec1386
```

All 33 Storage recipe-viewer classes are also unchanged between the pinned
and newer releases. Official release downloads were checked against their
published SHA-1 and SHA-512 before comparison. SOQM retains its existing
129 third-party pins for this patch.

JEI already provides the mapping API in the pinned 19.64.0.455. Its upstream
issue explicitly describes the need to map ingredient indices to centred
slots when constructing focus links. The verified Minecraft 1.21.1 release
that introduced this API was 19.64.0.453. This SOQM repair is an original
compatibility integration, not an existing Sophisticated patch backport.

Primary upstream references:

- [Core's adapter](https://github.com/P3pp3rF1y/SophisticatedCore/blob/1.21.x/src/main/java/net/p3pp3rf1y/sophisticatedcore/compat/recipeviewers/jei/CraftingSpecCategoryExtension.java)
- [JEI issue 4503: centred slot mapping for focus links](https://github.com/mezz/JustEnoughItems/issues/4503)
- [JEI's Minecraft 1.21.1 API commit](https://github.com/mezz/JustEnoughItems/commit/43206a25e4786fb852cd13e913cbfc4e08cccf30)
- [Core 1.5.17.2426 release](https://www.curseforge.com/minecraft/mc-mods/sophisticated-core/files/9279274)
- [Storage 1.6.8.2196 release](https://www.curseforge.com/minecraft/mc-mods/sophisticated-storage/files/9279428)

## Verification and reproduction

The shipped Mixin transforms the exact pinned target with one changed
argument and both native list lookups retained. The native regression passes
**830 cases / 14,544 assertions**, including 415 correctly linked pairs and
415 unchanged no-link conditions. It covers all 36 shaped source positions,
all 45 shapeless positions, grouped/ungrouped displays, focused variants,
sparse inputs, multiple outputs and native stack counts/components.

The original class is a required negative control: it produces 15 exact
exceptions and eight silent wrong-source links across the 36 shaped source
positions; 13 positions already work. In particular, the original `US` and
`USU` source index 1 must throw the reported exception. The same Mixin config
loaded for the server side leaves the target class byte-identical.

After building the module, run with Java 21 and the pinned installed libraries:

```sh
python3 scripts/storage_jei_probe.py \
  --runtime /path/to/neoforge-server \
  --mods .cache/mods \
  --javac /path/to/java21/bin/javac
```

The runner writes `reports/storage-jei-probe.json` with the before/after
results, source/dependency hashes, adapter hash and transformed-class hash.
Generated reports and downloaded dependencies remain local. The module was
rebuilt byte-identically twice; its production SHA-256 is:

```text
7c45f5c63648ed3c0986d6884fb2dfbc3e76532d0c4753fb690137306c11757c
```

## Verification boundary

The [module notes](../src/compat/storage-jei-compat/README.md) document its
native API build and the focused regression runner. The probe starts at a
constructed native Core display specification and executes the real Core
extension, JEI grid helper, ingredient manager and focus-link validation with
vanilla item stacks. Item identity fixtures replace mod registration and
rendering services. It does not launch a graphical client, register the full
Storage mod, create a world or run a server.

The supplied extract reports fourteen total broken-recipe diagnostics, but
only identifies eleven storage entries. The other three IDs and exceptions
are absent; this fix does not claim to diagnose or clear those unknown errors.
See [current verification results](../reports/README.md) and the separate
[graphical playtest](PLAYTEST.en.md#sophisticated-storage-in-jei).

For installation, see [English](INSTALL.en.md#sophisticated-storage-displays-in-jei)
or [Українська](INSTALL.uk.md#відображення-sophisticated-storage-у-jei).
