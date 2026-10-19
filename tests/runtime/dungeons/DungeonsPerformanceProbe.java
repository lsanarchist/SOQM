package soqm.validation.dungeons;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import qm.performance.dungeons.DungeonsDataOptimizations;

/** Test-only live registry, advancement, function and entity-event integration fixtures. */
public final class DungeonsPerformanceProbe {
    private static final ResourceLocation TRADE = ResourceLocation.parse("minecraft:give_quest_trader_trade");
    private static final ResourceLocation WITHERING = ResourceLocation.parse("nova_structures:wither_coated");
    private static final Map<String, Object> REPORT = new LinkedHashMap<>();
    private static Scenario active;

    private DungeonsPerformanceProbe() {}

    /** Starts native checks. The caller must subsequently call tick and afterReload. */
    public static Map<String, Object> run(MinecraftServer server) throws Exception {
        check(active == null, "Dungeons fixture starts once");
        REPORT.clear();
        REPORT.put("passed", false);
        verifyLoadedData(server);
        testAdvancementEvents(server);
        testBossPredicate(server);
        testTradeScope(server);
        active = new Scenario(server);
        REPORT.put("phase", "awaiting_entity_ticks");
        return REPORT;
    }

    /** Call from the test mod's ordinary ServerTickEvent.Post listener. */
    public static boolean tick(MinecraftServer server) throws Exception {
        check(active != null, "Dungeons fixture was started");
        Scenario s = active;
        if (s.stage == 3) return true;
        if (server.getTickCount() - s.since < 7) return false;
        if (s.stage == 0) {
            check(s.wither.getTags().contains("withering") && coating(server, s.wither) == 1,
                    "A newly joined overworld wither receives the original enchantment and marker");
            check(s.reloadWither.getTags().contains("withering") && coating(server, s.reloadWither) == 0,
                    "An already marked wither keeps its existing weapon unchanged");
            check(!s.netherWither.getTags().contains("withering") && coating(server, s.netherWither) == 0,
                    "Nether withers retain the original overworld-only function scope");
            check(!s.otherTypeWither.getTags().contains("withering") && coating(server, s.otherTypeWither) == 0,
                    "A WitherSkeleton subclass with another entity type is excluded like the original selector");
            check(converted(s.first), "Native lightning function converts an echo-shard allay inside six blocks");
            check(!converted(s.late) && !converted(s.outside) && !converted(s.wrongItem),
                    "Distance and held-item filters are preserved");
            check(Math.abs(s.alreadyConverted.getY() - s.origin.getY()) < 2,
                    "An already converted allay is not teleported a second time");
            // The same lightning bolt remains alive; this allay was outside the
            // first check and must be observed when it enters the radius later.
            s.late.moveTo(s.origin.getX() + 3, s.origin.getY(), s.origin.getZ());
            s.since = server.getTickCount();
            s.stage = 1;
        } else if (s.stage == 1) {
            check(converted(s.late), "A still-live lightning bolt also handles an allay arriving after its first check");
            check(!converted(s.outside) && !converted(s.wrongItem), "Repeated checks retain conversion filters");
            s.lightning.discard();
            s.afterRemoval = s.allay(2, Items.ECHO_SHARD, false);
            s.since = server.getTickCount();
            s.stage = 2;
        } else if (s.stage == 2) {
            check(!converted(s.afterRemoval), "Removing a lightning bolt stops further conversion checks");
            s.cleanup(false);
            // This loaded entity is deliberately no longer in the join queue.
            // The native data-sync event must find it once when data reloads.
            s.reloadWither.removeTag("withering");
            s.reloadWither.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            check(!s.reloadWither.getTags().contains("withering"), "Reload recovery fixture starts unprocessed");
            s.reloadPlayer = player(server, "DungeonsReloadHistory");
            AdvancementHolder source = discovery(server, 7);
            seedCompleted(s.reloadPlayer, source);
            check(!done(s.reloadPlayer, root(server)), "Historical discovery starts without the derived root");
            s.reloadPlayer.getAdvancements().save();
            online(server.getPlayerList(), s.reloadPlayer, true);
            s.stage = 3;
            REPORT.put("native_wither_enchantment_and_tag", true);
            REPORT.put("overworld_and_exact_entity_type_scope", true);
            REPORT.put("lightning_late_arrival_and_removal", true);
            REPORT.put("allay_equipment_radius_and_once_filters", true);
            REPORT.put("phase", "awaiting_reload");
            REPORT.put("passed", true);
        }
        return s.stage == 3;
    }

