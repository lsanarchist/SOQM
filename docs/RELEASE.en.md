# Preparing SOQM 0.4.9

The current work updates source and builds an alpha locally. A commit does not
establish publication of a new installation asset, a push or a graphical playtest.

```bash
./scripts/check
python3 scripts/soqm.py verify-downloads
python3 scripts/soqm.py build
```

The local check validates structure, data and packaging. `verify-downloads`
requires a network connection and checks **125** pinned third-party files,
not arbitrary latest versions. `build` writes
`dist/SOQM-0.4.9-curseforge.zip` and a checksum under `dist/`. A failed download
is an incomplete check, not permission to silently substitute versions.

A separate native-server check requires Java 21 and an isolated world. After
reading and accepting the Minecraft EULA:

```bash
python3 scripts/smoke_server.py --java /path/to/java21 --accept-eula
```

Check the current bind address, port and behavior in `--help` and the harness.
`reports/smoke-server.json` must match the current payload/metadata hashes.
An existing report or historical `passed` flag is not a fresh result. Server
startup, reload and saving do not establish client rendering or campaign completion.

Before accepting the update:

- Install the **built ZIP** into a clean profile using the
  [installation guide](INSTALL.en.md). Check MultiMC file migration separately
  from an actual client launch.
- Perform the [graphical checklist](PLAYTEST.en.md), recording commit, ZIP
  SHA-256, environment, actual results and known issues.
- Inspect package contents and provenance: no worlds, accounts, logs, caches
  or third-party JARs that should be referenced through the manifest.
- Establish redistribution rights for inherited text, assets and modules;
  the repository license does not replace upstream licenses.
- Review the diff and `git diff --check` before committing. Generated ZIPs,
  JARs, source archives, caches and JSON reports remain local.

The **Validate and package** workflow runs local validation on PRs and main,
then uploads the installation candidate and SHA256SUMS for 14 days. Publishing
a separate release remains a separate action. The [immutable first-alpha links](../downloads/README.md)
remain historical; they do not provide SOQM 0.4.9.

For a future publication, pin the tested commit, ZIP and SHA-256. Distinguish
completed checks, historical source evidence and untested areas in the notes.
Do not replace a published archive: its documentation is included, so even a
documentation change alters its checksum. Keep the experimental label until
acceptance criteria are met.

[Українська](RELEASE.uk.md)
