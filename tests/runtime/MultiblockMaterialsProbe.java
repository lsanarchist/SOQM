package soqm.validation;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbquests.item.MissingItem;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.util.DeferredInventoryDetection;
import dev.ftb.mods.ftbquests.util.FTBQuestsInventoryListener;
import dev.ftb.mods.ftbquests.util.PlayerInventorySummary;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.TeamManagerImpl;
import io.netty.channel.embedded.EmbeddedChannel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Native pinned FTB item tasks, not a reimplementation of their matcher. */
public final class MultiblockMaterialsProbe {
    private MultiblockMaterialsProbe() {}

    public static void run(MinecraftServer server, Map<String, Object> results) throws Exception {
        ServerQuestFile file = ServerQuestFile.INSTANCE;
        List<Quest> kits = new ArrayList<>();
        List<ItemTask> allItems = new ArrayList<>();
        file.forAllQuests(quest -> {
            if (quest.hasTag("multiblock_materials")) kits.add(quest);
            quest.getTasksAsList().forEach(task -> {
                if (task instanceof ItemTask item) allItems.add(item);
            });
        });
        check(kits.size() == 35, "All thirty-five construction kits are loaded");
        check(allItems.stream().filter(MultiblockMaterialsProbe::added).count() == 138,
                "All reviewed additive task IDs are loaded");
        GameProfile profile = new GameProfile(UUID.randomUUID(), "SOQMMaterials");
        ServerPlayer player = new ServerPlayer(server, server.overworld(), profile, ClientInformation.createDefault());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        EmbeddedChannel channel = new EmbeddedChannel(connection);
        player.connection = ProductionEvidenceProbe.listener(server, connection, player, profile);
        TeamManagerImpl manager = (TeamManagerImpl) FTBTeamsAPI.api().getManager();
        manager.playerLoggedIn(player, profile.getId(), profile.getName());
        TeamData actual = file.getOrCreateTeamData(manager.getTeamForPlayer(player).orElseThrow());
        int chunksBefore = server.overworld().getChunkSource().getLoadedChunksCount();
        int materialCount = 0, multiStack = 0, nativeChecks = 0;
        try {
            for (Quest quest : kits) {
                TeamData data = new TeamData(UUID.randomUUID(), file);
                for (var dependency : quest.streamDependencies().toList()) {
                    data.setCompleted(dependency.id, new Date(1_000_000L));
                }
                for (Task task : quest.getTasksAsList()) {
                    if (!(task instanceof ItemTask item)) continue;
                    check(!item.getItemStack().isEmpty() && !(item.getItemStack().getItem() instanceof MissingItem),
                            "Material resolves to a registered item: " + item.getCodeString());
                    check(!item.consumesResources() && !item.isOnlyFromCrafting() && !item.isTaskScreenOnly(),
                            "Construction materials use automatic non-consuming inventory checks");
                    int count = Math.toIntExact(item.getMaxProgress());
                    check(count > 0, "Native task has a positive count");
                    if (count > item.getItemStack().getMaxStackSize()) multiStack++;
                    fill(player, item.getItemStack(), count - 1, false);
                    PlayerInventorySummary.build(player);
                    for (int attempt = 0; attempt < 4; attempt++) item.submitTask(data, player, ItemStack.EMPTY);
                    check(data.getProgress(item) == count - 1 && !data.isCompleted(item),
                            "Repeated inventory detection neither fabricates nor accumulates missing material");
                    check(total(player) == count - 1, "Partial detection does not consume any item");
                    fill(player, item.getItemStack(), count, true);
                    PlayerInventorySummary.build(player);
                    item.submitTask(data, player, ItemStack.EMPTY);
                    check(data.getProgress(item) == count && data.isCompleted(item),
                            "Exact quantity across legal stacks and changed components completes the task");
                    check(total(player) == count, "Completed detection retains every construction block");
                    player.getInventory().clearContent();
                    PlayerInventorySummary.build(player);
                    item.submitTask(data, player, ItemStack.EMPTY);
                    check(data.getProgress(item) == count && data.isCompleted(item),
                            "Placing or moving already recorded materials does not revoke progress");
                    materialCount++;
                }
                for (Task task : quest.getTasksAsList()) {
                    if (!(task instanceof ItemTask)) {
                        check(!data.isCompleted(task) && data.getProgress(task) == 0,
                                "Having construction materials cannot grant formation or operation evidence");
                        nativeChecks++;
                    }
                }
                TeamData restored = new TeamData(data.getTeamId(), file);
                restored.deserializeNBT(data.serializeNBT());
                for (Task task : quest.getTasksAsList()) if (task instanceof ItemTask) {
                    check(restored.getProgress(task) == task.getMaxProgress() && restored.isCompleted(task),
                            "Native save/reload preserves material progress");
                }
            }
            check(materialCount == 166 && multiStack > 0 && nativeChecks > 0,
                    "Every live kit item and the separate native completion conditions were exercised");
            results.put("multiblock_native_kits", kits.size());
            results.put("multiblock_native_material_tasks", materialCount);
            results.put("multiblock_native_multi_stack_tasks", multiStack);
            results.put("multiblock_separate_native_conditions_not_granted", nativeChecks);
            results.put("multiblock_nonconsuming_counts_components_persistence", true);

            // Exercise the actual listener, including the 20-tick inventory
            // debounce. Repeated slot events must share one queued check.
            check(file.getDetectionDelay() == 20, "The pack coalesces inventory changes over twenty ticks");
            ItemTask sample = (ItemTask) kits.getFirst().getTasksAsList().stream()
                    .filter(task -> task instanceof ItemTask).findFirst().orElseThrow();
            for (var dependency : sample.getQuest().streamDependencies().toList()) {
                actual.setCompleted(dependency.id, new Date(1_000_000L));
            }
            check(actual.areDependenciesComplete(sample.getQuest()),
                    "Listener completion fixture has met the quest's real prerequisites");
            fill(player, sample.getItemStack(), Math.toIntExact(sample.getMaxProgress()), false);
            FTBQuestsInventoryListener.detect(player, ItemStack.EMPTY, 0);
            check(actual.isCompleted(sample), "The native full-book listener detects the real material task");
            FTBQuestsInventoryListener listener = new FTBQuestsInventoryListener(player);
            Field queuedField = DeferredInventoryDetection.class.getDeclaredField("playerMap");
            queuedField.setAccessible(true);
            @SuppressWarnings("unchecked") Map<UUID, Long> queued = (Map<UUID, Long>) queuedField.get(null);
            queued.remove(player.getUUID());
            listener.slotChanged(player.inventoryMenu, 36, player.getInventory().getItem(0));
            Long deadline = queued.get(player.getUUID());
            check(deadline != null, "A real inventory slot change schedules native detection");
            int queuedSize = queued.size();
            for (int n = 0; n < 100; n++) listener.slotChanged(player.inventoryMenu, 36, player.getInventory().getItem(0));
            check(queued.size() == queuedSize && deadline.equals(queued.get(player.getUUID())),
                    "One hundred changes do not multiply or indefinitely postpone the pending check");
            queued.remove(player.getUUID());
            results.put("multiblock_inventory_debounce_events_per_pending_check", 101);

            // Preserve a prior completed quest even when its newly introduced
            // inventory objectives were absent from that historical save.
            Quest legacyQuest = kits.stream().filter(quest -> !actual.isCompleted(quest)).findFirst().orElseThrow();
            long completedAt = 1_111_111L;
            actual.setCompleted(legacyQuest.id, new Date(completedAt));
            player.getInventory().clearContent();
            FTBQuestsInventoryListener.detect(player, ItemStack.EMPTY, 0);
            TeamData legacyReload = new TeamData(actual.getTeamId(), file);
            legacyReload.deserializeNBT(actual.serializeNBT());
            check(legacyReload.getCompletedTime(legacyQuest.id).orElseThrow().getTime() == completedAt,
                    "An existing completion timestamp survives new material definitions and native detection");
            results.put("multiblock_legacy_completion_timestamp_retained", true);

            TeamData benchmark = new TeamData(UUID.randomUUID(), file);
            List<ItemTask> baseline = allItems.stream().filter(task -> !added(task)).toList();
            // Empty inventory isolates lookup/dispatch overhead without progress
            // writes. This deliberately checks every task, even before unlock.
            Map<String, Object> timings = new LinkedHashMap<>();
            timings.put("scope", "Native ItemTask.submitTask plus one PlayerInventorySummary.build per scan; empty inventory, all item tasks, on the server thread. Not a client FPS or whole-server TPS benchmark.");
            timings.put("baseline_item_tasks", baseline.size());
            timings.put("current_item_tasks", allItems.size());
            timings.put("added_item_tasks", allItems.size() - baseline.size());
            timings.putAll(measurePaired(() -> scan(player, benchmark, baseline),
                    () -> scan(player, benchmark, allItems)));
            timings.put("actual_full_book_listener", measure(() -> FTBQuestsInventoryListener.detect(player, ItemStack.EMPTY, 0)));
            for (ItemTask item : allItems) benchmark.setCompleted(item.id, new Date(1_000_000L));
            timings.put("all_item_tasks_completed", measure(() -> scan(player, benchmark, allItems)));
            results.put("multiblock_detection_timings", timings);
            check(server.overworld().getChunkSource().getLoadedChunksCount() == chunksBefore,
                    "Material correctness and repeated detection never load another world chunk");
            results.put("multiblock_detection_additional_loaded_chunks", 0);
        } finally {
            player.getInventory().clearContent();
            channel.finishAndReleaseAll();
        }
    }

