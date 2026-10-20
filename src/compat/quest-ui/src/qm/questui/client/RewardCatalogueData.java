package qm.questui.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** Immutable display data. No reward, team, inventory, network or claim API. */
public record RewardCatalogueData(Map<Long, Claim> rewards, Map<Long, List<Long>> quests,
                                  Map<String, Pool> pools, List<Integer> draws,
                                  int qualityDraws, Map<Integer, String> levels) {
    private static final Pattern ITEM = Pattern.compile("[a-z0-9_.-]+:[a-z0-9_./-]+");
    public record Prize(String item, int count, String kind) {}
    public record Entry(String item, int count, String rarity, int level, List<String> requiresCraft) {}
    public record Pool(String key, Map<String, String> titles, List<Entry> entries) {}
    public record Claim(long questId, String randomPool, List<Prize> guaranteed, String manufacture) {}

    public static RewardCatalogueData empty() {
        return new RewardCatalogueData(Map.of(), Map.of(), Map.of(), List.of(), 0, Map.of());
    }

    public static RewardCatalogueData parse(Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        require(root.get("schema").getAsInt() == 1, "Unknown reward catalogue schema");
        List<Integer> draws = new ArrayList<>();
        root.getAsJsonArray("draws_by_level").forEach(value -> draws.add(value.getAsInt()));
        require(draws.size() == 7 && draws.stream().allMatch(n -> n > 0 && n <= 5), "Invalid draw counts");
        int qualityDraws = root.get("guaranteed_quality_draws").getAsInt();
        require(qualityDraws >= 0 && qualityDraws <= 1, "Invalid reserved quality draw count");
        Map<Integer, String> levels = new LinkedHashMap<>();
        for (JsonElement value : root.getAsJsonArray("levels")) {
            JsonObject level = value.getAsJsonObject();
            levels.put(level.get("level").getAsInt(), item(level.get("item").getAsString()));
        }
        require(levels.keySet().equals(java.util.Set.of(1, 2, 3, 4, 5, 6)), "Missing production milestone");
        Map<String, Pool> pools = new LinkedHashMap<>();
        for (JsonElement value : root.getAsJsonArray("pools")) {
            JsonObject raw = value.getAsJsonObject();
            String key = raw.get("key").getAsString();
            Map<String, String> titles = new LinkedHashMap<>();
            raw.getAsJsonObject("title").entrySet().forEach(e -> titles.put(e.getKey(), e.getValue().getAsString()));
            List<Entry> entries = new ArrayList<>();
            for (JsonElement e : raw.getAsJsonArray("entries")) {
                JsonObject entry = e.getAsJsonObject();
                List<String> craft = new ArrayList<>();
                if (entry.has("requires_craft")) entry.getAsJsonArray("requires_craft").forEach(i -> craft.add(item(i.getAsString())));
                int level = entry.has("min_level") ? entry.get("min_level").getAsInt() : 0;
                require(level >= 0 && level <= 6, "Invalid production level");
                String rarity = entry.get("rarity").getAsString();
                require(List.of("common", "uncommon", "rare", "epic").contains(rarity), "Unknown rarity");
                entries.add(new Entry(item(entry.get("item").getAsString()), count(entry.get("count")),
                        rarity, level, List.copyOf(craft)));
            }
            require(pools.put(key, new Pool(key, Map.copyOf(titles), List.copyOf(entries))) == null, "Duplicate reward pool");
        }
        Map<Long, Claim> rewards = new LinkedHashMap<>();
        for (var row : root.getAsJsonObject("rewards").entrySet()) {
            JsonObject raw = row.getValue().getAsJsonObject();
            List<Prize> guaranteed = new ArrayList<>();
            for (JsonElement value : raw.getAsJsonArray("guaranteed")) {
                JsonObject prize = value.getAsJsonObject();
                String kind = prize.get("kind").getAsString();
                require(List.of("cube", "refund", "item").contains(kind), "Unknown guaranteed prize");
                guaranteed.add(new Prize(item(prize.get("item").getAsString()), count(prize.get("count")), kind));
            }
            String pool = nullable(raw, "random_pool");
            require(pool == null || pools.containsKey(pool), "Missing displayed reward pool");
            String manufacture = nullable(raw, "requires_manufacture");
            if (manufacture != null) item(manufacture);
            require(!guaranteed.isEmpty() || pool != null, "Empty reward preview");
            rewards.put(id(row.getKey()), new Claim(id(raw.get("quest_id").getAsString()), pool,
                    List.copyOf(guaranteed), manufacture));
        }
        Map<Long, List<Long>> quests = new LinkedHashMap<>();
        for (var row : root.getAsJsonObject("quests").entrySet()) {
            long quest = id(row.getKey());
            List<Long> ids = new ArrayList<>();
            for (JsonElement value : row.getValue().getAsJsonArray()) {
                long reward = id(value.getAsString());
                require(rewards.containsKey(reward) && rewards.get(reward).questId() == quest, "Reward owner mismatch");
                ids.add(reward);
            }
            quests.put(quest, List.copyOf(ids));
        }
        require(quests.values().stream().mapToInt(List::size).sum() == rewards.size(), "Unlisted reward preview");
        return new RewardCatalogueData(Collections.unmodifiableMap(rewards), Collections.unmodifiableMap(quests),
                Collections.unmodifiableMap(pools), List.copyOf(draws), qualityDraws, Map.copyOf(levels));
    }

    private static String nullable(JsonObject object, String key) {
        JsonElement value = object.get(key);
        return value == null || value.isJsonNull() ? null : value.getAsString();
    }
    private static long id(String value) {
        require(value.matches("[0-9A-F]{16}"), "Invalid campaign identity");
        return Long.parseUnsignedLong(value, 16);
    }
    private static String item(String value) {
        require(ITEM.matcher(value).matches() && !value.equals("minecraft:air"), "Invalid display item");
        return value;
    }
    private static int count(JsonElement value) {
        int count = value.getAsInt();
        require(count > 0 && count <= 64, "Invalid displayed stack count");
        return count;
    }
    private static void require(boolean valid, String reason) {
        if (!valid) throw new IllegalArgumentException(reason);
    }
}
