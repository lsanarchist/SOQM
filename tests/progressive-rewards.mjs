import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import vm from 'node:vm';
import {fileURLToPath} from 'node:url';

// Executes the real server script with deterministic event/registry/RNG inputs.
// Persistent FTB claims and actual inventory delivery are covered by the native
// ProgressiveRewardsProbe; this fixture tests selection and the read-only UI.
const base = process.argv[2] || path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../overrides');
const source = fs.readFileSync(path.join(base, 'kubejs/server_scripts/soqm_progressive_rewards.js'), 'utf8');
const rewardId = 'F123456789ABCDEF';
const questId = 'A123456789ABCDEF';
const weights = [
  [70, 25, 4, 1], [65, 28, 6, 1], [58, 31, 9, 2], [50, 35, 12, 3],
  [45, 36, 15, 4], [38, 39, 18, 5], [32, 41, 21, 6],
];
const rarities = ['common', 'uncommon', 'rare', 'epic'];
const name = value => ({uk_ua: value, en_us: value});
const entry = (item, count, rarity, extra = {}) => ({item, count, rarity, weight: 1, name: name(item), ...extra});
const makePolicy = () => ({
  schema: 2,
  levels: Array.from({length: 6}, (_, n) => ({level: n + 1, item: `kubejs:milestone_${n + 1}`, name: name(`Milestone ${n + 1}`)})),
  production_items: ['kubejs:sealed_chassis', 'ae2:engineering_processor', 'sophisticatedstorage:diamond_barrel', 'kubejs:assembly',
    ...Array.from({length: 6}, (_, n) => `kubejs:milestone_${n + 1}`)],
  draws_by_level: [2, 2, 3, 3, 3, 4, 4],
  rarity_weights_by_level: weights.map(row => Object.fromEntries(rarities.map((rarity, index) => [rarity, row[index]]))),
  pools: [
    {key: 'survival', title: name('Survival'), fallback: 'survival', entries: [
      entry('minecraft:bread', 2, 'common'),
      entry('minecraft:torch', 4, 'common', {weight: 3}),
      entry('minecraft:iron_ingot', 2, 'uncommon'),
      entry('kubejs:sealed_chassis', 1, 'rare', {requires_craft: ['kubejs:sealed_chassis'], min_level: 1}),
      entry('ae2:engineering_processor', 1, 'epic', {requires_craft: ['ae2:engineering_processor'], min_level: 3}),
    ]},
    {key: 'storage', title: name('Storage'), fallback: 'survival', entries: [
      entry('sophisticatedstorage:diamond_barrel', 1, 'rare', {requires_craft: ['sophisticatedstorage:diamond_barrel'], min_level: 2}),
      entry('kubejs:assembly', 1, 'epic', {requires_craft: ['kubejs:assembly', 'kubejs:sealed_chassis'], min_level: 3}),
    ]},
    {key: 'empty', title: name('Empty'), fallback: 'cycle', entries: []},
    {key: 'cycle', title: name('Cycle'), fallback: 'empty', entries: []},
  ],
  reward_mappings: {
    [rewardId]: {pool: 'survival', quest_id: questId},
    F123456789ABCDE0: {pool: 'storage', quest_id: questId},
  },
});
const registry = new Set([
  'minecraft:bread', 'minecraft:torch', 'minecraft:iron_ingot',
  'kubejs:sealed_chassis', 'ae2:engineering_processor', 'sophisticatedstorage:diamond_barrel', 'kubejs:assembly',
  ...Array.from({length: 6}, (_, n) => `kubejs:milestone_${n + 1}`),
]);

class Player {
  constructor({produced = [], teamProduced = [], random = [0], language = 'en_us'} = {}) {
    this.produced = new Set(produced);
    this.teamProduced = new Set(teamProduced);
    this.randomValues = random;
    this.randomIndex = 0;
    this.language = language;
    this.messages = [];
    this.inventory = {};
    this.deliveries = [];
    this.spectator = false;
  }
  isSpectator() { return this.spectator; }
  getLanguage() { return this.language; }
  sendSystemMessage(message) { this.messages.push(message); }
  getRandom() { return {nextDouble: () => this.randomValues[this.randomIndex++ % this.randomValues.length]}; }
}
class FakePlayer extends Player {}
class Command {
  constructor(name) { this.name = name; this.children = new Map(); }
  then(child) { this.children.set(child.name, child); return this; }
  executes(callback) { this.callback = callback; return this; }
}

