package soqm.validation.questauto;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbquests.quest.BaseQuestFile;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import dev.ftb.mods.ftbteams.data.PlayerTeam;
import dev.ftb.mods.ftbteams.data.TeamManagerImpl;
import io.netty.channel.embedded.EmbeddedChannel;
import it.hurts.sskirillss.relics.api.events.relic.GatherRelicTemplateCacheKeyEvent;
import it.hurts.sskirillss.relics.api.relics.IRelicItem;
import it.hurts.sskirillss.relics.api.relics.data.RelicData;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import mekanism.api.recipes.basic.BasicSmeltingRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import qm.questauto.AutomationState;
import qm.questauto.QmQuestAutomation;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioStacksHandler;
import top.theillusivec4.curios.common.inventory.DynamicStackHandler;

/** Test-only native fixture; run in a fresh server, on a multiple-of-20 tick. */
public final class QuestAutomationPerformanceProbe {
    private QuestAutomationPerformanceProbe() {}

    private static final Method EQUIPMENT;
    static {
        try {
            EQUIPMENT = QmQuestAutomation.class.getDeclaredMethod("observeEquipment", ServerPlayer.class, UUID.class, AutomationState.class);
            EQUIPMENT.setAccessible(true);
        } catch (ReflectiveOperationException failure) { throw new ExceptionInInitializerError(failure); }
    }

    private static void observeEquipment(ServerPlayer player, UUID team, AutomationState state) {
        try { EQUIPMENT.invoke(null, player, team, state); }
        catch (InvocationTargetException failure) {
            if (failure.getCause() instanceof Error error) throw error;
            throw new IllegalStateException("Native equipment observation failed", failure.getCause());
        } catch (ReflectiveOperationException failure) { throw new IllegalStateException(failure); }
    }

    public static Map<String, Object> run(MinecraftServer server) throws Exception {
        check(server.getTickCount() % 20 == 0, "Run the quest fixture on its actual polling cadence");
        check(ServerQuestFile.INSTANCE != null && TeamManagerImpl.INSTANCE != null, "Native FTB data is loaded");
        Map<String, Object> result = new LinkedHashMap<>();
        testEquipment(server, result);
        try (Teams fixture = new Teams(server)) {
            testTeamChanges(fixture, result);
            testElectricRecipes(fixture, result);
        }
        return result;
    }

