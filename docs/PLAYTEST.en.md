# Game Checks before Alpha

This checklist requires a real graphical client. An unchecked box means **not tested**. File validation, a historical server report and successful ZIP creation do not complete these checks.

Record the release, commit, imported ZIP SHA-256, date, OS, GPU, Java, allocated RAM, player count and result. Attach reproduction steps and logs for failures; use screenshots or short recordings for behavior that logs cannot demonstrate.

## Clean install and early game

- [ ] The built ZIP imports into a fresh profile and all dependencies download without manual version substitutions.
- [ ] The client reaches the main menu without mod or resource loading errors.
- [ ] A fresh world creates, saves and reopens successfully.
- [ ] The introduction explains how to begin; text, icons and links render correctly.
- [ ] The first quest chain, dependencies and reward claims work.
- [ ] Progress, inventory and claimed rewards persist after rejoining.
- [ ] Item/recipe search works and key conflicts do not block essential actions.
- [ ] Custom textures appear in inventory and the world without missing textures or atlas errors.

## Game systems

- [ ] The early route can be completed in survival without commands or creative items; record elapsed time and points of confusion.
- [ ] At least one progression transition in every major branch is checked for obtainable ingredients, available recipes and quest detection.
- [ ] World loot, personal chests and random events work; unusual rewards also work with a full inventory.
- [ ] Tested dimensions load, allow returning and survive a world reload.
- [ ] If server support is advertised: a dedicated server starts, two clients join, and personal/team progress and rewards survive a restart.

These samples do not prove a complete campaign, long-term economic balance or extended-session stability. Performance claims need measurements with machine and test conditions; one machine does not establish universal requirements.

## Modern Foundry Crafting Station

The slot-index defect in the pinned Modern Foundry 4.1.6 is confirmed from its
code and matches the reported sandstone layout. The compatibility module
corrects all three writes. The following graphical checks remain unchecked
until actually performed; offline validation does not complete them.

- [ ] On both client and server, verify Modern Foundry 4.1.6 and
  `qm-foundry-compat-0.4.15.jar`. Use a separate survival test world.
- [ ] Put exactly 64 sand into the Crafting Station as four stacks of 16 in
  the bottom-right 2x2 square. Take one sandstone. Expect **60 sand and one
  sandstone**, with 15 sand in each original slot and the other five empty.
- [ ] Repeat in the other three corners. Then use one sand per ingredient
  slot: one sandstone must consume all four, leaving no further output.
- [ ] Repeat the user's Ctrl-assisted bulk-craft sequence, recording its click
  target; also test ordinary pickup and Shift+output pickup. Fully converting
  64 sand must produce **16 sandstone**. Count inventory, grid, cursor and
  dropped stacks, including after closing the station and reconnecting.
- [ ] Repeat with a nearly full inventory and adjacent storage. Confirm no
  loss or extra output when only part of the result can be moved.
- [ ] Check a shifted 1x2 recipe such as sticks, a full 3x3 recipe and a recipe
  that returns a bucket or other container. Remainders must use the actual
  consumed slots or the normal inventory/drop route.
- [ ] Confirm JEI ingredient transfer, IPN sorting and ordinary crafting-table
  behavior still work with the existing settings.

## Workshop recipe selection

- [ ] In an ordinary table, manually arrange each of the four iron gears and
  three gold gears shown in JEI. Before taking the result, check its exact mod
  and item ID. Repeat with a horizontally mirrored grid and after `/reload`.
- [ ] Craft Stone-to-Iron with eight iron blocks around cobblestone, and
  Copper-to-Iron with eight iron blocks around a copper ingot. Check each
  token upgrades its intended placed furnace.
- [ ] Repeat gear and upgrade crafting in the Modern Foundry station and via
  JEI transfer. Record the five changed gear patterns and Copper-to-Iron
  upgrade again in AE2 or other existing autocrafting systems.
- [ ] Confirm one gear consumes four plates and one previous-tier gear, and
  one iron-upgrade token consumes eight iron blocks and its centre ingredient.

## Sophisticated Storage in JEI

The headless regression exercises the actual pinned Core/JEI focus-link path.
The following GUI and full-pack checks require a graphical client and remain
unchecked until performed.

- [ ] Verify `qm-storage-jei-compat-0.4.17.jar`, Core 1.5.14.2413 and
  JEI 19.64.0.455 in the client. Fully restart it and join the test world.
- [ ] Open the basic-to-iron chest, barrel, shulker and all four limited-barrel
  recipes, plus the three double-chest upgrades. No listed storage recipe
  should show the broken-recipe banner or the focus-link exception in the log.
- [ ] Check recipe lookup and uses for plain, named and coloured storage.
  Each displayed source must correspond to the expected result variant.
- [ ] Transfer a `US` and a `USU` recipe into a crafting grid. Verify the
  required upgrade item(s), result and quantities; also inspect a full 3x3
  copper-to-iron recipe.
- [ ] Craft a named/coloured container containing a known inventory and
  upgrades. Verify retained components, contents and consumed materials.
- [ ] Repeat recipe display and lookup after `/reload` and after reconnecting
  to a dedicated server without the client-only compatibility module.
- [ ] If any broken-recipe diagnostics remain, record each recipe ID and its
  complete exception. The supplied extract omitted three nonstorage errors.

## Quest reward quantities

- [ ] In the 0.4.19 quest book, no reward catalogue contains a backpack, boat,
  raft or Masa. Claim rewards individually and through Claim All; none of those
  items should be awarded, including after manufacturing them.
- [ ] Check the replacement entries and quantities per draw: 8 golden carrots,
  8 experience bottles, 16 redstone, 8 gold ingots, 4 reinforced alloys and
  2 atomic alloys in their respective pools. Repeated stackable draws may
  combine several batches in the delivered reward.
- [ ] Before manufacturing each alloy, it remains locked even when the team has
  a high reward level or possesses a sample. Genuine reinforced-alloy production
  unlocks its prize only at reward level 2 or above; atomic alloy needs its own
  production and level 3 or above. Below the required level, production alone
  must not unlock the prize.
- [ ] Other unstackable equipment, such as an Ex Deorum crook or hammer, remains
  limited to one of each identical item per pack. Ordinary material batches,
  fixed crafting refunds and cube quantities match the book.
- [ ] Reopen the test save and reconnect: already claimed rewards stay claimed,
  all 1,643 existing reward IDs retain their history, production unlocks survive,
  and the updated catalogue still matches the server.

## Public alpha criteria

Require a clean import, successful graphical launch, a tested early survival route, persistent quest progress and no known blockers on that route. Every advertised mode needs its own completed test. Release notes must list known issues and untested branches.

Later readiness requires longer playthroughs of all branches, cross-branch transitions, recipe/reward regression and migration tests on a copied world. Quest count alone is not a readiness criterion.

[Українська](PLAYTEST.uk.md)
