package qm.performance.dungeons;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.commands.Commands;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import qm.performance.mixin.dungeons.EntityMembershipAccessor;

/** Event-driven discoveries and tracked entities instead of global selector polling. */
public final class DungeonsEvents {
    private static final Map<MinecraftServer, Pending> SERVERS = new IdentityHashMap<>();

    private static final class Pending {
        final Set<WitherSkeleton> withers = Collections.newSetFromMap(new IdentityHashMap<>());
        final Set<LightningBolt> lightning = Collections.newSetFromMap(new IdentityHashMap<>());
    }

    private DungeonsEvents() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(DungeonsEvents::earned);
        NeoForge.EVENT_BUS.addListener(DungeonsEvents::login);
        NeoForge.EVENT_BUS.addListener(DungeonsEvents::reload);
        NeoForge.EVENT_BUS.addListener(DungeonsEvents::join);
        NeoForge.EVENT_BUS.addListener(DungeonsEvents::leave);
        NeoForge.EVENT_BUS.addListener(DungeonsEvents::commands);
        NeoForge.EVENT_BUS.addListener(DungeonsEvents::stopped);
    }

    private static void earned(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && DungeonsDataOptimizations.DISCOVERIES.contains(event.getAdvancement().id())) {
            grant(player, event.getAdvancement().id());
        }
    }

    private static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) reconcile(player);
    }

    private static void reload(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) return; // The login event reconciles one player's loaded history.
        MinecraftServer server = event.getPlayerList().getServer();
        for (ServerPlayer player : event.getPlayerList().getPlayers()) reconcile(player);
        // Once per data reload, include entities that predate installation or changed data.
        for (Entity entity : server.overworld().getAllEntities()) track(entity);
    }

    public static void reconcile(ServerPlayer player) {
        AdvancementHolder root = player.server.getAdvancements().get(DungeonsDataOptimizations.ROOT);
        if (root == null || player.getAdvancements().getOrStartProgress(root).isDone()) return;
        for (ResourceLocation id : DungeonsDataOptimizations.DISCOVERIES) {
            AdvancementHolder source = player.server.getAdvancements().get(id);
            if (source != null && player.getAdvancements().getOrStartProgress(source).isDone()) {
                grant(player, id);
                if (player.getAdvancements().getOrStartProgress(root).isDone()) return;
            }
        }
    }

    private static void grant(ServerPlayer player, ResourceLocation source) {
        AdvancementHolder root = player.server.getAdvancements().get(DungeonsDataOptimizations.ROOT);
        if (root == null || player.getAdvancements().getOrStartProgress(root).isDone()) return;
        Criterion<?> criterion = root.value().criteria().get(source.toString());
        // Only our converted criteria: a pack author's different criterion keeps its own trigger.
        if (criterion != null && criterion.trigger() == CriteriaTriggers.IMPOSSIBLE) {
            player.getAdvancements().award(root, source.toString());
        }
    }

    private static void join(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) track(event.getEntity());
    }

    private static void track(Entity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        // Scheduled datapack functions use the overworld command source. Preserve
        // that scope instead of extending D&T's equipment/conversion rules to other dimensions.
        if (level != level.getServer().overworld()) return;
        if (entity.getType() == EntityType.WITHER_SKELETON && entity instanceof WitherSkeleton wither
                && !wither.getTags().contains("withering")) {
            SERVERS.computeIfAbsent(level.getServer(), ignored -> new Pending()).withers.add(wither);
        } else if (entity.getType() == EntityType.LIGHTNING_BOLT && entity instanceof LightningBolt lightning) {
            SERVERS.computeIfAbsent(level.getServer(), ignored -> new Pending()).lightning.add(lightning);
        }
    }

    private static void leave(EntityLeaveLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        Pending pending = SERVERS.get(level.getServer());
        if (pending == null) return;
        // NeoForge posts this from onTrackingEnd, including a live entity's
        // section becoming hidden. Visibility returning does not post another
        // join event, so keep the candidate until actual removal or processing.
        if (event.getEntity().getRemovalReason() == null) return;
        pending.withers.remove(event.getEntity());
        pending.lightning.remove(event.getEntity());
    }

    private static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("qm_runtime_optimizations")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("dungeons_tick").executes(context -> {
                dispatch(context.getSource().getServer());
                return 1;
            })));
    }

    private static void dispatch(MinecraftServer server) {
        Pending pending = SERVERS.get(server);
        if (pending == null) return;
        // Called by the original five-tick function schedule, before entity ticking.
        // Keep its phase so a short-lived bolt isn't missed by a later Post callback.
        // Allays entering a still-live bolt's six-block radius are checked again.
        for (WitherSkeleton wither : Set.copyOf(pending.withers)) {
            if (!registered(wither, server)) {
                pending.withers.remove(wither);
                continue;
            }
            if (!visible(wither)) continue;
            pending.withers.remove(wither);
            if (!wither.getTags().contains("withering")) {
                function(wither, "nova_structures:add_withering");
            }
        }
        for (LightningBolt lightning : Set.copyOf(pending.lightning)) {
            if (!registered(lightning, server)) pending.lightning.remove(lightning);
            else if (visible(lightning)) function(lightning, "nova_structures:find_allays");
        }
        if (pending.withers.isEmpty() && pending.lightning.isEmpty()) SERVERS.remove(server);
    }

    private static boolean registered(Entity entity, MinecraftServer server) {
        // Join fires before cancellation and duplicate-UUID checks finish.
        // Only an accepted insertion installs the section-manager callback.
        // isAddedToLevel is unsuitable: tracking loss clears it, and native
        // legacy/worldgen loaders also set it after some rejected insertions.
        return !entity.isRemoved() && entity.level() == server.overworld()
            && ((EntityMembershipAccessor) entity).qm$getLevelCallback() != EntityInLevelCallback.NULL;
    }

    private static boolean visible(Entity entity) {
        // Match @e's accessible-entity scope without loading a chunk or scanning.
        return ((ServerLevel) entity.level()).getEntity(entity.getId()) == entity;
    }

    private static void function(Entity entity, String function) {
        MinecraftServer server = entity.getServer();
        if (server != null) server.getCommands().performPrefixedCommand(
            entity.createCommandSourceStack().withPermission(2).withSuppressedOutput(), "function " + function);
    }

    private static void stopped(ServerStoppedEvent event) {
        SERVERS.remove(event.getServer());
    }
}
