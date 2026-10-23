# Installing SOQM 0.4.19

Use a purchased Minecraft Java Edition account, **Minecraft 1.21.1**, **NeoForge
21.1.262** and **Java 21**. Keep the exact versions and hashes in
`metadata/mods.lock.json`; arbitrary individual updates can break progression.

## Current package: CI candidate or local build

On GitHub, open **Actions → Validate and package** and choose the successful
run for the intended commit/PR. Download `SOQM-candidate-<commit>` and extract
GitHub's outer ZIP. It contains `SOQM-0.4.19-curseforge.zip` and `SHA256SUMS`;
import that inner installation ZIP into the launcher. CI artifacts remain
available for 14 days and can be reproduced locally afterward.

From the repository root, with Python 3.11+ and Node.js 18+:

```bash
./scripts/check
python3 scripts/soqm.py verify-downloads
python3 scripts/soqm.py build
```

The current output is `dist/SOQM-0.4.19-curseforge.zip`, with its checksum under
`dist/`. A separate public 0.4.19 asset is not claimed. The
[original published alpha](../downloads/README.md) is the historical
0.1.0-alpha.1 package, not this version. GitHub's automatic `Source code` ZIP
and the baseline Sources ZIP are not installable game profiles.
The build assembles the quest translation fragments into the two runtime
language files. Use the built ZIP for installation; copying `overrides/`
directly from a source checkout omits those generated translations.

## Fresh CurseForge App profile

