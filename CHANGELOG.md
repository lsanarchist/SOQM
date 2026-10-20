# Changelog

## Unreleased

### 0.4.14 — Village Guard without global mob polling

- Replace Village Guard's per-tick pass over every mob with target-change hooks
  and checks of ongoing encounters involving Millénaire villagers. Cover native
  mob targets, direct Brain attack-memory writes and Millénaire combat targets,
  including villagers entering protected ground after a pursuit begins.
- Update the spatial index after village membership, building geometry or culture
  definitions change. Coalesce repeated changes and rebuild only the affected
  village during ordinary play; unchanged villages no longer rebuild every
  200 ticks. Keep the index owned by its native manager and release listeners
  on removal or level unload.
- Retain peaceful-resident and actual-raider rules, radius and rotated building
  boundaries, damage prevention and block-by-block mob explosion filtering.
  Add an isolated native server regression harness with work counters.
- Rebuild and pin `qm-village-guard-0.4.3.jar`. A manual client/server update
  must remove the old `qm-village-guard-0.4.2.jar` before starting the new profile.

### 0.4.13 — dynamic lighting from held items

- Add LambDynamicLights 4.8.11+1.21.1 for Minecraft 1.21.1 / NeoForge, pinned
  to official CurseForge project 393442 / file 8780227 with verified byte count
  and SHA-256. This release supports the pack's Sodium 0.8.13.
- Enable lighting from held torches and lanterns through the mod's standard
  defaults. The pack does not overwrite a player's existing lighting settings.
  Required NeoForge libraries are already included in the official mod JAR.
- Mark the dependency as client-only so the dedicated-server harness excludes
  it. Update the manifest, mod list and installation guides for 129 pinned
  third-party dependencies and package version 0.4.13.

### 0.4.12 — hardcore workshop, MekaSuit progression and visible rewards

- Adapt the early Questing Mayhem workshop into 63 explicit recipe replacements:
  chest-core furnaces, eight-block Iron Furnaces, full copper/iron storage tiers,
  costly hoppers, rod-and-gear pistons and a chained gear workshop. Keep manual
  plates and steel available before powered machinery; the metal press rewards
  automation with better material efficiency. Food recipes are unchanged.
- Raise Mekanism casing and machine costs, require two previous-tier circuits
  for one upgraded circuit, and make Module Base an HDPE/Elite Circuit assembly.
  Remove 80 exact old recipe IDs, including alternative cheap piston, gear and
  upgrade paths. Preserve native storage and Mekanism data-transfer serializers.
- Move 14 existing MekaSuit/MekaTool lessons to the visible equipment chapter
  while retaining their saved IDs. Add 15 HDPE, module and equipment milestones,
  including native full-set and maximum-installed-upgrade advancements. Explain
  the current Industrial Foregoing rubber route and reveal the existing Cardboard
  Box lesson beside Precision Sawmill. The equipment branch remains optional.
- Increase guaranteed Mayhem Cube units from 193 to 222 (+15.0259%) by adding one
  cube to 29 existing personal claims. Retain all 193 cube milestones and all
  1,628 previous reward IDs; already claimed rewards receive no second payout.
  The new book contains 1,642 quests, 1,927 tasks and 1,643 personal rewards.
- Show guaranteed refund stacks as separate counted icons in the reward section.
  Add a View Rewards button and Ctrl+Shift+V catalogue with every possible pool
  prize, quantities, rarity and manufacture conditions. Keep native claim clicks
  and server entitlement checks. Build Quest UI 0.4.5 with the existing Xaero
  automatic-size crash fix intact.
- Keep recipe evidence aligned with active late overrides. Chemical Infuser now
  consumes two Advanced Chemical Tanks and returns one; the other 40 fixed refund
  payloads are unchanged. Validate historical quest ownership/content, native
  storage contents and components, removed cheap routes and catalogue coverage.

### Xaero automatic minimap crash hotfix

- Fix the first-world-render `StackOverflowError` in Quest and HUD Help 0.4.3
  when Xaero 26.6.0 uses automatic minimap size. Resolve automatic dimensions
  using Xaero's native user scale before applying SOQM's render-only size cap.
- Preserve fixed sizes, automatic UI scale, Minecraft GUI scale, enlarged maps
  and the HUD opt-out. Rebuild and pin `qm-quest-ui-0.4.4.jar`, replacing 0.4.3.
