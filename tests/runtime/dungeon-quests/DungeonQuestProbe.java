package soqm.validation.dungeonquests;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.block.BlockGoldPortal;
import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.item.BaseItemKey;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonDesigner.RoomType;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbquests.events.QuestProgressEventData;
import dev.ftb.mods.ftbquests.net.ClaimRewardMessage;
import dev.ftb.mods.ftbquests.net.SubmitTaskMessage;
import dev.ftb.mods.ftbquests.quest.BaseQuestFile;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.CustomReward;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardAutoClaim;
import dev.ftb.mods.ftbquests.quest.task.AdvancementTask;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.util.FTBQuestsInventoryListener;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import dev.ftb.mods.ftbteams.data.TeamManagerImpl;
import io.netty.channel.embedded.EmbeddedChannel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import vazkii.patchouli.common.item.ItemModBook;

/** Native FTB/DD integration gate. Never included in the released mod pack. */
@Mod("soqm_dungeon_quest_probe")
public final class DungeonQuestProbe {
    private static final long QUEST_BASE = 0x514E110000000000L;
    private static final long TASK_BASE = 0x514E120000000000L;
    private static final long REWARD_BASE = 0x514E130000000000L;
    private static final long ROOT_QUEST = 0x514D010000000002L;
    private static final UUID FIRST = UUID.fromString("c882568a-4218-40e2-836e-a193269dce01");
    private static final UUID SECOND = UUID.fromString("c882568a-4218-40e2-836e-a193269dce02");
    private static final UUID MIGRATION = UUID.fromString("c882568a-4218-40e2-836e-a193269dce03");
    private static final Map<Integer, String> ADVANCEMENTS = Map.of(
            4, "activate_basic_key", 5, "enter_basic_dungeon", 7, "find_key_charger", 8, "find_bell_1",
            9, "find_advanced_key", 10, "find_crown", 11, "enter_advanced_dungeon", 12, "all_trophies");
    private final Map<String, Object> results = new LinkedHashMap<>();
    private int assertions;
    private boolean awaitingReload, finished;
    private Players players;
    private TeamData team;
    private SNBTCompoundTag historicalSnapshot, newClaimsSnapshot;

    public DungeonQuestProbe() { NeoForge.EVENT_BUS.addListener(this::tick); }

    private void tick(ServerTickEvent.Post event) {
        if (finished || event.getServer().getTickCount() < 40) return;
        MinecraftServer server = event.getServer();
        try {
            if (!awaitingReload) {
                definitions(server);
                migration(server);
                players = new Players(server);
                team = ServerQuestFile.INSTANCE.getOrCreateTeamData(players.party);
                goals(server);
                rewards(server);
                players.first.getAdvancements().save();
                players.second.getAdvancements().save();
                team.markDirty(); team.saveIfChanged();
                newClaimsSnapshot = readTeam(server, team.getTeamId()).serializeNBT();
                check(newClaimsSnapshot.equals(team.serializeNBT()), "New branch claims are present in independent native disk read");
                awaitingReload = true;
                write("awaiting_reload", false, null);
            } else if (Files.exists(Path.of("soqm_probe_reload_complete"))) {
                reload(server);
                players.close();
                finished = true;
                write("finished", true, null);
            }
        } catch (Throwable failure) {
            failure.printStackTrace();
            if (players != null) try { players.close(); } catch (Exception cleanup) { failure.addSuppressed(cleanup); }
            finished = true;
            Throwable cause = failure;
            while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
            try { write("finished", false, cause.toString()); } catch (Exception output) { output.printStackTrace(); }
        }
    }

