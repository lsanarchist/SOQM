package qm.questui.mixin;

import earth.terrarium.adastra.client.screens.player.OverlayScreen;
import earth.terrarium.adastra.client.config.AdAstraConfigClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.questui.HudConfig;
import qm.questui.client.HudCoordinator;
import qm.questui.client.HudGeometry;

@Mixin(value = OverlayScreen.class, remap = false)
public abstract class AdAstraHudMixin {
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Learth/terrarium/adastra/client/config/AdAstraConfigClient;oxygenBarX:I"))
    private static int qm$oxygenX() { return HudConfig.enabled() ? (int)Math.ceil(HudGeometry.GAP / HudCoordinator.suitScale()) : AdAstraConfigClient.oxygenBarX; }
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Learth/terrarium/adastra/client/config/AdAstraConfigClient;oxygenBarY:I"))
    private static int qm$oxygenY() { return HudConfig.enabled() ? (int)Math.ceil(HudCoordinator.suitTop() / HudCoordinator.suitScale()) : AdAstraConfigClient.oxygenBarY; }
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Learth/terrarium/adastra/client/config/AdAstraConfigClient;energyBarX:I"))
    private static int qm$energyX() { return HudConfig.enabled() ? 62 + (int)Math.ceil(2 * HudGeometry.GAP / HudCoordinator.suitScale()) : AdAstraConfigClient.energyBarX; }
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Learth/terrarium/adastra/client/config/AdAstraConfigClient;energyBarY:I"))
    private static int qm$energyY() { return HudConfig.enabled() ? (int)Math.ceil(HudCoordinator.suitTop() / HudCoordinator.suitScale()) : AdAstraConfigClient.energyBarY; }
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Learth/terrarium/adastra/client/config/AdAstraConfigClient;oxygenBarScale:F"))
    private static float qm$oxygenScale() { return HudConfig.enabled() ? HudCoordinator.suitScale() : AdAstraConfigClient.oxygenBarScale; }
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Learth/terrarium/adastra/client/config/AdAstraConfigClient;energyBarScale:F"))
    private static float qm$energyScale() { return HudConfig.enabled() ? HudCoordinator.suitScale() : AdAstraConfigClient.energyBarScale; }
}
