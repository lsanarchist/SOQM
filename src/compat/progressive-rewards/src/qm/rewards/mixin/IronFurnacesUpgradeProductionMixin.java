package qm.rewards.mixin;

import ironfurnaces.items.upgrades.ItemUpgrade;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.rewards.ProductionEvidence;

@Mixin(value = ItemUpgrade.class, remap = false)
public abstract class IronFurnacesUpgradeProductionMixin {
    @Shadow private Block from;
    @Shadow private Block to;

    // In pinned 4.3.2 this single shrink follows the from-tier check and both
    // native replacement branches. Creative use skips it. The method itself
    // returns vanilla Item.useOn's PASS even when the replacement succeeds.
    @Redirect(method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), require = 1)
    private void qm$afterConsumed(ItemStack consumed, int count, UseOnContext context) {
        int previous = consumed.getCount();
        // Capture identity before the last item is consumed: empty stacks expose AIR.
        boolean nativeUpgrade = consumed.getItem() == (Item) (Object) this;
        consumed.shrink(count);
        if (nativeUpgrade && count == 1 && previous >= count && previous - consumed.getCount() == count) {
            ProductionEvidence.furnaceUpgraded(context, from, to);
        }
    }
}
