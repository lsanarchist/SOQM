package soqm.validation.encounters;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.DungeonConfig;
import com.catastrophe573.dimdungeons.block.BlockGoldPortal;
import com.catastrophe573.dimdungeons.block.BlockPortalKeyhole;
import com.catastrophe573.dimdungeons.block.TileEntityGoldPortal;
import com.catastrophe573.dimdungeons.block.TileEntityPortalKeyhole;
import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.item.BaseItemKey;
import com.catastrophe573.dimdungeons.item.ItemBlankThemeKey;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;
import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import qm.dungeonencounters.EncounterConfig;
import qm.dungeonencounters.QmDungeonEncounters;

/** Actual DD placement, authored room navigation, native guards, locks and portal use. */
final class DungeonExpeditionProbe {
    private static final List<String> ROOMS = List.of("entrance_1", "fourway_1", "fourway_2",
            "threeway_1", "hallway_1", "hallway_2", "corner_1", "end_1", "end_2");
    private static int assertions;
    private DungeonExpeditionProbe() { }

    static Map<String, Object> run(MinecraftServer server) throws Exception {
        assertions = 0;
        ServerLevel level = server.getLevel(DimDungeons.DUNGEON_DIMENSION);
        check(level != null && DungeonConfig.themeSettings.size() == 5, "Exactly five native DD themes loaded");
        Map<String, Object> results = new LinkedHashMap<>();
        results.put("scoped_native_theme_restore", saveScope(server));
        try (DungeonTestPlayers players = new DungeonTestPlayers()) {
            ServerPlayer player = players.create(level, new BlockPos(512, 55, 512), WorldTier.HAVEN);
            ServerPlayer partner = players.create(level, new BlockPos(513, 55, 512), WorldTier.PINNACLE);
            results.put("authored_rooms", allRooms(level, player, partner));
            results.put("random_theme_gates", randomThemes(server, level, player));
        }
        results.put("generated_expeditions", generateAndEnter(server));
        results.put("assertions", assertions);
        return results;
    }

    private static Map<String, Object> saveScope(MinecraftServer server) throws Exception {
        List<String> structures = List.of("qm:dimensional_dungeons/citadel/end_1", "qm:dimensional_dungeons/abyss/end_2",
                "dimdungeons:themed/legacy", "foreign:dimensional_dungeons/citadel/end_1",
                "qm:dimensional_dungeons/citadel/end_1", "qm:dimensional_dungeons/abyss/end_1",
                "qm:dimensional_dungeons/citadel_extra/end_1", "qm:dimensional_dungeons/citadel/end_2");
        int[] savedThemes = {4, 5, 3, 4, 5, 4, 4, 0};
        int[] expected = {4, 5, 0, 0, 0, 0, 0, 0};
        CompoundTag source = new CompoundTag();
        for (String name : List.of("room_data", "remaining_builds")) {
            ListTag rooms = new ListTag();
            for (int i = 0; i < structures.size(); i++) {
                CompoundTag room = new CompoundTag();
                room.putInt("x", 2000 + i); room.putInt("z", 2000);
                room.putString("structure", structures.get(i));
                room.putString("rotation", "COUNTERCLOCKWISE_90"); room.putString("room_type", "END");
                room.putString("dungeon_type", "BASIC"); room.putInt("theme", savedThemes[i]); rooms.add(room);
            }
            source.put(name, rooms);
        }
        CompoundTag count = new CompoundTag(); count.putInt("numKeysActivated", 734); source.put("total_key_data", count);
        DungeonData loaded = DungeonData.load(source, server.registryAccess());
        check(loaded.getNumKeysRegistered() == 734, "Scoped restart repair preserves the native address counter");
        CompoundTag roundTrip = loaded.save(new CompoundTag(), server.registryAccess());
        for (String name : List.of("room_data", "remaining_builds")) {
            ListTag saved = roundTrip.getList(name, 10);
            check(saved.size() == structures.size(), "Scoped restart repair neither adds nor removes native room records");
            for (int j = 0; j < saved.size(); j++) {
                CompoundTag room = saved.getCompound(j); int i = room.getInt("x") - 2000;
                check(room.getInt("theme") == expected[i], "Save-load-save repairs only matching authored expedition themes: " + name + "/" + structures.get(i));
                check(room.getInt("z") == 2000 && room.getString("structure").equals(structures.get(i))
                                && room.getString("rotation").equals("COUNTERCLOCKWISE_90") && room.getString("room_type").equals("END")
                                && room.getString("dungeon_type").equals("BASIC"),
                        "Scoped restart repair preserves every unrelated native room property");
            }
        }
        return Map.of("native_save_load_save_cases", 16, "matching_new_themes_restored", 4,
                "foreign_legacy_mismatched_cases_unchanged", 12, "key_counter_unchanged", true);
    }

