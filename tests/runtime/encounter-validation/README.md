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
- `DungeonRewardProbe` rolls all 15 combat-bearing outer chest tables at all five
  native WorldTiers, using 48 seeds each. It validates actual Apotheosis gem and
  affix components against the opener's eligible tier weights, keeps generated
  prizes through the real final-fill mixin, strips only the temporary prize
  marker, and preserves unrelated custom data. It checks guaranteed hard-chest
  rewards, optional supply rolls, DD-only retention of existing native valuables,
  and unchanged settlement retention behavior.
- Earned theme-4/5 keys are observed from the loaded outer tables through the
  real NeoForge global modifier and helper-table context. Advanced hard treasure
  and its legacy alias must both produce Citadel keys; Crazy and other ordinary
  routes cannot. Citadel repeat/next-stage and Abyss repeat keys are exercised.
  Two actual Lootr personal inventories also retain their contents through
  reopening and a real resource reload.
- `DungeonExpeditionProbe` places all 18 original templates through DD's native
  room placement, each in all four rotations. It checks the actual spawned
  entity AABBs, finite mob counts, Champion ability and theme strength, native
  door connectivity using server collision shapes, and climbable exits from
  every pit-floor position. Generated rooms must contain no mob spawner.
- Every locked end-room variant and rotation must have exactly one native
  matching keyholder. A real death produces exactly one matching key. Lootr's
  own conversion preserves the native lock; its actual access and open handlers
  reject empty/wrong hands and accept the dropped key for two separate personal
  inventories. Reopening does not generate new loot.
- The original blank-theme ritual and DD offhand reward path still produce only
  themes 1–3. Real earned keys then pass native keyhole insertion, generation
  with configured 32/42-room targets, completed-room guards, crowned-frame
  validation, and actual gold portal entry and return. Revisiting built rooms
  cannot create more mobs or regenerate treasure.
- Native DD room data is saved and loaded both while a build is pending and
  after completion. Separate positive and negative save/load/save controls check
  that only matching Citadel/Abyss records regain their saved theme; key counters,
  coordinates, structure names, rotations, room types and unrelated records keep
  native behavior. DD unknown-marker, failed-room and missing-exit messages fail
  the harness.

Probability boundaries are made deterministic by seeding the real entity's
random source or using the public configuration in the fixture. Configuration
values are restored after each case. The fixtures use native `ServerPlayer`
objects with an in-memory transport that discards outbound packets; they do not
claim a graphical client handshake or client rendering coverage.

NBT load cycles exercise the same native load and join path used for saved
entities, without restarting a second JVM. This suite is an integration gate,
not a full boss-fight playthrough, client-rendering check or TPS benchmark.

## SOQM 0.4.23 coverage

`WdaEncounterProbe.java` exercises registered WDA structure bounds, saved structure-start NBT without loading its origin chunk, named/equipped enemies, non-Pathfinder stats-only enemies, exclusions, bounded activation and saved encounter state. `AccessoryLootProbe.java` exercises duplicate replacement, native component preservation, the curated pool, seeded bonus frequencies, actual WDA chest tables and saved personal Lootr inventories. Both run through the existing locked full-pack harness and its resource-reload checkpoint. These are native dedicated-server fixtures; they do not establish graphical-client behavior, every naturally generated WDA structure, a second JVM restart or a complete survival playthrough.
