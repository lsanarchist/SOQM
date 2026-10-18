# Compatibility sources

These eight small modules supply the campaign's compatibility fixes. Their
Java sources, resources and original MIT license notices were recovered from
the supplied Questing Mayhem Expert source archives without changing them.

| Module | Pinned production JAR | Original source |
| --- | --- | --- |
| `cannibal` | `qm-cannibal-compat-0.4.0.jar` | 0.4.1: `authoring/cannibal_compat` |
| `chancecubes` | `qm-chancecubes-compat-0.4.1.jar` | 0.4.1: `authoring/chancecubes_compat` |
| `chance-lifecycle` | `qm-chancecubes-lifecycle-0.4.2.jar` | 0.4.2: `authoring/v042_work/chance_lifecycle/compat` |
| `lucky-support` | `qm-lucky-support-0.4.2.jar` | 0.4.2: `authoring/v042_work/lucky/compat` |
| `quest-automation` | `qm-quest-automation-0.4.2.jar` | 0.4.2: `authoring/v042_work/quests/compat` |
| `quest-ui` | `qm-quest-ui-0.4.2.jar` | 0.4.2: `authoring/v042_work/quest_ui/compat` |
| `village-guard` | `qm-village-guard-0.4.2.jar` | 0.4.2: `authoring/v042_work/village_protection/compat` |
| `zoom-defaults` | `qm-zoom-defaults-0.4.3.jar` | 0.4.3: `authoring/v043_work/mods/compat` |

The archive chain is 0.4.4 Sources → 0.4.3 Sources → 0.4.2 → 0.4.1.
[`provenance.json`](provenance.json) records the archive hashes, recovered file
hashes and production JAR hashes. The release uses those pinned production
JARs from `overrides/mods`.

**Independent recompilation of all eight production JARs has not been
reproduced.** Matching source provenance and a matching binary hash do not
establish that compiling this tree produces those bytes. The original build
scripts were intentionally omitted: they expect the previous workspace's
compiler paths, downloaded dependency paths and NeoForge server installation;
the client modules also require a named Minecraft 1.21.1 client API JAR that
was excluded from the source release.

A future supported build must restore the exact Java 21, compiler,
Minecraft/NeoForge 21.1.262 and mod API dependencies, resolve the client API
mapping step, then compile and test the modules. Keep Minecraft binaries and
downloaded third party dependencies out of this source tree. Preserve each
module's license when redistributing its source or binary.
