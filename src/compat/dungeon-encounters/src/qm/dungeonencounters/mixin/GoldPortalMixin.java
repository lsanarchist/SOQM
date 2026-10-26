package qm.dungeonencounters.mixin;

import com.catastrophe573.dimdungeons.block.BlockGoldPortal;
import com.catastrophe573.dimdungeons.item.BaseItemKey;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.dungeonencounters.QmExpeditions;

@Mixin(value = BlockGoldPortal.class, remap = false)
public abstract class GoldPortalMixin {
    @Redirect(method = "isPortalShapeIntact", at = @At(value = "INVOKE",
        target = "Lcom/catastrophe573/dimdungeons/item/BaseItemKey;getKeyLevel(Lnet/minecraft/world/item/ItemStack;)I"), remap = false)
    private int qm$requireAdvancedExpeditionFrame(BaseItemKey key, ItemStack stack) {
        return QmExpeditions.portalLevel(key, stack);
    }
}
