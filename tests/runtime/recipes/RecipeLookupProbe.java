package soqm.validation.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * Executes vanilla 1.21.1 codecs, pattern matching and no-hint RecipeManager
 * lookup without a world. Only the nine repaired recipes are loaded. Distinct
 * vanilla items stand in for mod items; the three tag witness sets below are
 * explicit fixtures from the pinned pack, not a live mod/tag registry.
 * HardcoreWorkshopProbe separately scans the entire real CRAFTING collection.
 */
public final class RecipeLookupProbe {
    private static final String PREFIX = "kubejs:hardcore_workshop/";
    private record Expected(String suffix, String output, String materialTag, int amount, String center) {}
    private static final List<Expected> EXPECTED = List.of(
        new Expected("gears/buildcraft_iron", "buildcraftcore:gears/gear_iron", "c:plates/iron", 4, "buildcraftcore:gears/gear_stone"),
        new Expected("gears/enderio/iron_gear", "enderio:iron_gear", "c:plates/iron", 4, "buildcraftcore:gears/gear_stone"),
        new Expected("gears/industrialforegoing/iron_gear", "industrialforegoing:iron_gear", "c:plates/iron", 4, "buildcraftcore:gears/gear_stone"),
        new Expected("gears/railcraft/iron_gear", "railcraft:iron_gear", "c:plates/iron", 4, "buildcraftcore:gears/gear_stone"),
        new Expected("gears/buildcraft_gold", "buildcraftcore:gears/gear_gold", "c:plates/gold", 4, "buildcraftcore:gears/gear_iron"),
        new Expected("gears/industrialforegoing/gold_gear", "industrialforegoing:gold_gear", "c:plates/gold", 4, "buildcraftcore:gears/gear_iron"),
        new Expected("gears/railcraft/gold_gear", "railcraft:gold_gear", "c:plates/gold", 4, "buildcraftcore:gears/gear_iron"),
        new Expected("ironfurnaces/upgrade_iron", "ironfurnaces:upgrade_iron", "c:storage_blocks/iron", 8, "minecraft:cobblestone"),
        new Expected("ironfurnaces/upgrade_iron2", "ironfurnaces:upgrade_iron2", "c:storage_blocks/iron", 8, "minecraft:copper_ingot")
    );
    private static final Map<String, List<String>> TAG_WITNESSES = Map.of(
        "c:plates/iron", List.of("ad_astra:iron_plate", "immersiveengineering:plate_iron", "railcraft:iron_plate"),
        "c:plates/gold", List.of("immersiveengineering:plate_gold", "railcraft:gold_plate"),
        "c:storage_blocks/iron", List.of("minecraft:iron_block")
    );
    private static final Map<String, String> MOD_ITEM_PROXIES = Map.ofEntries(
        Map.entry("ad_astra:iron_plate", "minecraft:paper"),
        Map.entry("immersiveengineering:plate_iron", "minecraft:leather"),
        Map.entry("railcraft:iron_plate", "minecraft:string"),
        Map.entry("immersiveengineering:plate_gold", "minecraft:feather"),
        Map.entry("railcraft:gold_plate", "minecraft:flint"),
        Map.entry("buildcraftcore:gears/gear_stone", "minecraft:stone_button"),
        Map.entry("buildcraftcore:gears/gear_iron", "minecraft:iron_nugget"),
        Map.entry("buildcraftcore:gears/gear_gold", "minecraft:gold_nugget"),
        Map.entry("enderio:iron_gear", "minecraft:compass"),
        Map.entry("industrialforegoing:iron_gear", "minecraft:clock"),
        Map.entry("railcraft:iron_gear", "minecraft:shears"),
        Map.entry("industrialforegoing:gold_gear", "minecraft:recovery_compass"),
        Map.entry("railcraft:gold_gear", "minecraft:spyglass"),
        Map.entry("ironfurnaces:upgrade_iron", "minecraft:brick"),
        Map.entry("ironfurnaces:upgrade_iron2", "minecraft:nether_brick")
    );

