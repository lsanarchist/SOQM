# Foundry compatibility 0.4.15

Targets Minecraft 1.21.1, NeoForge 21.1.262, Hilt 1.14, Java 21, and the exact
Modern Foundry 4.1.6 archive pinned in `metadata/mods.lock.json`.

## Crafting Station ingredient correction

Modern Foundry 4.1.6's `CraftingStationBlockEntity.takeResult` reads each
ingredient using its physical position in the 3×3 station, but writes the
updated stack using the index in the compact recipe rectangle. For example,
a top-left 2×2 sandstone recipe reads physical slots 0, 1, 3, and 4, then
incorrectly writes slots 0, 1, 2, and 3. This can leave ingredients behind,
copy stacks into other slots, and stop further crafting after one result.

`CraftingStationSlotMixin` changes only the integer slot argument of the
three `setItem` calls in this method. It maps the compact index through the
method's existing `CraftingInput.Positioned` snapshot and the station's
inventory width. This covers empty/single-item consumption, matching
remainders, and ordinary stack consumption. The original recipe selection,
stack calculations, crafting events, inventory notifications, remainder
insertion, and dropping behavior still run.

The mapping handles recipe offsets and recipe rectangles narrower than the
station. A full 3×3 recipe retains the same indices. The positioned snapshot
is captured before the loop; clearing the last item cannot change the
coordinate mapping for subsequent writes.

The injector requires exactly three matching calls. The mod dependencies
pin the runtime versions, and the build checks both the upstream archive and
target class hashes. These checks are complementary: the call count alone
does not identify a particular upstream implementation. Remove this
supplement when upgrading to an upstream build that already fixes the slot
writes.

Upstream independently corrected the same three write targets in
[commit 6cf71f28d3b943ed9d0f134b7e13701365e8f221](https://github.com/Modern-Mods/ModernFoundry/commit/6cf71f28d3b943ed9d0f134b7e13701365e8f221).
This supplement contains its own narrow argument mapping, with no bundled
upstream implementation or rewritten crafting method.

## Rebuild

Install the pinned NeoForge 21.1.262 server runtime outside the repository
and provide the pinned mod archives plus a Java 21 JDK:

```sh
python3 src/compat/foundry-compat/build.py \
  --runtime /path/to/neoforge-server \
  --mods /path/to/pinned-mods \
  --javac /path/to/jdk-21/bin/javac
```

The build uses the real patched Minecraft, NeoForge, FML, Mixin,
MixinExtras, Modern Foundry, and Hilt APIs. MixinExtras 0.5.5 is supplied
by the pinned NeoForge runtime. No API stubs or third-party classes are
packaged. Production method names are used with `remap = false`, so
annotation processing is disabled.

The default output is `overrides/mods/qm-foundry-compat-0.4.15.jar`.
Use `--output /path/to/file.jar` for an isolated build. Entries have fixed
ordering, timestamps, and permissions for reproducible packaging. Changes
to the production JAR also require updating the pack's authored-module
and source/payload provenance.

## Isolated native regression

After building the module, run:

```sh
python3 scripts/foundry_crafting_probe.py \
  --runtime /path/to/neoforge-server \
  --mods /path/to/pinned-mods \
  --javac /path/to/jdk-21/bin/javac
```

The probe applies the actual Sponge/MixinExtras transformation to the exact
pinned target and executes its original and transformed `takeResult` bytecode.
It retains native recipe positioning and the crafting inventory wrapper;
inventory, item, player, recipe and event boundaries use test fixtures.
The unmodified code reproduces both the reported eight-slot sand layout and
non-consuming crafts with single-item inputs.

The 0.4.15 artifact passed **868 cases / 12,306 assertions**, covering all
rectangular recipe sizes and offsets, stack counts 1/2/16/64, empty/same-item/
bucket remainders, full-inventory drops, and 16 sandstone crafts in each corner.
All three required injection sites were transformed. The generated report is
`reports/foundry-crafting-probe.json`. This is an isolated code regression;
it does not launch a world or test Ctrl input, networking, or every other mod's
Mixin. The graphical checks below remain separate.

## Installation and verification

Install the supplement on the client and server while retaining the pinned
Modern Foundry 4.1.6 and Hilt 1.14 JARs. It adds no registries or saved-world
data and requires no inventory-profile configuration changes.

Verify in a disposable world with 64 sand, an empty cursor, and room for
the result: the Crafting Station must produce 16 sandstone and leave no
sand. Repeat with the 2×2 pattern shifted within the 3×3 grid, then check
single-item depletion, a recipe with container remainders, and a full 3×3
recipe. Count items after reopening the station and after rejoining to
check both the server state and the client display. Existing surplus items
are not removed by this correction.
