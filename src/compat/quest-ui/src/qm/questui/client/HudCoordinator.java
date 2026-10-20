package qm.questui.client;

import java.util.ArrayList;
import java.util.List;
import earth.terrarium.adastra.common.items.armor.SpaceSuitItem;
import earth.terrarium.adastra.common.items.armor.JetSuitItem;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.item.gear.ItemMekaTool;
import mekanism.common.tags.MekanismTags;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import xaero.common.HudMod;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import qm.questui.mixin.ToastSlotsAccess;
import mcjty.rftoolsutility.client.RenderGameOverlayEventHandler;
import mcjty.lib.McJtyLib;
import mcjty.lib.gui.BuffStyle;

/** Small render-only integration: never writes the individual mods' config files. */
public final class HudCoordinator {
    private static final HudGeometry.Rect EMPTY = new HudGeometry.Rect(0, 0, 0, 0);
    private static HudGeometry.Rect ultimine = EMPTY;
    private static long ultimineFrame;
    private static HudGeometry.Rect tracker = EMPTY;
    private static long trackerFrame;
    private static int mapInfoHeight = 18;
    private static int lastWidth, lastHeight;
    private static HudGeometry.Rect renderedMap = EMPTY;
    private static int renderedMapTop;
    private static int mekaTextTop, mekaScreenHeight, mekaModuleHeight;
    private HudCoordinator() {}

    public static int width() { return Minecraft.getInstance().getWindow().getGuiScaledWidth(); }
    public static int height() { return Minecraft.getInstance().getWindow().getGuiScaledHeight(); }
    public static MinimapSession session() { return BuiltInHudModules.MINIMAP.getCurrentSession(); }

    public static int mapTop() {
        var player = Minecraft.getInstance().player;
        if (player == null) return HudGeometry.GAP;
        boolean positive = false, negative = false;
        for (var effect : player.getActiveEffects()) {
            if (!effect.showIcon()) continue;
            if (effect.getEffect().value().isBeneficial()) positive = true; else negative = true;
        }
        int toasts = ((ToastSlotsAccess)Minecraft.getInstance().getToasts()).qm$occupiedSlots().length() * 32;
        return HudGeometry.GAP + Math.max(toasts, negative ? 52 : positive ? 26 : 0);
    }

    public static HudGeometry.Rect mapRect() {
        var session = session();
        if (session == null || HudMod.INSTANCE == null
                || !HudMod.INSTANCE.getHudConfigs().getClientConfigManager()
                    .getEffective(MinimapProfiledConfigOptions.DISPLAY_MINIMAP)) return EMPTY;
        if (lastWidth != width() || lastHeight != height()) {
            lastWidth = width(); lastHeight = height(); mapInfoHeight = 18; renderedMap = EMPTY;
        }
        if (renderedMap.width() > 0 && renderedMapTop == mapTop())
            return new HudGeometry.Rect(renderedMap.x(), renderedMap.y(), renderedMap.width(),
                    renderedMap.height() + mapInfoHeight);
        int side = session.getWidth(Minecraft.getInstance().getWindow().getGuiScale());
        return new HudGeometry.Rect(width() - side - HudGeometry.GAP, mapTop(), side,
                side + mapInfoHeight);
    }

    public static void mapInfo(int guiPixels) {
        mapInfoHeight = Math.max(0, guiPixels);
    }
    public static void mapBounds(int x, int y, int width, int height) {
        lastWidth = width(); lastHeight = height(); renderedMapTop = mapTop();
        renderedMap = new HudGeometry.Rect(x, y, width, height);
    }

