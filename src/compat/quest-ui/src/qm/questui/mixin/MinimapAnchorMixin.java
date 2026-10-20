package qm.questui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.questui.HudConfig;
import qm.questui.client.HudCoordinator;
import qm.questui.client.HudGeometry;
import xaero.hud.module.ModuleSession;
import xaero.hud.minimap.module.MinimapSession;

@Mixin(value = ModuleSession.class, remap = false)
public abstract class MinimapAnchorMixin {
    @Inject(method = "getEffectiveX", at = @At("HEAD"), cancellable = true)
    private void qm$right(int width, double scale, CallbackInfoReturnable<Integer> ci) {
        if (HudConfig.enabled() && (Object)this instanceof MinimapSession session
                && !session.getProcessor().isEnlargedMap())
            ci.setReturnValue(Math.max(0, width - session.getWidth(scale) - HudGeometry.GAP));
    }
    @Inject(method = "getEffectiveY", at = @At("HEAD"), cancellable = true)
    private void qm$top(int height, double scale, CallbackInfoReturnable<Integer> ci) {
        if (HudConfig.enabled() && (Object)this instanceof MinimapSession session
                && !session.getProcessor().isEnlargedMap()) ci.setReturnValue(HudCoordinator.mapTop());
    }
}
