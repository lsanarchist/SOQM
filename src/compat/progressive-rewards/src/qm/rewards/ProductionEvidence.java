package qm.rewards;

import appeng.api.networking.IManagedGridNode;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamManager;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityItemStackChemicalToItemStackFactory;
import mekanism.common.tile.machine.TileEntityMetallurgicInfuser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;

/**
 * Actual manufacture only. Personal FTB team data stores the player's own facts;
 * a party stores a separate union. Reading a party never copies its facts back.
 */
public final class ProductionEvidence {
    private static final int SCHEMA = 1;
    private static final String STATE = "soqm_production_v1";
    private static final String OWN = "own_items";
    private static final String UNION = "team_items";
    private static final String PRODUCER = "soqm_producer_v1";

    private ProductionEvidence() {}

    /** Stable KubeJS API. Inventory contents and quest completion are irrelevant. */
    public static boolean hasProduced(ServerPlayer player, String itemId) {
        if (player == null || !ProductionPolicy.allows(itemId)) return false;
        TeamManager manager = manager();
        if (manager == null) return false;
        Team personal = manager.getPlayerTeamForPlayerID(player.getUUID()).orElse(null);
        if (has(personal, ownKey(personal), itemId)) return true;
        Team current = manager.getTeamForPlayerID(player.getUUID()).orElse(null);
        return current != null && current.isPartyTeam() && has(current, unionKey(current), itemId);
    }

    /** Immutable, deterministic effective facts for the player's current team. */
    public static Set<String> getProducedItems(ServerPlayer player) {
        if (player == null) return Set.of();
        TeamManager manager = manager();
        if (manager == null) return Set.of();
        Set<String> items = new TreeSet<>();
        Team personal = manager.getPlayerTeamForPlayerID(player.getUUID()).orElse(null);
        collect(personal, ownKey(personal), items);
        Team current = manager.getTeamForPlayerID(player.getUUID()).orElse(null);
        if (current != null && current.isPartyTeam()) collect(current, unionKey(current), items);
        return Collections.unmodifiableSet(items);
    }

    /** Read-only diagnostic used to validate the runtime policy against the registry. */
    public static Set<String> getTrackedItems() { return ProductionPolicy.allowedItems(); }

    /** Cheap output filter; policy reload replaces its registered-item identity set. */
    public static boolean isTrackedOutput(ItemStack output) {
        return !output.isEmpty() && ProductionPolicy.allows(output.getItem());
    }

    static TeamManager manager() {
        return FTBTeamsAPI.api().isManagerLoaded() ? FTBTeamsAPI.api().getManager() : null;
    }

    static boolean realSurvivalPlayer(Player player) {
        return player instanceof ServerPlayer && !(player instanceof FakePlayer)
                && !player.isSpectator() && !player.isCreative();
    }

    // FTB copies extraData when creating a party. Scope every ledger key by its
    // actual owner so a copied old union cannot become a new team's discovery.
    private static String ownKey(Team team) { return OWN + ":" + (team == null ? "none" : team.getId()); }
    private static String unionKey(Team team) { return UNION + ":" + team.getId(); }
    private static String actorKey(UUID playerId) { return "actor_eligible:" + playerId; }

    private static CompoundTag state(Team team) {
        if (team == null) return new CompoundTag();
        CompoundTag state = team.getExtraData().getCompound(STATE);
        return state.getInt("schema") == SCHEMA ? state : new CompoundTag();
    }

    private static CompoundTag writableState(Team team) {
        CompoundTag extra = team.getExtraData();
        CompoundTag state = extra.getCompound(STATE);
        if (extra.contains(STATE, Tag.TAG_COMPOUND) && state.getInt("schema") != SCHEMA) return null;
        if (!extra.contains(STATE, Tag.TAG_COMPOUND)) {
            state.putInt("schema", SCHEMA);
            extra.put(STATE, state);
        }
        return state;
    }

    private static boolean has(Team team, String key, String itemId) {
        return state(team).getCompound(key).getBoolean(itemId);
    }

    private static void collect(Team team, String key, Set<String> into) {
        CompoundTag items = state(team).getCompound(key);
        for (String id : items.getAllKeys()) {
            if (items.getBoolean(id) && ProductionPolicy.allows(id)) into.add(id);
        }
    }

    private static void add(Team team, String key, String itemId) {
        if (team == null || has(team, key, itemId)) return;
        CompoundTag state = writableState(team);
        if (state == null) return;
        CompoundTag items = state.getCompound(key);
        items.putBoolean(itemId, true);
        state.put(key, items);
        team.markDirty();
    }

