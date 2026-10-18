// Questing Mayhem 1.21.1 prototype. Requires a full game/server restart.
// One consumable progression component; no gameplay callbacks or commands.
StartupEvents.registry('item', event => {
  event.create('emc_core')
    .displayName('Ядро EMC')
    .texture('kubejs:item/emc_core')
    .glow(true)
    .tooltip('Ключ до пізньої трансмутації: антиматерія, Ґая, Mortum і калорит.')
})
