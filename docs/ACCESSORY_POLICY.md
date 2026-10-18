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
- Artifacts use Curios, while Aether and unrelated items keep their Accessories
  eligibility. Scarves, snorkel and night vision goggles route to face. Backpacks
  move from back to backpack. Whoopee Cushion keeps universal Curios eligibility.
- Eternal Steak and Everlasting Beef remain registered. They are excluded from
  the selected artifact/mimic pools and random loot in both table editing and
  final loot processing. Existing player inventories are untouched.

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

## Verification

Run `node tests/accessory-policy.mjs [path-to-overrides]`. It exercises handler
effects with resolved nested tags, unrelated items, optional integrations,
reloads, both loot stages, and all nine Curios slot definitions. It does not start
Minecraft or validate Rhino interoperability, resource-pack precedence, or live
mod interactions. Fresh dedicated-server and client checks are required before
calling this payload runtime verified.

## Publication constraints

Keep the old nested source archive private: it contains the historical derived
adapters. Do not copy its `authoring`/`build_notes` contents wholesale into public
Git. Include only reviewed flat source files and new provenance/validation data.
Craftoria currently declares [All Rights Reserved](https://www.curseforge.com/minecraft/modpacks/craftoria/license).
