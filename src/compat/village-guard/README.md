# Village Guard

This adapter protects peaceful villagers and registered Millenaire settlements
from mob aggression, damage and griefing. Its native compatibility contract is
Minecraft 1.21.1, NeoForge 21.1.262 and Millenaire 9.0.2.

## Target tracking and village lookup

Target writes provide the initial evidence: NeoForge's target-change event,
Minecraft's `Brain` attack-memory setter and Millenaire's direct combat setter
check a proposed village target. A sparse set retains attackers that can cross
a protection boundary while pursuing a villager. The server checks those
ongoing encounters; there is no `EntityTickEvent` pass over every loaded mob.
Joining entities and replacement Brains receive an initial observation.
Owner information lives on the Brain rather than in a global Brain registry.
An ongoing encounter survives a live entity becoming temporarily hidden by
section visibility. Actual removal or level unload releases its tracking state.

The spatial index belongs to the native village manager. Saved villages are
indexed when needed; subsequent village and building mutations mark affected
records for update. Culture-definition changes coalesce into a geometry rebuild
on the next lookup. A live `invalidateAll()` keeps ongoing target encounters
registered while invalidating geometry. Unchanged position lookups reuse the
index instead of rebuilding all villages every ten seconds. Removing a village
and unloading its level retire the corresponding cached state without requesting
chunk loads.

Protection still uses native village type, peaceful/hostile and raider status,
the village radius and building footprints. Target changes and damage/griefing
events enforce that policy separately, including explosion edges. Unrelated
player targets retain their normal targeting and navigation behavior.

## Build and reproduce

Prepare the pinned mod cache using `python3 scripts/soqm.py verify-downloads`
and provide a Java 21 JDK plus an installed NeoForge 21.1.262 server runtime.
Run from the repository root:

```sh
python3 src/compat/build_optimizations.py \
  --module village-guard \
  --runtime /path/to/native-runtime \
  --javac /path/to/jdk-21/bin/javac \
  --output-dir .work/village-guard/build-a

python3 src/compat/build_optimizations.py \
  --module village-guard \
  --runtime /path/to/native-runtime \
  --javac /path/to/jdk-21/bin/javac \
  --output-dir .work/village-guard/build-b

cmp .work/village-guard/build-a/qm-village-guard-0.4.3.jar \
    .work/village-guard/build-b/qm-village-guard-0.4.3.jar
```

The builder verifies the mapped server and dependency hashes, then verifies the
exact bytecode of Village Guard's seven vanilla/Millenaire hook targets before
compilation. These additional hook checks apply only to this module. Its JSON
result records compiler version, target hashes and the produced JAR's hash.
Reproducibility comparisons use the same compiler and Python/zlib runtime.

Compilation and byte-for-byte builds do not establish gameplay correctness.
The [native validation workflow](../../../docs/RUNTIME_VALIDATION.md) covers
the loaded hooks and behavior. Index rebuild and target-check counters measure
work avoided by this adapter; they do not measure whole-server TPS, client FPS
or throughput on a populated multiplayer world.
