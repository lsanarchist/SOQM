package qm.performance.guardian;

import dev.latvian.mods.kubejs.core.MinecraftServerKJS;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Event lifetime and original-UUID reward authority for Mayhem guardians. */
public final class GuardianLifecycle {
    static final String GUARDIAN_TAG = "qm_mayhem_guardian";
    static final String LIFETIME_KEY = "qm_mayhem_guardian_lifetime";
    static final long LIFETIME = 12_000L;
    static final int INDEX_BUDGET = 64;
    static final int EXPIRY_BUDGET = 128;
    private static final Map<MinecraftServer, Run> RUNS = new IdentityHashMap<>();

    private GuardianLifecycle() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, GuardianLifecycle::joined);
        NeoForge.EVENT_BUS.addListener(GuardianLifecycle::left);
        NeoForge.EVENT_BUS.addListener(GuardianLifecycle::tick);
        NeoForge.EVENT_BUS.addListener(GuardianLifecycle::stopping);
        NeoForge.EVENT_BUS.addListener(GuardianLifecycle::stopped);
    }

    /** Called only after the existing KubeJS spawner successfully adds the mob. */
    public static void registerGuardian(Mob entity, int tier) {
        if (tier < 0 || tier > 6 || !(entity.level() instanceof ServerLevel level)
                || entity.isRemoved()) throw new IllegalArgumentException("Invalid guardian spawn");
        MinecraftServer server = level.getServer();
        Run run = run(server);
        run.prepare();
        if (run.stopping) throw new IllegalStateException("Server is stopping");
        long expires = server.overworld().getGameTime() + LIFETIME;
        run.ledger.issue(entity.getUUID(), tier, expires, level.dimension().location().toString());
        entity.addTag(GUARDIAN_TAG);
        writeLifetime(entity, expires);
        run.tickets.put(entity.getUUID(), expires);
        run.loaded.put(entity, expires);
    }

    /** Reads pending authority directly; indexing order cannot hide a live ticket. */
    public static boolean hasTicket(Entity entity) {
        if (!(entity.level() instanceof ServerLevel level) || !marked(entity)) return false;
        Run run = run(level.getServer());
        run.prepare();
        GuardianLedger.Ticket ticket = run.ledger.find(entity.getUUID());
        return !run.stopping && ticket != null && ticket.expires() > level.getServer().overworld().getGameTime();
    }

    /**
     * The JS death callback waits a tick and checks resurrection before calling.
     * Repeat that guard here, then consume authority before any loot is spawned.
     * Entity data alone can never mint a ticket or pay a captured/duplicated mob.
     */
    public static int claimReward(LivingEntity entity) {
        if (!entity.isDeadOrDying() || !(entity.level() instanceof ServerLevel level) || !marked(entity)) return -1;
        Run run = run(level.getServer());
        run.prepare();
        if (run.stopping) return -1;
        GuardianLedger.Ticket ticket = run.ledger.find(entity.getUUID());
        if (ticket == null || ticket.expires() <= level.getServer().overworld().getGameTime()) return -1;
        CompoundTag lifetime = entity.getPersistentData().getCompound(LIFETIME_KEY);
        if (lifetime.hasUUID("owner") && !entity.getUUID().equals(lifetime.getUUID("owner"))) return -1;
        run.retire(entity.getUUID());
        run.loaded.remove(entity);
        return ticket.tier();
    }

    /** Called at the actual Peaceful branch of Mob.checkDespawn, not capture. */
    public static void peacefulDespawn(Mob entity) {
        if (!(entity.level() instanceof ServerLevel level) || !marked(entity)) return;
        Run run = run(level.getServer());
        if (run.stopping) return;
        run.prepare();
        run.retire(entity.getUUID());
    }

    private static boolean marked(Entity entity) { return entity.getTags().contains(GUARDIAN_TAG); }

    private static void writeLifetime(Entity entity, long expires) {
        CompoundTag data = new CompoundTag();
        data.putInt("version", 1);
        data.putUUID("owner", entity.getUUID());
        data.putLong("expires", expires);
        entity.getPersistentData().put(LIFETIME_KEY, data);
    }

    static Run run(MinecraftServer server) {
        if (!server.isSameThread()) throw new IllegalStateException("Guardian lifecycle must run on the server thread");
        return RUNS.computeIfAbsent(server, Run::new);
    }

    private static void joined(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !marked(event.getEntity())) return;
        // Join can run before the underlying chunk is FULL. Only retain the
        // marked entity here; all NBT migration and world changes wait for Post.
        run(level.getServer()).joins.addLast(event.getEntity());
    }

    private static void left(EntityLeaveLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        Run run = RUNS.get(level.getServer());
        if (run == null) return;
        Entity entity = event.getEntity();
        Entity.RemovalReason reason = entity.getRemovalReason();
        // A visibility/tracking change can fire this event without a removal.
        // Retain that bounded deadline so the still-loaded entity still expires.
        if (reason != null) run.loaded.remove(entity);
        // DISCARDED removals include capture items. Their ticket remains
        // claimable by the same UUID only until its original deadline, including
        // while absent. No ticket is retained indefinitely for an unloaded mob.
    }

    private static void tick(ServerTickEvent.Post event) { run(event.getServer()).tick(); }

    private static void stopping(ServerStoppingEvent event) {
        Run run = RUNS.get(event.getServer());
        if (run != null) { run.stopping = true; run.clearTransient(); }
    }

    private static void stopped(ServerStoppedEvent event) {
        Run run = RUNS.remove(event.getServer());
        if (run != null) run.clearTransient();
    }

    static final class Run {
        final MinecraftServer server;
        final DeadlineIndex<UUID> tickets = new DeadlineIndex<>();
        final DeadlineIndex<Entity> loaded = new DeadlineIndex<>();
        final Deque<Entity> joins = new ArrayDeque<>();
        GuardianLedger ledger;
        boolean stopping;

        Run(MinecraftServer server) { this.server = server; }

        void prepare() {
            if (ledger == null) {
                CompoundTag persistent = ((MinecraftServerKJS) server).kjs$getPersistentData();
                ledger = new GuardianLedger(persistent);
            }
        }

        void tick() {
            if (stopping) return;
            prepare();
            long now = server.overworld().getGameTime();
            for (int count = 0; count < EXPIRY_BUDGET && !joins.isEmpty(); count++) {
                observe(joins.removeFirst(), now);
            }
            ledger.indexBatch(INDEX_BUDGET, ticket -> tickets.put(ticket.id(), ticket.expires()));
            // Expire actual loaded mobs first. The dead/revived check and ticket
            // deadline in claimReward prevent expiry from yielding arena loot.
            loaded.drain(now, EXPIRY_BUDGET, entity -> {
                if (!entity.isRemoved()) entity.discard();
                retire(entity.getUUID());
            });
            tickets.drain(now, EXPIRY_BUDGET, ledger::remove);
        }

        void observe(Entity entity, long now) {
            if (entity.isRemoved() || entity.getServer() != server || !marked(entity)) return;
            GuardianLedger.Ticket ticket = ledger.find(entity.getUUID());
            if (ticket != null) {
                writeLifetime(entity, ticket.expires());
                tickets.put(ticket.id(), ticket.expires());
                loaded.put(entity, ticket.expires());
                return;
            }
            CompoundTag data = entity.getPersistentData().getCompound(LIFETIME_KEY);
            if (data.getInt("version") == 1 && data.hasUUID("owner")
                    && data.contains("expires", Tag.TAG_ANY_NUMERIC)) {
                // Captured copies can retain the deadline but never gain a new
                // UUID ticket. Original-UUID return also cannot reset its timer.
                loaded.put(entity, data.getLong("expires"));
                return;
            }
            // Legacy guardians had only this reserved tag, with the lifetime in
            // the server ledger. A tag with no authoritative ticket is expired
            // or a legacy copy, so retire it on natural load without loot. Direct
            // lookup above includes every unindexed pending migration bucket.
            loaded.put(entity, now);
        }

        void retire(UUID id) { ledger.remove(id); tickets.remove(id); }
        void clearTransient() { joins.clear(); loaded.clear(); tickets.clear(); }
    }
}
