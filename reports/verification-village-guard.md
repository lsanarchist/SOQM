# SOQM 0.4.14 — Village Guard verification

Validated on **2026-10-20**, based on main commit
`be66fb663b08f8e3cf8aa2c4254a914caa3f5629` (SOQM 0.4.13).
The production change replaces Village Guard 0.4.2 with **0.4.3**.
The reviewed game payload changes only that authored JAR. The pack retains
129 pinned third-party dependencies, 12 authored modules and 1,849 payload files.

## Change and behavior

Village Guard previously inspected every server mob on every entity tick and
rebuilt its complete settlement index after 200 ticks when queried. The new
adapter observes native target changes, Brain attack-memory writes and
Millénaire combat targets. It rechecks only ongoing encounters involving a
Millénaire villager, retaining boundary-crossing protection. Ordinary mobs
without such targets require no recurring protection lookup.

Each native village manager owns its index. Village membership and building
mutations invalidate only the affected village; repeated changes coalesce
until a lookup. Culture definitions invalidate a shared epoch, producing one
lazy reindex after a batch of changes. Geometry snapshots use exact rotated
footprints and release their listeners on replacement, removal, clear or unload.
Queries and index teardown do not request chunk loads.

The protection rules retain peaceful registered residents, distinguish actual
raiders from a guard profession's raider capability, and exclude lone-building
residents. Damage, hostile block changes and mob-attributed explosions retain
their existing policy. An explosion crossing a village boundary still affects
outside blocks. Clearing a protected target preserves an unrelated player
target and its navigation, including Millénaire's separate combat field.

## Native server gate

The final locked full-pack run passed **8,939 assertions across eight fixture
groups** in **74.89 seconds**. It used Java 21.0.12.1, Minecraft 1.21.1,
NeoForge 21.1.262 and Millénaire 9.0.2 in a new localhost world.
All 129 dependency hashes were verified before staging. The reviewed payload
matched its lock before the run and remained unchanged afterward.

| Native scenario | Observed result |
| --- | --- |
| Four building rotations, negative coordinates, circle and vertical boundaries | 8,820 footprint samples matched native geometry |
| 4,096 unchanged lookups after advancing the fixture clock by 20,000 ticks | 0 full reindexes and 0 village rebuilds |
| Building add, two coalesced moves, upgrade and removal among 65 villages | Only the changed village rebuilt; the other 64 did not |
| Same-ID replacement without changing village count | Old geometry and listeners retired immediately |
| Geometry registration, mutation and queries in distant unloaded terrain | 0 chunks loaded |
| 128 native zombie fixtures, 16 dispatched mob-tick/Brain-tick iterations each | 2,048 iterations; 0 recurring guard checks, observations or owner rebindings |
| One ongoing outside-village pursuit over 64 tracker ticks | Exactly 64 guard checks |
| Ordinary target, three Brain setter forms and Millénaire combat target | Protected targets rejected; boundary entry clears ongoing pursuit |
| Mixed target channels | Unrelated player targets, navigation and aggression retained |
| Actual entity-manager hide/show without a second join event | Tracking retained; real removal retired it |
| Damage and griefing | 3 native melee attempts, 8 damage-source cases, 4 explosion-source cases and block-event controls passed |
| Village manager clear and adapter level-unload handler | Listeners and tracked attackers released without duplicates on reacquisition |
| Vanilla `/reload` | Culture objects and existing protection retained; 0 unnecessary reindexes |
| Native `CultureLoader.loadAll()` | Existing village/building protection retained; all registry writes coalesced into 1 reindex |

The server loaded **20,056 recipes before and after `/reload`**, recorded zero
KubeJS startup/server errors and zero fatal server errors, saved all dimensions
and exited normally with code 0. No forced termination was needed.

Vanilla `/reload` does not reload Millénaire 9.0.2's culture registry. The fixture
checks object identity across that operation, then invokes Millénaire's native
clear/load/register sequence separately. It retains a detached native village
manager across both operations to test existing geometry without running
synthetic settlement construction in the live world.

The fixture dispatches selected native events and executes native setters,
Brain methods and melee calls. Work counts measure this adapter's avoided
operations. They are not whole-server TPS/FPS measurements, a natural spawning
benchmark, a graphical client playtest or a long survival/multiplayer session.

## Reproducible build and repository gate

The module compiled against the exact mapped server and all locked mod APIs.
The builder pins the bytecode of seven native Mixin targets. Sponge's actual
transformer applied all seven targets successfully; the final production JAR
also loaded and executed in the native server gate above. Two independent
builds with the same Java 21 compiler and Python/zlib runtime were byte-identical.

`./scripts/check` passed **162 Python tests with no skips**, both Node policy
suites, generated-content/locale checks and deterministic package validation.
All **12 coordinate previews** passed their freshness check. Final documentation
is included in the installation archive and checked by the packaging gate.

| Evidence | SHA-256 |
| --- | --- |
| `qm-village-guard-0.4.3.jar` — 25,199 bytes | `5972c1b1135c696f02479b5bc6c3f8ecd32b39f2a8d65e94a237151b0b78247b` |
| Production payload lock | `10b1c7e29581d0c60790a194609641a234bac935f99bf67c18b13117b5f28dfb` |
| Native fixture | `1aacb167cc14faeba6f30ea0bf7ad04295901847e515d55591095766b6becd35` |
| Focused harness | `ad401c6ee414b53fb5cff2f40b2f9e851d0d0ea6713e72b5cdce0e533e4e9666` |
| Final native console | `2c3ec87c14696370a21edadda748ba50a924e74b0a98afbc3df7dae4268b24f1` |

Run the [module build commands](../src/compat/village-guard/README.md), then:

```sh
python3 scripts/village_guard_probe.py \
  --accept-eula \
  --java /absolute/path/to/jdk-21/bin/java \
  --runtime /path/to/native-runtime

./scripts/check
python3 scripts/render_campaign.py --check
```

The focused fixture is `tests/runtime/villageguard/VillageGuardProbe.java`;
it is excluded from the general campaign fixture's source glob. Machine-readable
results are written to the ignored `reports/village-guard-probe.json`, including
every staged payload hash, source hashes, counters, recipe counts and exit status.
The final validation workspace was
`.work/village-guard-probe/run-20261020T191836.887354Z`.

For a manual client/server update, remove `qm-village-guard-0.4.2.jar`, install
the new 0.4.3 JAR and fully restart both sides. Existing settlements are indexed
when needed; this optimization requires no world conversion or new terrain.
