package qm.rewards;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.event.EventResult;
import dev.ftb.mods.ftbquests.events.CustomRewardEvent;
import dev.ftb.mods.ftbquests.quest.reward.CustomReward;
import dev.ftb.mods.ftbquests.quest.reward.ItemReward;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardAutoClaim;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fixed, recipe-backed part of an existing personal FTB claim. The FTB ledger
 * remains the only persistent claim ledger. Manufacture is read, never granted.
 */
public final class RecipeRefunds extends SimplePreparableReloadListener<Map<Long, RecipeRefunds.Bundle>> {
    public static final String TAG = "soqm:recipe_refund";
    private static final Logger LOGGER = LoggerFactory.getLogger("qm_progressive_rewards");
    private static volatile Map<Long, Bundle> policy = Map.of();
    private static final ThreadLocal<Delivery> CLAIM = new ThreadLocal<>();

    record Prize(String item, int count) {}
    record Bundle(long questId, String output, List<Prize> items, String cube) {}
    private static final class Delivery {
        final Reward reward;
        final ServerPlayer player;
        final List<ItemReward> prizes;
        final boolean randomPack;
        boolean delivered;
        boolean rolled;

        Delivery(Reward reward, ServerPlayer player, Bundle bundle) {
            this.reward = reward;
            this.player = player;
            this.randomPack = bundle.cube() == null;
            this.prizes = new ArrayList<>();
            for (Prize prize : bundle.items()) add(prize.item(), prize.count());
            if (bundle.cube() != null) add(bundle.cube(), 1);
        }

