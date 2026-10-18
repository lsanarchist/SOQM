package qm.questui.mixin;

import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.questui.client.QuestItemTooltip;

@Mixin(value = ItemTask.class, remap = false)
public abstract class ItemTaskTooltipMixin {
    @Inject(method = "addMouseOverHeader", at = @At("TAIL"))
    private void qm$sourceModAndItem(TooltipList tooltip, TeamData team, boolean advanced, CallbackInfo callback) {
        QuestItemTooltip.append((ItemTask)(Object)this, tooltip, advanced);
    }
}
