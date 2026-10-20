package qm.villageguard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlan;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillageType;
import org.millenaire.village.Village;
import org.millenaire.village.VillageId;
import org.millenaire.village.VillageManager;
import org.millenaire.village.VillageSavedData;
import qm.villageguard.mixin.VillageSavedDataCacheAccess;

/**
 * A manager-owned spatial snapshot. Ordinary lookups never enumerate all villages.
 * Only membership/geometry mutations and culture changes invalidate snapshots.
 */
public final class VillageProtectionIndex {
    private static final int CELL_SHIFT = 8;
    private static final AtomicLong CULTURE_EPOCH = new AtomicLong();

    private final VillageManager manager;
    private final Map<VillageId, Entry> entries = new LinkedHashMap<>();
    private final Map<VillageId, Area> areas = new HashMap<>();
    private final Map<Long, Set<Area>> cells = new HashMap<>();
    private final Set<VillageId> pending = new LinkedHashSet<>();
    private boolean initialized;
    private boolean closed;
    private long cultureEpoch;
    private int fullRebuilds;
    private int villageRebuilds;

    public VillageProtectionIndex(VillageManager manager) {
        this.manager = manager;
    }

    public static VillageProtectionIndex get(ServerLevel level) {
        VillageManager manager = VillageSavedData.get(level).getVillageManager();
        return ((VillageIndexAccess) manager).qm$getVillageProtectionIndex();
    }

    /** Content loading may run outside a level tick; the epoch owns no world data. */
    public static void invalidateAll() {
        CULTURE_EPOCH.incrementAndGet();
    }

    /** Do not create/load saved data just because an otherwise unused level unloads. */
    public static void unload(ServerLevel level) {
        SavedData saved = ((VillageSavedDataCacheAccess) level.getDataStorage())
                .qm$villageGuardSavedDataCache().get("millenaire_villages");
        if (saved instanceof VillageSavedData villages) {
            ((VillageIndexAccess) villages.getVillageManager()).qm$unloadVillageProtectionIndex();
        }
    }

    public boolean contains(BlockPos pos) {
        refresh();
        Set<Area> candidates = cells.get(cell(pos.getX() >> CELL_SHIFT, pos.getZ() >> CELL_SHIFT));
        if (candidates == null) return false;
        for (Area area : candidates) if (area.contains(pos)) return true;
        return false;
    }

    public boolean containsVillager(VillageId id, BlockPos pos) {
        refresh();
        Area area = areas.get(id);
        return area != null && area.contains(pos);
    }

    public Map<String, Integer> diagnostics() {
        refresh();
        return Map.of("villages", areas.size(), "cells", cells.size(),
                "registered", entries.size(), "full_rebuilds", fullRebuilds,
                "village_rebuilds", villageRebuilds, "pending_villages", pending.size());
    }

    public void villageAdded(Village village) {
        if (!initialized || closed) return;
        Entry previous = entries.get(village.getId());
        if (previous != null && previous.village == village) return;
        remove(village.getId());
        Entry entry = new Entry(village);
        entries.put(village.getId(), entry);
        entry.attach();
        pending.add(village.getId());
    }

    public void villageRemoved(VillageId id) {
        if (initialized && !closed) remove(id);
    }

    public void managerCleared() {
        if (closed) return;
        detachEntries();
        // Preserve initialized state and counters: querying an empty, cleared
        // manager must not schedule another all-village bootstrap.
        cultureEpoch = CULTURE_EPOCH.get();
    }

    public void close() {
        if (closed) return;
        detachEntries();
        closed = true;
    }

    private void detachEntries() {
        for (Entry entry : entries.values()) entry.detach();
        entries.clear();
        areas.clear();
        cells.clear();
        pending.clear();
    }

    private void remove(VillageId id) {
        Entry entry = entries.remove(id);
        if (entry != null) entry.detach();
        removeArea(areas.remove(id));
        pending.remove(id);
    }

    private void refresh() {
        if (closed) throw new IllegalStateException("Village protection index is unloaded");
        if (!initialized) {
            long epoch = CULTURE_EPOCH.get();
            for (Village village : manager.getAllVillages()) {
                Entry entry = new Entry(village);
                entries.put(village.getId(), entry);
                entry.attach();
            }
            initialized = true;
            rebuildAll(epoch);
        }
        long epoch = CULTURE_EPOCH.get();
        if (epoch != cultureEpoch) {
            rebuildAll(epoch);
        } else if (!pending.isEmpty()) {
            List<VillageId> changed = List.copyOf(pending);
            pending.clear();
            for (VillageId id : changed) {
                Entry entry = entries.get(id);
                if (entry != null) rebuild(entry);
            }
        }
    }

    private void rebuildAll(long epoch) {
        areas.clear();
        cells.clear();
        pending.clear();
        for (Entry entry : entries.values()) rebuild(entry);
        cultureEpoch = epoch;
        fullRebuilds++;
    }

    private void rebuild(Entry entry) {
        VillageId id = entry.village.getId();
        removeArea(areas.remove(id));
        Area area = snapshot(entry.village);
        villageRebuilds++;
        if (area == null) return;
        areas.put(id, area);
        for (long key : area.cellKeys) {
            cells.computeIfAbsent(key, ignored -> new LinkedHashSet<>()).add(area);
        }
    }

    private void removeArea(Area area) {
        if (area == null) return;
        for (long key : area.cellKeys) {
            Set<Area> bucket = cells.get(key);
            if (bucket == null) continue;
            bucket.remove(area);
            if (bucket.isEmpty()) cells.remove(key);
        }
    }

