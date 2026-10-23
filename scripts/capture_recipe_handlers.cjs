// Execute the pack's recipe handlers, without loading a game or serializers.
// KubeJS 2101.7.2 keeps native originals and queued additions separate.
const fs = require('node:fs');
const vm = require('node:vm');

const input = JSON.parse(fs.readFileSync(0, 'utf8'));
const sources = input.sources.map(source => ({
  ...source,
  priority: Number(source.text.match(/^\s*\/\/\s*priority:\s*(-?\d+)\s*$/m)?.[1] ?? 0),
})).sort((a, b) => b.priority - a.priority || (a.name < b.name ? -1 : a.name > b.name ? 1 : 0));
const handlers = [];
for (const source of sources) {
  vm.runInNewContext(source.text, {
    ServerEvents: {
      recipes(callback) {
        if (typeof callback !== 'function') throw new Error('Expected a recipe callback');
        handlers.push({callback, source: source.name, priority: source.priority});
      },
      // Tag callbacks are registered separately and are not recipe callbacks.
      tags() {},
    },
  }, {filename: source.name, timeout: 5000});
}

const originals = new Map(input.native_ids.map(id => [id, {id, removed: false}]));
const added = [];
const removals = [];
const handlerResults = [];
let sourceName;
const event = {
  addedRecipes: added,
  remove(filter) {
    if (Object.keys(filter).join(',') !== 'id' || typeof filter.id !== 'string') {
      throw new Error('Unsupported recipe removal filter: ' + JSON.stringify(filter));
    }
    const original = originals.get(filter.id);
    if (original) original.removed = true;
    removals.push({id: filter.id, source: sourceName});
  },
  custom(recipe) {
    const row = {
      source: sourceName, recipe, recipeId: null, removed: false,
      id(id) { this.recipeId = id; return this; },
      getId() { return this.recipeId; },
      remove() { this.removed = true; },
    };
    added.push(row);
    return row;
  },
};
for (const handler of handlers) {
  sourceName = handler.source;
  const before = added.length;
  handler.callback(event);
  handlerResults.push({source: handler.source, priority: handler.priority, added: added.length - before});
}
const rows = added.map(row => ({id: row.recipeId, source: row.source, recipe: row.recipe, removed: row.removed}));
const liveIds = new Set();
for (const row of rows.filter(row => !row.removed)) {
  if (typeof row.id !== 'string' || liveIds.has(row.id)) throw new Error('Missing or duplicate active ID: ' + row.id);
  liveIds.add(row.id);
}
process.stdout.write(JSON.stringify({
  rows,
  handlers: handlerResults,
  removals,
  removed_native_ids: [...originals.values()].filter(row => row.removed).map(row => row.id),
}));
