package qm.compat.mixin;

import net.neoforged.neoforge.event.level.ChunkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Avoid an asynchronous second owner for vanilla underwater loot containers.
 * Skipping this load hook leaves its conversion queue and periodic-scan set
 * empty. Crafting, placement, underwater repair and the repair chest's native
 * dry-out behavior are independent and remain available.
 */
@Mixin(targets = "com.cannibalvillages.core.event.ChestTransmutationEvents", remap = false)
public abstract class AutomaticChestConversionMixin {
    @Inject(method = "onChunkLoad", at = @At("HEAD"), cancellable = true,
            remap = false, require = 1)
    private static void qm$leaveLootrInCharge(ChunkEvent.Load event,
                                             CallbackInfo callback) {
        callback.cancel();
    }
}