    private void definitions(MinecraftServer server) {
        check(ServerQuestFile.INSTANCE != null && TeamManagerImpl.INSTANCE != null, "Native FTB quest/team data loaded");
        int taskCount = 0;
        for (int i = 1; i <= 12; i++) {
            Quest quest = quest(i);
            check(quest.isOptional(), "DD quest is explicitly optional: " + i);
            check(quest.getTasksAsList().size() == (i == 2 ? 2 : 1), "Native task count matches the additive contract");
            check(quest.getTasksAsList().contains(task(i)), "Stable primary task belongs to its intended quest");
            taskCount += quest.getTasksAsList().size();
            Set<Long> expected = switch (i) {
                case 1 -> Set.of(ROOT_QUEST);
                case 6 -> Set.of(QUEST_BASE + 3);
                case 7, 8, 9, 10, 12 -> Set.of(QUEST_BASE + 5);
                case 11 -> Set.of(QUEST_BASE + 9, QUEST_BASE + 10);
                default -> Set.of(QUEST_BASE + i - 1);
            };
            check(quest.streamDependencies().map(q -> q.id).collect(Collectors.toSet()).equals(expected),
                    "Native prerequisite graph matches the branch contract: " + i);
            Reward reward = reward(i);
            check(reward instanceof CustomReward && quest.getRewards().size() == 1 && quest.getRewards().contains(reward),
                    "Each new quest retains the existing native custom-reward path");
            check(!reward.isTeamReward() && reward.getAutoClaimType() == RewardAutoClaim.DISABLED
                    && reward.hasTag("soqm:progressive_reward") && reward.hasTag("soqm:reward_pool:expedition"),
                    "Each expedition reward is manual and personal");
            if (ADVANCEMENTS.containsKey(i)) {
                check(task(i) instanceof AdvancementTask, "Native advancement task type loaded: " + i);
                CompoundTag definition = new CompoundTag();
                task(i).writeData(definition, server.registryAccess());
                check(definition.getString("advancement").equals(advId(ADVANCEMENTS.get(i)).toString())
                        && definition.getString("criterion").isEmpty(), "Task requires the complete intended native advancement");
                advancement(server, ADVANCEMENTS.get(i));
            } else {
                for (Task task : quest.getTasksAsList()) check(task instanceof ItemTask item && !item.consumesResources()
                        && !item.isOnlyFromCrafting(), "Native acquisition task neither consumes items nor claims crafting proof");
            }
        }
        check(taskCount == 13 && quest(2).getTasksAsList().contains(task(13)) && task(13).getMaxProgress() == 2,
                "Thirteen new tasks include the two-gilded-block requirement");
        ServerQuestFile.INSTANCE.forAllQuests(q -> {
            if (isNewQuest(q.id)) return;
            check(q.streamDependencies().noneMatch(dependency -> isNewQuest(dependency.id)),
                    "Existing campaign quests do not depend on the optional DD branch");
        });
        results.put("native_definitions", Map.of("optional_quests", 12, "tasks", 13, "personal_rewards", 12,
                "native_advancements_resolved", ADVANCEMENTS.size(), "old_campaign_has_no_new_dependencies", true));
    }

