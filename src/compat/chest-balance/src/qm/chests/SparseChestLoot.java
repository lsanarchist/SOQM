package qm.chests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/** Finite table policies, applied after all loot modifiers but before slot fill.
 * No server, level, player, container or ItemStack survives this call.
 * Existing nonempty slots are never passed here by vanilla LootTable.fill.
 */
public final class SparseChestLoot {
    private static final JsonObject DATA = load();
    private static final Map<String, String> ROUTES = routes();
    private static final Set<String> BLOCKED = strings("forbidden_exact");
    private static final Set<String> PLASTIC = strings("plastic");
    private static final Set<String> PROTECTED = strings("protected_exact");
    private static final Set<String> PROTECTED_NS = strings("protected_namespaces");
    private static final Set<String> VALUABLE = strings("valuable_exact");
    private static final Map<String, Set<String>> CURATED = curated();
    private static final Set<String> REWARD_TABLES = strings("native_reward_tables");
    private static final List<String> BLOCKED_PREFIXES = new ArrayList<>(strings("forbidden_prefixes"));
    private static final List<String> PROTECTED_PARTS = new ArrayList<>(strings("protected_path_parts"));
    private static final List<String> VALUABLE_PARTS = new ArrayList<>(strings("valuable_parts"));

    private SparseChestLoot() {}

    private static JsonObject load() {
        var stream = SparseChestLoot.class.getResourceAsStream("/qm_chest_policy.json");
        if (stream == null) throw new IllegalStateException("Missing packaged QM chest policy");
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) { throw new IllegalStateException("Invalid packaged QM chest policy", e); }
    }
    private static Set<String> strings(String field) {
        Set<String> out = new HashSet<>();
        for (var v : DATA.getAsJsonArray(field)) out.add(v.getAsString());
        return Set.copyOf(out);
    }
    private static Map<String, String> routes() {
        Map<String, String> out = new HashMap<>();
        for (var entry : DATA.getAsJsonObject("routes").entrySet()) out.put(entry.getKey(), entry.getValue().getAsString());
        return Map.copyOf(out);
    }
    private static Map<String, Set<String>> curated() {
        Map<String, Set<String>> out = new HashMap<>();
        for (var entry : DATA.getAsJsonObject("profile_items").entrySet()) {
            Set<String> items = new HashSet<>();
            for (var value : entry.getValue().getAsJsonArray()) items.add(value.getAsString());
            out.put(entry.getKey(), Set.copyOf(items));
        }
        return Map.copyOf(out);
    }
    public static String profile(ResourceLocation tableId) { return tableId == null ? null : ROUTES.get(tableId.toString()); }
    public static boolean isPlastic(ItemStack stack) { return PLASTIC.contains(id(stack)); }
    public static boolean isBlocked(ItemStack stack) {
        String id = id(stack);
        return BLOCKED.contains(id) || BLOCKED_PREFIXES.stream().anyMatch(id::startsWith);
    }
    public static boolean isProtected(ItemStack stack, ResourceLocation tableId) {
        ResourceLocation item = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String id = item.toString(), path = item.getPath();
        if (PROTECTED.contains(id) || PROTECTED_NS.contains(item.getNamespace())) return true;
        if (PROTECTED_PARTS.stream().anyMatch(path::contains)) return true;
        return tableId != null && REWARD_TABLES.contains(tableId.toString()) && item.getNamespace().equals("aether");
    }
    private static String id(ItemStack stack) { return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(); }
    private static boolean valuable(ItemStack stack, String profile) {
        String id = id(stack), path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        if (CURATED.get(profile).contains(id) || id.equals("chancecubes:chance_cube")) return false;
        return VALUABLE.contains(id) || VALUABLE_PARTS.stream().anyMatch(path::contains)
                || stack.isEnchanted() || stack.getRarity() == Rarity.RARE || stack.getRarity() == Rarity.EPIC;
    }

    private static List<ItemStack> merge(Iterable<ItemStack> source) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack item : source) {
            if (item.isEmpty()) continue;
            int left = item.getCount();
            for (ItemStack old : result) {
                if (left == 0) break;
                if (!ItemStack.isSameItemSameComponents(old, item)) continue;
                int moved = Math.min(left, old.getMaxStackSize() - old.getCount());
                if (moved > 0) { old.grow(moved); left -= moved; }
            }
            while (left > 0) {
                int count = Math.min(left, item.getMaxStackSize());
                result.add(item.copyWithCount(count)); left -= count;
            }
        }
        return result;
    }

    /** Returns false outside the exact allowlist, leaving vanilla behavior intact. */
    public static boolean compact(ResourceLocation tableId, ObjectArrayList<ItemStack> generated,
                                  int availableSlots, RandomSource random) {
        String profile = profile(tableId);
        if (profile == null) return false;
        JsonObject rules = DATA.getAsJsonObject("profiles").getAsJsonObject(profile);
        int slotLimit = Math.min(availableSlots, rules.get("max_slots").getAsInt());
        int unitLimit = rules.get("max_units").getAsInt();
        int stackCap = rules.get("stack_cap").getAsInt();
        var protectedLoot = new ArrayList<ItemStack>();
        var supply = new ArrayList<ItemStack>();
        var ordinary = new ArrayList<ItemStack>();
        boolean cubeSeen = false;
        for (ItemStack stack : merge(generated)) {
            if (isBlocked(stack)) continue;
            if (isProtected(stack, tableId)) { protectedLoot.add(stack); continue; }
            String id = id(stack);
            if (id.equals("chancecubes:chance_cube")) {
                if (cubeSeen) continue;
                cubeSeen = true; stack.setCount(1); supply.add(stack); continue;
            }
            if (valuable(stack, profile)) {
                // Preserve an actual rare find occasionally, without filling a
                // village with enchanted equipment or completed technology.
                if (random.nextFloat() >= 0.10F) continue;
                stack.setCount(1);
            } else {
                stack.setCount(Math.min(stack.getCount(), Math.min(stackCap, stack.getMaxStackSize())));
            }
            if (CURATED.get(profile).contains(id)) supply.add(stack); else ordinary.add(stack);
        }
        Util.shuffle(supply, random); Util.shuffle(ordinary, random);
        generated.clear();
        // Keep the source's maps, keys, lore, artifacts and boss trophies. They
        // consume the ordinary budget first; the budget may expand only for
        // these pre-existing progression items, never to add more supplies.
        int units = 0;
        for (ItemStack stack : protectedLoot) {
            if (generated.size() >= availableSlots) break;
            generated.add(stack); units += stack.getCount();
        }
        slotLimit = Math.max(slotLimit, generated.size());
        var selected = new ArrayList<ItemStack>();
        // Keep one small modded bundle when available, then mix remaining
        // supplies with the source loot so every chest retains local flavor.
        if (!supply.isEmpty()) selected.add(supply.removeFirst());
        ordinary.addAll(supply); Util.shuffle(ordinary, random); selected.addAll(ordinary);
        Set<String> ordinaryIds = new HashSet<>();
        for (ItemStack stack : selected) {
            if (generated.size() >= slotLimit || units >= unitLimit) break;
            // A source can generate many independently split stacks of the
            // same plain material; limit their total contribution to one stack.
            String id = id(stack);
            if (stack.getMaxStackSize() > 1 && !ordinaryIds.add(id)) continue;
            stack.setCount(Math.min(stack.getCount(), unitLimit - units));
            if (!stack.isEmpty()) { generated.add(stack); units += stack.getCount(); }
        }
        Util.shuffle(generated, random);
        return true;
    }
}