- Add a behavioral probe for the transformed native size/scale call chain,
  alongside the existing geometry and Mixin gates. Check both bounded render
  size and stable source dimensions; transformation success alone did not
  detect this recursion. A full graphical client playtest remains separate.

### Early settlement protection

- Add the pinned Torchmaster 21.1.13 build for Minecraft 1.21.1 / NeoForge.
  Add an optional Mega Torch quest first in the Millénaire contact section,
  available after Base Foundation without a settlement visit or industrial gate.
- Explain the default 64-block hostile natural-spawn protection, range display,
  working spawners, existing mobs, event/raid limits and the separate passive-mob
  Dread Lamp in Ukrainian and English. The item check does not consume the torch.
- Keep all existing saved IDs and personal claims; add one personal settlement
  reward and verify the unchanged pre-addition save contract.

### Chapter-sized quest translations

- Split Ukrainian and English quest translations into 37 chapter files and one
  global file per language under `metadata/quest_locales/`. Preserve every
  translation key, string and paragraph; reject duplicate or misplaced entries.
- Assemble the two native FTB Quests locale files during packaging and server
  staging. Keep large generated files out of Git, with identical locale content
  in release archives, runtime checks and historical upgrade fixtures.
- Validate fragment ownership, matching language keys and a 200 KiB per-file
  limit so future text updates remain small and reviewable.

### 0.4.11 — optional exploration, varied dungeon loot and equipment compatibility

- Add the exact Minecraft 1.21.1 NeoForge builds of Twilight Treehouses 1.0.2,
  Ad Astra: More Structures 1.0.2 and Ember's Underground Rooms 2.3.2. Keep
  dimension restrictions, reduce Ember's large-dungeon candidate density and
  correct its jail starting height and restore its missing 1.21 loot table.
- Replace a prefilled 27-slot moon-tower chest with deferred randomized loot
  using a scoped native structure processor that preserves chest facing.
  Existing generated terrain and opened containers are untouched.
- Make all 67 audited Overworld visit objectives optional leaves. Rewire
  chapters 01, 22, 23 and 32 to remove 20 directly/transitively blocked
  follow-ups while preserving all 1,626 quest IDs, tasks, rewards and actual
  material/dimension/boss progression.
- Admit Atomic Disassembler and Meka-Tool through FTB Ultimine's additive
  included-tools tag. Preserve harvest, energy, hunger and block exclusions.
- Explain the separate Aether accessory inventory on all 32 accessory items
  in Ukrainian/English, using the player's actual configured key. Keep the
  native inventory mode and equipped-item state. Limit dedicated backpack
  relocation to Sophisticated Backpacks so Infinity Backpack keeps its native
  read/write slot route.
- Add weighted supplies and one optional useful-component find to five chest
  profiles; improve native treasure retention with location difficulty, retain
  3–10-slot ordinary budgets and protect original maps, keys, progression
  trophies and native Aether treasure.
  Cover 397 exact tables, including 17 new structure tables. Exclude plastic,
  nether stars, elite/ultimate circuits and custom late-game progression parts.
- Rebuild and pin Chest Balance 0.4.11. Add generated-data regressions, optional
  quest reachability checks and native object probes for 50,000 bounded chest
  cases, structure codecs and prefilled-container handling. A full client and
  world-generation playtest remains separate.

### 0.4.10 — crafting refunds, coordinated HUD and Sophisticated Storage

- Give 41 expensive crafting quests a guaranteed return of reviewed recipe
  ingredients alongside their existing personal random pack or Mayhem Cube.
  Partial refunds follow the cheapest supported live recipe, with native
  manufacture evidence and a pre-claim gate that keeps an early claim available.
  Nine cube/refund bundles reuse their original reward IDs; all 193 cube
  milestones remain. Existing claims are never reissued.
- Recognize successful Iron Furnaces upgrades performed in the world as
  manufacture, alongside the existing crafting and automation hooks.
- Coordinate Xaero's minimap, FTB Ultimine, the pinned quest tracker, Jade,
  Ad Astra suit indicators and Mekanism HUD panels in the existing Quest UI
  adapter. Keep the tracker bounded, wrap long lines and expose configurable
  Page Up/Page Down scrolling; retain an opt-out for custom HUD arrangements.
- Remove Iron Chests from the dependency manifest, recipes, EMC entries and
  quest content. Retarget six saved quest slots to Sophisticated Storage,
  teach one consistent storage route and use inventory-free tier upgrade kits
  in AE2 machine recipes. Include a read-only old-world reference audit and
  explicit preparation/rollback instructions; container conversion is not automatic.