    private static final class Evidence {
        int grids, ambiguousGrids, otherOutputs;
        final Set<String> ambiguousRecipes = new LinkedHashSet<>();
    }

    public static void main(String[] args) throws Exception {
        check(args.length == 2, "Usage: RecipeLookupProbe current-policy.json baseline-policy.json");
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        check(new LinkedHashSet<>(MOD_ITEM_PROXIES.values()).size() == MOD_ITEM_PROXIES.size(),
                "Every mod item has a distinct vanilla proxy");
        Evidence before = exercise(load(Path.of(args[1])), false);
        check(before.ambiguousRecipes.size() == 9 && before.otherOutputs > 0,
                "Negative control must reproduce all nine ambiguous recipes and an incorrect selected output");
        Evidence after = exercise(load(Path.of(args[0])), true);
        check(after.grids == before.grids, "Both policies exercise the same witness combinations");
        System.out.println("RECIPE_LOOKUP_NATIVE_OLD_COLLISIONS recipes=" + before.ambiguousRecipes.size()
                + " witness_grids=" + before.grids + " ambiguous_grids=" + before.ambiguousGrids
                + " other_selected_outputs=" + before.otherOutputs);
        System.out.println("RECIPE_LOOKUP_NATIVE_OK recipes=" + EXPECTED.size() + " witness_grids=" + after.grids
                + " ambiguous_grids=" + after.ambiguousGrids + " other_selected_outputs=" + after.otherOutputs);
        System.out.println("RECIPE_LOOKUP_TAG_WITNESSES " + new Gson().toJson(TAG_WITNESSES));
        System.out.println("SCOPE native_vanilla_codecs_pattern_and_lookup; nine_recipe_pool; explicit_mod_item_and_tag_proxies; no_world_or_server");
    }

    private static RecipeManager load(Path policy) throws Exception {
        JsonArray rows = JsonParser.parseString(Files.readString(policy)).getAsJsonObject().getAsJsonArray("recipes");
        Map<String, JsonObject> source = new LinkedHashMap<>();
        for (JsonElement element : rows) {
            JsonObject row = element.getAsJsonObject();
            source.put(row.get("id").getAsString(), row.getAsJsonObject("recipe"));
        }
        List<RecipeHolder<?>> recipes = new ArrayList<>();
        for (Expected expected : EXPECTED) {
            String id = PREFIX + expected.suffix();
            JsonObject json = source.get(id).deepCopy();
            check(json.get("type").getAsString().equals("minecraft:crafting_shaped"), "Expected native shaped recipe: " + id);
            JsonObject key = json.getAsJsonObject("key");
            for (String symbol : List.copyOf(key.keySet())) {
                JsonObject input = key.getAsJsonObject(symbol);
                if (input.has("tag")) {
                    List<String> witnesses = TAG_WITNESSES.get(input.get("tag").getAsString());
                    check(witnesses != null, "Tag fixture must be explicitly reviewed: " + input);
                    JsonArray choices = new JsonArray();
                    for (String witness : witnesses) choices.add(ingredientJson(witness));
                    key.add(symbol, choices);
                } else {
                    key.add(symbol, ingredientJson(input.get("item").getAsString()));
                }
            }
            JsonObject output = json.getAsJsonObject("result");
            output.addProperty("id", proxyId(output.get("id").getAsString()));
            ShapedRecipe recipe = RecipeSerializer.SHAPED_RECIPE.codec().codec().parse(JsonOps.INSTANCE, json).getOrThrow();
            recipes.add(new RecipeHolder<>(ResourceLocation.parse(id), recipe));
        }
        RecipeManager manager = new RecipeManager(RegistryAccess.EMPTY);
        manager.replaceRecipes(recipes);
        return manager;
    }

