# SOQM 0.1.0-alpha.1 — verification

Fresh checks on 2026-10-18; historical baseline claims are not used as proof of
the changed SOQM payload.

- **Offline checks:** 14 Python tests, the JavaScript equipment/loot policy
  regression, complete Ukrainian keys and deterministic English-guide freshness.
  The campaign has 1,625 quests, 37 chapters, 3,905 dependency edges and no cycles,
  missing dependencies or duplicate persistent IDs.
- **Packaging:** source payload hashes, the authored-JAR allowlist, ZIP CRC and
  exact contents, plus identical bytes after rebuilding on the same Python/zlib
  runtime and after changing checkout paths/file metadata.
- **Downloads:** [downloads.json](downloads.json) verifies all 111 exact public
  third-party files by SHA256 and size. No third-party JAR is bundled.
- **Native server:** [smoke-server.json](smoke-server.json) passes against the
  exact final game payload and metadata: startup to `Done`, a real data reload
  with **20,074 → 20,074 recipes**, zero KubeJS startup/server errors, saved
  shutdown and Java exit 0. It used 100 server-compatible third-party files and
  seven authored modules; 11 client-only third-party files and the zoom module
  were excluded.

The server console contained one nonfatal upstream Relics error about a skipped
client Screen mixin. Bloxy's Structures also emitted a mob-category warning.
Ex Deorum and Millénaire added omitted default configuration keys while
preserving every authored value. These diagnostics are recorded explicitly in
the native report; the result does not claim a completely clean third-party
console.

**Still unverified:** clean launcher import through the graphical UI, rendering,
actual client keys and equipment slots, full campaign progression, multiplayer
balance, long-term economy and client performance. Compatibility JAR hashes and
recovered source provenance are checked; independent recompilation of all eight
is not yet reproduced. English is an objective guide, not a complete editorial
translation of the Ukrainian narrative and humor.

Reproduce offline checks with `./scripts/check`; verify downloads with
`python3 scripts/soqm.py verify-downloads`. The dedicated-server harness requires
Java 21 and explicit `--accept-eula`, as described in the release documentation.
