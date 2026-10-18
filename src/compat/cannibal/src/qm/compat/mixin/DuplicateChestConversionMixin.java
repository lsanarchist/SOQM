package qm.compat.mixin;

import net.neoforged.neoforge.event.level.ChunkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The duplicate queue snapshots only LootTable/seed, then clears the chest.
 * It therefore loses ordinary Items and can overwrite a Lootr replacement.
 * Keep every unrelated ModEvents hook; prevent only this queue's population.
 */
@Mixin(targets = "com.cannibalvillages.core.event.ModEvents", remap = false)
public abstract class DuplicateChestConversionMixin {
    @Inject(method = "onChunkLoad", at = @At("HEAD"), cancellable = true,
            remap = false, require = 1)
    private static void qm$keepExistingContainers(ChunkEvent.Load event,
                                                 CallbackInfo callback) {
        callback.cancel();
    }
}
