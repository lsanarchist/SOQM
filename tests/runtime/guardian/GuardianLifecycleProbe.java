package soqm.validation.guardian;

import dev.latvian.mods.kubejs.core.MinecraftServerKJS;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import qm.performance.guardian.GuardianLifecycle;

/** Test-only fixture; the harness supplies a fresh, disposable server world. */
public final class GuardianLifecycleProbe {
    private static final String LEDGER = "qm_mayhem_guardians";
    private static final String TAG_NAME = "qm_mayhem_guardian";
    private static final String LIFETIME = "qm_mayhem_guardian_lifetime";
    private static GuardianLifecycleProbe active;
    private final MinecraftServer server;
    private final Map<String, Object> results = new LinkedHashMap<>();
    private final List<Mob> fixtures = new ArrayList<>();
    private final long began;
    private final Difficulty oldDifficulty;
    private final Mob expiresLoaded;
    private final Mob captureBefore;
    private final Mob clone;
    private final Mob canceled;
    private final Mob rewarded;
    private final Mob unrelated;
    private final Mob legacyLive;
    private final Mob legacyExpired;
    private final Mob legacyCopy;
    private final Mob changedDimension;
    private final CompoundTag capturedAfter;
    private final Mob legacyUnloaded;
    private Mob returnedLate;
    private int stage;

    public static Map<String, Object> storageChecks() throws Exception {
        Map<String, Object> checks = new LinkedHashMap<>();
        testDeadlineIndex(checks);
        testLedgerMigration(checks);
        return checks;
    }

    public static Map<String, Object> run(MinecraftServer server) throws Exception {
        active = start(server);
        // Return the live result map; phased assertions append their evidence.
        return active.results;
    }

    public static boolean tick(MinecraftServer server) throws Exception {
        check(active != null && active.server == server, "Guardian probe belongs to this server");
        return active.tick();
    }

    public static void afterReload(MinecraftServer server) throws Exception {
        check(active != null && active.server == server && active.stage == 2, "Guardian phases completed before reload");
        check(active.captureBefore.isAlive() && GuardianLifecycle.hasTicket(active.captureBefore),
                "Script reload retains living guardian authority");
        check(active.lifetime(active.captureBefore) == active.began + 12_000L,
                "Script reload cannot restart guardian lifetime");
        Object reloaded = construct(Class.forName("qm.performance.guardian.GuardianLedger"),
                new Class<?>[]{CompoundTag.class}, ((MinecraftServerKJS) server).kjs$getPersistentData().copy());
        check(call(reloaded, "find", new Class<?>[]{UUID.class}, active.captureBefore.getUUID()) != null,
                "Reloaded authority remains serializable across a subsequent restart");
        active.results.put("script_reload_preserves_authority_and_original_deadline", true);
        active.cleanup();
    }

    /** Call once after the world has begun ticking; then call tick() each tick. */
    public static GuardianLifecycleProbe start(MinecraftServer server) throws Exception {
        return new GuardianLifecycleProbe(server);
    }

