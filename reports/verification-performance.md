# Historical runtime integration verification — SOQM 0.4.8

This record applies to integration commit
[`5b59c3a`](https://github.com/lsanarchist/SOQM/commit/5b59c3ac44dbffb2bbc1d079f67d635d3e420d02),
before the Dungeons entity-membership fix. Its artifact and input hashes are
historical. See [the follow-up verification](verification-pr5.md) for the repaired
native module and current combined runs.

Validation completed on **2026-10-19** against the combined, locked production
payload after merging the updated questbook and personal rewards from
[`fc5328d`](https://github.com/lsanarchist/SOQM/commit/fc5328de31809e5d11fc0f0143e488dee6689090).
All **41 current questbook files**, campaign identity/route/reward policy files,
merged skill definitions/advancements and the progressive-rewards source/JAR
are byte-identical to that base. The complete questbook Git tree is
`0c1be6ed555e5c80845be44ae53e7072d6715cb5`.

**Result:** the native behavioral and work-count fixture passed against the
server side of the combined pinned pack, including real data reload and clean
shutdown. Both optimization JARs reproduced byte-for-byte in an independent
native build. Module versions remain Quest Automation 0.4.3 and Runtime
Optimizations 0.4.7 inside pack 0.4.8.

## Merge integration repairs

The merge at
[`37fdb1e`](https://github.com/lsanarchist/SOQM/commit/37fdb1e7521e0861e2afd86926a4a423831f4e24)
kept both branches' production implementations, but dropped four optimization
entries from the payload lock and the runtime module's authored/provenance
records. Its [CI run](https://github.com/lsanarchist/SOQM/actions/runs/39259910640)
therefore failed at the payload check. All four files were compared with the
reviewed optimization commit before restoring their exact pins.

The merge also removed the shared native harness's compiler, source and
workspace hooks while the performance entrypoint still supplied them. The
repaired harness supports both probes, retaining the newer reward recipe
evidence staging, multi-source Java compilation and authored-mod classpath.
Three offline regressions cover both dispatch paths and the baseline/payload
gates; module provenance now checks actual source directories and shipped JARs.

No questbook, reward policy, progressive-rewards source, dependency pin or
production optimization was edited for this integration repair. The original
0.4.7 evidence remains available in the
[initial verification record](https://github.com/lsanarchist/SOQM/blob/307cf612ae9b7873df7646ab4769de8d2146e526/reports/verification-performance.md).

## Runtime and execution

| Check | Result |
| --- | --- |
| Minecraft / NeoForge | 1.21.1 / 21.1.262 |
| JDK | Eclipse Temurin 21.0.12.1+1 |
| Third-party dependencies verified by hash and size | 122 |
| Authored modules in the production package | 12; server staging excludes client-only modules |
| Production override files | 1,835 |
| Fresh localhost world and synthetic players | Passed; no existing player world was used |
| Recipe count before and after `/reload` | 20,077 → 20,077 |
| KubeJS startup / server errors | 0 / 0 |
| Fatal server errors | 0 |
| Save and shutdown | All dimensions saved, exit 0, no forced termination |
| Payload lock matched before execution | Yes |
| Production payload unchanged during execution | Yes |
| Fixture and harness sources match the final report | Yes |

The successful local run is `.work/performance-probe/run-20261019T133759.867231Z`. Its complete staging,
compilation, startup, fixture, reload and shutdown duration was
**111.51 seconds**. That duration is not a generation benchmark.
Synthetic players use a native NeoForge mock connection and in-memory Netty
channels while retaining the real packet listener and payload checks. This
executes server-side event behavior; it does not validate a graphical client
or the client/server login protocol. Error counts above do not describe a
warning-free upstream modpack console.

## Abandoned Tunnels: exact native method comparison

The fixture loads the exact original `TunnelStructure` and `Site` bytecode from
the verified upstream JAR through a separate test-only loader. An authored
configuration access adapter delegates to the actual native `Config.mask()`;
it neither substitutes a fixed configuration nor rewrites the target method.
The other side is the actual production class after Mixin transformation.

Both versions receive equivalent generation contexts and seeded random
sources. The comparison checks exact generated piece NBT, random draw count,
next random value, and height-query order. Every retained query is the first
occurrence of an original query. No original terrain sample is omitted.

| Scenario group | Original height calls | Patched height calls | Calls removed |
| --- | ---: | ---: | ---: |
| 512 searches across eight synthetic terrain profiles | 463,220 | 437,298 | 25,922 (5.596%) |
| Four native Overworld terrain searches | 3,472 | 3,329 | 143 (4.119%) |

The synthetic cases include **374 generated tunnels** and **138 rejected
sites**, both axes, and every branch count from zero through three. All layouts
and random sequences matched. Their combined layout/RNG digest is
`8439d78d83ab1de7b595e31b87eec739deb9e34a41e091d21812d3411315fe86`.

The additional native terrain cases use the actual installed Overworld
generator. All four happen to reject their candidate site in both versions:

| Chunk X, Z | Original height calls | Patched height calls | Result |
| --- | ---: | ---: | --- |
| 0, 0 | 868 | 828 | Rejected in both |
| 128, -96 | 868 | 842 | Rejected in both |
| -512, 513 | 868 | 844 | Rejected in both |
| 2048, -2048 | 868 | 815 | Rejected in both |

Further native checks passed for separate heightmap caches, signed coordinates,
generator/accessor/random-state identity, an actual minimum-integer height,
the 2,048-entry capacity limit, nested calls, exceptions through the mixed
method's `finally`, and concurrent worker isolation.

These figures measure calls removed from the tunnel placement method. They
are not percentages for total chunk-generation time, server TPS, client FPS,
or exploration throughput. This fixture does not compare every block of two
fully generated worlds.

## Mayhem guardian lifetime and persistence

The actual native lifecycle, KubeJS bridge, entity events and saved NBT passed:

- **10,000 deadline replacements** retain one current entry; due processing
  respects its budget and supports reentrant updates.
- Bounded legacy migration allows a pending reward claim and a new spawn
  between batches. Compressed-NBT save/reload midway preserves unconsumed
  tickets and consumed markers. Malformed records are removed.
- **1,024 abandoned expired legacy records** drain instead of remaining in a
  repeated UUID lookup list.
- Loaded guardians and guardians changing dimensions expire at their original
  deadline. Same-UUID capture before expiry preserves remaining authority;
  returning after expiry or copying to a different UUID cannot mint a ticket.
- Actual Peaceful `Mob.checkDespawn` retires a ticket, while capture using
  `discard()` in Peaceful preserves it only until its existing deadline.
- Canceled death and revival preserve a valid ticket. The real delayed KubeJS
  reward path pays at most once and consumes authority before loot.
- A real `/reload` preserves authority and the original captured deadline.

This tests bounded indexing and encounter behavior with compressed saved data
and actual server events. It is not a multi-day population stress benchmark.
The migration/rollback requirements are in
[the deployment notes](../docs/PERFORMANCE_OPTIMIZATION.md#existing-worlds-and-migration).

## Quest Automation 0.4.3

The fixtures use native Curios, Relics, Mekanism, FTB team and quest APIs:

- **100 completed equipment-observation passes** performed **zero slot reads**.
- Already-recorded relic upgrade evidence skipped template reads. A sufficient
  first item stopped traversal after **one slot**.
- Early evidence still records before its quest gate opens. Cosmetic, inactive
  and ordinary inventory slots remain excluded; source-specific relic XP and
  paid-upgrade conditions retain their transitions.
- Current team membership is resolved on each observation. Native machine
  callbacks retain exact 16-block spherical distance, real-player/spectator
  filtering and team deduplication. The actual spectator mode setter runs.
- Machine output is evaluated once when eligible teams need iron evidence,
  and **zero times once those counters are capped**.

The observer still performs its inexpensive team/cap checks. This patch does
not claim to eliminate every Quest Automation tick or introduce a new player
spatial index.

## Dungeons & Taverns

The loaded production data, native events, commands and enchantment engine
passed all of these checks:

- **Eight** root criteria use the intended event trigger while retaining
  original criterion IDs and eight-way OR requirements. Each native discovery
  award credits the matching criterion. Login reconciliation and revoking a
  source advancement preserve historical root completion and its timestamp.
- The live boss predicate passes five visibility/tag/type cases. **20 native
  equipped-phantom enchantment ticks perform zero entity NBT saves**, with a
  test entity that fails immediately if serialization is attempted.
- The loaded trader function resets only its invoking player, verified with
  two synthetic players in the actual player list.
- The five-tick function retains the original scheduler phase. Wither equipment
  and tags, exact entity types, Overworld scope, lightning/allay equipment and
  six-block radius, once-only conversion, late arrivals and removed lightning
  all retain their intended behavior.
- Real `/reload` reconciles an existing player's discovery history and an
  already loaded unprocessed wither. The boss condition remains valid after
  reload; this is not a claim that Minecraft reloads every dynamic registry.

## Updated personal rewards and progress migration

The separate 0.4.8 campaign fixture also passed against the **same locked
production payload** and shared harness as the optimization fixture. It ran in
`.work/runtime-probe/run-20261019T134030.594760Z` for **113.15 seconds**,
using the prior shared-reward commit `72bd75d` as its upgrade baseline. This
exercises the reward branch's actual implementation alongside all server-side
optimizations, including its native crafting, team and game-mode listeners.

- All **240 catalogue items**, **326 reward entries** and **226 cited production
  recipes** resolved in the loaded pack. All **1,611 custom reward definitions**
  passed native NBT and packet serialization.
- Actual crafting, AE2 Assembler/Inscriber, Mekanism Infuser/Infusing Factory
  and consumed Sophisticated Storage upgrades recorded production. Inventory
  possession, reward delivery, creative/fake actors and nonproducing operations
  did not establish proof.
- Two synthetic members received independent personal rewards. Two-, three-
  and four-draw delivery, late joining, expensive-item production gates and
  disk-backed claim merges on team changes passed.
- The nonempty upgrade fixture preserved **41 task values**, including **15
  partial tasks**, and kept the old shared claim history. It rejected **3,250
  repeated personal claims** across two migrated members while preserving the
  existing personal book claim.
- Native AE2 terminal conditions, team production ownership and personal claim
  persistence survived real `/reload` and independent disk reads.
- Recipe counts remained **20,077 → 20,077**. KubeJS startup/server and fatal
  error counts were zero; all dimensions saved and Java exited normally.

The reward fixture uses its existing test-only packet listener, which drops
outbound interface traffic. Its reward packet serialization is tested
separately using native registry-aware buffers. This does not establish a real
client handshake, graphical rendering or a multiplayer survival playthrough.

| Additional integration evidence | SHA-256 |
| --- | --- |
| Reward fixture console | `69cfea9a9a29831b07767773b4804f540e9207156268962cb77e944decb323c7` |
| Reward test-only JAR | `ce8a42414477e5cde81449e6608a17499a386a76f55c22ab7c2681ac316da41a` |
| `metadata/reward_item_evidence.json` | `52b93305a0440b814612adeafef1a17752ff9169f643fa32f07b9a72b48944de` |
| `tests/runtime/ProductionEvidenceProbe.java` | `7bf7b72da9f5f308b6c554b8ac0c962ebd2c1fc8b621d777d6f18111e1a83895` |
| `tests/runtime/ProgressiveRewardsProbe.java` | `375269d27f7c4f916a1445cf7ec3b4d53762a0f018ba61c5c17f1bdbca2d21a0` |
| `tests/runtime/SoqmRuntimeProbe.java` | `e33286f1e86e66f1d00dc6f59962ee496b5ac72ea3fde5af1996bb05addf5978` |

## Repository and packaging gates

`./scripts/check` passed **56 Python tests**, both Node equipment/loot and
progressive-rewards policy regressions, campaign and English regeneration,
payload/provenance checks, and
installation ZIP content, CRC, hash and deterministic rebuild checks.
`python3 scripts/render_campaign.py --check` also passed. The generated archive
contains the 12 production modules; it contains no native test fixture JAR.

The installation ZIP is `dist/SOQM-0.4.8-curseforge.zip`; its exact checksum is
written to `dist/SHA256SUMS` alongside it. The archive also includes this
verification record. The existing CI workflow builds a candidate archive and
its checksum for each reviewed PR commit. A CI candidate is distinct from a
separately published release.

## Reproducible production artifacts

Both optimization JARs were rebuilt against the verified native runtime and
current 0.4.8 authored-mod classpath in the fresh `.work/integration-rebuild`
output directory. Exact binary equality with the shipped JARs passed. Original
MIT notices remain embedded. The other ten production JAR pins are unchanged
from the updated main branch, including the new progressive-rewards adapter.

| Artifact | Bytes | SHA-256 |
| --- | ---: | --- |
| `qm-quest-automation-0.4.3.jar` | 19,867 | `657799264f2cc53f08a417d0c201ca4e19d36c286385c4de8222807544df1672` |
| `qm-runtime-optimizations-0.4.7.jar` | 30,176 | `07a75c97becb0f365391ad157eecc809a6de40fe92bfb12765add26a5ac12b11` |

The mapped native server SHA-256 is
`f57ce3018e21459d24b50508e8c1b4fd5c16324ec1dc5f316c192aa0130e7fe0`. Build instructions and the narrow upstream
version requirements are in the [module README](../src/compat/runtime-optimizations/README.md).

## Input and evidence hashes

| File | SHA-256 |
| --- | --- |
| `metadata/mods.lock.json` | `df0d6d641d518d73512dbaedbe0197fdd3d2343bc0be65fa0dc038d87cd4168b` |
| `metadata/authored-mods.json` | `f0639231800bb6f2469448cfb2e5f1c58a4dde9a66bdf34fa1e0f8039210cde3` |
| `metadata/payload.sha256.json` | `e41dbd05a99c2cc39eb1adf07e2e9518458904ce25a5f46be0c8ca463f96917e` |
| `src/compat/provenance.json` | `15c5f4a2c111699564d33ca3af333b10df1edeeaf02d351ccf6ef3c692991d5c` |
| `src/compat/build_optimizations.py` | `e1c2f40bc16db0fd0c9822a10038d5e166649b849c65c3d404ed098ec5faad25` |
| `scripts/performance_probe.py` | `47e16d858a217e2c058e539d763b936df3b36a25b8087116d51db33694efb772` |
| `scripts/runtime_probe.py` | `e6e0c47b728818d5017fd9a4829009c2ab68f37c6669d45f583172839979b32b` |

| Native fixture source | SHA-256 |
| --- | --- |
| `tests/runtime/performance/PerformanceProbe.java` | `db73ef6f8ddcb4e9aed1eb5240cf49c3c52efd60a7e8cb2d53963f38f8c23edf` |
| `tests/runtime/worldgen/TunnelWorldgenProbe.java` | `10bfb21c1761283200971a7d2bcacd53c6b8c99a6f401e3502cf9a421d4b3285` |
| `tests/runtime/guardian/GuardianLifecycleProbe.java` | `7c2a41ca9f39c2ba131a3faf9e3a47ca28d25c84c9ceec57fdafd23d1332498f` |
| `tests/runtime/questauto/QuestAutomationPerformanceProbe.java` | `f3ce03e929bbb445fc6eef2fd95969a42869a8d4655f21a8af6ec51bee79f299` |
| `tests/runtime/dungeons/DungeonsPerformanceProbe.java` | `0440305e106afe70f5c76bdfcce6ef59cb3ce664460b24532f76db21f269e4f4` |

The successful console SHA-256 is `7a6326e952984d46796a7f5656cb6686f511fae5bcc49e33a79ee673e39a4343`.
The local test-only JAR SHA-256 is `aee057bf03e2c7c6a22542e212e22469f9b234a67908eceac2a115e33d2cedfe`.
Generated raw JSON reports, downloaded third-party dependencies and test worlds
remain ignored local outputs. Production sources, the two authored artifacts,
input pins and this verification record are committed.

## Reproduce the native gate

After preparing the verified dependency cache and accepting the Minecraft
EULA, use an installed pinned runtime and a Java 21 JDK:

```sh
python3 scripts/performance_probe.py \
  --runtime /absolute/path/to/installed/server \
  --java /absolute/path/to/jdk-21/bin/java \
  --accept-eula \
  --baseline-ref fc5328de31809e5d11fc0f0143e488dee6689090
```

The default localhost port is 25591. The harness compiles its isolated fixtures,
verifies pins, stages the production pack into a fresh world, runs the native
checks, reloads data and saves/shuts down. The production payload must match its
reviewed lock, and it must remain unchanged during execution. A failed fixture,
KubeJS/fatal error, mismatched recipe count or incomplete shutdown fails the
command.

To repeat the preserved 0.4.8 reward and nonempty progress-migration gate on
its separate default localhost port 25590:

```sh
python3 scripts/runtime_probe.py \
  --runtime /absolute/path/to/installed/server \
  --java /absolute/path/to/jdk-21/bin/java \
  --accept-eula \
  --baseline-ref 72bd75d131a986afe9cf0d11580fdcaf009a208a
```

That older baseline intentionally supplies shared claim history; the tested
production payload is the current 0.4.8 pack in both commands.