        private void add(String id, int count) {
            // Transient payloads never acquire IDs or enter the quest's reward
            // collection. ItemReward preserves FTB's normal full-inventory path.
            ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)));
            prizes.add(new ItemReward(0L, reward.getQuest(), stack, count));
        }
    }

    private static boolean marked(Reward reward) { return reward != null && reward.hasTag(TAG); }

    private static Bundle bundle(Reward reward) {
        if (!(reward instanceof CustomReward) || !marked(reward)
                || !reward.hasTag("soqm:progressive_reward") || reward.isTeamReward()
                || reward.getAutoClaimType() != RewardAutoClaim.DISABLED) return null;
        Bundle bundle = policy.get(reward.id);
        return bundle != null && bundle.questId() == reward.getQuest().id ? bundle : null;
    }

    private static Bundle eligible(ServerPlayer player, Reward reward) {
        Bundle bundle = bundle(reward);
        return bundle != null && ProductionEvidence.realSurvivalPlayer(player)
                && player.server.isSameThread() && ProductionEvidence.hasProduced(player, bundle.output())
                ? bundle : null;
    }

    /** Called before FTB can mark the existing (player, reward ID) as claimed. */
    public static boolean canClaim(ServerPlayer player, Reward reward) {
        if (!marked(reward)) return true;
        if (eligible(player, reward) != null) return true;
        if (player != null && player.server.isSameThread()) {
            boolean ukrainian = player.getLanguage().startsWith("uk");
            Bundle bundle = bundle(reward);
            if (bundle == null) {
                player.sendSystemMessage(Component.literal(ukrainian
                        ? "Компенсація тимчасово недоступна: перевірте політику нагород. Нагороду не витрачено."
                        : "Crafting refund unavailable: check the reward policy. Your claim has not been spent."));
            } else {
                player.sendSystemMessage(Component.literal(ukrainian
                        ? "Для компенсації ви або ваша команда маєте виготовити: "
                        : "For this refund, you or your team must manufacture: ")
                        .append(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(bundle.output()))).getHoverName())
                        .append(Component.literal(ukrainian ? ". Нагороду не витрачено." : ". Your claim has not been spent.")));
            }
        }
        return false;
    }

    /** Permit the original random pack once within this accepted native claim. */
    public static boolean beginRandomPack(Reward reward, ServerPlayer player) {
        Delivery delivery = CLAIM.get();
        if (delivery == null || delivery.reward != reward || delivery.player != player
                || !delivery.randomPack || delivery.rolled) return false;
        delivery.rolled = true;
        return true;
    }

    /**
     * Wrap only the successful native Reward.claim invocation. A scoped receipt
     * permits the event listener once, including nested callbacks, and is always
     * removed on exceptions. A repeated or direct custom event cannot pay twice.
     */
    public static void invokeClaim(Reward reward, ServerPlayer player, boolean notify) {
        if (!marked(reward)) {
            reward.claim(player, notify);
            return;
        }
        Bundle bundle = eligible(player, reward);
        if (bundle == null) throw new IllegalStateException("Refund eligibility changed inside an accepted native claim");
        Delivery previous = CLAIM.get();
        CLAIM.set(new Delivery(reward, player, bundle));
        try {
            reward.claim(player, notify);
        } finally {
            if (previous == null) CLAIM.remove();
            else CLAIM.set(previous);
        }
    }

    /** Registered once at common mod construction, not once per resource reload. */
    static EventResult deliver(CustomRewardEvent event) {
        Delivery delivery = CLAIM.get();
        if (delivery == null || delivery.delivered || delivery.reward != event.getReward()
                || delivery.player != event.getPlayer()) return EventResult.pass();
        delivery.delivered = true;
        for (ItemReward prize : delivery.prizes) prize.claim(delivery.player, event.getNotify());
        // No crafting event, inventory scan, recipe completion, or ledger write.
        return EventResult.pass();
    }

    private static String string(JsonObject object, String key) throws IOException {
        JsonElement value = object.get(key);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
            throw new IOException("Expected string " + key);
        }
        return value.getAsString();
    }

    private static String item(String value, boolean cube) throws IOException {
        ResourceLocation id = ResourceLocation.tryParse(value);
        if (id == null || !id.toString().equals(value) || value.equals("minecraft:air")
                || value.contains("plastic") || value.contains("hdpe") || value.startsWith("ironchest:")
                || (cube ? !value.matches("kubejs:mayhem_cube_t[0-6]") : value.contains("mayhem_cube"))) {
            throw new IOException("Invalid refund item " + value);
        }
        return value;
    }

    private static long id(String value) throws IOException {
        if (!value.matches("[0-9A-F]{16}")) throw new IOException("Expected canonical FTB ID");
        return Long.parseUnsignedLong(value, 16);
    }

    static Map<Long, Bundle> read() {
        var path = FMLPaths.GAMEDIR.get().resolve("kubejs/config/soqm_rewards.json");
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            if (!root.has("schema") || !root.get("schema").isJsonPrimitive()
                    || !root.getAsJsonPrimitive("schema").isNumber()
                    || !root.get("schema").getAsString().equals("2")) throw new IOException("Expected policy schema 2");
            Set<String> tracked = new HashSet<>();
            for (JsonElement value : root.getAsJsonArray("production_items")) {
                if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) throw new IOException("Invalid production list");
                tracked.add(item(value.getAsString(), false));
            }
            Map<Long, Bundle> bundles = new HashMap<>();
            for (var entry : root.getAsJsonObject("reward_mappings").entrySet()) {
                JsonObject mapping = entry.getValue().getAsJsonObject();
                if (!mapping.has("refund")) continue;
                long rewardId = id(entry.getKey());
                long questId = id(string(mapping, "quest_id"));
                JsonObject refund = mapping.getAsJsonObject("refund");
                String output = item(string(refund, "output"), false);
                if (!tracked.contains(output)) throw new IOException("Refund output is not observed: " + output);
                if (refund.getAsJsonArray("recipe_ids").isEmpty()) throw new IOException("Missing recipe evidence");
                for (JsonElement value : refund.getAsJsonArray("recipe_ids")) {
                    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()
                            || ResourceLocation.tryParse(value.getAsString()) == null) throw new IOException("Invalid recipe ID");
                }
                List<Prize> prizes = new ArrayList<>();
                Set<String> seen = new HashSet<>();
                for (JsonElement value : refund.getAsJsonArray("items")) {
                    JsonObject row = value.getAsJsonObject();
                    String prize = item(string(row, "item"), false);
                    JsonElement countValue = row.get("count");
                    if (countValue == null || !countValue.isJsonPrimitive() || !countValue.getAsJsonPrimitive().isNumber()
                            || !countValue.getAsString().matches("[1-9][0-9]*")) throw new IOException("Invalid refund count");
                    int count = countValue.getAsInt();
                    if (count > 16 || !seen.add(prize) || prize.equals(output)) throw new IOException("Unsafe refund payload");
                    prizes.add(new Prize(prize, count));
                }
                if (prizes.isEmpty() || prizes.size() > 4) throw new IOException("Expected 1–4 fixed refund stacks");
                String cube = mapping.has("cube") ? item(string(mapping, "cube"), true) : null;
                bundles.put(rewardId, new Bundle(questId, output, List.copyOf(prizes), cube));
            }
            return Map.copyOf(bundles);
        } catch (IOException | RuntimeException invalid) {
            LOGGER.error("Recipe refunds are disabled: cannot read {} ({})", path, invalid.toString());
            return Map.of();
        }
    }

    private static void install(Map<Long, Bundle> next) {
        // Registries are read only on the resource-apply/server thread, after
        // KubeJS registers its items. No stale Item or Quest objects cross reload.
        for (Bundle bundle : next.values()) {
            List<String> items = new ArrayList<>();
            items.add(bundle.output());
            bundle.items().forEach(prize -> items.add(prize.item()));
            if (bundle.cube() != null) items.add(bundle.cube());
            for (String item : items) {
                if (!BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(item))) {
                    LOGGER.error("Recipe refunds are disabled: unregistered item {}", item);
                    policy = Map.of();
                    return;
                }
            }
        }
        policy = Map.copyOf(next);
        LOGGER.info("Recipe refunds loaded: {} personal reward bundles", next.size());
    }

    static void loadNow() { install(read()); }
    static void clear() { policy = Map.of(); CLAIM.remove(); }
    @Override protected Map<Long, Bundle> prepare(ResourceManager resources, ProfilerFiller profiler) { return read(); }
    @Override protected void apply(Map<Long, Bundle> next, ResourceManager resources, ProfilerFiller profiler) { install(next); }
}
