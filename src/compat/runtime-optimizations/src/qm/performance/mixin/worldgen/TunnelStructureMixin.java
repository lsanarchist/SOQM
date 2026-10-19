package qm.performance.mixin.worldgen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.Optional;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.performance.worldgen.TunnelHeightCache;

/** Keeps the original search, scoring, random draws and piece creation intact. */
@Mixin(targets = "com.manyfunadventures.abandonedtunnels.TunnelStructure", remap = false)
public abstract class TunnelStructureMixin {
    @WrapMethod(method = "findGenerationPoint", remap = false, require = 1, allow = 1)
    private Optional<Structure.GenerationStub> qm$reuseTerrainSamples(
            Structure.GenerationContext context, Operation<Optional<Structure.GenerationStub>> original) {
        return TunnelHeightCache.during(context, () -> original.call(context));
    }

    // The pinned 1.0.0 class has exactly one call in surface and one in dry.
    // Separate selectors enforce both locations, rather than a combined total.
    @Redirect(method = "surface", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/chunk/ChunkGenerator;getFirstFreeHeight(IILnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;)I"),
            remap = false, require = 1, allow = 1)
    private static int qm$surfaceHeight(ChunkGenerator generator, int x, int z,
                                        Heightmap.Types type, LevelHeightAccessor heights,
                                        RandomState randomState) {
        return TunnelHeightCache.firstFreeHeight(generator, x, z, type, heights, randomState);
    }

    @Redirect(method = "dry", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/chunk/ChunkGenerator;getFirstFreeHeight(IILnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;)I"),
            remap = false, require = 1, allow = 1)
    private static int qm$oceanFloorHeight(ChunkGenerator generator, int x, int z,
                                           Heightmap.Types type, LevelHeightAccessor heights,
                                           RandomState randomState) {
        return TunnelHeightCache.firstFreeHeight(generator, x, z, type, heights, randomState);
    }
}
