# Modern Foundry Crafting Station ingredient duplication

SOQM 0.4.15 includes `qm-foundry-compat-0.4.15.jar` for the pinned
**Modern Foundry 4.1.6**, Minecraft 1.21.1 and NeoForge 21.1.262. It corrects
the station's ingredient and crafting-remainder write indices. Install the
module on both client and server; see the [English](INSTALL.en.md#crafting-station-fix-in-0415)
or [Ukrainian](INSTALL.uk.md#виправлення-верстака-у-0415) installation instructions.

## Confirmed cause

The report concerns bulk sand-to-sandstone crafting in the **Modern Foundry
Crafting Station**, with Ctrl held. The first craft produces one sandstone,
then leaves sand spread through eight grid slots. The ordinary recipe consumes
four sand in a 2x2 square for one sandstone.

The exact pinned JAR is `ModernFoundry-1.21.1-4.1.6-NeoForge.jar`, official
[CurseForge file 8675380](https://www.curseforge.com/minecraft/mc-mods/modern-foundry/files/8675380),
23,385,070 bytes, SHA-256
`0b0026db1c27a1d08b1e3d3eba8fd2cc470d4cb534eb08d74633adc5be5691f1`.
Its `CraftingStationBlockEntity.takeResult` crops the grid to the recipe bounds
and correctly computes the corresponding physical 3x3 slot for each input.
It reads from that physical slot, but all three branches write the remaining
stack to the compact recipe index instead.

For example, four stacks of 16 in the bottom-right 2x2 square occupy physical
slots 4, 5, 7 and 8 (zero-based). The faulty code reads those stacks and writes
four stacks of 15 into slots 0, 1, 2 and 3. The original four stacks remain.
This produces the screenshot's layout: **124 sand plus one sandstone**, from
64 sand. The repaired write path leaves **60 sand plus one sandstone**, with
the ingredients still in their original four slots. This is a code-derived
reproduction; it does not establish the user's initial inventory count.

With one sand in each bottom-right slot, the faulty empty-stack branch instead
clears four already empty slots. The ingredients remain usable for another
craft. The patch therefore covers all three writes, including count-one
consumption and same-item remainder merging.

The upstream author recorded the same defect in
[issue #3](https://github.com/Modern-Mods/ModernFoundry/issues/3) and fixed it in
[commit 6cf71f2](https://github.com/Modern-Mods/ModernFoundry/commit/6cf71f28d3b943ed9d0f134b7e13701365e8f221).
That correction changes each `setItem(i, ...)` to `setItem(slot, ...)` and adds
a regression test for positioned ingredients.

## Why a compatibility module

The official [Modern Foundry 4.1.8 release](https://github.com/Modern-Mods/ModernFoundry/releases/tag/v4.1.8)
contains the correction. Its downloaded GitHub asset was checked against the
release API digest, SHA-256
`57bb463ddf58cfba2a28ade0cff82fc693b6822d9e20cac3035cb27181462be1`,
and the corrected writes were verified in its bytecode.

On 2026-10-22, the official CurseForge listing still contained seven files,
with 4.1.6 as its newest Minecraft 1.21.1 artifact. It did not provide a 4.1.8
file ID for SOQM's CurseForge manifest. The small source-backed SOQM module
preserves that distribution format and applies only the reviewed slot fix.
It does not redistribute or alter the upstream JAR on disk.

The module requires Modern Foundry 4.1.6 and all three expected injection sites.
Remove it when performing a coordinated client/server upgrade to an upstream
version containing the correction. Version 4.1.7 predates the fix.

IPN's previous settings remain in place. The initial refill-disable hypothesis
did not address this confirmed station defect and was withdrawn before merging.
The bug lies in the result-taking path, so changing the Ctrl binding is not a
repair. An ordinary Minecraft crafting table is a temporary alternative while
the old station remains installed without the patch.

## Validation boundary

The shipped 3,232-byte JAR has SHA-256
`8d68c962a5fb77bdb1b0ec37e567730efa422dba49572cdf441e17cd4fa6254d`.
Two independent builds using Temurin javac 21.0.13 produced identical bytes.

The focused probe applied real Sponge/MixinExtras to all three sites in the
exact pinned class, then executed original and patched `takeResult` bytecode.
The original code reproduced the eight-slot screenshot and count-one dupe;
the patched code passed **868 cases / 12,306 assertions**. Recipe positioning
and the crafting wrapper are native; item, inventory, player, recipe and event
state use fixtures. No graphical client, game world or network session was run.

The repository gate passed **162 Python tests with no skips**, both Node
suites and deterministic archive checks. All **129 third-party artifacts**
were verified against their pinned sizes and SHA-256 hashes. The package
contains **13 authored modules** and **1,850 locked payload files**.

The module's [build and native probe notes](../src/compat/foundry-compat/README.md)
record the exact dependency pins and focused checks. Graphical checks remain
in the [English](PLAYTEST.en.md#modern-foundry-crafting-station) and
[Ukrainian](PLAYTEST.uk.md#верстак-modern-foundry) playtest lists. They include
all four 2x2 positions, count-one inputs, bulk output, full inventories,
adjacent storage, remainders and reconnect persistence.

The acceptance count is **64 sand -> 16 sandstone**. Before completing the
conversion, count sand plus four times sandstone across the station, inventory,
cursor and dropped items; the total must remain 64. A successful build or
isolated code test does not by itself confirm graphical input handling or a
full multiplayer session.
