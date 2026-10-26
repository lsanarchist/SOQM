package soqm.validation.encounters;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import qm.chests.SparseChestLoot;

/** Native registry, final-fill, real WDA loot and already-saved Lootr fixtures. */
final class AccessoryLootProbe {
    private static int assertions;
    private static DungeonTestPlayers persistentPlayers;
    private static final List<PersonalChest> PERSONAL = new ArrayList<>();
    private record PersonalChest(ILootrBlockEntity provider, ServerPlayer player,
                                 ILootrInventory inventory, CompoundTag saved) { }
    private AccessoryLootProbe() { }

    static Map<String, Object> run(MinecraftServer server) throws Exception {
        assertions = 0;
        JsonObject policy;
        try (var reader = new InputStreamReader(SparseChestLoot.class.getResourceAsStream("/qm_chest_policy.json"),
                StandardCharsets.UTF_8)) { policy = JsonParser.parseReader(reader).getAsJsonObject(); }
        JsonObject accessories = policy.getAsJsonObject("accessories");
        Set<String> pool = new HashSet<>();
        for (var value : accessories.getAsJsonObject("pools").getAsJsonArray("general")) {
            String id = value.getAsString();
            check(pool.add(id) && SparseChestLoot.isAccessory(item(id)), "Curated item is a distinct registered accessory: " + id);
            check(!SparseChestLoot.isBlocked(item(id)), "Curated accessory obeys existing progression exclusions");
        }
        check(pool.size() == 46, "Broader pool contains 37 Artifacts and nine native Overworld Relics");
        check(pool.stream().filter(id -> id.startsWith("artifacts:")).count() == 37, "Artifacts variety count is intentional");
        Set<String> observed = duplicates(pool);
        Map<String, Double> rates = bonuses(pool);
        int loadedRoutes = 0;
        for (var entry : policy.getAsJsonObject("routes").entrySet()) {
            if (!entry.getKey().startsWith("dungeons_arise:")) continue;
            DungeonRewardProbe.table(server, entry.getKey());
            check(entry.getValue().getAsString().equals(SparseChestLoot.profile(ResourceLocation.parse(entry.getKey()))),
                    "Packaged route covers the actual WDA outer table");
            loadedRoutes++;
        }
        check(loadedRoutes == 128, "Only the 128 valid reviewed outer WDA chest/barrel tables are routed");
        check(SparseChestLoot.profile(ResourceLocation.parse("dungeons_arise:chests/infested_temple/infested_temple_vault_normal")) == null,
                "Native WDA vault block rewards are outside the chest policy");
        int nativeRolls = nativeWda(server, pool);
        personalLootr(server);
        return Map.of("registered_pool_types", pool.size(), "observed_general_types", observed.size(),
                "duplicate_component_cases", 512, "seeded_bonus_samples", 12288, "bonus_rates", rates,
                "loaded_wda_outer_routes", loadedRoutes, "actual_wda_loot_rolls", nativeRolls,
                "saved_personal_inventories", PERSONAL.size(), "assertions", assertions);
    }

    private static Set<String> duplicates(Set<String> pool) {
        ResourceLocation table = ResourceLocation.parse("minecraft:chests/ancient_city");
        Set<String> observed = new HashSet<>();
        for (int seed = 0; seed < 512; seed++) {
            ItemStack original = item("artifacts:cross_necklace");
            original.set(DataComponents.CUSTOM_NAME, Component.literal("Native original " + seed));
            CompoundTag data = new CompoundTag(); data.putInt("foreign_seed", seed);
            original.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
            ItemStack other = item("artifacts:running_shoes");
            other.set(DataComponents.CUSTOM_NAME, Component.literal("Later native reward"));
            ItemStack duplicate = original.copy();
            duplicate.set(DataComponents.CUSTOM_NAME, Component.literal("Duplicate should be replaced"));
            var items = new ObjectArrayList<ItemStack>();
            items.add(original.copy()); items.add(duplicate); items.add(original.copy()); items.add(other.copy());
            SparseChestLoot.compact(table, items, 27, RandomSource.create(seed * 77191L));
            check(items.size() == 4, "Duplicate substitutions preserve the native accessory quantity");
            check(items.stream().allMatch(SparseChestLoot::isAccessory), "Substitutions are real accessories");
            check(items.stream().map(AccessoryLootProbe::id).distinct().count() == 4, "Every output type is distinct within this chest");
            check(items.stream().anyMatch(value -> ItemStack.isSameItemSameComponents(original, value)),
                    "First native accessory retains its complete original data");
            check(items.stream().anyMatch(value -> ItemStack.isSameItemSameComponents(other, value)),
                    "A replacement never takes a later native reward's identity");
            for (ItemStack value : items) { check(value.getCount() == 1 && pool.contains(id(value)), "Unique curated single item"); observed.add(id(value)); }
            var small = new ObjectArrayList<ItemStack>(); small.add(original.copy()); small.add(original.copy()); small.add(other.copy());
            SparseChestLoot.compact(table, small, 2, RandomSource.create(seed));
            check(small.size() == 2 && small.stream().anyMatch(value -> ItemStack.isSameItemSameComponents(other, value)),
                    "Physical capacity keeps native distinct rewards before replacement copies");
        }
        check(observed.equals(pool), "Seeded duplicate replacements exercise every reviewed accessory type");
        return observed;
    }

