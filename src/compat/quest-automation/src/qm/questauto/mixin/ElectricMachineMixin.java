package qm.questauto.mixin;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.questauto.QmQuestAutomation;

@Mixin(value = TileEntityElectricMachine.class, remap = false)
public abstract class ElectricMachineMixin {
    @Redirect(method = "createNewCachedRecipe(Lmekanism/api/recipes/ItemStackToItemStackRecipe;I)Lmekanism/api/recipes/cache/CachedRecipe;", at = @At(value = "INVOKE", target = "Lmekanism/api/recipes/cache/CachedRecipe;setOnFinish(Ljava/lang/Runnable;)Lmekanism/api/recipes/cache/CachedRecipe;"), require = 1)
    private CachedRecipe<ItemStackToItemStackRecipe> qm$observeFinish(CachedRecipe<ItemStackToItemStackRecipe> cached, Runnable original, ItemStackToItemStackRecipe recipe, int index) {
        return cached.setOnFinish(() -> {
            original.run();
            QmQuestAutomation.electricFinished((TileEntityElectricMachine)(Object)this, recipe);
        });
    }
}
