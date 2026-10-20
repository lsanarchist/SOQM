package soqm.validation.villageguard;

import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.millenaire.building.BuildingId;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlan;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.CultureLoader;
import org.millenaire.culture.VillageType;
import org.millenaire.culture.VillagerType;
import org.millenaire.entity.MillVillager;
import org.millenaire.entity.ModEntities;
import org.millenaire.village.Village;
import org.millenaire.village.VillageId;
import org.millenaire.village.VillageManager;
import org.millenaire.village.VillageSavedData;
import qm.villageguard.GeometryChangeSource;
import qm.villageguard.GuardedBrain;
import qm.villageguard.QmVillageGuard;
import qm.villageguard.VillageProtectionIndex;
import qm.villageguard.VillageIndexAccess;

/** Native regression mod, compiled only into the disposable probe server. */
@Mod("soqm_village_guard_probe")
public final class VillageGuardProbe {
    private static final BlockPos FAR = new BlockPos(-16385, 80, -16385);
    private static int assertions;
    private final Map<String, Object> results = new LinkedHashMap<>();
    private final Map<String, String> failures = new LinkedHashMap<>();
    private boolean awaitingReload;
    private boolean finished;
    private int beforeReloadRebuilds;
    private VillageProtectionIndex beforeReloadIndex;
    private Map<ResourceLocation, VillageType> beforeReloadTypes;
    private Map<ResourceLocation, BuildingPlan> beforeReloadPlans;
    private VillageManager reloadManager;
    private VillageProtectionIndex reloadGeometry;
    private BlockPos reloadBuildingPoint;
    private int beforeReloadGeometryRebuilds;

    public VillageGuardProbe() { NeoForge.EVENT_BUS.addListener(this::tick); }

    private void tick(ServerTickEvent.Post event) {
        if (finished || event.getServer().getTickCount() < 20) return;
        MinecraftServer server = event.getServer();
        try {
            if (!awaitingReload) {
                run("incremental_geometry", () -> geometry(server));
                run("culture_geometry", () -> cultures(server));
                run("target_channels", () -> targets(server));
                run("damage_and_grief_policy", () -> damageAndGrief(server));
                run("sparse_work_counts", () -> workCounts(server));
                run("native_visibility_lifecycle", () -> visibility(server));
                run("level_teardown", () -> teardown(server));
                prepareReload();
                beforeReloadIndex = VillageProtectionIndex.get(server.overworld());
                beforeReloadRebuilds = index(server.overworld(), "full_rebuilds");
                awaitingReload = true;
                write("awaiting_reload", false);
            } else if (Files.exists(Path.of("soqm_probe_reload_complete"))) {
                run("resource_reload", () -> afterReload(server));
                finished = true;
                write("finished", failures.isEmpty());
            }
        } catch (Throwable failure) {
            finished = true;
            recordFailure("orchestration", failure);
            try { write("finished", false); }
            catch (Exception outputFailure) { outputFailure.printStackTrace(); }
        }
    }

    @FunctionalInterface
    private interface Checked { Map<String, Object> get() throws Exception; }

    private void run(String name, Checked fixture) {
        try { results.put(name, fixture.get()); }
        catch (Throwable failure) { recordFailure(name, failure); }
    }

    private void recordFailure(String name, Throwable failure) {
        Throwable cause = failure;
        while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
        failures.put(name, cause.toString());
        failure.printStackTrace();
    }

    private void write(String phase, boolean passed) throws Exception {
        results.put("assertions", assertions);
        results.put("failures", failures);
        results.put("phase", phase);
        results.put("passed", passed);
        Files.writeString(Path.of("soqm_probe_result.json"),
                new GsonBuilder().setPrettyPrinting().create().toJson(results));
    }

