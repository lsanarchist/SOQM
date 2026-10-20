package qm.questui.client;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Client-only resource view; server-side production checks remain authoritative. */
public final class RewardCatalogue {
    private static final Logger LOGGER = LoggerFactory.getLogger("qm_quest_ui");
    private static final ResourceLocation RESOURCE = ResourceLocation.fromNamespaceAndPath("qm_quest_ui", "reward_catalog.json");
    private static RewardCatalogueData cached;
    private RewardCatalogue() {}

    public static void invalidate() { cached = null; }

    public static RewardCatalogueData data() {
        if (cached == null) {
            try (var input = Minecraft.getInstance().getResourceManager().getResourceOrThrow(RESOURCE).open();
                 var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                cached = RewardCatalogueData.parse(reader);
            } catch (Exception invalid) {
                LOGGER.warn("Reward preview catalogue could not be loaded; native claims are unaffected", invalid);
                cached = RewardCatalogueData.empty();
            }
        }
        return cached;
    }

    public static ItemStack stack(String id, int count) {
        ResourceLocation key = ResourceLocation.tryParse(id);
        if (key == null || !BuiltInRegistries.ITEM.containsKey(key)) return ItemStack.EMPTY;
        return new ItemStack(BuiltInRegistries.ITEM.get(key), count);
    }
    public static String language() { return Minecraft.getInstance().getLanguageManager().getSelected(); }
}