    private static Map<String, Double> bonuses(Set<String> pool) {
        Map<String, Double> observed = new LinkedHashMap<>();
        String[] tables = {"minecraft:chests/abandoned_mineshaft", "minecraft:chests/ancient_city", "minecraft:chests/end_city_treasure"};
        double[] probabilities = {.08, .12, .16};
        for (int profile = 0; profile < tables.length; profile++) {
            ResourceLocation table = ResourceLocation.parse(tables[profile]);
            int hits = 0;
            for (int seed = 0; seed < 4096; seed++) {
                var output = new ObjectArrayList<ItemStack>();
                SparseChestLoot.compact(table, output, 27, RandomSource.create(seed * 71023L + 19));
                check(output.size() <= 1, "One optional accessory at most");
                if (!output.isEmpty()) { hits++; check(pool.contains(id(output.getFirst())), "Bonus stays inside reviewed pool"); }
            }
            double rate = hits / 4096.0;
            check(Math.abs(rate - probabilities[profile]) < .02, "Seeded bonus frequency agrees with the configured modest chance");
            observed.put(SparseChestLoot.profile(table), rate);
        }
        var full = new ObjectArrayList<ItemStack>(); full.add(item("artifacts:cross_necklace"));
        SparseChestLoot.compact(ResourceLocation.parse(tables[2]), full, 1, RandomSource.create(1));
        check(full.size() == 1, "Bonus cannot exceed physical inventory capacity");
        var excluded = new ObjectArrayList<ItemStack>(); excluded.add(item("artifacts:cross_necklace")); excluded.add(item("artifacts:cross_necklace"));
        check(!SparseChestLoot.compact(ResourceLocation.parse("minecraft:gameplay/fishing"), excluded, 27, RandomSource.create(1))
                && excluded.size() == 2, "Unlisted loot sources are unchanged");
        return observed;
    }