    private static Map<String, Object> geometry(MinecraftServer server) throws Exception {
        ServerLevel level = server.overworld();
        try (Fixture f = new Fixture(level, FAR)) {
            for (int i = 0; i < 64; i++) f.addVillage(FAR.offset(i * 1024, 0, 65536), f.type);
            int loaded = level.getChunkSource().getLoadedChunksCount();
            check(level.getChunkSource().getChunkNow(FAR.getX() >> 4, FAR.getZ() >> 4) == null,
                    "Geometry fixture begins in an unloaded distant chunk");
            check(protectedAt(level, FAR), "Registered settlement protects its center");
            check(protectedAt(level, FAR.offset(16, 0, 0)), "Circular radius boundary is inclusive");
            check(!protectedAt(level, FAR.offset(17, 0, 0)), "Position just outside radius is unprotected");
            check(!protectedAt(level, FAR.offset(16, 0, 16)), "Circle does not protect square corners");
            check(protectedAt(level, FAR.below(24)) && protectedAt(level, FAR.above(64)),
                    "Circle vertical limits are inclusive");
            check(!protectedAt(level, FAR.below(25)) && !protectedAt(level, FAR.above(65)),
                    "Circle does not extend above or below configured vertical limits");

            BuildingInstance townhall = f.building(FAR, Rotation.NONE, f.plan);
            f.village.addBuilding(townhall);
            int footprintSamples = 0;
            for (Rotation rotation : Rotation.values()) {
                BlockPos origin = FAR.offset(512 + 64 * rotation.ordinal(), 0, 0);
                BuildingInstance building = f.building(origin, rotation, f.plan);
                f.village.addBuilding(building);
                for (int x = -10; x <= 10; x++) for (int z = -10; z <= 10; z++) {
                    for (int y : new int[]{-5, -4, 0, 6, 7}) {
                        BlockPos pos = origin.offset(x, y, z);
                        boolean expected = building.containsPos(pos) && y >= -4 && y <= 6;
                        check(protectedAt(level, pos) == expected,
                                "Native rotated building footprint and vertical buffer agree: " + rotation + " " + pos);
                        footprintSamples++;
                    }
                }
                check(listeners(building) == 1, "Each building has one owning index listener");
            }
            int full = index(level, "full_rebuilds");
            int rebuilds = index(level, "village_rebuilds");
            long gameTime = level.getGameTime();
            try {
                ((ServerLevelData) level.getLevelData()).setGameTime(gameTime + 20000);
                for (int i = 0; i < 4096; i++) protectedAt(level, FAR.offset((i & 63) - 32, 0, 0));
            } finally {
                ((ServerLevelData) level.getLevelData()).setGameTime(gameTime);
            }
            check(index(level, "full_rebuilds") == full && index(level, "village_rebuilds") == rebuilds,
                    "4096 lookups after 20,000 game ticks rebuild no unchanged village");

            BuildingInstance moving = f.building(FAR.offset(2048, 0, 0), Rotation.NONE, f.plan);
            f.village.addBuilding(moving);
            check(protectedAt(level, moving.getOrigin()), "Building addition is visible immediately");
            check(index(level, "village_rebuilds") == ++rebuilds, "Adding a building rebuilds only its village");
            BlockPos oldOrigin = moving.getOrigin();
            moving.setOrigin(oldOrigin.offset(128, 0, 0));
            moving.setOrigin(oldOrigin.offset(256, 0, 0));
            check(!protectedAt(level, oldOrigin) && protectedAt(level, moving.getOrigin()),
                    "Coalesced moves remove the old footprint and protect the final location");
            check(index(level, "village_rebuilds") == ++rebuilds, "Two geometry mutations coalesce to one village rebuild");
            moving.startUpgrade(f.upgrade.id(), 1);
            check(protectedAt(level, moving.getOrigin().offset(16, 0, 0)),
                    "Native startUpgrade immediately expands the protection footprint");
            check(index(level, "village_rebuilds") == ++rebuilds, "Upgrade rebuilds only one village");
            check(f.village.forgetBuilding(null, moving), "Native non-townhall removal succeeds");
            check(!protectedAt(level, moving.getOrigin()), "Removed building loses protection immediately");
            check(index(level, "village_rebuilds") == ++rebuilds && listeners(moving) == 0,
                    "Building removal detaches its listener and rebuilds once");
            moving.setOrigin(oldOrigin);
            protectedAt(level, oldOrigin);
            check(index(level, "village_rebuilds") == rebuilds, "Detached building changes cannot dirty its former village");
            check(index(level, "full_rebuilds") == full, "Local geometry changes never rebuild all 65 villages");

            Village replacement = new Village(f.village.getId(), f.type.culture(), f.type.id(), FAR.offset(8192, 0, 0));
            int count = f.manager.getAllVillages().size();
            f.manager.addVillage(replacement);
            check(f.manager.getAllVillages().size() == count, "Replacement keeps village count unchanged");
            check(!protectedAt(level, FAR) && protectedAt(level, replacement.getCenter()),
                    "Same-ID same-count replacement is immediately reflected in queries");
            check(listeners(f.village) == 0 && listeners(townhall) == 0 && listeners(replacement) == 1,
                    "Replacement releases all old village and building listeners");
            rebuilds = index(level, "village_rebuilds");
            f.manager.addVillage(replacement);
            protectedAt(level, replacement.getCenter());
            check(index(level, "village_rebuilds") == rebuilds, "Re-adding the identical instance is a no-op");
            f.manager.removeVillage(replacement.getId());
            check(!protectedAt(level, replacement.getCenter()) && listeners(replacement) == 0,
                    "Village removal immediately removes geometry and ownership");
            f.manager.clear();
            check(index(level, "registered") == 0 && index(level, "villages") == 0 && index(level, "cells") == 0,
                    "Manager clear leaves no retained entry or spatial cell");
            check(index(level, "full_rebuilds") == full, "Empty-manager queries do not force another bootstrap");
            check(level.getChunkSource().getLoadedChunksCount() == loaded
                            && level.getChunkSource().getChunkNow(FAR.getX() >> 4, FAR.getZ() >> 4) == null,
                    "All geometry registration, updates, removals and lookups load zero chunks");
            return Map.of("rotated_footprint_samples", footprintSamples, "steady_lookups", 4096,
                    "simulated_game_time_gap", 20000, "unchanged_villages", 64,
                    "steady_full_rebuilds", 0, "steady_village_rebuilds", 0,
                    "chunk_loads", 0, "same_count_replacement", true, "listener_cleanup", true);
        }
    }