    /** Call after a real /reload and at least five subsequent game ticks. */
    public static void afterReload(MinecraftServer server) throws Exception {
        check(active != null && active.stage == 3, "Native entity phases completed before reload");
        try {
            verifyLoadedData(server);
            testBossPredicate(server);
            check(done(active.reloadPlayer, root(server)),
                    "Native reload reconciles an online player's previously saved discovery history");
            check(done(active.reloadPlayer, discovery(server, 7)), "Reload preserves the existing discovery itself");
            check(active.reloadWither.getTags().contains("withering") && coating(server, active.reloadWither) == 1,
                    "Native data-sync reconciliation processes an already loaded, unmarked overworld wither");
            REPORT.put("advancement_transform_valid_after_reload", true);
            REPORT.put("boss_condition_valid_after_reload", true);
            REPORT.put("reload_existing_player_history", true);
            REPORT.put("reload_existing_entity_recovery", true);
            REPORT.put("reload_passed", true);
            REPORT.put("phase", "finished");
        } finally {
            active.cleanup(true);
            if (active.reloadPlayer != null) {
                online(server.getPlayerList(), active.reloadPlayer, false);
                closePlayer(active.reloadPlayer);
            }
        }
    }

    public static Map<String, Object> results() { return REPORT; }

    private static void verifyLoadedData(MinecraftServer server) throws Exception {
        AdvancementHolder root = root(server);
        check(root.value().criteria().size() == 8, "D&T root retains all eight original criterion IDs");
        for (ResourceLocation source : DungeonsDataOptimizations.DISCOVERIES) {
            var criterion = root.value().criteria().get(source.toString());
            check(criterion != null && criterion.trigger() == CriteriaTriggers.IMPOSSIBLE,
                    "Loaded advancement criterion uses the new event-driven impossible trigger");
        }
        check(root.value().requirements().requirements().size() == 1
                && root.value().requirements().requirements().getFirst().size() == 8,
                "Original eight-way OR completion requirement is preserved");
        check(server.getWorldData().overworldData().getScheduledEvents().getEventsIds().contains("nova_structures:tick"),
                "The original five-tick scheduled dispatcher retains its game-time phase");
        var resource = server.getResourceManager().getResource(
                ResourceLocation.parse("nova_structures:function/tick.mcfunction")).orElseThrow();
        try (var reader = resource.openAsReader()) {
            List<String> commands = reader.lines().map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#")).toList();
            check(commands.equals(List.of("qm_runtime_optimizations dungeons_tick",
                            "schedule function nova_structures:tick 5t")),
                    "The active scheduled function dispatches tracked entities without global entity selectors");
        }
        REPORT.put("loaded_impossible_criteria", 8);
        REPORT.put("original_or_requirements", true);
        REPORT.put("original_five_tick_dispatch_phase", true);
        REPORT.put("no_scheduled_global_entity_selectors", true);
    }

