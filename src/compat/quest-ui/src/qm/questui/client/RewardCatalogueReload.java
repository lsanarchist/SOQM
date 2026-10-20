package qm.questui.client;

import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = "qm_quest_ui", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RewardCatalogueReload {
    private RewardCatalogueReload() {}
    @SubscribeEvent
    public static void register(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> RewardCatalogue.invalidate());
    }
}
