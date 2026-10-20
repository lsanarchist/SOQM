# SOQM 0.4.10 — recipe refunds, HUD coordination and storage migration

**Validated:** 2026-10-20. **Upgrade baseline:**
[`1a7ecd3b5691888bc807667eb90cbd913e191934`](https://github.com/lsanarchist/SOQM/commit/1a7ecd3b5691888bc807667eb90cbd913e191934).

This candidate adds guaranteed ingredient refunds for expensive crafting,
coordinates overlapping HUD panels, removes Iron Chests in favour of
Sophisticated Storage, and tests the previous multiblock material objectives
against the pinned native FTB implementation. It does not automatically convert
legacy containers in an existing world.

## Content and saved-object contract

| Measure | Candidate |
| --- | ---: |
| Chapters / quests / tasks | 37 / 1,626 / 1,911 |
| Existing personal outer reward IDs | 1,627 |
| Quests with guaranteed recipe refunds | 41 |
| Progressive packs / Mayhem Cube milestones | 1,433 / 193 |
| Native custom reward objects | 1,442 |
| Sophisticated Storage task retargets | 6 |
| Pinned third-party mods / authored modules | 124 / 12 |
| Locked game payload files | 1,837 |

The custom-object count includes nine bundles that retain their existing cube
alongside a refund. There are 184 ordinary cube rewards plus those nine bundles;
the semantic cube total remains 193. All outer IDs, owners, personal scopes,
manual-claim flags and one-time behavior match the baseline.

The exact six task changes and 41 reward changes are recorded in
[`storage_refund_compatibility.json`](../metadata/storage_refund_compatibility.json).
Each approved `before` object matches the actual baseline, and each `after_sha256`
matches the candidate. Reversing this overlay reconstructs all baseline task and
reward definitions. Earlier migration contracts remain unchanged. The persistent
identity fingerprint remains
`17b49fe75ee12d853c098b43bf98f080f1393780e818e7854555a63263fd245d`.

## Guaranteed refunds and claim safety

The refund catalogue was checked against authored recipes and the native recipe
manager. **34 rewards return 25–50% of one repeated direct ingredient**. Seven
return one indivisible secondary component while at least two other distinct
campaign components remain consumed. These percentages describe the selected
ingredient, not the value of the entire recipe. The full catalogue returns 42
fixed item units across 41 one-time personal claims.

| Example crafted output | Fixed return | Direct recipe consumption |
| --- | --- | --- |
| ME Drive | 1 Engineering Processor | 2 processors |
| Digital Miner | 1 Teleportation Core | 2 cores |
| Netherite Furnace | 1 Netherite Ingot | 2 ingots |
| Dense Energy Cell | 2 AE2 Energy Cells | 6 cells |
| 256k ME Storage Component | 1 64k component | 3 components |
| Supermassive QIO Drive | 1 Time-Dilating QIO Drive | 3 drives |

Thirty-two refunds accompany the existing progressive pack; nine accompany the
existing cube. The guaranteed component does not depend on a random draw. The
3/4/5-draw policy, production eligibility and existing quality floor remain.
Both Ukrainian and English quest descriptions state the exact refund and claim
requirement. See [REWARDS.md](../docs/REWARDS.md) and
[recipe_refunds.json](../metadata/recipe_refunds.json) for all entries and primary
Craftoria / official Questing Mayhem 1.0.3 archive evidence. The requested
“by Spirit” attribution was not established from that official archive.

The native claim gate checks real manufacture of the quest output by the player
or team before FTB marks the reward claimed. Invalid policy, unknown mappings,
wrong quest parents and premature claims are rejected without consuming the
claim. A scoped native claim context prevents a duplicate or direct custom
reward event from paying again. Reward delivery does not manufacture an item
for production-unlock purposes.

The native run checked **43 actual recipe costs** for the 41 outputs, two
independent personal refunds in one team, fixed-plus-random delivery, exact cube
preservation, late joining and reload. It also exercised real Iron Furnaces
world upgrades and rejected wrong-tier, empty-hand, wrong-item, fake-player and
creative attempts. Existing AE2, Mekanism and Sophisticated Storage manufacture
checks passed. The production identity allowlist contains 242 outputs.

Already claimed rewards stay claimed; there is no retroactive second payout.
Old completed but unclaimed quests for the 40 newly observed output types need
a real craft/upgrade after this update unless the current team already has
such post-update evidence. Both quest locales explain the manufacture
requirement; REWARDS.md and the installation notes explain this upgrade caveat.

## Multiblock correctness and cost

`MultiblockMaterialsProbe` exercises the actual `ItemTask`, `TeamData`, inventory
summary and deferred FTB inventory listener in the pinned dedicated server.
It passed **166 material conditions across 35 construction kits**, including the
123-glass requirement spread across legal inventory stacks. Four repeated
submissions of an insufficient amount do not accumulate a false completion.
Exact quantities, renamed component-bearing items, non-consumption, retained
progress after removal and native SNBT round trips passed.

The **22 separate formation/operation conditions** are not granted by possessing
the construction materials. Existing completed-quest timestamps remain intact.
The native inventory listener coalesced **101 slot-change notifications into one
pending check**, with its original 20-tick deadline retained. It builds one
inventory summary, then uses item lookups and skips completed tasks. This work
does not scan block structures or load chunks. The whole same-tick correctness
and benchmark section added **zero loaded chunks**.

The following measurements are from the final exact-payload run
`20261020T083246.413723Z`, Java 21.0.12.1, NeoForge 21.1.262. Each case has 1,000
samples after 300 warmups; baseline/current samples alternate in pairs.

| Native operation | p50, ms | p95, ms | Maximum, ms |
| --- | ---: | ---: | ---: |
| 1,469 baseline item tasks, one empty-inventory summary | 0.171418 | 0.218900 | 1.336535 |
| 1,607 current item tasks, one empty-inventory summary | 0.187012 | 0.230770 | 45.157594 |
| Actual full-book inventory listener | 0.142034 | 0.193822 | 0.826426 |
| All item tasks already completed | 0.011707 | 0.020361 | 0.259552 |

The paired current-task median is 0.015594 ms above baseline. The isolated
45.16 ms maximum in the synthetic current scan is retained here; this run does
not establish its cause. The actual listener path stayed below 0.83 ms in this
sample. These are server-thread inventory-dispatch measurements with an empty
inventory, not a populated-base simulation, TPS guarantee, client FPS result or
proof that every physical multiblock layout has been built in survival.
Construction geometry evidence and those separate limits remain in
[MULTIBLOCK_MATERIALS.md](../docs/MULTIBLOCK_MATERIALS.md).

## HUD verification

The final `qm-quest-ui-0.4.3.jar` was compiled against the exact mapped client and
rebuilt independently **byte-identically**. Actual Sponge Mixin 0.8.7 transformed
all **15 target classes** from the production configuration, including retained
quest ingredient shortcuts. Production `HudGeometry` passed **394 assertions**
at logical GUI sizes **320×180, 640×360 and 960×540**.

Xaero uses the upper-right area and accounts for its information lines, potion
effects and native toast reservations. Ultimine moves below left-side equipment
indicators. The pinned tracker wraps, clips and scrolls within a bounded area;
Page Up / Down bindings are configurable and respect existing key conflicts.
Jade uses its actual content scale and avoids the map, tracker and known status
areas. Ad Astra, Mekanism, RFTools and Aether interactions are included, as are
lower instruments and compass reservations.

The generated `config/soqm-hud-client.toml` defaults to `coordinateHud = true`.
Render-time coordination also handles existing FTB `local/` overrides. It does
not overwrite per-mod configurations or `options.txt`, and setting the option
false restores individual mod positioning. There is no per-frame file I/O.

**A graphical OpenGL Minecraft client was not launched.** These gates verify
real API compatibility, transformations and placement logic, not screenshot QA.
An arbitrarily large collection of panels cannot fit a tiny viewport; oversized
tooltips/toasts, enlarged map mode, central flight/radial overlays, custom fonts
and reversed Mekanism HUD remain explicit visual-check boundaries. See the
installed-overlay audit and exact pinned sources in [HUD_LAYOUT.md](../docs/HUD_LAYOUT.md).

## Storage and old-world upgrade

Iron Chests is absent from the new manifest, authored recipes and EMC entries.
No remaining pinned mod declares it as a mandatory dependency. Six existing
quests now target Sophisticated Storage, preserving their saved-object IDs.
The main storage row is iron → gold → diamond → netherite; copper and the first
Stack Upgrade are separate early options. Its new coordinate preview was
visually inspected; it is a diagram generated from authored positions, not an
in-game screenshot.

ME Drive and Matter Condenser consume inventory-free Sophisticated tier upgrade
kits, preventing ordinary machine crafting from swallowing filled chest items.
Native inventory-preserving storage upgrade serializers remain unchanged.

**Existing worlds require preparation using the old mod set before opening in
0.4.10.** Back up the complete instance, transfer chest contents and remove old
Iron Chests blocks/items from all players and dimensions. The candidate includes
the standalone read-only audit at `tools/audit_storage_upgrade.py`. It validates
bounded NBT and Anvil data, checks external chunks and refuses unsupported or
corrupt input, symbolic links and a changing file tree. It does not convert or
delete anything. Exit 0 means no references in a complete supported scan;
2 means references were found; 3 means the scan is incomplete.

A stopped native test world was audited without modification: **139 files and
1,097 NBT records**, no old references, no errors, `complete: true` and
`ready_for_removal: true`. Exactly zero-byte native-created region files are
accepted as empty; nonzero truncated headers still fail closed. This behavior
was checked against the pinned vanilla `RegionFile` implementation. The focused
storage suite passed 13 tests, including malformed NBT, symlink/unreadable trees,
concurrent file changes and both empty/truncated region cases.

No real player save was supplied or altered. Follow
[STORAGE_MIGRATION.md](../docs/STORAGE_MIGRATION.md) for the complete procedure
and conservative-audit limitations.

## Final gates and native persistence

The final dedicated-server run passed in **110.43 seconds**, reloaded
**20,055 → 20,055 recipes**, saved the world and exited normally with code 0.
KubeJS startup/server error gates and the fatal-server gate each reported zero;
this does not mean every upstream diagnostic log line was absent. The reviewed
production payload matched its lock before launch and remained unchanged.

All **1,627 existing personal claims**, **43 nonempty task values including 15
partial tasks**, and **three completed branch quests** survived the upgrade
fixture. All 1,442 native custom reward objects passed exact NBT/packet and
runtime-mapping round trips. Real crafting, party ownership, offline leave/join,
disk persistence and reload gates passed. Cardboard transport and fusion
regressions also passed. Native fixtures stay outside the game payload.

All **124 third-party downloads** passed byte count, SHA-256 and ZIP verification.
Both changed authored modules reproduced byte-identically from source.
`./scripts/check` passed **118 Python tests with no skips**, both Node policy
suites, deterministic campaign/locale regeneration, payload/source pin checks
and installation ZIP CRC/content/hash/rebuild checks. All **11 coordinate
previews** passed freshness checks. The final archive includes this record,
the migration instructions and the standalone read-only storage audit.

## Reproduction and fingerprints

```bash
./scripts/check
python3 scripts/render_campaign.py --check
python3 scripts/soqm.py verify-downloads
python3 scripts/runtime_probe.py --runtime /path/to/native-server \
  --java /path/to/jdk21/bin/java \
  --baseline-ref 1a7ecd3b5691888bc807667eb90cbd913e191934 \
  --accept-eula --timeout 420
python3 src/compat/quest-ui/build.py --runtime /path/to/native-client \
  --javac /path/to/jdk21/bin/javac --output /tmp/quest-ui-rebuild.jar --verify
python3 scripts/audit_storage_upgrade.py --world /path/to/stopped-world-backup
```

The HUD geometry unit test needs a JDK in `PATH`; the final local gate used the
same Java 21 installation as the native build. Native setup and the meaning of
accepting the Minecraft EULA are documented in [RUNTIME_VALIDATION.md](../docs/RUNTIME_VALIDATION.md).
Raw `reports/runtime-probe.json`, logs and disposable test worlds are local;
this human-readable record is included in the installation ZIP.

| Input | SHA-256 |
| --- | --- |
| `metadata/payload.sha256.json` | `389b76d9abd46308fd242957803c82865a9db43ff2523275db0a5480fae8fe8e` |
| `metadata/mods.lock.json` | `eb1b2ee2097d224fa21c9a9dbe73f03eb827e910982e9d3f3afce4bc4604bf18` |
| `metadata/authored-mods.json` | `e2983f761aae56e6610ab2b7f5127e4244524ca7b5304d8437aad9b0395b00e9` |
| `metadata/recipe_refunds.json` | `f01e7f2d9e049e7fc73330fe6bdb3dea94d7fb566f61c66fe16cfcde4c1923ef` |
| `metadata/storage_refund_compatibility.json` | `6041639c58f54f7cfdb80245f456a8b140d7306f19028aeb84c8bca0bd62ccae` |
| `scripts/runtime_probe.py` | `3cf8bf8b849f6b56eb1a3893705b2450206c34aa7221a439011b401931cec9ca` |
| `tests/runtime/MultiblockMaterialsProbe.java` | `d74a42aad524178b41739cd461c201e811a57f1697b5f11519364ab77e4f281b` |
| `tests/runtime/SoqmRuntimeProbe.java` | `fe341d89f5922c3703de9ffee32f8eeb80fe166950a52af2b36d33ec5d1c01a1` |
| `qm-progressive-rewards-0.4.10.jar` | `30a9ff206d07511d66187711ec1954f7fc7e0b11f73d6a88bd38867a7bf0a8d4` |
| `qm-quest-ui-0.4.3.jar` | `43f833a37bdacf16b2afa7699d59c53f335aa59db6a28528d5c015d0adb2de26` |
| Pinned mapped server | `f57ce3018e21459d24b50508e8c1b4fd5c16324ec1dc5f316c192aa0130e7fe0` |
| Pinned mapped client | `aff67d0c3bbd015ecd881bb37416b740ada2e1e854433501ec5bb9381dbdd1c2` |

The complete source provenance is in [src/compat/provenance.json](../src/compat/provenance.json).
Successful CI provides a review candidate and checksum; it is separate from a
published release. Graphical rendering, every physical construction, a complete
survival route and a long-term multiplayer economy were not validated here.
