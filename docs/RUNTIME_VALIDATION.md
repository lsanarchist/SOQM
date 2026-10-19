# Native interaction validation

`scripts/runtime_probe.py` extends the dedicated-server smoke test with real
FTB Quests, team transitions, AE2/Mekanism production and Sophisticated Storage
operations. The harness compiles the three helpers in `tests/runtime/` against
the pinned Minecraft 1.21.1, NeoForge 21.1.262, AE2 19.2.18, FTB Quests
2101.1.36 and other locked mod APIs, including the authored production adapter.
Its test-only mod
is installed in an ignored, fresh localhost world. It is never copied into
`overrides/mods` or the distributable pack.

## Run the integration gate

Populate the SHA-256-verified dependency cache and create a native runtime with
the existing smoke harness. Both server commands require explicit acceptance
of the Minecraft EULA. Use the Java executable from a Java 21 **JDK**, since the
interaction harness also invokes `javac`.

```sh
python3 scripts/soqm.py verify-downloads
python3 scripts/smoke_server.py --accept-eula --java /absolute/path/to/jdk-21/bin/java
```

The smoke report records its `workspace`. Pass the `server` directory in that
workspace to the interaction probe and select the previous shared-reward commit.
For the 0.4.8 migration, use the merged 0.4.7 plus skills-fix base:

```sh
python3 scripts/runtime_probe.py \
  --accept-eula \
  --java /absolute/path/to/jdk-21/bin/java \
  --runtime .work/smoke/run-TIMESTAMP/server \
  --baseline-ref 72bd75d131a986afe9cf0d11580fdcaf009a208a
```

The default gate requires the production payload to match its reviewed lock,
verifies every third-party dependency and authored JAR, and checks that the
payload stays unchanged during the run. `--allow-unlocked-payload` is available
for development: its report explicitly records that exception and the exact
staged snapshot. Development results do not replace the final locked gate.

The report is `reports/runtime-probe.json`. Its workspace contains the compiler
log, server console, native result JSON, recipe-evidence copy and synthetic progress fixtures. Reports,
worlds, downloaded dependencies and test binaries are ignored by Git. The
server binds to `127.0.0.1`, requires online authentication for any external
connection and creates its synthetic player in process. It does not read
launcher accounts, existing worlds or real player state.

Synthetic players use an in-memory Netty channel and a test-only packet listener
that discards outbound interface packets. This does not simulate a client
handshake or certify client rendering. Reward packet serialization is exercised
separately through the native registry-aware buffers; all manufacture, team,
claim and persistence operations still execute in the real server runtime.

## What the native fixture checks

- All 1,626 outer rewards are personal and manual. The 1,611 native
  `CustomReward` definitions pass actual NBT and registry-aware packet round
  trips with their existing 64-bit IDs, tags and parent quest IDs intact.
- The actual schema-2 JSON must load all 14 themes and 326 entries, with
  localized milestone names and no emergency fallback. Every catalogue item
  and every cited production recipe is checked against the live registries
  after KubeJS recipe overrides; raw JAR presence alone is insufficient.
- Native `TeamData.claimReward` drives the real KubeJS callback and native
  `ItemReward` delivery. Two members take different independent results from
  the same quest, and each second claim must deliver nothing. Actual manufacture
  advances the tested pack from two to three to four draws. A high team level
  and inventory possession cannot unlock a still-unproduced expensive item.
- Actual grid crafts, Inscriber processor completion, Molecular Assembler
  output, Mekanism Infuser/Infusing Factory completion and a consumed in-world
  Sophisticated Storage tier upgrade establish proof. The production fixture
  also exercises reset/Name Press recipes, creative placement, nonproducing
  operations and captured machine-team ownership as negative cases.
- Personal production and the party union are separate. Joining shares the
  player's own discoveries; leaving retains only personal manufacture while
  the old party keeps its union. Creating another party must not launder copied
  FTB extra data into new discoveries. A machine captured for a former team
  cannot grant production to its owner's new party.
- Quest data from the specified base creates a nonempty native `TeamData`
  fixture with partial/complete tasks and all old rewards claimed. The 1,625
  historical shared `NIL_UUID` keys remain stored and intentionally yield one
  new personal entitlement for each member. Existing personal book claims
  remain claimed. Both members' second personal claims stay blocked.
