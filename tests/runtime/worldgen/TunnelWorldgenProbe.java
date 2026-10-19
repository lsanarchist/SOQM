package soqm.validation.worldgen;

import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.zip.ZipFile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import qm.performance.worldgen.TunnelHeightCache;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Test-only native fixture. Compares the live Mixin target with the exact original
 * class read from the pinned local mod JAR; no upstream implementation is copied.
 * The caller must run this in an isolated server and collect the returned report.
 */
public final class TunnelWorldgenProbe {
    private static final String TARGET = "com.manyfunadventures.abandonedtunnels.TunnelStructure";
    private static final String CONFIG = "com.manyfunadventures.abandonedtunnels.Config";
    private static final String JAR_SHA = "05061c8d700e870ea8f46fd42e0ca4416148fa1c7908f5d72775813022a62e68";
    private static final String CLASS_SHA = "954bc585c4705fbbf4d40df08b81c4063fa5a6dfb9c5be24ca7b0b7191711748";
    private static final String SITE_SHA = "41f266819d69c66143f15f31ce1037754fa6fc3b1116435634b06b37e9d1e2af";
    private static final String CONFIG_SHA = "ad11afa2fbfa3afbc96997e692f42f86d4d305ab9f773d2d6cf92916d92f5e36";
    private static Method liveTrapMask;

    private TunnelWorldgenProbe() {}

