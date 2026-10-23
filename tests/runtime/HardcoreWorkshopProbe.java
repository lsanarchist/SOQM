package soqm.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.p3pp3rf1y.sophisticatedstorage.item.ChestBlockItem;
import net.p3pp3rf1y.sophisticatedstorage.item.StackStorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageBlockItem;
import net.p3pp3rf1y.sophisticatedstorage.item.WoodStorageBlockItem;

/** Supplied-input fixtures exercising the pinned native recipe implementations. */
public final class HardcoreWorkshopProbe {
    private static final String PREFIX = "kubejs:hardcore_workshop/";
    private static final int EXPECTED_RECIPES = 63;
    private record LookupCase(String suffix, String output, String materialTag,
            int materialCount, String center) {}
    private static final List<LookupCase> UNIQUE_LOOKUPS = List.of(
        new LookupCase("gears/buildcraft_iron", "buildcraftcore:gears/gear_iron",
                "c:plates/iron", 4, "buildcraftcore:gears/gear_stone"),
        new LookupCase("gears/enderio/iron_gear", "enderio:iron_gear",
                "c:plates/iron", 4, "buildcraftcore:gears/gear_stone"),
        new LookupCase("gears/industrialforegoing/iron_gear", "industrialforegoing:iron_gear",
                "c:plates/iron", 4, "buildcraftcore:gears/gear_stone"),
        new LookupCase("gears/railcraft/iron_gear", "railcraft:iron_gear",
                "c:plates/iron", 4, "buildcraftcore:gears/gear_stone"),
        new LookupCase("gears/buildcraft_gold", "buildcraftcore:gears/gear_gold",
                "c:plates/gold", 4, "buildcraftcore:gears/gear_iron"),
        new LookupCase("gears/industrialforegoing/gold_gear", "industrialforegoing:gold_gear",
                "c:plates/gold", 4, "buildcraftcore:gears/gear_iron"),
        new LookupCase("gears/railcraft/gold_gear", "railcraft:gold_gear",
                "c:plates/gold", 4, "buildcraftcore:gears/gear_iron"),
        new LookupCase("ironfurnaces/upgrade_iron", "ironfurnaces:upgrade_iron",
                "c:storage_blocks/iron", 8, "minecraft:cobblestone"),
        new LookupCase("ironfurnaces/upgrade_iron2", "ironfurnaces:upgrade_iron2",
                "c:storage_blocks/iron", 8, "minecraft:copper_ingot")
    );
    private static final Set<String> REMOVED = Set.of(
        "aether:skyroot_piston",
        "buildcraftcore:gears/gear_diamond",
        "buildcraftcore:gears/gear_gold",
        "buildcraftcore:gears/gear_iron",
        "buildcraftcore:gears/gear_stone",
        "buildcraftcore:gears/gear_wood",
        "enderio:iron_gear",
        "immersiveengineering:crafting/plate_copper_hammering",
        "immersiveengineering:crafting/plate_gold_hammering",
        "immersiveengineering:crafting/plate_iron_hammering",
        "immersiveengineering:crafting/plate_steel_hammering",
        "industrialforegoing:gold_gear",
        "industrialforegoing:iron_gear",
        "ironfurnaces:furnaces/copper_furnace",
        "ironfurnaces:furnaces/iron_furnace",
        "ironfurnaces:furnaces/iron_furnace2",
        "ironfurnaces:upgrades/upgrade_copper",
        "ironfurnaces:upgrades/upgrade_iron",
        "ironfurnaces:upgrades/upgrade_iron2",
        "kubejs:expert/mekanism/control_circuit/advanced",
        "kubejs:expert/mekanism/control_circuit/elite",
        "kubejs:expert/mekanism/control_circuit/ultimate",
        "kubejs:expert/mekanism/metallurgic_infuser",
        "kubejs:expert/mekanism/steel_casing",
        "kubejs:expert_v042/mekanism/chemical_infuser",
        "kubejs:expert_v042/mekanism/crusher",
        "kubejs:expert_v042/mekanism/electrolytic_separator",
        "kubejs:expert_v042/mekanism/enrichment_chamber",
        "kubejs:expert_v042/mekanism/precision_sawmill",
        "kubejs:expert_v042/mekanism/rotary_condensentrator",
        "mekanism:chemical_infuser",
        "mekanism:control_circuit/advanced",
        "mekanism:control_circuit/elite",
        "mekanism:control_circuit/infused_advanced",
        "mekanism:control_circuit/infused_elite",
        "mekanism:control_circuit/infused_ultimate",
        "mekanism:control_circuit/ultimate",
        "mekanism:crusher",
        "mekanism:electrolytic_separator",
        "mekanism:enrichment_chamber",
        "mekanism:metallurgic_infuser",
        "mekanism:module_base",
        "mekanism:precision_sawmill",
        "mekanism:rotary_condensentrator",
        "mekanism:steel_casing",
        "minecraft:furnace",
        "minecraft:hopper",
        "minecraft:piston",
        "railcraft:copper_gear",
        "railcraft:gold_gear",
        "railcraft:iron_gear",
        "sophisticatedstorage:basic_to_copper_tier_upgrade",
        "sophisticatedstorage:basic_to_iron_tier_from_basic_to_copper_tier",
        "sophisticatedstorage:basic_to_iron_tier_upgrade",
        "sophisticatedstorage:copper_barrel",
        "sophisticatedstorage:copper_chest",
        "sophisticatedstorage:copper_shulker_box",
        "sophisticatedstorage:copper_to_iron_tier_upgrade",
        "sophisticatedstorage:double_copper_chest",
        "sophisticatedstorage:double_iron_chest",
        "sophisticatedstorage:double_iron_chest_from_copper_chest",
        "sophisticatedstorage:iron_barrel",
        "sophisticatedstorage:iron_barrel_from_copper_barrel",
        "sophisticatedstorage:iron_chest",
        "sophisticatedstorage:iron_chest_from_copper_chest",
        "sophisticatedstorage:iron_shulker_box",
        "sophisticatedstorage:iron_shulker_box_from_copper_shulker_box",
        "sophisticatedstorage:limited_copper_barrel_1",
        "sophisticatedstorage:limited_copper_barrel_2",
        "sophisticatedstorage:limited_copper_barrel_3",
        "sophisticatedstorage:limited_copper_barrel_4",
        "sophisticatedstorage:limited_iron_barrel_1",
        "sophisticatedstorage:limited_iron_barrel_1_from_limited_copper_barrel_1",
        "sophisticatedstorage:limited_iron_barrel_2",
        "sophisticatedstorage:limited_iron_barrel_2_from_limited_copper_barrel_2",
        "sophisticatedstorage:limited_iron_barrel_3",
        "sophisticatedstorage:limited_iron_barrel_3_from_limited_copper_barrel_3",
        "sophisticatedstorage:limited_iron_barrel_4",
        "sophisticatedstorage:limited_iron_barrel_4_from_limited_copper_barrel_4",
        "terralith:piston_alt"
    );

