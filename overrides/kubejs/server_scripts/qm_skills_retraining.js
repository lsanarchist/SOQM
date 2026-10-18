// Pufferfish Skills 0.19.2 public API. Right-click the book in empty air.
var QM_SKILLS_API = Java.loadClass('net.puffish.skillsmod.api.SkillsAPI');
var QM_SKILLS_RL = Java.loadClass('net.minecraft.resources.ResourceLocation');
var QM_SKILLS_FAKE = Java.loadClass('net.neoforged.neoforge.common.util.FakePlayer');

function qmRetrainSkills(player, stack) {
  if (player == null || player.getLevel().isClientSide() || player instanceof QM_SKILLS_FAKE) return 'ignored';
  var category = QM_SKILLS_API.getCategory(QM_SKILLS_RL.parse('puffish_skills:mayhem'));
  if (category.isEmpty()) {
    player.tell('Дерево навичок не завантажене. Посібник залишився у тебе.');
    console.error('[QM Skills] Missing category puffish_skills:mayhem');
    return 'missing_category';
  }
  var mayhem = category.get();
  if (mayhem.getSpentPoints(player) === 0) {
    player.tell('Витрачених очок немає. Переосмислити нічого; посібник не витрачено.');
    return 'no_spent_points';
  }
  // Native reset removes attribute/tag rewards and refunds allocated points.
  // It does not change the six source balances or replay advancement rewards.
  mayhem.resetSkills(player);
  if (!player.isCreative()) stack.shrink(1);
  player.getInventory().setChanged();
  player.tell('Навички скинуто, очки повернуто. Друга спроба має виглядати мудріше.');
  return 'reset';
}
ItemEvents.rightClicked('kubejs:mayhem_retraining_manual', event => {
  qmRetrainSkills(event.player, event.item);
});
