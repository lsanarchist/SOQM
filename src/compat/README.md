# Compatibility sources

SOQM 0.4.21 uses **fifteen** authored compatibility modules: ten recovered from
the supplied Questing Mayhem Expert source chain, the runtime optimization
module, the production-evidence adapter, the Foundry crafting fix and the
Sophisticated Core/JEI display fix, and the dungeon encounter integration. The original eight include full MIT
notices, which must remain with their source and binaries. The two later recovered modules
declare MIT in `neoforge.mods.toml`, but neither their source nor JAR supplied a
full license/copyright notice. Their local `LICENSE` files record that declaration
and missing notice; they do not invent an upstream copyright holder.

| Module | Pinned production JAR | Original source baseline |
| --- | --- | --- |
| `cannibal` | `qm-cannibal-compat-0.4.1.jar` | 0.4.1, then local discovery fix |
| `chancecubes` | `qm-chancecubes-compat-0.4.1.jar` | 0.4.1 |
| `chance-lifecycle` | `qm-chancecubes-lifecycle-0.4.2.jar` | 0.4.2 |
| `lucky-support` | `qm-lucky-support-0.4.2.jar` | 0.4.2 |
| `quest-automation` | `qm-quest-automation-0.4.4.jar` | 0.4.2, then local observation fixes |
| `quest-ui` | `qm-quest-ui-0.4.8.jar` | 0.4.2, then coordinated HUD, reward catalogue, mapped client API build and automatic minimap crash fix |
| `village-guard` | `qm-village-guard-0.4.3.jar` | 0.4.2, then target hooks and mutation-driven village indexing |
| `zoom-defaults` | `qm-zoom-defaults-0.4.3.jar` | 0.4.3 |
| `mob-compat` | `qm-mob-compat-0.4.5.jar` | 0.4.5 |
| `chest-balance` | `qm-chest-balance-0.4.21.jar` | Recovered 0.4.6 source; bounded exploration and Dimensional Dungeons routes |
| `progressive-rewards` | `qm-progressive-rewards-0.4.10.jar` | New SOQM source; recipe-backed refunds, world upgrades and native API build |
| `runtime-optimizations` | `qm-runtime-optimizations-0.4.7.jar` | New SOQM source and native API build; module version retained in pack 0.4.8 |
| `foundry-compat` | `qm-foundry-compat-0.4.15.jar` | New SOQM source; positioned ingredient writes for Modern Foundry 4.1.6 |
| `storage-jei-compat` | `qm-storage-jei-compat-0.4.17.jar` | New SOQM source; centred display-slot focus links for pinned Sophisticated Core and JEI |
| `dungeon-encounters` | `qm-dungeon-encounters-0.4.21.jar` | New SOQM source; persistent in-place Elite Mobs abilities and Apotheosis tier integration |

The source archive chain is 0.4.6 → 0.4.5 → 0.4.4 → 0.4.3 → 0.4.2 → 0.4.1.
[`provenance.json`](provenance.json) records exact recovered paths, file hashes,
archive hashes and production-JAR hashes. The Cannibal module has a local
nonblocking discovery fix, and Quest Automation has a bounded observation
optimization. Quest UI now coordinates the installed mods' HUD panels;
the other seven recovered binary pins remain unchanged.
See [Cannibal build and validation notes](cannibal/README.md). The chest module
limits first-fill loot in exact target tables;
existing player inventories and already generated loot are outside that policy.

The [production adapter](progressive-rewards/README.md) records successful
manufacture for the personal reward catalogue and persists native FTB claim
merges after team transitions. It has its own Java 21 native-API build script,
MIT notice, pinned binary and source hashes. Its production facts are written
from actual craft/finish events; it does not scan player inventories on ticks.
Its fixed recipe refunds are delivered within the same personal FTB claim,
after a native gate confirms manufacture before the claim is marked used.

The [Quest UI adapter](quest-ui/README.md) retains the existing quest ingredient
keys and tooltips, and coordinates Xaero, Ultimine, pinned quests and installed
status HUDs. Its build uses the pinned mapped client API, checks real Mixin
transformations and executes the native minimap size/scale call chain. The
client entry points remain isolated from dedicated servers.

Quest UI 0.4.7 is a resource-only revision of the verified 0.4.5 artifact. It
replaces backpacks and boats in the catalogue with consumable food, experience
and crafting supplies, retains the Masa exclusion and unstackable reward help,
and preserves every compiled class byte. Its separate resource build mode
pins the baseline artifact and unchanged sources; it does not claim a fresh
native compilation or graphical playtest.

The [runtime optimization module](runtime-optimizations/README.md) reuses
tunnel-height queries within one placement search, bounds Mayhem guardian
record processing and reduces Dungeons & Taverns polling. Its native API build
also reproduces Quest Automation 0.4.3. Both preserve their original MIT notices.
See the [runtime verification](../../reports/verification-pr5.md).

The [Foundry compatibility module](foundry-compat/README.md) remaps the three
ingredient/remainder writes in the pinned Crafting Station to their physical
inventory slots. It follows the upstream correction without including third-party
implementation classes. Its exact-version dependency and required injection
count prevent silently applying an unreviewed patch to a different release.

The client-only [storage JEI compatibility module](storage-jei-compat/README.md)
maps the compact source ingredient index to JEI's centred display slot before
Core links that slot to the result. Its exact dependency pins and required
single injection preserve the original focus conditions, recipes and storage
state transfer. It uses JEI's public mapping API and includes no upstream
implementation classes. The dedicated-server materializer excludes it.

The [dungeon encounter module](dungeon-encounters/README.md) retains each
eligible DivineRPG or dungeon mob's entity type, AI, equipment and drops. It
registers actual Elite Mobs ability state and applies one Apotheosis world tier
when a real player approaches. Saved ability cooldowns and one-shot flags are
restored without rerolling or applying attributes again. The exact-version
native build validates the upstream archives and Mixin target hashes. The
chest module's 0.4.21 native build adds explicit routes and protected puzzle items.

**Independent recompilation of all ten recovered production JARs has not been
reproduced.** The Cannibal, Quest Automation, Quest UI, runtime optimization and
production-adapter builds are documented separately. Matching recovered
source and binary provenance for the other modules does not show that compiling
this tree produces identical bytes. Historical build scripts can
require workspace-specific compiler/API paths and server installations; client
modules additionally need the correctly mapped Minecraft 1.21.1 client API.

A supported rebuild must restore exact Java 21, compiler, Minecraft/NeoForge
21.1.262 and mod API dependencies, resolve client mappings, then compile and test
the modules. Keep Minecraft binaries and downloaded third-party dependencies
out of this source tree. Use the pinned runtime binaries only through the local
build/materialization process; generated archives are not committed.