    private static void testAdvancementEvents(MinecraftServer server) {
        for (int i = 0; i < DungeonsDataOptimizations.DISCOVERIES.size(); i++) {
            ServerPlayer player = player(server, "DungeonsEarn" + i);
            try {
                AdvancementHolder source = discovery(server, i);
                for (String criterion : source.value().criteria().keySet()) player.getAdvancements().award(source, criterion);
                check(done(player, source) && done(player, root(server)),
                        "Native advancement-earn event grants the matching root criterion for discovery " + i);
                var progress = player.getAdvancements().getOrStartProgress(root(server));
                check(progress.getCriterion(source.id().toString()).isDone(), "Original criterion ID receives credit");
            } finally { closePlayer(player); }
        }
        ServerPlayer historical = player(server, "DungeonsLoginHistory");
        try {
            AdvancementHolder source = discovery(server, 2);
            seedCompleted(historical, source);
            check(!done(historical, root(server)), "Existing-history fixture does not fire a new earned event");
            NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(historical));
            check(done(historical, root(server)), "Registered login listener reconciles existing discovery history");
            Instant first = historical.getAdvancements().getOrStartProgress(root(server)).getFirstProgressDate();
            for (String criterion : source.value().criteria().keySet()) historical.getAdvancements().revoke(source, criterion);
            NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(historical));
            check(done(historical, root(server)) && first.equals(
                    historical.getAdvancements().getOrStartProgress(root(server)).getFirstProgressDate()),
                    "Revoking source progress preserves the earned historical root and its original timestamp");
        } finally { closePlayer(historical); }
        REPORT.put("native_discovery_event_cases", 8);
        REPORT.put("login_history_and_revoked_source_preserve_root", true);
        REPORT.put("login_event_uses_native_listener_and_mock_payload_connection", true);
    }

    private static void seedCompleted(ServerPlayer player, AdvancementHolder advancement) {
        var progress = player.getAdvancements().getOrStartProgress(advancement);
        for (String criterion : advancement.value().criteria().keySet()) progress.grantProgress(criterion);
        check(progress.isDone(), "Synthetic saved-history criteria are complete");
    }

    private static void testBossPredicate(MinecraftServer server) {
        ServerLevel level = server.overworld();
        Holder<Enchantment> holder = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(net.minecraft.resources.ResourceKey.create(Registries.ENCHANTMENT,
                        DungeonsDataOptimizations.BOSS_BEHAVIOUR));
        var effects = holder.value().getEffects(EnchantmentEffectComponents.TICK);
        check(effects.size() == 1, "Original boss enchantment still has its one tick effect");
        var effect = effects.getFirst();
        TrackingPhantom phantom = new TrackingPhantom(level);
        phantom.addTag("phantom_boss");
        check(!effect.matches(Enchantment.entityContext(level, 1, phantom, phantom.position())), "Visible phantom is rejected");
        phantom.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false));
        check(effect.matches(Enchantment.entityContext(level, 1, phantom, phantom.position())), "Invisible regular phantom boss matches");
        phantom.removeTag("phantom_boss");
        phantom.addTag("phantom_boss_ominous");
        check(!effect.matches(Enchantment.entityContext(level, 1, phantom, phantom.position())), "Ominous-only phantom tag is rejected");
        phantom.removeTag("phantom_boss_ominous");
        check(!effect.matches(Enchantment.entityContext(level, 1, phantom, phantom.position())), "Untagged phantom is rejected");
        Zombie zombie = new Zombie(EntityType.ZOMBIE, level);
        zombie.addTag("phantom_boss");
        zombie.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false));
        check(!effect.matches(Enchantment.entityContext(level, 1, zombie, zombie.position())), "Other entity types are rejected");
        phantom.addTag("phantom_boss");
        ItemStack item = new ItemStack(Items.STICK);
        item.enchant(holder, 1);
        phantom.setItemSlot(EquipmentSlot.MAINHAND, item);
        check(EnchantmentHelper.getItemEnchantmentLevel(holder, item) == 1, "Native enchantment is active on the supported held item");
        for (int tick = 0; tick < 20; tick++) EnchantmentHelper.tickEffects(level, phantom);
        check(phantom.saves == 0, "The live loaded tick condition and native equipment tick path never serialize phantom NBT");
        REPORT.put("boss_condition_cases", 5);
        REPORT.put("boss_native_equipment_ticks", 20);
        REPORT.put("boss_entity_nbt_saves", phantom.saves);
    }

    private static void testTradeScope(MinecraftServer server) throws Exception {
        ServerPlayer trading = player(server, "DungeonsTradingPlayer");
        ServerPlayer other = player(server, "DungeonsOtherTrader");
        try {
            online(server.getPlayerList(), trading, true);
            online(server.getPlayerList(), other, true);
            AdvancementHolder trade = server.getAdvancements().get(TRADE);
            check(trade != null, "The native trader advancement is loaded");
            seedCompleted(trading, trade);
            seedCompleted(other, trade);
            ServerLevel nether = server.getLevel(Level.NETHER);
            check(nether != null, "Nether command context is available");
            // No artificial trader is spawned: this exercises the live reward's
            // reset command without invoking exploration-map loot generation.
            check(nether.getEntities(EntityType.VILLAGER, entity -> entity.getTags().contains("dnt_quest_trader")).isEmpty(),
                    "Isolated command context has no quest trader to generate map loot");
            server.getCommands().performPrefixedCommand(trading.createCommandSourceStack().withLevel(nether)
                    .withPermission(2).withSuppressedOutput(), "function nova_structures:choose_dnt_quest_trader");
            check(!done(trading, trade), "Loaded reward function resets its invoking player's repeatable criterion");
            check(done(other, trade), "Loaded reward function preserves another online player's criterion");
            REPORT.put("trade_reset_is_invoking_player_only", true);
        } finally {
            online(server.getPlayerList(), trading, false);
            online(server.getPlayerList(), other, false);
            closePlayer(trading);
            closePlayer(other);
        }
    }

    private static ServerPlayer player(MinecraftServer server, String name) {
        UUID id = UUID.nameUUIDFromBytes(("soqm-dungeons-fixture:" + name).getBytes(StandardCharsets.UTF_8));
        GameProfile profile = new GameProfile(id, name.substring(0, Math.min(name.length(), 16)));
        ServerPlayer player = new ServerPlayer(server, server.overworld(), profile, ClientInformation.createDefault());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        // Activate the real Connection and configure every registered mod payload
        // channel before posting the full pack's native login event. This is an
        // in-memory packet transport; it does not validate the network protocol.
        new EmbeddedChannel(connection);
        NetworkRegistry.configureMockConnection(connection);
        player.connection = new ServerGamePacketListenerImpl(server, connection, player,
                new CommonListenerCookie(profile, 0, ClientInformation.createDefault(), false, ConnectionType.NEOFORGE));
        return player;
    }

    private static void closePlayer(ServerPlayer player) {
        player.getAdvancements().stopListening();
        if (player.connection.getConnection().channel() instanceof EmbeddedChannel channel && channel.isOpen()) {
            channel.finishAndReleaseAll();
        }
    }

    /** Test-only membership lets the native @a selector observe both synthetic players. */
    @SuppressWarnings("unchecked")
    private static void online(PlayerList playerList, ServerPlayer player, boolean add) throws Exception {
        Field playersField = PlayerList.class.getDeclaredField("players");
        Field idsField = PlayerList.class.getDeclaredField("playersByUUID");
        playersField.setAccessible(true);
        idsField.setAccessible(true);
        List<ServerPlayer> players = (List<ServerPlayer>) playersField.get(playerList);
        Map<UUID, ServerPlayer> ids = (Map<UUID, ServerPlayer>) idsField.get(playerList);
        if (add) {
            check(!ids.containsKey(player.getUUID()), "Synthetic online player UUID is unused");
            players.add(player);
            ids.put(player.getUUID(), player);
        } else {
            players.remove(player);
            ids.remove(player.getUUID(), player);
        }
    }

    private static AdvancementHolder root(MinecraftServer server) {
        AdvancementHolder root = server.getAdvancements().get(DungeonsDataOptimizations.ROOT);
        check(root != null, "D&T root is loaded");
        return root;
    }

    private static AdvancementHolder discovery(MinecraftServer server, int index) {
        AdvancementHolder source = server.getAdvancements().get(DungeonsDataOptimizations.DISCOVERIES.get(index));
        check(source != null, "Original discovery is loaded");
        return source;
    }

    private static boolean done(ServerPlayer player, AdvancementHolder advancement) {
        return player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    private static int coating(MinecraftServer server, WitherSkeleton wither) {
        var holder = server.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(
                net.minecraft.resources.ResourceKey.create(Registries.ENCHANTMENT, WITHERING));
        return EnchantmentHelper.getItemEnchantmentLevel(holder, wither.getMainHandItem());
    }

    private static boolean converted(Allay allay) { return allay.getTags().contains("converted"); }

    private static final class TrackingPhantom extends Phantom {
        int saves;
        TrackingPhantom(ServerLevel level) { super(EntityType.PHANTOM, level); }
        @Override public CompoundTag saveWithoutId(CompoundTag tag) {
            saves++;
            throw new AssertionError("Boss predicate attempted full entity NBT serialization");
        }
    }

    /** Keeps one native lightning entity alive across the late-arrival fixture. */
    private static final class LongLivedLightning extends LightningBolt {
        LongLivedLightning(ServerLevel level) { super(EntityType.LIGHTNING_BOLT, level); setVisualOnly(true); }
        @Override public void tick() {}
    }

    /** Represents a third-party subclass whose registered entity type differs. */
    private static final class OtherTypeWither extends WitherSkeleton {
        OtherTypeWither(ServerLevel level) { super(EntityType.WITHER_SKELETON, level); }
        @Override public EntityType<?> getType() { return EntityType.SKELETON; }
        // Native launch applies NeoForge's access transformation to this inherited
        // method; state its widened visibility when compiling the test subclass.
        @Override protected net.minecraft.sounds.SoundEvent getStepSound() {
            return net.minecraft.sounds.SoundEvents.WITHER_SKELETON_STEP;
        }
    }

    private static final class Scenario {
        final MinecraftServer server;
        final ServerLevel level;
        final BlockPos origin;
        final List<Entity> entities = new ArrayList<>();
        final Set<UUID> priorVexes;
        final WitherSkeleton wither;
        final WitherSkeleton reloadWither;
        final WitherSkeleton netherWither;
        final WitherSkeleton otherTypeWither;
        final LongLivedLightning lightning;
        final Allay first;
        final Allay late;
        final Allay outside;
        final Allay wrongItem;
        final Allay alreadyConverted;
        Allay afterRemoval;
        ServerPlayer reloadPlayer;
        int since;
        int stage;

        Scenario(MinecraftServer server) {
            this.server = server;
            this.level = server.overworld();
            this.origin = level.getSharedSpawnPos().offset(0, 48, 0);
            level.getChunk(origin);
            priorVexes = level.getEntitiesOfClass(Vex.class, area()).stream().map(Entity::getUUID).collect(Collectors.toSet());
            wither = wither(false);
            reloadWither = wither(true);
            ServerLevel nether = server.getLevel(Level.NETHER);
            check(nether != null, "Nether level exists for dimension scope check");
            netherWither = new WitherSkeleton(EntityType.WITHER_SKELETON, nether);
            netherWither.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(netherWither, nether));
            otherTypeWither = new OtherTypeWither(level);
            otherTypeWither.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(otherTypeWither, level));
            first = allay(3, Items.ECHO_SHARD, false);
            late = allay(10, Items.ECHO_SHARD, false);
            outside = allay(7, Items.ECHO_SHARD, false);
            wrongItem = allay(2, Items.DIAMOND, false);
            alreadyConverted = allay(4, Items.ECHO_SHARD, true);
            lightning = new LongLivedLightning(level);
            lightning.moveTo(origin.getX(), origin.getY(), origin.getZ());
            check(level.addFreshEntity(lightning), "Native lightning entity joins the test level");
            entities.add(lightning);
            since = server.getTickCount();
        }

        AABB area() { return new AABB(origin).inflate(16, 8, 16); }

        WitherSkeleton wither(boolean marked) {
            WitherSkeleton wither = new WitherSkeleton(EntityType.WITHER_SKELETON, level);
            wither.setNoAi(true);
            wither.setNoGravity(true);
            wither.setInvulnerable(true);
            wither.setPersistenceRequired();
            wither.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            if (marked) wither.addTag("withering");
            wither.moveTo(origin.getX() - 8, origin.getY(), origin.getZ());
            check(level.addFreshEntity(wither), "Native wither entity joins the test level");
            entities.add(wither);
            return wither;
        }

        Allay allay(int x, net.minecraft.world.item.Item held, boolean converted) {
            Allay allay = new Allay(EntityType.ALLAY, level);
            allay.setNoAi(true);
            allay.setNoGravity(true);
            allay.setInvulnerable(true);
            allay.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(held));
            if (converted) allay.addTag("converted");
            allay.moveTo(origin.getX() + x, origin.getY(), origin.getZ());
            check(level.addFreshEntity(allay), "Native allay entity joins the test level");
            entities.add(allay);
            return allay;
        }

        void cleanup(boolean includeReloadWither) {
            for (Entity entity : entities) if (includeReloadWither || entity != reloadWither) entity.discard();
            for (Vex vex : level.getEntitiesOfClass(Vex.class, area())) if (!priorVexes.contains(vex.getUUID())) vex.discard();
            NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(netherWither, netherWither.level()));
            NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(otherTypeWither, level));
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