    private HardcoreWorkshopProbe() {}

    public static void run(MinecraftServer server, Map<String, Object> results) {
        for (String id : REMOVED) {
            check(server.getRecipeManager().byKey(ResourceLocation.parse(id)).isEmpty(),
                    "Removed cheap/baseline recipe must stay absent: " + id);
        }
        List<RecipeHolder<CraftingRecipe>> allCrafting = server.getRecipeManager()
                .getAllRecipesFor(RecipeType.CRAFTING);
        List<RecipeHolder<CraftingRecipe>> recipes = allCrafting.stream()
                .filter(holder -> holder.id().toString().startsWith(PREFIX)).toList();
        check(recipes.size() == EXPECTED_RECIPES,
                "Every hardcore recipe loads: expected " + EXPECTED_RECIPES + ", got " + recipes.size());
        int storage = 0;
        for (RecipeHolder<CraftingRecipe> holder : recipes) {
            String serializer = BuiltInRegistries.RECIPE_SERIALIZER.getKey(holder.value().getSerializer()).toString();
            if (serializer.startsWith("sophisticatedstorage:")) {
                testStorageCarryover(server, holder, serializer.endsWith("double_chest_tier_upgrade"));
                storage++;
            } else {
                CraftingInput input = grid(ingredients(holder.value()));
                check(holder.value().matches(input, server.overworld()), "Native ingredients match " + holder.id());
                ItemStack output = holder.value().assemble(input, server.registryAccess());
                check(!output.isEmpty() && ItemStack.isSameItemSameComponents(output,
                        holder.value().getResultItem(server.registryAccess())), "Native assembly produces " + holder.id());
                if (holder.id().getPath().contains("control_circuit/") || holder.id().getPath().endsWith("/module_base")) {
                    check(output.getCount() == 1, "Circuit/module batch cannot undo the tier cost: " + holder.id());
                }
                if (holder.id().getPath().contains("/manual/plate_")) {
                    check(holder.value().getRemainingItems(input).stream().anyMatch(stack ->
                            stack.getItem() == item("immersiveengineering:hammer").getItem()
                                    && stack.getDamageValue() > 0), "Manual plate preserves and wears its hammer");
                }
            }
        }
        check(storage == 24, "All 21 single and 3 double native storage variants were exercised");
        testChestAndHopperSafety(server);
        testObsoletePatterns(server);
        int lookupCases = testUniqueRecipeLookups(server, allCrafting);
        for (String id : List.of("immersiveengineering:crafting/hammer", "immersiveengineering:crafting/stick_iron",
                "immersiveengineering:metalpress/plate_iron", "immersiveengineering:metalpress/plate_copper",
                "immersiveengineering:metalpress/plate_gold", "immersiveengineering:metalpress/plate_steel",
                "immersiveengineering:blastfurnace/steel", "mekanism:control_circuit/basic")) {
            check(server.getRecipeManager().byKey(ResourceLocation.parse(id)).isPresent(),
                    "Manual/steel/basic-circuit bootstrap source remains loaded: " + id);
        }
        results.put("hardcore_workshop_loaded_and_native_assembled", recipes.size());
        results.put("hardcore_workshop_removed_recipe_ids", REMOVED.size());
        results.put("hardcore_storage_full_contents_components_and_tier_slots", storage);
        results.put("hardcore_storage_double_price_no_single_recipe_bypass", true);
        results.put("hardcore_hopper_rejects_populated_storage", true);
        results.put("hardcore_manual_hammer_remainder_and_bootstrap_recipes", true);
        results.put("hardcore_vanilla_cheap_patterns_rejected", true);
        results.put("hardcore_unique_lookup_affected_recipes", UNIQUE_LOOKUPS.size());
        results.put("hardcore_unique_lookup_item_combinations_and_mirrors", lookupCases);
        results.put("hardcore_unique_lookup_full_crafting_collection", allCrafting.size());
        results.put("hardcore_unique_lookup_costs_and_expected_outputs", true);
    }

