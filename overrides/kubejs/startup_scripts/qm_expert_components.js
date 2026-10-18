// Five manufactured components; never awarded by quests, loot or EMC.
StartupEvents.registry('item', function (event) {
  event.create("industrial_chassis").displayName("Промислове шасі").texture("kubejs:item/industrial_chassis").maxStackSize(64);
  event.create("precision_mechanism").displayName("Точний механізм").texture("kubejs:item/precision_mechanism").maxStackSize(64);
  event.create("dimensional_matrix").displayName("Вимірна матриця").texture("kubejs:item/dimensional_matrix").maxStackSize(64);
  event.create("stellar_alloy").displayName("Зоряний композит").texture("kubejs:item/stellar_alloy").maxStackSize(64);
  event.create("mythic_circuit").displayName("Міфічна схема").texture("kubejs:item/mythic_circuit").maxStackSize(64);
});