    private static void testEquipment(MinecraftServer server, Map<String, Object> result) {
        ServerPlayer player = player(server, "EquipmentProbe");
        ICuriosItemHandler inventory = CuriosApi.getCuriosInventory(player).orElseThrow();
        var original = new HashMap<>(inventory.getCurios());
        CountingSlots slots = new CountingSlots(inventory);
        inventory.setCurios(Map.of("curio", slots));
        AtomicInteger templates = new AtomicInteger();
        Consumer<GatherRelicTemplateCacheKeyEvent> listener = event -> templates.incrementAndGet();
        NeoForge.EVENT_BUS.addListener(listener);
        try {
            UUID team = UUID.randomUUID();
            AutomationState state = new AutomationState();
            ItemStack relicStack = stack("relics:chorus_staff");
            check(relicStack.getItem() instanceof IRelicItem, "Native Relics fixture item is a relic");
            RelicData relic = ((IRelicItem) relicStack.getItem()).getRelicData(player, relicStack);
            String ability = relic.getAbilitiesData().getAbilityIDs().stream()
                    .filter(id -> relic.getAbilitiesData().getAbilityData(id).getTemplate().getRequiredPoints() > 0)
                    .findFirst().orElseThrow();

            slots.getCosmeticStacks().setStackInSlot(0, relicStack.copy());
            observeEquipment(player, team, state);
            evidence(state, team, 0, 0, 0, "Cosmetic equipment provides no evidence");
            slots.getCosmeticStacks().setStackInSlot(0, ItemStack.EMPTY);

            slots.stacks.setStackInSlot(0, relicStack);
            inventory.setSlotActive("curio", 0, false);
            observeEquipment(player, team, state);
            evidence(state, team, 0, 0, 0, "Inactive equipment provides no evidence");
            inventory.setSlotActive("curio", 0, true);

            slots.stacks.setStackInSlot(0, new ItemStack(Items.STONE));
            player.getInventory().setItem(0, relicStack);
            observeEquipment(player, team, state);
            evidence(state, team, 0, 0, 0, "Inventory-held relics and ordinary equipped items provide no evidence");
            player.getInventory().clearContent();

            slots.stacks.setStackInSlot(0, relicStack);
            observeEquipment(player, team, state);
            evidence(state, team, 1, 0, 0, "Equipping a fresh relic only records equipment");
            relic.getLevelingData().setExperience(5);
            observeEquipment(player, team, state);
            evidence(state, team, 1, 0, 0, "Generic XP without source XP does not prove native relic use");
            relic.getLevelingData().addSourceExperience(ability, "probe", 1);
            observeEquipment(player, team, state);
            evidence(state, team, 1, 1, 0, "Native source XP records the second signal");
            relic.getLevelingData().setLevel(1);
            observeEquipment(player, team, state);
            evidence(state, team, 1, 1, 0, "Relic level alone does not prove a paid ability upgrade");
            relic.getAbilitiesData().getAbilityData(ability).setLevel(1);
            observeEquipment(player, team, state);
            evidence(state, team, 1, 1, 1, "An actual ability level records the third signal");

            CompoundTag saved = state.save(new CompoundTag(), server.registryAccess());
            state = AutomationState.load(saved, server.registryAccess());
            state.setDirty(false);
            slots.resetReads(); templates.set(0);
            for (int i = 0; i < 100; i++) observeEquipment(player, team, state);
            check(slots.stackAccesses == 0 && slots.stacks.reads == 0 && templates.get() == 0,
                    "Completed persisted evidence bypasses all Curios slot and relic template reads");
            check(!state.isDirty(), "Repeated completed observations do not dirty saved data");
            result.put("completed_equipment_passes_with_zero_slot_reads", 100);

            // A completed upgrade must not cause template/ability traversal when
            // only source XP is still missing.
            UUID xpOnly = UUID.randomUUID();
            state.record(xpOnly, "curios_equipped", 1);
            state.record(xpOnly, "relic_upgrade", 1);
            templates.set(0);
            observeEquipment(player, xpOnly, state);
            evidence(state, xpOnly, 1, 1, 1, "Missing XP is still detected with other evidence capped");
            check(templates.get() == 0, "An already recorded upgrade does not revisit relic ability templates");
            result.put("already_recorded_upgrade_skips_template_reads", true);

            UUID equippedOnly = UUID.randomUUID();
            state.record(equippedOnly, "relic_experience", 1);
            state.record(equippedOnly, "relic_upgrade", 1);
            slots.stacks.setStackInSlot(0, stack("artifacts:cross_necklace"));
            slots.stacks.setStackInSlot(1, relicStack);
            slots.resetReads(); templates.set(0);
            observeEquipment(player, equippedOnly, state);
            evidence(state, equippedOnly, 1, 1, 1, "An artifact satisfies the remaining equipment signal");
            check(slots.stacks.reads == 1 && templates.get() == 0, "Stop after the first item fulfills all missing evidence");
            result.put("early_exit_slots_read", slots.stacks.reads);

            UUID beforeUnlock = UUID.randomUUID();
            TeamData data = new TeamData(beforeUnlock, ServerQuestFile.INSTANCE);
            var objective = QmQuestAutomation.OBJECTIVES.stream().filter(o -> o.signal().equals("curios_equipped")).findFirst().orElseThrow();
            Task task = ServerQuestFile.INSTANCE.getTask(objective.task());
            check(task != null && !data.areDependenciesComplete(task.getQuest()), "Updated questbook fixture starts behind real dependencies");
            observeEquipment(player, beforeUnlock, state);
            check(state.get(beforeUnlock, "curios_equipped") == 1, "Evidence is recorded before the quest opens");
            check(!QmQuestAutomation.reconcileObjective(data, task, objective, state), "Early evidence does not bypass dependencies");
            for (var prerequisite : task.getQuest().streamDependencies().toList()) {
                data.setStarted(prerequisite.id, new Date(1_000_000L));
                data.setCompleted(prerequisite.id, new Date(1_000_001L));
            }
            check(data.areDependenciesComplete(task.getQuest()), "Native prerequisite fixture is complete");
            check(QmQuestAutomation.reconcileObjective(data, task, objective, state), "Existing evidence completes the task after its dependencies open");
            check(!QmQuestAutomation.reconcileObjective(data, task, objective, state), "Already completed task is not completed twice");
            result.put("early_evidence_and_native_quest_gates_preserved", true);
            result.put("native_equipment_signal_transitions_and_slot_filters", true);
        } finally {
            NeoForge.EVENT_BUS.unregister(listener);
            inventory.setCurios(original);
            closePlayer(player);
        }
    }