function load(policy = makePolicy(), options = {}) {
  let callback;
  let command;
  let proofReads = 0;
  const warnings = [];
  const registered = options.registry || registry;
  const context = vm.createContext({
    console: {warn: message => warnings.push(message)},
    JsonIO: {readString: filename => {
      assert.equal(filename, 'kubejs/config/soqm_rewards.json');
      if (options.missing) throw new Error('missing policy');
      return typeof policy === 'string' ? policy : JSON.stringify(policy);
    }},
    Item: {exists: id => registered.has(id), of: id => {
      assert(registered.has(id), `Native stack must be registered: ${id}`); return {id};
    }},
    Java: {loadClass: className => {
      if (className === 'net.minecraft.network.chat.Component') return {literal: value => value};
      if (className === 'net.minecraft.server.level.ServerPlayer') return Player;
      if (className === 'net.neoforged.neoforge.common.util.FakePlayer') return FakePlayer;
      if (className === 'qm.rewards.ProductionEvidence') {
        if (options.noAdapter) throw new Error('absent adapter');
        return {getProducedItems: player => {
          proofReads++;
          if (options.proofFailure) throw new Error('failed proof read');
          const values = [...new Set([...player.produced, ...player.teamProduced])];
          let index = 0;
          return {iterator: () => ({hasNext: () => index < values.length, next: () => values[index++]})};
        }};
      }
      if (className === 'dev.ftb.mods.ftbquests.quest.reward.ItemReward') return class {
        constructor(id, quest, item, count) {
          assert.equal(id, 0, 'Transient delivery must never round-trip the outer long through JS Number');
          this.quest = quest; this.item = item; this.count = count;
        }
        claim(player, notify) {
          if (options.failDelivery && player.deliveries.length === 1) throw new Error('synthetic native delivery failure');
          player.inventory[this.item.id] = (player.inventory[this.item.id] || 0) + this.count;
          player.deliveries.push({item: this.item.id, count: this.count, notify});
        }
      };
      throw new Error(`Unexpected Java dependency ${className}`);
    }},
    FTBQuestsEvents: {customReward: handler => { assert.equal(callback, undefined); callback = handler; }},
    ServerEvents: {commandRegistry: handler => handler({
      commands: {literal: value => new Command(value)},
      register: value => { command = value; },
    })},
  });
  vm.runInContext(source, context, {filename: 'soqm_progressive_rewards.js', timeout: 2000});
  const preview = (player, key = 'survival') => context.SOQMProgressiveRewards.preview(player, key);
  const claim = (player, {id = rewardId, quest = questId, marked = true, notify = true} = {}) => callback({
    getReward: () => ({getCodeString: () => id, hasTag: tag => marked && tag === 'soqm:progressive_reward',
      getQuest: () => ({getCodeString: () => quest})}),
    getPlayer: () => player,
    getNotify: () => notify,
  });
  const runCommand = (player, value) => {
    const parts = value.replace(/^\//, '').split(' ');
    let node = command;
    assert.equal(parts.shift(), node.name);
    for (const part of parts) { node = node.children.get(part); assert(node, `Unknown command part ${part}`); }
    assert(node.callback, `Incomplete command ${value}`);
    return node.callback({getSource: () => ({getPlayerOrException: () => player})});
  };
  return {preview, claim, runCommand, command, warnings, proofReads: () => proofReads};
}

const adapter = load();
const fresh = new Player();
let view = adapter.preview(fresh);
assert.equal(view.state.level, 0);
assert.equal(view.state.draws, 2);
assert.equal(view.selected.available.length, 3);
assert.equal(view.locked.length, 2);
const odds = Object.fromEntries(Array.from(view.selected.available, row => [row.entry.item, row.probability]));
assert(Math.abs(odds['minecraft:bread'] - 70 / 95 / 4) < 1e-12, 'Available rarity weights normalize before entry weights');
assert(Math.abs(odds['minecraft:torch'] - 70 / 95 * 3 / 4) < 1e-12);
assert(Math.abs(odds['minecraft:iron_ingot'] - 25 / 95) < 1e-12);
assert(Math.abs(Object.values(odds).reduce((a, b) => a + b, 0) - 1) < 1e-12);
fresh.inventory['ae2:engineering_processor'] = 64;
assert.equal(adapter.preview(fresh).selected.available.length, 3, 'Inventory possession and reward delivery are not production evidence');

const skilled = new Player({produced: ['kubejs:milestone_6']});
view = adapter.preview(skilled);
assert.equal(view.state.level, 6, 'Level is highest actually produced milestone, not number of items held');
assert.equal(view.state.draws, 4);
assert.equal(view.locked.length, 2, 'A high level alone cannot unlock an expensive prize');
const teamPlayer = new Player({teamProduced: ['kubejs:milestone_3', 'ae2:engineering_processor']});
view = adapter.preview(teamPlayer);
assert.equal(view.state.level, 3);
assert.equal(view.state.draws, 3);
assert(Array.from(view.selected.available).some(row => row.entry.item === 'ae2:engineering_processor'), 'Current team production unlocks the exact output');
assert(Array.from(view.locked).some(row => row.entry.item === 'kubejs:sealed_chassis'), 'Other products stay locked');
teamPlayer.teamProduced.clear();
assert.equal(adapter.preview(teamPlayer).state.level, 0, 'A changed team is read when previewing/claiming; no stale team cache');

const assembly = new Player({produced: ['kubejs:milestone_3', 'kubejs:assembly']});
assert(adapter.preview(assembly, 'storage').fallback, 'Every required exact product must be manufactured');
assembly.produced.add('kubejs:sealed_chassis');
view = adapter.preview(assembly, 'storage');
assert.equal(view.fallback, false);
assert.equal(view.selected.available[0].entry.item, 'kubejs:assembly');
assert.equal(view.selected.available[0].probability, 1, 'Only eligible rarity is normalized to certainty');
const ownBarrel = new Player({produced: ['sophisticatedstorage:diamond_barrel']});
assert(adapter.preview(ownBarrel, 'storage').fallback, 'The exact output does not bypass minimum production level');
ownBarrel.produced.add('kubejs:milestone_2');
assert.equal(adapter.preview(ownBarrel, 'storage').selected.available[0].entry.item, 'sophisticatedstorage:diamond_barrel');
assert.equal(adapter.preview(fresh, 'empty').selected.pool.title.en_us, 'Basic supplies', 'Cycles cannot cause an empty prize or an infinite fallback loop');

const first = new Player({random: [0, 0]});
const second = new Player({random: [0.99, 0]});
adapter.claim(first);
adapter.claim(second);
assert.deepEqual(first.inventory, {'minecraft:bread': 4}, 'Two replacement draws combine before native delivery');
assert.equal(first.deliveries.length, 1);
assert.deepEqual(second.inventory, {'minecraft:iron_ingot': 4}, 'The second player uses their own RNG, not a shared quest roll');
assert(first.deliveries[0].notify);
const advanced = new Player({produced: ['kubejs:milestone_6', 'ae2:engineering_processor'], random: [0.9999, 0]});
adapter.claim(advanced, {notify: false});
assert.deepEqual(advanced.inventory, {'ae2:engineering_processor': 4});
assert.equal(advanced.deliveries[0].notify, false);
assert.equal(advanced.randomIndex, 8, 'Four draws each make a rarity roll and an entry roll');
const mid = new Player({produced: ['kubejs:milestone_2'], random: [0, 0]});
adapter.claim(mid);
assert.equal(mid.randomIndex, 6, 'Intermediate production grants three draws');

const outsider = new Player();
adapter.claim(outsider, {id: '0000000000000123', marked: false});
assert.equal(outsider.deliveries.length, 0, 'Unrelated custom rewards are not intercepted');
const fake = new FakePlayer();
adapter.claim(fake);
assert.equal(fake.deliveries.length, 0);
const spectator = new Player(); spectator.spectator = true;
adapter.claim(spectator);
assert.equal(spectator.deliveries.length, 0);
const mismatch = new Player({produced: ['kubejs:milestone_6', 'ae2:engineering_processor'], random: [0.9999, 0]});
adapter.claim(mismatch, {quest: '0000000000000002'});
assert(mismatch.deliveries.every(row => row.item.startsWith('minecraft:')), 'A stale mapping cannot route an unrelated quest to premium prizes');

for (const [description, policy, options] of [
  ['missing policy', makePolicy(), {missing: true}],
  ['malformed JSON', '{', {}],
  ['invalid schema', {schema: 99}, {}],
]) {
  const fixture = load(policy, options), player = new Player({random: [0, 0]});
  fixture.claim(player);
  assert(player.deliveries.length > 0, description);
  assert(player.deliveries.every(row => row.item.startsWith('minecraft:') && row.count > 0), description);
}
for (const options of [{noAdapter: true}, {proofFailure: true}]) {
  const fixture = load(makePolicy(), options);
  const player = new Player({produced: ['kubejs:milestone_6', 'ae2:engineering_processor'], random: [0.99, 0]});
  fixture.claim(player);
  assert.equal(fixture.preview(player).state.level, 0);
  assert(player.deliveries.every(row => row.item.startsWith('minecraft:')), 'Evidence failure closes the gate and retains basic rewards');
}
const malformed = makePolicy();
malformed.pools[0].entries.push(entry('ae2:engineering_processor', 1, 'epic', {requires_craft: 'ae2:engineering_processor'}));
malformed.pools[0].entries.push(entry('missing:bad_registry_entry', 1, 'common'));
malformed.pools[0].entries.push(entry('minecraft:bread', 0, 'common'));
malformed.pools[0].entries.push(entry('minecraft:bread', 1, 'common', {weight: -1}));
const invalidEntries = load(malformed);
assert.equal(invalidEntries.preview(new Player()).selected.available.length, 3, 'Invalid conditions, registry IDs, counts and weights do not become prizes');
const damaged = makePolicy();
damaged.production_items.push('ecologics:coconut_planks');
const damagedRegistry = new Set([...registry, 'ecologics:coconut_planks', 'sophisticatedstorage:gold_barrel',
  'mekanism:hdpe_sheet', 'kubejs:mayhem_cube_t0', 'industrialforegoing:plastic']);
damaged.pools[0].entries.push(
  entry('sophisticatedstorage:gold_barrel', 1, 'common'), // Gate and allowlist entry both absent.
  entry('kubejs:sealed_chassis', 1, 'common'),
  entry('ae2:engineering_processor', 1, 'common'),
  entry('ecologics:coconut_planks', 4, 'common'), // Cheap crafted supply still needs its own output proof.
  entry('mekanism:hdpe_sheet', 1, 'common'),
  entry('industrialforegoing:plastic', 1, 'common'),
  entry('kubejs:mayhem_cube_t0', 1, 'common'),
  entry('kubejs:sealed_chassis', 2, 'rare', {requires_craft: ['kubejs:sealed_chassis']}),
  entry('ecologics:coconut_planks', 4, 'common', {requires_craft: ['ecologics:coconut_planks']}),
);
const protectedEntries = load(damaged, {registry: damagedRegistry});
const craftedSupplyPlayer = new Player({produced: ['kubejs:milestone_6', ...damaged.production_items]});
view = protectedEntries.preview(craftedSupplyPlayer);
assert.equal(view.selected.available.length, 6, 'Omitted protected gates and banned products are discarded; valid original entries and crafted cheap supplies survive');
assert.equal(Array.from(view.selected.available).find(row => row.entry.item === 'ecologics:coconut_planks').entry.count, 4,
  'Allowed cheap crafted supplies retain the reviewed batch size');
assert.equal(protectedEntries.preview(new Player()).selected.available.length, 3, 'Missing own-craft gates never become starter prizes');
const badRandom = new Player({random: [NaN]});
adapter.claim(badRandom);
assert.deepEqual(badRandom.inventory, {'minecraft:bread': 16}, 'Selection failure falls back before any inventory delivery');
const deliveryFailure = load(makePolicy(), {failDelivery: true});
const partial = new Player({random: [0, 0, 0.99, 0]});
assert.throws(() => deliveryFailure.claim(partial), /delivery failure/);
assert.deepEqual(partial.inventory, {'minecraft:bread': 2}, 'A partial native delivery never triggers a second whole-reward fallback');

const visitor = new Player({language: 'uk_ua', produced: ['kubejs:milestone_3']});
const before = JSON.stringify({inventory: visitor.inventory, produced: [...visitor.produced], team: [...visitor.teamProduced]});
assert.equal(adapter.runCommand(visitor, '/soqm rewards'), 1);
assert.equal(adapter.runCommand(visitor, '/soqm rewards status'), 1);
assert.equal(adapter.runCommand(visitor, '/soqm rewards pool survival'), 1);
assert(visitor.messages.some(line => line.includes('Рівень нагород: 3/6')));
assert(visitor.messages.some(line => line.includes('3: Milestone 3')), 'Milestone names use the actual name field');
assert(visitor.messages.some(line => line.includes('особисте отримання')));
assert(visitor.messages.some(line => line.includes('виготовте ae2:engineering_processor')));
assert(visitor.messages.some(line => line.includes('Шанс вказано за одне жеребкування')));
assert.equal(JSON.stringify({inventory: visitor.inventory, produced: [...visitor.produced], team: [...visitor.teamProduced]}), before, 'Status and pool inspection must not grant items or manufacture evidence');
assert.equal(visitor.randomIndex, 0, 'Preview does not advance player RNG');
assert.equal(visitor.deliveries.length, 0);
const rewardsCommand = adapter.command.children.get('rewards');
assert.deepEqual([...rewardsCommand.children.keys()].sort(), ['pool', 'status']);
const reloaded = load();
assert.equal(reloaded.preview(visitor).state.level, adapter.preview(visitor).state.level, 'Reload reads the existing production facts');
assert.equal(reloaded.preview(visitor).selected.available.length, adapter.preview(visitor).selected.available.length);
assert.equal(adapter.warnings.filter(line => line.includes('Missing or mismatched mapping')).length, 1);

// A reserved draw must improve quality without using locked manufactured prizes.
const qualityPolicy = makePolicy();
qualityPolicy.guaranteed_quality_draws = 1;
qualityPolicy.draws_by_level = [3, 3, 4, 4, 4, 5, 5];
const quality = load(qualityPolicy);
const unlucky = new Player({random: [0, 0]});
quality.claim(unlucky);
assert.deepEqual(unlucky.inventory, {'minecraft:iron_ingot': 2, 'minecraft:bread': 4},
  'Even the lowest RNG roll gives one eligible premium prize, then ordinary draws');
assert.equal(unlucky.randomIndex, 6, 'The guarantee reserves one of the draws rather than adding hidden extra rolls');
const qualityView = quality.preview(new Player());
const qualityOdds = Object.fromEntries(Array.from(qualityView.selected.available, row => [row.entry.item, row.quality_probability]));
assert.equal(qualityOdds['minecraft:bread'], 0);
assert.equal(qualityOdds['minecraft:torch'], 0);
assert.equal(qualityOdds['minecraft:iron_ingot'], 1);
assert(!('kubejs:sealed_chassis' in qualityOdds), 'The guaranteed draw cannot unlock unproduced components');
const allCommonPolicy = makePolicy();
allCommonPolicy.guaranteed_quality_draws = 1;
allCommonPolicy.draws_by_level = [3, 3, 4, 4, 4, 5, 5];
allCommonPolicy.pools[0].entries = [entry('minecraft:bread', 8, 'common')];
const allCommon = load(allCommonPolicy), allCommonPlayer = new Player();
allCommon.claim(allCommonPlayer);
assert.deepEqual(allCommonPlayer.inventory, {'minecraft:bread': 24}, 'No premium unlock means a useful normal supply draw, never an empty reward');
assert.equal(allCommon.preview(new Player()).selected.available[0].quality_probability, 1);
const poorRngEndgame = new Player({produced: ['kubejs:milestone_6'], random: [0, 0]});
quality.claim(poorRngEndgame);
assert.deepEqual(poorRngEndgame.inventory, {'minecraft:iron_ingot': 2, 'minecraft:bread': 8});
assert.equal(quality.preview(poorRngEndgame).state.draws, 5);
const qualityReader = new Player();
quality.runCommand(qualityReader, '/soqm rewards pool survival');
assert(qualityReader.messages.some(line => line.includes('; first: 100.00%')), 'The command exposes exact reserved-draw odds');
assert.equal(qualityReader.randomIndex, 0);

// The generator/adapter contract must also be exercised against the actual pack
// JSON. A synthetic-only policy would miss a schema key or mapping mismatch and
// the emergency supplies could make an integration check appear to succeed.
const runtimePath = path.join(base, 'kubejs/config/soqm_rewards.json');
assert(fs.existsSync(runtimePath), 'Generate the committed runtime reward policy before running this gate');
const actual = JSON.parse(fs.readFileSync(runtimePath, 'utf8'));
assert.equal(actual.schema, 2, 'Committed policy contract');
assert.equal(actual.pools.length, 14, 'The complete authored chapter catalogue must load');
assert.equal(actual.levels.length, 6);
assert.equal(Object.keys(actual.reward_mappings).length, 1433, 'Every campaign custom reward is mapped');
const actualRegistry = new Set(registry);
for (const milestone of actual.levels) actualRegistry.add(milestone.item);
for (const pool of actual.pools) for (const prize of pool.entries) actualRegistry.add(prize.item);
const committed = load(actual, {registry: actualRegistry});
assert.deepEqual(committed.warnings, [], 'The reviewed policy cannot silently use emergency fallback');
const statusVisitor = new Player();
committed.runCommand(statusVisitor, '/soqm rewards status');
for (const milestone of actual.levels) assert(statusVisitor.messages.some(line => line.endsWith(`${milestone.level}: ${milestone.name.en_us}`)),
  'Actual localized milestone names must appear in the command UI');
for (const pool of actual.pools) {
  const beginning = committed.preview(new Player(), pool.key);
  assert.equal(beginning.fallback, false, `${pool.key} has a real starter pool`);
  assert.equal(beginning.selected.pool.title.en_us, pool.title.en_us);
  const ungated = pool.entries.filter(prize => (prize.min_level || 0) === 0 && !(prize.requires_craft || []).length);
  assert.equal(beginning.selected.available.length, ungated.length, `${pool.key} loads its actual initial entries`);
  const allProduced = new Set(actual.levels.map(milestone => milestone.item));
  for (const prize of pool.entries) for (const item of prize.requires_craft || []) allProduced.add(item);
  const end = committed.preview(new Player({produced: [...allProduced]}), pool.key);
  assert.equal(end.state.level, 6);
  assert.equal(end.state.draws, 5);
  assert.equal(end.fallback, false);
  assert.equal(end.selected.available.length, pool.entries.length, `${pool.key} loads every unlocked authored entry`);
  assert.equal(end.locked.length, 0);
  assert.equal(actual.guaranteed_quality_draws, 1);
  assert.equal(end.state.qualityDraws, 1);
  assert(Math.abs(Array.from(end.selected.available).reduce((sum, prize) => sum + prize.quality_probability, 0) - 1) < 1e-12);
  assert(Math.abs(Array.from(end.selected.available).reduce((sum, prize) => sum + prize.probability, 0) - 1) < 1e-12);
  const binding = Object.entries(actual.reward_mappings).find(([, row]) => row.pool === pool.key);
  assert(binding, `At least one actual quest uses ${pool.key}`);
  const player = new Player({random: [0, 0]});
  committed.claim(player, {id: binding[0], quest: binding[1].quest_id});
  assert(player.deliveries.length > 0, `${pool.key} actual mapping reaches the custom callback`);
  assert(player.deliveries.every(row => ungated.some(prize => prize.item === row.item)), `${pool.key} gives only initially eligible catalogue prizes`);
  const experienced = new Player({produced: [...allProduced], random: [0, 0]});
  committed.claim(experienced, {id: binding[0], quest: binding[1].quest_id});
  const premiumItems = new Set(pool.entries.filter(prize => prize.rarity !== 'common').map(prize => prize.item));
  assert(experienced.deliveries.some(row => premiumItems.has(row.item)), `${pool.key} always includes an eligible premium prize even with the lowest RNG`);
  assert.equal(experienced.randomIndex, 10, `${pool.key} uses five actual draws at maximum production level`);
}
assert.deepEqual(committed.warnings, [], 'All actual reward bindings and pools load without emergency fallback');
console.log('PASS: actual 14-pool/1433-reward policy contract, production gates, normalized two-stage odds, legacy 2/3/4 and new 3/4/5 draws, reserved quality prize, personal RNG, duplicate combining, exact outer IDs, fallback failures, read-only bilingual commands, reload');