    private GuardianLifecycleProbe(MinecraftServer server) throws Exception {
        this.server = server;
        results.putAll(storageChecks());
        began = server.overworld().getGameTime();
        oldDifficulty = server.getWorldData().getDifficulty();
        server.setDifficulty(Difficulty.NORMAL, true);

        expiresLoaded = guardian(server.overworld());
        expiresAt(expiresLoaded, began + 3);
        Mob before = guardian(server.overworld());
        CompoundTag beforeData = before.saveWithoutId(new CompoundTag());
        long originalDeadline = lifetime(before);
        before.discard();
        check(GuardianLifecycle.hasTicket(before), "Capture keeps authority while absent");
        captureBefore = restore(beforeData, server.overworld(), false);
        check(lifetime(captureBefore) == originalDeadline, "Capture does not restart lifetime");
        clone = restore(beforeData, server.overworld(), true);
        check(!GuardianLifecycle.hasTicket(clone), "Copied entity NBT cannot authorize a new UUID");

        Mob after = guardian(server.overworld());
        expiresAt(after, began + 3);
        capturedAfter = after.saveWithoutId(new CompoundTag());
        after.discard();

        Mob peaceful = guardian(server.overworld());
        Mob captureInPeaceful = guardian(server.overworld());
        CompoundTag peacefulCaptureData = captureInPeaceful.saveWithoutId(new CompoundTag());
        server.setDifficulty(Difficulty.PEACEFUL, true);
        captureInPeaceful.discard();
        check(GuardianLifecycle.hasTicket(captureInPeaceful), "Capture during Peaceful retains ticket");
        peaceful.checkDespawn();
        check(peaceful.isRemoved(), "Actual Peaceful checkDespawn removes persistent guardian");
        check(!GuardianLifecycle.hasTicket(peaceful), "Actual Peaceful branch retires ticket immediately");
        server.setDifficulty(Difficulty.NORMAL, true);
        Mob peacefulReturn = restore(peacefulCaptureData, server.overworld(), false);
        check(GuardianLifecycle.hasTicket(peacefulReturn), "Peaceful capture can be released before deadline");
        results.put("peaceful_despawn_distinguished_from_capture", true);

        canceled = guardian(server.overworld());
        Consumer<LivingDeathEvent> resurrection = event -> {
            if (event.getEntity() == canceled) {
                event.setCanceled(true);
                canceled.setHealth(canceled.getMaxHealth());
            }
        };
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, LivingDeathEvent.class, resurrection);
        try { canceled.hurt(server.overworld().damageSources().generic(), 10_000F); }
        finally { NeoForge.EVENT_BUS.unregister(resurrection); }
        check(canceled.isAlive() && GuardianLifecycle.claimReward(canceled) == -1,
                "Canceled death and living guardian cannot claim reward");

        // These legacy tickets are deliberately in a pending bucket underneath
        // 1024 expired records, so direct lookup must work before indexing them.
        CompoundTag legacy = new CompoundTag();
        legacyLive = bare(server.overworld(), true);
        legacyExpired = bare(server.overworld(), true);
        legacyCopy = bare(server.overworld(), true);
        legacyUnloaded = create(server.overworld(), true);
        rewarded = bare(server.overworld(), true);
        legacy.put(legacyLive.getUUID().toString(), record(2, began + 100));
        legacy.put(legacyExpired.getUUID().toString(), record(2, began - 1));
        legacy.put(legacyUnloaded.getUUID().toString(), record(2, began + 100));
        legacy.put(rewarded.getUUID().toString(), record(2, began + 100));
        ListTag pending = store().getList("pending", Tag.TAG_COMPOUND);
        pending.add(legacy);
        CompoundTag abandoned = new CompoundTag();
        for (int i = 0; i < 1024; i++) abandoned.put(new UUID(900, i).toString(), record(2, began - 1));
        pending.add(abandoned);
        check(GuardianLifecycle.hasTicket(legacyLive), "Unindexed legacy authority is immediately readable");
        rewarded.hurt(server.overworld().damageSources().generic(), 10_000F);

        unrelated = bare(server.overworld(), false);
        unrelated.addTag("another_mod_guardian");