    private static void testTeamChanges(Teams fixture, Map<String, Object> result) throws Exception {
        ServerPlayer player = fixture.addPlayer("TeamProbe");
        ICuriosItemHandler inventory = CuriosApi.getCuriosInventory(player).orElseThrow();
        CountingSlots slots = new CountingSlots(inventory);
        inventory.setCurios(Map.of("curio", slots));
        slots.stacks.setStackInSlot(0, stack("artifacts:cross_necklace"));
        AutomationState state = AutomationState.get(fixture.server);
        UUID first = fixture.currentTeam(player).getTeamId();
        for (String signal : List.of("curios_equipped", "relic_experience", "relic_upgrade")) state.record(first, signal, 1);
        Method tick = QmQuestAutomation.class.getDeclaredMethod("onTick", ServerTickEvent.Post.class);
        tick.setAccessible(true);
        slots.resetReads();
        tick.invoke(null, new ServerTickEvent.Post(() -> true, fixture.server));
        check(slots.stackAccesses == 0, "Current completed team skips equipment from the actual tick callback");

        PartyTeam next = fixture.newTeam();
        fixture.personal(player).setEffectiveTeam(next);
        check(fixture.currentTeam(player).getTeamId().equals(next.getTeamId()), "Native FTB lookup sees the new effective team");
        slots.resetReads();
        tick.invoke(null, new ServerTickEvent.Post(() -> true, fixture.server));
        check(slots.stacks.reads > 0 && state.get(next.getTeamId(), "curios_equipped") == 1,
                "The same player is observed again for the new team's missing evidence");
        evidence(state, first, 1, 1, 1, "Changing teams retains the old team's evidence");
        evidence(state, next.getTeamId(), 1, 0, 0, "Changing teams does not copy unrelated old evidence");
        result.put("native_current_team_lookup_on_each_tick", true);
        fixture.removePlayer(player);
    }