- Add native correctness and timing probes for all 166 item conditions in the
  35 construction kits, including multi-stack amounts, inventory debounce,
  retained progress and separate formation/operation conditions. No world
  scanner or periodic multiblock search is added for material detection.
- Preserve 1,626 quest IDs, 1,911 task IDs and 1,627 personal reward IDs. Record
  the six task retargets and 41 reward edits against the previous main commit
  without replacing the older immutable save contracts.

### Previously reviewed 0.4.9 questbook changes

- Add explicit, non-consuming construction checklists to 35 multiblock and
  assembly quests: 138 additive item tasks and 14 corrected quantities, checked
  against pinned structure templates and validators. Retain native formation
  and operation objectives and all existing saved object IDs.
- Put Coke Oven, creosote, Blast Furnace, steel and copper coils before the
  Industrial Chassis they supply. Rebuild the Immersive Engineering teaching
  sequence and remove artificial Precision Mechanism prerequisites from its
  basic production machines while retaining real recipe gates.
- Replace all 1,626 generic stage/role subtitles with actual section topics or
  specific device hints in Ukrainian and English. Name the six skill bonuses
  after their real materials, including antimatter for the last bonus.
- Keep short readable quest links, reveal distant requirements on hover, and
  route native curves around captions. Fix topological reading order in ordinary
  sections and repair misplaced Apotheosis, exploration, space, Botania and
  diving-equipment nodes. Coordinate previews follow FTB's real draw order.
- Increase personal Mayhem Cube milestones from 14 to 193. Ordinary packs now
  draw 3–5 prizes with one eligible uncommon-or-better draw, larger basic supply
  bundles and stronger rare/epic weights. Exact manufacture requirements,
  personal claim keys and plastic/HDPE exclusions remain enforced.
- Record the reviewed additive questbook changes separately from immutable
  historical save contracts; existing claimed rewards are not issued again.
  See [construction kits](docs/MULTIBLOCK_MATERIALS.md),
  [map readability](docs/QUEST_LAYOUT_READABILITY.md),
  [rewards](docs/REWARDS.md) and
  [verification](reports/verification-questbook.md).

- Replace the long bilingual repository landing page with a concise English
  introduction, gameplay highlights and installation/documentation links.
- Add Inventory Profiles Next 2.2.5 for inventory sorting, item transfers and
  locked slots, plus libIPN 6.6.3 and Kotlin for Forge 5.12.0 for Minecraft
  1.21.1 / NeoForge. Pin official CurseForge files, byte counts and SHA-256
  hashes; include all three in the client manifest and exclude this client-only
  dependency chain from the dedicated-server harness.
- Bind Inventory Profiles Next sorting to the middle mouse button so JEI keeps
  its `R` recipe shortcut. Open the inventory settings with `R+C`.

- Expand 286 quests with reviewed Ukrainian/English lessons covering industry,
  magic, expeditions, starter equipment and endgame production; fully translate
  all 35 Modern Foundry lessons. Correct Mythic Circuit reward eligibility,
  outdated chapter navigation, machine inputs and inventory-check timing.
  Preserve the complete quest graph and saved quest/task/reward definitions.
- Skip untracked production outputs before AE2 input scans/stack copies and
  Mekanism Infuser ingredient/owner reads. Rebuild a registered Item identity
  filter atomically with the reward policy on reload; ship Progressive Rewards
  0.4.9 with reviewed source and binary hashes. See
  [lesson and observer validation](docs/QUEST_LESSONS_AND_OBSERVER.md).

- Add an optional Mekanism Cardboard Box quest after Precision Sawmill, with
  Ukrainian/English use instructions, its own icon and a personal engineering
  reward. Retain every existing quest/task/reward object and add exactly one
  quest, task, reward and caption; historical identity hashes remain verified.
- Prevent the pinned Mekanism/AE2 active-autocrafting ingredient duplication
  path with an additive cardboard blacklist for all eight native CPU blocks.
  Ordinary chests, AE2 storage, controllers and budding quartz are unaffected.
- Correct the sawmill/PRC lessons: sawdust makes paper and boxes; the starter
  substrate/ethylene recipe requires Bio Fuel. Remove the unrelated sawmill
  prerequisite from the PRC quest.
- Credit fueled fusion with positive generation even when its buffer is
  emptied by consumers in the same tick. Restore explicit fission/fusion
  observation setup, proximity, duration and shutdown instructions.

