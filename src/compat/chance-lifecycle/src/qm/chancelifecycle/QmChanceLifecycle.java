package qm.chancelifecycle;

import chanceCubes.rewards.biodomeGen.BioDomeGen;
import chanceCubes.rewards.defaultRewards.BossBaseReward;
import chanceCubes.util.Task;
import com.mojang.logging.LogUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import qm.chancelifecycle.mixin.SchedulerAccess;

@Mod("qm_chancecubes_lifecycle")
public final class QmChanceLifecycle {
    private static final Logger LOG = LogUtils.getLogger();
    private static WeakReference<MinecraftServer> active = new WeakReference<>(null);
    private static final Map<Task, Long> epochs = new WeakHashMap<>();
    private static final Map<BioDomeGen, Arena> arenas = new IdentityHashMap<>();
    private static long epoch;
    private static boolean stopping;
    private static boolean restoring;

    public QmChanceLifecycle() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmChanceLifecycle::starting);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmChanceLifecycle::stopping);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmChanceLifecycle::stopped);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmChanceLifecycle::tick);
    }

    private static synchronized void begin(MinecraftServer server) {
        SchedulerAccess.qmTasks().clear();
        arenas.clear();
        active = new WeakReference<>(server);
        epoch++;
        stopping = false;
        restoring = false;
    }

    private static void starting(ServerAboutToStartEvent event) {
        begin(event.getServer());
    }

    private static void tick(ServerTickEvent.Pre event) {
        if (active.get() != event.getServer()) begin(event.getServer());
    }

    private static void stopping(ServerStoppingEvent event) {
        MinecraftServer server = event.getServer();
        if (active.get() != server) return;
        stopping = true;
        SchedulerAccess.qmTasks().clear();
        // Restore only temporary boss arenas, while their own server still
        // owns its live levels. Permanent biodome rewards are never registered.
        restoring = true;
        int restored = 0;
        try {
            for (Arena arena : new ArrayList<>(arenas.values())) {
                if (arena.level.getServer() != server || !usable(arena.level)) continue;
                try {
                    if (arena.battle != null) {
                        BossAccess.abort(arena.reward, arena.level, arena.pos, arena.player, arena.battle);
                    } else {
                        arena.dome.removeDome(false);
                    }
                    restored++;
                } catch (RuntimeException failure) {
                    LOG.warn("[QM Chance lifecycle] Could not restore a temporary boss arena during shutdown", failure);
                }
            }
        } finally {
            arenas.clear();
            SchedulerAccess.qmTasks().clear();
            restoring = false;
        }
        if (restored > 0) LOG.info("[QM Chance lifecycle] Restored {} temporary boss arena(s) before saving", restored);
    }

    private static synchronized void stopped(ServerStoppedEvent event) {
        if (active.get() != event.getServer()) return;
        SchedulerAccess.qmTasks().clear();
        arenas.clear();
        stopping = true;
        active.clear();
    }

    public static synchronized boolean claim(Task task) {
        MinecraftServer server = active.get();
        if (!mayTick() || stopping) return false;
        Long previous = epochs.get(task);
        if (previous != null && previous.longValue() != epoch) return false;
        epochs.put(task, epoch);
        return server != null;
    }

    public static boolean mayTick() {
        MinecraftServer server = active.get();
        return server != null && !stopping && server == ServerLifecycleHooks.getCurrentServer()
            && !server.isStopped() && server.isSameThread();
    }

    public static boolean usable(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        MinecraftServer server = active.get();
        return server != null && server == serverLevel.getServer()
            && server == ServerLifecycleHooks.getCurrentServer()
            && !server.isStopped() && server.isSameThread()
            && (!stopping || restoring)
            && server.getLevel(serverLevel.dimension()) == serverLevel;
    }

    public static void arena(BossBaseReward reward, BioDomeGen dome, ServerLevel level,
                             BlockPos pos, Player player) {
        if (usable(level)) arenas.put(dome, new Arena(reward, dome, level, pos.immutable(), player));
    }

    public static void battle(BossBaseReward reward, ServerLevel level, BlockPos pos,
                              Player player, Object state) {
        if (!usable(level)) return;
        BioDomeGen dome = BossAccess.dome(state);
        Arena record = arenas.get(dome);
        if (record == null) {
            record = new Arena(reward, dome, level, pos.immutable(), player);
            arenas.put(dome, record);
        }
        record.battle = state;
    }

    public static void finished(Object state) {
        arenas.remove(BossAccess.dome(state));
    }

    public static long currentEpoch() { return epoch; }
    public static int pendingTasks() { return SchedulerAccess.qmTasks().size(); }
    public static int activeArenas() { return arenas.size(); }

    private static final class Arena {
        final BossBaseReward reward;
        final BioDomeGen dome;
        final ServerLevel level;
        final BlockPos pos;
        final Player player;
        Object battle;
        Arena(BossBaseReward reward, BioDomeGen dome, ServerLevel level, BlockPos pos, Player player) {
            this.reward=reward; this.dome=dome; this.level=level; this.pos=pos; this.player=player;
        }
    }
}

