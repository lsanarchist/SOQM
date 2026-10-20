package soqm.validation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbquests.events.CustomRewardEvent;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.CustomReward;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardAutoClaim;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.util.QuestKey;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import dev.ftb.mods.ftbteams.data.TeamManagerImpl;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import qm.rewards.ProductionEvidence;
import qm.rewards.RecipeRefunds;

/** Test-only helper: real native claims and teams; never installed in the pack. */
public final class ProgressiveRewardsProbe {
    private static final UUID FIRST = UUID.fromString("f478a103-d481-4d68-acb5-5aff9979a8e7");
    private static final UUID SECOND = UUID.fromString("b95a9829-5d08-4c3c-af3f-c7cbe1a78586");
    private static final UUID THIRD = UUID.fromString("df08cc32-40ae-431e-9238-9b9bccb4a745");
    private static final UUID MIGRATION = UUID.fromString("f4d41bd9-a465-49fc-bd73-353012b05a7d");
    private static final String[] RARITIES = {"common", "uncommon", "rare", "epic"};
    private static final long OLD_TIME = 1_100_000L;
    private static final long NEW_TIME = 2_200_000L;
    private static final long BOOK_ID = 0x514D1311B22E8A64L;
    private static final List<Long> legacyShared = new ArrayList<>();
    private static final List<Long> oldPersonal = new ArrayList<>();
    private static SNBTCompoundTag migratedSnapshot;
    private static UUID destinationTeam;
    private static long switchedReward;
    private static JsonObject policy;
    private static long refundPackId;
    private static long refundCubeId;
    private static UUID refundTeam;

    private ProgressiveRewardsProbe() {}

