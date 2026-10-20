package qm.questui.mixin;

import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.client.gui.quests.RewardButton;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.questui.client.QuestRewardPreview;

@Mixin(value = RewardButton.class, remap = false)
public abstract class RewardButtonTooltipMixin {
    @Shadow @Final private Reward reward;
    @Inject(method = "addMouseOverText", at = @At("TAIL"))
    private void qm$rewardContents(TooltipList tooltip, CallbackInfo callback) {
        QuestRewardPreview.tooltip(reward, tooltip);
    }
}
