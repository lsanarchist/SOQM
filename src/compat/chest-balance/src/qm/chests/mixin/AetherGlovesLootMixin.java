package qm.chests.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.chests.SparseChestLoot;

/** Aether's replacement copies enchantments but drops affixes and custom data. */
@Pseudo
@Mixin(targets = "com.aetherteam.aether.loot.modifiers.GlovesLootModifier", remap = false)
public abstract class AetherGlovesLootMixin {
    // Exact candidate predicate in the pinned Aether 1.5.10 implementation.
    // Ordinary armor still uses Aether's original eligibility and 25% roll.
    @Inject(method = "lambda$doApply$3(Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void qm$keepAuthoredCombatPrize(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (SparseChestLoot.isDungeonCombatReward(stack)) cir.setReturnValue(false);
    }
}