    private static Map<String, Object> allRooms(ServerLevel level, ServerPlayer player, ServerPlayer partner) throws Exception {
        int placements = 0, mobs = 0, locks = 0, recoverablePitNodes = 0;
        Set<String> species = new HashSet<>();
        for (int theme : new int[] {4, 5}) {
            String family = theme == 4 ? "citadel" : "abyss";
            for (String name : ROOMS) for (Rotation rotation : Rotation.values()) {
                ChunkPos chunk = new ChunkPos(32 + placements % 12 * 2, 32 + placements / 12 * 2);
                BlockPos origin = new BlockPos(chunk.getMinBlockX(), 50, chunk.getMinBlockZ());
                DungeonRoom room = room(family, name, rotation, theme);
                player.moveTo(origin.offset(8, 5, 8), 0, 0);
                partner.moveTo(origin.offset(8, 5, 9), 0, 0);
                try (Spawns spawned = new Spawns(level)) {
                    check(DungeonPlacement.putRoomHere(chunk, level, room), "Native DD places " + room.structure + "/" + rotation);
                    int expectedMobs = name.startsWith("entrance") ? 0 : name.startsWith("end") ? 4
                            : theme == 4 && (name.startsWith("fourway") || name.startsWith("threeway") || name.startsWith("corner")) ? 4 : 3;
                    check(spawned.mobs.size() == expectedMobs, "Finite native marker mob count: " + room.structure + "/" + rotation);
                    for (Mob mob : spawned.mobs) {
                        species.add(BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString());
                        check(!level.getBlockCollisions(mob, mob.getBoundingBox()).iterator().hasNext(),
                                "Actual spawned entity AABB is clear after marker processing: " + room.structure + "/" + rotation
                                        + " " + BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()) + " " + mob.blockPosition());
                        check(mob instanceof PathfinderMob && QmDungeonEncounters.isPending(mob),
                                "Authored hostile is enrolled by the native DD RETURN hook");
                        check(mob.getPersistentData().getCompound(QmDungeonEncounters.DATA_KEY).getInt("theme") == theme,
                                "Native spawn carries exact expedition theme provenance");
                        check(QmDungeonEncounters.activate((PathfinderMob) mob, player), "New expedition mob activates with the nearby player's tier");
                        CompoundTag saved = mob.getPersistentData().getCompound(QmDungeonEncounters.DATA_KEY);
                        check(saved.getString("eliteTier").equals("CHAMPION") && saved.getString("worldTier").equals("haven"),
                                "New expedition gives a real Champion ability using the opening player's WorldTier");
                        double multiplier = theme == 4 ? EncounterConfig.CITADEL_HEALTH_MULTIPLIER.get() : EncounterConfig.ABYSS_HEALTH_MULTIPLIER.get();
                        close(mob.getAttribute(Attributes.MAX_HEALTH).getModifier(QmDungeonEncounters.HEALTH_ID).amount(), multiplier - 1,
                                "Theme-specific strength applied exactly once");
                        check(!QmDungeonEncounters.activate((PathfinderMob) mob, partner), "Second player's tier cannot stack or reroll the expedition encounter");
                        mobs++;
                    }
                    recoverablePitNodes += navigation(level, origin, room);
                    for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) {
                        BlockPos pos = origin.offset(x, 5, z);
                        if (!(level.getBlockEntity(pos) instanceof RandomizableContainerBlockEntity chest)) continue;
                        String kind = name.startsWith("end") ? "treasure" : "supplies";
                        check(chest.getLootTable() != null && chest.getLootTable().location().toString()
                                .equals("qm:chest/dimensional_dungeons/" + family + "_" + kind),
                                "Native data marker selected the authored themed chest loot table");
                        if (kind.equals("treasure")) {
                            lockedChest(level, pos, chest, spawned.mobs, player, partner);
                            locks++;
                        }
                    }
                    check(level.getChunkAt(origin).getBlockEntities().values().stream().noneMatch(be -> be.getBlockState().is(Blocks.SPAWNER)),
                            "Authored room contains no repeatable mob spawner");
                }
                placements++;
            }
        }
        check(locks == 16 && species.stream().anyMatch(id -> id.startsWith("divinerpg:")),
                "Every locked variant and rotation exercises real Divine guards");
        return Map.of("native_placements", placements, "rotations_per_template", 4, "original_templates", 18,
                "native_hostiles", mobs, "locked_chests", locks, "recoverable_pit_positions", recoverablePitNodes,
                "entity_types", species.stream().sorted().toList());
    }

    private static DungeonRoom room(String family, String name, Rotation rotation, int theme) {
        DungeonRoom room = new DungeonRoom();
        room.structure = "qm:dimensional_dungeons/" + family + "/" + name;
        room.rotation = rotation; room.theme = theme; room.dungeonType = DungeonType.BASIC;
        room.roomType = RoomType.valueOf(name.substring(0, name.indexOf('_')).toUpperCase(java.util.Locale.ROOT));
        return room;
    }

    private static void lockedChest(ServerLevel level, BlockPos pos, RandomizableContainerBlockEntity chest,
            List<Mob> mobs, ServerPlayer first, ServerPlayer second) throws Exception {
        String lock = chest.saveWithoutMetadata(level.registryAccess()).getString("Lock");
        check(!lock.isBlank() && lock.startsWith("Chest Key: "), "Native LockWithCode installs the per-room code");
        List<Mob> guards = mobs.stream().filter(mob -> {
            ItemStack item = mob.getItemBySlot(EquipmentSlot.CHEST);
            return item.is(Items.STICK) && item.has(DataComponents.CUSTOM_NAME)
                    && item.get(DataComponents.CUSTOM_NAME).getString().equals(lock);
        }).toList();
        check(guards.size() == 1, "Exactly one actual native keyholder carries the matching room key");
        Mob guard = guards.getFirst();
        Method dropChance = Mob.class.getDeclaredMethod("getEquipmentDropChance", EquipmentSlot.class);
        dropChance.setAccessible(true);
        close((Float) dropChance.invoke(guard, EquipmentSlot.CHEST), 1, "Encounter preserves native guaranteed key drop");
        List<ItemStack> deathKeys = new ArrayList<>();
        Consumer<LivingDropsEvent> drops = event -> {
            if (event.getEntity() == guard) for (ItemEntity item : event.getDrops()) {
                ItemStack stack = item.getItem();
                if (stack.is(Items.STICK) && stack.has(DataComponents.CUSTOM_NAME)
                        && stack.get(DataComponents.CUSTOM_NAME).getString().equals(lock)) deathKeys.add(stack.copy());
            }
        };
        NeoForge.EVENT_BUS.addListener(drops);
        // Native DD uses equipment chance exactly 1.0, so a real recent player
        // damage event is required by vanilla. Calling die() directly does not
        // establish that credit. Protect only this transport-only test player
        // from the native Reflective ability during the deliberately lethal hit.
        first.setInvulnerable(true);
        try {
            check(guard.hurt(level.damageSources().playerAttack(first), 1000000), "Native player damage reaches the keyholder death path");
            check(!guard.isAlive(), "Native player damage actually kills the keyholder");
        } finally { first.setInvulnerable(false); NeoForge.EVENT_BUS.unregister(drops); }
        check(deathKeys.size() == 1 && deathKeys.getFirst().getCount() == 1, "Native keyholder death drops exactly one matching key");

        // Invoke Lootr's own conversion routine, including its specific-data
        // copy and restore. Never synthesize or reapply the lock in this test.
        if (!(chest instanceof ILootrBlockEntity)) {
            Method convert = BlockEntityTicker.class.getDeclaredMethod("replaceEntitiesInChunk", ServerLevel.class, BlockEntityTicker.Entry.class);
            convert.setAccessible(true);
            convert.invoke(null, level, new BlockEntityTicker.Entry(new ChunkPos(pos), Set.of(pos)));
        }
        check(level.getBlockEntity(pos) instanceof ILootrBlockEntity, "Actual Lootr converter accepts the generated native locked chest");
        ILootrBlockEntity provider = (ILootrBlockEntity) level.getBlockEntity(pos);
        check(lock.equals(provider.asBlockEntity().saveWithoutMetadata(level.registryAccess()).getString("Lock")),
                "Lootr conversion retains the exact DD native LockCode");
        Object previousInventory = null;
        for (ServerPlayer player : List.of(first, second)) {
            player.moveTo(pos.above(), 0, 0);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            check(!provider.canPlayerOpen(player), "Lootr rejects a survival opener without the room key");
            var before = player.containerMenu;
            LootrAPI.handleProviderOpen(provider, player);
            check(player.containerMenu == before, "Actual Lootr open handler cannot bypass native locked access");
            ItemStack wrong = new ItemStack(Items.STICK); wrong.set(DataComponents.CUSTOM_NAME, Component.literal("Wrong key"));
            player.setItemInHand(InteractionHand.MAIN_HAND, wrong);
            check(!provider.canPlayerOpen(player), "A different named stick cannot unlock this room");
            player.setItemInHand(InteractionHand.MAIN_HAND, deathKeys.getFirst().copy());
            check(provider.canPlayerOpen(player), "The native guard's dropped key unlocks the personal chest");
            LootrAPI.handleProviderOpen(provider, player);
            check(player.containerMenu != before, "Actual Lootr open handler creates a menu only after matching-key validation");
            var inventory = LootrAPI.getInventory(provider, player);
            check(inventory != null && !inventory.isEmpty(), "Unlocked Lootr chest contains real personal treasure");
            if (previousInventory != null) check(inventory != previousInventory, "Matching lock still yields separate multiplayer inventories");
            previousInventory = inventory;
            CompoundTag snapshot = inventory.saveToTag(level.registryAccess());
            player.closeContainer();
            check(snapshot.equals(LootrAPI.getInventory(provider, player).saveToTag(level.registryAccess())),
                    "Reopening a completed room does not repeat chest rewards");
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    /** Directed walk graph uses actual server block collision shapes, including rotated ladders. */
    private static int navigation(ServerLevel level, BlockPos origin, DungeonRoom room) {
        Set<BlockPos> nodes = new HashSet<>();
        for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) for (int y = 1; y <= 8; y++) {
            BlockPos pos = origin.offset(x, y, z);
            if (walkable(level, pos)) nodes.add(pos);
        }
        Map<BlockPos, List<BlockPos>> graph = new HashMap<>(), reverse = new HashMap<>();
        for (BlockPos pos : nodes) {
            List<BlockPos> edges = graph.computeIfAbsent(pos, p -> new ArrayList<>());
            for (Direction dir : Direction.Plane.HORIZONTAL) for (int dy = -4; dy <= 1; dy++) {
                BlockPos next = pos.relative(dir).offset(0, dy, 0);
                if (!nodes.contains(next)) continue;
                // Step-up needs clear space at the original head; drops need
                // clear intermediate space in the target column.
                boolean free = true;
                for (int y = Math.min(pos.getY(), next.getY()); y <= Math.max(pos.getY(), next.getY()); y++) {
                    BlockPos column = new BlockPos(next.getX(), y, next.getZ());
                    if (!bodyClear(level, column)) { free = false; break; }
                }
                if (dy == 1 && !bodyClear(level, pos.above())) free = false;
                if (free) edges.add(next);
            }
            if (ladder(level, pos) || ladder(level, pos.below())) {
                for (int dy : new int[] {-1, 1}) {
                    BlockPos next = pos.offset(0, dy, 0);
                    if (nodes.contains(next)) edges.add(next);
                }
            }
            for (BlockPos next : edges) reverse.computeIfAbsent(next, p -> new ArrayList<>()).add(pos);
        }
        BlockPos center = origin.offset(8, 5, 8);
        check(nodes.contains(center), "Room's central crossing fits the actual player body: " + room.structure + "/" + room.rotation);
        Set<BlockPos> reachable = reachable(graph, center), returnable = reachable(reverse, center);
        Map<Direction, Boolean> doors = Map.of(Direction.NORTH, room.hasDoorNorth(), Direction.SOUTH, room.hasDoorSouth(),
                Direction.WEST, room.hasDoorWest(), Direction.EAST, room.hasDoorEast());
        for (var door : doors.entrySet()) if (door.getValue()) {
            boolean accessible = false;
            for (int lateral = 6; lateral <= 9; lateral++) {
                BlockPos edge = switch (door.getKey()) {
                    case NORTH -> origin.offset(lateral, 5, 0);
                    case SOUTH -> origin.offset(lateral, 5, 15);
                    case WEST -> origin.offset(0, 5, lateral);
                    case EAST -> origin.offset(15, 5, lateral);
                    default -> throw new AssertionError();
                };
                accessible |= reachable.contains(edge) && returnable.contains(edge);
            }
            check(accessible, "Native logical door has a real two-way player path after rotation: " + room.structure + "/" + room.rotation + "/" + door.getKey());
        }
        int pits = 0;
        for (BlockPos pos : nodes) if (pos.getY() == origin.getY() + 1) {
            check(returnable.contains(pos), "Every reachable-depth pit floor has a climbable exit: " + room.structure + "/" + room.rotation + " " + pos.subtract(origin));
            pits++;
        }
        return pits;
    }

    private static boolean walkable(ServerLevel level, BlockPos pos) {
        return bodyClear(level, pos) && (ladder(level, pos) || ladder(level, pos.below())
                || !level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty());
    }
    private static boolean bodyClear(ServerLevel level, BlockPos pos) {
        AABB body = new AABB(pos.getX() + .2, pos.getY() + .001, pos.getZ() + .2,
                pos.getX() + .8, pos.getY() + 1.8, pos.getZ() + .8);
        return !level.getBlockCollisions(null, body).iterator().hasNext();
    }
    private static boolean ladder(ServerLevel level, BlockPos pos) { return level.getBlockState(pos).is(BlockTags.CLIMBABLE); }
    private static Set<BlockPos> reachable(Map<BlockPos, List<BlockPos>> graph, BlockPos start) {
        Set<BlockPos> result = new HashSet<>(); ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        result.add(start); queue.add(start);
        while (!queue.isEmpty()) for (BlockPos next : graph.getOrDefault(queue.removeFirst(), List.of()))
            if (result.add(next)) queue.addLast(next);
        return result;
    }

    private static Map<String, Object> randomThemes(MinecraftServer server, ServerLevel level, ServerPlayer player) throws Exception {
        Set<Integer> rituals = new HashSet<>(), offhand = new HashSet<>();
        for (int i = 0; i < 48; i++) {
            player.getInventory().clearContent();
            ItemStack blank = item("dimdungeons:item_blank_theme_key");
            player.getInventory().setItem(0, blank);
            ((ItemBlankThemeKey) blank.getItem()).performActivationRitual(player, blank, level, player.blockPosition());
            ItemStack activated = player.getInventory().getItem(0);
            check(activated.getItem() instanceof BaseItemKey && ((BaseItemKey) activated.getItem()).isActivated(activated), "Native blank theme ritual creates a real activated key");
            int theme = ((BaseItemKey) activated.getItem()).getDungeonTheme(activated);
            check(theme >= 1 && theme <= 3, "Native ordinary random ritual cannot skip into earned expedition themes");
            rituals.add(theme);
        }
        player.getInventory().clearContent();
        Method spawn = DungeonPlacement.class.getDeclaredMethod("spawnEnemyHere", BlockPos.class, String.class,
                ServerLevel.class, int.class, DungeonType.class);
        spawn.setAccessible(true);
        int chance = DungeonConfig.chanceForThemeKeys;
        try (Spawns captured = new Spawns(level)) {
            DungeonConfig.chanceForThemeKeys = 100;
            for (int i = 0; i < 48; i++) {
                Mob mob = (Mob) spawn.invoke(null, new BlockPos(512, 80, 512), "minecraft:creeper", level, 0, DungeonType.BASIC);
                ItemStack key = mob.getItemBySlot(EquipmentSlot.OFFHAND);
                check(key.getItem() instanceof BaseItemKey && ((BaseItemKey) key.getItem()).isActivated(key), "Native DD chance path gives an actual offhand key");
                int theme = ((BaseItemKey) key.getItem()).getDungeonTheme(key);
                check(theme >= 1 && theme <= 3, "Native ordinary DD offhand reward cannot bypass expedition progression");
                offhand.add(theme);
            }
        } finally { DungeonConfig.chanceForThemeKeys = chance; }
        check(rituals.size() == 3 && offhand.size() == 3, "Both native random key paths still cover all three original themes");
        return Map.of("native_blank_rituals", 48, "native_offhand_rolls", 48, "ordinary_themes", rituals.stream().sorted().toList());
    }

    private static Map<String, Object> generateAndEnter(MinecraftServer server) throws Exception {
        ServerLevel dungeon = server.getLevel(DimDungeons.DUNGEON_DIMENSION), overworld = server.overworld();
        Map<String, Object> results = new LinkedHashMap<>();
        try (DungeonTestPlayers players = new DungeonTestPlayers()) {
            ServerPlayer player = players.create(overworld, new BlockPos(32, 100, 32), WorldTier.PINNACLE);
            for (int theme : new int[] {4, 5}) {
                ItemStack key = earnedKey(server, player, theme);
                BaseItemKey nativeKey = (BaseItemKey) key.getItem();
                check(nativeKey.getDungeonType(key) == DungeonType.BASIC && nativeKey.getKeyLevel(key) == 1,
                        "Earned themed key preserves native BASIC data and theme-specific spawning");
                BlockPos keyholePos = new BlockPos(32 + theme * 16, 100, 32);
                TileEntityPortalKeyhole keyhole = frame(overworld, keyholePos, key);
                keyhole.removeContents();
                player.moveTo(keyholePos.north().below(2), 0, 0);
                player.setItemInHand(InteractionHand.MAIN_HAND, key);
                BlockPortalKeyhole keyholeBlock = (BlockPortalKeyhole) overworld.getBlockState(keyholePos).getBlock();
                Method insert = BlockPortalKeyhole.class.getDeclaredMethod("useItemOn", ItemStack.class, BlockState.class,
                        Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
                insert.setAccessible(true);
                dungeon.getRandom().setSeed(880000L + theme);
                insert.invoke(keyholeBlock, key, overworld.getBlockState(keyholePos), overworld, keyholePos, player,
                        InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(keyholePos), Direction.NORTH, keyholePos, false));
                check(keyhole.isActivated() && overworld.getBlockState(keyholePos).getValue(BlockPortalKeyhole.IS_BUILDING),
                        "Actual keyhole insertion starts native expedition generation");
                ItemStack inserted = keyhole.getObjectInserted();
                long topX = nativeKey.getDungeonTopLeftX(inserted), topZ = nativeKey.getDungeonTopLeftZ(inserted);
                DungeonData data = DungeonData.get(dungeon);
                List<ChunkPos> rooms = new ArrayList<>();
                int ends = 0;
                for (int x = 4; x < 12; x++) for (int z = 4; z < 12; z++) {
                    ChunkPos cp = new ChunkPos((int) topX / 16 + x, (int) topZ / 16 + z);
                    DungeonRoom room = data.getRoomAtPos(cp);
                    if (room == null) continue;
                    check(room.theme == theme && room.structure.startsWith("qm:dimensional_dungeons/" + (theme == 4 ? "citadel/" : "abyss/")),
                            "Native designer selects only the requested original expedition rooms");
                    if (room.roomType == RoomType.END) ends++;
                    rooms.add(cp);
                }
                int target = theme == 4 ? 32 : 42;
                check(DungeonConfig.themeSettings.get(theme - 1).themeDungeonSize == target,
                        "Native designer receives the configured expedition room target");
                // DD's target is a soft stopping threshold: it closes pending
                // branches, so the final 8x8 layout can under/overshoot it.
                check(rooms.size() >= target / 2 && rooms.size() <= 64 && ends > 0,
                        "Native seeded expedition produces a substantial finite layout with locked ends: target="
                                + target + " actual=" + rooms.size() + " ends=" + ends);
                CompoundTag pending = data.save(new CompoundTag(), server.registryAccess());
                DungeonData restored = DungeonData.load(pending, server.registryAccess());
                for (ChunkPos cp : rooms) check(restored.getRoomAtPos(cp).theme == theme,
                        "Native save/load retains the new theme while a build is pending");
                var pendingField = DungeonData.class.getDeclaredField("remainingBuilds"); pendingField.setAccessible(true);
                @SuppressWarnings("unchecked") Map<ChunkPos, DungeonRoom> restoredPending = (Map<ChunkPos, DungeonRoom>) pendingField.get(restored);
                for (ChunkPos cp : rooms) check(restoredPending.get(cp).theme == theme,
                        "An interrupted build resumes with the same theme enemy pool and treasure routing");
                int spawnedCount;
                try (Spawns spawned = new Spawns(dungeon)) {
                    int ticks = 0;
                    while (data.hasMoreRoomsToBuild() && ticks++ < 100000) data.tick(dungeon);
                    check(!data.hasMoreRoomsToBuild(), "Native queue finishes all expedition rooms");
                    spawnedCount = spawned.mobs.size();
                    check(spawnedCount > 0, "Real generated expedition contains finite hostile encounters");
                    for (ChunkPos cp : rooms) {
                        check(DungeonPlacement.wasRoomBuiltAtChunk(dungeon, cp), "Native bedrock build marker records completed room");
                        check(!DungeonPlacement.buildRoomAtChunk(dungeon, cp), "Native revisit does not regenerate treasure or respawn defeated guards");
                    }
                    check(spawned.mobs.size() == spawnedCount, "Revisiting every built room adds zero native mobs");
                    TileEntityPortalKeyhole.tick(overworld, keyholePos, overworld.getBlockState(keyholePos), keyhole);
                    check(overworld.getBlockEntity(keyholePos.below()) instanceof TileEntityGoldPortal, "Native finished build opens the crowned entrance portal");
                    BlockGoldPortal portal = (BlockGoldPortal) overworld.getBlockState(keyholePos.below()).getBlock();
                    check(portal.checkPortalIntegrity(overworld.getBlockState(keyholePos.below()), overworld, keyholePos.below()), "Actual generated portal passes native advanced frame validation");
                    expirePortalCooldown(overworld, keyholePos.below());
                    portal.entityInside(overworld.getBlockState(keyholePos.below()), overworld, keyholePos.below(), player);
                    check(player.serverLevel() == dungeon, "Actual gold portal entity interaction enters the generated expedition");
                    // There is no graphical client to acknowledge the completed
                    // dimension transition in this in-memory transport fixture.
                    player.hasChangedDimension();
                    ChunkPos arrival = player.chunkPosition();
                    check(data.getRoomAtPos(arrival) != null && data.getRoomAtPos(arrival).theme == theme,
                            "Portal destination is the newly generated themed entrance room");
                    BlockPos exit = new BlockPos((int) nativeKey.getWarpX(inserted), 55, (int) nativeKey.getWarpZ(inserted) + 2);
                    check(dungeon.getBlockEntity(exit) instanceof TileEntityGoldPortal, "Original room supplies the native return portal doorway");
                    TileEntityGoldPortal returnTile = (TileEntityGoldPortal) dungeon.getBlockEntity(exit);
                    check(returnTile.getDestinationDimension().equals(Level.OVERWORLD)
                            && returnTile.getDestination().equals(BlockPortalKeyhole.getReturnPoint(overworld.getBlockState(keyholePos), keyholePos)),
                            "Native builder programs the exact safe return point");
                    expirePortalCooldown(dungeon, exit);
                    ((BlockGoldPortal) dungeon.getBlockState(exit).getBlock()).entityInside(dungeon.getBlockState(exit), dungeon, exit, player);
                    check(player.serverLevel() == overworld, "Actual native return portal takes the player home");
                    player.hasChangedDimension();
                }
                DungeonData after = DungeonData.load(data.save(new CompoundTag(), server.registryAccess()), server.registryAccess());
                for (ChunkPos cp : rooms) check(after.getRoomAtPos(cp).theme == theme, "Completed expedition theme persists through native NBT reload");
                results.put(Integer.toString(theme), Map.of("configured_room_target", target, "actual_rooms", rooms.size(), "locked_end_rooms", ends,
                        "spawned_hostiles", spawnedCount, "native_entry_and_return", true, "save_reload", true));
            }
        }
        return results;
    }

    private static void expirePortalCooldown(ServerLevel level, BlockPos pos) {
        TileEntityGoldPortal portal = (TileEntityGoldPortal) level.getBlockEntity(pos);
        // DD initializes every new portal with a nonzero cooldown. Model its
        // already-expired state through the native setter, at the prior tick,
        // rather than changing global production config or bypassing teleport.
        portal.setCooldown(0, level, pos, level.getServer().getTickCount() - 1);
        check(portal.getCooldown() == 0 && portal.needsUpdateThisTick(level.getServer().getTickCount()),
                "Fixture portal has an expired native cooldown before player contact");
    }

    private static ItemStack earnedKey(MinecraftServer server, ServerPlayer player, int theme) {
        var table = DungeonRewardProbe.table(server, theme == 4 ? "dimdungeons:chest/chestloot_advanced_hard"
                : "qm:chest/dimensional_dungeons/citadel_treasure");
        for (int seed = 1; seed <= 128; seed++) for (ItemStack stack : table.getRandomItems(
                DungeonRewardProbe.params(player.serverLevel(), player, player.position()), seed)) {
            if (stack.getItem() instanceof BaseItemKey key && key.isActivated(stack) && key.getDungeonTheme(stack) == theme) return stack.copy();
        }
        throw new AssertionError("Actual earned-key table failed to yield theme " + theme);
    }

    private static TileEntityPortalKeyhole frame(ServerLevel level, BlockPos pos, ItemStack key) {
        level.getChunkAt(pos);
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-4, -3, -2), pos.offset(4, 1, 2))) level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
        Block keyhole = block("dimdungeons:block_portal_keyhole");
        level.setBlock(pos, keyhole.defaultBlockState().setValue(BlockPortalKeyhole.FACING, Direction.NORTH), 2);
        TileEntityPortalKeyhole tile = (TileEntityPortalKeyhole) level.getBlockEntity(pos); tile.setContents(key.copy());
        for (BlockPos p : List.of(pos.west().below(), pos.west().below(2), pos.east().below(), pos.east().below(2), pos.below(3),
                pos.west(3).below(2), pos.west(3).below(3), pos.east(3).below(2), pos.east(3).below(3)))
            level.setBlock(p, Blocks.STONE_BRICKS.defaultBlockState(), 2);
        for (BlockPos p : List.of(pos.west(3).below(), pos.east(3).below())) level.setBlock(p, block("dimdungeons:block_gilded_portal").defaultBlockState(), 2);
        BlockGoldPortal portal = (BlockGoldPortal) block("dimdungeons:block_gold_portal");
        BlockState portalState = portal.defaultBlockState().setValue(BlockGoldPortal.AXIS, Direction.Axis.X);
        check(!portal.checkPortalIntegrity(portalState, level, pos.below()), "Earned theme cannot use a basic uncrowned frame");
        level.setBlock(pos.west(), block("dimdungeons:block_portal_crown").defaultBlockState(), 2);
        check(!portal.checkPortalIntegrity(portalState, level, pos.below()), "One crown is insufficient for the expedition gate");
        level.setBlock(pos.east(), block("dimdungeons:block_portal_crown").defaultBlockState(), 2);
        check(!portal.checkPortalIntegrity(portalState, level, pos.below()), "Two crowns without upgraded side banners are insufficient");
        for (BlockPos p : List.of(pos.west(3).below().north(), pos.east(3).below().north()))
            level.setBlock(p, Blocks.WHITE_WALL_BANNER.defaultBlockState().setValue(WallBannerBlock.FACING, Direction.NORTH), 2);
        check(portal.checkPortalIntegrity(portalState, level, pos.below()), "Complete native crowned frame accepts the earned expedition key");
        return tile;
    }

    private static Block block(String id) {
        ResourceLocation key = ResourceLocation.parse(id); check(BuiltInRegistries.BLOCK.containsKey(key), "Native block is registered: " + id);
        return BuiltInRegistries.BLOCK.get(key);
    }
    private static ItemStack item(String id) { return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id))); }
    private static final class Spawns implements AutoCloseable {
        final List<Mob> mobs = new ArrayList<>();
        final Consumer<EntityJoinLevelEvent> listener;
        Spawns(ServerLevel level) {
            listener = event -> { if (event.getLevel() == level && event.getEntity() instanceof Mob mob) { mobs.add(mob); mob.setNoAi(true); } };
            NeoForge.EVENT_BUS.addListener(listener);
        }
        @Override public void close() { NeoForge.EVENT_BUS.unregister(listener); mobs.forEach(Entity::discard); }
    }
    private static void close(double actual, double expected, String message) { check(Math.abs(actual - expected) < .0001, message); }
    private static void check(boolean condition, String message) { assertions++; if (!condition) throw new AssertionError(message); }
}