    public static Map<String, Object> run(MinecraftServer server) throws Exception {
        ServerLevel level = server.overworld();
        Structure live = server.registryAccess().registryOrThrow(Registries.STRUCTURE)
                .get(ResourceLocation.parse("abandonedtunnels:tunnel"));
        check(live != null && live.getClass().getName().equals(TARGET), "Native tunnel structure is registered");
        Path jar = Path.of("mods", "abandonedtunnels-1.0.0.jar");
        check(sha(Files.readAllBytes(jar)).equals(JAR_SHA), "Original JAR matches its reviewed pin");
        OriginalLoader originalLoader = new OriginalLoader(jar);
        check((int) originalLoader.loadClass(CONFIG).getMethod("mask").invoke(null) == originalTrapMask(),
                "Isolated configuration bridge reads the live mod configuration");
        Class<?> originalClass = originalLoader.loadClass(TARGET);
        Structure original = (Structure) originalClass.getConstructor(Structure.StructureSettings.class)
                .newInstance(live.getModifiedStructureSettings());
        Method originalFind = finder(originalClass);
        Method liveFind = finder(live.getClass());
        Map<String, Object> report = new LinkedHashMap<>();
        List<Map<String, Object>> profiles = new ArrayList<>();
        long before = 0;
        long after = 0;
        int present = 0;
        int absent = 0;
        Set<Integer> axes = new LinkedHashSet<>();
        Set<Integer> branchCounts = new LinkedHashSet<>();
        MessageDigest layouts = MessageDigest.getInstance("SHA-256");
        for (int profile = 0; profile < 8; profile++) {
            long profileBefore = 0;
            long profileAfter = 0;
            int profilePresent = 0;
            for (int sample = 0; sample < 64; sample++) {
                long seed = 0x32a485edL + sample * 7919L + profile * 104729L;
                ChunkPos pos = new ChunkPos((sample % 7 - 3) * 16, (sample % 5 - 2) * 16);
                CountingGenerator vanilla = new CountingGenerator(level.getChunkSource().getGenerator(), profile);
                CountingGenerator patched = new CountingGenerator(level.getChunkSource().getGenerator(), profile);
                Outcome expected = invoke(originalFind, original, context(level, vanilla, seed, pos), level);
                Outcome observed = invoke(liveFind, live, context(level, patched, seed, pos), level);
                check(expected.equals(observed), "Exact piece NBT and RNG equivalence: profile " + profile + ", seed " + seed);
                check(patched.requests.equals(new ArrayList<>(vanilla.unique)),
                        "Patched query order equals original first occurrences without missing terrain samples");
                check(patched.calls < vanilla.calls, "Repeated terrain queries are eliminated in each search");
                profileBefore += vanilla.calls;
                profileAfter += patched.calls;
                layouts.update(observed.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                if (observed.position != null) {
                    present++;
                    profilePresent++;
                    for (Tag entry : (ListTag) observed.pieces) {
                        CompoundTag piece = (CompoundTag) entry;
                        axes.add(piece.getInt("axis"));
                        branchCounts.add(piece.getIntArray("br").length / 4);
                    }
                } else absent++;
            }
            profiles.add(Map.of("terrain_profile", profile, "searches", 64,
                    "tunnels", profilePresent, "original_queries", profileBefore, "patched_queries", profileAfter));
            before += profileBefore;
            after += profileAfter;
        }
        check(present > 0 && absent > 0, "Both generated and rejected sites are covered");
        check(axes.containsAll(List.of(0, 1)), "Both tunnel axes are covered");
        check(branchCounts.containsAll(List.of(0, 1, 2, 3)), "All zero-to-three branch outcomes are covered");

        List<Map<String, Object>> noiseCases = new ArrayList<>();
        for (ChunkPos pos : List.of(new ChunkPos(0, 0), new ChunkPos(128, -96),
                new ChunkPos(-512, 513), new ChunkPos(2048, -2048))) {
            CountingGenerator vanilla = new CountingGenerator(level.getChunkSource().getGenerator(), -1);
            CountingGenerator patched = new CountingGenerator(level.getChunkSource().getGenerator(), -1);
            Outcome expected = invoke(originalFind, original, context(level, vanilla, level.getSeed(), pos), level);
            Outcome observed = invoke(liveFind, live, context(level, patched, level.getSeed(), pos), level);
            check(expected.equals(observed), "Native overworld noise preserves the exact result and RNG");
            check(patched.requests.equals(new ArrayList<>(vanilla.unique)), "Native noise query deduplication");
            noiseCases.add(Map.of("chunk_x", pos.x, "chunk_z", pos.z,
                    "original_queries", vanilla.calls, "patched_queries", patched.calls,
                    "tunnel_present", observed.position != null));
        }

        testCacheBoundaries(level);
        testExceptionalTarget(level, live, liveFind);
        testThreadIsolation(level);
        report.put("passed", true);
        report.put("baseline_jar_sha256", JAR_SHA);
        report.put("baseline_target_class_sha256", CLASS_SHA);
        report.put("baseline_target_bytecode_unmodified", true);
        report.put("baseline_config_uses_live_native_mask", true);
        report.put("synthetic_terrain_searches", 512);
        report.put("generated_tunnels", present);
        report.put("rejected_sites", absent);
        report.put("covered_axes", axes);
        report.put("covered_branch_counts", branchCounts);
        report.put("original_height_queries", before);
        report.put("patched_height_queries", after);
        report.put("removed_queries", before - after);
        report.put("layout_and_rng_digest_sha256", HexFormat.of().formatHex(layouts.digest()));
        report.put("terrain_profiles", profiles);
        report.put("native_overworld_noise_cases", noiseCases);
        report.put("cache_context_heightmap_coordinate_isolation", true);
        report.put("cache_capacity_limit", 2048);
        report.put("nested_and_exception_cleanup", true);
        report.put("parallel_worker_isolation", true);
        report.put("scope", "Method-level native generation equivalence and query counts; no wall-time speedup claim or whole-world block hash.");
        return report;
    }

    private static Structure.GenerationContext context(ServerLevel level, ChunkGenerator generator, long seed, ChunkPos pos) {
        return new Structure.GenerationContext(level.registryAccess(), generator, generator.getBiomeSource(),
                level.getChunkSource().randomState(), level.getStructureManager(), seed, pos, level, biome -> true);
    }

    private static Method finder(Class<?> type) throws Exception {
        Method method = type.getDeclaredMethod("findGenerationPoint", Structure.GenerationContext.class);
        method.setAccessible(true);
        return method;
    }

    @SuppressWarnings("unchecked")
    private static Outcome invoke(Method method, Object target, Structure.GenerationContext context,
                                  ServerLevel level) throws Exception {
        Optional<Structure.GenerationStub> result;
        try {
            result = (Optional<Structure.GenerationStub>) method.invoke(target, context);
        } catch (InvocationTargetException failure) {
            if (failure.getCause() instanceof Exception cause) throw cause;
            throw failure;
        }
        int draws = context.random().getCount();
        long next = context.random().nextLong();
        if (result.isEmpty()) return new Outcome(null, new ListTag(), draws, next);
        Structure.GenerationStub stub = result.orElseThrow();
        Tag pieces = stub.getPiecesBuilder().build().save(StructurePieceSerializationContext.fromLevel(level));
        return new Outcome(stub.position(), pieces, draws, next);
    }

    private record Outcome(BlockPos position, Tag pieces, int randomDraws, long nextRandomLong) {}
    private record Sample(int x, int z, Heightmap.Types type) {}

    private static void testCacheBoundaries(ServerLevel level) {
        CountingGenerator generator = new CountingGenerator(level.getChunkSource().getGenerator(), 0);
        Structure.GenerationContext context = context(level, generator, 88L, new ChunkPos(0, 0));
        height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
        height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
        check(generator.calls == 2, "Queries outside a placement search are not cached");
        generator.reset();
        TunnelHeightCache.during(context, () -> {
            height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
            height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
            height(context, 1, 2, Heightmap.Types.OCEAN_FLOOR_WG);
            check(generator.calls == 2, "Heightmap results are isolated");
            TunnelHeightCache.during(context, () -> {
                height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
                height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
                return null;
            });
            check(generator.calls == 3, "A nested search owns a separate cache");
            height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
            check(generator.calls == 3, "A nested search restores the outer cache");
            try {
                TunnelHeightCache.during(context, () -> { throw new ExpectedFailure(); });
                throw new AssertionError("Expected nested failure");
            } catch (ExpectedFailure expected) { /* expected */ }
            height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
            check(generator.calls == 3, "Exceptional nested search restores the outer cache");
            LevelHeightAccessor anotherHeightRange = LevelHeightAccessor.create(-32, 256);
            for (int i = 0; i < 2; i++) {
                TunnelHeightCache.firstFreeHeight(generator, 1, 2, Heightmap.Types.WORLD_SURFACE_WG,
                        anotherHeightRange, context.randomState());
                TunnelHeightCache.firstFreeHeight(generator, 1, 2, Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(), null);
            }
            check(generator.calls == 7, "Different height accessors and random states bypass the active cache");
            CountingGenerator anotherGenerator = new CountingGenerator(generator.delegate, 1);
            for (int i = 0; i < 2; i++) TunnelHeightCache.firstFreeHeight(anotherGenerator, 1, 2,
                    Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
            check(anotherGenerator.calls == 2, "Different generators bypass the active cache");
            return null;
        });
        height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
        check(generator.calls == 8, "Successful search removes its thread-local cache");

        generator.reset();
        TunnelHeightCache.during(context, () -> {
            for (int x = 0; x <= 2048; x++) height(context, x, 0, Heightmap.Types.WORLD_SURFACE_WG);
            height(context, 2048, 0, Heightmap.Types.WORLD_SURFACE_WG);
            height(context, 0, 0, Heightmap.Types.WORLD_SURFACE_WG);
            check(generator.calls == 2050, "Cache is bounded at 2048 entries without evicting needed values");
            return null;
        });
        generator.reset();
        TunnelHeightCache.during(context, () -> {
            for (int[] point : List.of(new int[]{-1, 0}, new int[]{0, -1},
                    new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE})) {
                for (int repeat = 0; repeat < 2; repeat++) height(context, point[0], point[1], Heightmap.Types.WORLD_SURFACE_WG);
            }
            check(generator.calls == 4, "Full signed coordinates and an Integer.MIN_VALUE height remain distinct and cacheable");
            return null;
        });
    }

    private static void testExceptionalTarget(ServerLevel level, Structure live, Method find) throws Exception {
        CountingGenerator generator = new CountingGenerator(level.getChunkSource().getGenerator(), 0);
        Structure.GenerationContext context = context(level, generator, 42L, new ChunkPos(0, 0));
        generator.failAt = 4;
        try {
            invoke(find, live, context, level);
            throw new AssertionError("Expected generation failure");
        } catch (ExpectedFailure expected) { /* exact live WrapMethod finally path */ }
        generator.reset();
        height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
        height(context, 1, 2, Heightmap.Types.WORLD_SURFACE_WG);
        check(generator.calls == 2, "The live mixed target removes its cache when native generation throws");
    }

    private static void testThreadIsolation(ServerLevel level) throws Exception {
        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int worker = 0; worker < 8; worker++) {
            CountingGenerator generator = new CountingGenerator(level.getChunkSource().getGenerator(), worker);
            Structure.GenerationContext context = context(level, generator, worker, new ChunkPos(0, 0));
            tasks.add(() -> TunnelHeightCache.during(context, () -> {
                for (int repeat = 0; repeat < 100; repeat++) height(context, 8, 8, Heightmap.Types.WORLD_SURFACE_WG);
                return generator.calls == 1;
            }));
        }
        try (var workers = Executors.newFixedThreadPool(4)) {
            for (var result : workers.invokeAll(tasks)) check(result.get(), "Concurrent generation scopes are isolated");
        }
    }

