# SOQM Storage JEI Compatibility

This source-authored client module corrects the slot used for a recipe focus
link in the pack's pinned Sophisticated Core and JEI. It keeps the existing
storage recipes, their native serializers and their component-preservation
behavior.

## Exact defect

`CraftingSpecCategoryExtension.setRecipe` receives a compact ingredient list,
then asks JEI's `ICraftingGridHelper` to create the displayed input slots.
JEI 19.64.0.455 returns nine physical slot builders and places compact inputs
using its public `getCraftingIndex` method.

Sophisticated Core uses the compact source index to choose a physical slot
for `createFocusLink`. For the pack's `US` and `USU` recipes, source index 1
is displayed in physical slot 4. The old code selects empty physical slot 1
and tries to link its zero alternatives to one output alternative. JEI rejects
the mismatched counts. This is a display-index error, not an ingredient-cost
error.

The 24 workshop storage recipes consist of 14 full 3x3 recipes, seven `US`
recipes and three `USU` double-chest recipes. The last ten have this index
mismatch. The supplied log contains those ten grouped display IDs plus the
original iron-shulker recipe. The latter has one uncolored global variant and
therefore also reaches the focus-link branch without selecting a focused
wood/color variant.

The mixin changes only the argument to the second `List.get(int)` in
`setRecipe`: the lookup in the physical builder list. The first lookup remains
in compact ingredient coordinates. The existing checks that permit a focus
link, variant filtering, source/output choices and the actual call to
`createFocusLink` remain in the native method. Shapeless recipes use the same
input-count-based square dimensions as the pinned JEI helper before invoking
its mapping API.

## Scope and pins

| Dependency | Exact artifact / value |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.262 |
| Sophisticated Core | `sophisticatedcore-1.21.1-1.5.14.2413.jar` |
| JEI | `jei-1.21.1-neoforge-19.64.0.455.jar` |
| MixinExtras | 0.5.5, bundled by NeoForge |

The Core artifact advertises runtime mod version **1.5.14**, so the loader
dependency uses `[1.5.14]`; build 2413 is enforced by the archive hash in
`build.py` and the pack's lock. JEI advertises and requires `[19.64.0.455]`.
The build also verifies the target class SHA-256:

```text
422d00aabf7a4a36d155ba7aaa121c927ec0ba0ea202a41dddc37ecbbeec1386
```

The injection requires exactly one matched call site. The mixin configuration
lists the adapter only under `client`, the mod entry point uses `Dist.CLIENT`,
and all dependencies apply only on the client. A dedicated server does not need
this module or JEI. A client can join a server that lacks this display fix.
The module does not replace upstream class files or modify third-party JARs.

Keep the module with the reviewed dependency pins. Review and remove or revise
it before a coordinated Core/JEI update; a later version number alone does not
establish that the upstream source-slot defect was corrected.

## Rebuilding

Use Java 21 and an installed NeoForge 21.1.262 server library directory as the
compilation classpath. Installation of those libraries does not require
starting a server or accepting the EULA.

```sh
python3 src/compat/storage-jei-compat/build.py \
  --runtime /path/to/neoforge-server \
  --mods .cache/mods \
  --javac /path/to/java21/bin/javac
```

The default output is `overrides/mods/qm-storage-jei-compat-0.4.17.jar`.
`--output /path/to/copy.jar` supports an independent comparison build.
The builder verifies the native server, universal, Sponge Mixin, MixinExtras,
Core and JEI inputs. It compiles against their real API classes and emits a
sorted JAR with fixed entry timestamps and permissions. No generated API stubs
or upstream implementation classes are packaged.

## Verification boundary

Two clean builds with Temurin `javac 21.0.13` produced the same 3,520-byte JAR:

```text
7c45f5c63648ed3c0986d6884fb2dfbc3e76532d0c4753fb690137306c11757c
```

The focused native probe lives in `tests/runtime/storage_jei/`, with its runner
in `scripts/storage_jei_probe.py`. It checks the actual shipped Mixin
transformation and native Core/JEI slot construction and focus-link validation.
Its recorded result must distinguish supplied display/item fixtures from a
fully loaded client with Storage's wood/color catalog.

Full graphical acceptance still requires opening the affected recipes and
usages, checking wood/color variants, JEI transfer, reconnect and recipe reload.
Actual storage contents, upgrades and tier capacities remain governed by the
unchanged native recipe implementations and their separate storage tests.
