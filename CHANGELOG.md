# Changelog

## Unreleased

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
