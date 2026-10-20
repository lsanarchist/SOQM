package qm.questui.client;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.BlankPanel;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetLayout;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftblibrary.util.client.PositionedIngredient;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.quest.Quest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Bounded, scrollable in-book catalogue. Every row is a preview, never a claim. */
public final class RewardCataloguePanel extends ModalPanel {
    private final QuestScreen screen;
    private final Quest quest;
    private final RewardCatalogueData catalogue;

    public RewardCataloguePanel(QuestScreen screen, Quest quest) {
        super(screen);
        this.screen = screen; this.quest = quest; this.catalogue = RewardCatalogue.data();
        // FTB adds each modal's extra Z to the previous modal, while tooltip
        // placement uses only their maximum. Use the native relative increment
        // above ViewQuestPanel so tooltips remain in front of this catalogue.
        setExtraZlevel(1);
        setOnlyRenderWidgetsInside(true);
        setOnlyInteractWithWidgetsInside(true);
    }

    @Override public void addWidgets() {
        int w = Math.min(430, Math.max(120, screen.getWidth() - 24));
        int h = Math.min(340, Math.max(100, screen.getHeight() - 24));
        setPosAndSize((screen.getWidth() - w) / 2, (screen.getHeight() - h) / 2, w, h);
        TextField title = new TextField(this).setMaxWidth(w - 16).setMinWidth(w - 16).addFlags(4)
                .setText(Component.translatable("qm_quest_ui.rewards.title"));
        title.setPos(8, 7); add(title);
        TextField name = new TextField(this).setMaxWidth(w - 16).setMinWidth(w - 16).setTrim().showTooltipForLongText()
                .setText(quest.getTitle().copy().withStyle(ChatFormatting.GRAY));
        name.setPosAndSize(8, 21, w - 16, 12); add(name);
        BlankPanel body = new BlankPanel(this, "SOQMRewardCatalogue");
        body.setPosAndSize(8, 39, w - 28, h - 72);
        body.setOnlyRenderWidgetsInside(true);
        body.setOnlyInteractWithWidgetsInside(true);
        add(body);
        label(body, Component.translatable("qm_quest_ui.rewards.personal").withStyle(ChatFormatting.GRAY));
        for (long id : catalogue.quests().getOrDefault(quest.getId(), List.of())) {
            RewardCatalogueData.Claim claim = catalogue.rewards().get(id);
            if (!claim.guaranteed().isEmpty()) {
                label(body, Component.translatable("qm_quest_ui.rewards.guaranteed").withStyle(ChatFormatting.GREEN));
                for (var prize : claim.guaranteed()) {
                    List<Component> details = new ArrayList<>();
                    details.add(Component.translatable(prize.kind().equals("refund") ? "qm_quest_ui.rewards.refund" : "qm_quest_ui.rewards.guaranteed"));
                    if (claim.manufacture() != null) details.add(Component.translatable("qm_quest_ui.rewards.manufacture", QuestRewardPreview.itemName(claim.manufacture())));
                    body.add(new ItemRow(body, prize.item(), prize.count(), ChatFormatting.GREEN, details));
                }
            }
            if (claim.randomPool() != null) {
                var pool = catalogue.pools().get(claim.randomPool());
                String titleText = pool.titles().getOrDefault(RewardCatalogue.language(), pool.titles().getOrDefault("en_us", pool.key()));
                label(body, Component.translatable("qm_quest_ui.rewards.possible", titleText).withStyle(ChatFormatting.AQUA));
                label(body, Component.translatable("qm_quest_ui.rewards.draws",
                        catalogue.draws().stream().min(Integer::compareTo).orElse(3),
                        catalogue.draws().stream().max(Integer::compareTo).orElse(5)).withStyle(ChatFormatting.GRAY));
                if (catalogue.qualityDraws() > 0) label(body, Component.translatable("qm_quest_ui.rewards.quality").withStyle(ChatFormatting.GRAY));
                label(body, Component.translatable("qm_quest_ui.rewards.conditions_note").withStyle(ChatFormatting.GRAY));
                for (var entry : pool.entries()) {
                    List<Component> details = new ArrayList<>();
                    details.add(Component.translatable("qm_quest_ui.rewards.rarity." + entry.rarity()));
                    if (entry.level() > 0) details.add(Component.translatable("qm_quest_ui.rewards.level",
                            entry.level(), QuestRewardPreview.itemName(catalogue.levels().get(entry.level()))));
                    for (String crafted : entry.requiresCraft()) details.add(Component.translatable("qm_quest_ui.rewards.manufacture", QuestRewardPreview.itemName(crafted)));
                    if (entry.level() == 0 && entry.requiresCraft().isEmpty()) details.add(Component.translatable("qm_quest_ui.rewards.no_condition"));
                    body.add(new ItemRow(body, entry.item(), entry.count(), rarityColor(entry.rarity()), details));
                }
            }
        }
        body.align(new WidgetLayout.Vertical(0, 3, 2));
        PanelScrollBar scrollbar = new PanelScrollBar(this, body);
        scrollbar.setPosAndSize(w - 16, 39, 8, h - 72);
        scrollbar.setScrollStep(32);
        add(scrollbar);
        SimpleTextButton back = SimpleTextButton.create(this, Component.translatable("qm_quest_ui.rewards.back"),
                Icons.CANCEL, button -> screen.closeModalPanel(this));
        back.setWidth(Math.min(back.getWidth(), w - 16));
        back.setPos((w - back.getWidth()) / 2, h - 26); add(back);
    }

