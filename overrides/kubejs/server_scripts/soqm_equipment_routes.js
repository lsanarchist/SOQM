// SPDX-License-Identifier: MIT
// SOQM equipment policy. Curios slot counts live in kubejs/data/soqm/curios.
// API references and verification limits: docs/ACCESSORY_POLICY.md.

ServerEvents.tags('item', function (event) {
  if (!Platform.isLoaded('curios')) return;

  var before = {};
  event.tags.keySet().forEach(function (key) {
    var name = String(key);
    if (name.indexOf('curios:') !== 0 && name.indexOf('accessories:') !== 0) return;
    var values = [];
    event.get(key).getObjectIds().forEach(function (item) { values.push(String(item)); });
    before[name] = values;
  });

  var backItems = before['curios:back'] || [];
  var backpacks = backItems.filter(function (id) {
    // Sophisticated Backpacks discovers its usable slots from Curios tags.
    // Industrial Foregoing's Infinity Backpack writes inventory updates into
    // the native "back" slot; relocating it breaks the read/write route.
    return id.indexOf('sophisticatedbackpacks:') === 0 && id.endsWith('backpack');
  });

  var faceItems = [];
  if (Platform.isLoaded('artifacts')) {
    event.getElementIds().forEach(function (key) {
      var id = String(key);
      if (id.indexOf('artifacts:') !== 0) return;
      if (id.indexOf('scarf') !== -1 || id === 'artifacts:snorkel' || id === 'artifacts:night_vision_goggles') {
        faceItems.push(id);
      }
    });
  }

  // Work from one resolved snapshot: changing a nested tag cannot change a
  // later decision, and untouched tags keep their original nested definitions.
  Object.keys(before).forEach(function (tag) {
    var retained = before[tag].filter(function (id) {
      if (tag === 'curios:back' && backpacks.indexOf(id) !== -1) return false;
      if (tag.indexOf('curios:') === 0 && tag !== 'curios:face' && faceItems.indexOf(id) !== -1) return false;
      if (tag.indexOf('accessories:') === 0 && Platform.isLoaded('accessories') && Platform.isLoaded('artifacts') && id.indexOf('artifacts:') === 0) return false;
      return true;
    });
    if (retained.length === before[tag].length) return;
    event.removeAll(tag);
    retained.forEach(function (id) { event.add(tag, id); });
  });

  backpacks.forEach(function (id) { event.add('curios:backpack', id); });
  faceItems.forEach(function (id) { event.add('curios:face', id); });
  if (Platform.isLoaded('artifacts') && Item.exists('artifacts:whoopee_cushion')) {
    event.add('curios:curio', 'artifacts:whoopee_cushion');
  }
});
