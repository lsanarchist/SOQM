package qm.questui.mixin;

import java.util.BitSet;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ToastComponent.class, remap = false)
public interface ToastSlotsAccess {
    @Accessor("occupiedSlots") BitSet qm$occupiedSlots();
}
