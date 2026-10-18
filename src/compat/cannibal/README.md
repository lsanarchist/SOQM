# Cannibal compatibility 0.4.1

Targets Minecraft 1.21.1, NeoForge 21.1.262, Java 21, and the exact
Cannibal Villages 2.4.9 release pinned in `metadata/mods.lock.json`.

## Discovery wait fix

The upstream `StructureDiscoveryEvents.verificarVila` scans up to 121 nearby
chunks to show the first-discovery title and sound for three structure types.
Its `getChunk(x, z, ChunkStatus.FULL, false)` call can wait for an existing
chunk holder whose full chunk is still loading or generating. `false` avoids
creating an absent holder, but does not make this synchronous API nonblocking.

`StructureDiscoveryMixin` redirects only that invocation to
`level.getChunkSource().getChunkNow(x, z)`. An unavailable chunk returns null,
which the original scan already skips. Later scans can discover the structure
once its chunk is ready. The radius, cadence, 80-block discovery distance,
persistent flags, titles, and sounds use the original code. The two existing
chest-conversion mixins also remain registered.

The redirect requires exactly one matching call. The exact Cannibal dependency
pin and `require = 1, allow = 1` make target drift fail visibly at loading.

## Rebuild

Install the pinned [NeoForge 21.1.262 server runtime](https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.262/neoforge-21.1.262-installer.jar)
outside the repository, then use a Java 21 JDK:

```sh
python3 src/compat/cannibal/build.py \
  --runtime /path/to/neoforge-server \
  --javac /path/to/jdk-21/bin/javac
```

The build compiles all four module classes against the real patched Minecraft,
NeoForge, FML, and Mixin APIs. It checks the mapped server JAR's SHA-256 against
the same pin as SOQM's server harness. No Minecraft/API stubs or bundled
third-party classes are used. Annotation processing is disabled because the
production-name injectors explicitly use `remap = false`.

The output is `overrides/mods/qm-cannibal-compat-0.4.1.jar`. The ZIP has stable
entry ordering/timestamps and contains the original MIT notice and module
manifest. The recorded build uses Temurin Java 21.0.12.1+1; updating a production
JAR also requires refreshing authored-mod and source/payload provenance hashes.

## Evidence and scope

Two player-provided execution profiles showed this scanner under
`ServerChunkCache.getChunkBlocking` on the server thread:

| Profile | Discovery scan | Share of sampled server-thread wall time |
| --- | ---: | ---: |
| [4VzPbrUrnA](https://spark.lucko.me/4VzPbrUrnA) | 23,080 ms | 19.70% |
| [4SfpElO9Ce](https://spark.lucko.me/4SfpElO9Ce) | 36,976 ms | 31.55% |

These are elapsed-time samples including waits, not whole-machine CPU shares
or predicted speedups. The separate Millénaire spawning waits are outside this
patch. Post-patch travel performance and graphical title/sound playback need a
new in-game observation.

## Installation

With Minecraft/server stopped, replace `mods/qm-cannibal-compat-0.4.0.jar` with
`mods/qm-cannibal-compat-0.4.1.jar`. Keep a single version of this compatibility
module. Keep the original `cannibalvillages-2.4.9-v1.21.1.jar` installed.
The patch works with existing worlds and does not modify stored discovery data.

If `/guioff` was used as a temporary workaround, `/guion` restores the original
discovery notifications; restarting the world/server also resets that native
switch. The patched lookup makes globally disabling those messages unnecessary.
