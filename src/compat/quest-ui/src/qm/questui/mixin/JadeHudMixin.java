package qm.questui.mixin;

import snownee.jade.impl.ui.BoxElement;
import snownee.jade.api.ui.TooltipRect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.questui.HudConfig;
import qm.questui.client.HudCoordinator;
import qm.questui.client.HudGeometry;

@Mixin(value = BoxElement.class, remap = false)
public abstract class JadeHudMixin {
    @Inject(method = "updateExpectedRect", at = @At("TAIL"))
    private void qm$avoidPanels(TooltipRect tooltip, CallbackInfo ci) {
        if (!HudConfig.enabled() || net.minecraft.client.Minecraft.getInstance().screen != null) return;
        var rect = tooltip.expectedRect;
        var original = new HudGeometry.Rect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        var fitted = HudGeometry.fitTooltip(HudCoordinator.width(), HudCoordinator.height(), original);
        tooltip.scale *= fitted.scale();
        rect.setWidth(fitted.rect().width()); rect.setHeight(fitted.rect().height());
        var placed = HudGeometry.placeTooltip(HudCoordinator.width(), HudCoordinator.height(), fitted.rect(), HudCoordinator.obstacles());
        rect.setX(placed.x()); rect.setY(placed.y());
        if (fitted.scale() < 1 || placed.x() != original.x() || placed.y() != original.y()) {
            tooltip.rect.setX(placed.x()); tooltip.rect.setY(placed.y());
            tooltip.rect.setWidth(placed.width()); tooltip.rect.setHeight(placed.height());
        }
        // Preserve all tooltip content and aspect ratio. Native Jade only fits
        // height; extra fitting is needed for very wide translated item names.
    }
}
