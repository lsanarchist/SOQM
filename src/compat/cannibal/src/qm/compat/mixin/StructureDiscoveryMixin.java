package qm.compat.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Discovery notifications must not wait for nearby chunks to finish loading.
 * getChunk(FULL, false) can still block on an existing chunk holder. The native
 * scan already skips null results, so an unfinished chunk can be checked again
 * on a later scan without changing discovery flags, titles, or sounds.
 */
@Mixin(targets = "com.cannibalvillages.common.event.StructureDiscoveryEvents", remap = false)
public abstract class StructureDiscoveryMixin {
    @Redirect(method = "verificarVila",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/server/level/ServerLevel;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;"),
            remap = false, require = 1, allow = 1)
    private static ChunkAccess qm$getAvailableChunk(ServerLevel level, int chunkX,
                                                   int chunkZ, ChunkStatus status,
                                                   boolean create) {
        return level.getChunkSource().getChunkNow(chunkX, chunkZ);
    }
}
