// AE2 19.2.18 fires the native crafted-item event from AppEngCraftingSlot.
// Observe a completed manual ME-terminal craft; this is not an autocrafting job.
(function () {
  var ServerPlayer = Java.loadClass('net.minecraft.server.level.ServerPlayer');
  var FakePlayer = Java.loadClass('net.neoforged.neoforge.common.util.FakePlayer');
  var CraftingTermMenu = Java.loadClass('appeng.menu.me.items.CraftingTermMenu');
  var AEItemKey = Java.loadClass('appeng.api.stacks.AEItemKey');
  var ResourceLocation = Java.loadClass('net.minecraft.resources.ResourceLocation');
  var milestone = ResourceLocation.parse('qm:ae2/first_me_craft');

  ItemEvents.crafted(function (event) {
    var player = event.player;
    if (!(player instanceof ServerPlayer) || player instanceof FakePlayer || player.isSpectator()) return;
    var menu = player.containerMenu;
    if (!(menu instanceof CraftingTermMenu)) return;

    var advancement = player.getServer().getAdvancements().get(milestone);
    if (advancement === null || player.getAdvancements().getOrStartProgress(advancement).isDone()) return;
    var node = menu.getGridNode();
    // isActive requires a booted network, available power and a working channel.
    if (node === null || !node.isActive()) return;

    // A terminal with only manually filled crafting slots is not an ME store.
    // Inspect once, before this milestone has been earned, not on player ticks.
    var stored = node.getGrid().getStorageService().getInventory().getAvailableStacks().iterator();
    while (stored.hasNext()) {
      var entry = stored.next();
      if (entry.getKey() instanceof AEItemKey && entry.getLongValue() > 0) {
        player.getAdvancements().award(advancement, 'me_terminal_craft');
        return;
      }
    }
  });
})();
