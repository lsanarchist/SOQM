package qm.questui.mixin;

import java.util.List;
import mekanism.client.render.HUDRenderer;
import mekanism.client.render.hud.MekanismHUD;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.questui.HudConfig;
import qm.questui.client.HudCoordinator;
import qm.questui.client.HudGeometry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.EquipmentSlot;
import mekanism.api.gear.IModuleHelper;

@Mixin(value = HUDRenderer.class, remap = false)
public abstract class MekanismEnergyHudMixin {
    @Inject(method = "renderHUD", at = @At("HEAD"))
    private void qm$reserve(Minecraft mc, GuiGraphics graphics, Font font, List<MekanismHUD.DelayedString> delayed,
                           DeltaTracker delta, int width, int height, int textTop, boolean reverse, CallbackInfo ci) {
        if (!HudConfig.enabled()) return;
        int count = 0;
        for (var slot : EquipmentSlot.values()) {
            var stack = mc.player.getItemBySlot(slot);
            var container = IModuleHelper.INSTANCE.getModuleContainer(stack);
            if (container != null) count += container.getHUDElements(mc.player, stack).size();
        }
        HudCoordinator.mekaBounds(textTop, count * 18);
    }
    @Inject(method = "renderMekaSuitEnergyIcons", at = @At("HEAD"))
    private void qm$fit(Player player, Font font, GuiGraphics graphics, List<MekanismHUD.DelayedString> delayed, CallbackInfo ci) {
        if (!HudConfig.enabled()) return;
        float scale = HudCoordinator.energyScale();
        float jitterX = graphics.pose().last().pose().m30();
        float jitterY = graphics.pose().last().pose().m31();
        graphics.pose().pushPose();
        // The native parent pose sways with head rotation. A screen-edge energy
        // row must keep its measured coordinates even during a fast camera turn.
        graphics.pose().translate(HudGeometry.GAP - 10 * scale - jitterX, HudCoordinator.energyTop() - 10 * scale - jitterY, 0);
        graphics.pose().scale(scale, scale, 1);
    }
    @Inject(method = "renderMekaSuitEnergyIcons", at = @At("RETURN"))
    private void qm$restore(Player player, Font font, GuiGraphics graphics, List<MekanismHUD.DelayedString> delayed, CallbackInfo ci) {
        if (HudConfig.enabled()) graphics.pose().popPose();
    }
    @Inject(method = "renderMekaSuitModuleIcons", at = @At("HEAD"))
    private void qm$arcanaGap(Player player, Font font, GuiGraphics graphics, List<MekanismHUD.DelayedString> delayed,
                             int width, int height, boolean reverse, int subtitleWidth, CallbackInfo ci) {
        if (!HudConfig.enabled()) return;
        graphics.pose().pushPose();
        // DivineRPG's native arcana bar occupies the last 18 pixels at right.
        // Keep Mekanism's subtitle-aware horizontal placement intact.
        if (!reverse) graphics.pose().translate(0, -22, 0);
    }
    @Inject(method = "renderMekaSuitModuleIcons", at = @At("RETURN"))
    private void qm$restoreModules(Player player, Font font, GuiGraphics graphics, List<MekanismHUD.DelayedString> delayed,
                                   int width, int height, boolean reverse, int subtitleWidth, CallbackInfo ci) {
        if (HudConfig.enabled()) graphics.pose().popPose();
    }
}
