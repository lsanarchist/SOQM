package qm.questui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.questui.HudConfig;
import qm.questui.client.HudCoordinator;
import qm.questui.client.HudGeometry;
import net.minecraft.client.Minecraft;
import xaero.hud.gui.util.GuiUtils;

@Mixin(value = GuiUtils.class, remap = false)
public abstract class MinimapScaleMixin {
    @Inject(method = "getMinimapScale", at = @At("RETURN"), cancellable = true)
    private static void qm$fitMap(CallbackInfoReturnable<Float> ci) {
        var session = HudCoordinator.session();
        if (!HudConfig.enabled() || session == null || session.getProcessor().isEnlargedMap()) return;
        double gui = Minecraft.getInstance().getWindow().getGuiScale();
        // MinimapAutoSizeMixin resolves AUTO dimensions with Xaero's native
        // scale, so this read cannot recurse into the render-scale cap.
        float maximum = (float)(HudGeometry.mapSide(HudCoordinator.width(), HudCoordinator.height())
                * gui / session.getConfiguredWidth());
        ci.setReturnValue(Math.min(ci.getReturnValueF(), maximum));
    }
}