        // Exercise the same old-instance leave and same-UUID new-instance join
        // events as dimension transfer without generating a Nether destination.
        Mob originalDimension = guardian(server.overworld());
        CompoundTag movedData = originalDimension.saveWithoutId(new CompoundTag());
        originalDimension.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
        changedDimension = restore(movedData, server.getLevel(Level.NETHER), false);
        expiresAt(changedDimension, began + 4);
    }

    public boolean tick() throws Exception {
        long elapsed = server.overworld().getGameTime() - began;
        if (stage == 0 && elapsed >= 6) {
            check(expiresLoaded.isRemoved(), "Loaded guardian expires at original deadline");
            check(changedDimension.isRemoved(), "Same-UUID guardian expires after changing dimension");
            check(GuardianLifecycle.hasTicket(captureBefore) && captureBefore.isAlive(),
                    "Captured original returns before deadline with authority intact");
            check(!GuardianLifecycle.hasTicket(clone), "Clone never receives authority during load handling");
            check(canceled.isAlive() && GuardianLifecycle.hasTicket(canceled),
                    "Canceled death keeps authority after deferred callback");
            check(!GuardianLifecycle.hasTicket(rewarded) && GuardianLifecycle.claimReward(rewarded) == -1,
                    "Native death callback consumes legacy reward at most once");
            check(legacyLive.isAlive() && lifetime(legacyLive) == began + 100,
                    "Unindexed legacy guardian keeps original deadline, independent of batch order");
            check(legacyExpired.isRemoved() && legacyCopy.isRemoved(),
                    "Expired legacy ticket and unauthorized legacy copy retire without reward");
            check(unrelated.isAlive(), "Unrelated entity/tag is untouched");
            check(!GuardianLifecycle.hasTicket(restoreDetached(capturedAfter, server.overworld(), false)),
                    "Absent capture ticket expires without loading its entity");
            returnedLate = restore(capturedAfter, server.overworld(), false);
            check(server.overworld().addFreshEntity(legacyUnloaded), "Legacy unloaded fixture joins naturally");
            stage = 1;
        }
        if (stage == 1 && elapsed >= 20) {
            check(returnedLate.isRemoved(), "Captured entity returning after deadline retires on natural join");
            check(legacyUnloaded.isAlive() && lifetime(legacyUnloaded) == began + 100,
                    "Unloaded legacy entity returning before expiry retains its original ticket");
            check(store().getList("pending", Tag.TAG_COMPOUND).isEmpty(),
                    "All abandoned legacy records drain in bounded batches");
            results.put("loaded_and_changed_dimension_expiry", true);
            results.put("capture_before_and_after_deadline", true);
            results.put("unloaded_legacy_and_pending_join_order", true);
            results.put("canceled_death_and_at_most_once_reward", true);
            results.put("legacy_expired_and_copied_entity_retirement", true);
            results.put("abandoned_legacy_records_drained", 1024);
            stage = 2;
        }
        return stage == 2;
    }

    public Map<String, Object> results() { return new LinkedHashMap<>(results); }

    private static void testDeadlineIndex(Map<String, Object> results) throws Exception {
        Class<?> type = Class.forName("qm.performance.guardian.DeadlineIndex");
        Object index = construct(type);
        for (int i = 0; i < 10_000; i++) call(index, "put", new Class<?>[]{Object.class, long.class}, "same", (long) i);
        check((int) call(index, "size") == 1 && (int) call(index, "buckets") == 1,
                "Repeated joins/deadline changes do not accumulate stale queue entries");
        call(index, "remove", new Class<?>[]{Object.class}, "same");
        for (int i = 0; i < 300; i++) call(index, "put", new Class<?>[]{Object.class, long.class}, i, 50L + i);
        List<Object> expired = new ArrayList<>();
        int drained = (int) call(index, "drain", new Class<?>[]{long.class, int.class, Consumer.class},
                200L, 64, (Consumer<Object>) expired::add);
        check(drained == 64 && expired.size() == 64, "Due processing respects its per-tick budget");
        check((int) call(index, "size") == 236, "Unprocessed deadlines remain indexed");
        call(index, "clear");
        call(index, "put", new Class<?>[]{Object.class, long.class}, "reentrant", 1L);
        call(index, "drain", new Class<?>[]{long.class, int.class, Consumer.class}, 1L, 64,
                (Consumer<Object>) key -> {
                    try { call(index, "put", new Class<?>[]{Object.class, long.class}, key, 2L); }
                    catch (Exception failure) { throw new RuntimeException(failure); }
                });
        check((int) call(index, "size") == 1, "Expiry callbacks can reschedule without corrupting cursor");
        results.put("deadline_index_replacement_budget_and_reentrancy", true);
    }

    private static void testLedgerMigration(Map<String, Object> results) throws Exception {
        Class<?> type = Class.forName("qm.performance.guardian.GuardianLedger");
        CompoundTag persistent = new CompoundTag();
        CompoundTag legacy = new CompoundTag();
        for (int i = 0; i < 200; i++) legacy.put(new UUID(700, i).toString(), record(i % 7, 12_000L + i));
        persistent.put(LEDGER, legacy);
        Object ledger = construct(type, new Class<?>[]{CompoundTag.class}, persistent);
        List<Object> indexed = new ArrayList<>();
        int count = (int) call(ledger, "indexBatch", new Class<?>[]{int.class, Consumer.class},
                64, (Consumer<Object>) indexed::add);
        check(count == 64 && legacy.size() == 136, "Legacy wrap is drained in a bounded first batch");
        UUID pendingClaim = UUID.fromString(legacy.getAllKeys().iterator().next());
        check(call(ledger, "find", new Class<?>[]{UUID.class}, pendingClaim) != null,
                "Pending capture/death reads authority before indexing");
        call(ledger, "remove", new Class<?>[]{UUID.class}, pendingClaim);
        UUID fresh = new UUID(701, 0);
        call(ledger, "issue", new Class<?>[]{UUID.class, int.class, long.class, String.class},
                fresh, 4, 22_000L, "minecraft:the_nether");
        count = (int) call(ledger, "indexBatch", new Class<?>[]{int.class, Consumer.class},
                64, (Consumer<Object>) indexed::add);
        check(count == 64, "Pending consumption and new spawn cannot invalidate the retained iterator");
        check(call(ledger, "find", new Class<?>[]{UUID.class}, pendingClaim) == null,
                "Consumed pending ticket cannot pay during migration");

        // A real compressed-NBT round trip represents a stop/restart midway
        // through migration: pending and active authority share one saved value.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        NbtIo.writeCompressed(persistent, bytes);
        CompoundTag reloaded = NbtIo.readCompressed(new ByteArrayInputStream(bytes.toByteArray()), NbtAccounter.unlimitedHeap());
        ledger = construct(type, new Class<?>[]{CompoundTag.class}, reloaded);
        check(call(ledger, "find", new Class<?>[]{UUID.class}, pendingClaim) == null,
                "Consumed legacy reward stays consumed after restart");
        check(call(ledger, "find", new Class<?>[]{UUID.class}, fresh) != null,
                "New guardian authority survives restart amid legacy migration");
        while ((boolean) call(ledger, "indexing")) {
            call(ledger, "indexBatch", new Class<?>[]{int.class, Consumer.class}, 64, (Consumer<Object>) indexed::add);
        }
        check((int) call(ledger, "activeSize") == 200, "Restart preserves every unconsumed ticket exactly once");
        call(ledger, "remove", new Class<?>[]{UUID.class}, fresh);
        check(call(ledger, "find", new Class<?>[]{UUID.class}, fresh) == null, "Active claim is consumed immediately");

        CompoundTag corruptRoot = new CompoundTag();
        CompoundTag corrupt = new CompoundTag();
        corrupt.put("not-a-uuid", record(2, 100));
        corrupt.put(new UUID(702, 0).toString(), record(99, 100));
        CompoundTag missingDeadline = new CompoundTag();
        missingDeadline.putInt("tier", 2);
        corrupt.put(new UUID(702, 1).toString(), missingDeadline);
        corruptRoot.put(LEDGER, corrupt);
        Object sanitized = construct(type, new Class<?>[]{CompoundTag.class}, corruptRoot);
        call(sanitized, "indexBatch", new Class<?>[]{int.class, Consumer.class}, 64, (Consumer<Object>) indexed::add);
        check((int) call(sanitized, "activeSize") == 0 && !(boolean) call(sanitized, "indexing"),
                "Malformed UUID, tier and missing expiry are retired without indexing or rewards");
        results.put("legacy_batch_mutation_and_compressed_restart", true);
    }

    private Mob guardian(ServerLevel level) throws Exception {
        Mob mob = bare(level, true);
        GuardianLifecycle.registerGuardian(mob, 2);
        return mob;
    }

    private Mob create(ServerLevel level, boolean marker) {
        Mob mob = EntityType.VINDICATOR.create(level);
        check(mob != null, "Guardian fixture type exists");
        mob.setNoAi(true);
        mob.setPersistenceRequired();
        mob.moveTo(server.overworld().getSharedSpawnPos().above(3), 0, 0);
        if (marker) mob.addTag(TAG_NAME);
        fixtures.add(mob);
        return mob;
    }

    private Mob bare(ServerLevel level, boolean marker) {
        Mob mob = create(level, marker);
        check(level.addFreshEntity(mob), "Guardian fixture joins its level");
        return mob;
    }

    private Mob restoreDetached(CompoundTag data, ServerLevel level, boolean newId) {
        Mob mob = create(level, true);
        mob.load(data.copy());
        if (newId) mob.setUUID(UUID.randomUUID());
        return mob;
    }

    private Mob restore(CompoundTag data, ServerLevel level, boolean newId) {
        Mob mob = restoreDetached(data, level, newId);
        check(level.addFreshEntity(mob), "Serialized guardian rejoins with intended UUID");
        return mob;
    }

    private long lifetime(Mob entity) { return entity.getPersistentData().getCompound(LIFETIME).getLong("expires"); }

    private void expiresAt(Mob entity, long expires) throws Exception {
        store().getCompound("active").getCompound(entity.getUUID().toString()).putLong("expires", expires);
        // Only fixture data shortens a ticket; the production spawner always
        // issues exactly 12000 game ticks. Re-observe through production code.
        Object run = callStatic(GuardianLifecycle.class, "run", new Class<?>[]{MinecraftServer.class}, server);
        call(run, "observe", new Class<?>[]{Entity.class, long.class}, entity, server.overworld().getGameTime());
    }

    private CompoundTag store() { return ((MinecraftServerKJS) server).kjs$getPersistentData().getCompound(LEDGER); }

    private static CompoundTag record(int tier, long expires) {
        CompoundTag record = new CompoundTag();
        record.putInt("tier", tier);
        record.putLong("expires", expires);
        record.putString("dimension", "minecraft:overworld");
        return record;
    }

    private void cleanup() throws Exception {
        Object run = callStatic(GuardianLifecycle.class, "run", new Class<?>[]{MinecraftServer.class}, server);
        for (Mob entity : fixtures) {
            call(run, "retire", new Class<?>[]{UUID.class}, entity.getUUID());
            if (!entity.isRemoved()) entity.discard();
        }
        server.setDifficulty(oldDifficulty, true);
    }

    private static Object construct(Class<?> type) throws Exception { return construct(type, new Class<?>[0]); }
    private static Object construct(Class<?> type, Class<?>[] parameters, Object... arguments) throws Exception {
        Constructor<?> constructor = type.getDeclaredConstructor(parameters);
        constructor.setAccessible(true);
        return constructor.newInstance(arguments);
    }
    private static Object call(Object target, String name) throws Exception { return call(target, name, new Class<?>[0]); }
    private static Object call(Object target, String name, Class<?>[] parameters, Object... arguments) throws Exception {
        Method method = target.getClass().getDeclaredMethod(name, parameters);
        method.setAccessible(true);
        return method.invoke(target, arguments);
    }
    private static Object callStatic(Class<?> type, String name, Class<?>[] parameters, Object... arguments) throws Exception {
        Method method = type.getDeclaredMethod(name, parameters);
        method.setAccessible(true);
        return method.invoke(null, arguments);
    }
    private static void check(boolean value, String message) {
        if (!value) throw new IllegalStateException(message);
    }
}