    static void rememberActor(ServerPlayer player, boolean eligible) {
        if (player instanceof FakePlayer) return;
        TeamManager manager = manager();
        if (manager == null) return;
        Team personal = manager.getPlayerTeamForPlayerID(player.getUUID()).orElse(null);
        if (personal == null) return;
        CompoundTag state = writableState(personal);
        String key = actorKey(player.getUUID());
        if (state != null && (!state.contains(key, Tag.TAG_BYTE)
                || state.getBoolean(key) != eligible)) {
            state.putBoolean(key, eligible);
            personal.markDirty();
        }
    }

    private static boolean eligibleActor(MinecraftServer server, UUID playerId, Team personal) {
        ServerPlayer online = server.getPlayerList().getPlayer(playerId);
        if (online != null) {
            boolean eligible = realSurvivalPlayer(online);
            rememberActor(online, eligible);
            return eligible;
        }
        // Unknown offline owners fail closed. Login/logout/game-mode events persist
        // the last real actor state; chunk-loaded production needs no player scan.
        return state(personal).getBoolean(actorKey(playerId));
    }

    private static void record(MinecraftServer server, UUID playerId, UUID capturedTeam, ItemStack output) {
        if (!isTrackedOutput(output)) return;
        String itemId = BuiltInRegistries.ITEM.getKey(output.getItem()).toString();
        TeamManager manager = manager();
        if (manager == null || manager.getServer() != server) return;
        Team personal = manager.getPlayerTeamForPlayerID(playerId).orElse(null);
        Team current = manager.getTeamForPlayerID(playerId).orElse(null);
        if (personal == null || current == null || !current.isValid()
                || !current.getId().equals(capturedTeam) || !eligibleActor(server, playerId, personal)) return;
        add(personal, ownKey(personal), itemId);
        if (current.isPartyTeam()) add(current, unionKey(current), itemId);
    }

    /** A rename, recolour, reset or pass-through cannot launder an existing item. */
    public static boolean isManufacture(ItemStack output, Container input) {
        if (output.isEmpty() || input == null) return false;
        boolean hasInput = false;
        for (int slot = 0; slot < input.getContainerSize(); slot++) {
            ItemStack ingredient = input.getItem(slot);
            if (ingredient.isEmpty()) continue;
            if (ingredient.is(output.getItem())) return false;
            hasInput = true;
        }
        return hasInput;
    }