    private static boolean added(ItemTask task) { return task.getCodeString().startsWith("515012"); }

    private static void fill(ServerPlayer player, ItemStack template, int count, boolean renamed) {
        player.getInventory().clearContent();
        int slot = 0;
        while (count > 0) {
            check(slot < player.getInventory().items.size(), "Required material fits legal player inventory stacks");
            int amount = Math.min(count, template.getMaxStackSize());
            ItemStack stack = template.copyWithCount(amount);
            if (renamed) stack.set(DataComponents.CUSTOM_NAME, Component.literal("SOQM material fixture"));
            player.getInventory().setItem(slot++, stack);
            count -= amount;
        }
    }

    private static int total(ServerPlayer player) {
        return player.getInventory().items.stream().mapToInt(ItemStack::getCount).sum();
    }

    private static void scan(ServerPlayer player, TeamData data, List<ItemTask> tasks) {
        PlayerInventorySummary.build(player);
        for (ItemTask task : tasks) task.submitTask(data, player, ItemStack.EMPTY);
    }

    private static Map<String, Object> measure(Runnable scan) {
        for (int i = 0; i < 300; i++) scan.run();
        long[] samples = new long[1000];
        for (int i = 0; i < samples.length; i++) {
            long started = System.nanoTime();
            scan.run();
            samples[i] = System.nanoTime() - started;
        }
        return summarize(samples);
    }

