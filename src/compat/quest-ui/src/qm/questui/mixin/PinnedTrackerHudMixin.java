package qm.questui.mixin;

import dev.ftb.mods.ftbquests.client.PinnedQuestsTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.questui.HudConfig;
import qm.questui.client.HudQuestTracker;

@Mixin(value = PinnedQuestsTracker.class, remap = false)
public abstract class PinnedTrackerHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void qm$bounded(Minecraft mc, GuiGraphics graphics, CallbackInfo ci) {
        if (!HudConfig.enabled()) return;
        HudQuestTracker.render(this, mc, graphics);
        ci.cancel();
    }
}
