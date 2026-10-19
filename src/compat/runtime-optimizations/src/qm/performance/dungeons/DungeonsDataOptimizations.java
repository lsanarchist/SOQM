package qm.performance.dungeons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

/** Small transformations of installed D&T data; no upstream data is bundled. */
public final class DungeonsDataOptimizations {
    public static final ResourceLocation ROOT = ResourceLocation.parse("nova_structures:root");
    public static final ResourceLocation BOSS_BEHAVIOUR = ResourceLocation.parse("nova_structures:boss_behaviour");
    public static final List<ResourceLocation> DISCOVERIES = List.of(
        ResourceLocation.parse("nova_structures:adventure/find_tavern"),
        ResourceLocation.parse("nova_structures:adventure/find_illager_manor"),
        ResourceLocation.parse("nova_structures:adventure/find_witch_villa"),
        ResourceLocation.parse("nova_structures:adventure/loot_illager_hideout"),
        ResourceLocation.parse("nova_structures:adventure/conquer_miner_outpost"),
        ResourceLocation.parse("nova_structures:nether/find_keep"),
        ResourceLocation.parse("nova_structures:nether/find_piglins"),
        ResourceLocation.parse("nova_structures:nether/find_donjon")
    );

    private DungeonsDataOptimizations() {}

    /** Match the complete small criterion before changing it; respect custom overrides. */
    public static void advancements(Map<ResourceLocation, JsonElement> data) {
        JsonElement existing = data.get(ROOT);
        if (existing == null || !existing.isJsonObject()) return;
        JsonObject copy = existing.getAsJsonObject().deepCopy();
        JsonObject criteria = object(copy, "criteria");
        if (criteria == null) return;
        boolean changed = false;
        for (ResourceLocation discovery : DISCOVERIES) {
            String id = discovery.toString();
            JsonObject expected = new JsonObject();
            expected.addProperty("trigger", "minecraft:tick");
            JsonObject completed = new JsonObject();
            completed.addProperty(id, true);
            JsonObject playerType = new JsonObject();
            playerType.addProperty("type", "player");
            playerType.add("advancements", completed);
            JsonObject player = new JsonObject();
            player.add("type_specific", playerType);
            JsonObject conditions = new JsonObject();
            conditions.add("player", player);
            expected.add("conditions", conditions);
            if (!expected.equals(criteria.get(id))) continue;
            JsonObject event = new JsonObject();
            event.addProperty("trigger", "minecraft:impossible");
            criteria.add(id, event);
            changed = true;
        }
        if (changed) data.put(ROOT, copy);
    }

    /** Only the regular invisible phantom's tick predicate changes. */
    public static JsonElement enchantment(JsonElement existing) {
        if (!existing.isJsonObject()) return existing;
        JsonObject copy = existing.getAsJsonObject().deepCopy();
        JsonObject effects = object(copy, "effects");
        if (effects == null || !effects.has("minecraft:tick") || !effects.get("minecraft:tick").isJsonArray()) return existing;
        boolean changed = false;
        for (JsonElement entry : effects.getAsJsonArray("minecraft:tick")) {
            if (!entry.isJsonObject()) continue;
            JsonObject effect = entry.getAsJsonObject();
            JsonObject requirements = object(effect, "requirements");
            if (requirements == null || !string(requirements, "condition", "minecraft:entity_properties")
                    || !string(requirements, "entity", "this")) continue;
            JsonObject predicate = object(requirements, "predicate");
            if (predicate == null || !string(predicate, "type", "minecraft:phantom")
                    || !string(predicate, "nbt", "{Tags:[\"phantom_boss\"]}")) continue;
            JsonObject mobEffects = object(predicate, "effects");
            if (mobEffects == null || !mobEffects.has("minecraft:invisibility")) continue;
            // Vanilla EntityPredicate already checks effects before NBT. Keep that order.
            predicate.remove("nbt");
            JsonObject directTag = new JsonObject();
            directTag.addProperty("condition", "qm_runtime_optimizations:entity_tag");
            directTag.addProperty("tag", "phantom_boss");
            JsonArray terms = new JsonArray();
            terms.add(requirements);
            terms.add(directTag);
            JsonObject all = new JsonObject();
            all.addProperty("condition", "minecraft:all_of");
            all.add("terms", terms);
            effect.add("requirements", all);
            changed = true;
        }
        return changed ? copy : existing;
    }

    private static JsonObject object(JsonObject parent, String key) {
        JsonElement value = parent.get(key);
        return value != null && value.isJsonObject() ? value.getAsJsonObject() : null;
    }

    private static boolean string(JsonObject parent, String key, String expected) {
        JsonElement value = parent.get(key);
        return value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()
            && expected.equals(value.getAsString());
    }
}
