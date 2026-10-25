package soqm.validation.encounters;

import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.affix.effect.DamageReductionAffix.DamageType;
import dev.shadowsoffire.apotheosis.attachments.DamageReductions;
import dev.shadowsoffire.apotheosis.loot.LootController;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import dev.shadowsoffire.apotheosis.tiers.augments.AttributeAugment;
import dev.shadowsoffire.apotheosis.tiers.augments.TierAugment;
import dev.shadowsoffire.apotheosis.tiers.augments.TierAugmentRegistry;
import io.netty.channel.embedded.EmbeddedChannel;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.tonicarter.elitemobs.EliteMobManager;
import net.tonicarter.elitemobs.affixes.AffixRegistry;
import net.tonicarter.elitemobs.affixes.AffixState;
import qm.dungeonencounters.EncounterConfig;
import qm.dungeonencounters.QmDungeonEncounters;
import qm.mobcompat.MobCompat45;

/** Native integration fixtures. Compiled only into the disposable probe server. */
@Mod("soqm_dungeon_encounter_probe")
public final class EncounterValidationProbe {
    private static final ResourceLocation FOREIGN = ResourceLocation.parse("soqm_probe:unrelated_bonus");
    private static final String CRAWLER = "divinerpg:cave_crawler";
    private static int assertions;
    private final Map<String, Object> results = new LinkedHashMap<>();
    private final Map<String, String> failures = new LinkedHashMap<>();
    private boolean awaitingReload, finished;
    private PathfinderMob reloadMob;
    private double reloadHealth, reloadDamage;
    private String reloadTier, reloadName;

    public EncounterValidationProbe() { NeoForge.EVENT_BUS.addListener(this::tick); }

    private void tick(ServerTickEvent.Post event) {
        if (finished || event.getServer().getTickCount() < 40) return;
        try {
            if (!awaitingReload) {
                MinecraftServer server = event.getServer();
                run("native_dungeon_order_and_tiers", () -> dungeonOrder(server));
                run("native_affix_damage_dispatch", () -> damageAbility(server));
                run("selection_and_exclusions", () -> selection(server));
                run("divine_bosses_and_dimensions", () -> bosses(server));
                run("player_local_budget_and_visibility", () -> scheduling(server));
                run("native_save_load_and_single_use", () -> persistence(server));
                run("dungeon_loot_keys_and_routes", () -> DungeonLootProbe.run(server));
                awaitingReload = true;
                write("awaiting_reload", false);
            } else if (Files.exists(Path.of("soqm_probe_reload_complete"))) {
                run("resource_reload_keeps_live_encounter", this::afterReload);
                if (reloadMob != null) {
                    ChunkPos chunk = reloadMob.chunkPosition();
                    reloadMob.discard();
                    ((ServerLevel) reloadMob.level()).setChunkForced(chunk.x, chunk.z, false);
                }
                finished = true;
                write("finished", failures.isEmpty());
            }
        } catch (Throwable error) {
            finished = true;
            record("orchestration", error);
            try { write("finished", false); } catch (Exception failure) { failure.printStackTrace(); }
        }
    }

    @FunctionalInterface private interface Checked { Map<String, Object> run() throws Exception; }
    private void run(String name, Checked test) {
        try { results.put(name, test.run()); } catch (Throwable error) { record(name, error); }
    }
    private void record(String name, Throwable error) {
        Throwable cause = error;
        while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
        failures.put(name, cause.toString());
        error.printStackTrace();
    }
    private void write(String phase, boolean passed) throws Exception {
        results.put("assertions", assertions);
        results.put("failures", failures);
        results.put("phase", phase);
        results.put("passed", passed);
        Files.writeString(Path.of("soqm_probe_result.json"), new GsonBuilder().setPrettyPrinting().create().toJson(results));
    }

