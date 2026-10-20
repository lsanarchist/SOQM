# SOQM accessory policy implementation

These files replace the two historical `craftoria_accessory_*.js` adapters. The
new implementation uses Curios data files under the `soqm` namespace and newly
written KubeJS handlers. It retains SOQM's existing equipment and food balance.
No Craftoria source or assets are included in this replacement directory.

The previous adapters were inspected during the archive audit. This is therefore
an independently expressed implementation based on mod APIs and functional
requirements, not a claim of an isolated clean-room process or a legal opinion.

## Policy

- Player slots: two rings, two hands, one belt, charm, feet, face, backpack and
  bundle; zero generic curio slots. Other mods' body/cloak slots remain available.
- Artifacts use Curios. Aether uses its separate native Accessories inventory.
  Scarves, snorkel and night vision goggles route to face. Only Sophisticated
  Backpacks move from back to backpack. Infinity Backpack, Relics mantles and
  Immersive Engineering's Powerpack keep their native back slot. Whoopee Cushion
  keeps universal Curios eligibility.
- Eternal Steak and Everlasting Beef remain registered. They are excluded from
  the selected artifact/mimic pools and random loot in both table editing and
  final loot processing. Existing player inventories are untouched.

## Equipping Aether accessories

The pinned Aether 1.5.10 bundles Accessories 1.1.0-beta.48 and registers its own
six slot families. The visible Curios **Hands** slots belong to a different API;
they cannot run Aether glove abilities. The reported glove problem is consistent
with opening Curios instead of the Aether inventory. Repository data does not
disable or remove Aether's native slots, and a graphical reproduction has not
been performed.

Open the **cloud button beside the player portrait in the inventory**, or use
**The Aether → Open/Close Accessory Inventory** in Controls (default **I**).
Drag the item into the corresponding active slot. A filled slot or a cosmetic
slot is not an additional active equipment slot. The pack now adds a localized
tooltip to every Aether accessory, including the player's actual rebound key.

| Native Aether slot | Items covered by the help | Slot count |
| --- | --- | --- |
| Gloves | All 12 glove materials, including dungeon gloves | 1 |
| Ring | Iron, golden, zanite and ice rings | 2 |
| Pendant | Iron, golden, zanite and ice pendants | 1 |
| Cape | All 8 capes/cloaks | 1 |
| Shield | Shield of Repulsion | 1 |
| Accessory | Golden Feather, Regeneration Stone and Iron Bubble | 2 |

