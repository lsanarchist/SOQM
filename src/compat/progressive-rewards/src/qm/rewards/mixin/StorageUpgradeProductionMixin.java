package qm.rewards.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageTierUpgradeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.rewards.ProductionEvidence;

@Mixin(value = StorageTierUpgradeItem.class, remap = false)
public abstract class StorageUpgradeProductionMixin {
    // This shrink is reached only after upgradeStorage returned true. It is not
    // reached for creative use, permission failure, a blocked tier, or too few
    // upgrade items. Observe a real consumption delta without changing the result.
    @Redirect(method = "tryUpgradeStorage(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/p3pp3rf1y/sophisticatedstorage/item/StorageTierUpgradeItem$TierUpgradeDefinition;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/InteractionResult;", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void qm$afterConsumed(ItemStack consumed, int count, ItemStack held,
            BlockPos pos, Level level, BlockState before, @Coerce Object definition,
            BlockEntity oldStorage, Player player) {
        int previous = consumed.getCount();
        consumed.shrink(count);
        if (count > 0 && previous >= count && previous - consumed.getCount() == count) {
            ProductionEvidence.storageUpgraded(player, level, pos, before);
        }
    }
}
