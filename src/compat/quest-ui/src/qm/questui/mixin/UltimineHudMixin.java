package qm.questui.mixin;

import java.util.List;
import dev.ftb.mods.ftbultimine.client.FTBUltimineClient;
import dev.ftb.mods.ftblibrary.util.PanelPositioning;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.questui.HudConfig;
import qm.questui.client.HudCoordinator;
import qm.questui.client.HudGeometry;

@Mixin(value = FTBUltimineClient.class, remap = false)
public abstract class UltimineHudMixin {
    @Shadow private int maxPanelWidth;
    @Shadow private List<?> infoPanelList;

    @ModifyVariable(method = "renderGameOverlay", at = @At("STORE"), ordinal = 0)
    private float qm$fitText(float scale) {
        if (!HudConfig.enabled()) return scale;
        return HudCoordinator.miningFit(maxPanelWidth, infoPanelList.size() * 9, Math.min(scale, 0.85F)).scale();
    }
    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE",
            target = "Ldev/ftb/mods/ftblibrary/util/PanelPositioning;getPanelPos(IIIIII)Ldev/ftb/mods/ftblibrary/util/PanelPositioning$PanelPos;"))
    private PanelPositioning.PanelPos qm$stack(PanelPositioning position, int screenWidth, int screenHeight,
                                         int width, int height, int insetX, int insetY) {
        if (!HudConfig.enabled()) return position.getPanelPos(screenWidth, screenHeight, width, height, insetX, insetY);
        HudCoordinator.miningBounds(width, height);
        return new PanelPositioning.PanelPos(HudGeometry.GAP, HudCoordinator.miningTop());
    }
}
