package soqm.validation.encounters;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.tonicarter.elitemobs.EliteMobManager;
import qm.dungeonencounters.AriseStructures;
import qm.dungeonencounters.EncounterConfig;
import qm.dungeonencounters.QmDungeonEncounters;
import qm.mobcompat.MobCompat45;

/** Real native registries/entities/start references, isolated to the disposable probe world. */
final class WdaEncounterProbe {
    private static int assertions;
    private static final ResourceLocation ID = ResourceLocation.parse("dungeons_arise:illager_fort");
    private static final BlockPos ORIGIN = new BlockPos(80, 250, 80);

    static Map<String, Object> run(MinecraftServer server) throws Exception {
        assertions = 0;
        for (String name : List.of("find_fishing_hut", "find_thornborn_towers")) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("dungeons_arise", name);
            check(server.getAdvancements().get(id) != null && server.getAdvancements().tree().get(id) != null,
                "Repaired WDA advancement decodes and joins the native tree: " + id);
        }
        boolean enabled = EncounterConfig.ENABLE_ARISE.get();
        double chance = EncounterConfig.ARISE_CHANCE.get();
        int budget = EncounterConfig.ACTIVATION_BUDGET.get();
        int discoveryBudget = EncounterConfig.ARISE_DISCOVERY_BUDGET.get();
        List<? extends String> affixes = EncounterConfig.AFFIXES.get();
        EncounterConfig.ENABLE_ARISE.set(true);
        EncounterConfig.ARISE_CHANCE.set(1.0);
        EncounterConfig.AFFIXES.set(List.of("vampiric"));
        try (Fixture f = new Fixture(server.overworld()); DungeonTestPlayers players = new DungeonTestPlayers()) {
            ServerPlayer player = players.create(f.level, ORIGIN.offset(2, 1, 2), WorldTier.ASCENT);
            check(AriseStructures.contains(f.level, ORIGIN.offset(4, 1, 4)), "Native WDA structure contains interior point");
            check(!AriseStructures.contains(f.level, ORIGIN.offset(4, -100, 4)), "Three-dimensional bounds exclude same-XZ underground point");
            Mob outside = f.create("minecraft:zombie", ORIGIN.offset(4, -100, 4), true);
            check(!QmDungeonEncounters.isPending(outside), "Outside hostile is not selected");
            check(!outside.getAttribute(Attributes.MAX_HEALTH).hasModifier(QmDungeonEncounters.HEALTH_ID), "Outside hostile keeps native health");

            Mob named = f.create("minecraft:zombie", ORIGIN.offset(3, 1, 3), false);
            named.setCustomName(Component.literal("Authored WDA Guard"));
            named.setPersistenceRequired();
            ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
            sword.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("Native WDA weapon"));
            named.setItemSlot(EquipmentSlot.MAINHAND, sword.copy());
            named.getAttribute(Attributes.MAX_HEALTH).setBaseValue(60.0);
            named.setHealth(30.0F);
            UUID uuid = named.getUUID();
            var goals = named.goalSelector;
            var drops = named.getLootTable();
            f.add(named);
            check(QmDungeonEncounters.isPending(named), "Authored named persistent guard is enrolled");
            check(QmDungeonEncounters.activate(named, player), "Named guard activates in place");
            check(named.getUUID().equals(uuid) && named.goalSelector == goals && named.getLootTable().equals(drops), "UUID, AI and native loot remain attached");
            check(named.getName().getString().contains("Authored WDA Guard"), "Native authored name remains in display");
            check(ItemStack.isSameItemSameComponents(sword, named.getMainHandItem()), "Native weapon components remain intact");
            close(named.getAttribute(Attributes.MAX_HEALTH).getBaseValue(), 60.0, "Original health base is retained");
            close(named.getAttribute(Attributes.MAX_HEALTH).getModifier(QmDungeonEncounters.HEALTH_ID).amount(), 1.0, "WDA adds 100% of base health");
            close(named.getAttribute(Attributes.ATTACK_DAMAGE).getModifier(QmDungeonEncounters.DAMAGE_ID).amount(), 0.25, "WDA adds 25% of base attack");
            close(named.getHealth() / named.getMaxHealth(), 0.5, "Current health ratio is preserved");
            check(EliteMobManager.getAffix(uuid).isPresent(), "Pathfinder receives a real native Elite ability");
            check(!EliteMobManager.isTrackedElite(uuid), "No second Elite loot encounter is created");
            check(named.getData(Apoth.Attachments.TIER_AUGMENTS_APPLIED), "One Apotheosis tier is marked applied");
            double maxHealth = named.getMaxHealth();
            String display = named.getName().getString();
            Mob restored = f.roundTrip(named);
            close(restored.getMaxHealth(), maxHealth, "Native NBT round trip does not stack health");
            check(restored.getName().getString().equals(display), "Native NBT round trip does not prefix name again");
            check(EliteMobManager.getAffix(uuid).isPresent(), "Saved native ability registration is restored");
            check(!QmDungeonEncounters.activate(restored, player), "Saved active encounter cannot activate a second time");

