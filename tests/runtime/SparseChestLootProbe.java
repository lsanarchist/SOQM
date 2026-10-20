package soqm.validation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import qm.chests.SparseChestLoot;

/** Executes the production compactor with native ItemStack and seeded RNG. */
public final class SparseChestLootProbe {
    private static final String[] TABLES = {
        "minecraft:chests/village/village_plains_house", "minecraft:chests/simple_dungeon",
        "minecraft:chests/village/village_armorer", "minecraft:chests/ancient_city",
        "minecraft:chests/end_city_treasure",
    };
    private static final int[] SLOTS = {6, 7, 6, 9, 10};
    private static final int[] UNITS = {16, 20, 18, 24, 28};
    private static final int[] RARES = {1, 1, 1, 2, 2};
    private static final float[] CHANCE = {.10F, .25F, .20F, .40F, .55F};
    private static final int SAMPLES = 10_000;

    private SparseChestLootProbe() {}

    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Map<String, Object> result = new LinkedHashMap<>();
        untouched();
        protectedItems();
        oneRolledFind();
        for (int profile = 0; profile < TABLES.length; profile++) {
            int diamonds = 0;
            ResourceLocation table = ResourceLocation.parse(TABLES[profile]);
            check(SparseChestLoot.profile(table) != null, "Fixture has a real route");
            for (int seed = 0; seed < SAMPLES; seed++) {
                var single = items(new ItemStack(Items.DIAMOND, 64));
                check(SparseChestLoot.compact(table, single, 27, RandomSource.create(seed)), "Known table is compacted");
                if (!single.isEmpty()) diamonds++;
                check(single.isEmpty() || single.getFirst().getCount() == 1, "A valuable is at most one item");
                var source = commonAndRare();
                int space = seed % 28;
                SparseChestLoot.compact(table, source, space, RandomSource.create(seed));
                check(source.size() <= Math.min(space, SLOTS[profile]), "Native slot and profile ceilings");
                check(source.stream().mapToInt(ItemStack::getCount).sum() <= UNITS[profile], "Per-chest unit ceiling");
                check(source.stream().noneMatch(s -> s.is(Items.NETHER_STAR)), "Nether stars never enter random chests");
                check(source.stream().filter(SparseChestLootProbe::rare).count() <= RARES[profile], "Native valuable slot ceiling");
                var ids = new HashSet<Item>();
                for (ItemStack stack : source) {
                    check(stack.getCount() > 0 && stack.getCount() <= stack.getMaxStackSize(), "Legal native stack count");
                    check(stack.getCount() <= (profile == 4 ? 6 : 4), "Small stack cap");
                    if (stack.getMaxStackSize() > 1) check(ids.add(stack.getItem()), "Duplicate plain stacks are consolidated");
                }
                if (seed < 100) {
                    var repeated = commonAndRare();
                    SparseChestLoot.compact(table, repeated, space, RandomSource.create(seed));
                    check(signature(source).equals(signature(repeated)), "Same source and seed yield the same loot");
                }
            }
            double rate = diamonds / (double) SAMPLES;
            check(Math.abs(rate - CHANCE[profile]) < .02, "Observed retention matches the configured probability");
            result.put(SparseChestLoot.profile(table) + "_native_diamond_retention", rate);
        }
        result.put("seeded_bounded_chests", SAMPLES * TABLES.length);
        result.put("scope", "Production compactor and vanilla ItemStack/RNG, no server, mod registry or Lootr client launch");
        System.out.println(result);
    }

    private static void untouched() {
        var original = new ItemStack(Items.DIAMOND, 64);
        var items = items(original);
        check(!SparseChestLoot.compact(ResourceLocation.parse("minecraft:gameplay/fishing"), items, 27,
                RandomSource.create(1)), "Unlisted table keeps vanilla filling");
        check(items.size() == 1 && items.getFirst() == original && original.getCount() == 64,
                "Unlisted table has no mutation");
    }

    private static void protectedItems() {
        Item[] protectedItems = {Items.FILLED_MAP, Items.HEART_OF_THE_SEA, Items.TRIAL_KEY,
                Items.OMINOUS_TRIAL_KEY, Items.MUSIC_DISC_CAT, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE};
        for (int seed = 0; seed < 100; seed++) {
            var items = commonAndRare();
            for (Item item : protectedItems) items.add(new ItemStack(item));
            SparseChestLoot.compact(ResourceLocation.parse(TABLES[0]), items, 27, RandomSource.create(seed));
            for (Item item : protectedItems) check(items.stream().anyMatch(stack -> stack.is(item)), "Native progression/collection item is retained");
            check(items.size() == protectedItems.length, "Protected loot consumes the supply budget first");
        }
    }

    private static void oneRolledFind() {
        for (int seed = 0; seed < 1000; seed++) {
            var generated = items(new ItemStack(Items.GOLDEN_APPLE, 64), new ItemStack(Items.SADDLE));
            SparseChestLoot.compact(ResourceLocation.parse(TABLES[0]), generated, 27, RandomSource.create(seed));
            check(generated.size() == 1 && generated.getFirst().getCount() == 1,
                    "At most one already-rolled find survives, without another chance or filler drop");
        }
    }

    private static boolean rare(ItemStack stack) {
        return stack.is(Items.DIAMOND) || stack.is(Items.EMERALD) || stack.is(Items.NETHERITE_SCRAP)
                || stack.is(Items.DIAMOND_SWORD) || stack.is(Items.DIAMOND_HELMET) || stack.is(Items.ENCHANTED_BOOK);
    }

    private static ObjectArrayList<ItemStack> commonAndRare() {
        return items(new ItemStack(Items.DIAMOND, 64), new ItemStack(Items.EMERALD, 64),
                new ItemStack(Items.NETHERITE_SCRAP, 64), new ItemStack(Items.DIAMOND_SWORD),
                new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.ENCHANTED_BOOK),
                new ItemStack(Items.NETHER_STAR, 64), new ItemStack(Items.BREAD, 64),
                new ItemStack(Items.BREAD, 64), new ItemStack(Items.COAL, 64), new ItemStack(Items.IRON_INGOT, 64),
                new ItemStack(Items.REDSTONE, 64), new ItemStack(Items.LEATHER, 64), new ItemStack(Items.STRING, 64),
                new ItemStack(Items.ARROW, 64), new ItemStack(Items.COPPER_INGOT, 64));
    }

    private static String signature(Iterable<ItemStack> stacks) {
        var result = new StringBuilder();
        for (ItemStack stack : stacks) result.append(BuiltInRegistries.ITEM.getKey(stack.getItem()))
                .append('=').append(stack.getCount()).append(';');
        return result.toString();
    }

    private static ObjectArrayList<ItemStack> items(ItemStack... stacks) {
        return new ObjectArrayList<>(stacks);
    }

    private static void check(boolean valid, String message) {
        if (!valid) throw new AssertionError(message);
    }
}