- Personal claims are saved, then the member leaves and joins another team.
  An independent native reader checks persisted team data, so a cached live
  claim cannot hide a missing `markDirty()` after native FTB claim merging.
- The ME fixture places real AE2 cable, terminal, drive and storage cell blocks.
  It takes the recipe result through `CraftingTermSlot.doClick`, which invokes
  AE2's normal crafted-item event. An ordinary crafting menu, an unpowered
  network and an empty ME store cannot earn the milestone. An active powered
  network containing items can earn it after an actual terminal craft.
  The existing ME quest's native advancement task must reject the unearned
  criterion and accept the earned one through the mod's task submission API.
- A server data reload must preserve the recipe count. The completed ME
  advancement, production journal, personal claims and nonempty quest progress
  are checked again after native reload and independent disk reads. The server must then save
  all dimensions and exit normally, with no KubeJS or fatal server errors.

The fixture supplies creative power, direct recipe ingredients/block setup and
completion flags for existing quest prerequisites to isolate the conditions
under test. Its successful producer actors use survival mode; costly components
must pass through actual native recipe output paths. It never writes a positive
production flag directly. The ME task and advancement start unearned and must
be completed by the real terminal craft. The fixture does not establish how long a survival player
needs to build that network. The milestone is a **successful craft in a powered ME
crafting terminal with stored items**, not a scheduled pattern-based
autocrafting job. The separate workshop kit quest describes item preparation.

The production adapter separately observes actual Molecular Assembler recipe
completion. It does not treat an arbitrary AE2 processing CPU job as proof:
returning an already existing item can satisfy that job. Other machine families
are outside the explicitly supported catalogue routes. Final run results and
their exact payload/source hashes are recorded in [reports](../reports/README.md).

## Exploration performance remains a separate experiment

The Cannibal 0.4.1 compatibility hotfix redirects its discovery lookup to
`getChunkNow`; retain that targeted behavior. Passing native interaction or
startup tests does not measure a travel speedup.

The pinned Millénaire 9.0.2 runtime already queues village candidates and limits
expensive attempts per tick. Its generated configuration defaults to
`generateHamlets = false`; the optional hamlet search must not be presented as
a demonstrated default-world bottleneck. `forceLoadVillages = false` is an
upstream performance option, but intentionally pauses villages when players
leave. Changing that policy is a simulation choice, not a transparent fix for
generation stalls. No village candidates, structures or chunk requests are
silently discarded by this change set.

An earlier chunk guard was considered against the actual
`org.millenaire.world.VillageSpawnQueue.trySpawnVillage` bytecode in that pinned
JAR. Its arguments contain a position and spawn-distance value, but no selected
village type or radius. It filters types using biome samples first, makes the
weighted type selection with `ThreadLocalRandom.nextInt`, and only then applies
`hasChunksAt` to the selected type's radius. Moving that exact guard to the
method's entry therefore requires changing type selection or substituting a
different radius. A fixed radius can defer a small valid village because of
unloaded chunks outside its own footprint. Retrying also has distinct owners:
the method requeues normal villages itself, the separate lone-building queue
adds its own backoff and preserves its expiry timestamp, and the direct
lone-building-only branch in the main queue ignores the return value. A generic
early `DEFERRED` return is not a safe replacement for those paths.

In the pinned Minecraft runtime, `LevelReader.getNoiseBiome` asks for the BIOMES
chunk status with creation disabled and falls back to the biome source if no
chunk is available. A biome call alone does not prove new chunk generation;
an actual recording must distinguish waiting on an existing chunk future,
uncached biome calculation, terrain placement and village force-load work.

For a reproducible travel investigation, create two independent worlds from
the same seed and exact payload, use identical Java/heap, hardware,
view/simulation distance and route, and keep the first traversal separate from
a repeat through already generated terrain. Start a local JVM recording with
the vanilla `/jfr start` command before the route and stop it with `/jfr stop`.
Record starting coordinates, route waypoints, movement speed, elapsed time,
loaded village count and client/server settings alongside each recording.
Compare tick-duration distributions and server-thread chunk waits; a wall-time
wait sample is not CPU work and does not by itself establish a speedup. Any
candidate generation patch must also preserve village types, building counts,
queued retries and save/reload behavior before a performance claim is made.

Graphical questbook rendering, actual input keys, client FPS, survival pacing
and multiplayer economy still require their own client/play sessions. This
dedicated-server fixture does not label them as tested.