    public static float suitScale() { return height() < 240 ? 0.65F : 0.75F; }
    public static boolean spaceSuit() {
        var player = Minecraft.getInstance().player;
        return player != null && !player.isSpectator() && SpaceSuitItem.hasFullSet(player);
    }
    public static boolean mekaHud() {
        var player = Minecraft.getInstance().player;
        return player != null && !player.isSpectator() && MekanismConfig.client.enableHUD.get()
                && MekanismConfig.client.hudOpacity.get() >= 0.05F
                && player.getItemBySlot(EquipmentSlot.HEAD).is(MekanismTags.Items.MEKASUIT_HUD_RENDERER);
    }
    public static int energyCount() {
        var player = Minecraft.getInstance().player;
        if (!mekaHud()) return 0;
        int count = 0;
        for (var slot : EquipmentSlot.values()) {
            var item = player.getItemBySlot(slot).getItem();
            if (item instanceof ItemMekaSuitArmor || item instanceof ItemMekaTool) count++;
        }
        return count;
    }
    public static float energyScale() {
        return Math.min(1F, (float)(width() - HudGeometry.rightColumn(width()) - 3 * HudGeometry.GAP)
                / Math.max(1, energyCount() * 48));
    }
    public static int buffColumns() { return Math.max(1, (width() - HudGeometry.rightColumn(width()) - 3 * HudGeometry.GAP) / 16); }
    public static HudGeometry.Rect buffRect() {
        var player = Minecraft.getInstance().player;
        var buffs = RenderGameOverlayEventHandler.buffs;
        if (player == null || buffs == null || buffs.isEmpty()
                || McJtyLib.getPreferencesProperties(player).getBuffStyle() == BuffStyle.OFF) return EMPTY;
        int columns = buffColumns();
        return new HudGeometry.Rect(HudGeometry.GAP, HudGeometry.GAP,
                Math.min(buffs.size(), columns) * 16, ((buffs.size() + columns - 1) / columns) * 16);
    }
    public static int suitTop() {
        int top = HudGeometry.stackTop(List.of(buffRect()));
        if (spaceSuit()) {
            var expected = new HudGeometry.Rect(HudGeometry.GAP, top,
                    (int)Math.ceil(111 * suitScale()) + HudGeometry.GAP, (int)Math.ceil(64 * suitScale()));
            if (expected.intersects(aetherTop())) top = Math.max(top, aetherTop().bottom() + HudGeometry.GAP);
        }
        return top;
    }
    public static int energyTop() { return Math.max(suitTop(), aetherTop().height() == 0 ? HudGeometry.GAP : 56); }

    public static HudGeometry.Rect suitRect() {
        if (!spaceSuit()) return EMPTY;
        boolean jet = JetSuitItem.hasFullSet(Minecraft.getInstance().player);
        return new HudGeometry.Rect(HudGeometry.GAP, suitTop(),
                jet ? (int)Math.ceil(111 * suitScale()) + HudGeometry.GAP : (int)Math.ceil(62 * suitScale()),
                (int)Math.ceil(64 * suitScale()) + 1);
    }
    public static HudGeometry.Rect energyRect() {
        if (!mekaHud()) return EMPTY;
        return new HudGeometry.Rect(HudGeometry.GAP, energyTop(),
                (int)Math.ceil(energyCount() * 48 * energyScale()), (int)Math.ceil(18 * energyScale()));
    }
    public static int miningTop() {
        return HudGeometry.stackTop(List.of(suitRect(), energyRect(), buffRect(), aetherTop()));
    }
    public static HudGeometry.Fit miningFit(int rawWidth, int rawHeight, float preferred) {
        int reserve = 52;
        if (mekaHud()) {
            int textTop = mekaScreenHeight == height() ? mekaTextTop : height() - 40;
            reserve = Math.max(reserve, height() - textTop);
            if (MekanismConfig.client.hudCompassEnabled.get())
                reserve = Math.max(reserve, height() - Math.min(height() - 20, textTop) + 70);
        }
        return HudGeometry.fitLeft(width(), height(), miningTop(), rawWidth + 4, rawHeight + 4, preferred, reserve);
    }
    public static void mekaBounds(int textTop, int moduleHeight) {
        mekaTextTop = textTop; mekaScreenHeight = height(); mekaModuleHeight = moduleHeight;
    }
    public static int rightBottomReserve() {
        int reserve = mekaHud() ? Math.max(64, mekaModuleHeight + 38) : 64;
        var player = Minecraft.getInstance().player;
        if (player != null) for (var stack : player.getHandSlots()) {
            var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (id.getNamespace().equals("immersiveengineering") && switch (id.getPath()) {
                case "revolver", "speedloader", "railgun", "drill", "buzzsaw", "chemthrower", "shield" -> true;
                default -> false;
            }) reserve = Math.max(reserve, 112);
        }
        return reserve;
    }
    public static void miningBounds(int width, int height) {
        ultimine = new HudGeometry.Rect(HudGeometry.GAP - 2, miningTop() - 2, width + 4, height + 4);
        ultimineFrame = System.nanoTime();
    }
    public static void trackerBounds(HudGeometry.Rect bounds) { tracker = bounds; trackerFrame = System.nanoTime(); }

