package soqm.validation;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.api.util.AEColor;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.core.definitions.AEParts;
import appeng.helpers.InventoryAction;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.slot.CraftingTermSlot;
import appeng.parts.reporting.CraftingTerminalPart;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.ItemReward;
import dev.ftb.mods.ftbquests.quest.reward.RandomReward;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.AdvancementTask;
import io.netty.buffer.Unpooled;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Test-only mod. The harness installs it only in a fresh localhost test world. */
@Mod("soqm_runtime_probe")
public final class SoqmRuntimeProbe {
    private static final UUID TEAM = UUID.fromString("d2e89d3c-f0f0-41ab-bb3d-46bb8eaf52da");
    private static final UUID PLAYER = UUID.fromString("be188e85-a312-446e-b010-7be5f8c2f642");
    private static final UUID TEAMMATE = UUID.fromString("ebc4d20d-3e11-4190-a967-ad66b51b821b");
    private static final ResourceLocation ME_ADVANCEMENT = ResourceLocation.parse("qm:ae2/first_me_craft");
    private final Map<String, Object> results = new LinkedHashMap<>();
    private int began = -1;
    private int stage;
    private boolean finished;
    private ServerPlayer player;
    private CraftingTerminalPart terminal;
    private CraftingTermMenu menu;
    private BlockPos origin;
    private SNBTCompoundTag upgradeState;
    private final List<Long> claimedRewardIds = new ArrayList<>();
    private TeamData meTaskTeam;

    public SoqmRuntimeProbe() { NeoForge.EVENT_BUS.addListener(this::tick); }

    private void tick(ServerTickEvent.Post event) {
        if (finished || event.getServer().getTickCount() < 20) return;
        MinecraftServer server = event.getServer();
        try {
            if (stage == 0) {
                check(ServerQuestFile.INSTANCE != null, "FTB quest file is loaded");
                testRewards(server);
                testProgressUpgrade(server);
                prepareWorkshop(server);
                testClaimDelivery();
                began = server.getTickCount(); stage = 1;
            } else if (stage == 1 && server.getTickCount() - began >= 80) {
                check(!terminal.getMainNode().isActive(), "Unpowered ME fixture must be inactive");
                craft();
                check(!meComplete(server), "Unpowered ME craft must not grant the milestone");
                results.put("me_unpowered_rejected", true);
                place(server.overworld(), origin.west(), "ae2:creative_energy_cell");
                began = server.getTickCount(); stage = 2;
            } else if (stage == 2 && server.getTickCount() - began >= 80) {
                check(terminal.getMainNode().isActive(), "Powered fixture must have a working ME channel");
                MEStorage storage = terminal.getGridNode().getGrid().getStorageService().getInventory();
                check(storage.getAvailableStacks().isEmpty(), "Empty-network negative fixture");
                craft();
                check(!meComplete(server), "A terminal with no stored items must not grant the milestone");
                results.put("me_empty_storage_rejected", true);
                check(storage.insert(AEItemKey.of(Items.COBBLESTONE), 16, Actionable.MODULATE,
                        IActionSource.ofPlayer(player)) == 16, "Native ME storage accepted sixteen items");
                craft();
                check(meComplete(server), "A native craft in an active, stocked ME terminal grants the milestone");
                checkMeQuestTask(true);
                check(storage.getAvailableStacks().get(AEItemKey.of(Items.COBBLESTONE)) == 16,
                        "Milestone observation must not consume unrelated stored items");
                results.put("me_native_terminal_craft_passed", true);
                results.put("me_network_storage_preserved", true);
                player.getAdvancements().save();
                writeResult(false, "awaiting_reload", null);
                stage = 3;
            } else if (stage == 3 && Files.exists(Path.of("soqm_probe_reload_complete"))) {
                testReloadPersistence(server);
                finish(true, null);
            }
        } catch (Throwable failure) {
            failure.printStackTrace();
            finish(false, failure.getClass().getSimpleName() + ": " + failure.getMessage());
        }
    }