    private static Map<String, Object> dungeonOrder(MinecraftServer server) throws Exception {
        ServerLevel level = dungeon(server);
        try (ConfigFixture config = new ConfigFixture(); Fixture f = new Fixture(level, new BlockPos(32, 80, 32))) {
            config.set(EncounterConfig.DUNGEON_CHANCE, 1.0);
            config.set(EncounterConfig.AFFIXES, List.of("regenerating"));
            check(level.players().isEmpty(), "DD pre-generation fixture has no real players in the dimension");
            double authoredBase = mob(level, CRAWLER).getAttributeBaseValue(Attributes.MAX_HEALTH);
            PathfinderMob placed = f.place(CRAWLER, false, 0);
            check(id(placed).equals(CRAWLER), "Actual DD placement retains the selected Divine entity type");
            check(QmDungeonEncounters.isPending(placed), "RETURN mixin enrolls the actual DD-spawned mob");
            check(EliteMobManager.getAffix(placed.getUUID()).isEmpty(), "No native ability is registered before a player arrives");
            close(placed.getAttributeBaseValue(Attributes.MAX_HEALTH), authoredBase * DungeonConfig.basicEnemyHealthScaling,
                    "DD's original health scaling runs before encounter activation");
            check(placed.isPersistenceRequired() && !placed.canPickUpLoot() && placed.getRestrictRadius() == 8,
                    "DD persistence, room restriction and pickup rules remain intact");
            check(placed.getTags().contains("collective.checked") && placed.getPersistentData().getBoolean(MobCompat45.SCALE_MARKER),
                    "DD authored identity is protected from earlier spawn-replacement and size handlers");

            ServerPlayer player = f.player(WorldTier.PINNACLE);
            ItemStack weapon = LootController.createLootItem(new ItemStack(Items.DIAMOND_SWORD),
                    RarityRegistry.INSTANCE.getValue(ResourceLocation.parse("apotheosis:mythic")),
                    GenContext.forPlayerAtPos(level.getRandom(), player, placed.blockPosition()));
            check(!weapon.isEmpty() && weapon.getComponents().size() > new ItemStack(Items.DIAMOND_SWORD).getComponents().size(),
                    "Fixture uses a real Apotheosis-generated item");
            placed.setItemSlot(EquipmentSlot.MAINHAND, weapon);
            ItemStack key = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("dimdungeons:item_portal_key")));
            check(!key.isEmpty(), "Native DD key registry entry exists");
            key.set(DataComponents.CUSTOM_NAME, Component.literal("Saved dungeon key"));
            placed.setItemSlot(EquipmentSlot.OFFHAND, key);
            placed.setDropChance(EquipmentSlot.OFFHAND, 1.0F);
            Map<EquipmentSlot, ItemStack> equipment = equipment(placed);
            UUID uuid = placed.getUUID();
            Object goals = field(net.minecraft.world.entity.Mob.class, "goalSelector").get(placed);
            double base = placed.getAttributeBaseValue(Attributes.MAX_HEALTH);
            placed.getAttribute(Attributes.ARMOR).addPermanentModifier(new AttributeModifier(FOREIGN, 2.0, AttributeModifier.Operation.ADD_VALUE));
            DamageReductions.Mutable foreign = new DamageReductions.Mutable(DamageReductions.EMPTY);
            foreign.set(DamageType.PHYSICAL, FOREIGN, 0.02F);
            placed.setData(Apoth.Attachments.DAMAGE_REDUCTIONS, foreign.toImmutable());
            for (TierAugment augment : TierAugmentRegistry.getAugments(WorldTier.FRONTIER, TierAugment.Target.MONSTERS)) augment.apply(level, placed);
            placed.setData(Apoth.Attachments.TIER_AUGMENTS_APPLIED, true);
            placed.setHealth(placed.getMaxHealth() * 0.75F);
            check(QmDungeonEncounters.activate(placed, player), "Nearby real player's tier activates the pending encounter");
            assertActive(placed, "regenerating");
            check(uuid.equals(placed.getUUID()) && id(placed).equals(CRAWLER)
                    && goals == field(net.minecraft.world.entity.Mob.class, "goalSelector").get(placed),
                    "Conversion retains UUID, original entity type and native AI object");
            close(placed.getAttributeBaseValue(Attributes.MAX_HEALTH), base, "Encounter does not rewrite DD's health base");
            close(placed.getHealth() / placed.getMaxHealth(), 0.75, "Activation preserves existing health fraction");
            checkEquipment(placed, equipment);
            Method dropChance = net.minecraft.world.entity.Mob.class.getDeclaredMethod("getEquipmentDropChance", EquipmentSlot.class);
            dropChance.setAccessible(true);
            close((Float) dropChance.invoke(placed, EquipmentSlot.OFFHAND), 1.0, "Guaranteed puzzle-key drop chance is preserved");
            check(placed.getAttribute(Attributes.ARMOR).hasModifier(FOREIGN), "Unrelated permanent modifier survives tier replacement");
            assertTier(placed, WorldTier.PINNACLE);
            PathfinderMob control = mob(level, CRAWLER);
            control.setData(Apoth.Attachments.DAMAGE_REDUCTIONS, foreign.toImmutable());
            for (TierAugment augment : TierAugmentRegistry.getAugments(WorldTier.PINNACLE, TierAugment.Target.MONSTERS)) augment.apply(level, control);
            check(placed.getData(Apoth.Attachments.DAMAGE_REDUCTIONS).equals(control.getData(Apoth.Attachments.DAMAGE_REDUCTIONS)),
                    "Native damage-reduction data contains exactly chosen tier plus unrelated reduction");
            double health = placed.getMaxHealth(), damage = placed.getAttributeValue(Attributes.ATTACK_DAMAGE);
            player.setData(Apoth.Attachments.WORLD_TIER, WorldTier.HAVEN);
            check(!QmDungeonEncounters.activate(placed, player), "Second player tier cannot reroll an active encounter");
            close(placed.getMaxHealth(), health, "Repeated activation cannot stack health");
            close(placed.getAttributeValue(Attributes.ATTACK_DAMAGE), damage, "Repeated activation cannot stack damage");