            Mob hoglin = f.create("minecraft:hoglin", ORIGIN.offset(5, 1, 3), true);
            check(!(hoglin instanceof Monster), "Hoglin fixture exercises Enemy outside Monster hierarchy");
            check(QmDungeonEncounters.isPending(hoglin) && QmDungeonEncounters.activate(hoglin, player), "Native WDA hoglin receives an encounter");
            for (String id : List.of("minecraft:phantom", "minecraft:slime", "minecraft:magma_cube", "minecraft:ghast")) {
                Mob mob = f.create(id, ORIGIN.offset(5, 2, 5), false);
                mob.setCustomName(Component.literal("Native " + id));
                f.add(mob);
                check(!(mob instanceof PathfinderMob), "Fixture exercises native non-Pathfinder hierarchy: " + id);
                check(QmDungeonEncounters.isPending(mob) && QmDungeonEncounters.activate(mob, player), "Native non-Pathfinder receives scoped strength: " + id);
                check(data(mob).getString("abilityMode").equals("stats_only") && EliteMobManager.getAffix(mob.getUUID()).isEmpty(), "No invented native Elite ability: " + id);
                close(mob.getAttribute(Attributes.MAX_HEALTH).getModifier(QmDungeonEncounters.HEALTH_ID).amount(), 1.0, "Native hierarchy uses WDA health addition: " + id);
                check(mob.getName().getString().equals("Native " + id), "Stats-only path retains exact name: " + id);
                check(mob.getData(Apoth.Attachments.TIER_AUGMENTS_APPLIED), "Native hierarchy receives Apotheosis tier: " + id);
                double before = mob.getMaxHealth();
                Mob loaded = f.roundTrip(mob);
                close(loaded.getMaxHealth(), before, "Stats-only native NBT load keeps one modifier: " + id);
                check(!QmDungeonEncounters.activate(loaded, player), "Stats-only load cannot reroll: " + id);
            }
            for (String id : List.of("minecraft:cow", "minecraft:wither", "minecraft:warden", "minecraft:elder_guardian", "artifacts:mimic")) {
                Mob mob = f.create(id, ORIGIN.offset(6, 2, 6), true);
                check(!QmDungeonEncounters.isPending(mob) && !data(mob).getString("source").equals("arise"), "Passive, boss or mimic excluded: " + id);
            }
            Wolf pet = (Wolf) f.create("minecraft:wolf", ORIGIN.offset(6, 2, 6), false);
            pet.setOwnerUUID(UUID.randomUUID()); pet.setTame(true, false); f.add(pet);
            check(!QmDungeonEncounters.isPending(pet), "Owned pet is excluded");
            for (String marker : List.of(MobCompat45.ELITE_MARKER, "apoth.boss", "apoth.miniboss")) {
                Mob mob = f.create("minecraft:zombie", ORIGIN.offset(6, 2, 6), false);
                mob.getPersistentData().putBoolean(marker, true); f.add(mob);
                check(!QmDungeonEncounters.isPending(mob), "Existing encounter is excluded: " + marker);
                check(mob.getPersistentData().getLong("qm_arise_next_check") > f.level.getGameTime(), "Excluded encounter yields future discovery budget: " + marker);
            }
            Mob divine = f.create("divinerpg:cave_crawler", ORIGIN.offset(6, 2, 6), true);
            check(!data(divine).getString("source").equals("arise"), "Divine policy retains its own roll inside WDA bounds");
            divine.discard();
            EncounterConfig.ARISE_CHANCE.set(0.0);
            Mob declined = f.create("minecraft:zombie", ORIGIN.offset(6, 2, 6), true);
            check(data(declined).getInt("state") == QmDungeonEncounters.DECLINED, "Zero selection chance saves a declined roll");
            EncounterConfig.ARISE_CHANCE.set(1.0);
            check(!QmDungeonEncounters.considerArise(declined), "Changing chance does not reroll a saved decline");
            EncounterConfig.ACTIVATION_BUDGET.set(2);
            for (int i = 0; i < 5; i++) f.create("minecraft:zombie", ORIGIN.offset(7, 1, 7), true);
            check(QmDungeonEncounters.activateNearby(player) == 2, "Player-local pass respects activation budget");
            check(QmDungeonEncounters.activateNearby(player) == 2 && QmDungeonEncounters.activateNearby(player) == 1, "Remaining candidates are activated on later bounded passes");
            // A stable excluded crowd used to consume every discovery budget.
            // Choose the last cohort member in the actual native query order so
            // the fixture cannot accidentally pass by visiting the target first.
            List<Mob> crowd = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                Mob mob = f.create("minecraft:zombie", ORIGIN.offset(8, 1, 8), false);
                mob.getPersistentData().putBoolean(MobCompat45.ELITE_MARKER, true);
                f.add(mob);
                mob.getPersistentData().putLong("qm_arise_next_check", 0);
                crowd.add(mob);
            }
            List<Mob> ordered = f.level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(48), crowd::contains);
            check(ordered.size() == 40, "Discovery fairness fixture contains a stable native-query crowd");
            Mob late = ordered.getLast();
            late.getPersistentData().remove(MobCompat45.ELITE_MARKER);
            EncounterConfig.ARISE_DISCOVERY_BUDGET.set(8);
            for (int i = 0; i < 6 && data(late).getInt("state") != QmDungeonEncounters.ACTIVE; i++) QmDungeonEncounters.activateNearby(player);
            check(data(late).getInt("state") == QmDungeonEncounters.ACTIVE, "Excluded crowd yields subsequent discovery passes to the last native-query candidate");
            f.savedStartScan();
            return Map.of("assertions", assertions, "native_structure", ID.toString(), "non_pathfinder_types", 4,
                "native_nbt_round_trip", true, "saved_unloaded_start_without_chunk_load", true,
                "repaired_advancement_entries", 2);
        } finally {
            EncounterConfig.ENABLE_ARISE.set(enabled);
            EncounterConfig.ARISE_CHANCE.set(chance);
            EncounterConfig.ACTIVATION_BUDGET.set(budget);
            EncounterConfig.ARISE_DISCOVERY_BUDGET.set(discoveryBudget);
            EncounterConfig.AFFIXES.set(affixes);
            AriseStructures.clear();
        }
    }

    private static CompoundTag data(Entity entity) { return entity.getPersistentData().getCompound(QmDungeonEncounters.DATA_KEY); }
    private static void check(boolean condition, String message) { assertions++; if (!condition) throw new AssertionError(message); }
    private static void close(double actual, double expected, String message) { check(Math.abs(actual - expected) <= Math.max(0.0001, Math.abs(expected) * 0.00001), message + " (" + actual + " vs " + expected + ")"); }
    private static Field field(Class<?> owner, String name) throws Exception { Field f = owner.getDeclaredField(name); f.setAccessible(true); return f; }

    private static final class Fixture implements AutoCloseable {
        final ServerLevel level;
        final LevelChunk chunk;
        final Structure structure;
        final Map<Structure, StructureStart> previousStarts;
        final Map<Structure, LongSet> previousReferences = new HashMap<>();
        final PersistentEntitySectionManager<Entity> manager;
        final Visibility previousVisibility;
        final List<Entity> entities = new ArrayList<>();

        @SuppressWarnings("unchecked") Fixture(ServerLevel level) throws Exception {
            this.level = level;
            chunk = level.getChunk(ORIGIN.getX() >> 4, ORIGIN.getZ() >> 4);
            structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(ID);
            check(structure != null, "Pinned WDA native structure registry entry exists");
            previousStarts = new HashMap<>(chunk.getAllStarts());
            chunk.getAllReferences().forEach((key, values) -> previousReferences.put(key, new LongOpenHashSet(values)));
            manager = (PersistentEntitySectionManager<Entity>) field(ServerLevel.class, "entityManager").get(level);
            Long2ObjectMap<Visibility> states = (Long2ObjectMap<Visibility>) field(PersistentEntitySectionManager.class, "chunkVisibility").get(manager);
            previousVisibility = states.get(chunk.getPos().toLong());
            manager.updateChunkStatus(chunk.getPos(), Visibility.TRACKED);
            chunk.setAllStarts(new HashMap<>());
            chunk.setAllReferences(new HashMap<>());
            StructureStart start = new StructureStart(structure, chunk.getPos(), 0,
                new PiecesContainer(List.of(new BoxPiece(new BoundingBox(80, 250, 80, 95, 270, 95)))));
            chunk.setStartForStructure(structure, start);
            chunk.addReferenceForStructure(structure, chunk.getPos().toLong());
            AriseStructures.clear();
        }

        Mob create(String id, BlockPos position, boolean add) {
            Entity entity = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(id)).create(level);
            check(entity instanceof Mob, "Native fixture registry contains mob " + id);
            Mob mob = (Mob) entity;
            mob.moveTo(position, 0, 0);
            mob.setNoAi(true);
            if (add) add(mob);
            return mob;
        }

        void add(Mob mob) { check(level.addFreshEntity(mob), "Native entity manager accepts fixture mob"); entities.add(mob); }

        Mob roundTrip(Mob original) throws Exception {
            CompoundTag saved = new CompoundTag();
            check(original.save(saved), "Native serializer saves complete fixture mob");
            UUID uuid = original.getUUID();
            original.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
            Entity loaded = EntityType.loadEntityRecursive(saved, level, entity -> entity);
            check(loaded instanceof Mob && loaded.getUUID().equals(uuid), "Native NBT loader retains mob type and UUID");
            Method add = PersistentEntitySectionManager.class.getDeclaredMethod("addEntity", EntityAccess.class, boolean.class);
            add.setAccessible(true);
            check((Boolean) add.invoke(manager, loaded, true), "Native loaded-from-disk entity manager accepts saved mob");
            entities.add(loaded);
            return (Mob) loaded;
        }

        void savedStartScan() throws Exception {
            ChunkPos remote = new ChunkPos(100000, 100000);
            check(level.getChunkSource().getChunkNow(remote.x, remote.z) == null, "Saved origin starts unloaded");
            ChunkStorage storage = (ChunkStorage) field(ServerChunkCache.class, "chunkMap").get(level.getChunkSource());
            CompoundTag root = new CompoundTag();
            CompoundTag structures = new CompoundTag(), starts = new CompoundTag(), start = new CompoundTag(), piece = new CompoundTag();
            start.putString("id", ID.toString());
            piece.putIntArray("BB", new int[] {80, 250, 80, 95, 270, 95});
            ListTag children = new ListTag(); children.add(piece); start.put("Children", children);
            starts.put(ID.toString(), start); structures.put("starts", starts); root.put("structures", structures);
            storage.write(remote, root).join();
            try {
                chunk.setAllReferences(new HashMap<>());
                chunk.addReferenceForStructure(structure, remote.toLong());
                AriseStructures.clear();
                int loaded = level.getChunkSource().getLoadedChunksCount();
                boolean found = false;
                long deadline = System.nanoTime() + 3_000_000_000L;
                while (System.nanoTime() < deadline && !(found = AriseStructures.contains(level, ORIGIN.offset(4, 1, 4)))) Thread.sleep(10);
                check(found, "Production async scanner reads saved native start pieces");
                check(!AriseStructures.contains(level, ORIGIN.offset(4, -100, 4)), "Saved start scanner retains three-dimensional bounds");
                check(level.getChunkSource().getChunkNow(remote.x, remote.z) == null, "Reading saved native start does not load its origin chunk");
                check(level.getChunkSource().getLoadedChunksCount() == loaded, "No chunks are generated or loaded by saved start lookup");
            } finally { storage.write(remote, null).join(); }
        }

        @Override public void close() {
            for (Entity entity : entities) if (!entity.isRemoved()) entity.discard();
            chunk.setAllStarts(previousStarts);
            chunk.setAllReferences(previousReferences);
            manager.updateChunkStatus(chunk.getPos(), previousVisibility == null ? Visibility.HIDDEN : previousVisibility);
            AriseStructures.clear();
        }
    }

    /** Synthetic piece provides a controlled bound within a real native registered StructureStart. */
    private static final class BoxPiece extends StructurePiece {
        BoxPiece(BoundingBox box) { super(BuiltInRegistries.STRUCTURE_PIECE.iterator().next(), 0, box); }
        @Override protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) { }
        @Override public void postProcess(WorldGenLevel level, StructureManager structures, ChunkGenerator generator,
                RandomSource random, BoundingBox box, ChunkPos chunk, BlockPos origin) { }
    }
}