    private static Map<String, Object> cultures(MinecraftServer server) throws Exception {
        ServerLevel level = server.overworld();
        try (Fixture f = new Fixture(level, FAR)) {
            BuildingInstance building = f.building(FAR.offset(512, 0, 0), Rotation.NONE, f.plan);
            f.village.addBuilding(building);
            check(!protectedAt(level, FAR.offset(24, 0, 0)), "Original village type has radius 16");
            int full = index(level, "full_rebuilds");
            ModCultures.registerVillageType(copy(f.type, Map.of("radius", 32)));
            check(protectedAt(level, FAR.offset(24, 0, 0)), "Replacing a village type updates existing villages");
            check(index(level, "full_rebuilds") == ++full, "One culture mutation produces one lazy full reindex");
            ModCultures.unregisterVillageType(f.type.id());
            check(!protectedAt(level, FAR), "Unregistered village type immediately loses protection");
            check(index(level, "full_rebuilds") == ++full, "Type removal invalidates the geometry epoch");
            ModCultures.registerVillageType(f.type);
            check(protectedAt(level, FAR), "Restored village type protects existing registered village again");
            check(index(level, "full_rebuilds") == ++full, "Type restoration reindexes once");
            building.containsPos(building.getOrigin()); // Populate the upstream object's dimension cache first.
            BuildingPlan wider = copy(f.plan, Map.of("width", 17, "height", 12));
            ModCultures.registerBuildingPlan(wider);
            check(protectedAt(level, building.getOrigin().offset(16, 12, 0)),
                    "Same-plan-ID content replacement uses new dimensions despite upstream dimension cache");
            check(index(level, "full_rebuilds") == ++full, "Plan replacement invalidates geometry once");
            ModCultures.unregisterBuildingPlan(f.plan.id());
            check(!protectedAt(level, building.getOrigin()), "Removing a plan removes its existing building footprint");
            check(protectedAt(level, FAR), "Removing a building plan retains the village circle");
            check(index(level, "full_rebuilds") == ++full, "Plan removal invalidates geometry");
            ModCultures.registerBuildingPlan(f.plan);
            ModCultures.registerVillageType(f.type);
            protectedAt(level, FAR);
            check(index(level, "full_rebuilds") == ++full, "Several culture writes before a query coalesce into one reindex");
            return Map.of("type_replace_remove_restore", true, "plan_same_id_replace_remove", true,
                    "culture_writes_coalesced", true);
        }
    }

