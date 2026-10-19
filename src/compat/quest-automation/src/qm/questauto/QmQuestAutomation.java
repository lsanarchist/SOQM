package qm.questauto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.CustomTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import it.hurts.sskirillss.relics.api.relics.IRelicItem;
import it.hurts.sskirillss.relics.api.relics.data.RelicData;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.millenaire.commerce.TradeGood;
import top.theillusivec4.curios.api.CuriosApi;
import velrondevs.botania.api.mana.ManaPool;

/** Observes real native actions, without issuing rewards or bypassing dependencies. */
@Mod(QmQuestAutomation.MOD_ID)
public final class QmQuestAutomation {
    public static final String MOD_ID = "qm_quest_automation";
    public record Objective(long quest, long task, String signal) {}
    public static final List<Objective> OBJECTIVES = List.of(
        new Objective(0x514D010000000001L, 0x514D020000000001L, "summary"),
        new Objective(0x514D010000000007L, 0x514D02000000000AL, "mill_trade"),
        new Objective(0x514D010000000008L, 0x514D02000000000BL, "mill_building_64"),
        new Objective(0x514D010000000003L, 0x514D020000000005L, "foundry_stone"),
        new Objective(0x514D010000000004L, 0x514D020000000006L, "mek_operation"),
        new Objective(0x514D010000000005L, 0x514D020000000008L, "mek_iron_32"),
        new Objective(0x514D01000000000BL, 0x514D02000000000EL, "mana_pool"),
        new Objective(0x514D01000000000CL, 0x514D02000000000FL, "summary"),
        new Objective(0x514D11FF5A4363E6L, 0x514D122A765B9623L, "fission_commission"),
        new Objective(0x514D113E1E8CF8B1L, 0x514D122185675E38L, "fusion_runtime"),
        new Objective(0x514D1112BAB39A83L, 0x514D123334C1CCAEL, "ultimine"),
        new Objective(0x514D11A7ED9AA3BBL, 0x514D12480266404EL, "summary"),
        new Objective(0x514D11407706C5A7L, 0x514D12A7B657AB1DL, "curios_equipped"),
        new Objective(0x514D11BF49B78F90L, 0x514D12A87813E335L, "relic_experience"),
        new Objective(0x514D113D5B1DB1C5L, 0x514D12D73681C680L, "relic_upgrade"),
        new Objective(0x514D11CCD1AF477EL, 0x514D1219811BF438L, "summary")
    );
    public static final Set<String> SIGNAL_NAMES = Set.of("mill_trade", "mill_building_64", "foundry_stone", "mek_operation", "mek_iron_32", "mana_pool", "fission_commission", "fusion_runtime", "ultimine", "curios_equipped", "relic_experience", "relic_upgrade");
    private static final Map<UUID, BreakGroup> ULTIMINE = new HashMap<>();
    private static final Map<UUID, ReactorWatch> REACTORS = new HashMap<>();
    public static long observedTrades, observedElectricRecipes, observedUltimineBreaks;