    /** Test ordinary no-hint selection, not only each recipe's by-ID matcher. */
    private static int testUniqueRecipeLookups(MinecraftServer server,
            List<RecipeHolder<CraftingRecipe>> allCrafting) {
        int tested = 0;
        for (LookupCase expected : UNIQUE_LOOKUPS) {
            CraftingRecipe recipe = recipe(server, expected.suffix());
            check(recipe instanceof ShapedRecipe, "Lookup fixture is shaped: " + expected.suffix());
            ShapedRecipe shaped = (ShapedRecipe)recipe;
            check(shaped.getWidth() == 3 && shaped.getHeight() == 3,
                    "Lookup fixtures retain their full 3x3 grid: " + expected.suffix());
            List<ItemStack[]> choices = new ArrayList<>();
            long combinations = 1;
            for (Ingredient ingredient : shaped.getIngredients()) {
                ItemStack[] options = ingredient.isEmpty() ? new ItemStack[]{ItemStack.EMPTY}
                        : Arrays.stream(ingredient.getItems()).map(stack -> stack.copyWithCount(1))
                                .toArray(ItemStack[]::new);
                check(options.length > 0, "Lookup ingredient has a registered witness: " + expected.suffix());
                combinations *= options.length;
                check(combinations <= 4096, "Pinned lookup witness set grew unexpectedly: " + expected.suffix());
                choices.add(options);
            }
            tested += testLookupCombinations(server, allCrafting, expected, choices, new ItemStack[9], 0);
        }
        return tested;
    }

