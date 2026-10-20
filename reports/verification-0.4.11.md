# SOQM 0.4.11 verification

Date: **2026-10-20**. Base: merged main
`91f2d1c8f6dcf132362f1c105ae6ef4a126d5100` (PR #11).

## Reviewed scope

- Three compatible Minecraft 1.21.1 / NeoForge structure additions, taking the
  manifest to **127 pinned third-party files**. No new library dependencies.
- **12 authored modules** and **1,848 locked game payload files**. Chest Balance
  is rebuilt as 0.4.11; other production compatibility JARs are unchanged.
- All **1,626 quest IDs, 1,911 task IDs and 1,627 personal reward IDs** remain.
  Task types, objectives, quantities, rewards and claim behavior are unchanged.
- Nine dependency lists change across chapters 01, 22, 23 and 32. All **67
  Overworld visit objectives** are optional rewarded leaves; **20 former
  descendants** no longer depend on finding those locations. All six material
  gates retain their previous descendants, and actual dimension/boss gates
  remain. Both Ukrainian automatic-summary task labels now match their new
  prerequisites and survive regeneration.
- Five weighted loot profiles, **397 exact first-fill table routes** and **326
  distinct modifier source IDs**, including the earlier Lily additions. No
  modifier source ID is duplicated or lacks a compaction route.

## Completed checks

| Check | Result and scope |
| --- | --- |
| `./scripts/check` with a Java 21 JDK on PATH | 128 Python tests, both Node policy suites, pack metadata/payload validation, campaign/English/loot regeneration and deterministic archive verification |
| `python3 scripts/render_campaign.py --check` | All 11 committed coordinate previews are fresh |
| `python3 scripts/soqm.py verify-downloads` | All 127 pinned dependency files available and match reviewed sizes/SHA-256 |
| Chest Balance native build | Compiles with Temurin javac 21.0.12.1 against hash-pinned NeoForge 21.1.262; two builds are byte-identical |
| `python3 scripts/probe_exploration.py …` | Packaged production compactor executed with native Minecraft ItemStack/RandomSource; actual structure processors, chest load/save and loot/placement codecs executed |
| Exact modded loot item check | All 49 final modded item IDs have item-model evidence in the pinned JARs; 565 generated expert recipes inspected for custom gate bypasses |
| Native equipment probe | Compiled against pinned Minecraft/mod APIs, including Aether's embedded Accessories API; **not executed in a loaded modded server** |
| Independent review | Separate reviews of quest graph, tool/slot APIs, loot rules and structure data; corrected stale Ukrainian task labels and the elite-circuit loot bypass |

The initial default-PATH suite skipped the existing pure HUD geometry test
because that PATH contained only a JRE. The geometry test passed separately
with the available Java 21 JDK; the final full gate uses that JDK on PATH.

The installation archive is `dist/SOQM-0.4.11-curseforge.zip`; the build writes
its exact checksum to `dist/SHA256SUMS`. Generated archives, downloaded JARs,
local runtime logs and probe classes are not committed. Only the reviewed
source-backed Chest Balance module replaces its previous authored JAR.

## Native object results

The packaged Chest Balance module passed **50,000 seeded mixed-loot cases**
plus **50,000 single-treasure probability checks**. Fixtures cover empty/small
containers, ordinary slot/unit limits, duplicate materials, forbidden nether
stars, protected keys/maps/collectibles, already-rolled finds, deterministic
output and unchanged unlisted tables.

| Profile | Configured native valuable retention | Observed single-diamond retention, 10,000 seeds |
| --- | --- | --- |
| Settlement | 10% | 9.99% |
| Expedition | 25% | 24.99% |
| Workshop | 20% | 20.00% |
| Stronghold | 40% | 40.01% |
| Relic vault | 55% | 54.99% |

This is retention of an **already-generated** native treasure, not a chest's
total chance to contain a diamond. The separate useful-component bonus pool
rolls at 8/16/20/28/36%, subject to source finds and remaining final capacity.

The structure probe passed:

- Four moon chest orientations preserved by the actual processor JSON.
- A control chest loads 27 prefilled stacks; after processing it loads zero
  prefilled items, retains its deferred loot table and drops `Items` on save.
- Four unmatched block/state examples remain unchanged.
- All five widened Ember structure-set placements decode natively.
- The authored undead-dungeon loot table decodes through the native loot codec;
  all 18 vanilla trim-template tag members resolve.

Chest Balance JAR SHA-256:
`2026d75df4039f005c3ffb7fb5f515cd3290a45bea22757deb29e9246f838b0c`.
Build, compiler and source provenance are retained in
`src/compat/provenance.json` and `metadata/authored-mods.json`.

## Behavior and limits

Aether 1.5.10's gloves and related items use its native accessory inventory.
No pack setting or blanket interaction cancellation removing those native slots
was found. The fix adds explicit localized inventory/slot/key guidance for all
32 native accessories. It does not claim a reproduced underlying Aether bug or
switch inventory modes on an existing save. Infinity Backpack retains its
native Curios Back route; only Sophisticated Backpacks are relocated.

No fresh full-pack dedicated server, graphical client, multi-seed terrain
generation run, existing-save load test or long-term multiplayer economy test
was performed for 0.4.11. The older 0.4.10 server results are historical evidence
and are not presented as a run of this candidate. Native object probes verify
concrete code/data behavior, not room connectivity, rendering, charged mining,
Lootr gameplay or exploration frame/tick times.

New structures need new chunks; newly generated covered chest inventories use
the new loot policy. Already opened loot and existing player storage remain.
Update the complete client/server payload and restart both. See
[installation](../docs/INSTALL.en.md),
[optional exploration](../docs/OPTIONAL_EXPLORATION.md),
[equipment](../docs/ACCESSORY_POLICY.md),
[loot](../docs/EXPLORATION_LOOT.md) and
[structure compatibility](../docs/EXPLORATION_STRUCTURE_COMPAT.md).