    private void testRewards(MinecraftServer server) {
        ServerQuestFile file = ServerQuestFile.INSTANCE;
        List<RandomReward> randomRewards = new ArrayList<>();
        file.forAllQuests(q -> q.getRewards().forEach(r -> { if (r instanceof RandomReward rr) randomRewards.add(rr); }));
        check(!randomRewards.isEmpty(), "Actual campaign has native random rewards");
        int weightedEntries = 0;
        for (RandomReward reward : randomRewards) {
            var table = reward.getTable();
            check(table != null && !table.getWeightedRewards().isEmpty(), "Resolved nonempty reward table");
            check(table.getTotalWeight(false) > 0, "Positive table weight");
            for (var entry : table.getWeightedRewards()) {
                check(entry.getWeight() > 0 && Float.isFinite(entry.getWeight()), "Finite positive reward weight");
                check(entry.getReward() instanceof ItemReward item && !item.getItem().isEmpty() && item.getCount() > 0,
                        "Each table entry resolves to a real item");
                weightedEntries++;
            }
            CompoundTag disk = new CompoundTag();
            reward.writeData(disk, server.registryAccess());
            RandomReward diskCopy = new RandomReward(reward.id, reward.getQuest());
            diskCopy.readData(disk, server.registryAccess());
            check(diskCopy.getTable() != null && diskCopy.getTable().id == table.id,
                    "Random reward NBT round trip retains table");
            RegistryFriendlyByteBuf network = new RegistryFriendlyByteBuf(Unpooled.buffer(), server.registryAccess());
            try {
                reward.writeNetData(network);
                RandomReward networkCopy = new RandomReward(reward.id, reward.getQuest());
                networkCopy.readNetData(network);
                check(network.readableBytes() == 0 && networkCopy.getTable() != null
                        && networkCopy.getTable().id == table.id, "Native reward packet round trip");
            } finally { network.release(); }
            RandomSource random = RandomSource.create(884411L);
            for (int i = 0; i < 32; i++) {
                var selected = table.generateWeightedRandomRewards(random, 1, false);
                check(selected.size() == 1 && table.getWeightedRewards().contains(selected.iterator().next()),
                        "A native random claim resolves to exactly one configured reward");
            }
        }
        results.put("native_random_rewards_roundtripped", randomRewards.size());
        results.put("resolved_weighted_entries", weightedEntries);
    }

