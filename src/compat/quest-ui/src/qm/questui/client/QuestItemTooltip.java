package qm.questui.client;

import java.util.LinkedHashSet;
import java.util.List;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public final class QuestItemTooltip {
    private QuestItemTooltip() {}

    public static String modName(ItemStack stack) {
        String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
        if (namespace.equals("minecraft")) return "Minecraft";
        if (namespace.equals("kubejs")) return "Questing Mayhem";
        return ModList.get().getModContainerById(namespace)
                .map(container -> container.getModInfo().getDisplayName()).orElse(namespace);
    }

    public static void append(ItemTask task, TooltipList tooltip, boolean advanced) {
        List<ItemStack> items = task.getValidDisplayItems().stream().filter(stack -> !stack.isEmpty()).toList();
        if (items.isEmpty()) return;
        if (!task.getRawTitle().isEmpty()) {
            for (ItemStack stack : items.stream().limit(4).toList()) {
                tooltip.add(Component.translatable("qm_quest_ui.item", stack.getHoverName()).withStyle(ChatFormatting.GRAY));
            }
            if (items.size() > 4) tooltip.add(Component.translatable("qm_quest_ui.more_items", items.size() - 4).withStyle(ChatFormatting.GRAY));
        }
        LinkedHashSet<String> names = new LinkedHashSet<>();
        for (ItemStack stack : items) names.add(modName(stack));
        String shown = String.join(", ", names.stream().limit(4).toList());
        if (names.size() > 4) shown += " (+" + (names.size() - 4) + ")";
        tooltip.add(Component.translatable("qm_quest_ui.mod", shown).withStyle(ChatFormatting.BLUE));
        if (advanced && !task.getRawTitle().isEmpty()) {
            for (ItemStack stack : items.stream().limit(4).toList())
                tooltip.add(Component.literal(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()).withStyle(ChatFormatting.DARK_GRAY));
        }
        tooltip.add(Component.translatable("qm_quest_ui.keys", QuestIngredientKeys.recipeKey(), QuestIngredientKeys.usesKey()).withStyle(ChatFormatting.DARK_GRAY));
        if (items.stream().anyMatch(QuestIngredientKeys::missingNotice))
            tooltip.add(Component.translatable("qm_quest_ui.no_recipes").withStyle(ChatFormatting.YELLOW));
    }
}
