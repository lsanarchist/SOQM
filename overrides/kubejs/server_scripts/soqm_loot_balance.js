// SPDX-License-Identifier: MIT
// SOQM survival policy: renewable Artifacts food is excluded from random loot.
// Items stay registered, and existing inventories are not edited.

(function () {
  var restrictedFood = ['artifacts:eternal_steak', 'artifacts:everlasting_beef'];

  ServerEvents.tags('item', function (event) {
    if (!Platform.isLoaded('artifacts')) return;
    var knownFood = restrictedFood.filter(function (id) { return Item.exists(id); });
    if (!knownFood.length) return;
    var sources = ['artifacts:artifacts'];
    if (Platform.isLoaded('reliquified_artifacts')) {
      sources.push('reliquified_artifacts:mimic_loot', 'reliquified_artifacts:mimificable');
    }
    sources.forEach(function (tag) {
      var original = [];
      event.get(tag).getObjectIds().forEach(function (id) { original.push(String(id)); });
      var kept = original.filter(function (id) { return knownFood.indexOf(id) === -1; });
      if (kept.length === original.length) return;
      event.removeAll(tag);
      kept.forEach(function (id) { event.add(tag, id); });
    });
    knownFood.forEach(function (id) { event.add('almostunified:hide', id); });
  });

  if (!Platform.isLoaded('lootjs')) return;
  LootJS.lootTables(function (event) {
    if (!Platform.isLoaded('artifacts')) return;
    restrictedFood.forEach(function (id) {
      if (Item.exists(id)) event.modifyLootTables(/.*/).removeItem(id);
    });
  });
  LootJS.modifiers(function (event) {
    if (!Platform.isLoaded('artifacts')) return;
    var finalLoot = event.addTableModifier(/.*/);
    restrictedFood.forEach(function (id) {
      if (Item.exists(id)) finalLoot.removeLoot(id);
    });
  });
})();
