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
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlan;
import org.millenaire.culture.VillageType;
import org.millenaire.culture.VillagerType;
import org.millenaire.entity.MillVillager;
import org.millenaire.culture.ModCultures;
import org.millenaire.village.Village;
import org.millenaire.village.VillageId;
import org.millenaire.village.VillageSavedData;

/** Protects peaceful registered Millenaire settlements without loading chunks. */
@Mod(QmVillageGuard.MOD_ID)
public final class QmVillageGuard {
    public static final String MOD_ID = "qm_village_guard";
    private static final Map<ServerLevel, Index> INDEXES = new WeakHashMap<>();
    private static final int CELL_SHIFT = 8; // 256 block X/Z spatial cells.
    private static final long REFRESH_TICKS = 200L;

    public QmVillageGuard() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmVillageGuard::onTarget);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmVillageGuard::onTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmVillageGuard::onDamage);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmVillageGuard::onExplosion);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmVillageGuard::onGrief);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmVillageGuard::onDestroy);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, QmVillageGuard::onPlace);
        NeoForge.EVENT_BUS.addListener(QmVillageGuard::onStopped);
    }

    private record Footprint(BuildingInstance building, int minY, int maxY,
                             int minX, int maxX, int minZ, int maxZ) {
        boolean contains(BlockPos pos) {
            return pos.getY() >= minY && pos.getY() <= maxY && building.containsPos(pos);
        }
    }

    private record Area(VillageId id, BlockPos center, int radius, List<Footprint> footprints,
                        int minX, int maxX, int minZ, int maxZ) {
        boolean contains(BlockPos pos) {
            long dx=(long)pos.getX()-center.getX(), dz=(long)pos.getZ()-center.getZ();
            if (pos.getY() >= center.getY()-24 && pos.getY() <= center.getY()+64 &&
                    dx*dx + dz*dz <= (long)radius*radius) return true;
            for (Footprint f : footprints) if (f.contains(pos)) return true;
            return false;
        }
    }

    private static final class Index {
        long refreshedAt = Long.MIN_VALUE;
        int villageCount = -1;
        final Map<VillageId, Area> byId = new HashMap<>();
        final Map<Long, List<Area>> cells = new HashMap<>();
        void refresh(ServerLevel level, Collection<Village> villages) {
            byId.clear(); cells.clear(); villageCount = villages.size(); refreshedAt = level.getGameTime();
            for (Village village : villages) {
                VillageType type = ModCultures.getVillageType(village.getVillageTypeId());
                if (type == null || type.loneBuilding()) continue;
                BlockPos center = village.getCenter().immutable(); int radius=Math.max(0,type.radius());
                int minX=center.getX()-radius,maxX=center.getX()+radius,minZ=center.getZ()-radius,maxZ=center.getZ()+radius;
                List<Footprint> footprints=new ArrayList<>();
                for (BuildingInstance building : village.getBuildings()) {
                    BuildingPlan plan=building.getPlanId()==null?null:ModCultures.getBuildingPlan(building.getPlanId());
                    if (plan==null || plan.width()<=0 || plan.depth()<=0 || plan.height()<=0) continue;
                    // Conservative cell bounds; the exact rotated native footprint
                    // performs the final position test, including negative offsets.
                    int extent=Math.max(plan.width(),plan.depth()); BlockPos origin=building.getOrigin();
                    int x0=origin.getX()-extent,x1=origin.getX()+extent,z0=origin.getZ()-extent,z1=origin.getZ()+extent;
                    int floor=building.getWallFootY();
                    footprints.add(new Footprint(building,floor-2,floor+plan.height()+2,x0,x1,z0,z1));
                    minX=Math.min(minX,x0);maxX=Math.max(maxX,x1);minZ=Math.min(minZ,z0);maxZ=Math.max(maxZ,z1);
                }
                Area area=new Area(village.getId(),center,radius,List.copyOf(footprints),minX,maxX,minZ,maxZ);
                byId.put(area.id(),area);
                for (int cx=minX>>CELL_SHIFT;cx<=maxX>>CELL_SHIFT;cx++)
                    for (int cz=minZ>>CELL_SHIFT;cz<=maxZ>>CELL_SHIFT;cz++)
                        cells.computeIfAbsent(cell(cx,cz),ignored->new ArrayList<>()).add(area);
            }
        }
    }

    private static long cell(int x,int z) { return ((long)x<<32) ^ (z & 0xffffffffL); }
    private static Index index(ServerLevel level) {
        Collection<Village> villages=VillageSavedData.get(level).getVillageManager().getAllVillages();
        Index index=INDEXES.computeIfAbsent(level,ignored->new Index()); long now=level.getGameTime();
        if(index.villageCount!=villages.size() || index.refreshedAt==Long.MIN_VALUE || now<index.refreshedAt || now-index.refreshedAt>=REFRESH_TICKS)
            index.refresh(level,villages);
        return index;
    }

    public static boolean isProtectedPos(ServerLevel level,BlockPos pos) {
        List<Area> areas=index(level).cells.get(cell(pos.getX()>>CELL_SHIFT,pos.getZ()>>CELL_SHIFT));
        if(areas==null)return false;
        for(Area area:areas)if(area.contains(pos))return true;
        return false;
    }

    public static boolean isProtectedVillager(Entity entity) {
        if(!(entity instanceof MillVillager villager) || !(villager.level() instanceof ServerLevel level))return false;
        if(villager.getVillageId()==null || villager.getVillagerTypeId()==null || villager.isRaiderEntity())return false;
        VillagerType type=ModCultures.getVillagerType(villager.getVillagerTypeId());
        if(type==null || type.isHostile())return false;
        // A guard's profession may have the "raider" capability, but only an
        // actual raider entity is excluded. Lone inns/farms/bandit huts are excluded.
        Area home=index(level).byId.get(villager.getVillageId());
        return home!=null && home.contains(villager.blockPosition());
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
            villager.setAttackTarget(null);changed=true;
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
        if(event.getEntity() instanceof Mob mob && isProtectedVillager(event.getNewAboutToBeSetTarget())) {
            event.setCanceled(true);clearProtectedTargets(mob);
        }
    }

    private static void onTick(EntityTickEvent.Pre event) {
        if(event.getEntity() instanceof Mob mob && mob.level() instanceof ServerLevel)clearProtectedTargets(mob);
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

    private static void onStopped(ServerStoppedEvent event) { invalidateAll(); }
    public static void invalidateAll() { INDEXES.clear(); }
    public static Map<String,Integer> debugIndex(ServerLevel level) {
        Index index=index(level);return Map.of("villages",index.byId.size(),"cells",index.cells.size(),"registered",index.villageCount);
    }
}
