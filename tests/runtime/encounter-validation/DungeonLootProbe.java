package soqm.validation.encounters;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.BaseItemKey;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import qm.chests.SparseChestLoot;

/** Real DD registry/component and final LootTable mixin fixtures; never shipped. */
public final class DungeonLootProbe {
    private static final Map<String, String> ROUTES = Map.ofEntries(
        Map.entry("chest/chestloot_basic_easy", "expedition"),
        Map.entry("chest/chestloot_basic_hard", "stronghold"),
        Map.entry("chest/chestloot_advanced_easy", "stronghold"),
        Map.entry("chest/chestloot_advanced_hard", "relic_vault"),
        Map.entry("chest/chestloot_lucky", "expedition"),
        Map.entry("chest/chestloot_crazy", "relic_vault"),
        Map.entry("chest/kit_random", "settlement"),
        Map.entry("chests/chestloot_2", "stronghold"),
        Map.entry("chests/chestloot_basic_easy", "expedition"),
        Map.entry("chests/chestloot_basic_hard", "stronghold"),
        Map.entry("chests/chestloot_advanced_hard", "relic_vault"),
        Map.entry("chests/chestloot_lucky", "expedition")
    );
    private static final List<String> PROTECTED = List.of(
        "item_portal_key", "item_blank_advanced_key", "item_blank_theme_key",
        "item_blank_build_key", "item_key_build", "item_blank_teleporter_key",
        "item_homeward_pearl", "item_secret_bell", "block_key_charger",
        "block_key_charger_used", "block_key_charger_damaged", "block_portal_crown",
        "item_trophy_1", "item_trophy_2", "item_trophy_3", "item_trophy_4",
        "item_trophy_5", "item_trophy_6", "item_trophy_7", "item_trophy_8"
    );
    private static int assertions;

    private DungeonLootProbe() {}