            PathfinderMob advanced = f.place("divinerpg:dungeon_prisoner", true, 0);
            double advancedBase = mob(level, "divinerpg:dungeon_prisoner").getAttributeBaseValue(Attributes.MAX_HEALTH);
            close(advanced.getAttributeBaseValue(Attributes.MAX_HEALTH), advancedBase * DungeonConfig.advancedEnemyHealthScaling,
                    "Advanced DD scaling also precedes the integration");
            check(data(advanced).getBoolean("advanced") && QmDungeonEncounters.activate(advanced, player),
                    "Real advanced placement retains tier provenance and activates");
            close(advanced.getAttribute(Attributes.MAX_HEALTH).getModifier(QmDungeonEncounters.HEALTH_ID).amount(),
                    EncounterConfig.ADVANCED_HEALTH_MULTIPLIER.get() - 1.0, "Advanced encounter uses its bounded modifier");
            return Map.of("real_dd_structure_spawn", true, "native_type", CRAWLER,
                    "pre_generated_then_player_activation", true, "tier_replacement_preserves_other_modifiers", true,
                    "native_gear_and_key_unchanged", true, "basic_and_advanced", true);
        }
    }

    private static Map<String, Object> damageAbility(MinecraftServer server) throws Exception {
        try (ConfigFixture config = new ConfigFixture(); Fixture f = new Fixture(dungeon(server), new BlockPos(64, 80, 32))) {
            config.set(EncounterConfig.DUNGEON_CHANCE, 1.0);
            config.set(EncounterConfig.AFFIXES, List.of("regenerating"));
            PathfinderMob undead = f.place("minecraft:zombie", false, 0);
            double originalHealth = undead.getMaxHealth();
            ServerPlayer player = f.player(WorldTier.HAVEN);
            check(!QmDungeonEncounters.activate(undead, player) && data(undead).getInt("state") == QmDungeonEncounters.DECLINED,
                    "An all-incompatible ability pool declines before adding encounter buffs");
            check(!undead.getAttribute(Attributes.MAX_HEALTH).hasModifier(QmDungeonEncounters.HEALTH_ID)
                    && !undead.getPersistentData().getBoolean(MobCompat45.ELITE_MARKER)
                    && EliteMobManager.getAffix(undead.getUUID()).isEmpty(),
                    "Declining an incompatible undead ability leaves no fake elite or partial stat conversion");
            close(undead.getMaxHealth(), originalHealth, "Incompatible ability does not alter native dungeon health");
            config.set(EncounterConfig.AFFIXES, List.of("vampiric"));
            PathfinderMob actor = f.place(CRAWLER, false, 0);
            check(QmDungeonEncounters.activate(actor, player), "Vampiric encounter activates");
            assertActive(actor, "vampiric");
            actor.setHealth(actor.getMaxHealth() * 0.5F);
            float health = actor.getHealth();
            PathfinderMob victim = EntityType.COW.create(f.level);
            check(victim != null, "Native damage target exists");
            victim.moveTo(f.origin.east(2), 0, 0);
            check(victim.hurt(f.level.damageSources().mobAttack(actor), 1.0F), "Native LivingEntity damage pipeline accepts hit");
            check(actor.getHealth() > health, "Elite Mobs native NeoForge damage listener actually dispatches Vampiric healing");
            return Map.of("registry_affix_identity", true, "actual_native_damage_event_heals", true,
                    "elite_bonus_loot_registration_absent", !EliteMobManager.isTrackedElite(actor.getUUID()),
                    "incompatible_pool_declines_before_conversion", true);
        }
    }

    private static Map<String, Object> selection(MinecraftServer server) throws Exception {
        try (ConfigFixture config = new ConfigFixture(); Fixture f = new Fixture(server.overworld(), new BlockPos(96, 200, 32))) {
            config.set(EncounterConfig.DIVINE_CHANCE, 0.35);
            config.set(EncounterConfig.AFFIXES, List.of("regenerating"));
            PathfinderMob nativeMob = mob(f.level, CRAWLER);
            nativeMob.moveTo(f.origin, 0, 0);
            boolean persistent = nativeMob.isPersistenceRequired();
            selectNextRoll(nativeMob, 0.35);
            check(QmDungeonEncounters.considerDivine(nativeMob), "Selected ordinary Divine mob enrolls without replacement");
            f.add(nativeMob);
            check(QmDungeonEncounters.activate(nativeMob, f.player(WorldTier.ASCENT)), "Real Divine mob activates outside DD");
            assertActive(nativeMob, "regenerating");
            check(nativeMob.isPersistenceRequired() == persistent,
                    "Ordinary Divine enhancement preserves its original natural-despawn policy");
            check(data(nativeMob).getString("source").equals("divine") && id(nativeMob).equals(CRAWLER),
                    "Divine encounter records source and keeps its own type");
            List<String> refused = List.of("divinerpg:jack_o_man", "divinerpg:whale", "minecraft:wither", "minecraft:warden");
            for (String id : refused) {
                Entity entity = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(id)).create(f.level);
                check(entity != null, "Excluded native entity exists: " + id);
                if (entity instanceof PathfinderMob pathfinder) {
                    check(!QmDungeonEncounters.considerDivine(pathfinder), "Boss/NPC is excluded from Divine promotion: " + id);
                }
                check(!data(entity).contains("state") && EliteMobManager.getAffix(entity.getUUID()).isEmpty(),
                        "Exclusion leaves no encounter or native ability registration: " + id);
            }
            Wolf pet = EntityType.WOLF.create(f.level);
            check(pet != null, "Pet fixture exists");
            pet.setTame(true, false);
            check(!QmDungeonEncounters.considerDivine(pet), "A tamed pet is excluded");
            for (String marker : List.of(MobCompat45.ELITE_MARKER, "apoth.boss", "apoth.miniboss")) {
                PathfinderMob existing = mob(f.level, CRAWLER);
                existing.getPersistentData().putBoolean(marker, true);
                check(!QmDungeonEncounters.considerDivine(existing), "Existing encounter marker cannot be promoted: " + marker);
            }
            PathfinderMob named = mob(f.level, CRAWLER);
            named.setCustomName(Component.literal("Existing named mob"));
            check(!QmDungeonEncounters.considerDivine(named), "Existing named Divine encounter is preserved");
            config.set(EncounterConfig.DIVINE_CHANCE, 0.0);
            PathfinderMob declined = mob(f.level, CRAWLER);
            check(!QmDungeonEncounters.considerDivine(declined), "Zero chance declines a new candidate");
            config.set(EncounterConfig.DIVINE_CHANCE, 0.35);
            selectNextRoll(declined, 0.35);
            check(!QmDungeonEncounters.considerDivine(declined) && data(declined).getInt("state") == QmDungeonEncounters.DECLINED,
                    "Previously declined candidate cannot reroll after chance changes");
            return Map.of("ordinary_divine_in_place", true, "other_boss_and_npc_ids_checked", refused,
                    "decline_is_terminal", true);
        }
    }

    private static Map<String, Object> bosses(MinecraftServer server) throws Exception {
        close(EncounterConfig.DIVINE_CHANCE.get(), 0.12, "Production ordinary Divine chance remains 12 percent");
        close(EncounterConfig.DIVINE_BOSS_CHANCE.get(), 0.85, "Production native Divine boss chance is 85 percent");
        List<String> bossIds = List.of("divinerpg:ancient_entity", "divinerpg:the_watcher", "divinerpg:kitra");
        Map<String, Object> observedHealth = new LinkedHashMap<>();
        try (ConfigFixture config = new ConfigFixture(); Fixture f = new Fixture(server.overworld(), new BlockPos(160, 200, 64))) {
            config.set(EncounterConfig.AFFIXES, List.of("regenerating"));
            ServerPlayer player = f.player(WorldTier.SUMMIT);
            for (String id : bossIds) {
                boolean undead = id.equals("divinerpg:kitra");
                config.set(EncounterConfig.AFFIXES, undead ? List.of("regenerating", "vampiric") : List.of("regenerating"));
                PathfinderMob boss = mob(f.level, id);
                boss.moveTo(f.origin, 0, 0);
                boss.setCustomName(Component.literal("Authored boss " + id));
                boss.setPersistenceRequired();
                UUID uuid = boss.getUUID();
                Class<?> nativeClass = boss.getClass();
                Object goals = boss.goalSelector, targets = boss.targetSelector;
                Object bar = bossBar(boss);
                var loot = boss.getLootTable();
                double baseHp = boss.getAttributeBaseValue(Attributes.MAX_HEALTH);
                float beforeMaxHealth = boss.getMaxHealth();
                if (undead) check(!boss.canBeAffected(new MobEffectInstance(MobEffects.REGENERATION, 100)),
                        "Pinned aquatic boss has native undead regeneration immunity");
                selectNextRoll(boss, 0.85);
                check(QmDungeonEncounters.considerDivine(boss), "Named persistent native Divine boss qualifies: " + id);
                f.add(boss);
                check(QmDungeonEncounters.activate(boss, player), "Selected native Divine boss activates: " + id);
                assertActive(boss, undead ? "vampiric" : "regenerating");
                check(boss.getUUID().equals(uuid) && boss.getClass() == nativeClass && id(boss).equals(id),
                        "Boss UUID, native class and registry type are retained: " + id);
                check(boss.goalSelector == goals && boss.targetSelector == targets && bossBar(boss) == bar,
                        "Native boss AI selectors and boss-bar object are retained: " + id);
                check(boss.getLootTable().equals(loot), "Native boss loot table is retained: " + id);
                check(boss.isPersistenceRequired(), "Authored persistent boss keeps its native despawn policy: " + id);
                close(boss.getAttributeBaseValue(Attributes.MAX_HEALTH), baseHp, "Boss authored health base remains intact: " + id);
                check(boss.getMaxHealth() > beforeMaxHealth, "Boss health is measurably strengthened under actual attribute limits: " + id);
                observedHealth.put(id, Map.of("before", beforeMaxHealth, "after", boss.getMaxHealth()));
                check(boss.getName().getString().contains("Authored boss " + id), "Existing boss name remains in its new display: " + id);
                assertTier(boss, WorldTier.SUMMIT);
                boss.setHealth(boss.getMaxHealth() * 0.4F);
                if (undead) {
                    float before = boss.getHealth();
                    PathfinderMob victim = EntityType.COW.create(f.level);
                    check(victim != null && victim.hurt(f.level.damageSources().mobAttack(boss), 1.0F),
                            "Native aquatic boss damage event reaches upstream affix dispatch");
                    check(boss.getHealth() > before && !boss.canBeAffected(new MobEffectInstance(MobEffects.REGENERATION, 100)),
                            "Compatible Vampiric heals the aquatic boss while its original undead immunity stays intact");
                } else {
                    EliteMobManager.tickAffixes(f.level);
                    check(boss.hasEffect(MobEffects.REGENERATION), "Native Elite ability executes on this boss hierarchy: " + id);
                }
                // The same boss classes remain excluded from the curated DD path.
                PathfinderMob dungeonBoss = mob(dungeon(server), id);
                check(!QmDungeonEncounters.enrollDungeon(dungeonBoss, true, 0) && !data(dungeonBoss).contains("state"),
                        "Divine boss is not silently added to DD's ordinary encounter path: " + id);
            }
            config.set(EncounterConfig.DIVINE_BOSS_CHANCE, 0.0);
            PathfinderMob refused = mob(f.level, "divinerpg:ancient_entity");
            check(!QmDungeonEncounters.considerDivine(refused) && data(refused).getInt("state") == QmDungeonEncounters.DECLINED,
                    "Separate zero boss chance declines while ordinary policy stays unchanged");
            config.set(EncounterConfig.DIVINE_BOSS_CHANCE, 0.85);
            selectNextRoll(refused, 0.85);
            check(!QmDungeonEncounters.considerDivine(refused), "Declined boss is not rerolled after policy changes");
            List<String> dimensions = new ArrayList<>();
            for (ServerLevel level : server.getAllLevels()) {
                if (!level.dimension().location().getNamespace().equals("divinerpg")) continue;
                PathfinderMob boss = mob(level, "divinerpg:ancient_entity");
                selectNextRoll(boss, 0.85);
                check(QmDungeonEncounters.considerDivine(boss), "Native boss selection applies in its registered Divine dimension");
                PathfinderMob ordinary = mob(level, CRAWLER);
                selectNextRoll(ordinary, 0.12);
                check(QmDungeonEncounters.considerDivine(ordinary), "Ordinary hostile selection applies in registered Divine dimension");
                dimensions.add(level.dimension().location().toString());
            }
            check(dimensions.contains("divinerpg:arcana") && dimensions.size() >= 6,
                    "Coverage includes the loaded Divine dimension family, including Arcana");
            return Map.of("native_boss_hierarchies", bossIds, "boss_probability", 0.85, "ordinary_probability", 0.12,
                    "overworld_native_boss_activation", true, "divine_dimensions_with_both_policies", dimensions,
                    "original_boss_ai_bars_and_loot_retained", true, "dungeon_boss_exclusion_retained", true,
                    "native_boss_health_observed", observedHealth, "undead_boss_keeps_immunity_and_gets_working_affix", true);
        }
    }

    private static Object bossBar(PathfinderMob boss) throws Exception {
        for (Class<?> owner = boss.getClass(); owner != null; owner = owner.getSuperclass()) {
            for (Field field : owner.getDeclaredFields()) {
                if (net.minecraft.server.level.ServerBossEvent.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    return field.get(boss);
                }
            }
        }
        throw new AssertionError("Fixture boss has no native boss bar: " + id(boss));
    }

    private static Map<String, Object> scheduling(MinecraftServer server) throws Exception {
        try (ConfigFixture config = new ConfigFixture(); Fixture f = new Fixture(dungeon(server), new BlockPos(96, 80, 64))) {
            config.set(EncounterConfig.DUNGEON_CHANCE, 1.0);
            config.set(EncounterConfig.AFFIXES, List.of("regenerating"));
            config.set(EncounterConfig.ACTIVATION_BUDGET, 2);
            List<PathfinderMob> pending = new ArrayList<>();
            for (int i = 0; i < 5; i++) pending.add(f.place(CRAWLER, false, 0));
            ServerPlayer player = f.player(WorldTier.SUMMIT);
            player.moveTo(f.origin.east(128), 0, 0);
            int chunks = f.level.getChunkSource().getLoadedChunksCount();
            check(QmDungeonEncounters.activateNearby(player) == 0, "Distant player cannot activate queued rooms");
            check(f.level.getChunkSource().getLoadedChunksCount() == chunks, "Activation query does not load remote chunks");
            player.moveTo(f.origin, 0, 0);
            var fake = FakePlayerFactory.get(f.level, new GameProfile(UUID.randomUUID(), "EncounterFake"));
            fake.moveTo(f.origin, 0, 0);
            check(QmDungeonEncounters.activateNearby(fake) == 0, "FakePlayer cannot supply encounter progression");
            int interval = EncounterConfig.ACTIVATION_INTERVAL.get();
            player.tickCount = Math.floorMod(player.getUUID().hashCode(), interval);
            NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(player));
            check(pending.stream().filter(m -> EliteMobManager.getAffix(m.getUUID()).isPresent()).count() == 2,
                    "Registered player-tick event obeys the per-pass activation budget");
            check(QmDungeonEncounters.activateNearby(player) == 2 && QmDungeonEncounters.activateNearby(player) == 1,
                    "Remaining pending mobs activate in later bounded passes");
            PathfinderMob actor = pending.getFirst();
            AffixState before = EliteMobManager.affixStateFor(actor.getUUID()).orElseThrow();
            int[] joins = {0};
            Consumer<EntityJoinLevelEvent> listener = event -> { if (event.getEntity() == actor) joins[0]++; };
            NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, EntityJoinLevelEvent.class, listener);
            try {
                f.manager.updateChunkStatus(f.chunk, Visibility.HIDDEN);
                check(!actor.isRemoved(), "Native visibility loss is not entity removal");
                f.manager.updateChunkStatus(f.chunk, Visibility.TRACKED);
                check(joins[0] == 0, "Native visibility restore emits no entity-join event");
                check(EliteMobManager.affixStateFor(actor.getUUID()).orElseThrow() == before,
                        "Ability registration survives visibility hide/show without a second join");
            } finally { NeoForge.EVENT_BUS.unregister(listener); }
            actor.discard();
            check(EliteMobManager.getAffix(actor.getUUID()).isEmpty(), "Real entity removal immediately retires native ability state");
            return Map.of("per_pass_budget", 2, "mobs_activated", 5, "fake_and_distant_players_rejected", true,
                    "native_visibility_round_trip", true, "actual_removal_cleanup", true);
        }
    }

    private Map<String, Object> persistence(MinecraftServer server) throws Exception {
        try (ConfigFixture config = new ConfigFixture(); Fixture f = new Fixture(dungeon(server), new BlockPos(128, 80, 64))) {
            config.set(EncounterConfig.DUNGEON_CHANCE, 1.0);
            config.set(EncounterConfig.AFFIXES, List.of("regenerating"));
            PathfinderMob actor = f.place(CRAWLER, false, 0);
            check(QmDungeonEncounters.activate(actor, f.player(WorldTier.PINNACLE)), "Persistence fixture activates");
            actor.setHealth(actor.getMaxHealth() * 0.4F);
            EliteMobManager.tickAffixes(f.level);
            check(actor.hasEffect(MobEffects.REGENERATION) && actor.hasEffect(MobEffects.DAMAGE_RESISTANCE),
                    "Upstream manager tick actually executes Regenerating's ability");
            AffixState state = EliteMobManager.affixStateFor(actor.getUUID()).orElseThrow();
            check(state.hasFired("regenerating_second_wind"), "Upstream one-shot flag was consumed by the real ability");
            state.setCooldown("soqm_probe_cooldown", 73);
            actor.removeEffect(MobEffects.REGENERATION);
            actor.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            double health = actor.getMaxHealth(), damage = actor.getAttributeValue(Attributes.ATTACK_DAMAGE);
            String name = actor.getName().getString(), tier = data(actor).getString("worldTier");
            UUID uuid = actor.getUUID();
            for (int i = 0; i < 2; i++) {
                actor = f.roundTrip(actor);
                assertActive(actor, "regenerating");
                AffixState restored = EliteMobManager.affixStateFor(uuid).orElseThrow();
                check(restored.mob == actor && restored.hasFired("regenerating_second_wind")
                        && restored.getCooldown("soqm_probe_cooldown") == 73,
                        "Native saved ability state restores to the new entity instance");
                EliteMobManager.tickAffixes(f.level);
                check(!actor.hasEffect(MobEffects.REGENERATION), "Used Regenerating ability cannot fire again after load");
                close(actor.getMaxHealth(), health, "Native load does not stack health modifiers");
                close(actor.getAttributeValue(Attributes.ATTACK_DAMAGE), damage, "Native load does not stack damage modifiers");
                check(actor.getName().getString().equals(name) && data(actor).getString("worldTier").equals(tier),
                        "Name and selected tier do not reroll on load");
                assertTier(actor, WorldTier.PINNACLE);
            }
            // A second ability proves onSpawn is not rerun: Shielded's initial
            // absorption must not refill merely because the entity was loaded.
            config.set(EncounterConfig.AFFIXES, List.of("shielded"));
            PathfinderMob shielded = f.place(CRAWLER, false, 0);
            check(QmDungeonEncounters.activate(shielded, f.player(WorldTier.HAVEN)), "Shielded fixture activates");
            check(shielded.getAbsorptionAmount() > 0, "Real Shielded onSpawn grants initial absorption");
            shielded.setAbsorptionAmount(1.0F);
            shielded = f.roundTrip(shielded);
            close(shielded.getAbsorptionAmount(), 1.0, "Loading does not rerun native onSpawn or refill absorption");

            actor.setHealth(actor.getMaxHealth());
            reloadMob = actor;
            reloadHealth = health;
            reloadDamage = damage;
            reloadTier = tier;
            reloadName = name;
            f.keep(actor);
            return Map.of("native_nbt_and_loaded_from_disk_join_cycles", 2, "saved_cooldown", 73,
                    "actual_one_shot_ability_remains_consumed", true, "native_on_spawn_not_repeated", true);
        }
    }

    private Map<String, Object> afterReload() {
        check(reloadMob != null && reloadMob.isAlive(), "Prepared encounter in a forced fixture chunk survives actual data reload"
                + (reloadMob == null ? " (missing fixture)" : " (removal=" + reloadMob.getRemovalReason() + ")"));
        assertActive(reloadMob, "regenerating");
        close(reloadMob.getMaxHealth(), reloadHealth, "Data reload cannot stack health");
        close(reloadMob.getAttributeValue(Attributes.ATTACK_DAMAGE), reloadDamage, "Data reload cannot stack damage");
        check(data(reloadMob).getString("worldTier").equals(reloadTier) && reloadMob.getName().getString().equals(reloadName),
                "Data reload preserves the saved tier and encounter name");
        check(EliteMobManager.affixStateFor(reloadMob.getUUID()).orElseThrow().hasFired("regenerating_second_wind"),
                "Data reload preserves the live one-shot ability state");
        return Map.of("actual_resource_reload", true, "live_native_affix_retained", true, "no_stat_or_name_stacking", true);
    }

    private static void assertActive(PathfinderMob mob, String affix) {
        check(data(mob).getInt("state") == QmDungeonEncounters.ACTIVE, "Encounter reached active state");
        AffixState state = EliteMobManager.affixStateFor(mob.getUUID()).orElseThrow();
        check(state.mob == mob && state.affix == AffixRegistry.byId(affix).orElseThrow(),
                "Native manager holds the original mob and the exact registered upstream ability");
        check(!EliteMobManager.isTrackedElite(mob.getUUID()), "Integration does not enroll the mob in bonus Elite loot/boss encounters");
        check(mob.getAttribute(Attributes.MAX_HEALTH).hasModifier(QmDungeonEncounters.HEALTH_ID),
                "Bounded named health modifier is installed");
        if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) check(mob.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(QmDungeonEncounters.DAMAGE_ID),
                "Bounded damage modifier is installed when the native entity exposes that attribute");
    }

    private static void assertTier(PathfinderMob mob, WorldTier chosen) {
        int nativeAttributes = 0;
        for (WorldTier tier : WorldTier.values()) {
            for (TierAugment augment : TierAugmentRegistry.getAugments(tier, TierAugment.Target.MONSTERS)) {
                if (augment instanceof AttributeAugment attribute) {
                    var instance = mob.getAttribute(attribute.modifier().attribute());
                    if (instance != null) {
                        check(instance.hasModifier(attribute.id()) == (tier == chosen), "Exactly one native tier's attribute IDs are installed");
                        if (tier == chosen) nativeAttributes++;
                    }
                }
            }
        }
        check(chosen == WorldTier.HAVEN || nativeAttributes > 0, "Fixture exercises loaded Apotheosis native tier data");
        check(mob.getData(Apoth.Attachments.TIER_AUGMENTS_APPLIED), "Native tier attachment prevents subsequent automatic application");
    }

    private static CompoundTag data(Entity entity) { return entity.getPersistentData().getCompound(QmDungeonEncounters.DATA_KEY); }
    private static String id(Entity entity) { return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString(); }
    private static ServerLevel dungeon(MinecraftServer server) {
        ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse("dimdungeons:dungeon_dimension")));
        check(level != null, "The real Dimensional Dungeons dimension is registered");
        return level;
    }
    private static PathfinderMob mob(ServerLevel level, String id) {
        Entity entity = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(id)).create(level);
        check(entity instanceof PathfinderMob, "Pinned registry contains the expected native pathfinder: " + id);
        return (PathfinderMob) entity;
    }
    private static void selectNextRoll(PathfinderMob mob, double chance) {
        for (long seed = 0; seed < 100000; seed++) {
            if (RandomSource.create(seed).nextDouble() < chance) { mob.getRandom().setSeed(seed); return; }
        }
        throw new AssertionError("Could not seed a selected candidate");
    }
    private static Map<EquipmentSlot, ItemStack> equipment(PathfinderMob mob) {
        Map<EquipmentSlot, ItemStack> saved = new LinkedHashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) saved.put(slot, mob.getItemBySlot(slot).copy());
        return saved;
    }
    private static void checkEquipment(PathfinderMob mob, Map<EquipmentSlot, ItemStack> saved) {
        saved.forEach((slot, item) -> check(ItemStack.isSameItemSameComponents(item, mob.getItemBySlot(slot))
                && item.getCount() == mob.getItemBySlot(slot).getCount(), "Native gear/key components and counts remain intact in " + slot));
    }
    private static Field field(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
    private static void close(double actual, double expected, String message) {
        check(Math.abs(actual - expected) <= Math.max(0.0001, Math.abs(expected) * 0.00001), message + " (" + actual + " vs " + expected + ")");
    }
    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }

    private static final class ConfigFixture implements AutoCloseable {
        private final Map<ModConfigSpec.ConfigValue<?>, Object> saved = new LinkedHashMap<>();
        @SuppressWarnings({"rawtypes", "unchecked"})
        <T> void set(ModConfigSpec.ConfigValue<T> key, T value) { saved.putIfAbsent(key, key.get()); ((ModConfigSpec.ConfigValue) key).set(value); }
        @Override @SuppressWarnings({"rawtypes", "unchecked"})
        public void close() { saved.forEach((key, value) -> ((ModConfigSpec.ConfigValue) key).set(value)); }
    }

    private static final class Fixture implements AutoCloseable {
        final ServerLevel level;
        final BlockPos origin;
        final ChunkPos chunk;
        final PersistentEntitySectionManager<Entity> manager;
        final Visibility previous;
        final List<Entity> entities = new ArrayList<>();
        final List<ServerPlayer> players = new ArrayList<>();
        boolean keepChunk;

        @SuppressWarnings("unchecked") Fixture(ServerLevel level, BlockPos origin) throws Exception {
            this.level = level;
            this.origin = origin;
            chunk = new ChunkPos(origin);
            level.getChunk(chunk.x, chunk.z);
            manager = (PersistentEntitySectionManager<Entity>) field(ServerLevel.class, "entityManager").get(level);
            Long2ObjectMap<Visibility> states = (Long2ObjectMap<Visibility>) field(PersistentEntitySectionManager.class, "chunkVisibility").get(manager);
            previous = states.get(chunk.toLong());
            manager.updateChunkStatus(chunk, Visibility.TRACKED);
            for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) level.setBlock(origin.offset(x, -1, z), Blocks.STONE.defaultBlockState(), 2);
        }

        void add(PathfinderMob mob) {
            mob.setNoAi(true);
            check(level.addFreshEntity(mob), "Native entity manager accepts fixture mob");
            entities.add(mob);
        }

        PathfinderMob place(String id, boolean advanced, int theme) throws Exception {
            Method method = DungeonPlacement.class.getDeclaredMethod("spawnEnemyHere", BlockPos.class, String.class,
                    ServerLevel.class, int.class, DungeonType.class);
            method.setAccessible(true);
            Entity entity = (Entity) method.invoke(null, origin, id, level, theme, advanced ? DungeonType.ADVANCED : DungeonType.BASIC);
            check(entity instanceof PathfinderMob, "Actual DD private placement returned a living hostile");
            PathfinderMob mob = (PathfinderMob) entity;
            mob.setNoAi(true);
            entities.add(mob);
            return mob;
        }

        ServerPlayer player(WorldTier tier) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "EncounterProbe");
            ServerPlayer player = new ServerPlayer(level.getServer(), level, profile, ClientInformation.createDefault());
            Connection connection = new Connection(PacketFlow.SERVERBOUND);
            new EmbeddedChannel(connection);
            player.connection = new ServerGamePacketListenerImpl(level.getServer(), connection, player,
                    CommonListenerCookie.createInitial(profile, false)) {
                @Override public void send(Packet<?> packet) { }
                @Override public void send(Packet<?> packet, PacketSendListener listener) { }
            };
            player.moveTo(origin.east(1), 0, 0);
            player.setGameMode(GameType.SURVIVAL);
            player.setData(Apoth.Attachments.WORLD_TIER, tier);
            level.addNewPlayer(player);
            players.add(player);
            return player;
        }

        PathfinderMob roundTrip(PathfinderMob original) throws Exception {
            CompoundTag saved = new CompoundTag();
            check(original.save(saved), "Native Entity.save writes complete entity NBT");
            UUID uuid = original.getUUID();
            original.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
            check(EliteMobManager.getAffix(uuid).isEmpty(), "Native unload retires the old instance's ability registration");
            Entity loaded = EntityType.loadEntityRecursive(saved, level, entity -> entity);
            check(loaded instanceof PathfinderMob && loaded.getUUID().equals(uuid), "Native NBT loader retains UUID and mob type");
            Method add = PersistentEntitySectionManager.class.getDeclaredMethod("addEntity", EntityAccess.class, boolean.class);
            add.setAccessible(true);
            check((Boolean) add.invoke(manager, loaded, true), "Native loaded-from-disk entity-manager path accepts saved mob");
            entities.add(loaded);
            return (PathfinderMob) loaded;
        }

        void keep(Entity entity) {
            entities.remove(entity);
            level.setChunkForced(chunk.x, chunk.z, true);
            keepChunk = true;
        }

        @Override public void close() {
            for (Entity entity : entities) if (!entity.isRemoved()) entity.discard();
            for (ServerPlayer player : players) level.removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
            if (!keepChunk) manager.updateChunkStatus(chunk, previous == null ? Visibility.HIDDEN : previous);
        }
    }
}