    public static HudGeometry.Rect aetherTop() {
        var player = Minecraft.getInstance().player;
        if (player == null) return EMPTY;
        var result = EMPTY;
        if (player.getVehicle() != null && BuiltInRegistries.ENTITY_TYPE.getKey(player.getVehicle().getType())
                .equals(ResourceLocation.fromNamespaceAndPath("aether", "moa")))
            result = new HudGeometry.Rect(width() / 2 - 64, 16, 128, 16);
        var hammer = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("aether", "hammer_of_kingbdogz"));
        if (player.getCooldowns().isOnCooldown(hammer)) {
            var carried = new ArrayList<>(player.getInventory().items);
            carried.addAll(player.getInventory().offhand);
            for (var item : carried) if (item.is(hammer)) {
                var shown = player.getMainHandItem().is(hammer) ? player.getMainHandItem()
                        : player.getOffhandItem().is(hammer) ? player.getOffhandItem() : item;
                String label = shown.getHoverName().getString() + " "
                        + net.minecraft.network.chat.Component.translatable("aether.hammer_of_kingbdogz_cooldown").getString();
                int width = Math.max(128, Minecraft.getInstance().font.width(label));
                int top = result.height() == 0 ? 30 : 16;
                return new HudGeometry.Rect(width() / 2 - width / 2, top, width, 52 - top);
            }
        }
        return result;
    }

    public static List<HudGeometry.Rect> obstacles() {
        return obstacles(true);
    }
    public static List<HudGeometry.Rect> trackerObstacles() {
        return obstacles(false);
    }
    private static List<HudGeometry.Rect> obstacles(boolean includeTracker) {
        var result = new ArrayList<HudGeometry.Rect>();
        result.add(mapRect()); result.add(suitRect()); result.add(energyRect()); result.add(aetherTop()); result.add(buffRect());
        if (mapTop() > HudGeometry.GAP)
            result.add(new HudGeometry.Rect(Math.max(0, width() - 160), 0, Math.min(160, width()), mapTop()));
        if (mekaHud() && MekanismConfig.client.hudCompassEnabled.get()) {
            int textTop = mekaScreenHeight == height() ? mekaTextTop : height() - 40;
            int x = MekanismConfig.client.reverseHUD.get() ? width() - 140 : 10;
            result.add(new HudGeometry.Rect(x, Math.min(height() - 20, textTop) - 70, 130, 90));
        }
        var boss = snownee.jade.util.ClientProxy.getBossBarRect();
        if (boss != null) result.add(new HudGeometry.Rect(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight()));
        // Ultimine is held, not permanent; stale bounds cannot displace tooltips indefinitely.
        if (System.nanoTime() - ultimineFrame < 150_000_000L) result.add(ultimine);
        if (includeTracker && System.nanoTime() - trackerFrame < 150_000_000L) result.add(tracker);
        return result;
    }
}
