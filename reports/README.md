# Verification status

## Unreleased Cannibal 0.4.1 hotfix

The Cannibal compatibility module is rebuilt as
`qm-cannibal-compat-0.4.1.jar`, SHA-256
`b9e26d11bcb42a7b99889a4c5bc11c60398e2989d2849def0e88f428099517e8`.
The [module notes](../src/compat/cannibal/README.md) describe the chunk lookup
change, exact supported versions, installation and reproducible build command.

Validation of this hotfix completed on **2026-10-18**:

- All four module classes compiled against the real Minecraft 1.21.1 /
  NeoForge 21.1.262 APIs with Java 21. Two independent executions of the build
  command produced byte-identical JARs with the SHA-256 above.
- A real NeoForge 21.1.262 bootstrap with Cannibal Villages 2.4.9 and Lithium
  0.15.4 loaded all three target classes through the Sponge Mixin transformer.
  Inspection of the exported classes confirmed that the discovery scanner's
  single blocking `getChunk` call was replaced by one redirect to `getChunkNow`.
  Both existing chest-conversion cancellation mixins were also applied.
- The transformed discovery code retains its tick cadence, persistent flag
  write, 80-block distance limit, title and sound calls.
- All 14 Python tests, the JavaScript equipment/loot policy regression,
  Ukrainian locale coverage, English-guide freshness, payload validation and
  deterministic installation-ZIP packaging passed for the updated payload.

The bootstrap used a temporary test-only class-loading probe before the normal
EULA gate; the probe is not part of the shipped JAR. The process exited normally
with `eula=false` and created no world. This verifies actual Mixin application,
not gameplay, discovery notifications with a player, or a measured improvement
in TPS or chunk generation. The full-pack world/reload smoke below belongs to
the unpatched 0.4.6 baseline and was not repeated for this hotfix.

## SOQM 0.4.6 baseline

The current source uses the supplied Questing Mayhem Expert **0.4.6** archive:
`5c17c9030296cbbc4ce8a5c0e075c19f10366524a17f95f7fbcbe12c25604061`.
It has 122 pinned third-party dependencies and 10 authored modules. Expected
campaign preservation is 1625 quests, 37 chapters, 1772 tasks and 1626 reward IDs.

Run `./scripts/check` for local validation and packaging, and
`python3 scripts/soqm.py verify-downloads` for pinned download integrity.
Generated JSON reports under this directory are local output ignored by Git.
A report applies only when its recorded payload/metadata hashes match the files
being assessed. The 0.4.6 source archive's historical audits describe its own
inputs; they do not establish a fresh SOQM run.

Baseline local validation completed on **2026-10-18** for SOQM **0.4.6**,
before the Cannibal 0.4.1 hotfix:

- All 14 Python tests, the JavaScript equipment/loot policy regression,
  complete Ukrainian locale coverage and English-guide freshness passed.
- All 122 pinned third-party files passed SHA-256, size and archive checks.
- The installation ZIP passed CRC, exact-content/hash checks and a
  deterministic rebuild on the same Python/zlib runtime.
- A fresh isolated localhost dedicated server started, reloaded
  **20,077 → 20,077 recipes**, logged zero KubeJS startup/server errors,
  saved all dimensions and exited normally with code 0. Client-only files
  were excluded. The upstream console still includes the known nonfatal
  Relics client-mixin diagnostic; this was not a warning-free console.

The ignored `reports/downloads.json` and `reports/smoke-server.json` contain
current local evidence and exact input hashes. A local MultiMC file migration
establishes installation state; graphical startup and gameplay remain separate.

Still required: graphical launcher/client startup, rendering and keys, actual
play through progression, multiplayer balance, long-term economy and measured
client performance. English is an objective guide rather than an editorial
translation of the full Ukrainian narrative and humor. Independent recompilation
of all ten authored production JARs has not been demonstrated.

## Historical SOQM 0.1.0-alpha.1

The following checks ran on 2026-10-18 for the published alpha at commit
[`6c4c7c4`](https://github.com/lsanarchist/SOQM/commit/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833).
They apply to that earlier 0.4.4-based payload, not to current source revisions.

- **Offline:** 14 Python tests, JavaScript equipment/loot policy regression,
  Ukrainian locale coverage and deterministic English-guide freshness. The
  1625-quest graph had 3905 edges with no cycles, missing dependencies or
  duplicate persistent IDs.
- **Packaging:** payload hashes, authored-JAR allowlist, ZIP CRC/exact contents
  and deterministic rebuilding on the same Python/zlib runtime and changed paths.
- **Downloads:** [historical downloads.json](https://github.com/lsanarchist/SOQM/blob/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833/reports/downloads.json)
  verified all 111 exact third-party files by SHA-256 and size.
- **Native server:** [historical smoke-server.json](https://github.com/lsanarchist/SOQM/blob/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833/reports/smoke-server.json)
  recorded startup, a data reload with **20,074 → 20,074 recipes**, no KubeJS
  startup/server errors, saved shutdown and Java exit 0. The server included
  100 compatible third-party files and seven authored modules; client-only
  files were excluded.

The [historical release record](https://github.com/lsanarchist/SOQM/blob/6c4c7c43543a43ca772c5cd2af3e6f2ba10de833/reports/release.json)
pins that archive and its evidence hashes. Its console also recorded a nonfatal
Relics client-mixin diagnostic and a Bloxy's Structures mob-category warning;
it was not a completely warning-free upstream console.