    private void migration(MinecraftServer server) throws Exception {
        JsonObject config = JsonParser.parseString(Files.readString(Path.of("soqm_probe_config.json"))).getAsJsonObject();
        ServerQuestFile old = new ServerQuestFile(server);
        old.readDataFull(Path.of(config.get("baseline_quests").getAsString()), server.registryAccess());
        List<Quest> prior = new ArrayList<>();
        old.forAllQuests(prior::add);
        check(prior.size() > 100 && old.getQuest(QUEST_BASE + 1) == null, "Migration baseline is the real pre-branch questbook");
        TeamData before = new TeamData(MIGRATION, old, "Existing DD upgrade fixture");
        List<Task> progressed = new ArrayList<>();
        List<Reward> claimed = new ArrayList<>();
        int retainedTasks = 0, retainedRewards = 0, partial = 0, completed = 0;
        for (int i = 0; i < prior.size(); i++) {
            Quest oldQuest = prior.get(i);
            check(ServerQuestFile.INSTANCE.getQuest(oldQuest.id) != null, "Every preexisting quest ID survives the additive branch");
            for (Task task : oldQuest.getTasksAsList()) {
                check(ServerQuestFile.INSTANCE.getTask(task.id) != null, "Every preexisting task ID is retained");
                retainedTasks++;
                if (i < 24) {
                    before.setProgress(task, task.getMaxProgress()); progressed.add(task);
                } else if (partial < 40 && task.getMaxProgress() > 1) {
                    before.setProgress(task, Math.min(5, task.getMaxProgress() - 1)); progressed.add(task); partial++;
                }
            }
            if (i < 24) {
                before.setStarted(oldQuest.id, new Date(1_000_000L + i));
                before.setCompleted(oldQuest.id, new Date(1_001_000L + i));
                completed++;
            }
            for (Reward reward : oldQuest.getRewards()) {
                check(ServerQuestFile.INSTANCE.getReward(reward.id) != null, "Every preexisting reward ID is retained");
                retainedRewards++;
                if (i < 24) {
                    check(before.markRewardAsClaimed(FIRST, reward, 2_000_000L + claimed.size()), "Historical personal claim recorded");
                    claimed.add(reward);
                }
            }
        }
        check(partial > 0 && !claimed.isEmpty(), "Baseline fixture contains partial progress and real claim timestamps");
        SNBTCompoundTag original = before.serializeNBT();
        TeamData imported = new TeamData(MIGRATION, ServerQuestFile.INSTANCE);
        imported.deserializeNBT(original);
        check(original.equals(imported.serializeNBT()), "Additive definitions preserve the complete historical native snapshot");
        for (Task task : progressed) check(imported.getProgress(ServerQuestFile.INSTANCE.getTask(task.id)) == before.getProgress(task),
                "Historical task progress survives by persistent ID");
        for (Reward reward : claimed) check(imported.getRewardClaimTime(FIRST, ServerQuestFile.INSTANCE.getReward(reward.id))
                .equals(before.getRewardClaimTime(FIRST, reward)), "Historical reward claim timestamp survives exactly");
        assertNewBranchEmpty(imported);
        imported.markDirty(); imported.saveIfChanged();
        historicalSnapshot = readTeam(server, MIGRATION).serializeNBT();
        check(original.equals(historicalSnapshot), "Independent native disk read retains the migrated snapshot");
        results.put("pre_branch_migration", Map.of("old_quests_retained", prior.size(), "old_tasks_retained", retainedTasks,
                "old_rewards_retained", retainedRewards, "completed_quests", completed, "partial_tasks", partial,
                "historical_personal_claims", claimed.size(), "all_new_quests_initially_empty", true));
    }

