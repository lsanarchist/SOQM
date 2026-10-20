package qm.questui.mixin;

import mcjty.rftoolsutility.client.RenderGameOverlayEventHandler;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.questui.HudConfig;
import qm.questui.client.HudCoordinator;
import qm.questui.client.HudGeometry;

@Mixin(value = RenderGameOverlayEventHandler.class, remap = false)
public abstract class RFToolsBuffHudMixin {
    @Unique private static int qm$buffIndex;
    @Inject(method = "renderBuffs", at = @At("HEAD"))
    private static void qm$start(CallbackInfo ci) { qm$buffIndex = 0; }
    @Redirect(method = "renderBuffs", at = @At(value = "INVOKE", target = "Lmcjty/lib/client/RenderHelper;renderItemStack(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/entity/ItemRenderer;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;Z)Z"))
    private static boolean qm$wrap(GuiGraphics graphics, ItemRenderer renderer, ItemStack stack, int x, int y, String text, boolean effects) {
        if (HudConfig.enabled()) {
            int columns = HudCoordinator.buffColumns();
            x = HudGeometry.GAP + (qm$buffIndex % columns) * 16;
            y = HudGeometry.GAP + (qm$buffIndex / columns) * 16;
            qm$buffIndex++;
        }
        return RenderHelper.renderItemStack(graphics, renderer, stack, x, y, text, effects);
    }
}
