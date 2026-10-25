# Dungeon encounter integration

The module empowers an existing hostile mob or DivineRPG boss in place. It never calls
EliteSpawner.spawnAt/applyStats, replaces an entity, adds goals, equips items,
changes drop chances, or registers a bonus-loot Elite encounter. Original mob
UUID, class, AI, inventory and DivineRPG drops remain attached to the same mob.
In particular, DD's chest-slot key and offhand theme key are preserved.

## Selection and activation

* Ordinary new DivineRPG hostiles have a saved 12% selection chance in the
  Overworld and all other dimensions. The chance is capped at 35% in the
  config. NPCs, owned pets, existing Elite/Apotheosis encounters, named
  ordinary world mobs and persistent ordinary encounters are excluded.
  Ordinary selection requires the native Monster class, leaving neutral
  whales and other peaceful Divine entities alone. Existing persistence flags
  are retained, so ordinary world encounters keep their normal despawn rules.
* Native DivineRPG bosses use a separate 85% selection chance outside DD,
  including their own dimensions. All 23 pinned boss classes are covered,
  including the flying bosses and aquatic Kitra. Native names and persistence
  are allowed on this explicit boss path. The original entity, goal selectors,
  phase code and drop table remain in use; no boss replacement is spawned.
* DD's `spawnEnemyHere` return hook enrolls eligible ordinary hostiles. The
  default chance is 100%; Artifacts mimics and bosses are excluded. Enrollment
  is deferred and does not touch the mob's stats while DD finishes its room.
* A real nearby player activates at most eight pending mobs per local pass,
  once per second. The radius defaults to 48 blocks. There is no global entity
  scan, forced chunk loading, pending world-reference queue or playerless tier
  guess. Pending state survives a server restart.
* Activation removes only registered Apotheosis monster-tier augments and
  applies one tier from the activating player. This also resolves an earlier
  automatic spawn augment based on a distant player. It then adds fixed,
  named permanent health/damage modifiers and retains current health ratio.
  Default additions are +50% base health/+15% base attack for ordinary mobs,
  +100%/+25% for advanced DD rooms, +150%/+35% for Iron Citadel,
  +200%/+50% for Obsidian Abyss and +25%/+10% for Divine bosses. These are
  attribute additions subject to native attribute limits; attacks with damage
  hardcoded by the original mod are not rewritten.
* The existing MobCompat45 marker is used to prevent a later automatic
  Apotheosis pass from layering another set of tier augments onto the result.

## Actual abilities

The selected `AffixRegistry` ability is registered in the pinned
`EliteMobManager.AFFIX_STATES` map as a real `AffixState`. Elite Mobs' own
`tickAffixes` and `onDamage` hooks run its effects. There is one native ability
per converted mob. The default pool is brutal, infernal_touch, vampiric,
withering, shielded, reflective, regenerating, frostbound and abyssal.

The default pool avoids explosive, lightning, teleport and horde affixes in
DD's puzzle rooms. This does not replace the native Lucifer encounter or its
summoning/progression. It does not award extra Elite loot or gear.
Shielded cannot lower absorption that the original entity already had.
Native effect immunities also constrain selection: Regenerating is omitted
when an entity cannot receive its regeneration or resistance, and Shielded is
omitted when absorption is rejected. This gives undead Kitra a compatible
native ability without changing the boss's immunity rules. If a custom pool
contains no compatible abilities, the encounter is declined before any buffs.

The ability ID, cooldowns and consumed one-shot flags are checkpointed before
entity serialization. On load, only the native runtime ability registration
is restored; health, permanent modifiers and onSpawn effects are not replayed.
On removal or unload, the map entry is removed so it cannot keep an unloaded
mob alive. A live entity becoming temporarily hidden retains its ability,
because NeoForge does not emit a second join event when it becomes visible.
Changing the selection config affects future conversions, not saved rolls.

## Earned expedition keys and authored rooms

Version 0.4.22 supports two original themes: Iron Citadel (4) and Obsidian
Abyss (5). `scripts/dungeon_rooms.py` builds 18 authored NBT room templates;
the native themed designer uses targets of 32 and 42 room cells. Encounters
remain finite native data-marker spawns. The new themes use Champion ability
strength, their own configured attribute additions and the same saved-state
lifecycle as existing encounters.

`ExpeditionKeyFunction` is a registered native loot function. It delegates
fresh address allocation and all dungeon-key components to DD's
`activateKeyLevel1`, then supplies localized names and lore. Reapplying it to
an activated key leaves its address unchanged. Native key data retains the
themed BASIC type, so the native themed room pools and their sizes are used.
Only the portal shape validator and its player diagnostic see an effective
level 2 for these keys; both crowns and valid banners are therefore required.
There is no global key-level override or replacement dungeon generator.

Two exact, pinned call sites limit natural random thematic keys and blank
thematic-key activation to the original three themes. Expedition keys are
instead selected by the scoped treasure tables, progressing from Advanced
to Citadel and then Abyss. The room marker RETURN hook assigns the four
authored loot tables after native lock-code generation, only in the DD
dimension and on the matching authored template prefix. It does not generate
loot early or replace the chest's opener context.

DD210 writes `room.theme` to its saved dungeon data but does not restore it
when rebuilding either `roomMap` or `remainingBuilds`. A pinned constructor
RETURN hook restores only saved theme 4/5 values whose structure belongs to
the matching authored Citadel/Abyss prefix. This lets an interrupted build
resume with its original enemies and treasure tables after a restart. The
hook changes no positions, rotations, room types, dungeon types, queues or
key counters. Native themes, foreign structures, missing theme tags and
mismatched theme/template pairs retain the native load behavior.

Combat item/gem rewards are defined in `metadata/dungeon_loot.json` and use
Apotheosis's own loot functions. This module still does not equip every
enhanced mob or create a second entity-death reward path.

## Build

Run `build.py --runtime <NeoForge-server-directory> --mods <pinned-mod-cache>
--javac <Java-21-javac>`. The builder verifies exact dependency and mixin target
hashes and produces a deterministic versioned JAR under `overrides/mods/`.

Runtime assertions are maintained separately under
`tests/runtime/encounter-validation` and cover actual native ability effects,
DD placement, tier replacement, equipment/identity preservation and reloads.