    private void goals(MinecraftServer server) throws Exception {
        ServerPlayer player = players.first;
        assertNewBranchEmpty(team);
        ListTag emptyInventory = inventory(player);
        claim(player, reward(1));
        check(!team.isRewardClaimed(FIRST, reward(1)) && inventory(player).equals(emptyInventory),
                "Native reward request cannot claim an unfinished DD quest");

        ItemStack otherBook = ItemModBook.forBook(ResourceLocation.parse("botania:lexicon"));
        check(!((ItemTask) task(1)).test(otherBook), "Another Patchouli book does not satisfy the DD guide task");
        put(player, otherBook); detect(player);
        check(team.getProgress(task(1)) == 0, "Native inventory detection rejects a different guide book");
        var guideHolder = server.getRecipeManager().byKey(ResourceLocation.parse("dimdungeons:recipe_item_guidebook_2"));
        check(guideHolder.isPresent() && guideHolder.get().value() instanceof CraftingRecipe,
                "Pinned DD alternate guide recipe is actually registered as a native crafting recipe");
        CraftingRecipe guideRecipe = (CraftingRecipe) guideHolder.orElseThrow().value();
        CraftingInput guideInput = CraftingInput.of(2, 1,
                List.of(new ItemStack(Items.BOOK), new ItemStack(Items.CHISELED_STONE_BRICKS)));
        check(guideRecipe.matches(guideInput, player.level()), "Native guide recipe matches its survival ingredients");
        ItemStack guide = guideRecipe.assemble(guideInput, server.registryAccess());
        check(ItemStack.isSameItemSameComponents(guide, ItemModBook.forBook(ResourceLocation.parse("dimdungeons:guide_book"))),
                "Native guide recipe produces the exact Patchouli DD book component");
        guide.set(DataComponents.CUSTOM_NAME, Component.literal("Reader's dungeon guide"));
        check(((ItemTask) task(1)).test(guide), "Fuzzy component matching accepts the correct guide with an extra name");
        put(player, guide); detectUnchanged(player);
        check(!team.isCompleted(task(1)), "Native detection defers completion until the preexisting root prerequisite is completed");
        // Flexible progression may record inventory progress before dependencies.
        // Seed only the preexisting root through its native event so FTB can
        // reconcile that legitimate progress through checkForDependantCompletion.
        Quest root = ServerQuestFile.INSTANCE.getQuest(ROOT_QUEST);
        check(root != null, "Existing branch prerequisite resolves");
        root.onStarted(new QuestProgressEventData<>(new Date(3_000_000L), team, root, team.getOnlineMembers(), List.of()));
        root.onCompleted(new QuestProgressEventData<>(new Date(3_000_001L), team, root, team.getOnlineMembers(), List.of()));
        check(team.areDependenciesComplete(quest(1)), "Native root completion unlocks the optional branch");
        detectUnchanged(player);
        completed(1);

        put(player, item("block_portal_keyhole", 1), item("block_gilded_portal", 1));
        detectUnchanged(player);
        check(team.isCompleted(task(2)) && !team.isCompleted(quest(2)), "One gilded block cannot satisfy the two-block preparation goal");
        player.getInventory().setItem(1, item("block_gilded_portal", 2)); detectUnchanged(player); completed(2);
        ItemStack key = item("item_portal_key", 1);
        put(player, key); detectUnchanged(player); completed(3);
        triggerInventory(player, key);
        submit(player, task(4));
        check(!done(player, "activate_basic_key") && !team.isCompleted(task(4)),
                "Holding a blank key cannot satisfy the native activation criterion");
        BaseItemKey keyItem = (BaseItemKey) key.getItem();
        keyItem.performActivationRitual(player, key, player.level(), player.blockPosition());
        check(keyItem.isActivated(key) && done(player, "activate_basic_key"), "Real DD activation ritual activates the key and grants its native criterion");
        ListTag activatedInventory = inventory(player);
        submit(player, task(4)); completed(4);
        check(inventory(player).equals(activatedInventory), "Quest detection does not consume or rewrite the activated key");

        // A dimension change into DD is entry evidence, not a dungeon-clear test.
        submit(player, task(5));
        check(!team.isCompleted(task(5)), "Merely activating a key is not dungeon entry");
        teleportNative(server, player, DungeonType.BASIC, new BlockPos(64, 80, 64));
        check(player.level().dimension().equals(DimDungeons.DUNGEON_DIMENSION) && done(player, "enter_basic_dungeon"),
                "Real native portal teleport grants the DD dimension-entry advancement");
        check(!done(player, "enter_advanced_dungeon"), "Basic destination cannot grant advanced entry");
        submit(player, task(5)); completed(5);
        put(player, item("item_homeward_pearl", 1)); detectUnchanged(player); completed(6);

        for (String charger : List.of("block_key_charger", "block_key_charger_used", "block_key_charger_damaged")) {
            revoke(player, "find_key_charger");
            check(!((AdvancementTask) task(7)).canSubmit(team, player), "Absent charger criterion is not satisfied");
            ItemStack stack = item(charger, 1);
            put(player, stack); triggerInventory(player, stack); submitUnchanged(player, task(7));
            check(done(player, "find_key_charger") && ((AdvancementTask) task(7)).canSubmit(team, player),
                    "Native charger advancement accepts this wear variant: " + charger);
        }
        completed(7);
        ItemStack bell = item("item_secret_bell", 1);
        put(player, bell); triggerInventory(player, bell); submitUnchanged(player, task(8)); completed(8);
        ItemStack advanced = item("item_blank_advanced_key", 1);
        put(player, advanced); triggerInventory(player, advanced); submitUnchanged(player, task(9)); completed(9);
        ItemStack crown = item("block_portal_crown", 1);
        put(player, crown); triggerInventory(player, crown); submitUnchanged(player, task(10));
        check(!done(player, "find_crown") && !team.isCompleted(task(10)), "One crown cannot grant the native two-crown objective");
        crown.setCount(2); triggerInventory(player, crown); submitUnchanged(player, task(10)); completed(10);
        submit(player, task(11));
        check(!team.isCompleted(task(11)), "Advanced key and crowns alone do not grant advanced dungeon entry");
        teleportNative(server, player, DungeonType.ADVANCED, new BlockPos(96, 80, 64));
        check(done(player, "enter_advanced_dungeon"), "Real DD teleport code recognizes an ADVANCED destination room");
        submit(player, task(11)); completed(11);

        for (int i = 1; i <= 8; i++) {
            ItemStack trophy = item("item_trophy_" + i, 1);
            put(player, trophy); triggerInventory(player, trophy); submitUnchanged(player, task(12));
            check(done(player, "all_trophies") == (i == 8), "Whole trophy advancement requires all eight types over time");
            check(team.isCompleted(task(12)) == (i == 8), "FTB trophy task follows the complete native advancement");
        }
        completed(12);
        for (int i = 1; i <= 12; i++) check(!team.isRewardClaimed(FIRST, reward(i)) && !team.isRewardClaimed(SECOND, reward(i)),
                "Native completion does not automatically spend either player's reward claim");
        results.put("native_goals", Map.of("all_quests_completed_via_native_detection", 12,
                "guide_component_filter", true, "native_guide_recipe_matches_and_assembles", true,
                "blank_key_is_not_activation", true, "native_key_activation", true,
                "native_basic_and_advanced_teleports", true, "charger_variants", 3, "one_crown_rejected", true,
                "trophy_types_acquired_over_time", 8, "no_quest_item_consumption", true));
    }