    public QmQuestAutomation() {
        NeoForge.EVENT_BUS.addListener(QmQuestAutomation::onTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmQuestAutomation::onDrops);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmQuestAutomation::onRightClick);
        NeoForge.EVENT_BUS.addListener(QmQuestAutomation::onLogout);
        NeoForge.EVENT_BUS.addListener(QmQuestAutomation::onStopped);
    }

    public static boolean realPlayer(Player player) {
        return player instanceof ServerPlayer && !(player instanceof FakePlayer) && !player.isSpectator();
    }

    private static TeamData team(ServerPlayer player) {
        return ServerQuestFile.INSTANCE == null ? null : ServerQuestFile.INSTANCE.getTeamData(player).orElse(null);
    }

    private static void record(ServerPlayer player, String signal, int count) {
        if (!realPlayer(player) || !SIGNAL_NAMES.contains(signal)) return;
        TeamData team = team(player);
        if (team != null) AutomationState.get(player.server).record(team.getTeamId(), signal, count);
    }

    /** Keep native task sequencing and all hard progression dependencies. */
    public static boolean canComplete(TeamData data, Task task) {
        if (!(task instanceof CustomTask) || task.getMaxProgress() != 1 || data.isCompleted(task)) return false;
        Quest quest = task.getQuest();
        if (!data.canStartTasks(quest, false) || !data.areDependenciesComplete(quest)) return false;
        if (quest.getRequireSequentialTasks()) {
            List<Task> tasks = quest.getTasksAsList();
            int index = tasks.indexOf(task);
            if (index < 0 || index > 0 && !data.isCompleted(tasks.get(index - 1))) return false;
        }
        return true;
    }

    public static boolean reconcileObjective(TeamData data, Task task, Objective objective, AutomationState state) {
        if (task == null || task.id != objective.task() || task.getQuest().id != objective.quest() || !canComplete(data, task)) return false;
        if (!objective.signal().equals("summary") && state.get(data.getTeamId(), objective.signal()) < AutomationState.cap(objective.signal())) return false;
        data.setProgress(task, 1);
        return true;
    }

    private static void onTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (server.getTickCount() % 20 != 0 || ServerQuestFile.INSTANCE == null) return;
        Set<UUID> seen = new HashSet<>();
        AutomationState state = AutomationState.get(server);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!realPlayer(player)) continue;
            TeamData data = team(player);
            if (data != null) observeEquipment(player, data.getTeamId(), state);
            observeReactor(player, server.getTickCount());
            if (data == null || !seen.add(data.getTeamId())) continue;
            for (Objective objective : OBJECTIVES) {
                Task task = ServerQuestFile.INSTANCE.getTask(objective.task());
                reconcileObjective(data, task, objective, state);
            }
        }
        ULTIMINE.entrySet().removeIf(e -> e.getValue().tick != server.getTickCount());
    }

    private static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ULTIMINE.remove(event.getEntity().getUUID());
        REACTORS.remove(event.getEntity().getUUID());
    }

    private static void onStopped(ServerStoppedEvent event) {
        ULTIMINE.clear(); REACTORS.clear();
        observedTrades = observedElectricRecipes = observedUltimineBreaks = 0;
    }

    public static boolean buildingMaterial(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(ItemTags.LOGS) || stack.is(ItemTags.PLANKS)) return true;
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return Set.of("minecraft:stone", "minecraft:cobblestone", "minecraft:mossy_cobblestone", "minecraft:stone_bricks", "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks", "minecraft:chiseled_stone_bricks", "minecraft:granite", "minecraft:diorite", "minecraft:andesite", "minecraft:deepslate", "minecraft:cobbled_deepslate", "minecraft:deepslate_bricks", "minecraft:deepslate_tiles", "minecraft:bricks", "minecraft:brick", "minecraft:clay", "minecraft:clay_ball", "minecraft:sand", "minecraft:red_sand", "minecraft:sandstone", "minecraft:cut_sandstone", "minecraft:smooth_sandstone", "minecraft:chiseled_sandstone", "minecraft:red_sandstone", "minecraft:glass", "minecraft:glass_pane", "minecraft:gravel", "minecraft:terracotta", "minecraft:obsidian", "minecraft:nether_bricks", "minecraft:nether_brick", "minecraft:quartz_block", "minecraft:quartz_pillar").contains(id);
    }

    /** Called after native accepted quantity is known and refunds have completed. */
    public static void tradeSuccess(Player player, TradeGood good, int accepted, boolean soldToVillage) {
        observedTrades++;
        if (!realPlayer(player) || accepted <= 0) return;
        ServerPlayer sp = (ServerPlayer) player;
        record(sp, "mill_trade", 1);
        Item resolved = good.resolveItem();
        if (soldToVillage && resolved != null && buildingMaterial(new ItemStack(resolved))) record(sp, "mill_building_64", accepted);
    }

    public static int ironOutput(ItemStackToItemStackRecipe recipe) {
        List<ItemStack> outputs = recipe.getOutputDefinition();
        // This machine has one operation per finish. Accept only an unambiguous
        // native recipe whose output is iron; dynamic alternatives are not proof.
        return outputs.size() == 1 && outputs.getFirst().is(Items.IRON_INGOT) ? outputs.getFirst().getCount() : 0;
    }

    /** Called after CachedRecipe.finishProcessing, never on a blocked attempt. */
    public static void electricFinished(TileEntityElectricMachine machine, ItemStackToItemStackRecipe recipe) {
        observedElectricRecipes++;
        if (!(machine.getLevel() instanceof ServerLevel level)) return;
        if (level.players().isEmpty() || ServerQuestFile.INSTANCE == null) return;
        Vec3 center = machine.getBlockPos().getCenter();
        AutomationState state = AutomationState.get(level.getServer());
        int iron = -1; // Resolve output once, only if a nearby team still needs it.
        Set<UUID> seen = new HashSet<>();
        for (ServerPlayer player : level.players()) {
            if (!realPlayer(player) || player.distanceToSqr(center) > 256) continue;
            TeamData team = team(player);
            if (team == null || !seen.add(team.getTeamId())) continue;
            UUID teamId = team.getTeamId();
            if (state.get(teamId, "mek_operation") < AutomationState.cap("mek_operation")) state.record(teamId, "mek_operation", 1);
            if (state.get(teamId, "mek_iron_32") >= AutomationState.cap("mek_iron_32")) continue;
            if (iron < 0) {
                boolean smelter = BuiltInRegistries.BLOCK.getKey(machine.getBlockState().getBlock()).toString().equals("mekanism:energized_smelter");
                iron = smelter ? ironOutput(recipe) : 0;
            }
            if (iron > 0) state.record(teamId, "mek_iron_32", iron);
        }
    }

    /** Distinct positions from actual successful native Ultimine calls only. */
    public static final class BreakGroup {
        public long tick = Long.MIN_VALUE;
        public final Set<BlockPos> blocks = new HashSet<>();
        public boolean add(long now, BlockPos pos) {
            if (now != tick) { tick = now; blocks.clear(); }
            if (blocks.size() < 2) blocks.add(pos.immutable());
            return blocks.size() >= 2;
        }
    }

    public static void ultimineBreak(ServerPlayer player, BlockPos pos, boolean successful) {
        if (!successful || !realPlayer(player)) return;
        observedUltimineBreaks++;
        BreakGroup group = ULTIMINE.computeIfAbsent(player.getUUID(), ignored -> new BreakGroup());
        if (group.add(player.server.getTickCount(), pos)) record(player, "ultimine", 1);
    }

    private static void onDrops(BlockDropsEvent event) {
        if (event.isCanceled() || !(event.getBreaker() instanceof ServerPlayer player) || !realPlayer(player)) return;
        if (event.getState().is(Blocks.STONE) && BuiltInRegistries.ITEM.getKey(event.getTool().getItem()).toString().equals("modernfoundry:pickaxe")) record(player, "foundry_stone", 1);
    }

    private static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled() || event.getHand() != InteractionHand.MAIN_HAND || !realPlayer(event.getEntity()) || event.getEntity().isShiftKeyDown()) return;
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!player.level().hasChunkAt(event.getPos())) return;
        BlockEntity block = player.level().getBlockEntity(event.getPos());
        if (block instanceof ManaPool pool && pool.getCurrentMana() > 0 && !BuiltInRegistries.BLOCK.getKey(block.getBlockState().getBlock()).toString().equals("botania:creative_pool")) record(player, "mana_pool", 1);
        if (block instanceof TileEntityMultiblock<?> tile) {
            MultiblockData data = tile.getMultiblock();
            if (data.isFormed() && (data instanceof FissionReactorMultiblockData || data instanceof FusionReactorMultiblockData)) {
                ReactorWatch previous = REACTORS.get(player.getUUID());
                if (previous == null || previous.data != data || !previous.dimension.equals(player.level().dimension())) REACTORS.put(player.getUUID(), new ReactorWatch(player.level().dimension(), event.getPos().immutable(), data));
            }
        }
    }

    private static boolean gear(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String ns = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
        return ns.equals("artifacts") || ns.equals("relics") && stack.getItem() instanceof IRelicItem;
    }

    public static boolean relicUsed(RelicData relic) {
        double experience = relic.getLevelingData().getTotalSourceExperience();
        return Double.isFinite(experience) && experience > 0;
    }

    public static boolean relicUpgraded(RelicData relic) {
        if (relic.getLevelingData().getLevel() < 1) return false;
        return relic.getAbilitiesData().getAbilityIDs().stream().anyMatch(id -> {
            var ability = relic.getAbilitiesData().getAbilityData(id);
            return ability.getLevel() > 0 && ability.getTemplate() != null && ability.getTemplate().getRequiredPoints() > 0;
        });
    }

    private static void observeEquipment(ServerPlayer player, UUID teamId, AutomationState state) {
        // Evidence is retained even before its quest opens. Resolve the current
        // team on each pass; neither task completion nor a player/team cache is
        // a safe reason to stop observing another team's missing evidence.
        boolean needEquipped = state.get(teamId, "curios_equipped") < AutomationState.cap("curios_equipped");
        boolean needExperience = state.get(teamId, "relic_experience") < AutomationState.cap("relic_experience");
        boolean needUpgrade = state.get(teamId, "relic_upgrade") < AutomationState.cap("relic_upgrade");
        if (!needEquipped && !needExperience && !needUpgrade) return;
        var inventory = CuriosApi.getCuriosInventory(player).orElse(null);
        if (inventory == null) return;
        for (var entry : inventory.getCurios().entrySet()) {
            String id = entry.getKey();
            var handler = entry.getValue();
            var stacks = handler.getStacks(); // explicitly excludes cosmetic stacks
            for (int slot = 0; slot < stacks.getSlots(); slot++) {
                if (!inventory.isSlotActive(id, slot)) continue;
                ItemStack stack = stacks.getStackInSlot(slot);
                if (!gear(stack)) continue;
                if (needEquipped) {
                    state.record(teamId, "curios_equipped", 1);
                    needEquipped = false;
                    if (!needExperience && !needUpgrade) return;
                }
                if (!(stack.getItem() instanceof IRelicItem item)) continue;
                RelicData relic = item.getRelicData(player, stack);
                if (needExperience && relicUsed(relic)) {
                    state.record(teamId, "relic_experience", 1);
                    needExperience = false;
                }
                if (needUpgrade && relicUpgraded(relic)) {
                    state.record(teamId, "relic_upgrade", 1);
                    needUpgrade = false;
                }
                if (!needEquipped && !needExperience && !needUpgrade) return;
            }
        }
    }

    /** Runtime continuity state is deliberately not persisted across logouts. */
    public static final class ReactorClock {
        private long began = -1, last = -1;
        private boolean qualified;
        public boolean sample(long tick, boolean runningSafely, boolean normalStop, boolean fusion) {
            if (last >= 0 && (tick <= last || tick - last > 20)) { began = -1; qualified = false; }
            last = tick;
            if (runningSafely) {
                if (began < 0) began = tick;
                if (tick - began >= 200) qualified = true;
                return fusion && qualified;
            }
            if (!fusion && qualified && normalStop) return true;
            began = -1; qualified = false;
            return false;
        }
    }

    private static final class ReactorWatch {
        final ResourceKey<Level> dimension;
        final BlockPos pos;
        final MultiblockData data;
        final ReactorClock clock = new ReactorClock();
        ReactorWatch(ResourceKey<Level> dimension, BlockPos pos, MultiblockData data) { this.dimension=dimension; this.pos=pos; this.data=data; }
    }

    public static boolean fissionSafe(FissionReactorMultiblockData data) {
        return data.isFormed() && !data.isForceDisabled() && Double.isFinite(data.heatCapacitor.getTemperature()) && data.heatCapacitor.getTemperature() < 1200 && data.reactorDamage == 0 && (!data.coolantTank.getFluidTank().isEmpty() || !data.coolantTank.getChemicalTank().isEmpty());
    }

    private static void observeReactor(ServerPlayer player, long tick) {
        ReactorWatch watch = REACTORS.get(player.getUUID());
        if (watch == null) return;
        Level level = player.level();
        if (!watch.dimension.equals(level.dimension()) || !level.hasChunkAt(watch.pos) || player.distanceToSqr(watch.pos.getCenter()) > 256 || !watch.data.isFormed()) { REACTORS.remove(player.getUUID()); return; }
        BlockEntity block = level.getBlockEntity(watch.pos);
        if (!(block instanceof TileEntityMultiblock<?> tile) || tile.getMultiblock() != watch.data) { REACTORS.remove(player.getUUID()); return; }
        if (watch.data instanceof FissionReactorMultiblockData data) {
            boolean safe = fissionSafe(data);
            if (watch.clock.sample(tick, safe && data.isActive() && data.lastBurnRate > 0, safe && !data.isActive() && data.lastBurnRate == 0, false)) {
                record(player, "fission_commission", 1); REACTORS.remove(player.getUUID());
            }
        } else if (watch.data instanceof FusionReactorMultiblockData data) {
            boolean generating = data.isFormed() && data.isBurning() && data.getPassiveGeneration(false, true) > 0 && data.energyContainer != null && data.energyContainer.getEnergy() > 0;
            if (watch.clock.sample(tick, generating, false, true)) { record(player, "fusion_runtime", 1); REACTORS.remove(player.getUUID()); }
        }
    }
}
