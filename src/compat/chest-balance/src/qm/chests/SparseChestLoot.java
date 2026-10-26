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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.ItemEnchantments;

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
    private static final Map<String, Set<String>> CURATED = itemGroups("profile_items");
    private static final Map<String, Set<String>> FINDS = itemGroups("find_items");
    private static final Set<String> REWARD_TABLES = strings("native_reward_tables");
    private static final List<String> BLOCKED_PREFIXES = new ArrayList<>(strings("forbidden_prefixes"));
    private static final List<String> PROTECTED_PARTS = new ArrayList<>(strings("protected_path_parts"));
    private static final List<String> VALUABLE_PARTS = new ArrayList<>(strings("valuable_parts"));
    private static final String DUNGEON_REWARD_MARKER = DATA.get("dungeon_reward_marker").getAsString();
    private static final JsonObject ACCESSORIES = DATA.getAsJsonObject("accessories");
    private static final Set<String> ACCESSORY_IDS = accessoryIds();
    private static final Map<String, List<String>> ACCESSORY_POOLS = accessoryPools();

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
    private static Map<String, Set<String>> itemGroups(String field) {
        Map<String, Set<String>> out = new HashMap<>();
        for (var entry : DATA.getAsJsonObject(field).entrySet()) {
            Set<String> items = new HashSet<>();
            for (var value : entry.getValue().getAsJsonArray()) items.add(value.getAsString());
            out.put(entry.getKey(), Set.copyOf(items));
        }
        return Map.copyOf(out);
    }
    private static Set<String> accessoryIds() {
        Set<String> out = new HashSet<>();
        for (var value : ACCESSORIES.getAsJsonArray("recognized_items")) out.add(value.getAsString());
        return Set.copyOf(out);
    }
    private static Map<String, List<String>> accessoryPools() {
        Map<String, List<String>> out = new HashMap<>();
        for (var entry : ACCESSORIES.getAsJsonObject("pools").entrySet()) {
            List<String> items = new ArrayList<>();
            for (var value : entry.getValue().getAsJsonArray()) items.add(value.getAsString());
            out.put(entry.getKey(), List.copyOf(items));
        }
        return Map.copyOf(out);
    }
    public static boolean isAccessory(ItemStack stack) { return ACCESSORY_IDS.contains(id(stack)); }

    /** Pick only unused, installed items; native Relics default initialization is retained. */
    private static ItemStack accessory(String profile, Set<String> reserved, RandomSource random) {
        JsonObject rules = ACCESSORIES.getAsJsonObject("profiles").getAsJsonObject(profile);
        List<ResourceLocation> eligible = new ArrayList<>();
        for (String item : ACCESSORY_POOLS.get(rules.get("pool").getAsString())) {
            ResourceLocation key = ResourceLocation.parse(item);
            if (!reserved.contains(item) && !BLOCKED.contains(item) && BuiltInRegistries.ITEM.containsKey(key))
                eligible.add(key);
        }
        if (eligible.isEmpty()) return ItemStack.EMPTY;
        ResourceLocation chosen = eligible.get(random.nextInt(eligible.size()));
        ItemStack result = BuiltInRegistries.ITEM.get(chosen).getDefaultInstance();
        if (result.isEmpty() || isBlocked(result)) return ItemStack.EMPTY;
        result.setCount(1);
        reserved.add(chosen.toString());
        return result;
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
        if (tableId != null && tableId.getNamespace().equals("dungeons_arise")
                && (stack.has(DataComponents.CUSTOM_NAME) || stack.has(DataComponents.ITEM_NAME)
                    || stack.has(DataComponents.CUSTOM_MODEL_DATA) || stack.has(DataComponents.LORE)
                    || ariseEnchantments(stack.get(DataComponents.ENCHANTMENTS))
                    || ariseEnchantments(stack.get(DataComponents.STORED_ENCHANTMENTS)))) return true;
        if (PROTECTED_PARTS.stream().anyMatch(path::contains)) return true;
        return tableId != null && REWARD_TABLES.contains(tableId.toString()) && item.getNamespace().equals("aether");
    }
    private static boolean ariseEnchantments(ItemEnchantments enchantments) {
        return enchantments != null && enchantments.keySet().stream().anyMatch(holder ->
                holder.unwrapKey().map(key -> key.location().getNamespace().equals("dungeons_arise")).orElse(false));
    }
    private static String id(ItemStack stack) { return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(); }
    private static boolean valuable(ItemStack stack, String profile) {
        String id = id(stack), path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        if (CURATED.get(profile).contains(id) || id.equals("chancecubes:chance_cube")) return false;
        return VALUABLE.contains(id) || VALUABLE_PARTS.stream().anyMatch(path::contains)
                || stack.isEnchanted() || stack.getRarity() == Rarity.RARE || stack.getRarity() == Rarity.EPIC;
    }

    /** Authored combat prizes keep their identity while global loot modifiers run. */
    public static boolean isDungeonCombatReward(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        return custom != null && custom.copyTag().getBoolean(DUNGEON_REWARD_MARKER);
    }

    /** The owned marker is temporary; affixes, gem identity and all other data survive. */
    private static boolean takeDungeonRewardMarker(ItemStack stack) {
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) return false;
        var tag = custom.copyTag();
        if (!tag.getBoolean(DUNGEON_REWARD_MARKER)) return false;
        tag.remove(DUNGEON_REWARD_MARKER);
        if (tag.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
        else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return true;
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
        int slotLimit = Math.min(availableSlots, random.nextIntBetweenInclusive(
                rules.get("min_slots").getAsInt(), rules.get("max_slots").getAsInt()));
        int unitLimit = rules.get("max_units").getAsInt();
        int stackCap = rules.get("stack_cap").getAsInt();
        float valuableChance = rules.get("valuable_keep_chance").getAsFloat();
        int valuableLimit = rules.get("max_valuables").getAsInt();
        int combatRewardLimit = rules.has("combat_rewards") ? rules.get("combat_rewards").getAsInt() : 0;
        var protectedLoot = new ArrayList<ItemStack>();
        var combatRewards = new ArrayList<ItemStack>();
        var finds = new ArrayList<ItemStack>();
        var valuables = new ArrayList<ItemStack>();
        var supply = new ArrayList<ItemStack>();
        var ordinary = new ArrayList<ItemStack>();
        var merged = merge(generated);
        // Reserve every native type first: a replacement must never steal the
        // identity of a later, independently rolled reward with custom data.
        Set<String> reservedAccessories = new HashSet<>();
        for (ItemStack stack : merged) if (isAccessory(stack) && !isBlocked(stack)) reservedAccessories.add(id(stack));
        Set<String> nativeAccessories = new HashSet<>();
        int duplicateAccessories = 0;
        boolean cubeSeen = false;
        for (ItemStack stack : merged) {
            if (isBlocked(stack)) continue;
            if (isAccessory(stack)) {
                int count = Math.min(stack.getCount(), availableSlots);
                if (nativeAccessories.add(id(stack))) {
                    stack.setCount(1);
                    protectedLoot.add(stack);
                    count--;
                }
                duplicateAccessories = Math.min(availableSlots, duplicateAccessories + Math.max(0, count));
                continue;
            }
            if (isProtected(stack, tableId)) { protectedLoot.add(stack); continue; }
            if (combatRewardLimit > 0 && takeDungeonRewardMarker(stack)) {
                // Dedicated DD tables already made the combat reward roll.
                // Do not reroll it or let filler/native treasure displace it.
                stack.setCount(1);
                if (combatRewards.size() < combatRewardLimit) combatRewards.add(stack);
                continue;
            }
            String id = id(stack);
            if (FINDS.get(profile).contains(id)) {
                // The loot table has already rolled this bonus. Keep one small
                // find ahead of filler instead of applying a second rare roll.
                stack.setCount(1); finds.add(stack); continue;
            }
            if (id.equals("chancecubes:chance_cube")) {
                if (cubeSeen) continue;
                cubeSeen = true; stack.setCount(1); supply.add(stack); continue;
            }
            if (valuable(stack, profile)) {
                // Native treasures improve with the danger of the location.
                // A separate slot limit prevents a treasure-rich source table
                // from turning this into a pile of enchanted equipment.
                if (random.nextFloat() >= valuableChance) continue;
                stack.setCount(1);
                valuables.add(stack); continue;
            } else {
                stack.setCount(Math.min(stack.getCount(), Math.min(stackCap, stack.getMaxStackSize())));
            }
            if (CURATED.get(profile).contains(id)) supply.add(stack); else ordinary.add(stack);
        }
        // Original keys, custom rewards and all first accessory copies precede
        // replacements; physical capacity and guaranteed combat prizes win.
        for (int index = 0; index < duplicateAccessories
                && protectedLoot.size() + combatRewards.size() < availableSlots; index++) {
            ItemStack replacement = accessory(profile, reservedAccessories, random);
            if (replacement.isEmpty()) break;
            protectedLoot.add(replacement);
        }
        Util.shuffle(finds, random); Util.shuffle(valuables, random);
        Util.shuffle(supply, random); Util.shuffle(ordinary, random);
        generated.clear();
        // Keep the source's maps, keys, lore, artifacts and boss trophies first.
        // Protected items and the bounded DD combat prizes below may expand
        // the soft budget; ordinary filler cannot expand it.
        int units = 0;
        for (ItemStack stack : protectedLoot) {
            if (generated.size() >= availableSlots) break;
            generated.add(stack); units += stack.getCount();
        }
        // Native keys/trophies remain first. The authored one or two combat
        // prizes follow before ordinary budgets; actual container space is
        // always a hard limit. Only DD profiles can enable these prizes.
        for (ItemStack stack : combatRewards) {
            if (generated.size() >= availableSlots) break;
            generated.add(stack); units += stack.getCount();
        }
        slotLimit = Math.max(slotLimit, generated.size());
        JsonObject accessoryRules = ACCESSORIES.getAsJsonObject("profiles").getAsJsonObject(profile);
        long accessoryTypes = generated.stream().filter(SparseChestLoot::isAccessory).map(SparseChestLoot::id).distinct().count();
        if (ACCESSORIES.get("bonus_max").getAsInt() > 0
                && accessoryTypes < ACCESSORIES.get("bonus_below_distinct").getAsInt()
                && generated.size() < slotLimit && units < unitLimit
                && random.nextFloat() < accessoryRules.get("bonus_chance").getAsFloat()) {
            ItemStack bonus = accessory(profile, reservedAccessories, random);
            if (!bonus.isEmpty()) { generated.add(bonus); units++; }
        }
        var selected = new ArrayList<ItemStack>();
        // At most one curated find per chest, even if upstream modifiers add
        // more copies. Progression keys and boss trophies still take priority.
        if (!finds.isEmpty()) selected.add(finds.getFirst());
        selected.addAll(valuables.subList(0, Math.min(valuableLimit, valuables.size())));
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
