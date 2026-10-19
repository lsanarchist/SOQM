package qm.performance.worldgen;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.function.Supplier;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;

/** Reuses deterministic terrain samples only during one tunnel placement search. */
public final class TunnelHeightCache {
    // Abandoned Tunnels 1.0.0 uses fewer than this many samples per search. The
    // ceiling also bounds memory if another compatibility mod adds more queries.
    private static final int MAX_ENTRIES = 2048;
    private static final ThreadLocal<Frame> CURRENT = new ThreadLocal<>();

    private TunnelHeightCache() {}

    public static <T> T during(Structure.GenerationContext context, Supplier<T> search) {
        Frame previous = CURRENT.get();
        CURRENT.set(new Frame(context.chunkGenerator(), context.heightAccessor(), context.randomState()));
        try {
            return search.get();
        } finally {
            // A generation worker is reused for other chunks/worlds. Never
            // retain its terrain or cached results after success or failure.
            if (previous == null) CURRENT.remove();
            else CURRENT.set(previous);
        }
    }

    public static int firstFreeHeight(ChunkGenerator generator, int x, int z,
                                      Heightmap.Types type, LevelHeightAccessor heights,
                                      RandomState randomState) {
        Frame frame = CURRENT.get();
        if (frame == null || generator != frame.generator || heights != frame.heights
                || randomState != frame.randomState) {
            return generator.getFirstFreeHeight(x, z, type, heights, randomState);
        }
        Long2IntOpenHashMap values;
        if (type == Heightmap.Types.WORLD_SURFACE_WG) values = frame.surface;
        else if (type == Heightmap.Types.OCEAN_FLOOR_WG) values = frame.oceanFloor;
        else return generator.getFirstFreeHeight(x, z, type, heights, randomState);

        // Preserve all 32 bits of each coordinate, including negative values.
        long key = ((long) x << 32) | (z & 0xffffffffL);
        int cached = values.get(key);
        if (cached != Integer.MIN_VALUE || values.containsKey(key)) return cached;
        int height = generator.getFirstFreeHeight(x, z, type, heights, randomState);
        if (frame.entries < MAX_ENTRIES) {
            values.put(key, height);
            frame.entries++;
        }
        return height;
    }

    private static final class Frame {
        final ChunkGenerator generator;
        final LevelHeightAccessor heights;
        final RandomState randomState;
        final Long2IntOpenHashMap surface = new Long2IntOpenHashMap(1024);
        final Long2IntOpenHashMap oceanFloor = new Long2IntOpenHashMap(32);
        int entries;

        Frame(ChunkGenerator generator, LevelHeightAccessor heights, RandomState randomState) {
            this.generator = generator;
            this.heights = heights;
            this.randomState = randomState;
            surface.defaultReturnValue(Integer.MIN_VALUE);
            oceanFloor.defaultReturnValue(Integer.MIN_VALUE);
        }
    }
}
