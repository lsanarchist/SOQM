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

## Public alpha criteria

Require a clean import, successful graphical launch, a tested early survival route, persistent quest progress and no known blockers on that route. Every advertised mode needs its own completed test. Release notes must list known issues and untested branches.

Later readiness requires longer playthroughs of all branches, cross-branch transitions, recipe/reward regression and migration tests on a copied world. Quest count alone is not a readiness criterion.

[Українська](PLAYTEST.uk.md)
