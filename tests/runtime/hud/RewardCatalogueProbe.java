package qm.hudprobe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import qm.questui.client.RewardCatalogueData;

/** Loads the exact shipped client catalogue without launching a game or claiming a reward. */
public final class RewardCatalogueProbe {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    private static void rejected(JsonObject malformed) {
        try {
            RewardCatalogueData.parse(new StringReader(malformed.toString()));
            throw new AssertionError("Invalid reward catalogue was accepted");
        } catch (IllegalArgumentException expected) {
            // Parsing failure keeps the normal native claim path intact.
        }
    }
    public static void main(String[] args) throws Exception {
        RewardUiRegressionProbe.verify();
        String raw;
        try (var stream = RewardCatalogueProbe.class.getClassLoader().getResourceAsStream("assets/qm_quest_ui/reward_catalog.json")) {
            check(stream != null, "Missing shipped reward catalogue");
            raw = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        var data = RewardCatalogueData.parse(new StringReader(raw));
        check(data.rewards().size() == 1643, "Every actual reward must have a visible preview");
        check(data.pools().size() == 14, "Every themed pool must be visible");
        check(data.qualityDraws() == 1, "Preview must retain the existing reserved quality draw");
        int cubes = 0, refunds = 0, random = 0, twoCubeClaims = 0;
        for (var claim : data.rewards().values()) {
            if (claim.randomPool() != null) random++;
            if (claim.manufacture() != null) refunds++;
            for (var prize : claim.guaranteed()) {
                if (prize.item().startsWith("kubejs:mayhem_cube_t")) {
                    cubes += prize.count();
                    if (prize.count() == 2) twoCubeClaims++;
                }
            }
        }
        check(cubes == 222 && twoCubeClaims == 29, "Preview must show the full 193 + 29 cube quantities");
        check(refunds == 41 && random == 1449, "Guaranteed bundles and random packs were confused");
        check(data.pools().values().stream().flatMap(pool -> pool.entries().stream())
                .filter(entry -> entry.item().startsWith("kubejs:"))
                .allMatch(entry -> entry.requiresCraft().contains(entry.item())), "Display lost exact production gates");
        JsonObject malformed = JsonParser.parseString(raw).getAsJsonObject();
        malformed.addProperty("schema", 2); rejected(malformed);
        malformed = JsonParser.parseString(raw).getAsJsonObject();
        malformed.getAsJsonObject("rewards").entrySet().iterator().next().getValue().getAsJsonObject()
                .addProperty("random_pool", "missing_pool"); rejected(malformed);
        malformed = JsonParser.parseString(raw).getAsJsonObject();
        malformed.getAsJsonArray("levels").remove(0); rejected(malformed);
        System.out.println("REWARD_CATALOGUE_OK rewards=" + data.rewards().size() + " pools=" + data.pools().size()
                + " cubes=" + cubes + " bonus_claims=" + twoCubeClaims + " refunds=" + refunds);
    }
}