    private static void testElectricRecipes(Teams fixture, Map<String, Object> result) {
        ServerLevel level = fixture.server.overworld();
        BlockPos pos = new BlockPos(8, 200, 8);
        var blockState = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("mekanism:energized_smelter")).defaultBlockState();
        var block = ((EntityBlock) blockState.getBlock()).newBlockEntity(pos, blockState);
        check(block instanceof TileEntityElectricMachine, "Native energized smelter fixture resolves");
        TileEntityElectricMachine machine = (TileEntityElectricMachine) block;
        machine.setLevel(level);
        var recipe = new CountingRecipe();
        var otherRecipe = new BasicSmeltingRecipe(ItemStackIngredient.of(SizedIngredient.of(Items.RAW_GOLD, 1)), new ItemStack(Items.GOLD_INGOT));
        Vec3 center = pos.getCenter();
        ServerPlayer inside = fixture.addPlayer("RecipeInside");
        ServerPlayer teammate = fixture.addPlayer("RecipeMate");
        fixture.personal(teammate).setEffectiveTeam(fixture.personal(inside).getEffectiveTeam());
        ServerPlayer edge = fixture.addPlayer("RecipeEdge");
        ServerPlayer outside = fixture.addPlayer("RecipeOutside");
        ServerPlayer corner = fixture.addPlayer("RecipeCorner");
        ServerPlayer spectator = fixture.addPlayer("RecipeSpectator");
        ServerPlayer noTeam = fixture.addPlayer("RecipeNoTeam");
        inside.setPos(center); teammate.setPos(center.add(1, 0, 0));
        edge.setPos(center.add(16, 0, 0)); outside.setPos(center.add(16.0001, 0, 0));
        corner.setPos(center.add(12, 12, 0)); spectator.setPos(center); noTeam.setPos(center);
        check(spectator.setGameMode(GameType.SPECTATOR) && spectator.isSpectator(),
                "Native spectator transition completes its actual equipment and packet synchronization");
        fixture.knownPlayers.remove(noTeam.getUUID());
        AutomationState state = AutomationState.get(fixture.server);
        UUID team = fixture.currentTeam(inside).getTeamId();
        UUID edgeTeam = fixture.currentTeam(edge).getTeamId();
        QmQuestAutomation.electricFinished(machine, recipe);
        check(state.get(team, "mek_operation") == 1 && state.get(team, "mek_iron_32") == 2,
                "A recipe counts once for two nearby players on one team");
        check(state.get(edgeTeam, "mek_iron_32") == 2, "Exactly sixteen blocks remains in range");
        check(recipe.outputReads == 1, "Several eligible teams share one output-definition lookup");
        for (ServerPlayer excluded : List.of(outside, corner, spectator)) {
            UUID id = fixture.currentTeam(excluded).getTeamId();
            check(state.get(id, "mek_operation") == 0 && state.get(id, "mek_iron_32") == 0,
                    "Outside-sphere and spectator players receive no evidence");
        }
        QmQuestAutomation.electricFinished(machine, otherRecipe);
        check(state.get(team, "mek_iron_32") == 2, "A non-iron output does not advance iron production");
        for (int i = 0; i < 15; i++) QmQuestAutomation.electricFinished(machine, recipe);
        check(state.get(team, "mek_iron_32") == 32, "Repeated real finishes reach the existing cap");
        state.setDirty(false);
        recipe.outputReads = 0;
        QmQuestAutomation.electricFinished(machine, recipe);
        check(!state.isDirty(), "Completed nearby teams do not dirty SavedData");
        check(recipe.outputReads == 0, "Completed nearby teams do not resolve recipe outputs");

