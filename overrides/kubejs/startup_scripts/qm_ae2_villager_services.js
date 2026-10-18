// AE2 Better Villagers 1.15.1.b has several component -> two filled-cell
// trades; disassembling those cells duplicates storage components. Its
// processor-only high-capacity sales also skip the pack's storage recipes.
// Preserve all professions and the unrelated trades. The specialist offers
// paid assembly of exactly ONE cell from its matching, already-made component.
// Native source: AeBetterVillagersModTrades, CF file 5956265.
var qmBVRegistry = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries');
var qmBVListing = Java.loadClass('net.neoforged.neoforge.common.BasicItemListing');
var qmBVRandom = Java.loadClass('net.minecraft.util.RandomSource');
var qmBVPriority = Java.loadClass('net.neoforged.bus.api.EventPriority');
var qmBVEvent = Java.loadClass('net.neoforged.neoforge.event.village.VillagerTradesEvent');
var qmBVIntMaps = Java.loadClass('it.unimi.dsi.fastutil.ints.Int2ObjectMaps');

global.qmV030VillagerServicePlan = [
  { size: '1k', level: 2, item_fee: 8, fluid_fee: 12 },
  { size: '4k', level: 2, item_fee: 12, fluid_fee: 16 },
  { size: '16k', level: 3, item_fee: 16, fluid_fee: 20 },
  { size: '64k', level: 4, item_fee: 24, fluid_fee: 28 },
  { size: '256k', level: 5, item_fee: 32, fluid_fee: 36 }
];
global.qmV030VillagerTradeAudit = [];

NativeEvents.onEvent(qmBVPriority.LOWEST, qmBVEvent, event => {
  var profession = String(qmBVRegistry.VILLAGER_PROFESSION.getKey(event.getType()));
  if (profession !== 'ae_better_villagers:ae_specialist') return;
  var trades = event.getTrades();
  var removed = [];
  var added = [];
  var random = qmBVRandom.create(3003);
  // Rhino wraps Int2ObjectMap as a JavaScript map. Looking up a method name
  // on it first calls containsKey(String), which cannot unbox an integer.
  // Iterate through the public fastutil helper and retain lists in a JS object.
  var listsByLevel = {};
  var levels = qmBVIntMaps.fastIterable(trades).iterator();
  while (levels.hasNext()) {
    var levelEntry = levels.next();
    var level = Number(levelEntry.getIntKey());
    var entries = levelEntry.getValue();
    listsByLevel[String(level)] = entries;
    var iterator = entries.iterator();
    while (iterator.hasNext()) {
      var listing = iterator.next();
      // Only this mod's simple listings: do not invoke unrelated dynamic offers.
      if (!(listing instanceof qmBVListing)) continue;
      var offer = listing.getOffer(null, random);
      if (offer === null) continue;
      var output = String(qmBVRegistry.ITEM.getKey(offer.getResult().getItem()));
      if (/^ae2:(cell_component_(1k|4k|16k|64k|256k)|(item|fluid)_storage_cell_(1k|4k|16k|64k|256k)|(1k|4k|16k|64k|256k)_crafting_storage)$/.test(output)) {
        removed.push({ level: level, output: output, count: offer.getResult().getCount() });
        iterator.remove();
      }
    }
  }
  global.qmV030VillagerServicePlan.forEach(service => {
    ['item', 'fluid'].forEach(kind => {
      var output = 'ae2:' + kind + '_storage_cell_' + service.size;
      var component = 'ae2:cell_component_' + service.size;
      var fee = kind === 'item' ? service.item_fee : service.fluid_fee;
      listsByLevel[String(service.level)].add(new qmBVListing(
        Item.of('minecraft:emerald', fee),
        Item.of(component),
        Item.of(output),
        8, 10, 0.05
      ));
      added.push({ level: service.level, output: output, count: 1, component: component, component_count: 1, emeralds: fee });
    });
  });
  global.qmV030VillagerTradeAudit.push({ profession: profession, removed: removed, added: added });
});
