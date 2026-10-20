# Quest lessons and production-observer update

This update builds on main commit
`df4036e0dc4585dc564e6a715dbafcf485733c00` in SOQM 0.4.9.
It improves player instructions and removes avoidable work from the native
production-evidence observer.

## Reviewed bilingual lessons

| Source | Quests | Scope |
| --- | ---: | --- |
| `foundations_endgame.json` | 52 | Starter tools/logistics, ProjectE, AppliedE and core Avaritia production |
| `foundry_english.json` | 35 | Full English translations of the detailed Ukrainian Modern Foundry chapter |
| `industry_logistics.json` | 102 | IE, BuildCraft, Railcraft, RFTools, storage, furnace upgrades and Mekanism equipment |
| `magic_expeditions.json` | 97 | Botania, EvilCraft, Apotheosis, Ad Astra and key dimension resources |
| **Total** | **286** | **733 paragraphs per language** |

The sources live in [tutorial_expansions](../metadata/tutorial_expansions).
Descriptions explain acquisition, setup, useful outputs and common mistakes.
Evidence comes from the pinned mod recipes/manuals and SOQM's KubeJS recipe
overrides; each lesson records its source references. Modern Foundry retains
its existing Ukrainian instructions except for an obsolete chapter reference.

Concrete corrections include:

- Mythic Circuit requires actual first manufacture and can then appear in the
  masterwork pool at reward level 5. The canonical route metadata now agrees
  with the reward policy.
- Grout points to `00 · Main Route`, replacing the obsolete chapter name.
- Mekanism Jetpack/Flamethrower use hydrogen, Scuba uses oxygen, and electric
  equipment has its own charging instructions.
- IE and Railcraft coke ovens have different construction requirements.
  Connectors, relays, extraction pipes and routing devices get distinct lessons.
- Ad Astra Oxygen Distributor explains water, FE, the room boundary and Show.
  EvilCraft lessons distinguish reusable captured spirits from consumed ones.
- Apotheosis lessons explain reforging reagents, gem withdrawal and the
  different effects of enchanting tomes.
- Twilight's map quest asks for the blank map before activation changes its ID.
- AppliedE lessons explain whose knowledge/EMC is used, network pooling and
  the item consumption of bulk learning.
- Avaritia lessons use SOQM's EMC-gated recipes and simplified food recipes,
  and distinguish table tiers and material-compression steps.

Including the existing authored lessons, **572 distinct quests** now have
reviewed descriptions in both languages. Compared with the base commit,
**253 Ukrainian** and **287 English** description entries change, including
the separate Mythic Circuit fix. Generic single-paragraph English inventory
descriptions decrease from **1,022 to 763**. This update does not claim that all
1,626 quests have a full tutorial.

### Regeneration contract

[`campaign_lessons.py`](../scripts/campaign_lessons.py) applies description-only
overlays after the existing progression/branch generators. It rejects duplicate
source keys and owners, incomplete translations, unsupported fields and unknown
quest IDs before writing a locale. Titles, task conditions, reward definitions
and dependency graphs remain in their existing sources.

```sh
python3 scripts/campaign_refresh.py
python3 scripts/localize_english.py
python3 scripts/soqm.py lock
./scripts/check
python3 scripts/render_campaign.py --check
```

Both locales are committed as generated chapter fragments under
`metadata/quest_locales/`; packaging assembles the runtime language files.
See [localization](LOCALIZATION.md) for the source and runtime layouts.
For this lesson update, the 37 chapter files remain byte-for-byte equal to
the base commit, as do reward policy, recipes and all other
compatibility-module binaries.

## Production observer 0.4.9

Previously, successful AE2/Infuser callbacks inspected inputs even when their
output was absent from the 202-item production allowlist. AE2 could also copy
pending stacks and inspect node ownership before learning that the result was
irrelevant to reward eligibility.

The observer now rejects these outputs first. Policy loading constructs an
immutable registered-`Item` identity set alongside the existing string-ID set,
then publishes both as one snapshot on the resource-apply thread. Output
filtering needs no registry lookup or ID-string allocation. Reload replaces
both views together; invalid policy entries and server stop clear both views.

Tracked outputs still follow the existing real manufacture, input-consumption,
accepted-output, player eligibility, producer ownership and team rules. The
change adds no cache of player, team or production evidence.

### Counted regression evidence

[`test_production_observer.py`](../tests/test_production_observer.py) executes
methods extracted unchanged from the production Java sources with counted
native stand-ins. It is an operation-count regression harness, not a game
performance benchmark.

| Case | Confirmed observer work |
| --- | --- |
| 1,000 untracked Molecular Assembler results | 0 input reads, 0 pending stack copies; all 1,000 native assemble calls retained |
| 1,000 untracked Inscriber results | 0 input-slot reads, 0 pending stack copies |
| 1,000 untracked Infuser results | 0 ingredient-representation reads, 0 owner reads |
| Output filtering in these runs | 0 registry reads |

Positive tracked manufacture, same-item rejection, ambiguous Infuser outputs,
policy replacement, immutable snapshots and fail-closed clearing are covered.
The native fixture also includes equality checks between both policy views for
every registered item before and after reload.

### Build and installation

The production JAR and updated native fixture compile against the pinned
Minecraft 1.21.1/NeoForge 21.1.262/mod APIs with `javac 21.0.2`.
The shipped adapter is `qm-progressive-rewards-0.4.9.jar`, 21,232 bytes,
SHA-256 `4e195cb0597de01e610aef711531daf49438a9a1d31b09664f6bfe9e4dfe1e06`.
The authored binary pin, source provenance and payload lock are updated together.

Use the complete generated pack on client and server. When updating manually,
replace `qm-progressive-rewards-0.4.8.jar` with the new 0.4.9 JAR and restart;
the two versions share a mod ID and must not coexist. The pack itself remains
0.4.9 alpha.

### Validation limits and follow-up profiling

Local validation passed all **72 Python tests**, both Node policy suites,
generator freshness checks, all **10** map previews, and the archive's CRC,
hash and deterministic-rebuild checks. The production module and test-only
native fixture both compiled against the pinned APIs with Java 21. A fresh full-pack
server run and graphical client review are separate checks; compilation is not
evidence that either ran. The isolated server harness requires the operator's
explicit Minecraft EULA acceptance as documented in
[runtime validation](RUNTIME_VALIDATION.md).

No overall TPS/FPS improvement is claimed. Useful next profiling targets are
the village-guard global mob-tick observer and index rebuilds, and the electric
quest observer's player scans at recipe completion. Measure their actual share
of server tick time before changing scheduling or adding stateful caches.