    private void testProgressUpgrade(MinecraftServer server) throws Exception {
        JsonObject config = JsonParser.parseString(Files.readString(Path.of("soqm_probe_config.json"))).getAsJsonObject();
        ServerQuestFile oldFile = new ServerQuestFile(server);
        oldFile.readDataFull(Path.of(config.get("baseline_quests").getAsString()), server.registryAccess());
        ServerQuestFile current = ServerQuestFile.INSTANCE;
        TeamData before = new TeamData(TEAM, oldFile, "Synthetic upgrade fixture");
        List<Task> progress = new ArrayList<>();
        List<Reward> claimed = new ArrayList<>();
        oldFile.forAllQuests(quest -> {
            if (progress.size() < 40 || quest.id == 0x513011AAD4652E6FL) {
                for (Task task : quest.getTasksAsList()) {
                    long value = task.getMaxProgress() > 1 ? task.getMaxProgress() - 1 : 1;
                    before.setProgress(task, value);
                    progress.add(task);
                }
            }
            for (Reward reward : quest.getRewards()) {
                check(before.markRewardAsClaimed(PLAYER, reward, 1_000_000L), "First fixture claim");
                claimed.add(reward);
            }
        });
        check(!progress.isEmpty() && !claimed.isEmpty(), "Upgrade fixture contains actual progress and claims");
        SNBTCompoundTag saved = before.serializeNBT();
        SNBT.tryWrite(Path.of("soqm_probe_before.snbt").toAbsolutePath(), saved);
        TeamData after = new TeamData(TEAM, current);
        after.deserializeNBT(saved);
        int partial = 0;
        for (Task oldTask : progress) {
            Task task = current.getTask(oldTask.id);
            check(task != null && after.getProgress(task) == before.getProgress(oldTask), "Task progress retained by persistent ID");
            if (before.getProgress(oldTask) < oldTask.getMaxProgress()) partial++;
        }
        int teamClaims = 0;
        int personalClaims = 0;
        for (Reward oldReward : claimed) {
            Reward reward = current.getReward(oldReward.id);
            check(reward != null && after.isRewardClaimed(PLAYER, reward), "Old claimed reward remains claimed after type change");
            check(!after.markRewardAsClaimed(PLAYER, reward, 2_000_000L), "Reload cannot grant a second claim");
            check(oldReward.isTeamReward() == reward.isTeamReward(), "Upgrade preserves team/personal reward scope");
            check(after.isRewardClaimed(TEAMMATE, reward) == oldReward.isTeamReward(),
                    "A teammate shares team claims while keeping a separate personal-book claim");
            if (oldReward.isTeamReward()) {
                check(!after.markRewardAsClaimed(TEAMMATE, reward, 2_000_000L), "A teammate cannot claim a shared reward twice");
                teamClaims++;
            } else {
                personalClaims++;
            }
        }
        check(teamClaims > 0 && personalClaims > 0, "Both shared and personal reward scopes were exercised");
        check(partial > 0, "Nonempty partial task progress was exercised");
        check(saved.equals(after.serializeNBT()), "Native TeamData round trip retains full fixture state");
        upgradeState = saved;
        claimed.forEach(reward -> claimedRewardIds.add(reward.id));
        results.put("old_claims_preserved_and_duplicate_claims_rejected", claimed.size());
        results.put("task_progress_preserved", progress.size());
        results.put("partial_tasks_preserved", partial);
        results.put("team_claims_shared_with_second_player", teamClaims);
        results.put("personal_claims_distinct_for_second_player", personalClaims);
    }