1. Open Minecraft → **Import** → **Import Profile .zip** → **Choose .zip file**,
   select the built 0.4.19 ZIP and wait for every download. See the
   [official instructions](https://support.curseforge.com/support/solutions/articles/9000197912).
   Importing a ZIP does not require a CurseForge catalog listing.
2. Check Java 21 and memory allocation. **6 GiB** is an initial testing
   recommendation, not a measured system requirement. Leave RAM for your OS.
3. Reach the menu, create a separate test world and check quests, recipes and
   reloading using the [playtest checklist](PLAYTEST.en.md).

If a dependency fails to download, inspect the import log and its exact official
file page from the lock file. Do not substitute an arbitrary version.

## Consumable quest rewards in 0.4.19

Backpacks, boats and rafts are completely excluded from quest rewards and the
in-book reward catalogue. Their former entries now offer useful consumables:
eight golden carrots, eight bottles of experience, sixteen redstone, eight gold
ingots, four reinforced alloys or two atomic alloys per draw, according to the
pool. These are possible prizes; a pack does not grant every listed item.
Masa remains excluded.

Reinforced alloy requires reward production level 2 and recorded manufacture of
that exact alloy by you or your team. Atomic alloy similarly requires level 3
and its own manufacture record. Possessing an alloy or reaching a high level
alone does not unlock it. Other unstackable equipment retains the one-copy limit
per identical item per pack. Stackable repeats combine, while fixed crafting
refunds and cube quantities retain their existing rules.

Import the complete 0.4.19 candidate, or update the built ZIP's `kubejs/` and
generated quest language files on both stopped client and server. Replace
`qm-quest-ui-0.4.6.jar` with `qm-quest-ui-0.4.7.jar` in each installation and
remove older duplicate versions. Restart both sides to load the reward script
and updated catalogue. All **1,643 existing outer reward IDs** and personal
claim history are preserved; already claimed rewards stay claimed. Quest
progress and production evidence need no reset.

## Crafting fixes in 0.4.19

The 0.4.19 candidate includes distinct manual iron/gold gear layouts and a
copper-ingot centre for the Copper-to-Iron Furnace Upgrade. Update the built
candidate's `overrides/kubejs/` files on both client and server while stopped,
or import the complete candidate into a new profile. Restart both sides, use
JEI to check the new layouts and record affected autocrafting patterns again.
All seven gear variants still use four plates and the same previous-tier gear;
both iron furnace upgrade tokens still cost eight iron blocks.

The package includes `qm-foundry-compat-0.4.15.jar` for **Modern Foundry 4.1.6**.
It corrects the station's ingredient-slot writes when taking a crafted result,
including bulk sand-to-sandstone crafting. For an existing 0.4.14 installation,
close the game and server, add this JAR from the candidate's `overrides/mods/`
to **both the client and server** `mods/` directories, then restart. An imported
0.4.19 candidate already includes it. The remaining third-party pins are the
same as 0.4.14.

The module deliberately requires Modern Foundry 4.1.6. Remove it before a
coordinated client/server upgrade to an upstream release containing the fix,
such as 4.1.8. Until the hotfix is installed, use the ordinary Minecraft
crafting table for this recipe. Changing Ctrl bindings or IPN refill settings
does not repair the faulty station code. See the
[cause and validation](CRAFTING_STATION_FIX.md) and
[counted playtest](PLAYTEST.en.md#modern-foundry-crafting-station).

## Sophisticated Storage displays in JEI

SOQM 0.4.19 includes `qm-storage-jei-compat-0.4.17.jar`. It fixes the
`All slots must have the same number of ingredients` exception when JEI
builds compact storage upgrade recipes. For an existing 0.4.14–0.4.16 profile
with the pinned mods, close Minecraft, copy this JAR from the candidate's
`overrides/mods/` into the **client** `mods/` folder, then fully restart the
client. Every player needs this display fix in their own profile. The
dedicated server does not need it; SOQM's server materializer excludes it.

The adapter requires **Sophisticated Core 1.5.14.2413**, **JEI 19.64.0.455**,
Minecraft 1.21.1 and NeoForge 21.1.262. Core reports runtime version `1.5.14`;
the exact archive is pinned in the pack. Keep these dependencies together.
Review or remove the adapter before a coordinated dependency update. The fix
maps the storage ingredient to its actual JEI slot; recipe costs and storage
state transfer are unchanged. This client JAR alone does not install the
earlier Foundry or workshop recipe fixes; import the complete 0.4.19 candidate
to receive all three changes.

See [cause, native reproduction and limits](STORAGE_JEI_FIX.md) and the
[graphical checks](PLAYTEST.en.md#sophisticated-storage-in-jei).

## Inventory sorting

Inventory Profiles Next sorts inventory slots with the **middle mouse button**
in SOQM. This leaves **R** for JEI recipes and **U** for item uses. Open the IPN
settings with **R+C** to customize shortcuts, locked slots and item transfers.

The client manifest includes Inventory Profiles Next, libIPN and Kotlin for
Forge at their pinned versions. This dependency chain is only needed by the
client in SOQM and is excluded by the dedicated-server harness.

## Dynamic lighting

The client manifest includes [LambDynamicLights 4.8.11+1.21.1](https://www.curseforge.com/minecraft/mc-mods/lambdynamiclights/files/8780227).
Hold a torch or lantern in either hand to light the area around you. New profiles
use the mod's standard enabled settings. Open **Video Settings** or the mod's
configuration from **Mods** to adjust dynamic lighting; see the
[author's instructions](https://lambdaurora.dev/projects/lambdynamiclights/).

This is a client-side visual effect. Each player needs the mod on their own
client; a dedicated server does not need it. The official JAR includes its
required NeoForge libraries, and SOQM keeps existing personal lighting settings.

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
3. Install the **129** exact third-party JARs referenced by the manifest/lock
   from their official sources and verify versions and SHA-256. Together with
   the package's **14** authored modules, these form the SOQM mod set. Remove
   duplicates and superseded mod versions only in the test copy; do not mix releases.
4. Preserve saves, personal key bindings and map data. Start with a fresh test
   world, then check a copied save for progress, inventory and working recipes.

Copying files does not prove a successful client launch. Roll back using the
instance backup; do not open an already upgraded save in an older version.

## World migration

**If upgrading from 0.4.9 or earlier, follow [Storage migration](STORAGE_MIGRATION.md).**
Empty and remove Iron Chests in a backup of the old instance while its mod is
still installed. Check placed containers, carried items, other dimensions and
offline players. Removing the mod does not transfer the stored items.

The original quest, task and reward IDs are retained. The additional HDPE and
Mekanism equipment lessons bring the totals to **1642 quests,
1927 tasks and 1643 outer rewards**. The six former Iron Chests objectives now
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

For **0.4.10 → 0.4.11**, keep the saved world and quest progress, replace the
complete pack payload and restart both client and server. The 67 Overworld visit
objectives remain available as optional rewards, and no longer block follow-up
quests. New treehouses, planetary structures and underground rooms require new
chunks. Aether gloves go in Aether's cloud-button inventory (default key `I`),
not Curios Hands; the new tooltips show the configured key. See
[optional exploration](OPTIONAL_EXPLORATION.md), [loot](EXPLORATION_LOOT.md)
and [equipment](ACCESSORY_POLICY.md).

For **0.4.11 → 0.4.12**, replace the complete payload in a copy of the profile
and restart both client and server. New recipes affect future crafts; existing
machines and containers remain. Fourteen MekaSuit, MekaTool and module quests
move to chapter 07 with their original IDs. Fifteen new lessons have separate
IDs. Twenty-nine existing milestones give two cubes for still-unclaimed rewards,
bringing the full campaign from 193 to 222 cubes. Previously claimed personal
rewards stay claimed and receive no automatic top-up. The updated Quest UI
client module shows separate refund icons and the possible-prize catalogue.
Opening a preview does not claim a reward.
For a manual update to the current 0.4.19 candidate, remove older
`qm-quest-ui-*.jar` versions from the copied profile and keep only
`qm-quest-ui-0.4.7.jar` from the package. Copying files into an existing directory
does not remove a differently named previous version.

For **0.4.12 → 0.4.13**, update the client profile to the new package so the
launcher downloads the pinned LambDynamicLights JAR. With a manual installation,
add the exact file linked above to the client's `mods/` directory and restart
the client. This update adds no server dependency or world migration step.

For **0.4.13 → 0.4.14**, update the pack and fully restart both client and server.
For a manual update, remove `mods/qm-village-guard-0.4.2.jar` and install
`qm-village-guard-0.4.3.jar` from the new package; keep only one version.
Village protection now updates from target and settlement changes. Existing
settlements are indexed when needed, with no world conversion or new chunks
required for the optimization.

Report problems with the SOQM version/commit, launcher, OS, Java, RAM allocation,
reproduction steps, `logs/latest.log` and any crash report. Remove tokens,
personal information and private server addresses before sharing.

[Українська](INSTALL.uk.md)
