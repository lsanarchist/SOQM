// Dedicated flight part; no Botania recipe depends on this item.
StartupEvents.registry('item', function(event) {
  event.create('propulsion_turbine').displayName('Реактивна турбіна').texture('kubejs:item/propulsion_turbine').maxStackSize(64).tooltip('Дві за крафт: Ad Astra fan/engine frame, приводи, IE coil MV, basic chemical tank і блоки олова. Для Jetpack та пізнього технологічного польоту.');
});
