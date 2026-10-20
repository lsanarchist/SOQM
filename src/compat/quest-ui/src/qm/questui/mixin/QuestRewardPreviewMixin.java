package qm.questui.mixin;

import dev.ftb.mods.ftblibrary.ui.BlankPanel;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel;
import dev.ftb.mods.ftbquests.quest.Quest;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.questui.client.QuestRewardPreview;

@Mixin(value = ViewQuestPanel.class, remap = false)
public abstract class QuestRewardPreviewMixin {
    @Shadow @Final private QuestScreen questScreen;
    @Shadow private Quest quest;
    @Shadow private BlankPanel panelRewards;
    @Shadow private BlankPanel panelText;

    @Inject(method = "addWidgets", at = @At(value = "INVOKE",
            target = "Ldev/ftb/mods/ftblibrary/ui/BlankPanel;align(Ldev/ftb/mods/ftblibrary/ui/WidgetLayout;)I", ordinal = 1))
    private void qm$guaranteedRewardIcons(CallbackInfo callback) {
        QuestRewardPreview.inline(panelRewards, quest, questScreen);
    }

    @Inject(method = "addWidgets", at = @At(value = "INVOKE",
            target = "Ldev/ftb/mods/ftbquests/client/gui/quests/ViewQuestPanel;buildPageIndices()V"))
    private void qm$openRewardCatalogue(CallbackInfo callback) {
        QuestRewardPreview.addOpenButton(panelText, quest, questScreen);
    }
}
