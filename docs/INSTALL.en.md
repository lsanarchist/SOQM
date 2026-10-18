# Installing SOQM

**Shout Out to Questing Mayhem** targets Minecraft **1.21.1**, **NeoForge 21.1.262** and **Java 21**. Keep the release's pinned mod versions: updating individual mods can change recipes, dependencies and quest behavior.

1. Download [SOQM-0.1.0-alpha.1-curseforge.zip](../downloads/SOQM-0.1.0-alpha.1-curseforge.zip) from the GitHub repository’s `downloads/` directory using **Download raw file**. Verify it against [SHA256SUMS](../downloads/SHA256SUMS). GitHub's automatic `Source code` archives and the original Questing Mayhem source archive are not installable profiles.
2. In CurseForge App, open Minecraft → **Import** → **Import Profile .zip** → **Choose .zip file**, then select the ZIP. Wait for every dependency to download. See the [official import instructions](https://support.curseforge.com/support/solutions/articles/9000197912). Importing a ZIP does not require a CurseForge catalog listing.
3. Check Java 21 and the profile's memory allocation. **6 GiB** is a starting recommendation for testing, not a measured system requirement. Leave sufficient RAM for your OS and launcher.
4. Launch, reach the main menu and create a separate test world. Check the quest book, recipes and saving using the [playtest checklist](PLAYTEST.en.md).

If a dependency fails to download, inspect the import log and use that mod's official file page. Do not substitute an arbitrary version. Report the SOQM version, affected filename and exact error.

## Updating

Close the game and back up the **entire profile**, including `saves/`, `config/`, `defaultconfigs/`, `local/`, `options.txt` and any map data. Keep the previous release ZIP. Store backups outside the profile directory.

Import the new release into a **new profile**. Test a copy of your world first: loading, quests, progress, inventory and key recipes. World migration is supported only where the release notes explicitly say so. Roll back using a backup, rather than opening an already updated world in an older version.

The existing `qm` namespace, `qm_*` filenames and stable quest IDs are retained from Questing Mayhem to preserve internal references and recorded progress. This does not guarantee compatibility with every historical world.

## Reporting a crash

Include the release version, OS, Java version, allocated RAM, reproduction steps, `logs/latest.log` and the relevant `crash-reports/` file if present. Remove personal information, private server addresses and tokens before publishing. Inspect world contents before sharing a save.

[Українська](INSTALL.uk.md)
