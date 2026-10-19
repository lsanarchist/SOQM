package qm.rewards.mixin;

import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.tile.machine.TileEntityMetallurgicInfuser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.rewards.ProductionEvidence;

@Mixin(value = TileEntityMetallurgicInfuser.class, remap = false)
public abstract class InfuserProductionMixin {
    @Redirect(method = "createNewCachedRecipe(Lmekanism/api/recipes/ItemStackChemicalToItemStackRecipe;I)Lmekanism/api/recipes/cache/CachedRecipe;",
            at = @At(value = "INVOKE", target = "Lmekanism/api/recipes/cache/CachedRecipe;setOnFinish(Ljava/lang/Runnable;)Lmekanism/api/recipes/cache/CachedRecipe;"))
    private CachedRecipe<ItemStackChemicalToItemStackRecipe> qm$observeFinish(
            CachedRecipe<ItemStackChemicalToItemStackRecipe> cached, Runnable original,
            ItemStackChemicalToItemStackRecipe recipe, int index) {
        return cached.setOnFinish(() -> {
            // CachedRecipe invokes this only after finishProcessing consumed the
            // inputs and committed the output. Preserve the original callback.
            original.run();
            ProductionEvidence.infuserFinished((TileEntityMetallurgicInfuser) (Object) this, recipe);
        });
    }
}
