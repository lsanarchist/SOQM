# SOQM 0.4.9 verification record

**Date:** 2026-10-19. **Upgrade baseline:** [abd73f1c0736247fc5b13e0e255f8a4ef6d71b34](https://github.com/lsanarchist/SOQM/commit/abd73f1c0736247fc5b13e0e255f8a4ef6d71b34), the merged 0.4.8 personal rewards and runtime optimizations.

The final native run passed on the reviewed production payload lock with `development_payload_allowed: false`. Production files did not change during execution. This record refers to the successful run after restoration of the interrupted publication workspace, including the reconstructed test sources; it does not reuse the earlier run's fingerprints.

## Change boundary

The [authored branch sources](../metadata/quest_branches/) reorganize **417 existing quests into 63 sections across nine chapters**. A supporting RFTools Crafter Tier 1 correction brings the total to **418 quest patches in ten chapters**. The sources declare **29 connected routes** and supply **268 bilingual quest descriptions**. Together with retained tutorials, the book contains **285 distinct authored lessons**; the 50 overlapping older lessons are counted once.

All **1,625 quests, 1,772 complete task objects and 1,626 complete reward objects** retain their IDs, objectives, ownership and claim settings. The canonical task/reward/repeatability contract is unchanged from the Git baseline, with SHA-256 `cbc3edbaf2ff9373a3ca8fb2e33488bc589e80e90859ee4e1cbda90934b87be9`. Existing chapter, group, image and caption IDs remain intact. No new persistent quest objects are allocated.

Only **13 game-payload files** change: eleven chapter files and the two locales. This includes the intro chapter's derived role/layout adjustment and the single RFTools correction. No recipe, production script, third-party pin or authored binary changes. The **122 third-party pins, 12 authored JARs and 1,835 payload paths** match the merged baseline. The retained reward catalogue has **14 pools, 326 entries and 240 distinct items, including 210 modded items**, with exact manufacture gates and personal 2/3/4-draw delivery.

## Executed offline checks

`./scripts/check` passed **63 Python tests**, both Node policy suites, payload and localization validation, generator freshness, installation ZIP CRC/content/hash checks and byte-identical rebuilding on the same Python/zlib runtime.

The seven new branch regressions check the complete saved-object contract, ancestry of every declared route, early ME storage and autocrafting before Matrix, realistic sieve/hammer requirements, Item Buffer/Comparator and ME return dependencies, complete map coverage and bilingual source precedence. Existing tests retain coverage for material gates, cycles, missing dependencies, idempotence, skills, runtime harness dispatch and personal reward policy behavior.

`python3 scripts/render_campaign.py --check` passed for all **ten** committed coordinate previews: the main route and all nine revised chapter maps. Temporary Inkscape rasterizations of all nine chapters or their relevant sections were visually inspected before workspace restoration. All eleven modified chapter files and all ten regenerated SVG files subsequently matched their pre-interruption Git blob hashes exactly. These are coordinate previews with neutral numbered markers, not Minecraft screenshots.

The final installation candidate, including this verification record, is built and checked again by the PR workflow. Its checksum is supplied separately in `dist/SHA256SUMS`, avoiding a self-referential archive hash. Compression output can differ between Python/zlib versions; use the checksum accompanying the specific candidate.

### Layout geometry

An independent check used each visible local dependency segment and the axis-aligned rectangle of every unrelated icon: `x/y ± (size / 2 + 0.1)`, Liang–Barsky with `lo < hi`. The counts measure lines through unrelated icon areas, not line-to-line crossings. Branch dependency lines remain enabled.

| Chapter | Quests | Visible local edges | Icon-area intersections | Upward edges |
| --- | ---: | ---: | ---: | ---: |
| Ex Deorum | 28 | 26 | 0 | 0 |
| Ex Machinis | 10 | 9 | 0 | 0 |
| AE2 | 80 | 96 | 0 | 0 |
| Mekanism | 88 | 108 | 10 | 0 |
| Nuclear Mekanism | 60 | 68 | 0 | 0 |
| Ender IO | 56 | 71 | 0 | 0 |
| Industrial Foregoing | 40 | 47 | 0 | 0 |
| Extreme Reactors | 25 | 25 | 0 | 0 |
| Mob Grinding Utils | 30 | 32 | 0 | 0 |

Mekanism retains ten intersections on long equipment and logistics prerequisites. Some lines cross other lines. The map is not claimed to be crossing-free. Opaque caption backplates keep text readable; an opened quest lists all prerequisites, including links to other chapters.

## Final native execution

The temporary Java fixture compiled against pinned Java **21.0.12.1**, Minecraft **1.21.1**, NeoForge **21.1.262**, FTB Quests **2101.1.36**, FTB Teams **2101.1.9** and AE2 **19.2.18** APIs. A fresh full-pack localhost server then executed the following checks against the final locked payload.

| Check | Executed result |
| --- | --- |
| Fresh dedicated-world startup | Passed |
| ME Chest with one 1k cell, without Drive or Controller | Real terminal output click passed |
| Ordinary crafting menu, unpowered ME and empty ME storage | All rejected for the ME milestone |
| Actual ME advancement and existing native FTB task | Earned and completed |
| Unrelated ME storage and advancement disk/reload state | Preserved |
| Existing personal reward claims retained | 1,626 |
| Repeat claims rejected for the original claimant | 1,626 |
| Personal claims independently read back from disk | 1,626 retained |
| New personal entitlements issued by the 0.4.8 → 0.4.9 migration | 0 |
| Second UUID's independent personal entitlement | Preserved |
| Completed Mechanical Sieve, ME Chest and actual-ME-craft quests | All three retained before and after disk reload |
| Main nonempty upgrade fixture task values | 43 retained, including 15 partial tasks |
| Separate reward fixture task values | 40 retained, including 15 partial tasks |
| Native reward NBT and registry-aware packet round trips | 1,611 custom rewards |
| Catalogue items / manufactured item IDs / cited recipe IDs in the live registry | 240 / 202 / 226 |
| Actual production reward catalogue | 326 entries, no fallback |
| Two-player independent rolls, late-joining first claim and duplicate rejection | Passed |
| Reward delivery at levels 0 / 2 / 6 | 2 / 3 / 4 draws |
| Unmanufactured chassis exclusion and real-craft unlock | Passed |
| Offline leave/join claim merge, team change and disk-backed repeat rejection | Passed |
| Production facts, ownership and personal claims after resource reload | Preserved |
| Recipes before and after reload | 20,077 → 20,077 |
| KubeJS startup / KubeJS server / fatal server errors | 0 / 0 / 0 |
| Saved dimensions and normal server exit | Yes, code 0 |
| Forced termination | No |
| Production payload changed during execution | No |
| Elapsed time | 113.26 seconds |

The retained manufacture regression executes actual crafting-grid chassis production, AE2 Inscriber and Molecular Assembler finishes, Mekanism Infuser and Infusing Factory finishes, and a Sophisticated Storage in-world barrel upgrade with consumed inputs. It rejects possession/pickup, creative and fake crafts, unpowered or blocked machines, prefilled output, Name Press operations and invalid ownership. Machine owner NBT, native FTB Teams disk state and party join/leave behavior are exercised as in the 0.4.8 fixture.

The upgrade scenario starts from an already personal 0.4.8 save. It writes that nonempty baseline to disk before loading it with the new graph; an unchanged claim need not create new dirty state. The older shared-to-personal transition remains supported by the harness but was not this run's baseline. Its historical executed record is in [verification-0.4.8.md](verification-0.4.8.md).

## Evidence fingerprints and reproduction

| File or executed evidence | SHA-256 |
| --- | --- |
| `metadata/payload.sha256.json` | `a3b57eab43c9684859e01ceac2cc34e4f80f5176455afa55908debc96eef7454` |
| Executed raw `reports/runtime-probe.json` | `0cef6874900df233a2b34371be0b459a2222923af87a1fa1326445b323327c37` |
| `scripts/runtime_probe.py` | `9611a769d9915b4955ce420b704f7f6e99044adb1a17dc421055a8417b7efe6c` |
| `tests/runtime/SoqmRuntimeProbe.java` | `bbe97610445f567ec4bab829425864be3b47e48aa56afab291427e1ea86ae607` |
| `tests/runtime/ProgressiveRewardsProbe.java` | `4e3952572e9889f9a902b743c5b3637d2c152e250a41a320f7e358f8a728ab33` |
| `tests/runtime/ProductionEvidenceProbe.java` | `7bf7b72da9f5f308b6c554b8ac0c962ebd2c1fc8b621d777d6f18111e1a83895` |
| Mapped native server JAR | `f57ce3018e21459d24b50508e8c1b4fd5c16324ec1dc5f316c192aa0130e7fe0` |
| Native server console | `052953fbc878ee7ab1f23913facf45e96923ceff17b210ca4c900606d187dff4` |
| Test-only mod | `0d25e9502a8e8ee7090473f21df647f75d91ecadaf68728ad089b8937903df88` |

Executed native workspace: `.work/runtime-probe/run-20261019T170333.725712Z`. Raw reports, logs, temporary rasterizations, test-only JARs and worlds are ignored local evidence; workspace maintenance can remove them. The committed sources, fixtures and pinned inputs reproduce the checks:

```sh
./scripts/check
python3 scripts/render_campaign.py --check
python3 scripts/runtime_probe.py --accept-eula \
  --java /path/to/java21/bin/java \
  --runtime /path/to/installed-neoforge-server \
  --baseline-ref abd73f1c0736247fc5b13e0e255f8a4ef6d71b34 --timeout 600
```

See [the native harness guide](../docs/RUNTIME_VALIDATION.md) for setup and [the branch guide](../docs/QUEST_BRANCHES.md) for exact recipe/code evidence. GitHub CI runs offline and package gates and uploads the installation candidate; the full native execution recorded here is a separate local gate.

## Scope and remaining limits

Native tests use synthetic players, supplied materials, direct placement and test power. The ME Chest check executes a real manual terminal craft; the assembler check executes a real production finish. Neither is a complete survival playthrough or a submitted autonomous pattern job through the entire new quest route. Inventory objectives remain inventory checks; new instructions explain assembly and manual operating checks separately.

Ex Deorum and industrial instructions were checked against pinned code, configuration and pack recipes. This run does not execute every machine or recipe described by all 268 lessons. All cited reward recipe IDs resolve, but only the named manufacture paths are physically executed. No graphical client, live two-client session or long-term balance result is claimed.

Pinned Ex Machinis can discard unaccepted random output when its receiver fills. The new lessons explain receiver capacity, output extraction and stopping input; this questbook change does not patch that third-party binary or claim lossless unattended operation.

Known nonfatal upstream warnings remain; zero gate errors do not mean a warning-free console. Runtime optimization binaries from the merged baseline are unchanged. Their [earlier verification](verification-pr5.md) remains the performance evidence; this questbook update makes no new TPS, FPS or total world-generation speedup claim.
