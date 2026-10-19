# Compatibility sources

SOQM 0.4.6 uses **ten** authored compatibility modules recovered from the supplied
Questing Mayhem Expert source chain. The original eight include full MIT
notices, which must remain with their source and binaries. The two new modules
declare MIT in `neoforge.mods.toml`, but neither their source nor JAR supplied a
full license/copyright notice. Their local `LICENSE` files record that declaration
and missing notice; they do not invent an upstream copyright holder.

| Module | Pinned production JAR | Original source baseline |
| --- | --- | --- |
| `cannibal` | `qm-cannibal-compat-0.4.1.jar` | 0.4.1, then local discovery fix |
| `chancecubes` | `qm-chancecubes-compat-0.4.1.jar` | 0.4.1 |
| `chance-lifecycle` | `qm-chancecubes-lifecycle-0.4.2.jar` | 0.4.2 |
| `lucky-support` | `qm-lucky-support-0.4.2.jar` | 0.4.2 |
| `quest-automation` | `qm-quest-automation-0.4.2.jar` | 0.4.2 |
| `quest-ui` | `qm-quest-ui-0.4.2.jar` | 0.4.2 |
| `village-guard` | `qm-village-guard-0.4.2.jar` | 0.4.2 |
| `zoom-defaults` | `qm-zoom-defaults-0.4.3.jar` | 0.4.3 |
| `mob-compat` | `qm-mob-compat-0.4.5.jar` | 0.4.5 |
| `chest-balance` | `qm-chest-balance-0.4.6.jar` | 0.4.6 |

The source archive chain is 0.4.6 → 0.4.5 → 0.4.4 → 0.4.3 → 0.4.2 → 0.4.1.
[`provenance.json`](provenance.json) records exact recovered paths, file hashes,
archive hashes and production-JAR hashes. The Cannibal module has a local
nonblocking discovery fix; the other recovered binary pins remain unchanged.
See [Cannibal build and validation notes](cannibal/README.md). The chest module
limits first-fill loot in exact target tables;
existing player inventories and already generated loot are outside that policy.

**Independent recompilation of all ten production JARs has not been
reproduced.** The Cannibal rebuild is documented separately. Matching recovered
source and binary provenance for the other modules does not show that compiling
this tree produces identical bytes. Historical build scripts can
require workspace-specific compiler/API paths and server installations; client
modules additionally need the correctly mapped Minecraft 1.21.1 client API.

A supported rebuild must restore exact Java 21, compiler, Minecraft/NeoForge
21.1.262 and mod API dependencies, resolve client mappings, then compile and test
the modules. Keep Minecraft binaries and downloaded third-party dependencies
out of this source tree. Use the pinned runtime binaries only through the local
build/materialization process; generated archives are not committed.