    public static boolean isManufacture(ItemStack output, CraftingInput input) {
        if (output.isEmpty() || input == null) return false;
        boolean hasInput = false;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack ingredient = input.getItem(slot);
            if (ingredient.isEmpty()) continue;
            if (ingredient.is(output.getItem())) return false;
            hasInput = true;
        }
        return hasInput;
    }

    static void playerCrafted(Player player, ItemStack output, Container input) {
        if (!realSurvivalPlayer(player) || !isTrackedOutput(output) || !isManufacture(output, input)) return;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        rememberActor(serverPlayer, true);
        TeamManager manager = manager();
        if (manager == null) return;
        manager.getTeamForPlayerID(player.getUUID()).ifPresent(team ->
                record(serverPlayer.server, player.getUUID(), team.getId(), output));
    }

    static boolean supportedMachine(BlockEntity machine) {
        return machine instanceof appeng.blockentity.misc.InscriberBlockEntity
                || machine instanceof appeng.blockentity.crafting.MolecularAssemblerBlockEntity
                || machine instanceof TileEntityMetallurgicInfuser
                || machine instanceof TileEntityItemStackChemicalToItemStackFactory factory
                    && factory.getFactoryType() == FactoryType.INFUSING;
    }

    static void placed(BlockEntity machine, Player placer) {
        if (!supportedMachine(machine) || !(machine.getLevel() instanceof ServerLevel)) return;
        CompoundTag binding = new CompoundTag();
        binding.putInt("schema", SCHEMA);
        binding.putBoolean("allowed", false);
        TeamManager manager = manager();
        if (realSurvivalPlayer(placer) && manager != null) {
            ServerPlayer player = (ServerPlayer) placer;
            rememberActor(player, true);
            manager.getTeamForPlayerID(player.getUUID()).ifPresent(team -> {
                binding.putBoolean("allowed", true);
                binding.putUUID("player", player.getUUID());
                binding.putUUID("team", team.getId());
            });
        }
        // Persist a denial too: a newly creative/fake-placed machine must not be
        // re-attributed through its mod's native owner on its next output.
        machine.getPersistentData().put(PRODUCER, binding);
        machine.setChanged();
    }

    private static void machineFinished(BlockEntity machine, UUID nativeOwner, ItemStack output) {
        if (!(machine.getLevel() instanceof ServerLevel level) || !isTrackedOutput(output)) return;
        CompoundTag persistent = machine.getPersistentData();
        CompoundTag binding = persistent.getCompound(PRODUCER);
        if (!persistent.contains(PRODUCER, Tag.TAG_COMPOUND)) {
            // Existing survival worlds retain native AE2/Mekanism owners. Bind once
            // at an actual finish; never to a nearby player or a storage/CPU job.
            TeamManager manager = manager();
            if (nativeOwner == null || manager == null) return;
            Team personal = manager.getPlayerTeamForPlayerID(nativeOwner).orElse(null);
            Team current = manager.getTeamForPlayerID(nativeOwner).orElse(null);
            if (personal == null || current == null || !eligibleActor(level.getServer(), nativeOwner, personal)) return;
            binding.putInt("schema", SCHEMA);
            binding.putBoolean("allowed", true);
            binding.putUUID("player", nativeOwner);
            binding.putUUID("team", current.getId());
            persistent.put(PRODUCER, binding);
            machine.setChanged();
        }
        if (binding.getInt("schema") != SCHEMA || !binding.getBoolean("allowed")
                || !binding.hasUUID("player") || !binding.hasUUID("team")) return;
        UUID producer = binding.getUUID("player");
        if (nativeOwner != null && !producer.equals(nativeOwner)) return;
        record(level.getServer(), producer, binding.getUUID("team"), output);
    }

    /** Called only after accepted output AND native input consumption. */
    public static void ae2Finished(BlockEntity machine, IManagedGridNode node, ItemStack output) {
        if (!isTrackedOutput(output)) return;
        UUID owner = node.getNode() == null ? null : node.getNode().getOwningPlayerProfileId();
        machineFinished(machine, owner, output);
    }

    /** Called by the native cached recipe's post-finish callback. */
    public static void infuserFinished(TileEntityMekanism machine,
            ItemStackChemicalToItemStackRecipe recipe) {
        if (!supportedMachine(machine)) return;
        List<ItemStack> outputs = recipe.getOutputDefinition();
        if (outputs.size() != 1 || outputs.getFirst().isEmpty()) return;
        ItemStack output = outputs.getFirst();
        if (!isTrackedOutput(output)) return;
        // Only unambiguous fixed outputs are supported. Reject any same-item
        // ingredient alternative, even if a dynamic recipe used another input.
        for (ItemStack input : recipe.getItemInput().getRepresentations()) {
            if (!input.isEmpty() && input.is(output.getItem())) return;
        }
        machineFinished(machine, machine.getSecurity().getOwnerUUID(), output);
    }

    /** The native tier upgrade has succeeded and consumed its upgrade item. */
    public static void storageUpgraded(Player player, Level level, BlockPos pos, BlockState before) {
        if (!realSurvivalPlayer(player) || !(level instanceof ServerLevel serverLevel)) return;
        ItemStack output = new ItemStack(level.getBlockState(pos).getBlock().asItem());
        if (!isTrackedOutput(output) || output.is(before.getBlock().asItem())) return;
        TeamManager manager = manager();
        if (manager == null) return;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        rememberActor(serverPlayer, true);
        manager.getTeamForPlayerID(player.getUUID()).ifPresent(team ->
                record(serverLevel.getServer(), player.getUUID(), team.getId(), output));
    }

    static void joined(ServerPlayer player) {
        TeamManager manager = manager();
        if (manager == null) return;
        Team personal = manager.getPlayerTeamForPlayerID(player.getUUID()).orElse(null);
        Team current = manager.getTeamForPlayerID(player.getUUID()).orElse(null);
        if (current != null && current.isPartyTeam()) {
            Set<String> own = new TreeSet<>();
            collect(personal, ownKey(personal), own);
            own.forEach(id -> add(current, unionKey(current), id));
        }
        dirtyQuestTeams(player.getUUID());
    }

    /** FTB's join/leave events run after its native progress/claim merge. */
    static void dirtyQuestTeams(UUID playerId) {
        TeamManager manager = manager();
        if (manager == null || ServerQuestFile.INSTANCE == null) return;
        Set<UUID> seen = new java.util.HashSet<>();
        manager.getTeamForPlayerID(playerId).ifPresent(team -> dirtyQuestTeam(team, seen));
        manager.getPlayerTeamForPlayerID(playerId).ifPresent(team -> dirtyQuestTeam(team, seen));
    }

    private static void dirtyQuestTeam(Team team, Set<UUID> seen) {
        if (!seen.add(team.getId())) return;
        var data = ServerQuestFile.INSTANCE.getOrCreateTeamData(team);
        data.clearCachedProgress();
        data.markDirty();
    }
}
