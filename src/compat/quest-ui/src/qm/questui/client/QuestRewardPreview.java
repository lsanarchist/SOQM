package qm.questui.client;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.BlankPanel;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftblibrary.util.client.PositionedIngredient;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import qm.questui.mixin.BaseScreenAccess;

/** Adds display-only widgets; the original native reward button owns the claim. */
public final class QuestRewardPreview {
    private QuestRewardPreview() {}

    public static void inline(BlankPanel panel, Quest quest, QuestScreen screen) {
        if (panel == null || quest == null) return;
        for (Reward reward : quest.getRewards()) {
            if (!reward.hasTag("soqm:progressive_reward")) continue;
            var view = RewardCatalogue.data().rewards().get(reward.getId());
            if (view == null) continue;
            for (var prize : view.guaranteed()) {
                // A bundled milestone's original native claim button already
                // shows its cube. Add each separate refund stack exactly once.
                if (prize.kind().equals("cube")) continue;
                panel.add(new GuaranteedItem(panel, quest, screen, prize, view.manufacture()));
            }
        }
    }

    public static void addOpenButton(BlankPanel panel, Quest quest, QuestScreen screen) {
        if (panel == null || quest == null) return;
        if (!RewardCatalogue.data().quests().containsKey(quest.getId())) return;
        SimpleTextButton button = SimpleTextButton.create(panel, Component.translatable("qm_quest_ui.rewards.open"),
                Icons.BOOK, mouse -> open(screen, quest), Component.translatable("qm_quest_ui.rewards.shortcut"));
        button.setWidth(Math.min(button.getWidth(), panel.getWidth()));
        button.setHeight(20);
        button.setX((panel.getWidth() - button.getWidth()) / 2);
        panel.add(button);
    }

    public static void open(QuestScreen screen, Quest quest) {
        if (quest != null && RewardCatalogue.data().quests().containsKey(quest.getId()))
            screen.pushModalPanel(new RewardCataloguePanel(screen, quest));
    }

    public static boolean handle(QuestScreen screen, Key key) {
        if (!key.is(86) || !key.modifiers.control() || !key.modifiers.shift() || key.modifiers.alt()) return false;
        BaseScreenAccess access = (BaseScreenAccess) screen;
        if (access.qm$getFocusedWidget() instanceof TextBox text && text.isFocused()) return false;
        ModalPanel top = access.qm$getModalPanels().peekFirst();
        // Repeated shortcut events must not fall through to the editor's
        // Ctrl+V paste binding while this catalogue already covers the quest.
        if (top instanceof RewardCataloguePanel) return true;
        if (top != screen.viewQuestPanel) return false;
        Quest quest = screen.viewQuestPanel.getViewedQuest();
        if (quest == null || !RewardCatalogue.data().quests().containsKey(quest.getId())) return false;
        open(screen, quest);
        return true;
    }

    public static void tooltip(Reward reward, TooltipList tooltip) {
        if (!reward.hasTag("soqm:progressive_reward")) return;
        var view = RewardCatalogue.data().rewards().get(reward.getId());
        if (view == null) return;
        if (!view.guaranteed().isEmpty()) {
            tooltip.blankLine();
            tooltip.add(Component.translatable("qm_quest_ui.rewards.guaranteed").withStyle(ChatFormatting.GREEN));
            for (var prize : view.guaranteed()) tooltip.add(stackTitle(prize.item(), prize.count()));
        }
        if (view.randomPool() != null) {
            var pool = RewardCatalogue.data().pools().get(view.randomPool());
            tooltip.add(Component.translatable("qm_quest_ui.rewards.possible_count", pool.entries().size())
                    .withStyle(ChatFormatting.AQUA));
        }
        if (view.manufacture() != null) tooltip.add(Component.translatable("qm_quest_ui.rewards.manufacture",
                itemName(view.manufacture())).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("qm_quest_ui.rewards.shortcut").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static Component itemName(String id) {
        ItemStack stack = RewardCatalogue.stack(id, 1);
        return stack.isEmpty() ? Component.literal(id) : stack.getHoverName();
    }
    public static Component stackTitle(String id, int count) {
        return Component.literal(count + " × ").append(itemName(id));
    }

    private static final class GuaranteedItem extends Button {
        private final Quest quest;
        private final QuestScreen screen;
        private final RewardCatalogueData.Prize prize;
        private final String manufacture;
        private final ItemStack stack;

        GuaranteedItem(Panel parent, Quest quest, QuestScreen screen, RewardCatalogueData.Prize prize, String manufacture) {
            super(parent, stackTitle(prize.item(), prize.count()), ItemIcon.getItemIcon(RewardCatalogue.stack(prize.item(), prize.count())));
            this.quest = quest; this.screen = screen; this.prize = prize; this.manufacture = manufacture;
            this.stack = RewardCatalogue.stack(prize.item(), prize.count());
            setSize(18, 18);
        }
        @Override public void onClicked(MouseButton button) { open(screen, quest); }
        @Override public Optional<PositionedIngredient> getIngredientUnderMouse() {
            return PositionedIngredient.of(stack, this);
        }
        @Override public void addMouseOverText(TooltipList tooltip) {
            tooltip.add(stackTitle(prize.item(), prize.count()));
            tooltip.add(Component.translatable("qm_quest_ui.rewards.refund").withStyle(ChatFormatting.GREEN));
            if (manufacture != null) tooltip.add(Component.translatable("qm_quest_ui.rewards.manufacture", itemName(manufacture)));
            tooltip.add(Component.translatable("qm_quest_ui.rewards.preview_only").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("qm_quest_ui.rewards.open"));
        }
        @Override public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            drawBackground(graphics, theme, x, y, w, h);
            if (stack.isEmpty()) return;
            int ix = x + (w - 16) / 2, iy = y + (h - 16) / 2;
            graphics.renderItem(stack, ix, iy);
            graphics.renderItemDecorations(Minecraft.getInstance().font, stack, ix, iy, Integer.toString(prize.count()));
        }
    }
}
