package qm.questui.mixin;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "dev.ftb.mods.ftbquests.client.PinnedQuestsTracker$RenderData", remap = false)
public interface PinnedRenderDataAccess {
    @Accessor("title") Component qm$title();
    @Accessor("pinnedQuestText") List<FormattedCharSequence> qm$lines();
}