    private static int height(Structure.GenerationContext context, int x, int z, Heightmap.Types type) {
        return TunnelHeightCache.firstFreeHeight(context.chunkGenerator(), x, z, type,
                context.heightAccessor(), context.randomState());
    }

    private static final class ExpectedFailure extends RuntimeException {}

    private static final class CountingGenerator extends ChunkGenerator {
        final ChunkGenerator delegate;
        final int profile;
        final List<Sample> requests = new ArrayList<>();
        final Set<Sample> unique = new LinkedHashSet<>();
        int calls;
        int failAt;

        CountingGenerator(ChunkGenerator delegate, int profile) {
            super(delegate.getBiomeSource());
            this.delegate = delegate;
            this.profile = profile;
        }

        void reset() { calls = 0; failAt = 0; requests.clear(); unique.clear(); }

        @Override public int getFirstFreeHeight(int x, int z, Heightmap.Types type,
                                                LevelHeightAccessor heights, RandomState state) {
            calls++;
            Sample sample = new Sample(x, z, type);
            requests.add(sample);
            unique.add(sample);
            if (calls == failAt) throw new ExpectedFailure();
            if (profile < 0) return delegate.getFirstFreeHeight(x, z, type, heights, state);
            if (x == Integer.MAX_VALUE && z == Integer.MIN_VALUE) return Integer.MIN_VALUE;
            int dx = Math.floorMod(x + 128, 256) - 128;
            int dz = Math.floorMod(z + 128, 256) - 128;
            int ridgeX = Math.max(0, 100 - 2 * Math.abs(dx));
            int ridgeZ = Math.max(0, 100 - 2 * Math.abs(dz));
            int hill = Math.max(0, 100 - (int) (2 * Math.hypot(dx, dz)));
            int surface = switch (profile) {
                case 0 -> 80 + ridgeX;
                case 1 -> 80 + ridgeZ;
                case 2 -> 80 + hill;
                case 3 -> 72;
                case 4 -> 63;
                case 5 -> 80 + ridgeX;
                case 6 -> 80 + Math.min(ridgeX, ridgeZ);
                case 7 -> 80 + hill + Math.floorMod(x + z, 3);
                default -> throw new AssertionError("Unknown terrain fixture");
            };
            if (type == Heightmap.Types.OCEAN_FLOOR_WG
                    && (profile == 4 || profile == 5 && Math.floorMod(x + z, 7) < 3)) return surface - 8;
            return surface;
        }

