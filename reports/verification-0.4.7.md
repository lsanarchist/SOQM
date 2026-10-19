# SOQM 0.4.7 verification record

**Date:** 2026-10-19. **Upgrade baseline:**
`e71db8398ddc9f07e85981fac942f52dd23f447d`.

This record summarizes an executed native test of the exact reviewed game
payload. Reproducing the commands creates fresh raw reports; it does not reuse
a historical `passed` flag. Installation-archive hashes are distributed beside
the archive, because documentation is part of its bytes.

## Offline and package gates

- **48 Python tests** passed, including persistent ownership/claim identities,
  directed acyclic dependencies, early AE2 and space gates, actual recipe
  requirements, reward budgets, locale coverage, geometry, idempotence and
  packaging boundary tests.
- The Node equipment/accessory/loot policy regression passed.
- Campaign, English locale, reward tables and both coordinate SVGs match their
  deterministic generators.
- All **122 third-party pins** passed size, SHA-256 and archive checks. The
  production payload has **1,843 files**, including the same **10 authored JARs**.
- Installation packaging checks CRC, exact file contents, the authored-JAR
  allowlist and byte-identical rebuilding on the same Python/zlib runtime.

## Final native execution

Java **21.0.12.1**, Minecraft **1.21.1**, NeoForge **21.1.262**, FTB Quests
**2101.1.36**, AE2 **19.2.18**. The harness verified the reviewed lock and every
pinned mod, then compiled its own temporary Java fixture. The temporary test
JAR is excluded from production and packaging.

The final invocation used the Java 21 JDK, an installed verified NeoForge
runtime, `--baseline-ref e71db8398ddc9f07e85981fac942f52dd23f447d` and
`--accept-eula`. It did **not** enable `--allow-unlocked-payload`.

| Check | Executed result |
| --- | --- |
| Fresh dedicated-world startup | Passed |
| Native random-reward NBT + registry-aware packet round trips | 1,611 |
| Resolved weighted entries | 8,055 |
| Distinct pools with real inventory delivery + duplicate rejection | 14 |
| Old claimed rewards preserved + still blocked | 1,626 |
| Old task values preserved | 42, including 6 partial |
| Team claims shared with second player UUID | 1,625 |
| Separate personal book claim for second UUID | 1 |
| Ordinary crafting menu, unpowered network, empty storage | All rejected for ME milestone |
| Actual AE2 terminal output click in powered stocked network | Advancement granted |
| Existing native FTB advancement task | Rejects unearned; accepts earned criterion |
| Unrelated ME stored items | Preserved |
| Independent native advancement disk reader | Passed, including missing-file negative control |
| Nonempty TeamData disk reload and server resource reload | Passed |
| Recipes before → after reload | 20,077 → 20,077 |
| KubeJS startup errors / server errors / fatal server errors | 0 / 0 / 0 |
| All dimensions saved; normal exit | Yes, code 0 |
| Forced termination | No |
| Production payload changed during execution | No |
| Elapsed time | 83.77 s |

The ME fixture marks its four prerequisites complete to model an eligible
player. The target task and advancement start unearned and must be earned by
the real terminal craft. Synthetic fixtures isolate native behavior; they do
not measure survival crafting time or require access to an existing player save.

## Evidence fingerprints

| File/evidence | SHA-256 |
| --- | --- |
| `metadata/payload.sha256.json` | `bdbedf5878bf1fd87bc91bbc0530f427b102c08c730f80dbe7ffdbba95ffd2b3` |
| Executed raw `reports/runtime-probe.json` | `a39916c9b4ae7f1c78cea577b6c1b928ce061ee32c75b54e7aafa9a301557434` |
| `scripts/runtime_probe.py` | `e1b9448e2c81f46d5acbea9391d9fdbe6e2bf8d487d1e174c2fd316af30e092f` |
| `tests/runtime/SoqmRuntimeProbe.java` | `97e1b353c443ea055c9510e645aca4f475c5c4908de28bba207ed4d8690ba74d` |
| Production ME script | `7ebd9b1361c69daf7a7207bd30905d4d9adbdac4371e2d31fb6051c39227425e` |
| Production ME advancement | `05aa8f1a0df4adde334d72f85977392d811fca8029e6e758233ec12d4b27c682` |
| Native server console | `432ae2050e0e4173f392867d14851120f2734676ab088b418362f0fa02135600` |

Local native workspace: `.work/runtime-probe/run-20261019T105521.482698Z`.
Raw reports and runtime worlds are ignored local evidence, not shipped game
content. The committed harness and fixture reproduce these checks with the
pinned inputs. See [RUNTIME_VALIDATION](../docs/RUNTIME_VALIDATION.md).

## Performance and graphical scope

The current Cannibal **0.4.1** hotfix is preserved and ran in the full pack.
A previous standalone smoke also passed, but used the preceding map snapshot;
its raw report SHA-256 is
`08c0cd5f8a043a55b8286d9aa86d95ee2fd3367ed949b49f2129d4f083ce5451`.
The final interaction run above is the authoritative current-payload server
result and includes startup, reload and shutdown itself.

Known nonfatal upstream Relics/Bloxy diagnostics and default-config messages
remain; this is not a warning-free console. No FPS/TPS improvement, complete
survival playthrough or full graphical client validation is claimed. The two
SVGs are exact-coordinate layout reviews, not Minecraft screenshots.

Millénaire generation was inspected, including its selection and retry flow.
No village-skipping or simulation-changing shortcut was introduced. Its
remaining performance question requires a measured exploration profile;
[the native validation notes](../docs/RUNTIME_VALIDATION.md#exploration-performance-remains-a-separate-experiment)
explain why a generic early chunk guard would not preserve existing behavior.