- Preserve Dungeons & Taverns pending entities through native visibility loss
  and resume their effects when visible again. Discard canceled and duplicate-UUID
  insertions before dispatch, including legacy loads with misleading added flags.
  Add native regressions for both cases and rebuild the pinned runtime module.

### 0.4.9 — production quest branches from starter to scaled systems

- Rebuild Ex Deorum, Ex Machinis and AE2 around usable starter installations,
  feed/output paths and successive upgrades. Remove Diamond Mesh/Hammer
  prerequisites from first mechanical machines; correctly attach Comparator
  Upgrade to Item Buffer and import Flux output from its external receiver.
- Start AE2 with a one-cell ME Chest, actual terminal crafting and a minimal
  1k autocrafting setup. Align the real RFTools Crafter T1 and Ender IO Crafter
  ingredients with Precision stage 2. Keep Drive, Controller, larger CPUs,
  P2P, subnets and remote/spatial systems in their subsequent branches.
- Reorganize Mekanism, its nuclear chapter, Ender IO, Industrial Foregoing,
  Extreme Reactors and Mob Grinding Utils into component, assembly and
  operating sequences. Keep alternative routes and optional samples separate.
- Add bilingual operation guides and vertical branch maps with visible local
  dependencies. Reuse existing quests, panels and captions; generate ten
  coordinate previews for review.
- Preserve all 1,625 quests, 1,772 task definitions and 1,626 personal reward
  definitions. Existing 0.4.8 claims and production discoveries stay used
  and unlocked. Add route/save-contract regressions and native ME Chest plus
  completed-quest/personal-claim upgrade checks. See
  [quest branches](docs/QUEST_BRANCHES.md) and [verification](reports/README.md).

### 0.4.8 — personal rewards that improve with production

- Expand the 14 themed pools to 326 entries: 240 distinct items, including 210
  modded items. Add custom chassis/circuits, AE2 processors, all six
  Sophisticated barrel tiers, storage/backpack upgrades and useful modded supplies.
- Award each member one personal pack per ordinary quest, with 2/2/3/3/3/4/4
  independent draws at production levels 0–6. Increase rare/epic group weights
  as the team manufactures the six material milestones. Keep all outer reward IDs.
- Require actual production of the exact expensive prize. Add an event-based
  native adapter for crafting, AE2 assembly/Inscriber, Mekanism infusing and
  consumed Sophisticated Storage upgrades. Pickup, reward delivery, renaming,
  reset recipes, fake players and creative manufacture do not establish proof.
- Keep production discoveries in persisted FTB Teams data. Party discoveries
  remain shared; departing members retain only their own production. Preserve
  native personal claims across team transitions and persist merged claim data.
- Intentionally grant one new personal entitlement for each formerly shared
  reward on completed quests. Preserve historical shared keys and old personal
  book claims; subsequent duplicate claims remain blocked for each member.
- Add bilingual read-only reward status, eligible-item odds and unlock conditions
  through `/soqm rewards` and `/soqm rewards pool <theme>`. Retire the 14 static
  tables. Keep plastic/HDPE excluded and produce fresh, empty default containers.
- Include the authored production adapter source, deterministic build, binary
  pin, recipe evidence and native manufacture/claim/persistence regression gates.
  Details and the complete catalogue are in [REWARDS](docs/REWARDS.md).

- Reuse repeated Abandoned Tunnels height queries within one placement search,
  keeping the original terrain samples, random sequence and generated pieces.
- Replace repeated Mayhem guardian UUID sweeps with bounded persistent deadline
  processing and native lifecycle events, including existing-world migration.
- Skip capped Quest Automation evidence before accessory/output reads; replace
  avoidable Dungeons & Taverns polling with native events, direct tag checks
  and tracked scheduled entities. Keep the updated 0.4.8 questbook and personal
  rewards. See [runtime optimization details](docs/PERFORMANCE_OPTIMIZATION.md).

- Replace nine skill-milestone tick/NBT criteria with explicit advancement
  grants from Pufferfish Skills unlock rewards. Existing unlocked skills are
  reconciled on login or data reload; advancement IDs, saved completion,
  capstone's any-one-branch rule and historical completion after respec are
  retained. No new mod or polling script is required. See the
  [patch notes](docs/SKILLS_OPTIMIZATION.md) for scope and validation.

### 0.4.7 — campaign clarity and thematic rewards