    private static Evidence exercise(RecipeManager manager, boolean requireUnique) {
        Evidence evidence = new Evidence();
        for (Expected expected : EXPECTED) {
            CraftingRecipe recipe = (CraftingRecipe)manager.byKey(ResourceLocation.parse(PREFIX + expected.suffix())).orElseThrow().value();
            check(recipe instanceof ShapedRecipe shaped && shaped.getWidth() == 3 && shaped.getHeight() == 3, "Expected full 3x3 pattern");
            enumerate(manager, expected, recipe.getIngredients(), new ItemStack[9], 0, requireUnique, evidence);
        }
        return evidence;
    }

    private static void enumerate(RecipeManager manager, Expected expected, List<Ingredient> ingredients,
            ItemStack[] inputs, int index, boolean requireUnique, Evidence evidence) {
        if (index < ingredients.size()) {
            Ingredient ingredient = ingredients.get(index);
            ItemStack[] options = ingredient.isEmpty() ? new ItemStack[]{ItemStack.EMPTY} : ingredient.getItems();
            for (ItemStack option : options) {
                inputs[index] = option.copyWithCount(1);
                enumerate(manager, expected, ingredients, inputs, index + 1, requireUnique, evidence);
            }
            return;
        }
        inspect(manager, expected, inputs, requireUnique, evidence);
        ItemStack[] mirrored = new ItemStack[9];
        for (int row = 0; row < 3; row++) for (int col = 0; col < 3; col++) mirrored[row * 3 + col] = inputs[row * 3 + 2 - col];
        inspect(manager, expected, mirrored, requireUnique, evidence);
    }

    private static void inspect(RecipeManager manager, Expected expected, ItemStack[] inputs,
            boolean requireUnique, Evidence evidence) {
        String id = PREFIX + expected.suffix();
        CraftingInput input = CraftingInput.of(3, 3, Arrays.asList(inputs));
        // getRecipesFor also sorts display names through Level.registryAccess;
        // enumerate native matchers directly so this probe needs no Level fixture.
        List<RecipeHolder<CraftingRecipe>> matching = manager.getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(holder -> holder.value().matches(input, null)).toList();
        RecipeHolder<CraftingRecipe> selected = manager.getRecipeFor(RecipeType.CRAFTING, input, null).orElseThrow();
        ItemStack output = selected.value().assemble(input, RegistryAccess.EMPTY);
        boolean correctOutput = selected.id().toString().equals(id) && output.is(item(expected.output())) && output.getCount() == 1;
        evidence.grids++;
        if (matching.size() != 1) { evidence.ambiguousGrids++; evidence.ambiguousRecipes.add(id); }
        if (!correctOutput) evidence.otherOutputs++;
        if (requireUnique) {
            check(matching.size() == 1 && correctOutput, "Unique no-hint native selection failed: " + id);
            List<Item> material = TAG_WITNESSES.get(expected.materialTag()).stream().map(RecipeLookupProbe::item).toList();
            check(Arrays.stream(inputs).filter(stack -> material.contains(stack.getItem())).count() == expected.amount()
                    && Arrays.stream(inputs).filter(stack -> stack.is(item(expected.center()))).count() == 1
                    && Arrays.stream(inputs).filter(stack -> !stack.isEmpty()).count() == expected.amount() + 1,
                    "Reviewed material cost changed: " + id);
        }
    }

    private static JsonObject ingredientJson(String id) {
        JsonObject result = new JsonObject(); result.addProperty("item", proxyId(id)); return result;
    }
    private static String proxyId(String id) {
        String proxy = id.startsWith("minecraft:") ? id : MOD_ITEM_PROXIES.get(id);
        check(proxy != null, "Mod item needs an explicit proxy: " + id); return proxy;
    }
    private static Item item(String id) { return BuiltInRegistries.ITEM.get(ResourceLocation.parse(proxyId(id))); }
    private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
}
