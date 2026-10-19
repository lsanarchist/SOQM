package qm.rewards.mixin;

import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.tile.factory.TileEntityItemStackChemicalToItemStackFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.rewards.ProductionEvidence;

@Mixin(value = TileEntityItemStackChemicalToItemStackFactory.class, remap = false)
public abstract class InfusingFactoryProductionMixin {
    @Redirect(method = "createNewCachedRecipe(Lmekanism/api/recipes/ItemStackChemicalToItemStackRecipe;I)Lmekanism/api/recipes/cache/CachedRecipe;",
            at = @At(value = "INVOKE", target = "Lmekanism/api/recipes/cache/CachedRecipe;setOnFinish(Ljava/lang/Runnable;)Lmekanism/api/recipes/cache/CachedRecipe;"))
    private CachedRecipe<ItemStackChemicalToItemStackRecipe> qm$observeFinish(
            CachedRecipe<ItemStackChemicalToItemStackRecipe> cached, Runnable original,
            ItemStackChemicalToItemStackRecipe recipe, int index) {
        TileEntityItemStackChemicalToItemStackFactory machine =
                (TileEntityItemStackChemicalToItemStackFactory) (Object) this;
        if (machine.getFactoryType() != FactoryType.INFUSING) return cached.setOnFinish(original);
        return cached.setOnFinish(() -> {
            original.run();
            ProductionEvidence.infuserFinished(machine, recipe);
        });
    }
}
