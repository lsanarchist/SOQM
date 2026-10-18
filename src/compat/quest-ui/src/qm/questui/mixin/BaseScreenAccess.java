package qm.questui.mixin;

import java.util.Deque;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BaseScreen.class, remap = false)
public interface BaseScreenAccess {
    @Accessor("focusedWidget") Widget qm$getFocusedWidget();
    @Accessor("modalPanels") Deque<ModalPanel> qm$getModalPanels();
}
