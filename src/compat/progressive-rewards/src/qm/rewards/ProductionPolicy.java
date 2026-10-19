package qm.rewards;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The native observer and KubeJS reward handler use the same authored file. */
final class ProductionPolicy extends SimplePreparableReloadListener<Set<String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger("qm_progressive_rewards");
    private record Policy(Set<String> ids, Set<Item> items) {}
    private static final Policy EMPTY = new Policy(Set.of(), Set.of());
    private static volatile Policy policy = EMPTY;

    static boolean allows(String itemId) { return policy.ids().contains(itemId); }
    static boolean allows(Item item) { return policy.items().contains(item); }
    static Set<String> allowedItems() { return policy.ids(); }
    static void clear() { policy = EMPTY; }

    static Set<String> read() {
        Path path = FMLPaths.GAMEDIR.get().resolve("kubejs/config/soqm_rewards.json");
        try {
            JsonElement parsed = JsonParser.parseString(Files.readString(path));
            if (!parsed.isJsonObject() || !parsed.getAsJsonObject().has("schema")
                    || !parsed.getAsJsonObject().get("schema").isJsonPrimitive()
                    || !parsed.getAsJsonObject().getAsJsonPrimitive("schema").isNumber()
                    || !parsed.getAsJsonObject().get("schema").getAsString().equals("2")) {
                throw new IOException("Expected integer policy schema 2");
            }
            if (!parsed.getAsJsonObject().has("production_items")
                    || !parsed.getAsJsonObject().get("production_items").isJsonArray()) {
                throw new IOException("production_items must be an array of exact item IDs");
            }
            Set<String> items = new HashSet<>();
            for (JsonElement element : parsed.getAsJsonObject().getAsJsonArray("production_items")) {
                if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                    throw new IOException("production_items contains a non-string entry");
                }
                String id = element.getAsString();
                ResourceLocation location = ResourceLocation.tryParse(id);
                if (location == null || !location.toString().equals(id) || id.equals("minecraft:air")
                        || id.contains("plastic") || id.contains("hdpe") || id.contains("mayhem_cube")) {
                    throw new IOException("Invalid exact production item ID: " + id);
                }
                if (!items.add(id)) throw new IOException("Duplicate production item ID: " + id);
            }
            if (items.isEmpty()) throw new IOException("production_items must not be empty");
            return Set.copyOf(items);
        } catch (IOException | RuntimeException invalid) {
            LOGGER.error("Production evidence is disabled: cannot read {} ({})", path, invalid.toString());
            return Set.of();
        }
    }

    static void loadNow() { install(read()); }

    private static void install(Set<String> next) {
        // Installation runs on the resource-apply/server thread after item
        // registration, including KubeJS startup items. Never read registries on
        // the background preparation thread.
        Set<Item> items = Collections.newSetFromMap(new IdentityHashMap<>());
        for (String id : next) {
            ResourceLocation location = ResourceLocation.parse(id);
            if (!BuiltInRegistries.ITEM.containsKey(location)) {
                LOGGER.error("Production evidence is disabled: policy item does not exist: {}", id);
                next = Set.of();
                items.clear();
                break;
            }
            items.add(BuiltInRegistries.ITEM.get(location));
        }
        // Publish both views together. Filtering a finish needs no registry lookup
        // or item-ID string allocation, and reload/stop cannot retain old entries.
        policy = new Policy(Set.copyOf(next), Collections.unmodifiableSet(items));
        LOGGER.info("Production policy loaded: {} allowed item IDs", next.size());
    }

    @Override protected Set<String> prepare(ResourceManager resources, ProfilerFiller profiler) { return read(); }
    @Override protected void apply(Set<String> next, ResourceManager resources, ProfilerFiller profiler) { install(next); }
}