    public static void run(MinecraftServer server, Map<String, Object> results) throws Exception {
        legacyShared.clear();
        oldPersonal.clear();
        policy = JsonParser.parseString(Files.readString(Path.of("kubejs/config/soqm_rewards.json"))).getAsJsonObject();
        check(policy.get("schema").getAsInt() == 2, "Actual runtime policy uses schema 2");
        check(policy.getAsJsonArray("pools").size() == 14, "Full authored catalogue has fourteen themes");
        check(policy.getAsJsonArray("levels").size() == 6, "Six production milestones are present");
        ServerQuestFile file = ServerQuestFile.INSTANCE;
        List<CustomReward> custom = roundTripDefinitions(server, results);
        Set<String> actualRewardIds = custom.stream().map(reward -> reward.getCodeString())
                .collect(java.util.stream.Collectors.toSet());
        check(policy.getAsJsonObject("reward_mappings").keySet().equals(actualRewardIds),
                "Runtime mappings match every live custom reward ID, without missing or stale entries");
        results.put("native_actual_custom_reward_mappings", actualRewardIds.size());
        testLegacyProgress(server, results);

        ProbePlayer first = player(server, FIRST, "SOQMRewardA");
        ProbePlayer second = player(server, SECOND, "SOQMRewardB");
        ProbePlayer third = player(server, THIRD, "SOQMRewardC");
        TeamManagerImpl manager = (TeamManagerImpl) FTBTeamsAPI.api().getManager();
        PartyTeam party = manager.createParty(first, "SOQM rewards A");
        party.join(second);
        TeamData claims = file.getOrCreateTeamData(party);
        Team personalSecond = manager.getPlayerTeamForPlayerID(SECOND).orElseThrow();
        // Persist a clean, already existing personal team before any party claim.
        // The later leave check therefore cannot succeed because it was newly dirty.
        TeamData personalBefore = file.getOrCreateTeamData(personalSecond);
        personalBefore.markDirty();
        personalBefore.saveIfChanged();

        int catalogueEntries = 0;
        for (JsonElement element : policy.getAsJsonArray("pools")) {
            JsonObject pool = element.getAsJsonObject();
            catalogueEntries += pool.getAsJsonArray("entries").size();
            for (JsonElement entryElement : pool.getAsJsonArray("entries")) {
                String id = item(entryElement.getAsJsonObject());
                check(BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(id))
                                && !new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id))).isEmpty(),
                        "Every authored prize is an actual nonempty registered item: " + id);
            }
            verifyPoolCommand(server, first, pool, 0);
        }
        check(catalogueEntries == 326, "The complete reviewed catalogue is present in the actual runtime policy");
        verifyStatusCommand(server, first);
        results.put("native_actual_reward_catalogue_entries", catalogueEntries);
        results.put("native_actual_policy_loaded_without_fallback", true);

        CustomReward same = custom.stream().filter(reward -> !reward.hasTag(RecipeRefunds.TAG)).findFirst().orElseThrow();
        JsonObject pool = poolFor(same);
        List<JsonObject> start = eligible(first, pool, 0);
        check(start.size() >= 2, "Two distinct initial prizes exercise independent player rolls");
        JsonObject firstPrize = start.getFirst();
        JsonObject secondPrize = start.stream().filter(e -> !item(e).equals(item(firstPrize))).findFirst().orElseThrow();
        complete(claims, same);
        claimTarget(first, claims, same, pool, firstPrize, 0);
        Map<String, Integer> firstItems = inventory(first);
        check(claims.isRewardClaimed(FIRST, same) && !claims.isRewardClaimed(SECOND, same),
                "The first native personal claim does not consume the teammate's claim");
        claimTarget(second, claims, same, pool, secondPrize, 0);
        check(!firstItems.equals(inventory(second)), "Each player performs an independent native roll");
        denyDuplicate(first, claims, same);
        denyDuplicate(second, claims, same);
        switchedReward = same.id;
        results.put("native_two_player_personal_claims_and_independent_rolls", true);
        results.put("native_level_zero_three_draw_delivery", true);
        party.join(third);
        check(claims.isCompleted(same.getQuest()) && !claims.isRewardClaimed(THIRD, same),
                "A late joiner inherits completed team progress with their personal reward still available");
        claimTarget(third, claims, same, pool, firstPrize, 0);
        denyDuplicate(third, claims, same);
        party.leave(THIRD);
        results.put("native_late_joiner_personal_claim_once", true);

        // The production helper performs an actual recipe through ResultSlot;
        // none of these unlocks are direct writes to the production ledger.
        JsonObject levelTwo = milestone(2);
        craftOutput(server, first, levelTwo.get("item").getAsString());
        check(ProductionEvidence.hasProduced(second, levelTwo.get("item").getAsString()),
                "The teammate can use a real shared production milestone");
        CustomReward middle = unclaimed(custom, claims, first, pool.get("key").getAsString());
        complete(claims, middle);
        claimTarget(first, claims, middle, pool, eligible(first, pool, 2).getFirst(), 2);
        results.put("native_production_level_two_four_draw_delivery", true);

        JsonObject levelSix = milestone(6);
        craftOutput(server, first, levelSix.get("item").getAsString());
        JsonObject manufacturedPool = null, manufactured = null;
        for (JsonElement poolElement : policy.getAsJsonArray("pools")) {
            JsonObject candidatePool = poolElement.getAsJsonObject();
            for (JsonElement entryElement : candidatePool.getAsJsonArray("entries")) {
                JsonObject entry = entryElement.getAsJsonObject();
                if (item(entry).startsWith("kubejs:") && requiresOwnProduct(entry) && !ProductionEvidence.hasProduced(first, item(entry))
                        && craftingRecipe(server, item(entry)) != null) {
                    manufacturedPool = candidatePool; manufactured = entry; break;
                }
            }
            if (manufactured != null) break;
        }
        check(manufactured != null, "Actual catalogue contains a still-locked craftable custom progression component");
        String manufacturedId = item(manufactured);
        first.getInventory().add(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(manufacturedId))));
        check(!ProductionEvidence.hasProduced(first, manufacturedId), "Holding an expensive prize is not a production unlock");
        verifyPoolCommand(server, first, manufacturedPool, 6);
        check(eligible(first, manufacturedPool, 6).stream().noneMatch(e -> item(e).equals(manufacturedId)),
                "High production level alone keeps the exact unproduced item locked");
        CustomReward negative = unclaimed(custom, claims, first, manufacturedPool.get("key").getAsString());
        complete(claims, negative);
        JsonObject safe = eligible(first, manufacturedPool, 6).getFirst();
        claimTarget(first, claims, negative, manufacturedPool, safe, 6);
        check(!inventory(first).containsKey(manufacturedId), "A live native claim cannot yield the unproduced expensive item");
        craftOutput(server, first, manufacturedId);
        check(ProductionEvidence.hasProduced(second, manufacturedId), "Manufactured output unlock is shared by the current team");
        CustomReward positive = unclaimed(custom, claims, second, manufacturedPool.get("key").getAsString());
        complete(claims, positive);
        claimTarget(second, claims, positive, manufacturedPool, manufactured, 6);
        verifyPoolCommand(server, second, manufacturedPool, 6);
        results.put("native_unproduced_expensive_prize_rejected", manufacturedId);
        results.put("native_actual_craft_unlock_and_five_draw_prize_delivery", manufacturedId);

        testRecipeRefunds(server, party, first, second, custom, results);
        testTeamSwitch(server, manager, party, second, third, same, results);
        manager.saveNow();
        file.saveNow();
    }

    private static List<CustomReward> roundTripDefinitions(MinecraftServer server, Map<String, Object> results) {
        List<CustomReward> custom = new ArrayList<>();
        int[] total = {0};
        ServerQuestFile.INSTANCE.forAllQuests(q -> q.getRewards().forEach(reward -> {
            total[0]++;
            check(!reward.isTeamReward(), "Every outer campaign reward is personal");
            check(reward.getAutoClaimType() == RewardAutoClaim.DISABLED, "Native auto field disables automatic claims");
            if (reward instanceof CustomReward dynamic) {
                custom.add(dynamic);
                JsonObject binding = policy.getAsJsonObject("reward_mappings").getAsJsonObject(reward.getCodeString());
                check(binding != null, "An exact long reward ID resolves in the runtime mapping");
                check(binding.get("quest_id").getAsString().equals(reward.getQuest().getCodeString()),
                        "Mapping references the actual parent quest");
                check(reward.hasTag("soqm:progressive_reward") && reward.hasTag("soqm:reward_pool:" + binding.get("pool").getAsString()),
                        "Actual custom reward has marker and theme tags");
                CompoundTag disk = new CompoundTag();
                reward.writeData(disk, server.registryAccess());
                check(disk.getString("auto").equals("disabled"), "Native serialized key is auto, not auto_claim");
                CustomReward diskCopy = new CustomReward(reward.id, q);
                diskCopy.readData(disk, server.registryAccess());
                check(!diskCopy.isTeamReward() && diskCopy.getAutoClaimType() == RewardAutoClaim.DISABLED
                        && diskCopy.hasTag("soqm:progressive_reward"), "Native custom reward NBT round trip");
                RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), server.registryAccess());
                try {
                    reward.writeNetData(buffer);
                    CustomReward copy = new CustomReward(reward.id, q);
                    copy.readNetData(buffer);
                    check(buffer.readableBytes() == 0 && !copy.isTeamReward()
                                    && copy.getAutoClaimType() == RewardAutoClaim.DISABLED && copy.getTags().equals(reward.getTags()),
                            "Native custom reward packet round trip retains tags and personal claim scope");
                } finally { buffer.release(); }
            }
        }));
        check(total[0] == 1628 && custom.size() == 1443, "All actual outer rewards and custom callbacks are covered");
        results.put("native_personal_outer_rewards", total[0]);
        results.put("native_custom_rewards_nbt_and_packet_roundtripped", custom.size());
        return custom;
    }

    private static void testLegacyProgress(MinecraftServer server, Map<String, Object> results) throws Exception {
        JsonObject config = JsonParser.parseString(Files.readString(Path.of("soqm_probe_config.json"))).getAsJsonObject();
        ServerQuestFile old = new ServerQuestFile(server);
        old.readDataFull(Path.of(config.get("baseline_quests").getAsString()), server.registryAccess());
        TeamData before = new TeamData(MIGRATION, old, "Synthetic personal-reward migration");
        List<Task> tasks = new ArrayList<>();
        List<Reward> rewards = new ArrayList<>();
        old.forAllQuests(q -> {
            if (tasks.size() < 40) for (Task task : q.getTasksAsList()) {
                before.setProgress(task, task.getMaxProgress() > 1 ? task.getMaxProgress() - 1 : 1);
                tasks.add(task);
            }
            q.getRewards().forEach(reward -> {
                check(before.markRewardAsClaimed(FIRST, reward, OLD_TIME), "Nonempty old-world claim fixture");
                rewards.add(reward);
            });
        });
        SNBTCompoundTag original = before.serializeNBT();
        before.saveIfChanged();
        check(readTeam(server, MIGRATION).serializeNBT().equals(original),
                "The existing baseline progress and claims are present in an independent native disk read");
        TeamData after = new TeamData(MIGRATION, ServerQuestFile.INSTANCE);
        after.deserializeNBT(original);
        int partial = 0;
        for (Task oldTask : tasks) {
            Task current = ServerQuestFile.INSTANCE.getTask(oldTask.id);
            check(current != null && before.getProgress(oldTask) == after.getProgress(current), "Task progress survives reward-scope change");
            if (before.getProgress(oldTask) < oldTask.getMaxProgress()) partial++;
        }
        check(partial > 0, "Migration fixture exercises partial task progress");
        for (Reward oldReward : rewards) {
            Reward current = ServerQuestFile.INSTANCE.getReward(oldReward.id);
            check(current != null && !current.isTeamReward(), "Outer reward identity remains intact and becomes personal");
            if (oldReward.isTeamReward()) {
                legacyShared.add(current.id);
                check(!after.isRewardClaimed(FIRST, current) && !after.isRewardClaimed(SECOND, current),
                        "Legacy shared reward intentionally grants each player one new personal claim");
                check(after.markRewardAsClaimed(FIRST, current, NEW_TIME)
                                && after.markRewardAsClaimed(SECOND, current, NEW_TIME + 1),
                        "Both native personal keys are accepted once");
                check(!after.markRewardAsClaimed(FIRST, current, NEW_TIME + 2)
                                && !after.markRewardAsClaimed(SECOND, current, NEW_TIME + 2),
                        "The new personal claims do not repeat");
                check(after.getRewardClaimTime(FIRST, oldReward).orElseThrow().getTime() == OLD_TIME,
                        "Legacy NIL_UUID key and timestamp remain historical");
            } else {
                oldPersonal.add(current.id);
                check(after.isRewardClaimed(FIRST, current) && !after.isRewardClaimed(SECOND, current)
                                && !after.markRewardAsClaimed(FIRST, current, NEW_TIME),
                        "An earlier personal reward stays claimed for its recipient");
            }
        }
        check((legacyShared.size() == 1625 && oldPersonal.equals(List.of(BOOK_ID)))
                        || (legacyShared.isEmpty() && (oldPersonal.size() == 1626 || oldPersonal.size() == 1627) && oldPersonal.contains(BOOK_ID)),
                "Both a legacy shared baseline and an already personal baseline retain every reward");
        after.saveIfChanged();
        migratedSnapshot = readTeam(server, MIGRATION).serializeNBT();
        check(migratedSnapshot.equals(after.serializeNBT()), "Native saveIfChanged persists the full migration fixture");
        for (String key : original.getCompound("claimed_rewards").getAllKeys()) {
            check(migratedSnapshot.getCompound("claimed_rewards").getLong(key) == original.getCompound("claimed_rewards").getLong(key),
                    "Every historical claim timestamp remains unchanged");
        }
        results.put("legacy_shared_rewards_with_one_new_personal_claim_each", legacyShared.size());
        results.put("legacy_personal_book_claim_preserved", true);
        results.put("existing_personal_claims_preserved", oldPersonal.size());
        results.put("legacy_task_progress_preserved", tasks.size());
        results.put("legacy_partial_tasks_preserved", partial);
        results.put("legacy_claim_keys_retained_as_history", original.getCompound("claimed_rewards").size());
    }

    private static void testTeamSwitch(MinecraftServer server, TeamManagerImpl manager, PartyTeam party,
                                       ProbePlayer second, ProbePlayer third, CustomReward reward,
                                       Map<String, Object> results) throws Exception {
        ServerQuestFile file = ServerQuestFile.INSTANCE;
        TeamData partyData = file.getOrCreateTeamData(party);
        partyData.saveIfChanged();
        TeamData originalPersonal = file.getOrCreateTeamData(SECOND);
        check(!readTeam(server, SECOND).isRewardClaimed(SECOND, reward), "Clean pre-leave disk snapshot has no party claim");
        party.leave(SECOND); // PlayerList deliberately has no online connection for this UUID.
        TeamData personal = file.getOrCreateTeamData(SECOND);
        check(personal == originalPersonal && personal.isRewardClaimed(SECOND, reward), "Native offline leave merges only this player's claim");
        check(personal.isRewardClaimed(SECOND, file.getReward(refundPackId)), "A consumed crafting refund also follows its personal owner on leave");
        personal.saveIfChanged();
        TeamData fromDisk = readTeam(server, SECOND);
        check(fromDisk.isRewardClaimed(SECOND, reward) && !fromDisk.isRewardClaimed(FIRST, reward),
                "Post-merge dirty hook persists personal claims, without copying the other player's keys");
        // Replace the in-memory object with an independent disk reader before
        // joining another party. A cache-only transfer cannot satisfy this test.
        file.addData(fromDisk, true);
        PartyTeam destination = manager.createParty(third, "SOQM rewards B");
        TeamData beforeJoin = file.getOrCreateTeamData(destination);
        beforeJoin.saveIfChanged();
        check(!readTeam(server, destination.getId()).isRewardClaimed(SECOND, reward), "Destination is clean on disk before joining");
        destination.join(second);
        TeamData afterJoin = file.getOrCreateTeamData(destination);
        afterJoin.saveIfChanged();
        TeamData destinationDisk = readTeam(server, destination.getId());
        check(destinationDisk.isRewardClaimed(SECOND, reward), "Post-join dirty hook persists the imported personal claim");
        check(destinationDisk.isRewardClaimed(SECOND, file.getReward(refundPackId)), "Joining another party retains the exact refund claim key");
        file.addData(destinationDisk, true);
        complete(destinationDisk, reward);
        second.getInventory().clearContent();
        denyDuplicate(second, destinationDisk, reward);
        denyDuplicate(second, destinationDisk, file.getReward(refundPackId));
        check(inventory(second).isEmpty(), "Changing teams and reloading disk state cannot duplicate a personal reward");
        destinationTeam = destination.getId();
        destinationDisk.saveIfChanged();
        results.put("native_offline_leave_claim_merge_saved_to_disk", true);
        results.put("native_join_claim_merge_saved_to_disk", true);
        results.put("native_team_switch_after_disk_reload_duplicate_rejected", true);
    }

    private static void testRecipeRefunds(MinecraftServer server, PartyTeam party, ProbePlayer first,
                                           ProbePlayer second, List<CustomReward> custom,
                                           Map<String, Object> results) throws Exception {
        JsonObject config = JsonParser.parseString(Files.readString(Path.of("soqm_probe_config.json"))).getAsJsonObject();
        JsonObject authored = JsonParser.parseString(Files.readString(Path.of(config.get("recipe_refunds").getAsString()))).getAsJsonObject();
        int recipeCount = 0;
        for (JsonElement value : authored.getAsJsonArray("refunds")) {
            JsonObject refund = value.getAsJsonObject();
            String output = refund.get("output").getAsString();
            Set<String> expectedIds = new java.util.HashSet<>();
            List<CraftingRecipe> actualRecipes = new ArrayList<>();
            for (JsonElement proof : refund.getAsJsonArray("recipes")) {
                String id = proof.getAsJsonObject().get("id").getAsString();
                var holder = server.getRecipeManager().byKey(ResourceLocation.parse(id)).orElseThrow();
                check(holder.value() instanceof CraftingRecipe, "Refund recipe uses a supported crafting output: " + id);
                CraftingRecipe recipe = (CraftingRecipe) holder.value();
                ItemStack result = recipe.getResultItem(server.registryAccess());
                check(result.getCount() == 1 && BuiltInRegistries.ITEM.getKey(result.getItem()).toString().equals(output),
                        "Native recipe makes exactly the refund's observed output: " + id);
                actualRecipes.add(recipe);
                expectedIds.add(id);
                recipeCount++;
            }
            Set<String> activeIds = new java.util.HashSet<>();
            for (var holder : server.getRecipeManager().getRecipes()) {
                if (!(holder.value() instanceof CraftingRecipe recipe)
                        || !BuiltInRegistries.ITEM.getKey(recipe.getResultItem(server.registryAccess()).getItem()).toString().equals(output)) continue;
                // Erasure/recolour recipes obligatorily consume the same item.
                // They cannot pass ProductionEvidence.isManufacture and are not
                // a cheaper route to the output-only manufacture requirement.
                if (exactIngredientCount(recipe, output) > 0) continue;
                activeIds.add(holder.id().toString());
            }
            check(activeIds.equals(expectedIds), "All live manufacturing alternatives were reviewed: " + output + " " + activeIds);
            if (refund.has("world_upgrade")) {
                JsonObject upgrade = refund.getAsJsonObject("world_upgrade");
                for (JsonElement proof : upgrade.getAsJsonArray("recipes")) {
                    String id = proof.getAsJsonObject().get("id").getAsString();
                    var holder = server.getRecipeManager().byKey(ResourceLocation.parse(id)).orElseThrow();
                    check(holder.value() instanceof CraftingRecipe, "World-upgrade item has a supported recipe: " + id);
                    CraftingRecipe recipe = (CraftingRecipe) holder.value();
                    check(BuiltInRegistries.ITEM.getKey(recipe.getResultItem(server.registryAccess()).getItem()).toString()
                                    .equals(upgrade.get("item").getAsString()),
                            "The reviewed upgrade recipe makes the exact item consumed by the native upgrade hook");
                    actualRecipes.add(recipe);
                    recipeCount++;
                }
            }
            for (JsonElement prizeValue : refund.getAsJsonArray("items")) {
                JsonObject prize = prizeValue.getAsJsonObject();
                int minimum = actualRecipes.stream().mapToInt(recipe -> exactIngredientCount(recipe, item(prize))).min().orElseThrow();
                check(minimum == prize.get("minimum_consumed").getAsInt()
                                && prize.get("count").getAsInt() <= minimum,
                        "Fixed refund is backed by the actual minimum direct ingredient count: " + output + " / " + item(prize));
            }
        }
        check(authored.getAsJsonArray("refunds").size() == 41, "Every reviewed refund has live recipe-cost coverage");
        results.put("native_refund_recipe_costs_checked", recipeCount);

        CustomReward pack = custom.stream().filter(reward -> {
            JsonObject mapping = policy.getAsJsonObject("reward_mappings").getAsJsonObject(reward.getCodeString());
            return mapping.has("refund") && mapping.getAsJsonObject("refund").get("output").getAsString().equals("ae2:drive");
        }).findFirst().orElseThrow();
        CustomReward cube = custom.stream().filter(reward -> {
            JsonObject mapping = policy.getAsJsonObject("reward_mappings").getAsJsonObject(reward.getCodeString());
            return mapping.has("refund") && mapping.has("cube")
                    && mapping.getAsJsonObject("refund").get("output").getAsString().equals("ironfurnaces:netherite_furnace");
        }).findFirst().orElseThrow();
        TeamData claims = ServerQuestFile.INSTANCE.getOrCreateTeamData(party);
        complete(claims, pack); // Includes the old completed-but-unclaimed world case.
        first.getInventory().clearContent();
        first.getInventory().add(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("ae2:drive"))));
        Map<String, Integer> gift = inventory(first);
        int beforeCalls = first.random.calls;
        claims.claimReward(first, pack, false);
        check(!claims.isRewardClaimed(FIRST, pack) && claims.getRewardClaimTime(FIRST, pack).isEmpty()
                        && inventory(first).equals(gift) && first.random.calls == beforeCalls,
                "A completed quest and gifted machine cannot spend or deliver a refund before manufacture");
        check(first.messages.stream().anyMatch(message -> message.contains("Your claim has not been spent")),
                "A premature claim explains the exact manufacture condition without consuming the reward");

        CompoundTag definition = new CompoundTag();
        pack.writeData(definition, server.registryAccess());
        CustomReward unknown = new CustomReward(0x5A7F000000000001L, pack.getQuest());
        unknown.readData(definition, server.registryAccess());
        claims.claimReward(first, unknown, false);
        check(!claims.isRewardClaimed(FIRST, unknown) && inventory(first).equals(gift),
                "An unknown refund mapping fails closed before FTB records a claim");
        CustomReward wrongParent = new CustomReward(pack.id, cube.getQuest());
        wrongParent.readData(definition, server.registryAccess());
        claims.claimReward(first, wrongParent, false);
        check(!claims.isRewardClaimed(FIRST, pack), "A stale parent-quest mapping cannot consume the saved reward ID");

        craftOutput(server, first, "ae2:drive");
        check(ProductionEvidence.hasProduced(second, "ae2:drive"), "The teammate shares actual machine manufacture");
        check(!ProductionEvidence.hasProduced(first, "ae2:engineering_processor"),
                "Using an obtained ingredient in a real machine recipe does not fabricate its own manufacture proof");
        // A damaged native policy cannot silently spend the once-only claim and
        // then substitute a fallback for its promised fixed component.
        Path runtimePath = Path.of("kubejs/config/soqm_rewards.json");
        String originalPolicy = Files.readString(runtimePath);
        var reload = RecipeRefunds.class.getDeclaredMethod("loadNow");
        reload.setAccessible(true);
        try {
            JsonObject damaged = policy.deepCopy();
            damaged.getAsJsonObject("reward_mappings").getAsJsonObject(pack.getCodeString())
                    .getAsJsonObject("refund").getAsJsonArray("items").get(0).getAsJsonObject().addProperty("count", 0);
            Files.writeString(runtimePath, damaged.toString());
            reload.invoke(null);
            claims.claimReward(first, pack, false);
            check(!claims.isRewardClaimed(FIRST, pack), "Invalid refund policy does not consume an otherwise eligible claim");
            CustomReward unaffected = unclaimed(custom, claims, first, poolFor(pack).get("key").getAsString());
            complete(claims, unaffected);
            claimTarget(first, claims, unaffected, poolFor(unaffected), eligible(first, poolFor(unaffected), 6).getFirst(), 6);
        } finally {
            Files.writeString(runtimePath, originalPolicy);
            reload.invoke(null);
        }
        Set<String> beforeRefund = ProductionEvidence.getProducedItems(first);
        JsonObject packPool = poolFor(pack);
        claimTarget(first, claims, pack, packPool, eligible(first, packPool, 6).getFirst(), 6);
        check(inventory(first).getOrDefault("ae2:engineering_processor", 0) == 1,
                "A real ME Drive craft pays its fixed Engineering Processor plus the original random pack");
        check(ProductionEvidence.getProducedItems(first).equals(beforeRefund)
                        && !ProductionEvidence.hasProduced(first, "ae2:engineering_processor"),
                "Refund delivery neither unlocks its ingredient nor creates a production-evidence loop");
        claimTarget(second, claims, pack, packPool, eligible(second, packPool, 6).getFirst(), 6);
        check(inventory(second).getOrDefault("ae2:engineering_processor", 0) == 1,
                "Actual team manufacture allows each teammate their own fixed compensation");
        denyDuplicate(first, claims, pack);
        denyDuplicate(second, claims, pack);
        Map<String, Integer> paid = inventory(first);
        CustomRewardEvent.EVENT.invoker().act(new CustomRewardEvent(pack, first, false));
        check(inventory(first).equals(paid), "An out-of-context repeated custom event cannot pay a second refund or random pack");

        complete(claims, cube);
        craftOutput(server, first, "ironfurnaces:netherite_furnace");
        assertCubeRefund(first, claims, cube);
        assertCubeRefund(second, claims, cube);
        denyDuplicate(first, claims, cube);
        denyDuplicate(second, claims, cube);
        refundPackId = pack.id;
        refundCubeId = cube.id;
        refundTeam = party.getId();
        results.put("native_refund_premature_claim_remains_unclaimed", true);
        results.put("native_refund_unknown_invalid_policy_and_wrong_parent_denied", true);
        results.put("native_refund_fixed_plus_random_two_personal_team_claims", true);
        results.put("native_refund_preserved_cube_and_exact_fixed_quantity", true);
        results.put("native_refund_delivery_does_not_unlock_production", true);
        results.put("native_refund_duplicate_event_without_claim_context_rejected", true);
    }

    private static int exactIngredientCount(CraftingRecipe recipe, String item) {
        var expected = BuiltInRegistries.ITEM.get(ResourceLocation.parse(item));
        int count = 0;
        for (var ingredient : recipe.getIngredients()) {
            ItemStack[] matches = ingredient.getItems();
            if (matches.length > 0 && java.util.Arrays.stream(matches).allMatch(stack -> stack.is(expected))) count++;
        }
        return count;
    }

    private static void assertCubeRefund(ProbePlayer player, TeamData team, CustomReward reward) {
        JsonObject mapping = policy.getAsJsonObject("reward_mappings").getAsJsonObject(reward.getCodeString());
        Map<String, Integer> expected = new HashMap<>();
        expected.put(mapping.get("cube").getAsString(), 1);
        for (JsonElement item : mapping.getAsJsonObject("refund").getAsJsonArray("items")) {
            JsonObject prize = item.getAsJsonObject();
            expected.merge(item(prize), prize.get("count").getAsInt(), Integer::sum);
        }
        player.getInventory().clearContent();
        player.random.script(0.25);
        team.claimReward(player, reward, false);
        check(team.isRewardClaimed(player.getUUID(), reward) && inventory(player).equals(expected) && player.random.calls == 0,
                "A cube milestone delivers exactly its original cube plus fixed materials, with no random pack");
    }

    public static void verifyReload(MinecraftServer server, Map<String, Object> results) throws Exception {
        TeamData restored = readTeam(server, MIGRATION);
        SNBTCompoundTag restoredSnapshot = restored.serializeNBT();
        check(restoredSnapshot.equals(migratedSnapshot), "Legacy and new personal keys survive /reload and independent native disk reading");
        for (long id : legacyShared) {
            Reward reward = ServerQuestFile.INSTANCE.getReward(id);
            check(reward != null && restored.isRewardClaimed(FIRST, reward) && restored.isRewardClaimed(SECOND, reward)
                            && !restored.markRewardAsClaimed(FIRST, reward, NEW_TIME + 100),
                    "Each new personal claim remains consumed after reload");
            check(restoredSnapshot.getCompound("claimed_rewards").getLong(QuestKey.create(Util.NIL_UUID, id).toString()) == OLD_TIME,
                    "Reload preserves historical shared keys");
        }
        for (long id : oldPersonal) {
            Reward reward = ServerQuestFile.INSTANCE.getReward(id);
            check(reward != null && restored.isRewardClaimed(FIRST, reward)
                            && !restored.isRewardClaimed(SECOND, reward)
                            && !restored.markRewardAsClaimed(FIRST, reward, NEW_TIME + 100),
                    "Earlier personal claims remain consumed for their recipient after reload");
        }
        check(restored.isRewardClaimed(FIRST, ServerQuestFile.INSTANCE.getReward(BOOK_ID)), "Personal book remains claimed after reload");
        TeamData switched = readTeam(server, destinationTeam);
        check(switched.isRewardClaimed(SECOND, ServerQuestFile.INSTANCE.getReward(switchedReward)), "Transferred claim survives /reload and native disk reading");
        check(switched.isRewardClaimed(SECOND, ServerQuestFile.INSTANCE.getReward(refundPackId)), "Transferred crafting refund retains its saved personal claim after reload");
        TeamData originalRefunds = readTeam(server, refundTeam);
        check(originalRefunds.isRewardClaimed(FIRST, ServerQuestFile.INSTANCE.getReward(refundPackId))
                        && originalRefunds.isRewardClaimed(FIRST, ServerQuestFile.INSTANCE.getReward(refundCubeId)),
                "Both fixed-plus-random and fixed-plus-cube claims persist across reload");
        ProbePlayer late = player(server, UUID.fromString("3f7a9456-abcd-4a2a-8610-b45ce14f53ba"), "SOQMRefundLate");
        PartyTeam originalParty = (PartyTeam) FTBTeamsAPI.api().getManager().getTeamForPlayerID(FIRST).orElseThrow();
        originalParty.join(late);
        TeamData lateClaims = ServerQuestFile.INSTANCE.getOrCreateTeamData(originalParty);
        assertCubeRefund(late, lateClaims, (CustomReward) ServerQuestFile.INSTANCE.getReward(refundCubeId));
        denyDuplicate(late, lateClaims, ServerQuestFile.INSTANCE.getReward(refundCubeId));
        results.put("native_refund_reload_uses_current_policy_and_late_joiner_claim", true);
        results.put("progressive_personal_claims_reload_and_disk_persistence", true);
    }

    private static ProbePlayer player(MinecraftServer server, UUID id, String name) {
        GameProfile profile = new GameProfile(id, name);
        ProbePlayer player = new ProbePlayer(server, profile);
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        // Native FTB login probes negotiated client channels. An in-memory
        // channel supplies real attributes while advertising no remote client.
        new EmbeddedChannel(connection);
        player.connection = ProductionEvidenceProbe.listener(server, connection, player, profile);
        ((TeamManagerImpl) FTBTeamsAPI.api().getManager()).playerLoggedIn(player, id, name);
        player.getInventory().clearContent();
        return player;
    }

    private static TeamData readTeam(MinecraftServer server, UUID id) throws Exception {
        Path path = server.getWorldPath(ServerQuestFile.FTBQUESTS_DATA).resolve(id + ".snbt");
        check(Files.isRegularFile(path), "Native TeamData file exists: " + id);
        SNBTCompoundTag saved = SNBT.tryRead(path);
        check(saved != null, "Native TeamData file is readable");
        TeamData team = new TeamData(id, ServerQuestFile.INSTANCE);
        team.deserializeNBT(saved);
        return team;
    }

    private static void complete(TeamData team, Reward reward) {
        team.setStarted(reward.getQuest().id, new Date(OLD_TIME));
        team.setCompleted(reward.getQuest().id, new Date(OLD_TIME + 1));
        check(team.isCompleted(reward.getQuest()), "Synthetic team has legitimately claimable completed quest state");
    }

    private static Map<String, Integer> inventory(ServerPlayer player) {
        Map<String, Integer> items = new HashMap<>();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.isEmpty()) items.merge(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), stack.getCount(), Integer::sum);
        }
        return items;
    }

    private static void claimTarget(ProbePlayer player, TeamData team, CustomReward reward, JsonObject pool, JsonObject target, int level) {
        List<JsonObject> entries = eligible(player, pool, level);
        check(entries.contains(target), "Deterministic target is eligible by actual production facts");
        JsonObject rarityWeights = policy.getAsJsonArray("rarity_weights_by_level").get(level).getAsJsonObject();
        List<JsonObject> premium = entries.stream().filter(entry -> !entry.get("rarity").getAsString().equals("common")).toList();
        boolean reserved = policy.has("guaranteed_quality_draws") && policy.get("guaranteed_quality_draws").getAsInt() == 1 && !premium.isEmpty();
        JsonObject first = reserved && !premium.contains(target) ? premium.getFirst() : target;
        int draws = policy.getAsJsonArray("draws_by_level").get(level).getAsInt();
        double[] sequence = new double[draws * 2];
        Map<String, Integer> expected = new HashMap<>();
        for (int draw = 0; draw < draws; draw++) {
            JsonObject chosen = draw == 0 ? first : target;
            List<JsonObject> candidates = draw == 0 && reserved ? premium : entries;
            double[] interval = selectionInterval(candidates, chosen, rarityWeights);
            sequence[draw * 2] = interval[0];
            sequence[draw * 2 + 1] = interval[1];
            expected.merge(item(chosen), chosen.get("count").getAsInt(), Integer::sum);
        }
        JsonObject binding = policy.getAsJsonObject("reward_mappings").getAsJsonObject(reward.getCodeString());
        if (binding.has("refund")) for (JsonElement element : binding.getAsJsonObject("refund").getAsJsonArray("items")) {
            JsonObject prize = element.getAsJsonObject();
            expected.merge(item(prize), prize.get("count").getAsInt(), Integer::sum);
        }
        player.random.script(sequence);
        player.getInventory().clearContent();
        team.claimReward(player, reward, false);
        check(player.random.calls == draws * 2, "Native JS handler made the intended rarity and item draws");
        check(inventory(player).equals(expected),
                "Native delivery matches reserved-quality and ordinary draws, with duplicate quantities combined");
    }

    private static double[] selectionInterval(List<JsonObject> entries, JsonObject target, JsonObject rarityWeights) {
        String rarity = target.get("rarity").getAsString();
        double groupTotal = 0, beforeGroup = 0;
        for (String candidate : RARITIES) {
            if (entries.stream().anyMatch(e -> e.get("rarity").getAsString().equals(candidate))) {
                groupTotal += rarityWeights.get(candidate).getAsDouble();
            }
        }
        // Compute the target interval directly. Different candidate sets for the
        // reserved first draw and later draws must not silently use the same RNG.
        for (String candidate : RARITIES) {
            if (candidate.equals(rarity)) break;
            if (entries.stream().anyMatch(e -> e.get("rarity").getAsString().equals(candidate))) beforeGroup += rarityWeights.get(candidate).getAsDouble();
        }
        double totalEntries = 0, beforeEntry = 0;
        boolean seen = false;
        for (JsonObject entry : entries) if (entry.get("rarity").getAsString().equals(rarity)) {
            double weight = entry.get("weight").getAsDouble();
            if (entry == target) seen = true;
            if (!seen) beforeEntry += weight;
            totalEntries += weight;
        }
        check(seen && totalEntries > 0 && groupTotal > 0, "Target has a positive probability interval");
        double groupUnit = (beforeGroup + rarityWeights.get(rarity).getAsDouble() / 2) / groupTotal;
        double itemUnit = (beforeEntry + target.get("weight").getAsDouble() / 2) / totalEntries;
        return new double[]{groupUnit, itemUnit};
    }

    private static void denyDuplicate(ProbePlayer player, TeamData team, Reward reward) {
        Map<String, Integer> before = inventory(player);
        int calls = player.random.calls;
        team.claimReward(player, reward, false);
        check(inventory(player).equals(before) && player.random.calls == calls, "Native duplicate claim delivers nothing and performs no new roll");
    }

    private static void verifyPoolCommand(MinecraftServer server, ProbePlayer player, JsonObject pool, int level) {
        Map<String, Integer> before = inventory(player);
        Set<String> produced = ProductionEvidence.getProducedItems(player);
        int calls = player.random.calls;
        player.messages.clear();
        server.getCommands().performPrefixedCommand(player.createCommandSourceStack(), "soqm rewards pool " + pool.get("key").getAsString());
        List<JsonObject> eligible = eligible(player, pool, level);
        check(!eligible.isEmpty(), "Reviewed theme has its own nonempty current pool");
        check(player.messages.stream().anyMatch(line -> line.startsWith(pool.getAsJsonObject("title").get("en_us").getAsString() + " — level " + level + ",")),
                "Actual command loaded the authored pool title and current production level");
        long displayed = player.messages.stream().filter(line -> line.matches(".* — (?:<[0-9.]+|[0-9.]+)%(?:; first: (?:<[0-9.]+|[0-9.]+)%)?$")).count();
        check(displayed == eligible.size(), "Actual command exposes exactly the authored eligible entries, not emergency fallback supplies");
        long locked = player.messages.stream().filter(line -> line.contains(" — locked: ")).count();
        check(displayed + locked == pool.getAsJsonArray("entries").size(), "The complete locked and unlocked catalogue survived runtime validation");
        check(player.messages.stream().noneMatch(line -> line.startsWith("Fallback pool:")), "Reviewed policy does not silently enter fallback");
        check(inventory(player).equals(before) && ProductionEvidence.getProducedItems(player).equals(produced) && player.random.calls == calls,
                "Read-only pool command preserves inventory, production and RNG");
    }

    private static void verifyStatusCommand(MinecraftServer server, ProbePlayer player) {
        player.messages.clear();
        server.getCommands().performPrefixedCommand(player.createCommandSourceStack(), "soqm rewards status");
        for (JsonElement element : policy.getAsJsonArray("levels")) {
            JsonObject milestone = element.getAsJsonObject();
            String text = milestone.get("level").getAsInt() + ": " + milestone.getAsJsonObject("name").get("en_us").getAsString();
            check(player.messages.stream().anyMatch(line -> line.endsWith(text)), "Actual status command shows the authored production milestone name");
        }
    }

    private static List<JsonObject> eligible(ServerPlayer player, JsonObject pool, int level) {
        Set<String> produced = ProductionEvidence.getProducedItems(player);
        List<JsonObject> result = new ArrayList<>();
        for (JsonElement element : pool.getAsJsonArray("entries")) {
            JsonObject entry = element.getAsJsonObject();
            if (entry.has("min_level") && entry.get("min_level").getAsInt() > level) continue;
            boolean allowed = true;
            if (entry.has("requires_craft")) for (JsonElement requirement : entry.getAsJsonArray("requires_craft")) {
                if (!produced.contains(requirement.getAsString())) { allowed = false; break; }
            }
            if (allowed) result.add(entry);
        }
        return result;
    }

    private static JsonObject poolFor(Reward reward) {
        String key = policy.getAsJsonObject("reward_mappings").getAsJsonObject(reward.getCodeString()).get("pool").getAsString();
        for (JsonElement element : policy.getAsJsonArray("pools")) if (element.getAsJsonObject().get("key").getAsString().equals(key)) return element.getAsJsonObject();
        throw new AssertionError("Missing actual pool " + key);
    }

    private static CustomReward unclaimed(List<CustomReward> rewards, TeamData data, ServerPlayer player, String pool) {
        return rewards.stream().filter(reward -> poolFor(reward).get("key").getAsString().equals(pool)
                && !reward.hasTag(RecipeRefunds.TAG)
                && !data.isRewardClaimed(player.getUUID(), reward)).findFirst().orElseThrow();
    }

    private static JsonObject milestone(int level) {
        for (JsonElement element : policy.getAsJsonArray("levels")) if (element.getAsJsonObject().get("level").getAsInt() == level) return element.getAsJsonObject();
        throw new AssertionError("Missing level " + level);
    }

    private static ResourceLocation craftingRecipe(MinecraftServer server, String output) {
        return server.getRecipeManager().getRecipes().stream()
                .filter(holder -> holder.value() instanceof CraftingRecipe
                        && BuiltInRegistries.ITEM.getKey(holder.value().getResultItem(server.registryAccess()).getItem()).toString().equals(output))
                .map(holder -> holder.id()).sorted().findFirst().orElse(null);
    }

    private static void craftOutput(MinecraftServer server, ServerPlayer player, String output) throws Exception {
        ResourceLocation recipe = craftingRecipe(server, output);
        check(recipe != null, "Active crafting recipe exists for fixture output " + output);
        ProductionEvidenceProbe.craft(player, recipe.toString());
        check(ProductionEvidence.hasProduced(player, output), "Real native crafting granted production evidence for " + output);
    }

    private static String item(JsonObject entry) { return entry.get("item").getAsString(); }
    private static boolean requiresOwnProduct(JsonObject entry) {
        if (!entry.has("requires_craft")) return false;
        for (JsonElement required : entry.getAsJsonArray("requires_craft")) if (required.getAsString().equals(item(entry))) return true;
        return false;
    }
    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }

    public static final class ProbeRandom extends LegacyRandomSource {
        private double[] sequence = {0.25, 0.25};
        private int calls;
        ProbeRandom() { super(661199L); }
        void script(double... values) { sequence = values.clone(); calls = 0; }
        @Override public double nextDouble() { return sequence[calls++ % sequence.length]; }
    }

    public static final class ProbePlayer extends ServerPlayer {
        private final ProbeRandom random = new ProbeRandom();
        private final List<String> messages = new ArrayList<>();
        ProbePlayer(MinecraftServer server, GameProfile profile) {
            super(server, server.overworld(), profile, ClientInformation.createDefault());
            setPos(12.5, 200, 12.5);
        }
        @Override public RandomSource getRandom() { return random == null ? super.getRandom() : random; }
        @Override public void sendSystemMessage(Component message) { if (messages != null) messages.add(message.getString()); }
    }
}
