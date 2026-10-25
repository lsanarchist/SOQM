# Dungeon encounter runtime validation

This test-only mod runs in the existing full-pack harness. The harness checks
the dependency hashes, authored JAR pins, native NeoForge server and reviewed
production payload before creating a fresh localhost server and world. It does
not read a player's existing save or add the fixture JAR to the release.

Run from the repository root after building and pinning the production modules:

```bash
python tests/runtime/encounter-validation/run_probe.py \
  --runtime .work/native-runtime \
  --java .cache/tooling/jdk21/bin/java \
  --accept-eula \
  --timeout 1200
```

`--accept-eula` acknowledges the Minecraft EULA for this disposable server.
`--runtime` may point to another verified NeoForge 21.1.262 installation.
`--allow-unlocked-payload` is available for a development run; a release evidence
run must pass without it. The final report is
`reports/dungeon-encounter-probe.json`, with compiler, console and native fixture
details in the reported `.work/dungeon-encounter-probe/` directory.

## What is exercised

- Real DD `spawnEnemyHere` calls with Divine entity IDs, for both basic and
  advanced rooms. The tests check the RETURN mixin, native health-scaling order,
  pending state before a player arrives, original UUID/type/AI, room restriction,
  persistence and protection from earlier replacement handlers.
- The exact upstream Elite registry instance in `EliteMobManager`, an actual
  native damage event that triggers Vampiric healing, and manager ticks that
  execute Regenerating. The encounter must remain absent from the separate
  bonus-loot/boss registration map.
- Native undead immunities: an entirely incompatible ability pool declines
  before conversion, while Kitra gets a functioning Vampiric ability from a
  mixed pool without removing its original regeneration immunity.
- Apotheosis's loaded tier registry, removal of a previous tier's attribute and
  damage-reduction entries, preservation of unrelated modifiers, and retention
  of a real Apotheosis-generated sword and DD key, including key drop chance.
- Separate 12% ordinary and 85% boss policies; native ground, flying and aquatic
  Divine boss classes; retained AI selectors, native boss bar and loot table;
  selection in every registered Divine dimension. DD continues to exclude
  bosses from its ordinary encounter path. NPCs, pets, existing Elite/Apotheosis
  encounters and already-declined rolls remain excluded. Ordinary neutral
  whales are excluded, and ordinary hostiles retain their natural despawn rule
  while authored persistent bosses and dungeon mobs stay persistent.
- The registered player-tick handler, bounded activation passes, rejection of
  distant and fake players, and a check that activation does not load chunks.
- Native entity visibility transitions through the section manager. Hiding then
  showing a live entity emits no second join event, so this specifically catches
  lost ability state during that lifecycle.
- Complete native entity NBT serialization and two loads through the actual
  `loadedFromDisk` entity-manager path. The tests check cooldowns, one-shot
  state, type/UUID, stable stats/name/tier, cleanup of old instances, and that
  Shielded's spawn effect is not repeated on load. The harness then issues a
  real resource reload and rechecks the live encounter.
- Registered DD loot tables and the transformed final sparse-loot compaction
  path, including activated keys with their native components. The separate
  `DungeonLootProbe` helper owns these assertions.

Probability boundaries are made deterministic by seeding the real entity's
random source or using the public configuration in the fixture. Configuration
values are restored after each case. The fixtures use native `ServerPlayer`
objects with an in-memory transport that discards outbound packets; they do not
claim a graphical client handshake or client rendering coverage.

NBT load cycles exercise the same native load and join path used for saved
entities, without restarting a second JVM. This suite is an integration gate,
not a full boss-fight playthrough, client-rendering check or TPS benchmark.
