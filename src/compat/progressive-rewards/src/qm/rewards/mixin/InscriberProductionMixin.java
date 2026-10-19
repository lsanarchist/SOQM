package qm.rewards.mixin;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.blockentity.misc.InscriberBlockEntity;
import appeng.recipes.handlers.InscriberRecipe;
import appeng.util.inv.AppEngInternalInventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.rewards.ProductionEvidence;

@Mixin(value = InscriberBlockEntity.class, remap = false)
public abstract class InscriberProductionMixin {
    @Shadow private int finalStep;
    @Shadow @Final private AppEngInternalInventory sideItemHandler;
    @Shadow @Final private AppEngInternalInventory topItemHandler;
    @Shadow @Final private AppEngInternalInventory bottomItemHandler;
    @Unique private ItemStack qm$pendingProduction = ItemStack.EMPTY;

    @Inject(method = "tickingRequest", at = @At("HEAD"))
    private void qm$capture(IGridNode node, int ticks, CallbackInfoReturnable<TickRateModulation> callback) {
        qm$pendingProduction = ItemStack.EMPTY;
        InscriberBlockEntity machine = (InscriberBlockEntity) (Object) this;
        if (!machine.isSmash() || finalStep != 7) return;
        InscriberRecipe recipe = machine.getTask();
        if (recipe == null) return;
        ItemStack output = recipe.getResultItem();
        if (!ProductionEvidence.isTrackedOutput(output)) return;
        ItemStack middle = sideItemHandler.getStackInSlot(0);
        if (middle.isEmpty() || middle.is(output.getItem())
                || topItemHandler.getStackInSlot(0).is(output.getItem())
                || bottomItemHandler.getStackInSlot(0).is(output.getItem())) return;
        qm$pendingProduction = output.copy();
    }

    // Ordinal 2 is the mandatory middle-input extraction in the accepted-output
    // branch. The two preceding PRESS extractions are optional top/bottom inputs.
    // Simulated output insertion and blocked output never reach this instruction.
    @Inject(method = "tickingRequest", at = @At(value = "INVOKE",
            target = "Lappeng/util/inv/AppEngInternalInventory;extractItem(IIZ)Lnet/minecraft/world/item/ItemStack;",
            ordinal = 2, shift = At.Shift.AFTER))
    private void qm$committed(IGridNode node, int ticks, CallbackInfoReturnable<TickRateModulation> callback) {
        if (qm$pendingProduction.isEmpty()) return;
        InscriberBlockEntity machine = (InscriberBlockEntity) (Object) this;
        ProductionEvidence.ae2Finished(machine, machine.getMainNode(), qm$pendingProduction);
        qm$pendingProduction = ItemStack.EMPTY;
    }

    @Inject(method = "tickingRequest", at = @At("RETURN"))
    private void qm$clear(IGridNode node, int ticks, CallbackInfoReturnable<TickRateModulation> callback) {
        qm$pendingProduction = ItemStack.EMPTY;
    }
}
