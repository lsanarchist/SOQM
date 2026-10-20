package qm.questui.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import qm.questui.HudConfig;

@EventBusSubscriber(modid = "qm_quest_ui", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class HudControls {
    public static final KeyMapping UP = key("tracker_up", 266);
    public static final KeyMapping DOWN = key("tracker_down", 267);
    private static boolean checked;
    private static KeyMapping key(String name, int code) {
        return new KeyMapping("key.qm_quest_ui." + name, KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, code, "key.categories.qm_quest_ui");
    }
    @SubscribeEvent public static void register(RegisterKeyMappingsEvent event) {
        event.register(UP); event.register(DOWN);
    }
    @EventBusSubscriber(modid = "qm_quest_ui", value = Dist.CLIENT)
    public static final class GameEvents {
        @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
            var mc = Minecraft.getInstance();
            if (!checked) {
                // Options have now loaded. Existing custom bindings win; a new
                // default conflicting with another action becomes unbound.
                checked = true;
                for (var own : new KeyMapping[]{UP, DOWN}) {
                    if (!own.isDefault()) continue;
                    for (var other : mc.options.keyMappings) {
                        if (other != own && !other.isUnbound() && other.getKey().equals(own.getKey())) {
                            own.setKey(InputConstants.UNKNOWN); break;
                        }
                    }
                }
                KeyMapping.resetMapping();
            }
            while (UP.consumeClick()) if (HudConfig.enabled() && mc.screen == null && !mc.options.hideGui) HudQuestTracker.scroll(-1);
            while (DOWN.consumeClick()) if (HudConfig.enabled() && mc.screen == null && !mc.options.hideGui) HudQuestTracker.scroll(1);
        }
    }
}