        PartyTeam changed = fixture.newTeam();
        fixture.personal(inside).setEffectiveTeam(changed);
        QmQuestAutomation.electricFinished(machine, recipe);
        check(state.get(changed.getTeamId(), "mek_iron_32") == 2, "Recipe observation uses current team membership");
        check(state.get(team, "mek_iron_32") == 32, "Former team's capped evidence remains unchanged");
        result.put("native_recipe_exact_radius_spectator_and_team_filters", true);
        result.put("native_recipe_team_dedup_caps_and_team_changes", true);
        result.put("capped_recipe_output_reads", 0);
    }

    private static final class CountingRecipe extends BasicSmeltingRecipe {
        int outputReads;
        CountingRecipe() {
            super(ItemStackIngredient.of(SizedIngredient.of(Items.RAW_IRON, 1)), new ItemStack(Items.IRON_INGOT, 2));
        }
        @Override public List<ItemStack> getOutputDefinition() { outputReads++; return super.getOutputDefinition(); }
    }

    private static final class CountingStacks extends DynamicStackHandler {
        int reads;
        CountingStacks(ICuriosItemHandler inventory) {
            super(2, slot -> new SlotContext("curio", inventory.getWearer(), slot, false, true));
        }
        @Override public ItemStack getStackInSlot(int slot) { reads++; return super.getStackInSlot(slot); }
    }

    private static final class CountingSlots extends CurioStacksHandler {
        final CountingStacks stacks;
        int stackAccesses;
        CountingSlots(ICuriosItemHandler inventory) {
            super(inventory, "curio", 2, true, true, true, ICurio.DropRule.DEFAULT);
            stacks = new CountingStacks(inventory);
        }
        @Override public IDynamicStackHandler getStacks() {
            stackAccesses++;
            return stacks == null ? super.getStacks() : stacks;
        }
        void resetReads() { stackAccesses = 0; stacks.reads = 0; }
    }

    /** Temporary fixture membership; no login, sockets, chunk loading or real player data. */
    private static final class Teams implements AutoCloseable {
        final MinecraftServer server;
        final TeamManagerImpl manager = TeamManagerImpl.INSTANCE;
        final Map<UUID, PlayerTeam> knownPlayers;
        final List<ServerPlayer> online;
        final Map<UUID, ServerPlayer> byUUID;
        final Map<UUID, TeamData> questTeams;
        final List<ServerPlayer> players = new ArrayList<>();
        final List<UUID> teamIds = new ArrayList<>();

        Teams(MinecraftServer server) throws Exception {
            this.server = server;
            knownPlayers = field(TeamManagerImpl.class, manager, "knownPlayers");
            online = field(PlayerList.class, server.getPlayerList(), "players");
            byUUID = field(PlayerList.class, server.getPlayerList(), "playersByUUID");
            questTeams = field(BaseQuestFile.class, ServerQuestFile.INSTANCE, "teamDataMap");
        }

        PartyTeam newTeam() {
            UUID id = UUID.randomUUID();
            PartyTeam team = new PartyTeam(manager, id);
            manager.getTeamMap().put(id, team);
            ServerQuestFile.INSTANCE.getOrCreateTeamData(team);
            teamIds.add(id);
            return team;
        }

        ServerPlayer addPlayer(String name) {
            ServerPlayer player = player(server, name);
            PlayerTeam personal = new PlayerTeam(manager, player.getUUID());
            personal.setEffectiveTeam(newTeam());
            knownPlayers.put(player.getUUID(), personal);
            online.add(player); byUUID.put(player.getUUID(), player);
            server.overworld().players().add(player);
            players.add(player);
            return player;
        }

        PlayerTeam personal(ServerPlayer player) { return knownPlayers.get(player.getUUID()); }
        TeamData currentTeam(ServerPlayer player) { return ServerQuestFile.INSTANCE.getTeamData(player).orElseThrow(); }

        void removePlayer(ServerPlayer player) {
            online.remove(player); byUUID.remove(player.getUUID());
            server.overworld().players().remove(player);
            knownPlayers.remove(player.getUUID());
            closePlayer(player);
        }

        @Override public void close() throws Exception {
            for (ServerPlayer player : players) removePlayer(player);
            Map<UUID, ?> evidence = field(AutomationState.class, AutomationState.get(server), "teams");
            for (UUID id : teamIds) {
                manager.getTeamMap().remove(id); questTeams.remove(id); evidence.remove(id);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(Class<?> owner, Object instance, String name) throws Exception {
        Field field = owner.getDeclaredField(name); field.setAccessible(true);
        return (T) field.get(instance);
    }

    private static ServerPlayer player(MinecraftServer server, String name) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        ClientInformation information = ClientInformation.createDefault();
        ServerPlayer player = new ServerPlayer(server, server.overworld(), profile, information);
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        // Keep native payload checks and sends; capture outgoing packets in memory.
        new EmbeddedChannel(connection);
        NetworkRegistry.configureMockConnection(connection);
        player.connection = new ServerGamePacketListenerImpl(server, connection, player,
                new CommonListenerCookie(profile, 0, information, false, ConnectionType.NEOFORGE));
        return player;
    }

    private static void closePlayer(ServerPlayer player) {
        EmbeddedChannel channel = (EmbeddedChannel) player.connection.getConnection().channel();
        if (channel.isOpen()) channel.finishAndReleaseAll();
    }

    private static ItemStack stack(String id) {
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)));
        check(!stack.isEmpty(), "Native fixture item exists: " + id);
        return stack;
    }

    private static void evidence(AutomationState state, UUID team, int equipped, int xp, int upgraded, String message) {
        check(state.get(team, "curios_equipped") == equipped && state.get(team, "relic_experience") == xp
                && state.get(team, "relic_upgrade") == upgraded, message);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
