# Runtime optimizations 0.4.7

Original SOQM compatibility code, licensed under the full MIT notice in
`LICENSE`. It targets the pinned Minecraft 1.21.1 / NeoForge 21.1.262 pack and
does not contain copied Abandoned Tunnels or Dungeons & Taverns implementation
classes. Install it on the client and server together with the updated KubeJS
overrides. Its entity-tag loot condition is a registered type.

## Changes and boundaries

- Cache duplicate Abandoned Tunnels height queries within one invocation of
  `findGenerationPoint`. The two heightmaps have separate caches, and the key
  includes full signed X/Z coordinates. The original queries, random draws and
  structure generation remain authoritative. `finally` restores the previous
  thread-local frame; at most 2,048 entries are retained during an invocation.
- Keep Mayhem guardian reward tickets and expiry deadlines in one versioned
  KubeJS persistent ledger. Migrate old records incrementally, expire missing
  entities' tickets, and track loaded entities through lifecycle events. Work
  budgets and a replaceable deadline index prevent both an unbounded cleanup
  pass and accumulation of obsolete queue entries.
- Observe Dungeons & Taverns discoveries on advancement events and reconcile
  existing history on login/reload. Preserve criterion IDs, rewards and the
  original OR requirements. The source player's trader reset affects only that
  player.
- Track relevant loaded wither skeletons and lightning bolts through lifecycle
  events. A tiny native dispatcher retains the original five-tick function
  phase, dimension scope and conversion functions without global selectors.
  Hidden but registered entities wait for visibility to return. Removed,
  canceled and duplicate-UUID insertions cannot remain pending or run effects.
- Replace the exact phantom-boss single-tag NBT predicate with direct entity
  tag containment, preserving its type and invisibility conditions. Other
  enchantment conditions and third-party JARs remain untouched.

The exact upstream version constraints are in `META-INF/neoforge.mods.toml`.
Mixins live only in `qm.performance.mixin`; helpers must remain outside that
reserved package. Injectors fail if required native callsites are absent.
Data transformations only apply to the known upstream condition shape.

## Build

Use a Java 21 JDK, a native server installed by `scripts/smoke_server.py`, and
the 122 verified dependencies in `.cache/mods`. From the repository root:

```sh
python3 src/compat/build_optimizations.py \
  --runtime /absolute/path/to/installed/server \
  --javac /absolute/path/to/jdk-21/bin/javac
```

This builds both `qm-runtime-optimizations-0.4.7.jar` and
`qm-quest-automation-0.4.3.jar`. `--module` selects one module and `--output-dir`
supports an independent reproduction without replacing production artifacts.
The build verifies dependency hashes, the mapped native server hash and the
Abandoned Tunnels target bytecode. It uses native APIs without Minecraft stubs,
fixed ZIP timestamps, stable entry order and embedded license notices.

The mapped server pin is
`f57ce3018e21459d24b50508e8c1b4fd5c16324ec1dc5f316c192aa0130e7fe0`.
The reviewed compiler is Eclipse Temurin JDK 21.0.12.1+1. Module sources and
production artifact hashes are recorded in `../provenance.json`.

## Native validation

```sh
python3 scripts/performance_probe.py \
  --runtime /absolute/path/to/installed/server \
  --java /absolute/path/to/jdk-21/bin/java \
  --accept-eula --baseline-ref main
```

Pass `--accept-eula` only after accepting the Minecraft EULA. The harness uses
a fresh localhost world, synthetic players and verified production artifacts.
It compiles the test-only fixtures against real mod APIs and never includes
that test JAR in the installation archive.

The worldgen fixture compares the actual mixed implementation with an isolated
loader for the original pinned upstream bytecode. It checks structure piece
NBT, random draws, query order, real terrain and cache lifetime/thread behavior.
Other fixtures exercise guardian persistence/migration and capture semantics,
Curios/Relics and native machine observations, D&T functions and enchantments,
then a real `/reload` and clean shutdown. These are behavioral and work-count
regressions, not an exploration-speed or FPS benchmark.

See [deployment and behavior notes](../../../docs/PERFORMANCE_OPTIMIZATION.md)
and [combined 0.4.8 verification](../../../reports/verification-pr5.md).
The [original 0.4.7 measurements](https://github.com/lsanarchist/SOQM/blob/307cf612ae9b7873df7646ab4769de8d2146e526/reports/verification-performance.md)
remain available with their historical source and payload hashes.
