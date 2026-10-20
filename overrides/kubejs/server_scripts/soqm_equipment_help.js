// SPDX-License-Identifier: MIT
// Aether 1.5.10 uses its own Accessories inventory, separate from Curios.
// Explicit pinned item IDs avoid an unresolved/empty tooltip ingredient
// turning into a global tooltip. No inventory or slot migration is performed.
ItemEvents.modifyTooltips(function (event) {
  if (Platform.isLoaded('aether')) {
    var families = {
      gloves: ['leather_gloves', 'chainmail_gloves', 'iron_gloves', 'golden_gloves',
        'diamond_gloves', 'netherite_gloves', 'zanite_gloves', 'gravitite_gloves',
        'neptune_gloves', 'phoenix_gloves', 'obsidian_gloves', 'valkyrie_gloves'],
      ring: ['iron_ring', 'golden_ring', 'zanite_ring', 'ice_ring'],
      pendant: ['iron_pendant', 'golden_pendant', 'zanite_pendant', 'ice_pendant'],
      cape: ['red_cape', 'blue_cape', 'yellow_cape', 'white_cape', 'swet_cape',
        'invisibility_cloak', 'agility_cape', 'valkyrie_cape'],
      shield: ['shield_of_repulsion'],
      accessory: ['golden_feather', 'regeneration_stone', 'iron_bubble']
    };
    Object.keys(families).forEach(function (family) {
      event.add(families[family].map(function (id) { return 'aether:' + id; }), [
        Text.aqua(Text.translate('soqm.equipment.aether.' + family)),
        Text.gray(Text.translate('soqm.equipment.aether.open',
          Text.keybind('key.aether.open_accessories.desc')))
      ]);
    });
  }
  if (Platform.isLoaded('industrialforegoing')) {
    event.add('industrialforegoing:infinity_backpack', [
      Text.gray(Text.translate('soqm.equipment.infinity_backpack'))
    ]);
  }
});