    public static Map<String, Object> run(MinecraftServer server) throws Exception {
        assertions = 0;
        Method finalFill = LootTable.class.getDeclaredMethod("shuffleAndSplitItems",
                ObjectArrayList.class, int.class, RandomSource.class);
        finalFill.setAccessible(true);
        List<ItemStack> expected = new ArrayList<>();
        for (String name : PROTECTED) {
            ItemStack stack = item(name);
            stack.setCount(Math.min(3, stack.getMaxStackSize()));
            expected.add(stack);
        }

        // Use the author's activation API and component, including destination,
        // theme, dungeon type and built state. Plain named vanilla keys cannot
        // detect accidental loss or merging of real DD key components.
        BaseItemKey portal = (BaseItemKey) item("item_portal_key").getItem();
        ItemStack basic = item("item_portal_key");
        portal.activateKeyLevel1(server, basic, 0);
        ItemStack themed = item("item_portal_key");
        portal.activateKeyLevel1(server, themed, 2);
        ItemStack advanced = item("item_portal_key");
        portal.activateKeyLevel2(server, advanced);
        ItemPortalKey.setDungeonBuilt(advanced);
        for (ItemStack key : List.of(basic, themed, advanced)) {
            check(key.has(DimDungeons.DUNGEON_KEY_DATA) && portal.isActivated(key),
                    "Fixture key carries the real activated DD data component");
            expected.add(key);
        }
        check(portal.getKeyLevel(basic) == 1 && portal.getKeyLevel(advanced) == 2
                        && portal.getDungeonTheme(themed) == 2,
                "Fixtures represent basic, advanced and themed destinations");
        check(!ItemStack.isSameItemSameComponents(basic, themed)
                        && !ItemStack.isSameItemSameComponents(basic, advanced),
                "Different dungeon keys cannot be merged into one destination");

        var params = new LootParams.Builder(server.getLevel(DimDungeons.DUNGEON_DIMENSION))
                .withParameter(LootContextParams.ORIGIN, new Vec3(0.5, 80.0, 0.5))
                .create(LootContextParamSets.CHEST);
        int finalFills = 0, rolledChests = 0, protectedNativeRolls = 0;
        Map<String, Integer> nativeItems = new LinkedHashMap<>();
        for (var route : ROUTES.entrySet()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("dimdungeons", route.getKey());
            LootTable table = server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
            check(table != LootTable.EMPTY && id.equals(table.getLootTableId()),
                    "Actual loaded native or forwarding loot table exists: " + id);
            check(route.getValue().equals(SparseChestLoot.profile(id)),
                    "Packaged final compactor routes the actual outer table: " + id);
            for (ItemStack stack : expected) {
                check(!SparseChestLoot.isBlocked(stack) && SparseChestLoot.isProtected(stack, id),
                        "Real registered dungeon treasure is eligible for preservation: " + id);
            }
            for (int seed = 0; seed < 64; seed++) {
                for (int capacity : new int[] {27, expected.size()}) {
                    var generated = noisySource(expected);
                    finalFill.invoke(table, generated, capacity, RandomSource.create(seed));
                    retained(expected, generated);
                    check(generated.size() == expected.size(),
                            "Protected treasure consumes budget before optional filler and banned rewards");
                    check(generated.stream().noneMatch(stack -> stack.is(Items.NETHER_STAR)),
                            "Real final mixin ran: blocked ordinary rewards were removed");
                    finalFills++;
                }
                // Also evaluate the loaded tables and global loot modifiers,
                // then the exact native final-fill hook used by LootTable.fill.
                var rolled = table.getRandomItems(params, seed + 1L);
                nativeItems.merge(id.toString(), rolled.stream().mapToInt(ItemStack::getCount).sum(), Integer::sum);
                List<ItemStack> treasures = rolled.stream().filter(DungeonLootProbe::dungeonTreasure)
                        .map(ItemStack::copy).toList();
                finalFill.invoke(table, rolled, 27, RandomSource.create(seed));
                retained(treasures, rolled);
                if (!treasures.isEmpty()) protectedNativeRolls++;
                check(rolled.size() <= 27 && rolled.stream().noneMatch(SparseChestLoot::isBlocked),
                        "Native dungeon rolls remain legal after final fill");
                rolledChests++;
            }
            check(nativeItems.get(id.toString()) > 0,
                    "Each actual native/legacy alias table yields loot: " + id);
        }
        check(protectedNativeRolls > 0, "Actual native rolls exercise dungeon treasures as well as forced fixtures");
        return Map.of("routes", ROUTES.size(), "real_protected_item_types", PROTECTED.size(),
                "activated_key_variants", 3, "native_final_fill_cases", finalFills,
                "actual_loaded_loot_rolls", rolledChests, "native_treasure_rolls", protectedNativeRolls,
                "key_components_and_counts_retained", true, "assertions", assertions);
    }

    private static ItemStack item(String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("dimdungeons", path);
        check(BuiltInRegistries.ITEM.containsKey(id), "Pinned DD item is actually registered: " + id);
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(id));
        check(!stack.isEmpty(), "Pinned DD item resolves to a nonempty ItemStack: " + id);
        return stack;
    }

    private static boolean dungeonTreasure(ItemStack stack) {
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.getNamespace().equals("dimdungeons") && PROTECTED.contains(id.getPath());
    }

    private static ObjectArrayList<ItemStack> noisySource(List<ItemStack> expected) {
        var source = new ObjectArrayList<ItemStack>();
        source.add(new ItemStack(Items.BREAD, 64));
        source.add(new ItemStack(Items.NETHER_STAR, 64));
        for (ItemStack stack : expected) source.add(stack.copy());
        source.add(new ItemStack(Items.DIAMOND, 64));
        source.add(new ItemStack(Items.REDSTONE, 64));
        return source;
    }

    private static void retained(List<ItemStack> expected, List<ItemStack> actual) {
        for (ItemStack stack : expected) {
            int before = expected.stream().filter(other -> ItemStack.isSameItemSameComponents(stack, other))
                    .mapToInt(ItemStack::getCount).sum();
            int after = actual.stream().filter(other -> ItemStack.isSameItemSameComponents(stack, other))
                    .mapToInt(ItemStack::getCount).sum();
            check(before == after, "Final native fill retains exact treasure count and all components: "
                    + BuiltInRegistries.ITEM.getKey(stack.getItem()));
        }
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }
}
