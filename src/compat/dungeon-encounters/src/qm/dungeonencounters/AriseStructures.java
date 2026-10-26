package qm.dungeonencounters;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/** Native 3-D structure bounds; no radius guesses, chunk tickets, generation or synchronous disk reads. */
public final class AriseStructures {
    private static final int CACHE_LIMIT = 1024;
    private static final int IN_FLIGHT_LIMIT = 8;
    private static final long RETRY_NANOS = TimeUnit.SECONDS.toNanos(30);
    private static final Map<Key, Bounds> CACHE = new LinkedHashMap<>(128, 0.75F, true);
    private static final Map<Key, Long> PENDING = new HashMap<>();
    private static long sequence;
    private record Key(ResourceLocation dimension, ResourceLocation structure, long chunk) { }
    private record Bounds(BoundingBox box, boolean adjusted, long retryAfter) { }
    private AriseStructures() { }

    /** Cheap loaded-chunk discovery hint; eligibility still requires contains(). */
    public static boolean hasReferences(ServerLevel level, BlockPos position) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(position.getX() >> 4, position.getZ() >> 4);
        if (chunk == null) return false;
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        return chunk.getAllReferences().keySet().stream().anyMatch(structure -> {
            ResourceLocation id = registry.getKey(structure);
            return id != null && id.getNamespace().equals("dungeons_arise");
        });
    }

    /** The candidate's own chunk must be loaded; only its native references are followed. */
    public static boolean contains(ServerLevel level, BlockPos position) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(position.getX() >> 4, position.getZ() >> 4);
        if (chunk == null) return false;
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        for (Map.Entry<Structure, LongSet> entry : chunk.getAllReferences().entrySet()) {
            ResourceLocation id = registry.getKey(entry.getKey());
            if (id == null || !id.getNamespace().equals("dungeons_arise")) continue;
            for (long reference : entry.getValue()) {
                Key key = new Key(level.dimension().location(), id, reference);
                LevelChunk startChunk = level.getChunkSource().getChunkNow(ChunkPos.getX(reference), ChunkPos.getZ(reference));
                if (startChunk != null) {
                    StructureStart start = startChunk.getStartForStructure(entry.getKey());
                    if (start != null && start.isValid()) {
                        remember(key, new Bounds(start.getBoundingBox(), true, Long.MAX_VALUE));
                        if (start.getBoundingBox().isInside(position)) return true;
                    } else {
                        remember(key, new Bounds(null, false, System.nanoTime() + RETRY_NANOS));
                    }
                    continue;
                }
                Bounds known = cached(key);
                if (known != null && known.box() != null) {
                    BoundingBox bounds = known.adjusted() ? known.box() : entry.getKey().adjustBoundingBox(known.box());
                    if (bounds.isInside(position)) return true;
                } else if (known == null || System.nanoTime() >= known.retryAfter()) {
                    // scanChunk reads existing region data/pending saves only. It
                    // neither creates a ChunkHolder nor asks the world generator.
                    scan(level.getChunkSource().chunkScanner(), key);
                }
            }
        }
        return false;
    }

    /** Capture starts as chunks load, including newly generated starts not saved to disk yet. */
    public static void loaded(ServerLevel level, ChunkAccess chunk) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        for (var entry : chunk.getAllStarts().entrySet()) {
            ResourceLocation id = registry.getKey(entry.getKey());
            if (id == null || !id.getNamespace().equals("dungeons_arise")) continue;
            StructureStart start = entry.getValue();
            remember(new Key(level.dimension().location(), id, chunk.getPos().toLong()),
                new Bounds(start != null && start.isValid() ? start.getBoundingBox() : null,
                    true, System.nanoTime() + RETRY_NANOS));
        }
    }

    public static synchronized void clear() {
        CACHE.clear();
        PENDING.clear();
        // Late completions carry an obsolete token and cannot repopulate a new world.
        sequence++;
    }

    private static synchronized Bounds cached(Key key) { return CACHE.get(key); }

    private static synchronized void remember(Key key, Bounds bounds) {
        PENDING.remove(key);
        store(key, bounds);
    }

    private static void store(Key key, Bounds bounds) {
        CACHE.put(key, bounds);
        while (CACHE.size() > CACHE_LIMIT) CACHE.remove(CACHE.keySet().iterator().next());
    }

    private static void scan(ChunkScanAccess scanner, Key key) {
        final long token;
        synchronized (AriseStructures.class) {
            if (PENDING.containsKey(key) || PENDING.size() >= IN_FLIGHT_LIMIT) return;
            token = ++sequence;
            PENDING.put(key, token);
        }
        CollectFields collector = new CollectFields(new FieldSelector("structures", CompoundTag.TYPE, "starts"));
        try {
            scanner.scanChunk(new ChunkPos(key.chunk()), collector).whenComplete((unused, error) -> {
                BoundingBox box = error == null ? savedBounds(collector.getResult(), key.structure()) : null;
                synchronized (AriseStructures.class) {
                    if (!Long.valueOf(token).equals(PENDING.get(key))) return;
                    PENDING.remove(key);
                    store(key, new Bounds(box, false, System.nanoTime() + RETRY_NANOS));
                }
            });
        } catch (RuntimeException failure) {
            synchronized (AriseStructures.class) {
                if (Long.valueOf(token).equals(PENDING.get(key))) {
                    PENDING.remove(key);
                    store(key, new Bounds(null, false, System.nanoTime() + RETRY_NANOS));
                }
            }
        }
    }

    private static BoundingBox savedBounds(Tag root, ResourceLocation id) {
        if (!(root instanceof CompoundTag saved)) return null;
        CompoundTag start = saved.getCompound("structures").getCompound("starts").getCompound(id.toString());
        if (!start.getString("id").equals(id.toString())) return null;
        ListTag pieces = start.getList("Children", Tag.TAG_COMPOUND);
        List<BoundingBox> boxes = new ArrayList<>();
        for (int i = 0; i < pieces.size(); i++) {
            int[] box = pieces.getCompound(i).getIntArray("BB");
            if (box.length != 6 || box[0] > box[3] || box[1] > box[4] || box[2] > box[5]) return null;
            boxes.add(new BoundingBox(box[0], box[1], box[2], box[3], box[4], box[5]));
        }
        return BoundingBox.encapsulatingBoxes(boxes).orElse(null);
    }
}
