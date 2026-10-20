# SOQM 0.4.12 verification

Date: **2026-10-20**. Local candidate based on main
`95421eeda09f2e3447e6e9d3189a1e43d9c41381`. Verification was performed on the
local candidate before publication.

## Reviewed scope

- **63 hardcore crafting recipes and variants**, with **80 exact old recipe
  IDs absent** in the loaded server. Eleven of those IDs were recipes queued
  by earlier pack scripts and need explicit removal from KubeJS `addedRecipes`.
  Manual workshop, storage and Mekanism costs are documented in
  [Hardcore workshop](../docs/HARDCORE_WORKSHOP.md). Food recipes and hunger
  settings are unchanged.
- Chapter **07: MekaSuit, Modules and Equipment** contains **36 quests in seven
  sections**. Fourteen existing quests move with their IDs intact, and fifteen
  quests are added. HDPE production, plastic alternatives and the early Cardboard
  Box lesson have explicit bilingual guidance. Native Mekanism advancements
  check the complete equipment inventory and the maximum installed loadout;
  the latter includes all four armor pieces and Meka-Tool.
- The book contains **1,642 quests, 1,927 tasks and 1,643 personal reward
  objects** in 37 chapters. All **1,628 baseline reward IDs** remain. Historical
  task definitions, claim scope and ownership are reconstructed from the exact
  reviewed migration snapshot, rather than accepted through a changed baseline.
- **193 Mayhem Cube milestones** now deliver **222 cube units**, up from 193:
  29 existing claims gain one additional cube, an increase of **15.0259%**.
  Already claimed personal rewards remain claimed and receive no automatic
  second payout. The fifteen new lessons use ordinary personal reward packs.
- There are **1,449 progressive packs**, **14 pools**, **326 possible prize
  entries** and **41 ingredient refunds**. Chemical Infuser now consumes two
  Advanced Chemical Tanks and refunds one. The other forty fixed refund payloads
  retain their previous contents.
- Quest UI **0.4.5** displays guaranteed refund stacks as separate counted icons
  and provides an in-book catalogue of every possible prize, including its
  quantity, rarity, level and manufacture conditions. Preview controls preserve
  the original claim operation and its server checks. The existing Xaero AUTO
  minimap crash fix is retained.
- The unchanged dependency set has **128 pinned third-party files** and
  **12 authored modules**. The reviewed game payload contains **1,849 files**.

## Completed gates

| Gate | Executed result |
| --- | --- |
| `./scripts/check` with the Java 21 JDK on PATH | **162 Python tests, no skips**, both Node policy suites, metadata/payload validation, fresh campaign/English/loot generation and deterministic installation archive checks |
| Hardcore workshop regression suite | **9 tests**, including an executed JavaScript model with separate original/queued recipe collections, exact storage metal costs, reviewed native alternatives and positive first-workshop reachability |
| Equipment quest suite | **6 tests** covering optional progression, native advancement definitions, module slots, early HDPE, alternative plastic routes and the box's stable identity |
| Coordinate previews | All **12** SVG previews pass freshness checks; the final chapter 07 preview was rasterized and visually inspected |
| Pinned dependencies | All **128** files verified against reviewed size and SHA-256 pins; archive integrity was checked during the dependency audit |
| Quest UI native build | Compiles with **javac 21.0.12.1** against the pinned NeoForge 21.1.262 client; a second independent build produces exactly the same JAR bytes |
| Production client Mixins | **18 transformed targets**, with shortcut cancellation before native editor paste |
| HUD regression | **394 geometry assertions** and **1,920 native minimap sizing cases**, including AUTO, fixed sizes, resizing and opt-out |
| Reward UI regression | Compiled shortcut guard executes open/repeat/editor/text/other-modal/modifier cases; pinned FTB bytecode proves tooltips stay above the catalogue and reproduces the previous bad Z ordering as a negative control |
| Shipped catalogue | Exact parser verifies **1,643 rewards / 14 pools / 222 cubes / 29 double-cube claims / 41 refunds**, including the complete possible-prize mapping |
| Full pinned dedicated server | Final run **passed in 119.17 seconds**, with **20,056 → 20,056 recipes** across a real data reload, normal exit, zero KubeJS/fatal gate errors and an unchanged reviewed payload |

The full local gate also exercises all fifteen new equipment reward callbacks,
their exact saved IDs, three initial draws, absence of fabricated production
facts and unchanged cube/refund bundle behavior. Generated installation ZIPs
are checked for duplicate paths, exact file contents, CRC integrity and a
byte-identical rebuild on the same Python/zlib runtime.

## Native gameplay and persistence evidence

The final server workspace is
`.work/runtime-probe/run-20261020T154128.325271Z`. Its detailed machine-readable
record is `reports/runtime-probe.json`; runtime worlds, logs and test-only JARs
are local verification data and do not enter the installation package.

### Recipes and storage

The loaded native recipe manager confirms all **80 removed IDs are absent** and
all **63 new recipes** match supplied ingredients and assemble their expected
outputs. Circuit and Module Base outputs are checked at one item per craft.
Manual plate crafting retains and damages the Engineer's Hammer. Native manual
rod, steel, press and Basic Control Circuit recipes remain available.

