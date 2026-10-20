// FTB owns the persistent (player UUID, outer reward ID) claim. This script only
// selects and delivers its contents after that native claim has been accepted.
var SOQMProgressiveRewards = (function () {
  var ItemReward = Java.loadClass('dev.ftb.mods.ftbquests.quest.reward.ItemReward');
  var Component = Java.loadClass('net.minecraft.network.chat.Component');
  var ServerPlayer = Java.loadClass('net.minecraft.server.level.ServerPlayer');
  var FakePlayer = Java.loadClass('net.neoforged.neoforge.common.util.FakePlayer');
  var ProductionEvidence = null;
  var RecipeRefunds = null;
  var warnings = {};
  var rarities = ['common', 'uncommon', 'rare', 'epic'];
  var defaultDraws = [3, 3, 4, 4, 4, 5, 5];
  var defaultRarityWeights = [
    [50, 35, 12, 3], [45, 36, 15, 4], [38, 38, 19, 5], [30, 40, 23, 7],
    [24, 40, 27, 9], [18, 40, 30, 12], [12, 40, 33, 15]
  ];
  var itemPattern = /^[a-z0-9_.-]+:[a-z0-9_./-]+$/;
  var poolPattern = /^[a-z0-9_]+$/;
  var marker = 'soqm:progressive_reward';
  var refundMarker = 'soqm:recipe_refund';
  var rarityNames = {
    uk_ua: {common: 'звичайний', uncommon: 'незвичайний', rare: 'рідкісний', epic: 'епічний'},
    en_us: {common: 'common', uncommon: 'uncommon', rare: 'rare', epic: 'epic'}
  };

  function warn(key, message) {
    if (warnings[key]) return;
    warnings[key] = true;
    console.warn('[SOQM rewards] ' + message);
  }

  function finite(value) { return typeof value === 'number' && isFinite(value); }
  function integer(value, min, max) {
    return finite(value) && Math.floor(value) === value && value >= min && value <= max;
  }
  function validItem(id) {
    return typeof id === 'string' && itemPattern.test(id) && id !== 'minecraft:air' && Item.exists(id);
  }
  function protectedProduct(id, rarity) {
    return /^(kubejs|sophisticatedstorage|sophisticatedbackpacks):/.test(id) ||
      /^(ae2):(logic|calculation|engineering)_processor$/.test(id) ||
      (id.indexOf('minecraft:') !== 0 && (rarity === 'rare' || rarity === 'epic'));
  }
  function code(id) {
    if (typeof id !== 'string' || !/^[0-9a-fA-F]{1,16}$/.test(id)) return '';
    return ('0000000000000000' + id.toUpperCase()).slice(-16);
  }
  function names(value, fallback) {
    return {
      uk_ua: value && typeof value.uk_ua === 'string' ? value.uk_ua : fallback,
      en_us: value && typeof value.en_us === 'string' ? value.en_us : fallback
    };
  }
  function language(player) { return String(player.getLanguage()).indexOf('uk') === 0 ? 'uk_ua' : 'en_us'; }
  function say(player, uk, en) { player.sendSystemMessage(Component.literal(language(player) === 'uk_ua' ? uk : en)); }

  // This last-resort pool is independent of the external policy. These are
  // registered vanilla supplies; no manufactured prize is an emergency fallback.
  function emergencyPool() {
    var supplies = [
      ['minecraft:bread', 8, 'Хліб', 'Bread'],
      ['minecraft:iron_ingot', 6, 'Залізний злиток', 'Iron Ingot'],
      ['minecraft:torch', 16, 'Смолоскип', 'Torch']
    ];
    return {
      key: 'survival', title: names({uk_ua: 'Базові припаси', en_us: 'Basic supplies'}, 'survival'),
      fallback: '', entries: supplies.filter(function (row) { return validItem(row[0]); }).map(function (row) {
        return {item: row[0], count: row[1], weight: 1, rarity: 'common', min_level: 0,
          requires_craft: [], name: {uk_ua: row[2], en_us: row[3]}};
      })
    };
  }

  function compile(raw) {
    if (!raw || raw.schema !== 2 || !Array.isArray(raw.pools)) throw new Error('Expected reward policy schema 2');
    var result = {pools: Object.create(null), mappings: Object.create(null), itemNames: Object.create(null),
      levels: [], draws: [], weights: [], qualityDraws: 0};
    // Older schema-2 policy files can still load without the reserved draw.
    var qualityDraws = raw.guaranteed_quality_draws === undefined ? 0 : raw.guaranteed_quality_draws;
    if (!integer(qualityDraws, 0, 1)) throw new Error('Invalid reserved quality draw count');
    result.qualityDraws = qualityDraws;
    if (!Array.isArray(raw.production_items) || !raw.production_items.every(validItem)) {
      throw new Error('Missing or invalid production item allowlist');
    }
    var productionItems = Object.create(null);
    raw.production_items.forEach(function (id) { productionItems[id] = true; });
    if (!Array.isArray(raw.draws_by_level) || raw.draws_by_level.length !== 7 ||
        !Array.isArray(raw.rarity_weights_by_level) || raw.rarity_weights_by_level.length !== 7) {
      throw new Error('Seven production levels must define draws and rarity weights');
    }
    for (var level = 0; level <= 6; level++) {
      var draws = raw.draws_by_level[level];
      var weights = raw.rarity_weights_by_level[level];
      if (!integer(draws, 2, 5) || !weights) throw new Error('Invalid draw count or rarity weights');
      var total = 0, row = {};
      rarities.forEach(function (rarity) {
        var weight = weights[rarity];
        if (!finite(weight) || weight < 0 || weight > 1000000) throw new Error('Invalid rarity weight');
        row[rarity] = weight; total += weight;
      });
      if (!(total > 0)) throw new Error('All rarity weights are zero');
      result.draws.push(draws); result.weights.push(row);
    }
    if (!Array.isArray(raw.levels) || raw.levels.length !== 6) throw new Error('Six production milestones are required');
    var seenLevels = {};
    raw.levels.forEach(function (milestone) {
      if (!milestone || !integer(milestone.level, 1, 6) || !productionItems[milestone.item] || seenLevels[milestone.level]) {
        throw new Error('Invalid production milestone');
      }
      seenLevels[milestone.level] = true;
      result.levels.push({level: milestone.level, item: milestone.item,
        title: names(milestone.name, milestone.item)});
      result.itemNames[milestone.item] = names(milestone.name, milestone.item);
    });
    result.levels.sort(function (a, b) { return a.level - b.level; });
    raw.pools.forEach(function (pool) {
      if (!pool || typeof pool.key !== 'string' || !poolPattern.test(pool.key) || !Array.isArray(pool.entries)) {
        throw new Error('Invalid reward pool');
      }
      if (result.pools[pool.key]) throw new Error('Duplicate reward pool ' + pool.key);
      var entries = [];
      pool.entries.forEach(function (entry) {
        var required = entry && entry.requires_craft === undefined ? [] : entry && entry.requires_craft;
        var minLevel = entry && entry.min_level === undefined ? 0 : entry && entry.min_level;
        // A damaged condition is rejected, never interpreted as ungated. The
        // allowlist also protects cheap crafted supplies; independent family
        // guards protect expensive parts if both the gate and list were edited.
        if (!entry || !validItem(entry.item) || !integer(entry.count, 1, 16) ||
            /plastic|hdpe|mayhem_cube/.test(entry.item) ||
            !finite(entry.weight) || entry.weight <= 0 || entry.weight > 1000000 ||
            rarities.indexOf(entry.rarity) < 0 || !integer(minLevel, 0, 6) ||
            !Array.isArray(required) || !required.every(function (id, index) {
              return typeof id === 'string' && productionItems[id] && required.indexOf(id) === index;
            }) || ((productionItems[entry.item] || protectedProduct(entry.item, entry.rarity)) && required.indexOf(entry.item) < 0) ||
            (protectedProduct(entry.item, entry.rarity) && entry.count !== 1)) {
          warn('entry:' + pool.key + ':' + entries.length, 'Ignoring invalid entry in pool ' + pool.key);
          return;
        }
        entries.push({item: entry.item, count: entry.count, weight: entry.weight, rarity: entry.rarity,
          requires_craft: required.slice(), min_level: minLevel, name: names(entry.name, entry.item)});
        result.itemNames[entry.item] = names(entry.name, entry.item);
      });
      result.pools[pool.key] = {key: pool.key, title: names(pool.title, pool.key), entries: entries,
        fallback: typeof pool.fallback === 'string' && poolPattern.test(pool.fallback) ? pool.fallback : 'survival'};
    });
    var mappings = raw.reward_mappings;
    if (!mappings || typeof mappings !== 'object' || Array.isArray(mappings)) throw new Error('Missing reward mappings');
    Object.keys(mappings).forEach(function (id) {
      var canonical = code(id), mapping = mappings[id];
      if (!canonical || !mapping || !result.pools[mapping.pool] || !code(mapping.quest_id)) {
        throw new Error('Invalid reward mapping ' + id);
      }
      result.mappings[canonical] = {pool: mapping.pool, quest_id: code(mapping.quest_id)};
    });
    return result;
  }

  function fallbackPolicy() {
    var pool = emergencyPool();
    var itemNames = {};
    pool.entries.forEach(function (entry) { itemNames[entry.item] = entry.name; });
    return {pools: {survival: pool}, mappings: {}, itemNames: itemNames, levels: [], draws: defaultDraws.slice(), qualityDraws: 0,
      weights: defaultRarityWeights.map(function (row) {
        var value = {}; rarities.forEach(function (rarity, index) { value[rarity] = row[index]; }); return value;
      })};
  }

  var policy;
  try { policy = compile(JSON.parse(JsonIO.readString('kubejs/config/soqm_rewards.json'))); }
  catch (error) {
    warn('policy', 'Policy unavailable or invalid; using basic supplies. ' + error);
    policy = fallbackPolicy();
  }
  try { ProductionEvidence = Java.loadClass('qm.rewards.ProductionEvidence'); }
  catch (error) { warn('adapter', 'Production evidence unavailable; manufactured prizes remain locked. ' + error); }
  try { RecipeRefunds = Java.loadClass('qm.rewards.RecipeRefunds'); }
  catch (error) { warn('refund-adapter', 'Recipe-refund adapter unavailable; fixed bundles require the shipped native adapter. ' + error); }

  function production(player) {
    var produced = Object.create(null), level = 0;
    if (ProductionEvidence !== null) {
      try {
        var iterator = ProductionEvidence.getProducedItems(player).iterator();
        while (iterator.hasNext()) produced[String(iterator.next())] = true;
      } catch (error) {
        produced = Object.create(null);
        warn('proof', 'Could not read production evidence; manufactured prizes remain locked. ' + error);
      }
    }
    policy.levels.forEach(function (milestone) {
      if (produced[milestone.item]) level = Math.max(level, milestone.level);
    });
    return {produced: produced, level: level, draws: policy.draws[level], qualityDraws: policy.qualityDraws};
  }

  function inspectPool(state, pool) {
    var available = [], locked = [], groups = {}, groupTotal = 0, qualityTotal = 0;
    rarities.forEach(function (rarity) { groups[rarity] = {rarity: rarity, entries: [], total: 0, weight: 0}; });
    pool.entries.forEach(function (entry) {
      var missing = entry.requires_craft.filter(function (id) { return !state.produced[id]; });
      if (missing.length || entry.min_level > state.level) {
        locked.push({entry: entry, missing: missing, level: entry.min_level > state.level ? entry.min_level : 0});
        return;
      }
      var group = groups[entry.rarity];
      group.entries.push(entry); group.total += entry.weight;
    });
    rarities.forEach(function (rarity) {
      var group = groups[rarity];
      if (group.entries.length) {
        group.weight = policy.weights[state.level][rarity]; groupTotal += group.weight;
        if (rarity !== 'common') qualityTotal += group.weight;
      }
    });
    rarities.forEach(function (rarity) {
      var group = groups[rarity];
      if (!(group.weight > 0) || !(groupTotal > 0)) return;
      group.entries.forEach(function (entry) {
        var probability = group.weight / groupTotal * entry.weight / group.total;
        var qualityProbability = qualityTotal > 0 ?
          (rarity === 'common' ? 0 : group.weight / qualityTotal * entry.weight / group.total) : probability;
        available.push({entry: entry, probability: probability, quality_probability: qualityProbability});
      });
    });
    return {pool: pool, available: available, locked: locked, groups: groups, groupTotal: groupTotal,
      qualityTotal: qualityTotal};
  }

  function view(state, key) {
    var requested = policy.pools[key], pool = requested, visited = {}, chosen;
    while (pool && !visited[pool.key]) {
      visited[pool.key] = true;
      chosen = inspectPool(state, pool);
      if (chosen.available.length) break;
      pool = policy.pools[pool.fallback];
    }
    if (!chosen || !chosen.available.length) chosen = inspectPool(state, emergencyPool());
    return {state: state, requested: requested, selected: chosen,
      locked: requested ? inspectPool(state, requested).locked : [],
      fallback: !requested || requested !== chosen.pool};
  }

  function weighted(values, weightOf, random) {
    var total = 0;
    values.forEach(function (value) { total += weightOf(value); });
    var unit = Number(random.nextDouble());
    if (!(total > 0) || !finite(total) || !finite(unit) || unit < 0 || unit >= 1) throw new Error('Invalid random selection');
    var position = unit * total;
    for (var i = 0; i < values.length; i++) {
      position -= weightOf(values[i]);
      if (position < 0) return values[i];
    }
    return values[values.length - 1];
  }

  function plan(player, key) {
    var current = view(production(player), key), groups = [];
    rarities.forEach(function (rarity) {
      var group = current.selected.groups[rarity];
      if (group.weight > 0 && group.entries.length) groups.push(group);
    });
    var combined = Object.create(null), random = player.getRandom();
    var qualityGroups = groups.filter(function (group) { return group.rarity !== 'common'; });
    for (var draw = 0; draw < current.state.draws; draw++) {
      var drawGroups = draw < current.state.qualityDraws && qualityGroups.length ? qualityGroups : groups;
      var group = weighted(drawGroups, function (value) { return value.weight; }, random);
      var entry = weighted(group.entries, function (value) { return value.weight; }, random);
      combined[entry.item] = (combined[entry.item] || 0) + entry.count;
    }
    return combined;
  }

  FTBQuestsEvents.customReward(function (event) {
    var reward = event.getReward(), player = event.getPlayer();
    // The native pre-claim gate and once-per-claim event listener own the fixed
    // refund and preserved cube. This script adds only the original random pack.
    // Native routing also remains correct if this JS policy fell back to supplies.
    if (reward.hasTag(refundMarker) && (RecipeRefunds === null || !RecipeRefunds.beginRandomPack(reward, player))) return;
    var mapping = policy.mappings[code(String(reward.getCodeString()))];
    if (!mapping && !reward.hasTag(marker)) return;
    if (!(player instanceof ServerPlayer) || player instanceof FakePlayer || player.isSpectator()) return;
    if (mapping && mapping.quest_id !== code(String(reward.getQuest().getCodeString()))) mapping = null;
    var key = mapping ? mapping.pool : '';
    if (!mapping) warn('mapping', 'Missing or mismatched mapping for ' + reward.getCodeString() + '; using basic supplies');
    var chosen;
    try { chosen = plan(player, key); }
    catch (error) {
      warn('selection', 'Could not prepare a reward; using guaranteed basic supplies. ' + error);
      chosen = Object.create(null);
      var fallback = emergencyPool().entries[0];
      if (!fallback) throw new Error('No registered vanilla reward fallback');
      chosen[fallback.item] = fallback.count * 2;
    }
    // Construct every delivery before giving anything. Do not retry the whole
    // reward after a partial native delivery: that would duplicate earlier items.
    var deliveries = Object.keys(chosen).map(function (id) {
      // ID 0 is transient and never added to QuestFile. ItemReward.claim uses only
      // the stack/count; the actual 64-bit outer ID never passes through JS Number.
      return new ItemReward(0, reward.getQuest(), Item.of(id), chosen[id]);
    });
    deliveries.forEach(function (delivery) { delivery.claim(player, event.getNotify()); });
  });

  function percent(probability) {
    return probability > 0 && probability < 0.0001 ? '<0.01%' : (Math.round(probability * 10000) / 100).toFixed(2) + '%';
  }

  function status(player) {
    var state = production(player), locale = language(player);
    say(player, 'Рівень нагород: ' + state.level + '/6; призів за квест: ' + state.draws + '.',
      'Reward level: ' + state.level + '/6; prizes per quest: ' + state.draws + '.');
    say(player, 'Особисте отримання раз за квест. Кожен приз розігрується окремо; повтори додаються.',
      'One personal claim per quest. Each prize is rolled independently; duplicate items are combined.');
    say(player, 'Вказані в квесті компенсації додають витрачені компоненти до набору або куба після справжнього виготовлення вами чи командою.',
      'Refunds listed in a quest add consumed components to its pack or cube after actual manufacture by you or your team.');
    if (state.qualityDraws) say(player,
      'Один приз — щонайменше незвичайний, якщо такий уже відкрито в темі. Решта — звичайні жеребкування.',
      'One prize is uncommon or better when this theme has an eligible prize of that quality. The remaining draws use the full pool.');
    say(player, 'Колишні спільні нагороди мають одне нове особисте отримання. Старі особисті отримання збережено.',
      'Previously shared rewards allow one new personal claim. Earlier personal claims remain claimed.');
    policy.levels.forEach(function (milestone) {
      var done = !!state.produced[milestone.item];
      say(player, (done ? '✓ ' : '○ ') + milestone.level + ': ' + milestone.title[locale],
        (done ? '✓ ' : '○ ') + milestone.level + ': ' + milestone.title[locale]);
    });
    Object.keys(policy.pools).sort().forEach(function (key) {
      var inspected = view(state, key);
      var description = policy.pools[key].title[locale] + ' — /soqm rewards pool ' + key;
      say(player, description + ' (' + inspected.selected.available.length + ' доступно, ' + inspected.locked.length + ' закрито)',
        description + ' (' + inspected.selected.available.length + ' available, ' + inspected.locked.length + ' locked)');
    });
    return 1;
  }

  function showPool(player, key) {
    var current = view(production(player), key), locale = language(player);
    var title = (current.requested || current.selected.pool).title[locale];
    say(player, title + ' — рівень ' + current.state.level + ', ' + current.state.draws + ' призи за квест.',
      title + ' — level ' + current.state.level + ', ' + current.state.draws + ' prizes per quest.');
    say(player, 'Шанс вказано за одне жеребкування. Предмет може випасти повторно.',
      'Chances are for one draw. The same item can be rolled again.');
    if (current.state.qualityDraws) say(player,
      'Перше жеребкування: лише відкриті незвичайні або кращі призи. Без них діє звичайний пул.',
      'First draw: eligible uncommon-or-better prizes only. Without one, the normal pool is used.');
    if (current.fallback) say(player, 'Резервний пул: ' + current.selected.pool.title[locale] + '.',
      'Fallback pool: ' + current.selected.pool.title[locale] + '.');
    current.selected.available.forEach(function (available) {
      var entry = available.entry;
      var line = entry.count + ' × ' + entry.name[locale] + ' [' + rarityNames[locale][entry.rarity] + '] — ' + percent(available.probability);
      if (current.state.qualityDraws) line += (locale === 'uk_ua' ? '; перше: ' : '; first: ') + percent(available.quality_probability);
      say(player, line, line);
    });
    current.locked.forEach(function (locked) {
      var entry = locked.entry;
      var missing = locked.missing.map(function (id) { return policy.itemNames[id] ? policy.itemNames[id][locale] : id; }).join(', ');
      var uk = entry.name[locale] + ' — закрито: ', en = entry.name[locale] + ' — locked: ';
      if (locked.level) { uk += 'рівень ' + locked.level; en += 'level ' + locked.level; }
      if (missing) { uk += (locked.level ? '; ' : '') + 'виготовте ' + missing; en += (locked.level ? '; ' : '') + 'produce ' + missing; }
      say(player, uk, en);
    });
    return 1;
  }

  ServerEvents.commandRegistry(function (event) {
    var C = event.commands;
    var rewards = C.literal('rewards').executes(function (ctx) { return status(ctx.getSource().getPlayerOrException()); });
    rewards.then(C.literal('status').executes(function (ctx) { return status(ctx.getSource().getPlayerOrException()); }));
    var pools = C.literal('pool');
    Object.keys(policy.pools).sort().forEach(function (key) {
      pools.then(C.literal(key).executes(function (ctx) { return showPool(ctx.getSource().getPlayerOrException(), key); }));
    });
    rewards.then(pools);
    // No grant/reset/unlock branch. Commands only read the same state and
    // probability calculation that the native claim callback uses.
    event.register(C.literal('soqm').then(rewards));
  });

  return {preview: function (player, key) { return view(production(player), key); }};
})();
