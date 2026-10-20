package qm.villageguard;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.millenaire.culture.VillagerType;
import org.millenaire.entity.MillVillager;
import org.millenaire.culture.ModCultures;

/** Protects peaceful registered Millenaire settlements without loading chunks. */
@Mod(QmVillageGuard.MOD_ID)
public final class QmVillageGuard {
    public static final String MOD_ID = "qm_village_guard";
    // Only attackers with a Millenaire target need boundary/type rechecks.
    // Weak keys and explicit leave/unload cleanup cannot retain an old world.
    private static final Set<Mob> TRACKED = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Set<Mob> PENDING = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Set<Mob> REMOVING = Collections.newSetFromMap(new IdentityHashMap<>());
    private static boolean checkingTargets;
    private static long targetChecks, observations, brainBindings;

    public QmVillageGuard() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmVillageGuard::onTarget);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmVillageGuard::onTick);
        NeoForge.EVENT_BUS.addListener(QmVillageGuard::onJoin);
        NeoForge.EVENT_BUS.addListener(QmVillageGuard::onLeave);
        NeoForge.EVENT_BUS.addListener(QmVillageGuard::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmVillageGuard::onDamage);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmVillageGuard::onExplosion);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmVillageGuard::onGrief);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmVillageGuard::onDestroy);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmVillageGuard::onPlace);
        NeoForge.EVENT_BUS.addListener(QmVillageGuard::onStopped);
    }

    public static boolean isProtectedPos(ServerLevel level,BlockPos pos) {
        return VillageProtectionIndex.get(level).contains(pos);
    }

    public static boolean isProtectedVillager(Entity entity) {
        if(!(entity instanceof MillVillager villager) || !(villager.level() instanceof ServerLevel level))return false;
        if(villager.getVillageId()==null || villager.getVillagerTypeId()==null || villager.isRaiderEntity())return false;
        VillagerType type=ModCultures.getVillagerType(villager.getVillagerTypeId());
        if(type==null || type.isHostile())return false;
        // A guard's profession may have the "raider" capability, but only an
        // actual raider entity is excluded. Lone inns/farms/bandit huts are excluded.
        return VillageProtectionIndex.get(level).containsVillager(villager.getVillageId(),villager.blockPosition());
    }

    public static boolean isHostile(Entity entity) {
        if(entity instanceof MillVillager villager) {
            VillagerType type=villager.getVillagerTypeId()==null?null:ModCultures.getVillagerType(villager.getVillagerTypeId());
            return villager.isRaiderEntity() || (type!=null && type.isHostile());
        }
        return entity instanceof Enemy || (entity instanceof Mob && entity.getType().getCategory()==MobCategory.MONSTER);
    }

    private static boolean mobAttributed(Entity entity) {
        if(entity instanceof Mob)return true;
        return entity instanceof Projectile projectile && projectile.getOwner() instanceof Mob;
    }

    public static boolean mobDamage(DamageSource source) {
        return mobAttributed(source.getEntity()) || mobAttributed(source.getDirectEntity());
    }

    public static boolean clearProtectedTargets(Mob mob) {
        boolean changed=false;
        if(isProtectedVillager(mob.getTarget())) { mob.setTarget(null);changed=true; }
        Optional<LivingEntity> memory=attackMemory(mob);
        if(memory.isPresent() && isProtectedVillager(memory.get())) {
            mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);changed=true;
        }
        if(mob instanceof MillVillager villager && isProtectedVillager(villager.getAttackTarget())) {
            // Millenaire's custom setter also overwrites Mob.target. The
            // remaining ordinary target has already passed the guard above.
            LivingEntity ordinary=mob.getTarget();
            villager.setAttackTarget(null);changed=true;
            if(ordinary!=null && mob.getTarget()==null)mob.setTarget(ordinary);
        }
        // Keep any unrelated player target/navigation intact.
        if(changed && mob.getTarget()==null && attackMemory(mob).isEmpty() &&
                (!(mob instanceof MillVillager villager) || villager.getAttackTarget()==null)) {
            mob.getNavigation().stop();mob.setAggressive(false);
        }
        return changed;
    }

    private static Optional<LivingEntity> attackMemory(Mob mob) {
        return mob.getBrain().checkMemory(MemoryModuleType.ATTACK_TARGET,MemoryStatus.REGISTERED)
                ? mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET) : Optional.empty();
    }

    private static void onTarget(LivingChangeTargetEvent event) {
        if(event.getEntity() instanceof Mob mob && mob.level() instanceof ServerLevel) {
            if(rejectTarget(mob,event.getNewAboutToBeSetTarget()))event.setCanceled(true);
        }
    }

    /** Shared guard for native targets, direct Brain writes and Millenaire combat. */
    public static boolean rejectTarget(Mob owner, LivingEntity target) {
        if(isProtectedVillager(target)) {
            if(owner!=null && owner.level() instanceof ServerLevel)clearProtectedTargets(owner);
            return true;
        }
        if(owner!=null && target instanceof MillVillager && target.isAlive())watch(owner);
        return false;
    }

    private static void watch(Mob mob) {
        if(!(mob.level() instanceof ServerLevel) || mob.isRemoved() || !mob.isAlive())return;
        if(checkingTargets) {
            REMOVING.remove(mob);
            PENDING.add(mob);
        } else TRACKED.add(mob);
    }

    private static boolean hasVillageTarget(Mob mob) {
        LivingEntity ordinary=mob.getTarget();
        if(ordinary instanceof MillVillager && ordinary.isAlive())return true;
        Optional<LivingEntity> memory=attackMemory(mob);
        if(memory.isPresent() && memory.get() instanceof MillVillager && memory.get().isAlive())return true;
        return mob instanceof MillVillager villager && villager.getAttackTarget() instanceof MillVillager target && target.isAlive();
    }

    /** One cold observation on entity join or Brain replacement, never an all-mob tick. */
    public static void observeTargets(Mob mob) {
        if(!(mob.level() instanceof ServerLevel))return;
        observations++;
        if(hasVillageTarget(mob))watch(mob);
    }

    public static void brainBound(Mob owner) {
        if(!(owner.level() instanceof ServerLevel))return;
        brainBindings++;
        observeTargets(owner);
    }

    /** Recheck only ongoing village-target encounters, including boundary crossings. */
    public static void tickTrackedTargets() {
        if(checkingTargets)return;
        checkingTargets=true;
        try {
            Iterator<Mob> iterator=TRACKED.iterator();
            while(iterator.hasNext()) {
                Mob mob=iterator.next();
                if(mob.isRemoved() || !mob.isAlive() || !(mob.level() instanceof ServerLevel)) {
                    iterator.remove();continue;
                }
                targetChecks++;
                clearProtectedTargets(mob);
                if(!hasVillageTarget(mob))iterator.remove();
            }
        } finally {
            checkingTargets=false;
            TRACKED.addAll(PENDING);
            TRACKED.removeAll(REMOVING);
            PENDING.clear();REMOVING.clear();
        }
    }

    private static void forget(Mob mob) {
        PENDING.remove(mob);
        if(checkingTargets)REMOVING.add(mob);else TRACKED.remove(mob);
    }

    private static void onTick(ServerTickEvent.Pre event) { tickTrackedTargets(); }

    private static void onJoin(EntityJoinLevelEvent event) {
        if(event.getLevel() instanceof ServerLevel && event.getEntity() instanceof Mob mob)
            ((GuardedBrain)mob.getBrain()).qm$bindOwner(mob);
    }

    private static void onLeave(EntityLeaveLevelEvent event) {
        // NeoForge also posts this when a live section becomes hidden. Becoming
        // visible again need not post a join, so keep those ongoing encounters.
        if(event.getLevel() instanceof ServerLevel && event.getEntity() instanceof Mob mob
                && mob.getRemovalReason()!=null)forget(mob);
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        if(!(event.getLevel() instanceof ServerLevel level))return;
        // Use a snapshot: level teardown can be reentrant during an event callback.
        for(Mob mob:new ArrayList<>(TRACKED))if(mob.level()==level)forget(mob);
        PENDING.removeIf(mob->mob.level()==level);
        VillageProtectionIndex.unload(level);
    }

    private static void onDamage(LivingIncomingDamageEvent event) {
        if(isProtectedVillager(event.getEntity()) && mobDamage(event.getSource()))event.setCanceled(true);
    }

    private static void onExplosion(ExplosionEvent.Detonate event) {
        if(!(event.getLevel() instanceof ServerLevel level))return;
        if(!mobAttributed(event.getExplosion().getDirectSourceEntity()) && !mobAttributed(event.getExplosion().getIndirectSourceEntity()))return;
        event.getAffectedEntities().removeIf(QmVillageGuard::isProtectedVillager);
        event.getAffectedBlocks().removeIf(pos->isProtectedPos(level,pos));
    }

    private static void onGrief(EntityMobGriefingEvent event) {
        Entity entity=event.getEntity();
        // Let creepers reach Detonate so an explosion crossing the boundary still
        // destroys ordinary outside blocks instead of disabling the whole blast.
        if(entity instanceof Creeper)return;
        if(entity.level() instanceof ServerLevel level && isHostile(entity) && isProtectedPos(level,entity.blockPosition()))event.setCanGrief(false);
    }

    private static void onDestroy(LivingDestroyBlockEvent event) {
        if(event.getEntity().level() instanceof ServerLevel level && isHostile(event.getEntity()) && isProtectedPos(level,event.getPos()))event.setCanceled(true);
    }

    private static void onPlace(BlockEvent.EntityPlaceEvent event) {
        Entity entity=event.getEntity();
        if(entity!=null && entity.level() instanceof ServerLevel level && isHostile(entity) && isProtectedPos(level,event.getPos()))event.setCanceled(true);
    }

    private static void onStopped(ServerStoppedEvent event) {
        VillageProtectionIndex.invalidateAll();
        TRACKED.clear();PENDING.clear();REMOVING.clear();
    }
    public static void invalidateAll() { VillageProtectionIndex.invalidateAll(); }
    public static Map<String,Integer> debugIndex(ServerLevel level) {
        return VillageProtectionIndex.get(level).diagnostics();
    }
    public static Map<String,Long> debugTracking() {
        return Map.of("tracked",(long)TRACKED.size(),"checks",targetChecks,
                "observations",observations,"brain_bindings",brainBindings);
    }
}