This change preserves the native inventory mode and existing equipment. Do not
change Aether's `Use default Accessories' menu` setting with items equipped:
upstream explicitly requires emptying the inventory before changing modes. If
an existing profile's cloud button and key are both unavailable, inspect
`config/aether-client.toml`, `[Gui]`, `"Disables the accessories button"`.
Upstream uses that same setting to disable the key handler as well.

## Infinity Backpack: confirmed slot-routing defect

The old adapter moved **every item whose name contained `backpack`** from
`curios:back` to `curios:backpack`. In Industrial Foregoing 3.6.39, the active
`CuriosPlugin` inventory finder reads the Infinity Backpack across Curios slots,
but its write callback still names `back`. Changing the equipment tag does not
change that callback: an update can target a different slot from the one holding
the backpack. Restoring its native route removes this static read/write mismatch.
The JAR also contains a back-only `InfinityBackpackCurios.canEquip`, but no live
registration reference to that class was found; it is not used as proof that
the current version rejects equipping. Full in-game behavior remains a playtest.

Relocation is now limited to Sophisticated Backpacks, whose pinned Curios
integration discovers eligible slots through `CuriosApi.getItemStackSlots`.
Infinity Backpack remains in **Curios → Back** and gains a tooltip stating that
route. Unknown backpacks keep their original routes. No additional generic
slots or armor slots are granted.

The audit also inspected the pinned tags/slot definitions for Artifacts, Relics,
Reliquified Artifacts, Botania Revived, EvilCraft, Industrial Foregoing,
Immersive Engineering, ProjectE and Sophisticated Backpacks. Their head,
necklace, body, back, bracelet, ring and Klein Star routes remain assigned.
Botania cosmetics and Whoopee Cushion use the universal `curios:curio` item tag;
a zero-sized generic curio slot does not remove their eligibility for other
compatible slots.

## FTB Ultimine and Mekanism energy tools

SOQM requires a tool and a valid harvest tool. FTB Ultimine 2101.1.15 recognizes
tiered items, damageable items or members of `ftbultimine:included_tools`.
Atomic Disassembler and Meka-Tool use energy rather than durability, and the
pinned inclusion tag is empty. An additive item tag now admits these two tools.

The 64-block limit, exhaustion, cooldown, harvest suitability, energy use and
Lootr/Chance Cube exclusions retain their existing behavior. Charge the tool
and enable its usable mode/modules; the inclusion tag does not supply energy
or bypass block protections.

## Primary API references

Checked 2026-10-18:

- [Curios slot schema and merging](https://docs.illusivesoulworks.com/curios/slots/slot-register)
- [Curios player slot assignment](https://docs.illusivesoulworks.com/curios/slots/entity-register)
- [Curios equipment item tags](https://docs.illusivesoulworks.com/curios/items/assign-slots)
- [KubeJS tag editing](https://kubejs.com/wiki/tutorials/tags)
- [KubeJS 2101 tag event public API](https://github.com/KubeJS-Mods/KubeJS/blob/2101/src/main/java/dev/latvian/mods/kubejs/server/tag/TagKubeEvent.java)
- [KubeJS 2101 resolved tag members](https://github.com/KubeJS-Mods/KubeJS/blob/2101/src/main/java/dev/latvian/mods/kubejs/server/tag/TagWrapper.java)
- [LootJS 1.21 table transformers](https://docs.almostreliable.com/lootjs/1.21.x/api/loot-entries-transformer.html)
- [LootJS 1.21 final loot modifiers](https://docs.almostreliable.com/lootjs/1.21.x/api/loot-modifier.html)

Equipment audit checked 2026-10-20 against hash-verified lockfile artifacts:

- [FTB Ultimine tool tags](https://docs.feed-the-beast.com/mod-docs/mods/suite/Ultimine/tags/)
- [FTB Ultimine 2101.1.15 native tool check](https://github.com/FTBTeam/FTB-Ultimine/blob/cce1eb2a9a255ee0dbe98c229e9103c1fb290c6a/common/src/main/java/dev/ftb/mods/ftbultimine/FTBUltimine.java)
- [Aether 1.5.10 native slot registration](https://github.com/The-Aether-Team/The-Aether/blob/04875348b64822eb5c5dcd344cbc9dddfb39e06f/src/main/java/com/aetherteam/aether/inventory/AetherAccessorySlots.java)
- [Aether 1.5.10 button and key handling](https://github.com/The-Aether-Team/The-Aether/blob/04875348b64822eb5c5dcd344cbc9dddfb39e06f/src/main/java/com/aetherteam/aether/client/event/hooks/GuiHooks.java)
- [Industrial Foregoing 3.6.39 pinned artifact](https://www.curseforge.com/minecraft/mc-mods/industrial-foregoing/files/8370717)
- [Sophisticated Backpacks 3.26.12.2208 pinned artifact](https://www.curseforge.com/minecraft/mc-mods/sophisticated-backpacks/files/9154769)
- [KubeJS 1.21 tooltip API](https://kubejs.com/wiki/events/ItemEvents/modifyTooltips)

## Verification

Run `node tests/accessory-policy.mjs [path-to-overrides]`. It exercises handler
effects with resolved nested tags, unrelated items, optional integrations,
reloads, both loot stages, and all nine Curios slot definitions. It does not start
Minecraft or validate Rhino interoperability, resource-pack precedence, or live
mod interactions. Fresh dedicated-server and client checks are required before
calling this payload runtime verified.

The same regression harness now checks that Infinity Backpack and unrelated
backpacks remain in `back`, while both basic and upgraded Sophisticated
Backpacks move to `backpack`.

`tests/runtime/EquipmentCompatibilityProbe.java` is integrated into the existing
isolated runtime probe, before and after data reload. It checks native Ultimine
tool acceptance and rejection; equips and removes all 32 Aether accessories
through their native slot API; and validates 18 Curios routes, including all
six Sophisticated Backpack tiers and the Infinity Backpack restriction. The
compiler harness extracts metadata-declared embedded API JARs into its temporary
workspace, because Aether bundles Accessories.

For this change the fixture was **compiled against the pinned Java 21 native
APIs, but not run in a loaded game/server**. The existing explicit EULA gate for
the isolated runtime server is retained. Client acceptance should include the
cloud button, a rebound Aether key, glove abilities, backpack hotkeys, and
Ultimine mining/energy consumption with a charged Disassembler and Meka-Tool.

## Publication constraints

Keep the old nested source archive private: it contains the historical derived
adapters. Do not copy its `authoring`/`build_notes` contents wholesale into public
Git. Include only reviewed flat source files and new provenance/validation data.
Craftoria currently declares [All Rights Reserved](https://www.curseforge.com/minecraft/modpacks/craftoria/license).