- Move the six-stage Main Route to chapter 00 in the first group. Differentiate
  finale prerequisites, practical tasks, reference items and optional branches;
  preserve every existing quest/task/reward/chapter/image ID.
- Shorten 1,525 titles, remove 1,453 repeated openings and 2,287 obsolete
  reward/preparation/patch-note paragraphs. Add 67 bilingual core tutorials
  and editorial names for all 251 map sections.
- Correct early AE2 preparation, preflight-before-Moon dependencies and
  furnace/reactor material stages. Bring the initial 1k ME autocrafting setup
  to stage 2 while gating 4k/16k components so native CPU upgrades stay valid.
- Convert the existing ME Crafting Terminal task to a real powered-terminal
  crafting advancement. Clarify that the ME workshop kit checks preparation
  and does not claim to measure autonomous crafting jobs.
- Replace 1,611 ordinary payouts with one visible weighted draw from 14 original
  thematic supply pools. Retain one cube at 14 selected milestones and the
  personal introductory book. No random machines or material-gate components.
- Add deterministic campaign regeneration, save-identity/geometry/budget/gate
  regressions and native FTB/AE2 interaction plus nonempty-save migration tests.
- Add CI validation and installation ZIP/checksum artifacts for reviewing a
  specific commit. The complete change and remaining client/performance scope
  are recorded in [the campaign notes](docs/CAMPAIGN_UPDATE.md).

### Included hotfixes from the current base

- Update `qm-cannibal-compat` to 0.4.1. Cannibal Villages discovery scans now
  inspect only ready chunks instead of waiting for nearby generation on the
  server thread. Existing discovery flags, titles, sounds, and chest protections
  remain in place. This targets the repeated blocking path identified in two
  player-provided spark profiles; it does not claim a measured speedup or fix
  the separate Millénaire village-spawn waits.
- Work around empty Ukrainian Apotheosis World Tier tooltips with the default
  font by shortening the two activation captions to fit their button. The
  resource and font bounds are verified; graphical client verification is pending.

## 0.4.6 — local alpha update

SOQM now uses the supplied Questing Mayhem Expert **0.4.6** baseline, while
retaining SOQM branding and independently maintained policies. The source
archive SHA-256 is `5c17c9030296cbbc4ce8a5c0e075c19f10366524a17f95f7fbcbe12c25604061`.

- 122 pinned third-party dependencies: the original 111 unchanged plus 11 additions.
- 10 authored compatibility modules: the original eight plus
  `qm-mob-compat-0.4.5` and `qm-chest-balance-0.4.6`.
- Leaner new chest loot and compact first-fill handling for 380 exact tables;
  protected story items, keys and boss rewards can exceed ordinary limits.
- Nine structure templates adjust 22 prefilled inventories. Existing opened
  chests and personal Lootr inventories are not regenerated or cleared.
- Plastic/HDPE removed from random chest loot and 42 entries in 36 cube tables;
  quest rewards and recipe progression are retained.
- 280 quest icon replacements, including 140 distinct DivineRPG icons, plus two
  model-reference fixes.
- Improved Ukrainian guidance: routes for all 37 chapters, clearer material
  milestones, 1506 revised descriptions and 3131 repeated service paragraphs removed.
- The same 1625 quests, 1772 tasks and 1626 reward IDs; no new quests or recipe
  changes in the 0.4.6 baseline. English remains an objective guide.

These content counts describe the imported baseline; historical native results
from its archive are not a fresh test of the changed SOQM package. See
[verification status](reports/README.md). The local output is
`dist/SOQM-0.4.6-curseforge.zip`; no new public asset is claimed. Graphical
playthrough, multiplayer balance, economy and client performance remain alpha work.

## 0.1.0-alpha.1 — 2026-10-18 (historical)

First standalone SOQM source and installation release, based on the supplied
Questing Mayhem Expert 0.4.4 campaign.

- 1,625 quests in 37 chapters, with existing quest/task/reward IDs retained.
- Minecraft 1.21.1, NeoForge 21.1.262, Java 21; 111 pinned mod references.
- Full installation manifest replaces dependence on an existing patch profile.
- Independent accessory and loot policies replace two uncleared adaptations.
- Eight recovered compatibility source modules with original license notices.
- Ukrainian campaign; English objective guide with explicit translation scope.
- Local validation, reproducible installation ZIP, download integrity audit.

Historical server and archive evidence is linked in `reports/README.md`; it
applies to that immutable alpha, not automatically to SOQM 0.4.6.