        @Override public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heights, RandomState state) {
            return getFirstFreeHeight(x, z, type, heights, state);
        }
        @Override protected MapCodec<? extends ChunkGenerator> codec() { throw new UnsupportedOperationException("Fixture only"); }
        @Override public void applyCarvers(WorldGenRegion region, long seed, RandomState state, BiomeManager biomes,
                StructureManager structures, ChunkAccess chunk, GenerationStep.Carving step) { throw new UnsupportedOperationException(); }
        @Override public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState state,
                ChunkAccess chunk) { throw new UnsupportedOperationException(); }
        @Override public void spawnOriginalMobs(WorldGenRegion region) { throw new UnsupportedOperationException(); }
        @Override public int getGenDepth() { return delegate.getGenDepth(); }
        @Override public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState state,
                StructureManager structures, ChunkAccess chunk) { throw new UnsupportedOperationException(); }
        @Override public int getSeaLevel() { return delegate.getSeaLevel(); }
        @Override public int getMinY() { return delegate.getMinY(); }
        @Override public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor heights, RandomState state) {
            throw new UnsupportedOperationException();
        }
        @Override public void addDebugScreenInfo(List<String> text, RandomState state, BlockPos pos) {}
    }

    /**
     * Public only for the test loader's tiny Config bridge. The real mask method
     * is package-private: a matching package name in another class loader does
     * not grant package access. Invoke the actual native method so server config
     * values are preserved, rather than building an independent Config spec.
     */
    public static int originalTrapMask() {
        try {
            return (int) liveTrapMask.invoke(null);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Could not read the native tunnel trap configuration", failure);
        }
    }

    private static final class OriginalLoader extends ClassLoader {
        private final Map<String, byte[]> isolatedClasses = new LinkedHashMap<>();

        OriginalLoader(Path jar) throws Exception {
            super(TunnelWorldgenProbe.class.getClassLoader());
            try (ZipFile archive = new ZipFile(jar.toFile())) {
                byte[] config = archive.getInputStream(archive.getEntry(CONFIG.replace('.', '/') + ".class")).readAllBytes();
                check(sha(config).equals(CONFIG_SHA), "Native Config bytecode matches the reviewed package-private mask method");
                for (var entry : Map.of(TARGET, CLASS_SHA, TARGET + "$Site", SITE_SHA).entrySet()) {
                    byte[] bytes = archive.getInputStream(archive.getEntry(entry.getKey().replace('.', '/') + ".class")).readAllBytes();
                    check(sha(bytes).equals(entry.getValue()), "Original class matches its reviewed bytecode");
                    isolatedClasses.put(entry.getKey(), bytes);
                }
            }
            Class<?> liveConfig = Class.forName(CONFIG, true, getParent());
            liveTrapMask = liveConfig.getDeclaredMethod("mask");
            check(liveTrapMask.getReturnType() == int.class && Modifier.isStatic(liveTrapMask.getModifiers()),
                    "Native trap mask retains its exact static no-argument contract");
            liveTrapMask.setAccessible(true);
            isolatedClasses.put(CONFIG, configBridge());

            // These are the only other foreign members in TunnelStructure's
            // reviewed constant pool. Keep both on the live mod loader: their
            // registered holders and produced pieces must not be duplicated.
            Class<?> structures = Class.forName("com.manyfunadventures.abandonedtunnels.ModStructures", true, getParent());
            check(Modifier.isPublic(structures.getField("TUNNEL").getModifiers()), "Native structure holder is publicly accessible");
            Class<?> piece = Class.forName("com.manyfunadventures.abandonedtunnels.TunnelPiece", true, getParent());
            check(Modifier.isPublic(piece.getConstructor(int.class, int.class, int.class, int.class,
                            long.class, int.class, int.class, int[].class, int.class).getModifiers()),
                    "Native piece constructor is publicly accessible across the loader boundary");
        }

        @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (!isolatedClasses.containsKey(name)) return super.loadClass(name, resolve);
            synchronized (getClassLoadingLock(name)) {
                Class<?> type = findLoadedClass(name);
                if (type == null) {
                    byte[] bytes = isolatedClasses.get(name);
                    type = defineClass(name, bytes, 0, bytes.length);
                }
                if (resolve) resolveClass(type);
                return type;
            }
        }

        /** Authored access adapter only; original TunnelStructure/Site bytes stay exact. */
        private static byte[] configBridge() {
            ClassWriter writer = new ClassWriter(0);
            writer.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
                    CONFIG.replace('.', '/'), null, "java/lang/Object", null);
            var method = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "mask", "()I", null, null);
            method.visitCode();
            method.visitMethodInsn(Opcodes.INVOKESTATIC, "soqm/validation/worldgen/TunnelWorldgenProbe",
                    "originalTrapMask", "()I", false);
            method.visitInsn(Opcodes.IRETURN);
            method.visitMaxs(1, 0);
            method.visitEnd();
            writer.visitEnd();
            return writer.toByteArray();
        }
    }

    private static String sha(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
