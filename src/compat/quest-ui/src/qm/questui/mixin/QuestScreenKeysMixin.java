package qm.questui.mixin;

import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.questui.client.QuestIngredientKeys;

@Mixin(value = QuestScreen.class, remap = false)
public abstract class QuestScreenKeysMixin {
    @Inject(method = "keyPressed", at = @At("RETURN"), cancellable = true)
    private void qm$recipeFallback(Key key, CallbackInfoReturnable<Boolean> callback) {
        if (!callback.getReturnValueZ() && QuestIngredientKeys.handle((QuestScreen)(Object)this, key))
            callback.setReturnValue(true);
    }
}
