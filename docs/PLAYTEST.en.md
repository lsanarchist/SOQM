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

## Public alpha criteria

Require a clean import, successful graphical launch, a tested early survival route, persistent quest progress and no known blockers on that route. Every advertised mode needs its own completed test. Release notes must list known issues and untested branches.

Later readiness requires longer playthroughs of all branches, cross-branch transitions, recipe/reward regression and migration tests on a copied world. Quest count alone is not a readiness criterion.

[Українська](PLAYTEST.uk.md)
