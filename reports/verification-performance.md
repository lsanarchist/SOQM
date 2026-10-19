# Runtime optimization verification — SOQM 0.4.7

Validation completed on **2026-10-19** against the final production payload.
The branch is based on commit
[`72bd75d`](https://github.com/lsanarchist/SOQM/commit/72bd75d131a986afe9cf0d11580fdcaf009a208a),
which includes both the updated questbook and merged skill-advancement patch.
All **55 questbook files**, campaign identity/route/reward policy files, and the
merged skill definitions/advancements are byte-identical to that base.

**Result:** the native behavioral and work-count fixture passed in the full
pinned modpack, including real data reload and clean shutdown. Both changed
production JARs also reproduced byte-for-byte in an independent build.

## Runtime and execution

| Check | Result |
| --- | --- |
| Minecraft / NeoForge | 1.21.1 / 21.1.262 |
| JDK | Eclipse Temurin 21.0.12.1+1 |
| Third-party dependencies verified by hash and size | 122 |
| Authored modules in the production package | 11; server staging excludes client-only modules |
| Production override files | 1,846 |
| Fresh localhost world and synthetic players | Passed; no existing player world was used |
| Recipe count before and after `/reload` | 20,077 → 20,077 |
| KubeJS startup / server errors | 0 / 0 |
| Fatal server errors | 0 |
| Save and shutdown | All dimensions saved, exit 0, no forced termination |
| Payload lock matched before execution | Yes |
| Production payload unchanged during execution | Yes |
| Fixture and harness sources match the final report | Yes |

The successful local run is `.work/performance-probe/run-20261019T123622.672442Z`. Its complete staging,
compilation, startup, fixture, reload and shutdown duration was
**119.14 seconds**. That duration is not a generation benchmark.
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

## Repository and packaging gates

`./scripts/check` passed **53 Python tests**, the Node equipment/loot policy
regression, campaign and English regeneration, payload/provenance checks, and
installation ZIP content, CRC, hash and deterministic rebuild checks.
`python3 scripts/render_campaign.py --check` also passed. The generated archive
contains the 11 production modules; it contains no native test fixture JAR.

The tested installation ZIP is `dist/SOQM-0.4.7-curseforge.zip`,
**2,146,218 bytes**, SHA-256
`8dae08948a8f89dfd607ec9eaaddf16e222f018efe3316314354236e8cefa42b`.
The existing CI workflow builds a candidate archive for each reviewed PR
commit. A CI candidate is distinct from a separately published release.

## Reproducible production artifacts

Both JARs were compiled twice against the verified native runtime, using a
fresh output directory for the independent build. Exact binary equality passed.
Original MIT notices remain embedded. The other nine production JAR pins are
unchanged by this patch.

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
| `metadata/authored-mods.json` | `cd64b5b2711648c84742b61db5179266bba2c9c4ad25496591d06692ca289881` |
| `metadata/payload.sha256.json` | `84d212fdb881ba5badb821abefce80814be3fc50b8784272193ee9d5211dabbb` |
| `src/compat/provenance.json` | `ac378ae1ff4de3064a12efcbce3b047f54cf51fe9188ffa5df28417946dc397d` |
| `src/compat/build_optimizations.py` | `e1c2f40bc16db0fd0c9822a10038d5e166649b849c65c3d404ed098ec5faad25` |
| `scripts/performance_probe.py` | `47e16d858a217e2c058e539d763b936df3b36a25b8087116d51db33694efb772` |
| `scripts/runtime_probe.py` | `71accb1c9959ef0a9f5607b9cfaf9b1d8840413ef8fbd5c5ca4bc1a600f677e7` |

| Native fixture source | SHA-256 |
| --- | --- |
| `tests/runtime/performance/PerformanceProbe.java` | `db73ef6f8ddcb4e9aed1eb5240cf49c3c52efd60a7e8cb2d53963f38f8c23edf` |
| `tests/runtime/worldgen/TunnelWorldgenProbe.java` | `10bfb21c1761283200971a7d2bcacd53c6b8c99a6f401e3502cf9a421d4b3285` |
| `tests/runtime/guardian/GuardianLifecycleProbe.java` | `7c2a41ca9f39c2ba131a3faf9e3a47ca28d25c84c9ceec57fdafd23d1332498f` |
| `tests/runtime/questauto/QuestAutomationPerformanceProbe.java` | `f3ce03e929bbb445fc6eef2fd95969a42869a8d4655f21a8af6ec51bee79f299` |
| `tests/runtime/dungeons/DungeonsPerformanceProbe.java` | `0440305e106afe70f5c76bdfcce6ef59cb3ce664460b24532f76db21f269e4f4` |

The successful console SHA-256 is `475b65c7752e7f8ec991ff015847c24a786da2b86e2dc73f5149beb46d077364`.
The local test-only JAR SHA-256 is `032d485c97a1a36e2cf7d8fa8b6e62c0dc9dc75cce04d8ce296918e66913f432`.
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
  --baseline-ref 72bd75d131a986afe9cf0d11580fdcaf009a208a
```

The default localhost port is 25591. The harness compiles its isolated fixtures,
verifies pins, stages the production pack into a fresh world, runs the native
checks, reloads data and saves/shuts down. The production payload must match its
reviewed lock, and it must remain unchanged during execution. A failed fixture,
KubeJS/fatal error, mismatched recipe count or incomplete shutdown fails the
command.