    private static ChatFormatting rarityColor(String rarity) {
        return switch (rarity) {
            case "epic" -> ChatFormatting.LIGHT_PURPLE;
            case "rare" -> ChatFormatting.AQUA;
            case "uncommon" -> ChatFormatting.YELLOW;
            default -> ChatFormatting.WHITE;
        };
    }
    private static void label(Panel panel, Component text) {
        panel.add(new TextField(panel).setMinWidth(panel.getWidth()).setMaxWidth(panel.getWidth()).setText(text));
    }
    @Override public void alignWidgets() {}
    @Override public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        theme.drawGui(graphics, x, y, w, h, WidgetType.NORMAL);
    }

    private static final class ItemRow extends Widget {
        private final ItemStack stack;
        private final String id;
        private final int count;
        private final ChatFormatting color;
        private final List<Component> details;

        ItemRow(Panel panel, String id, int count, ChatFormatting color, List<Component> details) {
            super(panel);
            this.id = id; this.count = count; this.color = color; this.details = List.copyOf(details);
            this.stack = RewardCatalogue.stack(id, count);
            setSize(panel.getWidth(), 32);
        }
        @Override public Optional<PositionedIngredient> getIngredientUnderMouse() {
            return PositionedIngredient.of(stack, this);
        }
        @Override public void addMouseOverText(TooltipList tooltip) {
            tooltip.add(QuestRewardPreview.stackTitle(id, count));
            if (!stack.isEmpty()) tooltip.add(Component.literal(QuestItemTooltip.modName(stack)).withStyle(ChatFormatting.BLUE));
            details.forEach(tooltip::add);
            tooltip.add(Component.translatable("qm_quest_ui.keys", QuestIngredientKeys.recipeKey(), QuestIngredientKeys.usesKey()).withStyle(ChatFormatting.DARK_GRAY));
        }
        @Override public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            if (isMouseOver()) graphics.fill(x, y, x + w, y + h, 0x2035B6C8);
            if (!stack.isEmpty()) {
                graphics.renderItem(stack, x + 2, y + 7);
                graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x + 2, y + 7, Integer.toString(count));
            }
            Component title = QuestRewardPreview.itemName(id).copy().withStyle(color);
            theme.drawString(graphics, theme.trimStringToWidth(title, Math.max(1, w - 26)), x + 24, y + 4, 0);
            Component condition = Component.empty();
            for (int i = 0; i < details.size(); i++) {
                if (i > 0) condition = condition.copy().append(" · ");
                condition = condition.copy().append(details.get(i));
            }
            theme.drawString(graphics, theme.trimStringToWidth(condition.copy().withStyle(ChatFormatting.GRAY), Math.max(1, w - 26)), x + 24, y + 18, 0);
        }
    }
}
