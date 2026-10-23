package qm.foundrycompat.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import modernmods.modernfoundry.tables.block.entity.inventory.CraftingContainerWrapper;
import modernmods.modernfoundry.tables.block.entity.table.CraftingStationBlockEntity;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Corrects all three ingredient/remainder writes in the pinned 4.1.6 station. */
@Mixin(value = CraftingStationBlockEntity.class, remap = false)
public abstract class CraftingStationSlotMixin {
    @Shadow
    @Final
    private CraftingContainerWrapper craftingInventory;

    @ModifyArg(
        method = "takeResult(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lmodernmods/modernfoundry/tables/block/entity/table/CraftingStationBlockEntity;setItem(ILnet/minecraft/world/item/ItemStack;)V"
        ),
        index = 0,
        require = 3,
        allow = 3,
        remap = false
    )
    private int soqm$usePhysicalCraftingSlot(
        int compactIndex,
        @Local(ordinal = 0) CraftingInput.Positioned positioned
    ) {
        // Use the snapshot already captured by takeResult, before any slot changes.
        int recipeWidth = positioned.input().width();
        int column = compactIndex % recipeWidth;
        int row = compactIndex / recipeWidth;
        return positioned.left() + column
            + (positioned.top() + row) * craftingInventory.getWidth();
    }
}
