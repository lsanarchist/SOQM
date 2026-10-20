package qm.questui.mixin;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.render.InfoDisplayRenderer;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;
import xaero.hud.minimap.module.MinimapSession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.questui.HudConfig;
import qm.questui.client.HudCoordinator;

@Mixin(value = InfoDisplayRenderer.class, remap = false)
public abstract class MinimapInfoMixin {
    @Unique private int qm$lines;
    @Inject(method = "render", at = @At("HEAD"))
    private void qm$start(CallbackInfo ci) { qm$lines = 0; }
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lxaero/hud/minimap/info/render/compile/InfoDisplayCompiler;compile(Lxaero/hud/minimap/info/InfoDisplay;Lxaero/hud/minimap/module/MinimapSession;ILnet/minecraft/core/BlockPos;)Ljava/util/List;"))
    private List<Component> qm$count(InfoDisplayCompiler compiler, InfoDisplay<?> display, MinimapSession session, int size, BlockPos pos) {
        var lines = compiler.compile(display, session, size, pos);
        qm$lines += lines.size();
        return lines;
    }
    @Inject(method = "render", at = @At("TAIL"))
    private void qm$height(GuiGraphics graphics, MinimapSession session, Minimap minimap, int height, int size,
                          BlockPos pos, int x, int y, float mapScale, MultiBufferSource.BufferSource buffer, CallbackInfo ci) {
        if (HudConfig.enabled()) HudCoordinator.mapInfo((int)Math.ceil(qm$lines * 10 / mapScale) + 2);
    }
}