    private Area snapshot(Village village) {
        VillageType type = ModCultures.getVillageType(village.getVillageTypeId());
        if (type == null || type.loneBuilding()) return null;
        BlockPos center = village.getCenter().immutable();
        int radius = Math.max(0, type.radius());
        List<Footprint> footprints = new ArrayList<>();
        Set<Long> keys = new LinkedHashSet<>();
        addCells(keys, (long) center.getX() - radius, (long) center.getX() + radius,
                (long) center.getZ() - radius, (long) center.getZ() + radius);
        for (BuildingInstance building : village.getBuildings()) {
            BuildingPlan plan = ModCultures.getBuildingPlan(building.getPlanId());
            if (plan == null || plan.width() <= 0 || plan.depth() <= 0 || plan.height() <= 0) continue;
            BlockPos origin = building.getOrigin();
            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(building.getRotation());
            BlockPos a = StructureTemplate.calculateRelativePosition(settings, BlockPos.ZERO);
            BlockPos b = StructureTemplate.calculateRelativePosition(settings,
                    new BlockPos(plan.width() - 1, 0, plan.depth() - 1));
            long floor = (long) origin.getY() - plan.groundLevel();
            Footprint footprint = new Footprint(
                    (long) origin.getX() + Math.min(a.getX(), b.getX()),
                    (long) origin.getX() + Math.max(a.getX(), b.getX()),
                    floor - 2, floor + plan.height() + 2,
                    (long) origin.getZ() + Math.min(a.getZ(), b.getZ()),
                    (long) origin.getZ() + Math.max(a.getZ(), b.getZ()));
            footprints.add(footprint);
            addCells(keys, footprint.minX, footprint.maxX, footprint.minZ, footprint.maxZ);
        }
        return new Area(center, radius, List.copyOf(footprints), Set.copyOf(keys));
    }

    private static void addCells(Set<Long> keys, long minX, long maxX, long minZ, long maxZ) {
        // Index the union of actual circle/footprint bounds, avoiding a huge
        // empty bounding rectangle between a settlement and a distant building.
        for (long x = minX >> CELL_SHIFT; x <= maxX >> CELL_SHIFT; x++) {
            for (long z = minZ >> CELL_SHIFT; z <= maxZ >> CELL_SHIFT; z++) {
                keys.add(cell((int) x, (int) z));
            }
        }
    }

    private static long cell(int x, int z) {
        return ((long) x << 32) ^ (z & 0xffffffffL);
    }

    private final class Entry {
        private final Village village;
        private final Set<BuildingInstance> buildings = Collections.newSetFromMap(new IdentityHashMap<>());
        private final Runnable villageListener;
        private final Runnable buildingListener;

        Entry(Village village) {
            this.village = village;
            villageListener = () -> {
                if (entries.get(village.getId()) != this) return;
                syncBuildings();
                pending.add(village.getId());
            };
            buildingListener = () -> {
                if (entries.get(village.getId()) == this) pending.add(village.getId());
            };
        }

        void attach() {
            ((GeometryChangeSource) village).qm$addGeometryListener(villageListener);
            syncBuildings();
        }

        void syncBuildings() {
            Set<BuildingInstance> current = Collections.newSetFromMap(new IdentityHashMap<>());
            current.addAll(village.getBuildings());
            for (BuildingInstance building : buildings) {
                if (!current.contains(building)) {
                    ((GeometryChangeSource) building).qm$removeGeometryListener(buildingListener);
                }
            }
            for (BuildingInstance building : current) {
                if (!buildings.contains(building)) {
                    ((GeometryChangeSource) building).qm$addGeometryListener(buildingListener);
                }
            }
            buildings.clear();
            buildings.addAll(current);
        }

        void detach() {
            ((GeometryChangeSource) village).qm$removeGeometryListener(villageListener);
            for (BuildingInstance building : buildings) {
                ((GeometryChangeSource) building).qm$removeGeometryListener(buildingListener);
            }
            buildings.clear();
        }
    }

    private record Footprint(long minX, long maxX, long minY, long maxY, long minZ, long maxZ) {
        boolean contains(BlockPos pos) {
            return pos.getX() >= minX && pos.getX() <= maxX
                    && pos.getY() >= minY && pos.getY() <= maxY
                    && pos.getZ() >= minZ && pos.getZ() <= maxZ;
        }
    }

    /** Identity equality makes removing an area from its cell sets constant time. */
    private static final class Area {
        final BlockPos center;
        final int radius;
        final List<Footprint> footprints;
        final Set<Long> cellKeys;

        Area(BlockPos center, int radius, List<Footprint> footprints, Set<Long> cellKeys) {
            this.center = center;
            this.radius = radius;
            this.footprints = footprints;
            this.cellKeys = cellKeys;
        }

        boolean contains(BlockPos pos) {
            long dx = (long) pos.getX() - center.getX();
            long dz = (long) pos.getZ() - center.getZ();
            if ((long) pos.getY() >= (long) center.getY() - 24
                    && (long) pos.getY() <= (long) center.getY() + 64
                    && Math.abs(dx) <= radius && Math.abs(dz) <= radius
                    && dx * dx + dz * dz <= (long) radius * radius) return true;
            for (Footprint footprint : footprints) if (footprint.contains(pos)) return true;
            return false;
        }
    }
}