    private static Map<String, Object> targets(MinecraftServer server) throws Exception {
        ServerLevel level = server.overworld();
        try (Fixture f = new Fixture(level, FAR)) {
            MillVillager victim = f.villager(f.village, FAR, f.peaceful);
            check(QmVillageGuard.isProtectedVillager(victim), "Peaceful registered resident is protected");
            victim.setRaiderEntity(true);
            check(!QmVillageGuard.isProtectedVillager(victim), "Actual raider entity is excluded");
            victim.setRaiderEntity(false);
            victim.setVillagerTypeId(f.guard.id());
            check(QmVillageGuard.isProtectedVillager(victim), "Peaceful guard with raider-capable profession remains protected");
            victim.setVillagerTypeId(f.hostile.id());
            check(!QmVillageGuard.isProtectedVillager(victim), "Hostile profession is excluded");
            victim.setVillagerTypeId(f.peaceful.id());
            victim.setVillageId(VillageId.random());
            check(!QmVillageGuard.isProtectedVillager(victim), "Unregistered village ID cannot borrow another village's protection");
            Village other = f.addVillage(FAR.offset(1024, 0, 0), f.type);
            victim.setVillageId(other.getId());
            check(!QmVillageGuard.isProtectedVillager(victim), "Resident must stand in its own registered village");
            Village lone = f.addVillage(FAR, f.lone);
            victim.setVillageId(lone.getId());
            check(!QmVillageGuard.isProtectedVillager(victim), "Lone-building residents are excluded even inside an overlapping village");
            victim.setVillageId(f.village.getId());

            CountingZombie actor = f.zombie(FAR);
            ServerPlayer player = player(server, level);
            actor.setTarget(player);
            actor.setAggressive(true);
            int stops = actor.stops();
            actor.setTarget(victim);
            check(actor.getTarget() == player && actor.stops() == stops && actor.isAggressive(),
                    "Native Mob.setTarget rejects protected villager and preserves existing player pursuit");
            actor.setTarget(null);
            actor.setTarget(victim);
            check(actor.getTarget() == null, "Ordinary empty target cannot become a protected villager");

            tickBrain(actor, level);
            check(actor.getBrain() instanceof GuardedBrain, "The real Brain class carries the active guard mixin");
            actor.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, victim);
            check(memory(actor).isEmpty(), "Direct Brain memory write rejects protected target");
            actor.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, Optional.of(victim));
            check(memory(actor).isEmpty(), "Optional Brain memory write rejects protected target");
            actor.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, victim, 200);
            check(memory(actor).isEmpty(), "Expiring Brain memory write rejects protected target");

            position(victim, FAR.offset(40, 0, 0));
            actor.setTarget(victim);
            check(actor.getTarget() == victim, "Outside resident remains a valid ordinary target");
            actor.setAggressive(true);
            stops = actor.stops();
            QmVillageGuard.invalidateAll();
            position(victim, FAR);
            QmVillageGuard.tickTrackedTargets();
            check(actor.getTarget() == null && actor.stops() == stops + 1 && !actor.isAggressive(),
                    "Tracked ordinary pursuit stops on boundary entry even across explicit index invalidation");

            position(victim, FAR.offset(40, 0, 0));
            actor.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, victim);
            check(memory(actor).orElse(null) == victim, "Outside Brain target is retained and watched");
            actor.setTarget(player);
            actor.setAggressive(true);
            stops = actor.stops();
            position(victim, FAR);
            QmVillageGuard.tickTrackedTargets();
            check(memory(actor).isEmpty() && actor.getTarget() == player
                            && actor.stops() == stops && actor.isAggressive(),
                    "Clearing protected Brain target preserves unrelated ordinary player target and navigation");

            position(victim, FAR.offset(40, 0, 0));
            actor.setTarget(victim);
            actor.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, player);
            stops = actor.stops();
            position(victim, FAR);
            QmVillageGuard.tickTrackedTargets();
            check(actor.getTarget() == null && memory(actor).orElse(null) == player && actor.stops() == stops,
                    "Clearing protected ordinary target preserves unrelated Brain player target");

            MillVillager fighter = f.villager(f.village, FAR.offset(40, 0, 0), f.hostile);
            fighter.setAttackTarget(victim);
            check(fighter.getAttackTarget() == null && fighter.getTarget() == null,
                    "Native Millenaire combat setter rejects protected target before either write");
            position(victim, FAR.offset(40, 0, 0));
            fighter.setAttackTarget(victim);
            check(fighter.getAttackTarget() == victim && fighter.getTarget() == victim,
                    "Outside target is accepted by both Millenaire and ordinary channels");
            fighter.setTarget(player);
            fighter.setAggressive(true);
            position(victim, FAR);
            QmVillageGuard.tickTrackedTargets();
            check(fighter.getAttackTarget() == null && fighter.getTarget() == player && fighter.isAggressive(),
                    "Clearing private Millenaire target must preserve an unrelated ordinary player target");

            CountingZombie preexisting = f.zombie(FAR);
            position(victim, FAR.offset(40, 0, 0));
            preexisting.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, victim);
            tickBrain(preexisting, level);
            position(victim, FAR);
            QmVillageGuard.tickTrackedTargets();
            check(memory(preexisting).isEmpty(), "First native Brain tick observes an existing unbound target");
            return Map.of("peaceful_guard_vs_raider", true, "own_village_membership", true,
                    "native_mob_setter", true, "brain_setter_variants", 3,
                    "native_millenaire_private_target", true, "boundary_entry", true,
                    "mixed_target_channels_preserved", true, "explicit_invalidation_preserves_encounters", true);
        }
    }

    private static Map<String, Object> damageAndGrief(MinecraftServer server) throws Exception {
        ServerLevel level = server.overworld();
        // A loaded spawn position is used only for block snapshots; event fixtures never change a block.
        BlockPos center = level.getSharedSpawnPos().above(64);
        level.getChunk(center.getX() >> 4, center.getZ() >> 4);
        try (Fixture f = new Fixture(level, center)) {
            MillVillager victim = f.villager(f.village, center, f.peaceful);
            MillVillager outside = f.villager(f.village, center.offset(40, 0, 0), f.peaceful);
            CountingZombie zombie = f.zombie(center);
            Cow cow = f.keep(new Cow(EntityType.COW, level));
            position(cow, center);
            Creeper creeper = f.keep(new Creeper(EntityType.CREEPER, level));
            position(creeper, center);
            ServerPlayer player = player(server, level);
            Arrow mobArrow = f.keep(new Arrow(EntityType.ARROW, level));
            mobArrow.setOwner(zombie);
            Arrow playerArrow = f.keep(new Arrow(EntityType.ARROW, level));
            playerArrow.setOwner(player);
            check(damage(victim, level.damageSources().mobAttack(zombie)), "Hostile mob damage is canceled");
            check(damage(victim, level.damageSources().mobAttack(cow)), "Neutral mob damage is also canceled");
            check(damage(victim, level.damageSources().arrow(mobArrow, zombie)), "Mob-owned projectile damage is canceled");
            check(!damage(victim, level.damageSources().playerAttack(player)), "Player damage is retained");
            check(!damage(victim, level.damageSources().arrow(playerArrow, player)), "Player-owned projectile damage is retained");
            check(!damage(victim, level.damageSources().generic()), "Environmental damage is retained");
            check(!damage(outside, level.damageSources().mobAttack(zombie)), "Outside resident remains vulnerable");
            victim.setRaiderEntity(true);
            check(!damage(victim, level.damageSources().mobAttack(zombie)), "Actual raider is not damage-protected");
            victim.setRaiderEntity(false);
            float protectedHealth = victim.getHealth();
            zombie.doHurtTarget(victim);
            check(victim.getHealth() == protectedHealth, "Real vanilla melee cannot damage a protected resident");
            MillVillager fighter = f.villager(f.village, center, f.hostile);
            fighter.setAttackCooldown(0);
            check(fighter.performAttack(victim) && victim.getHealth() == protectedHealth,
                    "Real Millenaire melee executes while the protected resident takes no damage");
            float outsideHealth = outside.getHealth();
            zombie.doHurtTarget(outside);
            check(outside.getHealth() < outsideHealth, "Real vanilla melee still damages an outside resident");

            BlockPos beyond = center.offset(40, 0, 0);
            for (Entity source : List.of(zombie, mobArrow)) {
                ExplosionEvent.Detonate blast = blast(level, source, center, beyond, victim, outside, player);
                NeoForge.EVENT_BUS.post(blast);
                check(!blast.getAffectedBlocks().contains(center) && blast.getAffectedBlocks().contains(beyond),
                        "Mob-attributed blast crossing boundary retains only outside blocks");
                check(!blast.getAffectedEntities().contains(victim)
                                && blast.getAffectedEntities().contains(outside) && blast.getAffectedEntities().contains(player),
                        "Mob-attributed blast retains outside villager and unrelated player damage");
            }
            ExplosionEvent.Detonate playerBlast = blast(level, player, center, beyond, victim, outside, player);
            NeoForge.EVENT_BUS.post(playerBlast);
            check(playerBlast.getAffectedBlocks().size() == 2 && playerBlast.getAffectedEntities().contains(victim),
                    "Player-attributed explosion remains unchanged");
            ExplosionEvent.Detonate environmental = blast(level, null, center, beyond, victim, outside, player);
            NeoForge.EVENT_BUS.post(environmental);
            check(environmental.getAffectedBlocks().size() == 2 && environmental.getAffectedEntities().contains(victim),
                    "Unattributed environmental explosion remains unchanged");
            check(!canGrief(level, zombie), "Hostile entity cannot grief inside protected village");
            check(canGrief(level, cow), "Peaceful entity grief rules are retained");
            check(canGrief(level, creeper), "Creeper reaches selective explosion filtering");
            position(zombie, beyond);
            check(canGrief(level, zombie), "Hostile grief remains enabled outside village");
            check(destroy(zombie, center) && !destroy(zombie, beyond) && !destroy(cow, center),
                    "Block-destruction events distinguish hostile actor and protected block position");
            BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, center);
            BlockEvent.EntityPlaceEvent hostilePlace = new BlockEvent.EntityPlaceEvent(snapshot, Blocks.STONE.defaultBlockState(), zombie);
            NeoForge.EVENT_BUS.post(hostilePlace);
            BlockEvent.EntityPlaceEvent peacefulPlace = new BlockEvent.EntityPlaceEvent(snapshot, Blocks.STONE.defaultBlockState(), cow);
            NeoForge.EVENT_BUS.post(peacefulPlace);
            check(hostilePlace.isCanceled() && !peacefulPlace.isCanceled(), "Placement protection blocks hostile actors only");
            return Map.of("damage_sources_checked", 8, "real_melee_attempts", 3, "explosion_sources_checked", 4,
                    "boundary_blast_filter", true, "player_and_environment_retained", true,
                    "creeper_grief_exception", true, "block_events", true);
        }
    }

    private static Map<String, Object> workCounts(MinecraftServer server) throws Exception {
        ServerLevel level = server.overworld();
        try (Fixture f = new Fixture(level, FAR)) {
            QmVillageGuard.tickTrackedTargets();
            check(tracking("tracked") == 0, "Disposable server begins sparse work fixture without ongoing village attacks");
            List<CountingZombie> unrelated = new ArrayList<>();
            ServerPlayer player = player(server, level);
            for (int i = 0; i < 128; i++) {
                CountingZombie actor = f.zombie(FAR.offset(40, 0, 0));
                actor.setTarget(player);
                tickBrain(actor, level);
                unrelated.add(actor);
            }
            long checks = tracking("checks");
            long observations = tracking("observations");
            long bindings = tracking("brain_bindings");
            for (int tick = 0; tick < 16; tick++) {
                for (CountingZombie actor : unrelated) {
                    NeoForge.EVENT_BUS.post(new EntityTickEvent.Pre(actor));
                    tickBrain(actor, level);
                }
                QmVillageGuard.tickTrackedTargets();
            }
            check(tracking("checks") == checks && tracking("observations") == observations
                            && tracking("brain_bindings") == bindings && tracking("tracked") == 0,
                    "2048 unrelated mob/Brain ticks perform zero guard target checks, observations or rebindings");
            MillVillager victim = f.villager(f.village, FAR.offset(40, 0, 0), f.peaceful);
            CountingZombie attacker = unrelated.getFirst();
            attacker.setTarget(victim);
            check(tracking("tracked") == 1, "Only the actual Millenaire-target attacker is tracked");
            checks = tracking("checks");
            for (int tick = 0; tick < 64; tick++) QmVillageGuard.tickTrackedTargets();
            check(tracking("checks") - checks == 64 && attacker.getTarget() == victim,
                    "64 sparse ticks inspect one continuing outside encounter exactly 64 times");
            attacker.setTarget(player);
            QmVillageGuard.tickTrackedTargets();
            check(tracking("tracked") == 0 && attacker.getTarget() == player, "Changing to a player retires sparse tracking");
            attacker.setTarget(victim);
            attacker.discard();
            checks = tracking("checks");
            QmVillageGuard.tickTrackedTargets();
            check(tracking("tracked") == 0 && tracking("checks") == checks,
                    "Removed actor retires without a protection query");
            return Map.of("unrelated_actors", 128, "unrelated_mob_and_brain_ticks", 2048,
                    "unrelated_guard_checks", 0, "unrelated_guard_observations", 0,
                    "active_encounters", 1, "active_ticks", 64, "active_guard_checks", 64);
        }
    }

    /** Exercise the actual native visibility callbacks, not a manually posted leave event. */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> visibility(MinecraftServer server) throws Exception {
        ServerLevel level = server.overworld();
        BlockPos center = level.getSharedSpawnPos().offset(32, 64, 32);
        ChunkPos chunk = new ChunkPos(center);
        level.getChunk(chunk.x, chunk.z);
        PersistentEntitySectionManager<Entity> entityManager = (PersistentEntitySectionManager<Entity>)
                field(ServerLevel.class, "entityManager").get(level);
        Long2ObjectMap<Visibility> states = (Long2ObjectMap<Visibility>)
                field(PersistentEntitySectionManager.class, "chunkVisibility").get(entityManager);
        Visibility previous = states.get(chunk.toLong());
        try (Fixture f = new Fixture(level, center)) {
            entityManager.updateChunkStatus(chunk, Visibility.TRACKED);
            CountingZombie actor = f.zombie(center);
            MillVillager victim = f.villager(f.village, center.offset(40, 0, 0), f.peaceful);
            actor.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, victim);
            check(level.addFreshEntity(actor), "Native entity manager accepts attacker with existing unbound Brain target");
            check(tracking("tracked") == 1, "Actual entity join binds Brain and observes existing outside target");
            int[] joins = {0};
            Consumer<EntityJoinLevelEvent> listener = event -> { if (event.getEntity() == actor) joins[0]++; };
            NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, EntityJoinLevelEvent.class, listener);
            try {
                entityManager.updateChunkStatus(chunk, Visibility.HIDDEN);
                check(actor.getRemovalReason() == null && !actor.isRemoved() && level.getEntity(actor.getId()) == null,
                        "Visibility loss hides a live entity without a removal reason");
                entityManager.updateChunkStatus(chunk, Visibility.TRACKED);
                check(joins[0] == 0 && level.getEntity(actor.getId()) == actor,
                        "Native visibility restoration emits no second join event");
                position(victim, center);
                QmVillageGuard.tickTrackedTargets();
                check(memory(actor).isEmpty(), "Attacker remains watched across hide/show and stops on boundary entry");
            } finally {
                NeoForge.EVENT_BUS.unregister(listener);
                entityManager.updateChunkStatus(chunk, Visibility.TRACKED);
            }
            position(victim, center.offset(40, 0, 0));
            actor.setTarget(victim);
            check(tracking("tracked") == 1, "Actual entity is tracked again for a new encounter");
            actor.discard();
            check(tracking("tracked") == 0, "Real entity removal immediately retires tracker membership");
            return Map.of("native_hide_show_without_rejoin", true, "brain_target_survives_visibility_loss", true,
                    "real_join_cold_observation", true, "real_removal_cleanup", true);
        } finally {
            entityManager.updateChunkStatus(chunk, previous == null ? Visibility.HIDDEN : previous);
        }
    }

    private static Map<String, Object> teardown(MinecraftServer server) throws Exception {
        ServerLevel level = server.overworld();
        try (Fixture f = new Fixture(level, FAR)) {
            BuildingInstance building = f.building(FAR, Rotation.NONE, f.plan);
            f.village.addBuilding(building);
            check(protectedAt(level, FAR), "Teardown fixture index is initialized");
            VillageProtectionIndex oldIndex = VillageProtectionIndex.get(level);
            CountingZombie actor = f.zombie(FAR);
            actor.setTarget(f.villager(f.village, FAR.offset(40, 0, 0), f.peaceful));
            check(listeners(f.village) == 1 && listeners(building) == 1 && tracking("tracked") == 1,
                    "Fixture owns one village listener, one building listener and one attacker");
            // Invoke only this mod's real unload handler: broadcasting unload would tear down
            // every mod's active overworld while the disposable server still needs its reload.
            var unload = QmVillageGuard.class.getDeclaredMethod("onLevelUnload", LevelEvent.Unload.class);
            unload.setAccessible(true);
            unload.invoke(null, new LevelEvent.Unload(level));
            check(listeners(f.village) == 0 && listeners(building) == 0 && tracking("tracked") == 0,
                    "Level unload releases geometry listeners and tracked attackers");
            check(VillageProtectionIndex.get(level) != oldIndex && protectedAt(level, FAR),
                    "Reacquiring index after teardown produces a fresh correct snapshot");
            check(listeners(f.village) == 1 && listeners(building) == 1,
                    "Fresh index attaches exactly once without duplicating old listeners");
            return Map.of("unload_handler_releases_listeners_and_attackers", true,
                    "manager_reacquisition", true, "duplicate_listeners", 0);
        }
    }

    private void prepareReload() {
        beforeReloadTypes = new LinkedHashMap<>(ModCultures.getAllVillageTypes());
        beforeReloadPlans = new LinkedHashMap<>(ModCultures.getAllBuildingPlans());
        VillageType type = beforeReloadTypes.values().stream()
                .filter(candidate -> !candidate.loneBuilding() && candidate.radius() > 0).findFirst().orElseThrow();
        BuildingPlan plan = beforeReloadPlans.values().stream()
                .filter(candidate -> candidate.width() > 0 && candidate.height() > 0 && candidate.depth() > 0)
                .findFirst().orElseThrow();
        // A real manager detached from world ticking retains existing native geometry
        // across reload without a synthetic settlement running construction AI.
        reloadManager = new VillageManager();
        Village village = new Village(VillageId.random(), type.culture(), type.id(), FAR);
        BuildingInstance building = new BuildingInstance(BuildingId.random(), plan.id(), FAR.offset(4096, 0, 0),
                Rotation.CLOCKWISE_90, BuildingInstance.Status.COMPLETE);
        village.addBuilding(building);
        reloadBuildingPoint = building.getOrigin().below(plan.groundLevel());
        reloadManager.addVillage(village);
        reloadGeometry = ((VillageIndexAccess) reloadManager).qm$getVillageProtectionIndex();
        check(reloadGeometry.contains(FAR) && reloadGeometry.contains(reloadBuildingPoint),
                "Existing native village and rotated building geometry are ready before reload");
        beforeReloadGeometryRebuilds = reloadGeometry.diagnostics().get("full_rebuilds");
    }

    private Map<String, Object> afterReload(MinecraftServer server) throws Exception {
        ServerLevel level = server.overworld();
        try {
            check(VillageProtectionIndex.get(level) == beforeReloadIndex,
                    "Data reload preserves manager ownership of the index");
            check(sameDefinitions(beforeReloadTypes, ModCultures.getAllVillageTypes())
                            && sameDefinitions(beforeReloadPlans, ModCultures.getAllBuildingPlans()),
                    "Pinned Millenaire keeps identical culture objects during vanilla data reload");
            check(index(level, "full_rebuilds") == beforeReloadRebuilds,
                    "Vanilla reload with unchanged cultures does not force a needless full reindex");
            check(reloadGeometry.contains(FAR) && reloadGeometry.contains(reloadBuildingPoint)
                            && reloadGeometry.diagnostics().get("full_rebuilds") == beforeReloadGeometryRebuilds,
                    "Existing village and building retain protection through unchanged vanilla data reload");

            // Millenaire 9.0.2's own reload path is separate from vanilla /reload.
            // Invoke its real clear/load/register sequence, including native JSON readers.
            CultureLoader.loadAll();
            check(!sameDefinitions(beforeReloadTypes, ModCultures.getAllVillageTypes())
                            && !sameDefinitions(beforeReloadPlans, ModCultures.getAllBuildingPlans()),
                    "Native culture loader really replaces village types and plans");
            check(reloadGeometry.contains(FAR) && reloadGeometry.contains(reloadBuildingPoint),
                    "Existing village and rotated building survive native culture content reload");
            check(reloadGeometry.diagnostics().get("full_rebuilds") == beforeReloadGeometryRebuilds + 1,
                    "All native culture reload writes coalesce into exactly one lazy reindex");
            int full = index(level, "full_rebuilds");
            check(full == beforeReloadRebuilds + 1, "Live level index also applies native culture reload once");
            for (int i = 0; i < 1024; i++) protectedAt(level, FAR.offset(i & 31, 0, 0));
            check(index(level, "full_rebuilds") == full, "Reload settles into a stable index without periodic rebuilding");
            try (Fixture f = new Fixture(level, FAR)) {
                check(protectedAt(level, FAR) && listeners(f.village) == 1, "Native village registration still updates index after reload");
                CountingZombie actor = f.zombie(FAR);
                MillVillager victim = f.villager(f.village, FAR, f.peaceful);
                actor.setTarget(victim);
                check(actor.getTarget() == null, "Native target protection still applies after both data reload paths");
            }
            return Map.of("actual_server_reload", true, "vanilla_reload_keeps_culture_identity", true,
                    "vanilla_reload_full_rebuilds", 0, "native_culture_loader_reload", true,
                    "native_culture_reload_full_rebuilds", 1, "existing_geometry_survives_both_reload_paths", true,
                    "stable_post_reload_lookups", 1024, "post_reload_registration_and_targeting", true);
        } finally {
            reloadManager.clear();
        }
    }

    private static <T> boolean sameDefinitions(Map<ResourceLocation, T> before, Map<ResourceLocation, T> after) {
        return before.size() == after.size() && before.entrySet().stream()
                .allMatch(entry -> after.get(entry.getKey()) == entry.getValue());
    }

    private static boolean damage(LivingEntity victim, DamageSource source) {
        LivingIncomingDamageEvent event = new LivingIncomingDamageEvent(victim, new DamageContainer(source, 4));
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    private static ExplosionEvent.Detonate blast(ServerLevel level, Entity source, BlockPos inside,
            BlockPos outside, Entity... entities) {
        Explosion explosion = new Explosion(level, source, inside.getX(), inside.getY(), inside.getZ(), 4,
                false, Explosion.BlockInteraction.DESTROY, new ArrayList<>(List.of(inside, outside)));
        return new ExplosionEvent.Detonate(level, explosion, new ArrayList<>(List.of(entities)));
    }

    private static boolean canGrief(ServerLevel level, Entity entity) {
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(level, entity);
        event.setCanGrief(true);
        NeoForge.EVENT_BUS.post(event);
        return event.canGrief();
    }

    private static boolean destroy(LivingEntity entity, BlockPos pos) {
        LivingDestroyBlockEvent event = new LivingDestroyBlockEvent(entity, pos, Blocks.STONE.defaultBlockState());
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    private static boolean protectedAt(ServerLevel level, BlockPos pos) { return QmVillageGuard.isProtectedPos(level, pos); }
    private static int index(ServerLevel level, String key) { return QmVillageGuard.debugIndex(level).get(key); }
    private static long tracking(String key) { return QmVillageGuard.debugTracking().get(key); }
    private static int listeners(Object object) { return ((GeometryChangeSource) object).qm$geometryListenerCount(); }
    private static Optional<LivingEntity> memory(Mob mob) { return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET); }
    private static void position(Entity entity, BlockPos pos) { entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5); }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void tickBrain(Mob mob, ServerLevel level) { ((Brain) mob.getBrain()).tick(level, mob); }

    private static ServerPlayer player(MinecraftServer server, ServerLevel level) {
        return new ServerPlayer(server, level, new GameProfile(UUID.randomUUID(), "VillageGuardProbe"), ClientInformation.createDefault());
    }

    private static Field field(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @SuppressWarnings("unchecked")
    private static <T> T copy(T record, Map<String, Object> replacements) throws Exception {
        RecordComponent[] components = record.getClass().getRecordComponents();
        Class<?>[] types = new Class<?>[components.length];
        Object[] values = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            RecordComponent component = components[i];
            types[i] = component.getType();
            values[i] = replacements.containsKey(component.getName())
                    ? replacements.get(component.getName()) : component.getAccessor().invoke(record);
        }
        return (T) record.getClass().getDeclaredConstructor(types).newInstance(values);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        assertions++;
    }

    /** Minimal native zombie with a registered attack memory and observable navigation. */
    private static final class CountingZombie extends Zombie {
        CountingZombie(ServerLevel level) { super(EntityType.ZOMBIE, level); setNoAi(true); }
        @Override protected Brain.Provider<CountingZombie> brainProvider() {
            return Brain.provider(List.of(MemoryModuleType.ATTACK_TARGET), List.of());
        }
        @Override protected PathNavigation createNavigation(Level level) { return new CountingNavigation(this, level); }
        int stops() { return ((CountingNavigation) getNavigation()).stops; }
    }

    private static final class CountingNavigation extends GroundPathNavigation {
        int stops;
        CountingNavigation(Mob mob, Level level) { super(mob, level); }
        @Override public void stop() { stops++; super.stop(); }
    }

    /** Registry and manager mutations are synchronous and restored before any native world tick. */
    private static final class Fixture implements AutoCloseable {
        final ServerLevel level;
        final VillageManager manager;
        final List<Village> saved;
        final List<Entity> entities = new ArrayList<>();
        final VillageType type;
        final VillageType lone;
        final BuildingPlan plan;
        final BuildingPlan upgrade;
        final VillagerType peaceful;
        final VillagerType guard;
        final VillagerType hostile;
        final Village village;

        Fixture(ServerLevel level, BlockPos center) throws Exception {
            this.level = level;
            VillageType template = ModCultures.getAllVillageTypes().values().stream()
                    .filter(candidate -> !candidate.loneBuilding()).findFirst().orElseThrow();
            String suffix = UUID.randomUUID().toString().replace("-", "");
            type = copy(template, Map.of("id", id("village_" + suffix), "radius", 16,
                    "spawnable", false, "layout", List.of()));
            lone = copy(type, Map.of("id", id("lone_" + suffix), "loneBuilding", true));
            plan = new BuildingPlan(id("plan_" + suffix), type.culture(), "probe_unused", 5, 6, 9,
                    2, 0, BlockPos.ZERO, List.of(), "", "", List.of(), null);
            upgrade = copy(plan, Map.of("id", id("upgrade_" + suffix), "width", 17, "height", 12));
            peaceful = ModCultures.getAllVillagerTypes().values().stream().filter(t -> !t.isHostile()).findFirst().orElseThrow();
            guard = ModCultures.getAllVillagerTypes().values().stream().filter(t -> t.isRaider() && !t.isHostile()).findFirst().orElseThrow();
            hostile = ModCultures.getAllVillagerTypes().values().stream().filter(VillagerType::isHostile).findFirst().orElseThrow();
            manager = VillageSavedData.get(level).getVillageManager();
            saved = List.copyOf(manager.getAllVillages());
            ModCultures.registerVillageType(type);
            ModCultures.registerVillageType(lone);
            ModCultures.registerBuildingPlan(plan);
            ModCultures.registerBuildingPlan(upgrade);
            manager.clear();
            village = addVillage(center, type);
        }

        Village addVillage(BlockPos center, VillageType villageType) {
            Village added = new Village(VillageId.random(), villageType.culture(), villageType.id(), center);
            manager.addVillage(added);
            return added;
        }

        BuildingInstance building(BlockPos origin, Rotation rotation, BuildingPlan buildingPlan) {
            return new BuildingInstance(BuildingId.random(), buildingPlan.id(), origin, rotation, BuildingInstance.Status.COMPLETE);
        }

        MillVillager villager(Village home, BlockPos pos, VillagerType villagerType) {
            MillVillager resident = keep(new MillVillager(ModEntities.MILL_VILLAGER.get(), level));
            resident.setVillageId(home.getId());
            resident.setVillagerTypeId(villagerType.id());
            resident.setNoAi(true);
            position(resident, pos);
            return resident;
        }

        CountingZombie zombie(BlockPos pos) {
            CountingZombie actor = keep(new CountingZombie(level));
            position(actor, pos);
            return actor;
        }

        <T extends Entity> T keep(T entity) { entities.add(entity); return entity; }

        @Override public void close() {
            for (Entity entity : entities) entity.discard();
            QmVillageGuard.tickTrackedTargets();
            manager.clear();
            for (Village original : saved) manager.addVillage(original);
            ModCultures.unregisterVillageType(type.id());
            ModCultures.unregisterVillageType(lone.id());
            ModCultures.unregisterBuildingPlan(plan.id());
            ModCultures.unregisterBuildingPlan(upgrade.id());
        }

        private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("soqm_guard_probe", path); }
    }
}
