package qm.rewards.mixin;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.blockentity.crafting.MolecularAssemblerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.rewards.ProductionEvidence;

@Mixin(value = MolecularAssemblerBlockEntity.class, remap = false)
public abstract class AssemblerProductionMixin {
    @Unique private ItemStack qm$pendingProduction = ItemStack.EMPTY;

    @Inject(method = "tickingRequest", at = @At("HEAD"))
    private void qm$begin(IGridNode node, int ticks, CallbackInfoReturnable<TickRateModulation> callback) {
        qm$pendingProduction = ItemStack.EMPTY;
    }

    @Redirect(method = "tickingRequest", at = @At(value = "INVOKE",
            target = "Lappeng/blockentity/crafting/IMolecularAssemblerSupportedPattern;assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack qm$capture(IMolecularAssemblerSupportedPattern pattern, CraftingInput input, Level level) {
        ItemStack result = pattern.assemble(input, level);
        if (ProductionEvidence.isTrackedOutput(result) && ProductionEvidence.isManufacture(result, input)) {
            qm$pendingProduction = result.copy();
        }
        return result;
    }

    // The second saveChanges in this method follows both output acceptance into
    // native pushOut and replacement of every recipe input with its remainder.
    // The first one is merely retrying delivery of a previously crafted item.
    @Inject(method = "tickingRequest", at = @At(value = "INVOKE",
            target = "Lappeng/blockentity/crafting/MolecularAssemblerBlockEntity;saveChanges()V",
            ordinal = 1, shift = At.Shift.AFTER))
    private void qm$committed(IGridNode node, int ticks, CallbackInfoReturnable<TickRateModulation> callback) {
        if (qm$pendingProduction.isEmpty()) return;
        MolecularAssemblerBlockEntity machine = (MolecularAssemblerBlockEntity) (Object) this;
        ProductionEvidence.ae2Finished(machine, machine.getMainNode(), qm$pendingProduction);
        qm$pendingProduction = ItemStack.EMPTY;
    }

    @Inject(method = "tickingRequest", at = @At("RETURN"))
    private void qm$clear(IGridNode node, int ticks, CallbackInfoReturnable<TickRateModulation> callback) {
        qm$pendingProduction = ItemStack.EMPTY;
    }
}