    private void rewards(MinecraftServer server) throws Exception {
        JsonObject policy = JsonParser.parseString(Files.readString(Path.of("kubejs/config/soqm_rewards.json"))).getAsJsonObject();
        JsonObject mappings = policy.getAsJsonObject("reward_mappings");
        String expeditionTitle = null;
        for (var entry : policy.getAsJsonArray("pools")) {
            JsonObject pool = entry.getAsJsonObject();
            if (pool.get("key").getAsString().equals("expedition")) expeditionTitle = pool.getAsJsonObject("title").get("en_us").getAsString();
        }
        check(expeditionTitle != null, "Expedition pool has a live policy title");
        int eligible = 0;
        for (ServerPlayer player : List.of(players.first, players.second)) {
            List<String> chat = players.chat.get(player.getUUID());
            chat.clear();
            ListTag before = inventory(player);
            check(server.getCommands().getDispatcher().execute("soqm rewards pool expedition", player.createCommandSourceStack()) == 1,
                    "Native compiled Expedition pool command succeeds for each actual claim recipient");
            String title = expeditionTitle;
            check(chat.stream().anyMatch(line -> line.startsWith(title + " — level ")),
                    "Production command resolves the requested Expedition pool");
            check(chat.stream().noneMatch(line -> line.startsWith("Fallback pool:")),
                    "Production pool inspection does not silently select a fallback");
            int available = (int) chat.stream().filter(line -> line.contains(" × ") && line.contains("%")) .count();
            check(available > 0, "Compiled Expedition policy exposes registered and eligible native prizes");
            check(inventory(player).equals(before), "Production pool inspection is read-only");
            eligible += available;
        }
        results.put("native_expedition_selection", Map.of("recipient_pool_commands", 2,
                "eligible_entry_lines", eligible, "silent_pool_fallback", false));
        for (int i = 1; i <= 12; i++) {
            Reward reward = reward(i);
            JsonObject mapping = mappings.getAsJsonObject(reward.getCodeString());
            check(mapping != null && mapping.get("pool").getAsString().equals("expedition")
                    && mapping.get("quest_id").getAsString().equals(quest(i).getCodeString()),
                    "Live custom-reward mapping points at the existing expedition pool and intended quest");
            players.first.getInventory().clearContent();
            players.second.getInventory().clearContent();
            claim(players.first, reward);
            check(team.isRewardClaimed(FIRST, reward) && !team.isRewardClaimed(SECOND, reward)
                    && !players.first.getInventory().isEmpty(), "First native request delivers an actual prize without consuming teammate entitlement");
            ListTag firstPrize = inventory(players.first);
            var firstTime = team.getRewardClaimTime(FIRST, reward);
            claim(players.first, reward);
            check(inventory(players.first).equals(firstPrize) && team.getRewardClaimTime(FIRST, reward).equals(firstTime),
                    "Duplicate first-player native request neither rerolls items nor changes the claim timestamp");
            claim(players.second, reward);
            check(team.isRewardClaimed(SECOND, reward) && !players.second.getInventory().isEmpty(),
                    "Second party member receives their personal expedition reward");
            ListTag secondPrize = inventory(players.second);
            var secondTime = team.getRewardClaimTime(SECOND, reward);
            claim(players.second, reward);
            check(inventory(players.second).equals(secondPrize) && team.getRewardClaimTime(SECOND, reward).equals(secondTime),
                    "Duplicate teammate native request is rejected");
        }
        results.put("native_rewards", Map.of("new_reward_mappings", 12, "actual_personal_claims", 24,
                "duplicate_requests_rejected", 24, "premature_request_rejected", true));
    }

