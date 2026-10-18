// YIGD 2.0.13: observe a successful recovery of the player's own grave.
var QM_GRAVE_EVENT = Java.loadClass('com.b1n_ry.yigd.events.YigdEvents$GraveClaimEvent');
var QM_GRAVE_PRIORITY = Java.loadClass('net.neoforged.bus.api.EventPriority');
var QM_GRAVE_RL = Java.loadClass('net.minecraft.resources.ResourceLocation');
var QM_GRAVE_FAKE = Java.loadClass('net.neoforged.neoforge.common.util.FakePlayer');

NativeEvents.onEvent(QM_GRAVE_PRIORITY.HIGHEST, QM_GRAVE_EVENT, event => {
  var player = event.getPlayer();
  var grave = event.getGrave();
  if (player == null || player instanceof QM_GRAVE_FAKE) return;
  var owner = grave.getOwner().id();
  if (owner.isEmpty() || !player.getGameProfile().getId().equals(owner.get())) return;
  var server = event.getLevel().getServer();
  server.scheduleInTicks(1, () => {
    if (String(grave.getStatus()) !== 'CLAIMED') return;
    var holder = server.getAdvancements().get(QM_GRAVE_RL.parse('qm:survival/grave_reclaimed'));
    if (holder != null) player.getAdvancements().award(holder, 'claimed');
  });
});
