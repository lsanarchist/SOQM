# Preparing a Release

A release must be reproducible from a specific commit with pinned dependencies. Passing local file checks does not establish graphical client compatibility or a completed campaign playthrough.

Run from the repository root:

```bash
./scripts/check
python3 scripts/soqm.py verify-downloads
python3 scripts/soqm.py build
```

The local check validates package structure and data consistency. `verify-downloads` separately checks pinned external files and requires network access. The current build output is `dist/SOQM-0.1.0-alpha.1-curseforge.zip`. Record these checks separately from an actual game launch. A blocked download is an incomplete check, not a reason to silently substitute versions.

Before publishing:

- Import the **built ZIP** into a clean profile using the [installation guide](INSTALL.en.md).
- Perform the [graphical smoke test](PLAYTEST.en.md). Record the version, commit, date, machine, Java, memory allocation and actual results.
- Inspect ZIP contents for worlds, logs, credentials, caches and local paths. Normal third-party mods are referenced through the manifest. Include other JARs and inherited assets only when redistribution rights are established; a repository license does not replace their licenses.
- Check sources and permissions for text, images, scripts and compatibility mods. An unresolved redistribution right blocks public release of the affected material.

The first package is tracked as [downloads/SOQM-0.1.0-alpha.1-curseforge.zip](../downloads/SOQM-0.1.0-alpha.1-curseforge.zip), with [downloads/SHA256SUMS](../downloads/SHA256SUMS). After the final build, update both from `dist/`, verify the checksum and commit them alongside their sources. Downloading does not require a separate GitHub Release. Future releases can tag a tested commit and attach the installable ZIP and checksum. State Minecraft/NeoForge/Java versions, player-facing changes, migration rules, completed checks and known issues. GitHub's automatic `Source code` archives are not installable packages.

Keep the release experimental until the [alpha criteria](PLAYTEST.en.md) are met. Historical Questing Mayhem reports apply to the files and versions they actually tested; the current package needs its own record. GitHub is used for version control and releases; verification runs locally without requiring GitHub Actions.

[Українська](RELEASE.uk.md)