    private void testClaimDelivery() {
        var seenTables = new java.util.HashSet<Long>();
        List<RandomReward> representatives = new ArrayList<>();
        ServerQuestFile.INSTANCE.forAllQuests(q -> q.getRewards().forEach(reward -> {
            if (reward instanceof RandomReward random && seenTables.add(random.getTable().id)) representatives.add(random);
        }));
        TeamData claims = new TeamData(UUID.fromString("764ff5bb-a6e5-4f31-a427-c0afad2055c0"), ServerQuestFile.INSTANCE);
        for (RandomReward reward : representatives) {
            player.getInventory().clearContent();
            claims.claimReward(player, reward, false);
            List<ItemStack> inventory = new ArrayList<>();
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty()) inventory.add(stack.copy());
            }
            check(!inventory.isEmpty(), "Native random claim delivers items to the synthetic player");
            boolean configured = reward.getTable().getWeightedRewards().stream().anyMatch(entry -> {
                ItemReward item = (ItemReward) entry.getReward();
                return inventory.stream().allMatch(stack -> ItemStack.isSameItemSameComponents(stack, item.getItem()))
                        && inventory.stream().mapToInt(ItemStack::getCount).sum() == item.getCount();
            });
            check(configured, "Delivered stack matches exactly one configured weighted reward");
            int count = inventory.stream().mapToInt(ItemStack::getCount).sum();
            claims.claimReward(player, reward, false);
            int countAfter = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) countAfter += player.getInventory().getItem(i).getCount();
            check(countAfter == count, "Claiming the same native random reward twice delivers nothing extra");
        }
        player.getInventory().clearContent();
        results.put("native_random_claim_deliveries_and_duplicate_rejections", representatives.size());
    }

    private void testReloadPersistence(MinecraftServer server) throws Exception {
        player.getAdvancements().reload(server.getAdvancements());
        check(meComplete(server), "Completed ME milestone survives native advancement reload");
        Path advancementFile = server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).resolve(PLAYER + ".json");
        check(Files.isRegularFile(advancementFile), "Native player advancement save exists on disk");
        // PlayerList caches PlayerAdvancements by UUID. Construct an independent
        // native reader so this assertion cannot pass by reusing the live cache.
        PlayerAdvancements restored = new PlayerAdvancements(server.getFixerUpper(), server.getPlayerList(),
                server.getAdvancements(), advancementFile, player);
        try {
            check(restored != player.getAdvancements()
                    && restored.getOrStartProgress(server.getAdvancements().get(ME_ADVANCEMENT)).isDone(),
                    "An independent native reader loads the earned ME milestone from disk");
        } finally { restored.stopListening(); }
        PlayerAdvancements empty = new PlayerAdvancements(server.getFixerUpper(), server.getPlayerList(),
                server.getAdvancements(), Path.of("soqm_probe_absent_advancements.json"), player);
        try {
            check(!empty.getOrStartProgress(server.getAdvancements().get(ME_ADVANCEMENT)).isDone(),
                    "A missing advancement file cannot inherit completion from the player's live cache");
        } finally { empty.stopListening(); }
        SNBTCompoundTag saved = SNBT.tryRead(Path.of("soqm_probe_before.snbt"));
        TeamData restoredTeam = new TeamData(TEAM, ServerQuestFile.INSTANCE);
        restoredTeam.deserializeNBT(saved);
        check(upgradeState.equals(restoredTeam.serializeNBT()), "Nonempty progress survives disk and data reload");
        for (long id : claimedRewardIds) {
            Reward reward = ServerQuestFile.INSTANCE.getReward(id);
            check(reward != null && restoredTeam.isRewardClaimed(PLAYER, reward)
                    && !restoredTeam.markRewardAsClaimed(PLAYER, reward, 3_000_000L), "Claim remains blocked after data reload");
            check(restoredTeam.isRewardClaimed(TEAMMATE, reward) == reward.isTeamReward(),
                    "Team and personal claim scope survives disk and data reload");
        }
        results.put("me_advancement_reload_and_disk_persistence", true);
        results.put("nonempty_team_state_reload_and_disk_persistence", true);
    }

    private void prepareWorkshop(MinecraftServer server) {
        ServerLevel level = server.overworld();
        GameProfile profile = new GameProfile(PLAYER, "SOQMProbe");
        player = new ServerPlayer(server, level, profile, ClientInformation.createDefault());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        player.connection = new ServerGamePacketListenerImpl(server, connection, player,
                CommonListenerCookie.createInitial(profile, false));
        meTaskTeam = new TeamData(UUID.fromString("a4bdb6a4-09f6-42d7-800b-9c118b9b8ab9"), ServerQuestFile.INSTANCE);
        var workshopQuest = ServerQuestFile.INSTANCE.getQuest(0x513011AAD4652E6FL);
        check(workshopQuest != null, "Persistent ME quest exists");
        var prerequisites = workshopQuest.streamDependencies().toList();
        check(!prerequisites.isEmpty(), "ME fixture exercises a quest with prerequisite gates");
        // Model an existing player who has reached this quest. The ME task and
        // its advancement remain unearned until the real terminal output click.
        for (var dependency : prerequisites) {
            meTaskTeam.setStarted(dependency.id, new Date(1_000_000L));
            meTaskTeam.setCompleted(dependency.id, new Date(1_000_001L));
        }
        check(meTaskTeam.areDependenciesComplete(workshopQuest), "Native prerequisite checks accept the fixture state");
        results.put("me_prerequisite_fixture_objects", prerequisites.size());
        origin = new BlockPos(8, 200, 8);
        player.setPos(8.5, 200, 6.5);
        check(server.getAdvancements().get(ME_ADVANCEMENT) != null, "ME milestone advancement loaded");
        EventHooks.firePlayerCraftingEvent(player, new ItemStack(Items.OAK_PLANKS, 4), new SimpleContainer(9));
        check(!meComplete(server), "An ordinary crafting event cannot grant ME completion");
        checkMeQuestTask(false);
        results.put("me_other_menu_rejected", true);

        place(level, origin, "ae2:cable_bus");
        place(level, origin.east(), "ae2:drive");
        DriveBlockEntity drive = (DriveBlockEntity) level.getBlockEntity(origin.east());
        drive.getInternalInventory().setItemDirect(0,
                new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("ae2:item_storage_cell_1k"))));
        CableBusBlockEntity cable = (CableBusBlockEntity) level.getBlockEntity(origin);
        cable.addPart(AEParts.GLASS_CABLE.item(AEColor.TRANSPARENT), null, player);
        terminal = cable.addPart(AEParts.CRAFTING_TERMINAL.get(), Direction.NORTH, player);
        check(terminal != null, "Native AE2 crafting terminal placed");
        menu = new CraftingTermMenu(10, player.getInventory(), terminal);
        player.containerMenu = menu;
    }

    private void checkMeQuestTask(boolean expected) {
        var quest = ServerQuestFile.INSTANCE.getQuest(0x513011AAD4652E6FL);
        check(quest != null && quest.getTasksAsList().size() == 1, "Persistent ME quest resolves to one task");
        Task task = quest.getTasksAsList().getFirst();
        check(task instanceof AdvancementTask, "ME quest uses native advancement detection");
        AdvancementTask advancementTask = (AdvancementTask) task;
        check(advancementTask.canSubmit(meTaskTeam, player) == expected,
                "The native FTB advancement task recognizes the actual ME criterion state");
        advancementTask.submitTask(meTaskTeam, player, ItemStack.EMPTY);
        check(meTaskTeam.isCompleted(task) == expected, "Native task submission follows the earned criterion");
        results.put(expected ? "native_me_quest_task_accepts_crafted_advancement"
                : "native_me_quest_task_rejects_unearned_advancement", true);
    }

    private void craft() {
        menu.setCarried(ItemStack.EMPTY);
        menu.getCraftingMatrix().clear();
        menu.getCraftingMatrix().setItemDirect(0, new ItemStack(Items.OAK_LOG));
        menu.slotsChanged(menu.getCraftingMatrix().toContainer());
        CraftingTermSlot output = (CraftingTermSlot) menu.slots.stream()
                .filter(s -> s instanceof CraftingTermSlot).findFirst().orElseThrow();
        check(output.getItem().is(Items.OAK_PLANKS), "Native crafting recipe resolved");
        output.doClick(InventoryAction.CRAFT_ITEM, player);
        check(menu.getCarried().is(Items.OAK_PLANKS) && menu.getCarried().getCount() == 4,
                "Native ME output click produced four planks");
    }

    private boolean meComplete(MinecraftServer server) {
        AdvancementHolder holder = server.getAdvancements().get(ME_ADVANCEMENT);
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    private static void place(ServerLevel level, BlockPos pos, String id) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id));
        check(block != null && !id.equals("minecraft:air"), "Fixture block exists");
        level.setBlock(pos, block.defaultBlockState(), 3);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private void finish(boolean passed, String failure) {
        finished = true;
        writeResult(passed, "finished", failure);
        System.out.println("SOQM_RUNTIME_PROBE " + (passed ? "PASS" : "FAIL"));
    }

    private void writeResult(boolean passed, String phase, String failure) {
        results.put("passed", passed);
        results.put("phase", phase);
        results.put("scope", "Native dedicated-server tests with synthetic team/player fixtures. No graphical client or survival playthrough.");
        if (failure != null) results.put("failure", failure);
        try {
            Files.writeString(Path.of("soqm_probe_result.json"), new GsonBuilder().setPrettyPrinting().create().toJson(results));
        } catch (Exception error) { throw new RuntimeException(error); }
    }
}
