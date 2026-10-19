# PR #5 — combined SOQM 0.4.8 verification

Validation completed on **2026-10-19** after repairing merge commit
[`37fdb1e`](https://github.com/lsanarchist/SOQM/commit/37fdb1e7521e0861e2afd86926a4a423831f4e24).
The base is current main
[`fc5328d`](https://github.com/lsanarchist/SOQM/commit/fc5328de31809e5d11fc0f0143e488dee6689090).
This report covers the combined personal-reward and runtime-optimization payload.
The concurrent integration commit
[`5b59c3a`](https://github.com/lsanarchist/SOQM/commit/5b59c3ac44dbffb2bbc1d079f67d635d3e420d02)
was preserved before applying the additional native fix. Its earlier results
remain in [the integration record](verification-performance.md).

## Repair and preservation

The conflict resolution retained the runtime JAR and KubeJS overrides but lost
its authored-mod and source-provenance entries, plus four payload-lock entries
(two function additions, the changed guardian script and the new JAR). The
`campaign` CI gate therefore stopped with `Payload changed`. The merge also
removed the compiler/source/workspace parameters from `runtime_probe.run`, while
`performance_probe.py` still passed them, causing an immediate `TypeError`.

Integration commit `5b59c3a` restores those records and harness hooks; this
follow-up preserves that repair while retaining
the 0.4.8 manufacture, personal-claim, team-transition and persistence fixtures.
All **41 questbook files**, all **13 progressive-reward module source files**,
the progressive-reward JAR, KubeJS reward policy/script, production recipe
evidence, reward pools and campaign identity remain byte-identical to `fc5328d`.

The Dungeons dispatcher also now distinguishes actual entity removal from
tracking visibility loss. Hidden registered entities remain pending without
running functions and resume when visible; canceled and duplicate-UUID insertions
are removed before they can execute effects or remain in the dispatcher. A
small native field accessor avoids a global entity scan or a new polling queue.
The original five-tick phase, Overworld scope and dungeon functions are retained.

## Offline and package gates

- `./scripts/check`: **56 Python tests**, both Node policy regressions,
  deterministic campaign/localization generation, provenance and payload checks,
  installation-ZIP CRC, exact contents, hashes and deterministic rebuild passed.
- Three new harness regressions cover performance callback selection and
  provenance, the default multi-source reward compiler and authored classpath,
  recipe-evidence/baseline staging, and rejection of invalid or modified inputs.
  These stop before JVM launch; the native runs below validate game behavior.
- `python3 scripts/render_campaign.py --check` passed.
- **122** pinned third-party dependencies, **12** authored modules and
  **1,835** override files verified. Test JARs, runtime worlds and dependency
  caches are excluded from the installation package.

The installation archive is `dist/SOQM-0.4.8-curseforge.zip`. Its exact checksum
is written to `dist/SHA256SUMS` after the documentation is finalized; the CI
candidate provides both files. This verification record is also included in
the installation ZIP.

## Two fresh full-pack native runs

Both runs used Minecraft **1.21.1**, NeoForge **21.1.262**, Temurin Java
**21.0.12.1+1**, the exact reviewed payload, synthetic players and separate
localhost-only worlds. Both performed a real `/reload`, retained
**20,077 → 20,077 recipes**, recorded **zero KubeJS startup/server or fatal-server
errors**, saved all dimensions and exited with **code 0** without forced
termination. The production payload and recorded fixture sources matched the
final files and remained unchanged during execution.

| Gate | Local run | Result |
| --- | --- | --- |
| Personal rewards, manufacture and migration | `.work/runtime-probe/run-20261019T135927.639381Z` | Passed; 128.08 seconds |
| Runtime behavior and work counts | `.work/performance-probe/run-20261019T135929.548274Z` | Passed; 122.09 seconds |

Durations include compilation, staging, startup, fixtures, reload and shutdown;
they are not gameplay-performance benchmarks.

### Personal-reward and saved-progress checks

The combined payload loaded **240 catalogue items** and **226 cited recipe
IDs**, round-tripped **1,611 custom rewards** through native NBT/network
serialization, and exercised actual crafting, AE2, Mekanism and Sophisticated
Storage manufacture. Independent personal claims and 2/3/4-draw payouts passed.
Unproduced expensive prizes remained excluded until actual manufacture.

The nonempty upgrade fixture, using baseline `72bd75d`, preserved **41 task
values**, including **15 partial tasks**, retained existing personal book claims,
and rejected **3,250 duplicate personal claims** for two migrated members.
Party joining/leaving, machine ownership, claim merges saved to disk and
production/claim persistence through `/reload` also passed.

### Runtime and Dungeons regressions

- Real `TRACKED → HIDDEN → TRACKED` transitions emit no replacement join event.
  Hidden withers/lightning performed no dungeon function, then the same visible
  instances resumed equipment and allay-conversion behavior.
- Native fresh and legacy insertion canceled by a later-priority listener, and
  legacy duplicate-UUID insertion, produced no dungeon effects or retained
  dispatcher entry. The legitimate entity with the duplicate UUID remained valid.
  The fixture also covers the legacy path that sets `isAddedToLevel=true` even
  after rejecting an insertion.
- Existing discovery, login/reload reconciliation, invoking-player trader reset,
  five-tick phase, entity-type/dimension scope, lightning radius/late arrivals,
  removed entities and once-only conversion passed.
- Equipped phantom checks performed **20 native ticks with zero entity NBT saves**.
- **100** completed equipment observation passes performed **zero slot reads**;
  capped machine-output checks performed **zero output reads**. Early evidence,
  current-team lookup, native recipe distance and spectator filters passed.
- Guardian deadlines, migration/restart, capture, Peaceful despawn, canceled
  deaths, at-most-once payouts and reload passed. **1,024** abandoned legacy
  records drained; **10,000** deadline replacements retained a bounded index.
- **512** native tunnel-method comparisons retained identical piece NBT and RNG
  across **374 generated** and **138 rejected** sites. Height calls remained
  **463,220 → 437,298**, removing **25,922 (5.596%)** duplicate queries. Four native
  terrain cases and cache context, capacity, nested/exception and worker-isolation
  checks also passed. These are method-level call counts, not total generation
  time, TPS or FPS measurements.

## Production artifact reproduction

The deterministic native builder reproduced both the repaired runtime module
and Quest Automation byte-for-byte in separate build directories. Progressive
Rewards remains unchanged from main.

| Artifact | Bytes | SHA-256 |
| --- | ---: | --- |
| Runtime Optimizations 0.4.7 | 31,046 | `538ec28ceef21f45452d4653f06a5a20b8ac68ab86f1e20609cc596d874bbb5b` |
| Quest Automation 0.4.3 | 19,867 | `657799264f2cc53f08a417d0c201ca4e19d36c286385c4de8222807544df1672` |
| Progressive Rewards 0.4.8 | 19,880 | `fe1f74c356edeee94a362a1f1a25c478dab7c488b56ee706d18221a15c1de17b` |

## Evidence and reproduction

The raw reports and disposable worlds remain local; the following hashes pin
the records and inputs used for this verification.

| Evidence | SHA-256 |
| --- | --- |
| `reports/runtime-probe.json` | `0731ac97eed3c8bf465461d8b7eb175945452a908c3bfaced0b4fd1cbd5927a0` |
| `reports/performance-probe.json` | `d4312a7c0f34b99a8a83f517d761a848545c4dc0c983bd60aac7dbc62e683ed0` |
| `metadata/payload.sha256.json` | `9d3d2cac6caa7a5ca9a5f1629dad1ea2b590aa41619f4f95f3b05f2fa9a42c41` |
| `metadata/mods.lock.json` | `df0d6d641d518d73512dbaedbe0197fdd3d2343bc0be65fa0dc038d87cd4168b` |
| `metadata/authored-mods.json` | `966462f5c612e8ff678dcb4e7fa2549ae457a684076335b1d144619b6e1001cf` |
| `src/compat/provenance.json` | `2e9b52ad9aaea443d46c132f2c1b5a9fa3765a6c8aa31c94f2a010a6c44bc41e` |
| `scripts/runtime_probe.py` | `e6e0c47b728818d5017fd9a4829009c2ab68f37c6669d45f583172839979b32b` |
| `scripts/performance_probe.py` | `47e16d858a217e2c058e539d763b936df3b36a25b8087116d51db33694efb772` |
| `tests/test_runtime_probe_hooks.py` | `f67f0aff4b49ffb1e37474bfe012a8205dd11a11dcea99dfd41f0cd51a24e071` |
| `tests/runtime/dungeons/DungeonsPerformanceProbe.java` | `9ada9dc071985c1441ec097078e2cfc81962bfd063edc999d536e271daa323c6` |

Prepare the pinned Java/runtime/dependency cache as described in
[native validation](../docs/RUNTIME_VALIDATION.md), then run:

```sh
./scripts/check
python3 scripts/render_campaign.py --check
python3 scripts/runtime_probe.py \
  --accept-eula --baseline-ref 72bd75d \
  --java /absolute/path/to/jdk-21/bin/java \
  --runtime /absolute/path/to/pinned/server
python3 scripts/performance_probe.py \
  --accept-eula --baseline-ref 72bd75d \
  --java /absolute/path/to/jdk-21/bin/java \
  --runtime /absolute/path/to/pinned/server
```

The performance and reward probes share the same staging/preflight/reload
harness and select separate native fixtures. Full graphical-client/login
validation, survival playthrough, long-term economy and measured travel
performance remain outside these runs. The error counts do not imply a
warning-free upstream modpack console.
