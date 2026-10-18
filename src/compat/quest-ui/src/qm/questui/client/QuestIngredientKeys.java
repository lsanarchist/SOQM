package qm.questui.client;

import java.util.List;
import java.util.Optional;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.util.client.PositionedIngredient;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbxmodcompat.ftbquests.jei.FTBQuestsJEIIntegration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import qm.questui.mixin.BaseScreenAccess;

/** Fallback after native quest controls; always uses the user's current JEI bindings. */
public final class QuestIngredientKeys {
    public enum Outcome { UNAVAILABLE, INVALID, NO_RECIPES, SHOWN }
    private static ItemStack lastMissing = ItemStack.EMPTY;
    private static long missingUntil;

    private QuestIngredientKeys() {}

    public static Optional<ItemStack> hoveredItem(QuestScreen screen) {
        BaseScreenAccess access = (BaseScreenAccess) screen;
        Widget focused = access.qm$getFocusedWidget();
        if (focused instanceof TextBox text && text.isFocused()) return Optional.empty();
        ModalPanel top = access.qm$getModalPanels().peekFirst();
        // The quest details are themselves a modal panel, separate from the main widgets.
        // Other modals (editors, confirmations and context menus) retain exclusive input.
        if (top != null && top != screen.viewQuestPanel) return Optional.empty();
        Optional<PositionedIngredient> ingredient = top == null
                ? screen.getIngredientUnderMouse() : top.getIngredientUnderMouse();
        return ingredient.map(PositionedIngredient::ingredient)
                .filter(ItemStack.class::isInstance).map(ItemStack.class::cast)
                .filter(stack -> !stack.isEmpty());
    }

    public static boolean handle(QuestScreen screen, Key key) {
        IJeiRuntime runtime = FTBQuestsJEIIntegration.runtime;
        if (runtime == null) return false;
        RecipeIngredientRole role;
        if (runtime.getKeyMappings().getShowRecipe().isActiveAndMatches(key.getInputMapping()))
            role = RecipeIngredientRole.OUTPUT;
        else if (runtime.getKeyMappings().getShowUses().isActiveAndMatches(key.getInputMapping()))
            role = RecipeIngredientRole.INPUT;
        else return false;
        Optional<ItemStack> hovered = hoveredItem(screen);
        if (hovered.isEmpty()) return false;
        Outcome outcome = show(runtime, hovered.get(), role);
        if (outcome == Outcome.NO_RECIPES) {
            lastMissing = hovered.get().copyWithCount(1);
            missingUntil = System.nanoTime() + 5_000_000_000L;
        } else if (outcome == Outcome.SHOWN) {
            lastMissing = ItemStack.EMPTY;
        }
        return outcome == Outcome.SHOWN || outcome == Outcome.NO_RECIPES;
    }

    /** Public JEI interfaces only; separated for honest headless focus-routing probes. */
    public static Outcome show(IJeiRuntime runtime, ItemStack stack, RecipeIngredientRole role) {
        if (runtime == null) return Outcome.UNAVAILABLE;
        if (stack == null || stack.isEmpty()) return Outcome.INVALID;
        var typed = runtime.getIngredientManager().createTypedIngredient(VanillaTypes.ITEM_STACK, stack, false);
        if (typed.isEmpty()) return Outcome.INVALID;
        IFocus<ItemStack> focus = runtime.getJeiHelpers().getFocusFactory().createFocus(role, typed.get());
        try (var categories = runtime.getRecipeManager().createRecipeCategoryLookup().limitFocus(List.of(focus)).get()) {
            if (categories.findAny().isEmpty()) return Outcome.NO_RECIPES;
        }
        runtime.getRecipesGui().show(focus);
        return Outcome.SHOWN;
    }

    public static Component recipeKey() {
        IJeiRuntime runtime = FTBQuestsJEIIntegration.runtime;
        return runtime == null ? Component.literal("R") : runtime.getKeyMappings().getShowRecipe().getTranslatedKeyMessage();
    }

    public static Component usesKey() {
        IJeiRuntime runtime = FTBQuestsJEIIntegration.runtime;
        return runtime == null ? Component.literal("U") : runtime.getKeyMappings().getShowUses().getTranslatedKeyMessage();
    }

    public static boolean missingNotice(ItemStack stack) {
        return !lastMissing.isEmpty() && System.nanoTime() < missingUntil
                && ItemStack.isSameItemSameComponents(lastMissing, stack);
    }
}
