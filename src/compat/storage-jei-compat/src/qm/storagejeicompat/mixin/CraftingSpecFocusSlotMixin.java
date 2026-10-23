package qm.storagejeicompat.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common.CraftingDisplaySpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Links the displayed storage slot, keeping the compact ingredient index intact. */
@Mixin(
    targets = "net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.jei.CraftingSpecCategoryExtension",
    remap = false
)
public abstract class CraftingSpecFocusSlotMixin {
    @ModifyArg(
        method = "setRecipe(Lnet/minecraft/world/item/crafting/RecipeHolder;Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lmezz/jei/api/gui/ingredient/ICraftingGridHelper;Lmezz/jei/api/recipe/IFocusGroup;)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;get(I)Ljava/lang/Object;",
            ordinal = 1
        ),
        index = 0,
        require = 1,
        allow = 1,
        remap = false
    )
    private int soqm$useDisplayedSourceSlot(
        int compactIndex,
        @Local(argsOnly = true) ICraftingGridHelper craftingGridHelper,
        @Local(ordinal = 0) CraftingDisplaySpec spec
    ) {
        int width = spec.width();
        int height = spec.height();
        if (width <= 0 || height <= 0) {
            // Match JEI 19.64.0.455 setInputs/getShapelessSize normalization.
            int inputs = spec.baseIngredients().size();
            width = height = inputs > 4 ? 3 : inputs > 1 ? 2 : 1;
        }
        // Only the second List.get uses JEI's nine physical slot builders.
        // The first List.get must retain the compact recipe ingredient index.
        return craftingGridHelper.getCraftingIndex(compactIndex, width, height);
    }
}