    private void reload(MinecraftServer server) throws Exception {
        TeamData history = readTeam(server, MIGRATION);
        check(history.serializeNBT().equals(historicalSnapshot), "Preexisting progress and claim timestamps survive disk and resource reload");
        assertNewBranchEmpty(history);
        TeamData saved = readTeam(server, team.getTeamId());
        check(saved.serializeNBT().equals(newClaimsSnapshot), "New quest completions and both players' claims survive disk and resource reload");
        Map<UUID, TeamData> nativeTeams = field(BaseQuestFile.class, ServerQuestFile.INSTANCE, "teamDataMap");
        nativeTeams.put(saved.getTeamId(), saved);
        team = saved;
        for (int i = 1; i <= 12; i++) {
            completed(i);
            for (ServerPlayer player : List.of(players.first, players.second)) {
                ListTag inventory = inventory(player);
                var stamp = team.getRewardClaimTime(player.getUUID(), reward(i));
                check(stamp.isPresent(), "Saved personal claim has a timestamp");
                claim(player, reward(i));
                check(inventory(player).equals(inventory) && team.getRewardClaimTime(player.getUUID(), reward(i)).equals(stamp),
                        "Native request using independently loaded TeamData cannot duplicate a reward");
            }
        }
        players.first.getAdvancements().reload(server.getAdvancements());
        Path path = server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).resolve(FIRST + ".json");
        check(Files.isRegularFile(path), "Native player advancement data was saved to disk");
        PlayerAdvancements restored = new PlayerAdvancements(server.getFixerUpper(), server.getPlayerList(),
                server.getAdvancements(), path, players.first);
        try {
            for (String name : ADVANCEMENTS.values()) check(restored.getOrStartProgress(advancement(server, name)).isDone(),
                    "Independent native advancement reader restores earned DD objective: " + name);
        } finally { restored.stopListening(); }
        results.put("native_persistence", Map.of("actual_resource_reload", true, "independent_team_disk_reads", true,
                "historical_snapshot_unchanged", true, "new_personal_claims_preserved", 24,
                "post_reload_duplicate_requests_rejected", 24, "advancements_restored_from_disk", 8));
    }

    private void teleportNative(MinecraftServer server, ServerPlayer player, DungeonType type, BlockPos destination) throws Exception {
        ServerLevel level = server.getLevel(DimDungeons.DUNGEON_DIMENSION);
        check(level != null, "Native DD dimension is present");
        level.getChunkAt(destination);
        for (int x = -2; x <= 2; x++) for (int z = -2; z <= 2; z++) level.setBlock(destination.offset(x, -1, z), Blocks.STONE.defaultBlockState(), 2);
        ConcurrentHashMap<ChunkPos, DungeonRoom> rooms = field(DungeonData.class, DungeonData.get(level), "roomMap");
        ChunkPos chunk = new ChunkPos(destination);
        DungeonRoom previous = rooms.get(chunk);
        DungeonRoom fixture = new DungeonRoom();
        fixture.dungeonType = type; fixture.roomType = RoomType.ENTRANCE; fixture.theme = 0;
        fixture.rotation = Rotation.NONE; fixture.structure = "dimdungeons:entrance1";
        rooms.put(chunk, fixture);
        try {
            BlockGoldPortal portal = (BlockGoldPortal) BuiltInRegistries.BLOCK.get(ResourceLocation.parse("dimdungeons:block_gold_portal"));
            Method teleport = BlockGoldPortal.class.getDeclaredMethod("actuallyPerformTeleport", ServerPlayer.class,
                    ServerLevel.class, double.class, double.class, double.class, float.class);
            teleport.setAccessible(true);
            check(teleport.invoke(portal, player, level, destination.getX() + 0.5, (double) destination.getY(),
                    destination.getZ() - 0.5, 0.0F) == player, "Real DD portal routine returns the original player");
        } finally {
            if (previous == null) rooms.remove(chunk); else rooms.put(chunk, previous);
        }
    }

    private void assertNewBranchEmpty(TeamData data) {
        for (int i = 1; i <= 12; i++) {
            check(!data.isCompleted(quest(i)), "New optional quest does not inherit existing completion");
            for (Task task : quest(i).getTasksAsList()) check(data.getProgress(task) == 0, "New task starts without unrelated old progress");
            check(!data.isRewardClaimed(FIRST, reward(i)) && !data.isRewardClaimed(SECOND, reward(i)), "New personal reward starts unclaimed");
        }
    }
    private void completed(int index) { check(team.isCompleted(quest(index)), "Native progression completes intended quest " + index); }
    private Quest quest(int index) {
        Quest quest = ServerQuestFile.INSTANCE.getQuest(QUEST_BASE + index);
        check(quest != null, "Native quest ID resolves: " + index); return quest;
    }
    private Task task(int index) {
        Task task = ServerQuestFile.INSTANCE.getTask(TASK_BASE + index);
        check(task != null, "Native task ID resolves: " + index); return task;
    }
    private Reward reward(int index) {
        Reward reward = ServerQuestFile.INSTANCE.getReward(REWARD_BASE + index);
        check(reward != null, "Native reward ID resolves: " + index); return reward;
    }
    private static boolean isNewQuest(long id) { return id > QUEST_BASE && id <= QUEST_BASE + 12; }
    private static ResourceLocation advId(String name) { return ResourceLocation.parse("dimdungeons:dungeons/" + name); }
    private AdvancementHolder advancement(MinecraftServer server, String name) {
        AdvancementHolder holder = server.getAdvancements().get(advId(name));
        check(holder != null, "Pinned DD advancement is actually loaded: " + name); return holder;
    }
    private boolean done(ServerPlayer player, String name) {
        return player.getAdvancements().getOrStartProgress(advancement(player.server, name)).isDone();
    }
    private void revoke(ServerPlayer player, String name) {
        AdvancementHolder holder = advancement(player.server, name);
        for (String criterion : holder.value().criteria().keySet()) player.getAdvancements().revoke(holder, criterion);
    }
    private void triggerInventory(ServerPlayer player, ItemStack changed) {
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), changed);
    }
    private void detect(ServerPlayer player) { FTBQuestsInventoryListener.detect(player, ItemStack.EMPTY, 0L); }
    private void detectUnchanged(ServerPlayer player) {
        ListTag before = inventory(player); detect(player);
        check(inventory(player).equals(before), "Native inventory task detection preserves all items and components");
    }
    private void submit(ServerPlayer player, Task task) { SubmitTaskMessage.handle(new SubmitTaskMessage(task.id), context(player)); }
    private void submitUnchanged(ServerPlayer player, Task task) {
        ListTag before = inventory(player); submit(player, task);
        check(inventory(player).equals(before), "Native task submission does not consume or modify dungeon items");
    }
    private void claim(ServerPlayer player, Reward reward) {
        ClaimRewardMessage.handle(new ClaimRewardMessage(reward.id, false), context(player));
    }
    private static NetworkManager.PacketContext context(ServerPlayer player) {
        return new NetworkManager.PacketContext() {
            @Override public Player getPlayer() { return player; }
            @Override public void queue(Runnable task) { task.run(); }
            @Override public Env getEnvironment() { return Env.SERVER; }
            @Override public RegistryAccess registryAccess() { return player.registryAccess(); }
        };
    }
    private static void put(ServerPlayer player, ItemStack... stacks) {
        player.getInventory().clearContent();
        for (int i = 0; i < stacks.length; i++) player.getInventory().setItem(i, stacks[i]);
    }
    private ItemStack item(String name, int count) {
        ResourceLocation id = ResourceLocation.parse("dimdungeons:" + name);
        check(BuiltInRegistries.ITEM.containsKey(id), "DD fixture item is registered: " + name);
        return new ItemStack(BuiltInRegistries.ITEM.get(id), count);
    }
    private static ListTag inventory(ServerPlayer player) { return player.getInventory().save(new ListTag()); }
    private TeamData readTeam(MinecraftServer server, UUID id) throws Exception {
        Path path = server.getWorldPath(ServerQuestFile.FTBQUESTS_DATA).resolve(id + ".snbt");
        check(Files.isRegularFile(path), "Native team progress file exists");
        SNBTCompoundTag disk = SNBT.tryRead(path);
        check(disk != null, "Native SNBT reader loads team progress");
        TeamData result = new TeamData(id, ServerQuestFile.INSTANCE); result.deserializeNBT(disk); return result;
    }
    @SuppressWarnings("unchecked") private static <T> T field(Class<?> type, Object instance, String name) throws Exception {
        Field field = type.getDeclaredField(name); field.setAccessible(true); return (T) field.get(instance);
    }
    private void check(boolean condition, String message) {
        assertions++; if (!condition) throw new AssertionError(message);
    }
    private void write(String phase, boolean passed, String error) throws Exception {
        results.put("assertions", assertions); results.put("phase", phase); results.put("passed", passed);
        if (error != null) results.put("error", error);
        Files.writeString(Path.of("soqm_probe_result.json"), new GsonBuilder().setPrettyPrinting().create().toJson(results));
    }

    private static final class Players implements AutoCloseable {
        final MinecraftServer server;
        final ServerPlayer first, second;
        final PartyTeam party;
        final List<ServerPlayer> online;
        final Map<UUID, ServerPlayer> byId;
        final Map<UUID, List<String>> chat = new LinkedHashMap<>();
        boolean closed;

        Players(MinecraftServer server) throws Exception {
            this.server = server;
            online = field(PlayerList.class, server.getPlayerList(), "players");
            byId = field(PlayerList.class, server.getPlayerList(), "playersByUUID");
            first = create(FIRST, "DDQuestFirst"); second = create(SECOND, "DDQuestSecond");
            party = TeamManagerImpl.INSTANCE.createParty(first, "DD quest fixture");
            party.join(second);
            first.getInventory().clearContent(); second.getInventory().clearContent();
        }

        ServerPlayer create(UUID id, String name) {
            GameProfile profile = new GameProfile(id, name);
            List<String> messages = new ArrayList<>();
            chat.put(id, messages);
            ServerPlayer player = new ServerPlayer(server, server.overworld(), profile, ClientInformation.createDefault());
            Connection connection = new Connection(PacketFlow.SERVERBOUND);
            new EmbeddedChannel(connection);
            player.connection = new ServerGamePacketListenerImpl(server, connection, player,
                    CommonListenerCookie.createInitial(profile, false)) {
                private void capture(Packet<?> packet) {
                    if (packet instanceof ClientboundSystemChatPacket message) messages.add(message.content().getString());
                }
                @Override public void send(Packet<?> packet) { capture(packet); }
                @Override public void send(Packet<?> packet, PacketSendListener listener) { capture(packet); }
            };
            player.setPos(8.5, 200, 8.5);
            player.setGameMode(GameType.SURVIVAL);
            // Keep the absent-client fixtures stationary and alive while the
            // harness reloads data; these flags do not satisfy quest goals.
            player.setNoGravity(true);
            player.setInvulnerable(true);
            online.add(player); byId.put(id, player);
            server.overworld().addNewPlayer(player);
            TeamManagerImpl.INSTANCE.playerLoggedIn(player, id, name);
            return player;
        }

        @Override public void close() {
            if (closed) return; closed = true;
            for (ServerPlayer player : List.of(first, second)) {
                TeamManagerImpl.INSTANCE.playerLoggedOut(player);
                online.remove(player); byId.remove(player.getUUID());
                player.serverLevel().removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
                EmbeddedChannel channel = (EmbeddedChannel) player.connection.getConnection().channel();
                if (channel.isOpen()) channel.finishAndReleaseAll();
            }
        }
    }
}
