import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';
import {fileURLToPath} from 'node:url';

const base = process.argv[2] || path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../overrides');
const scripts = ['soqm_equipment_routes.js', 'soqm_loot_balance.js'];
const itemIds = [
  'artifacts:everlasting_beef', 'artifacts:eternal_steak', 'artifacts:scarf',
  'artifacts:cross_necklace', 'artifacts:snorkel', 'artifacts:night_vision_goggles',
  'artifacts:whoopee_cushion', 'artifacts:running_shoes', 'aether:golden_ring',
  'sophisticatedbackpacks:backpack', 'example:travel_backpack', 'example:cloak',
  'example:ring', 'example:face_mask', 'minecraft:diamond',
];
const seed = {
  'example:back': ['sophisticatedbackpacks:backpack', 'example:cloak'],
  'curios:back': ['#example:back', 'example:travel_backpack'],
  'curios:backpack': ['minecraft:diamond'],
  'curios:face': ['example:face_mask'],
  'curios:ring': ['artifacts:scarf', 'example:ring'],
  'curios:necklace': ['artifacts:snorkel', 'artifacts:cross_necklace'],
  'curios:curio': ['artifacts:night_vision_goggles'],
  'example:artifact_pool': ['artifacts:running_shoes', 'artifacts:everlasting_beef'],
  'accessories:ring': ['#example:artifact_pool', 'aether:golden_ring'],
  'example:recipe_input': ['artifacts:scarf'],
  'artifacts:artifacts': ['#example:artifact_pool', 'artifacts:eternal_steak'],
  'reliquified_artifacts:mimic_loot': ['#example:artifact_pool'],
  'reliquified_artifacts:mimificable': ['artifacts:eternal_steak', 'minecraft:diamond'],
};

function load(mods) {
  const tags = structuredClone(seed);
  const callbacks = [];
  const tableActions = [];
  const finalActions = [];
  const loaded = new Set(mods);
  const known = new Set(itemIds);
  const context = vm.createContext({
    Platform: {isLoaded: name => loaded.has(name)},
    Item: {exists: name => known.has(name)},
    ServerEvents: {tags: (registry, callback) => { assert.equal(registry, 'item'); callbacks.push(callback); }},
    LootJS: {
      lootTables: callback => callback({modifyLootTables: filter => {
        assert.equal(filter.test('minecraft:chests/simple_dungeon'), true);
        return {removeItem: id => tableActions.push(id)};
      }}),
      modifiers: callback => callback({addTableModifier: filter => {
        assert.equal(filter.test('example:after_global_modifier'), true);
        return {removeLoot: id => finalActions.push(id)};
      }}),
    },
  });
  for (const name of scripts) {
    vm.runInContext(fs.readFileSync(path.join(base, 'kubejs/server_scripts', name), 'utf8'), context, {filename: name, timeout: 1000});
  }
  const resolve = (tag, seen = new Set()) => {
    if (seen.has(tag)) return [];
    const next = new Set(seen).add(tag);
    return [...new Set((tags[tag] || []).flatMap(id => id.startsWith('#') ? resolve(id.slice(1), next) : known.has(id) ? [id] : []))];
  };
  const event = {
    tags: {keySet: () => new Set(Object.keys(tags))},
    get: tag => ({getObjectIds: () => resolve(String(tag))}),
    getElementIds: () => known,
    removeAll: tag => { tags[String(tag)] = []; },
    add: (tag, id) => { const name = String(tag); (tags[name] ||= []).push(String(id)); },
  };
  const run = () => callbacks.forEach(callback => callback(event));
  run();
  return {tags, resolve, run, tableActions, finalActions};
}

const full = load(['curios', 'artifacts', 'accessories', 'reliquified_artifacts', 'lootjs']);
const equals = (tag, expected) => assert.deepEqual(full.resolve(tag).sort(), expected.sort(), tag);
equals('curios:back', ['example:cloak']);
equals('curios:backpack', ['minecraft:diamond', 'sophisticatedbackpacks:backpack', 'example:travel_backpack']);
equals('curios:face', ['example:face_mask', 'artifacts:scarf', 'artifacts:snorkel', 'artifacts:night_vision_goggles']);
equals('curios:ring', ['example:ring']);
equals('curios:necklace', ['artifacts:cross_necklace']);
equals('curios:curio', ['artifacts:whoopee_cushion']);
equals('accessories:ring', ['aether:golden_ring']);
equals('example:recipe_input', ['artifacts:scarf']);
assert.deepEqual(full.tags['example:back'], seed['example:back'], 'source nested tag stays intact');
equals('artifacts:artifacts', ['artifacts:running_shoes']);
equals('reliquified_artifacts:mimic_loot', ['artifacts:running_shoes']);
equals('reliquified_artifacts:mimificable', ['minecraft:diamond']);
equals('almostunified:hide', ['artifacts:eternal_steak', 'artifacts:everlasting_beef']);
assert.deepEqual(full.tableActions.sort(), ['artifacts:eternal_steak', 'artifacts:everlasting_beef']);
assert.deepEqual(full.finalActions.sort(), full.tableActions);
const firstResult = Object.fromEntries(Object.keys(full.tags).map(tag => [tag, full.resolve(tag).sort()]));
full.run();
assert.deepEqual(Object.fromEntries(Object.keys(full.tags).map(tag => [tag, full.resolve(tag).sort()])), firstResult, 'reload preserves resolved policy');

const noMods = load([]);
assert.deepEqual(noMods.tags, seed, 'absent integrations cause no changes');
assert.equal(noMods.tableActions.length + noMods.finalActions.length, 0);
const justCurios = load(['curios']);
assert.deepEqual(justCurios.resolve('accessories:ring').sort(), ['aether:golden_ring', 'artifacts:everlasting_beef', 'artifacts:running_shoes']);
assert.deepEqual(justCurios.tags['curios:ring'], seed['curios:ring']);
const justFood = load(['artifacts', 'lootjs']);
assert.deepEqual(justFood.tags['curios:ring'], seed['curios:ring']);
assert.deepEqual(justFood.tags['reliquified_artifacts:mimic_loot'], seed['reliquified_artifacts:mimic_loot']);

const slotRoot = path.join(base, 'kubejs/data/soqm/curios');
const expectedSizes = {backpack: 1, belt: 1, bundle: 1, charm: 1, curio: 0, face: 1, feet: 1, hands: 2, ring: 2};
const replacements = new Set(['belt', 'charm', 'curio', 'feet', 'hands', 'ring']);
for (const [name, size] of Object.entries(expectedSizes)) {
  const definition = JSON.parse(fs.readFileSync(path.join(slotRoot, 'slots', `${name}.json`), 'utf8'));
  assert.equal(definition.size, size, `${name} count`);
  assert.equal(!!definition.replace, replacements.has(name), `${name} merging`);
  assert.equal(definition.operation || 'SET', 'SET');
  if (name !== 'curio') assert.deepEqual(definition.validators, ['curios:tag']);
}
const player = JSON.parse(fs.readFileSync(path.join(slotRoot, 'entities/players.json'), 'utf8'));
assert.deepEqual(player.entities, ['minecraft:player']);
assert.deepEqual(player.slots.sort(), Object.keys(expectedSizes).sort());
assert.equal(player.replace || false, false, 'other mods retain their player slots');
console.log('PASS: equipment routes, nested tags, unrelated items, optional mods, reload, two loot stages, nine slot definitions');