    private static Map<String, Object> measurePaired(Runnable baseline, Runnable current) {
        for (int i = 0; i < 300; i++) { baseline.run(); current.run(); }
        long[] before = new long[1000], after = new long[1000];
        for (int i = 0; i < before.length; i++) {
            // Alternate order so warming/JIT drift does not always favor the
            // second variant. Report raw distributions, not a server TPS claim.
            Runnable first = i % 2 == 0 ? baseline : current;
            Runnable second = i % 2 == 0 ? current : baseline;
            long started = System.nanoTime();
            first.run();
            long firstTime = System.nanoTime() - started;
            started = System.nanoTime();
            second.run();
            long secondTime = System.nanoTime() - started;
            before[i] = i % 2 == 0 ? firstTime : secondTime;
            after[i] = i % 2 == 0 ? secondTime : firstTime;
        }
        return Map.of("baseline_scan", summarize(before), "current_scan", summarize(after),
                "comparison_order", "Alternating paired samples after warming both variants");
    }

    private static Map<String, Object> summarize(long[] samples) {
        Arrays.sort(samples);
        return Map.of("samples", samples.length, "p50_ms", samples[samples.length / 2] / 1_000_000.0,
                "p95_ms", samples[(int)(samples.length * 0.95)] / 1_000_000.0,
                "max_ms", samples[samples.length - 1] / 1_000_000.0);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
