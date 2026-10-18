// Mayhem v0.4.0: one consumed book resets allocation, never point sources.
StartupEvents.registry('item', event => {
  event.create('mayhem_retraining_manual')
    .displayName('Посібник перенавчання: цього разу подумай')
    .texture('minecraft:item/book')
    .maxStackSize(16);
});
