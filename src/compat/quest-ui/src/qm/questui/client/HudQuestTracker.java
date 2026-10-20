package qm.questui.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import dev.ftb.mods.ftbquests.client.PinnedQuestsTracker;
import dev.ftb.mods.ftbquests.client.FTBQuestsClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import qm.questui.mixin.PinnedRenderDataAccess;

/** A viewport over upstream's exact collected lines, preserving all quests/tasks/progress. */
public final class HudQuestTracker {
    private static final Field DATA = dataField();
    private static Object cachedData;
    private static int cachedWidth, firstLine, visibleLines;
    private static List<FormattedCharSequence> lines = List.of();
    private static long lastRender;
    private HudQuestTracker() {}

    private static Field dataField() {
        try {
            // The field type is an upstream private record, inaccessible to Java
            // source. Only this one getter is reflective; record members use a
            // typed Mixin accessor. The pinned bytecode gate checks both sides.
            Field field = PinnedQuestsTracker.class.getDeclaredField("renderData");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException ex) { throw new ExceptionInInitializerError(ex); }
    }

    public static void scroll(int direction) {
        if (System.nanoTime() - lastRender > 250_000_000L) return;
        firstLine = Math.clamp(firstLine + direction * Math.max(1, visibleLines - 1),
                0, Math.max(0, lines.size() - visibleLines));
    }

    public static void render(Object tracker, Minecraft mc, GuiGraphics graphics) {
        Object value;
        try { value = DATA.get(tracker); }
        catch (IllegalAccessException ex) { throw new IllegalStateException("Pinned HUD accessor", ex); }
        if (!(value instanceof PinnedRenderDataAccess data) || data.qm$lines().isEmpty()) return;
        lastRender = System.nanoTime();
        var bounds = HudGeometry.tracker(graphics.guiWidth(), graphics.guiHeight(), HudCoordinator.mapRect(),
                HudCoordinator.rightBottomReserve());
        float scale = Math.clamp(FTBQuestsClientConfig.PINNED_QUESTS_SCALE.get().floatValue(), 0.5F, 0.85F);
        int width = Math.max(24, (int)(bounds.width() / scale));
        int height = (int)(bounds.height() / scale);
        if (height < 36) {
            // A huge map/effect stack on a tiny viewport leaves no room for even
            // one task. Keep a visible heading; the complete book remains native.
            var compactBounds = HudGeometry.compactTracker(graphics.guiWidth(), graphics.guiHeight(),
                    bounds, HudCoordinator.mapRect(), HudCoordinator.rightBottomReserve(),
                    HudCoordinator.trackerObstacles());
            int x = compactBounds.x(), y = compactBounds.y(), compactWidth = compactBounds.width();
            HudCoordinator.trackerBounds(compactBounds);
            graphics.fill(x, y, x + compactWidth, y + 11, 0xAA101820);
            String compact = mc.font.getSplitter().headByWidth(Component.translatable("qm_quest_ui.tracker.compact"),
                    compactWidth - 6, net.minecraft.network.chat.Style.EMPTY).getString();
            graphics.drawString(mc.font, compact, x + 3, y + 1, 0xFFFFFF55);
            visibleLines = 0;
            return;
        }
        if (cachedData != value || cachedWidth != width) {
            lines = wrap(mc, data.qm$lines(), width - 10);
            cachedData = value; cachedWidth = width;
        }
        visibleLines = Math.max(1, (height - 27) / 9);
        firstLine = Math.clamp(firstLine, 0, Math.max(0, lines.size() - visibleLines));
        int count = Math.min(visibleLines, lines.size() - firstLine);
        int drawnHeight = 27 + count * 9;
        HudCoordinator.trackerBounds(new HudGeometry.Rect(bounds.x(), bounds.y(), bounds.width(), (int)Math.ceil(drawnHeight * scale)));
        graphics.enableScissor(bounds.x(), bounds.y(), bounds.right(), bounds.bottom());
        graphics.pose().pushPose();
        graphics.pose().translate(bounds.x(), bounds.y(), 100);
        graphics.pose().scale(scale, scale, 1);
        graphics.fill(0, 0, width, drawnHeight, 0xAA101820);
        graphics.fill(0, 0, width, 14, 0xBB29394B);
        var title = mc.font.getSplitter().headByWidth(data.qm$title(), width - 8, net.minecraft.network.chat.Style.EMPTY);
        graphics.drawString(mc.font, title.getString(), 4, 3, 0xFFFFFF55);
        for (int i = 0; i < count; i++) graphics.drawString(mc.font, lines.get(firstLine + i), 4, 17 + i * 9, 0xFFFFFFFF);
        String progress = (firstLine + 1) + "–" + (firstLine + count) + " / " + lines.size();
        if (lines.size() > visibleLines) progress += "  ↑↓";
        graphics.drawString(mc.font, progress, 4, 18 + count * 9, 0xFFB0C4D9);
        graphics.pose().popPose();
        graphics.disableScissor();
    }

    static List<FormattedCharSequence> wrap(Minecraft mc, List<FormattedCharSequence> source, int width) {
        var result = new ArrayList<FormattedCharSequence>();
        for (var sequence : source) {
            if (sequence == FormattedCharSequence.EMPTY) { result.add(sequence); continue; }
            var glyphs = new ArrayList<FormattedCharSequence>();
            float[] used = {0};
            sequence.accept((index, style, codePoint) -> {
                var glyph = FormattedCharSequence.codepoint(codePoint, style);
                float advance = mc.font.getSplitter().stringWidth(glyph);
                if (!glyphs.isEmpty() && used[0] + advance > width) {
                    result.add(FormattedCharSequence.composite(List.copyOf(glyphs)));
                    glyphs.clear(); used[0] = 0;
                }
                glyphs.add(glyph); used[0] += advance;
                return true;
            });
            if (!glyphs.isEmpty()) result.add(FormattedCharSequence.composite(List.copyOf(glyphs)));
        }
        return List.copyOf(result);
    }
}
