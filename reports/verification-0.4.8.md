# SOQM 0.4.8 verification record

**Date:** 2026-10-19. **Upgrade baseline:**
[`72bd75d131a986afe9cf0d11580fdcaf009a208a`](https://github.com/lsanarchist/SOQM/commit/72bd75d131a986afe9cf0d11580fdcaf009a208a).

This record summarizes executed checks of the progressive personal-reward
update. The final native run used the reviewed payload lock, without
`--allow-unlocked-payload`, and verified that production files did not change
during execution. Reproducing the commands creates fresh evidence rather than
reusing a historical `passed` flag. Installation-archive checksums are supplied
beside the archive because this document is included in the archive itself.

## Change boundary and offline gates

- **53 Python tests** passed, including catalogue validation, exact production
  requirements, probability normalization, nondecreasing unlocked item sets,
  claim-scope migration, persistent IDs, bilingual player guidance, campaign
  dependencies, layout, generator idempotence and packaging boundaries.
- Both Node regressions passed: the existing equipment/accessory/loot policy
  and the actual progressive-reward script with the generated production JSON.
  The reward checks cover all **14 themes**, **326 entries**, **1,611 mapped
  custom rewards**, native 64-bit IDs kept as strings, two-stage weighted draws,
  2/3/4-draw delivery, independent personal rolls, duplicate-item combining,
  malformed-policy behavior and read-only Ukrainian/English commands.
- The catalogue contains **240 distinct items**, including **210 distinct
  modded items**. There are **240 production-gated catalogue positions** for
  **202 distinct manufactured items**, plus **86 starter positions**. Each
  theme has at least five starter options. Plastic, HDPE and nested Mayhem
  Cubes are absent from the random pools.
- All **1,626 outer reward IDs** remain attached to their original quests and
  are personal: **1,611 custom packs**, **14 fixed Mayhem Cubes** and the
  existing personal book. Each uses the pinned FTB Quests manual-claim field
  `auto: "disabled"`. Quest, task, dependency and layout data outside rewards
  are structurally identical to the baseline across all **37 chapters**.
- Campaign generation, English-guide generation and both coordinate SVGs are
  current. The Skills, Cannibal, early-AE2 and campaign-layout fixes already
  merged into the baseline are preserved.
- All **122 third-party pins** passed size, SHA-256 and archive checks. The
  game payload contains **1,832 files**, including **11 authored JARs**.
  Only the new progressive-rewards module is added to the authored allowlist;
  existing production binaries retain their pins.
- The new **19,880-byte** `qm-progressive-rewards-0.4.8.jar` compiled against
  the pinned native APIs and was rebuilt byte-identically. Its source
  provenance covers all 13 module files. This does not establish independent
  recompilation of the ten pre-existing authored modules.
- Installation packaging checks CRC, exact file contents and hashes, the
  authored-JAR allowlist and byte-identical rebuilding on the same
  Python/zlib runtime. The final documentation is included in that gate.

## Final native execution

Java **21.0.12.1**, Minecraft **1.21.1**, NeoForge **21.1.262**, FTB Quests
**2101.1.36**, FTB Teams **2101.1.9**, AE2 **19.2.18**, Mekanism **10.7.19**
and the pinned Sophisticated Storage **1.6.7.2184** archive were used. The
storage mod advertises native version `1.6.7`; the adapter's dependency range
accepts that native version while build and download metadata pin the exact
archive. The harness compiles three temporary Java fixtures into a test-only
mod, which is excluded from the installation payload.

| Check | Executed result |
| --- | --- |
| Fresh dedicated-world startup | Passed |
| Catalogue items resolved in the live registry | 240 |
| Manufactured catalogue IDs resolved in the live registry | 202 |
| Cited production recipe IDs present after KubeJS recipe changes | 226 |
| Actual catalogue loaded by the production reward script | 326 entries, no emergency fallback |
| Native custom-reward NBT and registry-aware packet round trips | 1,611 |
| Native outer rewards using personal claim scope | 1,626 |
| Independent personal claim and random roll for two UUIDs | Passed |
| Late-joining member's first personal claim and duplicate rejection | Passed |
| Level 0 / level 2 / level 6 reward delivery | 2 / 3 / 4 draws |
| Expensive chassis without its own production evidence | Excluded, even at a higher reward level |
| Actual chassis craft followed by eligible prize delivery | Passed |
| Old shared claim keys retained with one new personal entitlement | 1,625 |
| Duplicate personal claims rejected across two migrated members | 3,250 |
| Existing personal book claim retained | Passed |
| Nonempty upgrade fixture task values retained | 41, including 15 partial tasks |
| Separate personal-reward fixture task values retained | 40, including 15 partial tasks |
| Ordinary crafting menu / unpowered ME / empty ME storage for the ME milestone | All rejected |
| Actual powered, stocked AE2 terminal output click | Earned advancement and satisfied the native quest task |
| Unrelated ME storage | Preserved |
| Independent advancement and nonempty TeamData disk readers | Passed |
| Offline leave and subsequent party join claim merges saved to disk | Passed |
| Repeat personal claim after disk-backed team switch | Rejected |
| Resource reload with production facts and personal claims | Preserved |
| Recipes before and after reload | 20,077 → 20,077 |
| KubeJS startup / KubeJS server / fatal server errors | 0 / 0 / 0 |
| Dimensions saved and normal server exit | Yes, code 0 |
| Forced termination | No |
| Production payload changed during execution | No |
| Elapsed time | 112.23 seconds |

### Actual manufacture and team ownership

The staged fixture consumes real native recipe inputs and checks the produced
outputs. These are executed operations, not synthetic calls that insert facts
into the production ledger:

| Production path | Verified manufacture |
| --- | --- |
| Native crafting grid | Custom industrial chassis |
| AE2 Inscriber | Processor |
| AE2 Molecular Assembler | Custom precision mechanism |
| Mekanism Metallurgic Infuser | Basic control circuit |
| Mekanism Infusing Factory | Infused alloy |
| Sophisticated Storage in-world tier upgrade | Iron barrel, with upgrade-item consumption |

The same execution rejects inventory possession, pickup, recipe-book unlock,
creative crafting, FakePlayer crafting, a same-item reset, unpowered machines,
blocked output and a prefilled assembler output slot. It also performs a
successful native AE2 Name Press operation and verifies that this does not
count as manufacture. Creative machine placement followed by a switch to
survival does not establish a legitimate production owner.

Party join and leave checks verify the union of current members' own facts,
retention of the departing player's own facts, retention of historical party
facts, and isolation of a newly created party from an old copied union.
Machine ownership records the placing player's UUID and team. A machine
bound to a previous party cannot open a new prize for that player after a
team change. Breaking and legitimately placing the machine again binds it
to the new team; its next real finish then counts. Machine-owner native NBT
round trips and independent FTB Teams disk reads for three players passed.
The effective public production set is immutable.

### Intentional old-world transition

The old shared claim key contains a nil UUID and cannot identify the actual
recipient. This update retains those historical keys and intentionally gives
each member one new personal claim for a formerly shared reward on a completed
quest. Existing personal claims stay used. Further claims remain blocked for
that player, including after leaving a party, joining another party and loading
the saved state from disk. This is a deliberate entitlement change, not a claim
history reset. Quest and partial-task progress are preserved.

Old worlds do not receive invented production history from inventories or
completed quests. Manufacture one new sample of an item after the update to
open its prize. After changing teams, replace your production machines in
survival or adventure mode to bind future output to the current team. Both
rules are explained in the questbook, installation guides and
[reward documentation](../docs/REWARDS.md).

## Evidence fingerprints

| File or evidence | SHA-256 |
| --- | --- |
| `metadata/payload.sha256.json` | `8780a087cb1eb7940fda024965cc2f5e97818a5629a8555c19f238faa47bf070` |
| Executed raw `reports/runtime-probe.json` | `7a8361851b41f0b563fd473a9e9814f7dfa15b681fa4a02b7a2c9a64507aa813` |
| `scripts/runtime_probe.py` | `57ea2aff5415ca942b82e96734f7f85d4e36a2a9a4d845d1c9f2fb33d246ca64` |
| `tests/runtime/SoqmRuntimeProbe.java` | `e33286f1e86e66f1d00dc6f59962ee496b5ac72ea3fde5af1996bb05addf5978` |
| `tests/runtime/ProductionEvidenceProbe.java` | `7bf7b72da9f5f308b6c554b8ac0c962ebd2c1fc8b621d777d6f18111e1a83895` |
| `tests/runtime/ProgressiveRewardsProbe.java` | `375269d27f7c4f916a1445cf7ec3b4d53762a0f018ba61c5c17f1bdbca2d21a0` |
| Generated production reward policy | `663a54748a180ad701ae74e71cebf202f40136163fa522c990012dae6a5c1abb` |
| Production reward script | `794752b83706ab90a61abaa998395334982c69866bbfc29b3395cf774ecd3dd0` |
| `metadata/reward_pools.json` | `b9749a66cc3e27c660751bdc5db89aa9feff36078b608a48da60fe3bdcbf8778` |
| `metadata/reward_item_evidence.json` | `52b93305a0440b814612adeafef1a17752ff9169f643fa32f07b9a72b48944de` |
| Production progressive-rewards JAR | `fe1f74c356edeee94a362a1f1a25c478dab7c488b56ee706d18221a15c1de17b` |
| Mapped native server JAR | `f57ce3018e21459d24b50508e8c1b4fd5c16324ec1dc5f316c192aa0130e7fe0` |
| Native server console | `851439b4c7270b1c71573c3cbb4ea3947395d9c913e59500cbb2564701de35b5` |

Local native workspace: `.work/runtime-probe/run-20261019T125245.798012Z`.
Raw reports, logs, test-only JARs and runtime worlds are ignored local evidence.
The committed harness, fixtures and pinned inputs reproduce the checks:

```sh
./scripts/check
python3 scripts/render_campaign.py --check
python3 scripts/runtime_probe.py --accept-eula \
  --java /path/to/java21/bin/java \
  --runtime /path/to/installed-neoforge-server \
  --baseline-ref 72bd75d --timeout 600
```

See [the native harness guide](../docs/RUNTIME_VALIDATION.md) and
[adapter build instructions](../src/compat/progressive-rewards/README.md).

## Scope and limits

The catalogue registry and recipe checks establish that all 240 item IDs and
226 cited recipe IDs exist in the running pack. They do not mean every one of
the 226 recipes was physically crafted. The table above names the executed
production paths. The manual-ME production hook was additionally inspected
against pinned AE2 bytecode: the positive-output crafting event carries the
crafting inputs before consumption. The ME fixture separately executes a real
terminal output click; it does not separately assert an allowlisted item unlock
from that terminal.

The server uses synthetic players, supplied materials, placed infrastructure
and test power to isolate conditions. An in-memory Netty connection and a
test-only player listener discard outbound UI packets without fabricating a
client handshake or acknowledgement. The reward NBT and packet round trips
still use the actual native serialization APIs. No graphical client, live
two-client session, complete survival playthrough or long-term reward-economy
balance test is claimed.

Known nonfatal upstream diagnostics and default-configuration messages remain;
the zero-error counts above do not mean a warning-free console. No FPS/TPS or
travel-performance improvement is claimed. Millénaire generation and existing
simulation behavior remain outside this reward change; the separate
[exploration performance notes](../docs/RUNTIME_VALIDATION.md#exploration-performance-remains-a-separate-experiment)
explain the outstanding measurement work.
