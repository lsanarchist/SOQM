// Personal loot containers must not disappear as part of a mass-mining action.
// Additive: preserves the existing Chance Cubes exclusions and native tags.
ServerEvents.tags('block', event => {
  event.add('ftbultimine:excluded_blocks', '#lootr:containers');
});
