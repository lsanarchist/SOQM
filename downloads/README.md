# SOQM installation packages

## Current: 0.4.7 — CI candidate and local build

Each successful **Validate and package** Actions run uploads an installation
candidate named `SOQM-candidate-<commit>`, containing the ZIP and `SHA256SUMS`.
Choose the intended PR/commit, extract GitHub's outer artifact archive, then
import the inner `SOQM-0.4.7-curseforge.zip`. Retention is 14 days. A candidate
is tied to its tested source revision; release publication remains separate.

The current installation ZIP is generated locally as
`dist/SOQM-0.4.7-curseforge.zip`. From the repository root:

```sh
./scripts/check
python3 scripts/soqm.py verify-downloads
python3 scripts/soqm.py build
```

The build writes the archive and checksum under `dist/`. These outputs are not
committed. **No separately published 0.4.7 download asset is claimed.**
Import the generated ZIP in a CurseForge-manifest-compatible launcher, or follow
the manual MultiMC guidance. Minecraft Java Edition and Java 21 are required.
GitHub's automatic source-code ZIP is not an installation package.

[Українська](../docs/INSTALL.uk.md) · [English](../docs/INSTALL.en.md) ·
[Verification status](../reports/README.md)

## Historical: 0.1.0-alpha.1

[Download the original 0.1.0-alpha.1 ZIP](https://raw.githubusercontent.com/lsanarchist/SOQM/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833/downloads/SOQM-0.1.0-alpha.1-curseforge.zip) ·
[Historical SHA256SUMS](https://raw.githubusercontent.com/lsanarchist/SOQM/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833/downloads/SHA256SUMS)

These immutable links point to commit
[`6c4c7c4`](https://github.com/lsanarchist/SOQM/commit/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833).
That archive uses the earlier 0.4.4 baseline and is **not SOQM 0.4.7**.
Its checks and published bytes remain historical evidence.
