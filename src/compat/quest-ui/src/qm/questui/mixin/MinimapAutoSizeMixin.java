package qm.questui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.questui.HudConfig;
import xaero.hud.gui.util.GuiUtils;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.lib.client.config.ClientConfigManager;

/** Keep automatic map dimensions independent of the render-only HUD scale cap. */
@Mixin(value = MinimapConfigClientUtils.class, remap = false)
public abstract class MinimapAutoSizeMixin {
    @Redirect(method = "getAutoMinimapSize()I", at = @At(value = "INVOKE", target =
            "Lxaero/hud/gui/util/GuiUtils;getMinimapScale(Lxaero/lib/client/config/ClientConfigManager;)F"))
    private static float qm$nativeScaleForAutoSize(ClientConfigManager configManager) {
        if (!HudConfig.enabled()) return GuiUtils.getMinimapScale(configManager);

        // Xaero 26.6.0 derives AUTO size from the UI scale. Calling the capped
        // getMinimapScale here asks for getConfiguredWidth again and recurses.
        // Use its native scale calculation so AUTO size stays constant when
        // MinimapScaleMixin subsequently shrinks the rendered map to fit.
        int configuredScale = configManager.getEffective(MinimapProfiledConfigOptions.UI_SCALE);
        var validScales = MinimapProfiledConfigOptions.UI_SCALE.getValidValues();
        return GuiUtils.getUIScale(configuredScale, validScales.get(0),
                validScales.get(validScales.size() - 1));
    }
}
