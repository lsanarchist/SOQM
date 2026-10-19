# Changelog

## Unreleased

- Preserve Dungeons & Taverns pending entities through native visibility loss
  and resume their effects when visible again. Discard canceled and duplicate-UUID
  insertions before dispatch, including legacy loads with misleading added flags.
  Add native regressions for both cases and rebuild the pinned runtime module.

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