All **24 native storage variants** preserve stored items, contents UUID,
installed upgrades, names, colors, wood type, locked/packed state and tier
capacity. Fixtures use **17 diamonds** and a Pickup Upgrade in the source
container. Single/double substitution cannot bypass the double price. Hopper
crafting rejects a populated Sophisticated container; the recipe consumes an
unused upgrade token instead. Old cheap vanilla and alternate piston grids are
rejected.

### Quests, rewards and saved state

- **57 existing nonempty task values**, including **15 partial tasks**, remain.
- **17 completed branch quests**, including all **14 moved MekaSuit quests**,
  retain their completion. The **15 new equipment lessons** begin unclaimed.
- All **1,628 existing personal claims** remain used. The current book has
  **1,643 outer rewards**; all **1,458 custom rewards** pass native NBT and
  packet round trips with exact mapping checks.
- Actual two-player claims, independent random rolls, three/four/five-draw
  delivery, late joining, leave/join ownership and disk-backed duplicate-claim
  rejection pass. Unmanufactured expensive items stay excluded; real crafting
  unlocks the corresponding production evidence.
- **43 live recipe alternatives** support the forty-one refund definitions.
  Premature, invalid-policy and wrong-parent claims are rejected without
  consuming the entitlement. Fixed quantities, preserved cubes, late-joiner
  claims and policy selection also pass after reload.
- ME terminal crafting, AE2 Inscriber/Molecular Assembler finishes and Mekanism
  Infuser/Infusing Factory finishes execute through native game code. Stored
  output, unpowered machines, inventory pickup, creative placement and item
  renaming do not create false manufacture records.
- Team progress, completed quests, ME advancement, production policy/evidence
  and personal claims pass the final **reload and disk-persistence checks**.

The preserved general fixtures additionally exercise **35 multiblock kits / 166
material checks**, all **32 Aether accessory equip/unequip cases**, **18 actual
Curios insert/lookup/extract routes**, Cardboard Box rejection of all eight AE2
CPU blocks, ordinary chest/quartz carryover and fusion detection under full
energy export.

## Corrective findings incorporated

The first full run exposed a real recipe bypass: pinned KubeJS
`event.remove({id})` visits `originalRecipes` but does not remove earlier queued
`event.custom` additions. The generated late script now marks the eleven exact
superseded queued recipes removed before adding the replacements. Static
progression/refund analysis uses the same explicit `removeAddedIds` contract.
The strict native eighty-ID absence test remains intact and passes.

Independent reward UI review found cumulative FTB modal Z placing tooltips
behind the catalogue, and a shortcut collision with editor paste. The relative
Z increment and scoped cancellable HEAD interception fix both. Repeated keys
are consumed in the open catalogue; focused text fields and unrelated modals
retain native input. The previous minimap recursion fix remains covered.

The first execution of the older compiled-only equipment fixture also exposed
an incorrect test assumption: native Curios permits tag-based items without an
optional item capability. The fixture now uses the actual slot handler's
validation, insertion, lookup and extraction path, retaining all own-hook,
event and negative-slot checks. Production equipment policy is unchanged.

One intermediate server run continued its functional fixtures after its log
outputs stopped updating. It was stopped and excluded from successful runs.
The final sequential run above passes the unchanged startup, log, real-reload,
recipe-stability and shutdown gates; no production workaround or weakened gate
was introduced for the inconclusive run.

## Fingerprints and reproduction

| Input | SHA-256 |
| --- | --- |
| Quest UI 0.4.5 JAR, 134,799 bytes | `66688e1cca5af8e5218afeaa57c21dc7251225b36b73eff82082444e2472b9c0` |
| Mapped NeoForge 21.1.262 client | `aff67d0c3bbd015ecd881bb37416b740ada2e1e854433501ec5bb9381dbdd1c2` |
| Mapped NeoForge 21.1.262 server | `f57ce3018e21459d24b50508e8c1b4fd5c16324ec1dc5f316c192aa0130e7fe0` |
| Current saved-object identity | `609df13b427602821a5e8dfd0577f6df352b16248b608806bc8f7710e743cd87` |
| Reconstructed baseline contract | `aff57cadd6bb9fa66d0b1069efec03fae55be20ff19280f3b8af5f075c57ebde` |

The reviewed source hashes and previous authored revisions are retained in
`src/compat/provenance.json`. The final installation archive is
`dist/SOQM-0.4.12-curseforge.zip`; its build checksum is written to
`dist/SHA256SUMS`.

With the pinned dependency cache and native runtime available:

```sh
./scripts/check
python3 scripts/render_campaign.py --check
python3 src/compat/quest-ui/build.py --runtime /path/to/native-runtime --javac /path/to/jdk-21/bin/javac --mods .cache/mods --verify
python3 scripts/runtime_probe.py --accept-eula --java /path/to/jdk-21/bin/java --runtime /path/to/native-runtime --baseline-ref 95421eeda09f2e3447e6e9d3189a1e43d9c41381 --timeout 300
```

These are dedicated-server fixtures, native client bytecode/state checks and
coordinate-preview inspection. **No graphical Minecraft client playtest,
complete survival playthrough or long-term multiplayer economy test is claimed.**
The crafting fixtures supply ingredients; they do not measure mining time,
early resource scarcity or factory throughput.

Use the [installation guide](../docs/INSTALL.en.md) for applying the complete
payload. When manually updating a copied client profile, replace the previous
Quest UI JAR rather than leaving both versions in `mods/`.