    private static int testLookupCombinations(MinecraftServer server,
            List<RecipeHolder<CraftingRecipe>> allCrafting, LookupCase expected,
            List<ItemStack[]> choices, ItemStack[] inputs, int index) {
        if (index < choices.size()) {
            int tested = 0;
            for (ItemStack option : choices.get(index)) {
                inputs[index] = option;
                tested += testLookupCombinations(server, allCrafting, expected, choices, inputs, index + 1);
            }
            return tested;
        }
        testLookupGrid(server, allCrafting, expected, inputs);
        ItemStack[] mirrored = new ItemStack[9];
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                mirrored[row * 3 + column] = inputs[row * 3 + 2 - column];
            }
        }
        testLookupGrid(server, allCrafting, expected, mirrored);
        return 2;
    }

    private static void testLookupGrid(MinecraftServer server,
            List<RecipeHolder<CraftingRecipe>> allCrafting, LookupCase expected, ItemStack[] inputs) {
        var material = TagKey.create(Registries.ITEM, ResourceLocation.parse(expected.materialTag()));
        long materialSlots = Arrays.stream(inputs).filter(stack -> stack.is(material)).count();
        long centerSlots = Arrays.stream(inputs).filter(stack -> stack.getItem() == item(expected.center()).getItem()).count();
        long occupied = Arrays.stream(inputs).filter(stack -> !stack.isEmpty()).count();
        check(materialSlots == expected.materialCount() && centerSlots == 1
                        && occupied == expected.materialCount() + 1
                        && Arrays.stream(inputs).filter(stack -> !stack.isEmpty()).allMatch(stack -> stack.getCount() == 1),
                "Every witness preserves the reviewed material cost: " + expected.suffix());
        CraftingInput input = grid(inputs);
        ResourceLocation expectedId = ResourceLocation.parse(PREFIX + expected.suffix());
        List<ResourceLocation> matching = allCrafting.stream()
                .filter(holder -> holder.value().matches(input, server.overworld()))
                .map(RecipeHolder::id).toList();
        check(matching.equals(List.of(expectedId)),
                "Full crafting registry has exactly one match for " + expectedId + ": " + matching);
        RecipeHolder<CraftingRecipe> selected = server.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, server.overworld()).orElseThrow();
        check(selected.id().equals(expectedId), "Ordinary no-hint lookup selects " + expectedId);
        ItemStack result = selected.value().assemble(input, server.registryAccess());
        check(result.getItem() == item(expected.output()).getItem() && result.getCount() == 1,
                "Global selection produces exactly one expected output: " + expectedId);
    }

    private static void testStorageCarryover(MinecraftServer server, RecipeHolder<CraftingRecipe> holder,
            boolean doubled) {
        ItemStack[] inputs = ingredients(holder.value());
        ItemStack original = Arrays.stream(inputs).filter(stack -> stack.getItem() instanceof StorageBlockItem)
                .findFirst().orElseThrow(() -> new AssertionError("Missing storage input: " + holder.id()));
        if (original.getItem() instanceof ChestBlockItem) ChestBlockItem.setDoubleChest(original, doubled);
        if (original.getItem() instanceof WoodStorageBlockItem) {
            WoodStorageBlockItem.setWoodType(original, WoodType.SPRUCE);
            WoodStorageBlockItem.setPacked(original, true);
        }
        original.set(DataComponents.CUSTOM_NAME, Component.literal("Workshop contents " + holder.id().getPath()));
        StorageBlockItem.setLocked(original, true);
        StackStorageWrapper source = StackStorageWrapper.fromStack(server.registryAccess(), original);
        source.ensureContentsUuid();
        source.setColors(0x237C74, 0xCA9B45);
        source.getInventoryHandler().setStackInSlot(0, new ItemStack(Items.DIAMOND, 17));
        source.getUpgradeHandler().setStackInSlot(0, item("sophisticatedstorage:pickup_upgrade"));
        var uuid = source.getContentsUuid().orElseThrow();
        CraftingInput input = grid(inputs);
        check(holder.value().matches(input, server.overworld()), "Filled native storage matches " + holder.id());
        ItemStack output = holder.value().assemble(input, server.registryAccess());
        check(!output.isEmpty() && output.getItem() == holder.value().getResultItem(server.registryAccess()).getItem(),
                "Filled storage produces the expected tier: " + holder.id());
        StackStorageWrapper target = StackStorageWrapper.fromStack(server.registryAccess(), output);
        check(target.getContentsUuid().orElseThrow().equals(uuid), "Storage contents UUID survives " + holder.id());
        ItemStack carried = target.getInventoryHandler().getStackInSlot(0);
        check(carried.is(Items.DIAMOND) && carried.getCount() == 17, "Stored inventory survives " + holder.id());
        check(target.getUpgradeHandler().getStackInSlot(0).getItem() == item("sophisticatedstorage:pickup_upgrade").getItem(),
                "Installed upgrade survives " + holder.id());
        check(Objects.equals(original.get(DataComponents.CUSTOM_NAME), output.get(DataComponents.CUSTOM_NAME))
                        && target.getMainColor() == 0x237C74 && target.getAccentColor() == 0xCA9B45
                        && StorageBlockItem.isLocked(output), "Name, colors and lock survive " + holder.id());
        if (original.getItem() instanceof WoodStorageBlockItem) {
            check(WoodStorageBlockItem.getWoodType(output).orElseThrow() == WoodType.SPRUCE
                            && WoodStorageBlockItem.isPacked(output), "Wood variant and packed state survive " + holder.id());
        }
        check(ChestBlockItem.isDoubleChest(output) == doubled, "Double state survives " + holder.id());
        check(StorageBlockItem.getNumberOfInventorySlots(output) == target.getDefaultNumberOfInventorySlots() * (doubled ? 2 : 1)
                        && StorageBlockItem.getNumberOfUpgradeSlots(output) == target.getDefaultNumberOfUpgradeSlots() * (doubled ? 2 : 1),
                "Tier capacity follows the native single/double serializer: " + holder.id());
        if (original.getItem() instanceof ChestBlockItem) {
            ChestBlockItem.setDoubleChest(original, !doubled);
            check(!holder.value().matches(grid(inputs), server.overworld()),
                    "Single and double recipes cannot exchange their material prices: " + holder.id());
        }
    }

    private static void testChestAndHopperSafety(MinecraftServer server) {
        CraftingRecipe recipe = recipe(server, "minecraft/hopper");
        ItemStack[] inputs = ingredients(recipe);
        long iron = Arrays.stream(inputs).filter(stack -> stack.is(Items.IRON_INGOT)).count();
        check(iron == 5 && Arrays.stream(inputs).anyMatch(stack -> stack.getItem()
                == item("sophisticatedstorage:basic_to_iron_tier_upgrade").getItem()),
                "Hopper pays five iron plus the full unused industrial tier token");
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].is(Items.CHEST)) {
                inputs[i] = item("sophisticatedstorage:iron_chest");
                StackStorageWrapper stored = StackStorageWrapper.fromStack(server.registryAccess(), inputs[i]);
                stored.ensureContentsUuid();
                stored.getInventoryHandler().setStackInSlot(0, new ItemStack(Items.DIAMOND, 17));
            }
        }
        check(!recipe.matches(grid(inputs), server.overworld()), "Hopper cannot consume a populated industrial chest");
    }

    private static void testObsoletePatterns(MinecraftServer server) {
        ItemStack[] furnace = {
            item("minecraft:cobblestone"), item("minecraft:cobblestone"), item("minecraft:cobblestone"),
            item("minecraft:cobblestone"), ItemStack.EMPTY, item("minecraft:cobblestone"),
            item("minecraft:cobblestone"), item("minecraft:cobblestone"), item("minecraft:cobblestone")};
        rejectOutput(server, furnace, "minecraft:furnace");
        ItemStack[] hopper = {
            item("minecraft:iron_ingot"), ItemStack.EMPTY, item("minecraft:iron_ingot"),
            item("minecraft:iron_ingot"), item("minecraft:chest"), item("minecraft:iron_ingot"),
            ItemStack.EMPTY, item("minecraft:iron_ingot"), ItemStack.EMPTY};
        rejectOutput(server, hopper, "minecraft:hopper");
        for (String plank : List.of("minecraft:oak_planks", "aether:skyroot_planks")) {
            ItemStack[] piston = {
                item(plank), item(plank), item(plank), item("minecraft:cobblestone"), item("minecraft:iron_ingot"),
                item("minecraft:cobblestone"), item("minecraft:cobblestone"), item("minecraft:redstone"), item("minecraft:cobblestone")};
            rejectOutput(server, piston, "minecraft:piston");
        }
    }

    private static void rejectOutput(MinecraftServer server, ItemStack[] inputs, String output) {
        check(server.getRecipeManager().getRecipesFor(RecipeType.CRAFTING, grid(inputs), server.overworld()).stream()
                .noneMatch(holder -> holder.value().getResultItem(server.registryAccess()).getItem() == item(output).getItem()),
                "Obsolete cheap pattern cannot produce " + output);
    }

    private static CraftingRecipe recipe(MinecraftServer server, String suffix) {
        return (CraftingRecipe) server.getRecipeManager().byKey(ResourceLocation.parse(PREFIX + suffix)).orElseThrow().value();
    }

    private static ItemStack[] ingredients(CraftingRecipe recipe) {
        ItemStack[] grid = new ItemStack[9];
        Arrays.fill(grid, ItemStack.EMPTY);
        List<Ingredient> entries = recipe.getIngredients();
        int width = recipe instanceof ShapedRecipe shaped ? shaped.getWidth() : 3;
        for (int i = 0; i < entries.size(); i++) {
            Ingredient ingredient = entries.get(i);
            if (ingredient.isEmpty()) continue;
            check(ingredient.getItems().length > 0, "Every pinned ingredient/tag has at least one registered item");
            grid[(i / width) * 3 + i % width] = ingredient.getItems()[0].copyWithCount(1);
        }
        return grid;
    }

    private static CraftingInput grid(ItemStack[] inputs) {
        return CraftingInput.of(3, 3, Arrays.asList(inputs));
    }

    private static ItemStack item(String id) {
        ResourceLocation key = ResourceLocation.parse(id);
        check(BuiltInRegistries.ITEM.containsKey(key), "Fixture item is registered: " + id);
        return new ItemStack(BuiltInRegistries.ITEM.get(key));
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