    private static int nativeWda(MinecraftServer server, Set<String> pool) throws Exception {
        Method fill = LootTable.class.getDeclaredMethod("shuffleAndSplitItems", ObjectArrayList.class, int.class, RandomSource.class);
        fill.setAccessible(true);
        String[] names = {"illager_fort/illager_fort_normal", "illager_fort/illager_fort_treasure",
                "heavenly_challenger/heavenly_challenger_supply", "heavenly_challenger/heavenly_challenger_treasure",
                "kisegi_sanctuary/kisegi_sanctuary_normal", "kisegi_sanctuary/kisegi_sanctuary_treasure",
                "infested_temple/infested_temple_room_normal", "infested_temple/infested_temple_room_supply",
                "greenwood_pub/greenwood_pub_normal", "foundry/foundry_treasure"};
        int rolls = 0;
        try (DungeonTestPlayers players = new DungeonTestPlayers()) {
            ServerLevel level = server.overworld();
            ServerPlayer player = players.create(level, new BlockPos(80, 80, 80), WorldTier.HAVEN);
            LootParams params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player).create(LootContextParamSets.CHEST);
            for (String name : names) {
                String fullName = "dungeons_arise:chests/" + name;
                LootTable table = DungeonRewardProbe.table(server, fullName);
                for (int seed = 1; seed <= 32; seed++) {
                    var output = table.getRandomItems(params, seed);
                    var protectedNative = output.stream().filter(stack -> !SparseChestLoot.isBlocked(stack)
                            && !SparseChestLoot.isAccessory(stack) && SparseChestLoot.isProtected(stack, ResourceLocation.parse(fullName)))
                            .map(ItemStack::copy).toList();
                    fill.invoke(table, output, 27, RandomSource.create(seed));
                    check(output.size() <= 27 && output.stream().noneMatch(SparseChestLoot::isBlocked), "Real WDA final-fill obeys physical/progression limits");
                    Set<String> accessories = new HashSet<>();
                    for (ItemStack stack : output) if (SparseChestLoot.isAccessory(stack)) check(accessories.add(id(stack)), "Real WDA rolls have no duplicate accessory type");
                    for (ItemStack wanted : protectedNative) check(output.stream().anyMatch(actual -> ItemStack.isSameItemSameComponents(actual, wanted)),
                            "Actual WDA special native components remain intact");
                    rolls++;
                }
            }
        }
        return rolls;
    }

    private static void personalLootr(MinecraftServer server) {
        check(LootrAPI.isReady(), "Native Lootr API is ready");
        persistentPlayers = new DungeonTestPlayers();
        ServerLevel level = server.overworld();
        BlockPos pos = new BlockPos(96, 79, 96);
        level.getChunkAt(pos); level.setBlock(pos.below(), Blocks.STONE.defaultBlockState(), 2);
        level.setBlock(pos, BuiltInRegistries.BLOCK.get(ResourceLocation.parse("lootr:lootr_chest")).defaultBlockState(), 3);
        var blockEntity = level.getBlockEntity(pos);
        check(blockEntity instanceof RandomizableContainerBlockEntity && blockEntity instanceof ILootrBlockEntity, "Real Lootr first-fill fixture");
        var container = (RandomizableContainerBlockEntity) blockEntity;
        container.setLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse("minecraft:chests/simple_dungeon")), 998877L);
        var provider = (ILootrBlockEntity) blockEntity;
        for (int i = 0; i < 2; i++) {
            ServerPlayer player = persistentPlayers.create(level, pos.above().offset(i, 0, 0), WorldTier.HAVEN);
            ILootrInventory inventory = LootrAPI.getInventory(provider, player);
            check(inventory != null, "Personal inventory is created");
            ItemStack old = item("artifacts:cross_necklace"); old.set(DataComponents.CUSTOM_NAME, Component.literal("Already opened duplicate " + i));
            inventory.setItem(0, old.copy()); inventory.setItem(1, old.copy());
            CompoundTag saved = inventory.saveToTag(server.registryAccess());
            check(LootrAPI.getInventory(provider, player) == inventory && saved.equals(inventory.saveToTag(server.registryAccess())),
                    "Reopening preserves intentionally duplicated already-saved loot");
            PERSONAL.add(new PersonalChest(provider, player, inventory, saved));
        }
    }

    static Map<String, Object> afterReload(MinecraftServer server) {
        int checked = 0;
        try {
            for (PersonalChest entry : PERSONAL) {
                ILootrInventory inventory = LootrAPI.getInventory(entry.provider(), entry.player());
                check(entry.saved().equals(inventory.saveToTag(server.registryAccess())), "Data reload never scans or rerolls already-opened personal inventories");
                check(ItemStack.isSameItemSameComponents(inventory.getItem(0), inventory.getItem(1)), "Old duplicates remain exactly as saved");
                checked++;
            }
            check(checked == 2, "Both personal inventories survive reload");
            return Map.of("personal_inventories_retained", checked, "assertions", assertions);
        } finally {
            if (persistentPlayers != null) persistentPlayers.close();
            persistentPlayers = null; PERSONAL.clear();
        }
    }
    private static ItemStack item(String name) {
        ResourceLocation key = ResourceLocation.parse(name);
        check(BuiltInRegistries.ITEM.containsKey(key), "Native item registry contains " + name);
        ItemStack value = BuiltInRegistries.ITEM.get(key).getDefaultInstance();
        check(!value.isEmpty(), "Native default item is usable: " + name);
        return value;
    }
    private static String id(ItemStack stack) { return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(); }
    private static void check(boolean value, String detail) { assertions++; if (!value) throw new AssertionError(detail); }
}
