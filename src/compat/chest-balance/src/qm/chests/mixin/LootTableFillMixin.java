package qm.chests.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.chests.SparseChestLoot;

@Mixin(LootTable.class)
public abstract class LootTableFillMixin {
    @Shadow public abstract ResourceLocation getLootTableId();

    @Inject(method = "shuffleAndSplitItems", at = @At("HEAD"), cancellable = true)
    private void qm$compactWorldChest(ObjectArrayList<ItemStack> loot, int slots, RandomSource random, CallbackInfo ci) {
        if (SparseChestLoot.compact(getLootTableId(), loot, slots, random)) ci.cancel();
    }
}
