package qm.questui.mixin;

import net.minecraft.client.gui.GuiGraphics;
import xaero.hud.minimap.module.MinimapRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.render.module.ModuleRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.questui.HudConfig;
import qm.questui.client.HudCoordinator;

@Mixin(value = MinimapRenderer.class, remap = false)
public abstract class MinimapBoundsMixin {
    @Inject(method = "render(Lxaero/hud/minimap/module/MinimapSession;Lxaero/hud/render/module/ModuleRenderContext;Lnet/minecraft/client/gui/GuiGraphics;F)V", at = @At("HEAD"))
    private void qm$capture(MinimapSession session, ModuleRenderContext context, GuiGraphics graphics, float partial, CallbackInfo ci) {
        if (HudConfig.enabled()) HudCoordinator.mapBounds(context.x, context.y, context.w, context.h);
    }
}
