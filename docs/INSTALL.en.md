# Installing SOQM 0.4.10

Use a purchased Minecraft Java Edition account, **Minecraft 1.21.1**, **NeoForge
21.1.262** and **Java 21**. Keep the exact versions and hashes in
`metadata/mods.lock.json`; arbitrary individual updates can break progression.

## Current package: CI candidate or local build

On GitHub, open **Actions → Validate and package** and choose the successful
run for the intended commit/PR. Download `SOQM-candidate-<commit>` and extract
GitHub's outer ZIP. It contains `SOQM-0.4.10-curseforge.zip` and `SHA256SUMS`;
import that inner installation ZIP into the launcher. CI artifacts remain
available for 14 days and can be reproduced locally afterward.

From the repository root, with Python 3.11+ and Node.js 18+:

```bash
./scripts/check
python3 scripts/soqm.py verify-downloads
python3 scripts/soqm.py build
```

The current output is `dist/SOQM-0.4.10-curseforge.zip`, with its checksum under
`dist/`. A separate public 0.4.10 asset is not claimed. The
[original published alpha](../downloads/README.md) is the historical
0.1.0-alpha.1 package, not this version. GitHub's automatic `Source code` ZIP
and the baseline Sources ZIP are not installable game profiles.

## Fresh CurseForge App profile

1. Open Minecraft → **Import** → **Import Profile .zip** → **Choose .zip file**,
   select the built 0.4.10 ZIP and wait for every download. See the
   [official instructions](https://support.curseforge.com/support/solutions/articles/9000197912).
   Importing a ZIP does not require a CurseForge catalog listing.
2. Check Java 21 and memory allocation. **6 GiB** is an initial testing
   recommendation, not a measured system requirement. Leave RAM for your OS.
3. Reach the menu, create a separate test world and check quests, recipes and
   reloading using the [playtest checklist](PLAYTEST.en.md).

If a dependency fails to download, inspect the import log and its exact official
file page from the lock file. Do not substitute an arbitrary version.

## Inventory sorting

Inventory Profiles Next sorts inventory slots with the **middle mouse button**
in SOQM. This leaves **R** for JEI recipes and **U** for item uses. Open the IPN
settings with **R+C** to customize shortcuts, locked slots and item transfers.

The client manifest includes Inventory Profiles Next, libIPN and Kotlin for
Forge at their pinned versions. This dependency chain is only needed by the
client in SOQM and is excluded by the dedicated-server harness.

## Existing MultiMC profile

The CurseForge ZIP contains a manifest and `overrides/`; it is not a standalone
MultiMC profile export. Direct import of this ZIP into MultiMC is not claimed
as verified. Manual installation requires the exact game/loader/Java above.

1. Close the game and back up the **entire instance** outside its directory,
   including `saves/`, `options.txt`, `config/`, `defaultconfigs/`, `local/` and
   any map data. Keep the original for rollback.
2. Work on a copied profile. Extract the **built** ZIP temporarily and merge
   the contents of `overrides/` into its `.minecraft/`, applying the pack files.
   Do not copy the manifest itself or a GitHub source archive into that folder.
3. Install the **124** exact third-party JARs referenced by the manifest/lock
   from their official sources and verify versions and SHA-256. Together with
   the package's **12** authored modules, these form the SOQM mod set. Remove
   duplicates and superseded mod versions only in the test copy; do not mix releases.
4. Preserve saves, personal key bindings and map data. Start with a fresh test
   world, then check a copied save for progress, inventory and working recipes.

Copying files does not prove a successful client launch. Roll back using the
instance backup; do not open an already upgraded save in an older version.

## World migration

**Before installing 0.4.10, follow [Storage migration](STORAGE_MIGRATION.md).**
Empty and remove Iron Chests in a backup of the old instance while its mod is
still installed. Check placed containers, carried items, other dimensions and
offline players. Removing the mod does not transfer the stored items.

The original quest, task and reward IDs are retained. The optional Cardboard Box
lesson and additive construction checklists bring the totals to **1626 quests,
1911 tasks and 1627 outer rewards**. The six former Iron Chests objectives now
detect Sophisticated Storage items under the same IDs. Claimed rewards stay claimed.
Previously claimed rewards are not issued again. Existing `qm`
namespaces and `qm_*` filenames preserve internal references and recorded
progress; they do not guarantee compatibility with every historical world.

Upgrading from 0.4.9 to 0.4.10 preserves personal claims and production discoveries.
Selected expensive craft quests add guaranteed ingredient returns alongside their
existing pack or cube. A still-unclaimed refund requires recorded manufacture of
its exact output by the current team. Outputs added to the production journal in
this update need one new manufacture if no prior evidence exists; completion or
ownership is not imported as proof. See [Rewards](REWARDS.md).

Since 0.4.8, upgrading from the older shared rewards gives every member one new
personal entitlement on already completed quests. Existing personal claims,
including the introductory book, remain claimed. Leaving or joining teams does
not restore a used personal claim. Historical shared claim keys remain stored.

For items made before the 0.4.8 journal existed, manufacture one new sample;
existing inventories do not prove production. Recorded 0.4.8 discoveries stay
unlocked. `/soqm rewards` shows the level,
and `/soqm rewards pool logistics` shows available prizes and current odds.
Replace the complete pack payload: the old generated
`config/ftbquests/quests/reward_tables/000000006A51*.snbt` files are retired;
the new catalogue is `kubejs/config/soqm_rewards.json`. Existing completed ME
terminal tasks retain progress; unfinished ones require a real active-network craft.

New loot balance applies when a targeted chest or new personal Lootr inventory
first generates its contents. Existing generated contents are not cleared.
Structure-template changes affect newly generated structures. Test new
containers and territory rather than expecting already opened chests to change.

Report problems with the SOQM version/commit, launcher, OS, Java, RAM allocation,
reproduction steps, `logs/latest.log` and any crash report. Remove tokens,
personal information and private server addresses before sharing.

[Українська](INSTALL.uk.md)
