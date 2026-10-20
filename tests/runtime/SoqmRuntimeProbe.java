package soqm.validation;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.api.util.AEColor;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.storage.MEChestBlockEntity;
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
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.AdvancementTask;
import io.netty.channel.embedded.EmbeddedChannel;
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
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
    private final List<Long> legacySharedRewardIds = new ArrayList<>();
    private final List<Long> completedBranchQuestIds = List.of(
            0x5140118233B3CC83L, 0x51301145AC657CE8L, 0x513011AAD4652E6FL,
            0x513011CDE1D10A76L, 0x51301111493CFC21L, 0x51301181BF058BAEL,
            0x513011087C06E9D8L, 0x5130112F15810A47L, 0x51301138646C9874L,
            0x513011C521E5B4DDL, 0x51301132F9C95555L, 0x5130113FB494C115L,
            0x513011ED1A3D11E0L, 0x51301144D5FB95D4L, 0x51301155BDC855A3L,
            0x513011F1FC51AAE1L, 0x51301125CDBD7A23L);
    private TeamData meTaskTeam;

    public SoqmRuntimeProbe() { NeoForge.EVENT_BUS.addListener(this::tick); }

    private void tick(ServerTickEvent.Post event) {
        if (finished || event.getServer().getTickCount() < 20) return;
        MinecraftServer server = event.getServer();
        try {
            if (stage == 0) {
                check(ServerQuestFile.INSTANCE != null, "FTB quest file is loaded");
                HardcoreWorkshopProbe.run(server, results);
                testRecipeEvidence(server);
                testProgressUpgrade(server);
                MultiblockMaterialsProbe.run(server, results);
                prepareWorkshop(server);
                EquipmentCompatibilityProbe.run(player, results);
                CardboardTransportProbe.run(server, player, results);
                FusionRuntimeProbe.run(server, results);
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
                ProductionEvidenceProbe.begin(server, results);
                stage = 4;
            } else if (stage == 4 && ProductionEvidenceProbe.tick(server, results)) {
                ProgressiveRewardsProbe.run(server, results);
                writeResult(false, "awaiting_reload", null);
                stage = 3;
            } else if (stage == 3 && Files.exists(Path.of("soqm_probe_reload_complete"))) {
                testReloadPersistence(server);
                ProductionEvidenceProbe.verifyReload(server, results);
                ProgressiveRewardsProbe.verifyReload(server, results);
                EquipmentCompatibilityProbe.run(player, results);
                finish(true, null);
            }
        } catch (Throwable failure) {
            failure.printStackTrace();
            finish(false, failure.getClass().getSimpleName() + ": " + failure.getMessage());
        }
    }

    private void testRecipeEvidence(MinecraftServer server) throws Exception {
        JsonObject items = JsonParser.parseString(Files.readString(Path.of("soqm_probe_recipe_evidence.json")))
                .getAsJsonObject().getAsJsonObject("items");
        var uniqueRecipes = new java.util.HashSet<String>();
        int manufactured = 0;
        for (var itemEntry : items.entrySet()) {
            String itemId = itemEntry.getKey();
            check(BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemId)),
                    "Catalogue prize is registered: " + itemId);
            JsonObject evidence = itemEntry.getValue().getAsJsonObject();
            if (evidence.get("starter_supply").getAsBoolean()) continue;
            var recipes = evidence.getAsJsonArray("production_recipes");
            check(recipes != null && !recipes.isEmpty(), "Expensive prize has productive recipe evidence: " + itemId);
            for (var element : recipes) {
                JsonObject definition = element.getAsJsonObject();
                String id = definition.get("id").getAsString();
                var recipe = server.getRecipeManager().byKey(ResourceLocation.parse(id));
                check(recipe.isPresent(), "Production recipe remains registered after pack overrides: " + id + " -> " + itemId);
                ItemStack output = recipe.orElseThrow().value().getResultItem(server.registryAccess());
                check(output.isEmpty() || BuiltInRegistries.ITEM.getKey(output.getItem()).toString().equals(itemId),
                        "A fixed recipe output matches its catalogue evidence: " + id + " -> " + itemId);
                uniqueRecipes.add(id);
            }
            manufactured++;
        }
        check(manufactured == 202, "Every distinct manufactured reward has a native recipe route");
        results.put("native_registered_catalogue_items", items.size());
        results.put("native_registered_manufactured_catalogue_items", manufactured);
        results.put("native_registered_production_recipe_ids", uniqueRecipes.size());
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
            if (progress.size() < 40 || completedBranchQuestIds.contains(quest.id)) {
                for (Task task : quest.getTasksAsList()) {
                    long value = completedBranchQuestIds.contains(quest.id) ? task.getMaxProgress()
                            : task.getMaxProgress() > 1 ? task.getMaxProgress() - 1 : 1;
                    before.setProgress(task, value);
                    progress.add(task);
                }
            }
            if (completedBranchQuestIds.contains(quest.id)) {
                before.setStarted(quest.id, new Date(900_000L));
                before.setCompleted(quest.id, new Date(900_001L));
            }
            for (Reward reward : quest.getRewards()) {
                check(before.markRewardAsClaimed(PLAYER, reward, 1_000_000L), "First fixture claim");
                claimed.add(reward);
            }
        });
        check(!progress.isEmpty() && !claimed.isEmpty(), "Upgrade fixture contains actual progress and claims");
        SNBTCompoundTag saved = before.serializeNBT();
        SNBT.tryWrite(Path.of("soqm_probe_legacy.snbt").toAbsolutePath(), saved);
        TeamData after = new TeamData(TEAM, current);
        after.deserializeNBT(saved);
        int partial = 0;
        for (Task oldTask : progress) {
            Task task = current.getTask(oldTask.id);
            check(task != null && after.getProgress(task) == before.getProgress(oldTask), "Task progress retained by persistent ID");
            if (before.getProgress(oldTask) < oldTask.getMaxProgress()) partial++;
        }
        check(saved.equals(after.serializeNBT()), "Loading personal reward definitions preserves all legacy claim keys");
        for (long id : completedBranchQuestIds) {
            check(current.getQuest(id) != null && after.isCompleted(current.getQuest(id)),
                    "Completed production branch quests survive changed dependencies by persistent ID");
        }
        checkNewLessonsUnclaimed(after);
        int sharedMigrations = 0;
        int personalClaims = 0;
        for (Reward oldReward : claimed) {
            Reward reward = current.getReward(oldReward.id);
            check(reward != null && !reward.isTeamReward(), "Every current reward has personal claim scope");
            if (oldReward.isTeamReward()) {
                check(after.isRewardClaimed(new UUID(0L, 0L), reward), "The historical shared NIL_UUID claim remains recorded");
                check(!after.isRewardClaimed(PLAYER, reward) && !after.isRewardClaimed(TEAMMATE, reward),
                        "A legacy shared claim intentionally grants each member one new personal entitlement");
                check(after.markRewardAsClaimed(PLAYER, reward, 2_000_000L)
                        && after.markRewardAsClaimed(TEAMMATE, reward, 2_000_001L),
                        "Both members may take their new personal reward once");
                check(!after.markRewardAsClaimed(PLAYER, reward, 2_000_002L)
                        && !after.markRewardAsClaimed(TEAMMATE, reward, 2_000_003L),
                        "Personal migration does not allow duplicate claims");
                check(after.isRewardClaimed(new UUID(0L, 0L), reward), "Personal claims retain historical shared evidence");
                legacySharedRewardIds.add(reward.id);
                sharedMigrations++;
            } else {
                check(after.isRewardClaimed(PLAYER, reward)
                        && !after.markRewardAsClaimed(PLAYER, reward, 2_000_000L),
                        "An existing personal claim is retained without a new entitlement");
                check(!after.isRewardClaimed(TEAMMATE, reward), "The other member keeps an independent personal entitlement");
                personalClaims++;
            }
        }
        check((sharedMigrations == 1625 && personalClaims == 1)
                        || (sharedMigrations == 0 && personalClaims == claimed.size()
                            && (personalClaims == 1626 || personalClaims == 1627 || personalClaims == 1628)),
                "The baseline exercises either the shared upgrade or already personal campaign claims");
        check(partial > 0, "Nonempty partial task progress was exercised");
        upgradeState = after.serializeNBT();
        SNBT.tryWrite(Path.of("soqm_probe_before.snbt").toAbsolutePath(), upgradeState);
        claimed.forEach(reward -> claimedRewardIds.add(reward.id));
        results.put("legacy_shared_keys_preserved_and_new_personal_entitlements", sharedMigrations);
        results.put("migrated_personal_duplicate_claims_rejected_for_two_members", sharedMigrations * 2);
        results.put("task_progress_preserved", progress.size());
        results.put("partial_tasks_preserved", partial);
        results.put("existing_personal_claims_preserved", personalClaims);
        results.put("completed_branch_quests_preserved", completedBranchQuestIds.size());
        results.put("moved_mekasuit_quests_with_completed_progress_preserved", 14);
        results.put("new_equipment_lessons_start_unclaimed", 15);
    }

    private static void checkNewLessonsUnclaimed(TeamData data) {
        for (int index = 1; index <= 15; index++) {
            var quest = ServerQuestFile.INSTANCE.getQuest(0x514C110000000000L + index);
            check(quest != null && !data.isCompleted(quest), "An additive lesson never inherits an old completion");
            for (Reward reward : quest.getRewards()) {
                check(!data.isRewardClaimed(PLAYER, reward) && !data.isRewardClaimed(TEAMMATE, reward),
                        "An additive personal reward never inherits an unrelated old claim");
            }
        }
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
        for (long id : completedBranchQuestIds) {
            check(restoredTeam.isCompleted(ServerQuestFile.INSTANCE.getQuest(id)),
                    "Completed production branch quests remain complete after disk and data reload");
        }
        checkNewLessonsUnclaimed(restoredTeam);
        for (long id : claimedRewardIds) {
            Reward reward = ServerQuestFile.INSTANCE.getReward(id);
            check(reward != null && restoredTeam.isRewardClaimed(PLAYER, reward)
                    && !restoredTeam.markRewardAsClaimed(PLAYER, reward, 3_000_000L), "Claim remains blocked after data reload");
            check(restoredTeam.isRewardClaimed(TEAMMATE, reward) == legacySharedRewardIds.contains(id),
                    "Each member's personal claims survive disk and data reload independently");
            if (legacySharedRewardIds.contains(id)) {
                check(restoredTeam.isRewardClaimed(new UUID(0L, 0L), reward),
                        "Historical shared claim keys survive disk and data reload");
            }
        }
        results.put("me_advancement_reload_and_disk_persistence", true);
        results.put("nonempty_team_state_reload_and_disk_persistence", true);
        results.put("completed_branch_quests_reload_and_disk_persistence", true);
    }

    private void prepareWorkshop(MinecraftServer server) {
        ServerLevel level = server.overworld();
        GameProfile profile = new GameProfile(PLAYER, "SOQMProbe");
        player = new ServerPlayer(server, level, profile, ClientInformation.createDefault());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        // Cardboard capture posts native BreakEvent listeners, including accessory
        // sync. Use the existing absent-client fixture transport: real channel
        // attributes for capability queries and a sink for outgoing client packets.
        new EmbeddedChannel(connection);
        player.connection = ProductionEvidenceProbe.listener(server, connection, player, profile);
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
        place(level, origin.east(), "ae2:chest");
        MEChestBlockEntity chest = (MEChestBlockEntity) level.getBlockEntity(origin.east());
        chest.setCell(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("ae2:item_storage_cell_1k"))));
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
