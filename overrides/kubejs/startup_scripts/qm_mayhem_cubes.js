// Questing Mayhem 0.3. A full restart is required for new block registration.
// These are quest cubes using the Chance Cubes reward engine. They are visibly
// distinct from native blue world-generated Chance Cubes and have isolated tiers.
StartupEvents.registry('block', event => {
  const names = ['Мандрівний', 'Сталевий', 'Інженерний', 'Міжсвітовий', 'Зоряний', 'Міфічний', 'Майстерний'];
  const scope = [
    'Виживання, поселення й перші пригоди.',
    'Сталь, базова енергія, мана та поверхня DivineRPG.',
    'Сплави, автоматизація та Сутінковий ліс.',
    'Мережа ME, терасталь, Aether і спеціальні машини.',
    'Місяць, Eden—Apalachia та реакторні матеріали.',
    'Ґая, Mortum і підготовка до синтезу.',
    'Антиматерія, трансмутація та завершення кампанії.'
  ];
  names.forEach((name, tier) => {
    event.create('mayhem_cube_t' + tier)
      .displayName('Квестовий куб: ' + name)
      .parentModel('kubejs:block/mayhem_cube_t' + tier + '_visual')
      .hardness(0.35)
      .resistance(3600000)
      .soundType('glass')
      .lightLevel(0.3)
      .noDrops()
      .noValidSpawns(true)
      .suffocating(false)
      .item(item => item
        .maxStackSize(64)
        .rarity(tier < 2 ? 'uncommon' : tier < 5 ? 'rare' : 'epic')
        .tooltip('Етап ' + tier + '. ' + scope[tier])
        .tooltip('Постав і розбий рукою або інструментом у Survival.')
        .tooltip('До 64 різних подій: споруди — 20%, решта — лут, бій і сюрпризи.')
        .tooltip('Споруди замінюють блоки навколо. Відкривай подалі від бази!')
        .tooltip('Останні 4 типи подій не повторюються; варіанти й лут змінюються.')
        .tooltip('Вищі куби чекають відповідного технічного рубежу; до нього куб цілий.')
        .tooltip('Без крафту, EMC, дропу блока та масового ламання Ultimine.'));
  });
});
