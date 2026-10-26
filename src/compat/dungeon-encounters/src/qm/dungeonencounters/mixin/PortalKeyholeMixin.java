package qm.dungeonencounters.mixin;

import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.item.BaseItemKey;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.dungeonencounters.QmExpeditions;

@Mixin(value = BlockPortalKeyhole.class, remap = false)
public abstract class PortalKeyholeMixin {
    @Redirect(method = "checkForProblemsAndLiterallySpeakToPlayer", at = @At(value = "INVOKE",
        target = "Lcom/catastrophe573/dimdungeons/item/BaseItemKey;getKeyLevel(Lnet/minecraft/world/item/ItemStack;)I"), remap = false)
    private static int qm$explainMissingExpeditionCrowns(BaseItemKey key, ItemStack stack) {
        return QmExpeditions.portalLevel(key, stack);
    }
}
