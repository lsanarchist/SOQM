package soqm.validation;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.ids.AEComponents;
import appeng.api.inventories.InternalInventory;
import appeng.blockentity.crafting.MolecularAssemblerBlockEntity;
import appeng.blockentity.misc.InscriberBlockEntity;
import appeng.recipes.handlers.InscriberRecipe;
import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import dev.ftb.mods.ftbteams.data.TeamManagerImpl;
import ironfurnaces.items.upgrades.ItemUpgrade;
import io.netty.channel.embedded.EmbeddedChannel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityItemStackChemicalToItemStackFactory;
import mekanism.common.tile.machine.TileEntityMetallurgicInfuser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageTierUpgradeItem;
import qm.rewards.ProductionEvidence;

/** Test-only native manufacture fixtures. Never writes the production ledger. */
public final class ProductionEvidenceProbe {
    private static final String CHASSIS = "kubejs:industrial_chassis";
    private static final String CHASSIS_RECIPE = "kubejs:expert/kubejs/industrial_chassis";
    private static final String PRECISION = "kubejs:precision_mechanism";
    private static final String PRECISION_RECIPE = "kubejs:expert/kubejs/precision_mechanism";
    private static final BlockPos ORIGIN = new BlockPos(48, 200, 48);
    private static ServerPlayer producer, member, denied;
    private static InscriberBlockEntity inscriber, renamer, creativePlaced;
    private static MolecularAssemblerBlockEntity assembler;
    private static TileEntityMetallurgicInfuser infuser;
    private static TileEntityItemStackChemicalToItemStackFactory factory;
    private static PartyTeam party;
    private static int stage, began;
    private static Set<String> beforeReload;
    private static Set<String> trackedBeforeReload;

    private ProductionEvidenceProbe() {}

    public static ServerPlayer preparedPlayer() { return producer; }
    public static ServerPlayer getSecondPlayer() { return member; }

    /** Shared with the reward fixture: supplied materials, real native output take. */
    public static ItemStack craft(ServerPlayer player, String recipeId) {
        RecipeHolder<CraftingRecipe> recipe = craftingRecipe(player.server, recipeId);
        ItemStack[] inputs = ingredients(recipe);
        AbstractContainerMenu previous = player.containerMenu;
        CraftingMenu menu = new CraftingMenu(700, player.getInventory(),
                ContainerLevelAccess.create(player.level(), player.blockPosition()));
        player.containerMenu = menu;
        try {
            menu.beginPlacingRecipe();
            for (int i = 0; i < inputs.length; i++) menu.getSlot(i + 1).set(inputs[i].copy());
            check(menu.recipeMatches(recipe), "Fixture ingredients match active recipe " + recipeId);
            menu.finishPlacingRecipe(recipe);
            ResultSlot slot = (ResultSlot) menu.getSlot(0);
            ItemStack expected = recipe.value().assemble(CraftingInput.of(3, 3, Arrays.asList(inputs)),
                    player.server.registryAccess());
            check(!expected.isEmpty() && ItemStack.isSameItemSameComponents(slot.getItem(), expected),
                    "Native result slot resolves " + recipeId);
            ItemStack crafted = slot.remove(slot.getItem().getCount());
            check(!crafted.isEmpty(), "Native output removal has a positive crafted delta");
            slot.onTake(player, crafted);
            int remaining = 0;
            for (int i = 1; i <= 9; i++) remaining += menu.getSlot(i).getItem().getCount();
            check(remaining < Arrays.stream(inputs).mapToInt(ItemStack::getCount).sum(),
                    "Native crafting consumes supplied ingredients");
            return crafted;
        } finally { player.containerMenu = previous; }
    }

    /**
     * Server-only fixture transport: discard outbound UI packets without claiming
     * that a client negotiated their channels or acknowledged their delivery.
     */
    public static ServerGamePacketListenerImpl listener(MinecraftServer server, Connection connection,
            ServerPlayer player, GameProfile profile) {
        return new ServerGamePacketListenerImpl(server, connection, player,
                CommonListenerCookie.createInitial(profile, false)) {
            @Override public void send(Packet<?> packet) { }
            @Override public void send(Packet<?> packet, PacketSendListener sendListener) { }
        };
    }

    public static ServerPlayer createPlayer(MinecraftServer server, UUID id, String name) {
        GameProfile profile = new GameProfile(id, name);
        ServerPlayer player = new ServerPlayer(server, server.overworld(), profile, ClientInformation.createDefault());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        // A real in-memory channel lets native NeoForge/FTB hasChannel checks
        // return false for absent client capabilities instead of dereferencing
        // a null transport. No client handshake or reward channel is fabricated.
        new EmbeddedChannel(connection);
        player.connection = listener(server, connection, player, profile);
        player.setPos(ORIGIN.getX() + 0.5, ORIGIN.getY(), ORIGIN.getZ() - 3.5);
        player.setGameMode(GameType.SURVIVAL);
        ((TeamManagerImpl) FTBTeamsAPI.api().getManager()).playerLoggedIn(player, id, name);
        NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(player));
        return player;
    }

    public static void begin(MinecraftServer server, Map<String, Object> results) throws Exception {
        check(!ProductionEvidence.getTrackedItems().isEmpty(), "Native production policy is loaded");
        for (String id : ProductionEvidence.getTrackedItems()) {
            check(BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(id)) && !item(id).isEmpty(),
                    "Every production policy item exists: " + id);
        }
        results.put("production_registry_items_validated", ProductionEvidence.getTrackedItems().size());
        verifyPolicyFilter(results, "production_identity_filter_registry_items");
        producer = createPlayer(server, UUID.fromString("c87e6889-3d5a-44d0-adb9-0bd04d9d4d01"), "SOQMProducer");
        member = createPlayer(server, UUID.fromString("c87e6889-3d5a-44d0-adb9-0bd04d9d4d02"), "SOQMProdMember");
        denied = createPlayer(server, UUID.fromString("c87e6889-3d5a-44d0-adb9-0bd04d9d4d03"), "SOQMProdDenied");
        check(ProductionEvidence.getProducedItems(producer).isEmpty(), "Fresh player has no manufacture facts");
        producer.addItem(item(CHASSIS));
        producer.awardRecipes(List.of(craftingRecipe(server, CHASSIS_RECIPE)));
        ItemEntity pickup = new ItemEntity(server.overworld(), producer.getX(), producer.getY(), producer.getZ(), item(CHASSIS));
        pickup.setNoPickUpDelay();
        server.overworld().addFreshEntity(pickup);
        pickup.playerTouch(producer);
        check(!ProductionEvidence.hasProduced(producer, CHASSIS), "Inventory, pickup and recipe book are not production");
        craft(producer, "mob_grinding_utils:recipe_solidifier_reset");
        check(!ProductionEvidence.hasProduced(producer, "mob_grinding_utils:xpsolidifier"),
                "A real same-item reset craft cannot launder an existing item");
        denied.setGameMode(GameType.CREATIVE);
        craft(denied, CHASSIS_RECIPE);
        check(!ProductionEvidence.hasProduced(denied, CHASSIS), "Creative native craft is rejected");
        var fake = FakePlayerFactory.get(server.overworld(), new GameProfile(
                UUID.fromString("c87e6889-3d5a-44d0-adb9-0bd04d9d4d04"), "SOQMProdFake"));
        EventHooks.firePlayerCraftingEvent(fake, item(CHASSIS), new SimpleContainer(ingredients(craftingRecipe(server, CHASSIS_RECIPE))));
        check(!ProductionEvidence.hasProduced(fake, CHASSIS), "FakePlayer crafting event is rejected");
        craft(producer, CHASSIS_RECIPE);
        check(ProductionEvidence.hasProduced(producer, CHASSIS), "Actual grid craft records custom chassis");
        check(!ProductionEvidence.hasProduced(producer, "ae2:calculation_processor"),
                "Supplying a recipe ingredient never records that ingredient as manufactured");
        results.put("production_manual_grid_and_custom_chassis", true);
        results.put("production_inventory_pickup_recipe_book_creative_fake_reset_rejected", true);

        inscriber = (InscriberBlockEntity) placeBy(producer, ORIGIN, "ae2:inscriber");
        fillInscriber(server, inscriber, "logic_processor");
        inscriber.getInternalInventory().setItemDirect(3, new ItemStack(Items.COBBLESTONE, 64));
        assembler = (MolecularAssemblerBlockEntity) placeBy(producer, ORIGIN.east(4), "ae2:molecular_assembler");
        fillAssembler(server, assembler, PRECISION_RECIPE);
        assembler.getSubInventory(MolecularAssemblerBlockEntity.INV_MAIN).setItemDirect(9, item("kubejs:dimensional_matrix"));
        infuser = (TileEntityMetallurgicInfuser) placeBy(producer, ORIGIN.south(4), "mekanism:metallurgic_infuser");
        fillMekanism(server, infuser, "mekanism:control_circuit/basic");
        factory = (TileEntityItemStackChemicalToItemStackFactory) placeBy(producer, ORIGIN.east(4).south(4),
                "mekanism:basic_infusing_factory");
        fillMekanism(server, factory, "mekanism:metallurgic_infusing/alloy/infused");
        creativePlaced = (InscriberBlockEntity) placeBy(denied, ORIGIN.west(4), "ae2:inscriber");
        fillInscriber(server, creativePlaced, "logic_processor");
        denied.setGameMode(GameType.SURVIVAL);
        renamer = (InscriberBlockEntity) placeBy(denied, ORIGIN.west(8), "ae2:inscriber");
        ItemStack press = item("ae2:name_press");
        press.set(AEComponents.NAME_PRESS_NAME, Component.literal("not manufactured"));
        renamer.getInternalInventory().setItemDirect(0, press);
        renamer.getInternalInventory().setItemDirect(2, item("ae2:engineering_processor"));
        check(renamer.getTask() != null
                && renamer.getTask().getResultItem().has(DataComponents.CUSTOM_NAME),
                "Native Name Press recipe resolves the press's AE2 name component");
        testStorageUpgrade(server, results);
        testIronFurnacesUpgrade(server, results);
        began = server.getTickCount();
        stage = 0;
    }

    public static boolean tick(MinecraftServer server, Map<String, Object> results) throws Exception {
        int elapsed = server.getTickCount() - began;
        if (elapsed > 1200) throw new AssertionError("Native production fixture timed out at stage " + stage);
        if (stage == 0 && elapsed >= 40) {
            check(!ProductionEvidence.hasProduced(producer, "ae2:logic_processor")
                    && !ProductionEvidence.hasProduced(producer, PRECISION)
                    && !ProductionEvidence.hasProduced(producer, "mekanism:basic_control_circuit")
                    && !ProductionEvidence.hasProduced(producer, "mekanism:alloy_infused"),
                    "Unpowered/blocked machines cannot establish production");
            check(!ProductionEvidence.hasProduced(producer, "kubejs:dimensional_matrix"),
                    "An item in assembler output storage is not a new craft");
            results.put("production_unpowered_blocked_and_stored_output_rejected", true);
            inscriber.getInternalInventory().setItemDirect(3, ItemStack.EMPTY);
            assembler.getSubInventory(MolecularAssemblerBlockEntity.INV_MAIN).setItemDirect(9, ItemStack.EMPTY);
            for (BlockEntity machine : List.of(inscriber, assembler, renamer, creativePlaced)) {
                BlockPos powerPos = machine instanceof InscriberBlockEntity press
                        ? machine.getBlockPos().relative(press.getFront().getOpposite())
                        : machine.getBlockPos().north();
                place(server.overworld(), powerPos, "ae2:creative_energy_cell");
            }
            charge(infuser); charge(factory);
            stage = 1;
        } else if (stage == 1 && hasAll(producer, "ae2:logic_processor", PRECISION,
                "mekanism:basic_control_circuit", "mekanism:alloy_infused")
                && !renamer.getInternalInventory().getStackInSlot(3).isEmpty()
                && !creativePlaced.getInternalInventory().getStackInSlot(3).isEmpty()) {
            check(inscriber.getInternalInventory().getStackInSlot(2).isEmpty(), "Inscriber consumed middle ingredient");
            check(assembler.getSubInventory(MolecularAssemblerBlockEntity.INV_MAIN).getStackInSlot(9).is(item(PRECISION).getItem()),
                    "Assembler retains an actually manufactured output");
            check(!ProductionEvidence.hasProduced(denied, "ae2:logic_processor"),
                    "A creative-placed machine stays ineligible after its owner switches to survival");
            results.put("production_ae2_inscriber_and_assembler_actual_finishes", true);
            results.put("production_mekanism_infuser_and_infusing_factory_actual_finishes", true);
            ItemStack renamed = renamer.getInternalInventory().getStackInSlot(3);
            check(renamer.getInternalInventory().getStackInSlot(2).isEmpty()
                    && renamed.is(item("ae2:engineering_processor").getItem())
                    && renamed.has(DataComponents.CUSTOM_NAME)
                    && "not manufactured".equals(renamed.get(DataComponents.CUSTOM_NAME).getString()),
                    "Native Name Press consumes the original and returns the named same-item result: "
                            + renamed.getComponents());
            check(!ProductionEvidence.hasProduced(denied, "ae2:engineering_processor"),
                    "Native successful Name Press operation is not manufacture; facts="
                            + ProductionEvidence.getProducedItems(denied));
            results.put("production_native_name_press_and_creative_placement_rejected", true);
            testTeamMembership(server, results);
            // This machine captured the old solo team at placement. Its owner has
            // now created a party; its next actual finish must not credit it.
            fillInscriber(server, inscriber, "calculation_processor");
            stage = 2;
        } else if (stage == 2 && inscriber.getInternalInventory().getStackInSlot(3)
                .is(item("ae2:calculation_processor").getItem())) {
            check(!ProductionEvidence.hasProduced(producer, "ae2:calculation_processor"),
                    "An old team's machine cannot credit the producer's new team");
            results.put("production_machine_captured_team_blocks_cross_team_unlock", true);
            CompoundTag saved = inscriber.saveWithFullMetadata(server.registryAccess());
            BlockEntity restored = BlockEntity.loadStatic(inscriber.getBlockPos(), inscriber.getBlockState(), saved,
                    server.registryAccess());
            check(restored != null && restored.getPersistentData().equals(inscriber.getPersistentData()),
                    "Machine producer/team binding survives native block-entity NBT");
            results.put("production_machine_owner_native_nbt_roundtrip", true);
            server.overworld().removeBlock(ORIGIN, false);
            inscriber = (InscriberBlockEntity) placeBy(producer, ORIGIN, "ae2:inscriber");
            fillInscriber(server, inscriber, "engineering_processor");
            stage = 3;
        } else if (stage == 3 && ProductionEvidence.hasProduced(producer, "ae2:engineering_processor")) {
            check(!ProductionEvidence.hasProduced(producer, "ae2:calculation_processor"),
                    "Re-placement cannot retroactively manufacture the prior rejected output");
            results.put("production_survival_replacement_rebinds_machine", true);
            beforeReload = Set.copyOf(ProductionEvidence.getProducedItems(producer));
            trackedBeforeReload = Set.copyOf(ProductionEvidence.getTrackedItems());
            try {
                ProductionEvidence.getProducedItems(producer).add("minecraft:diamond");
                throw new AssertionError("Production API exposes a mutable ledger");
            } catch (UnsupportedOperationException expected) { /* Required read-only API. */ }
            verifyNativeDisk(server, results);
            results.put("production_effective_api_immutable", true);
            results.put("production_effective_items_before_reload", new ArrayList<>(beforeReload));
            stage = 4;
            return true;
        }
        return stage == 4;
    }

    public static void verifyReload(MinecraftServer server, Map<String, Object> results) {
        check(beforeReload != null && beforeReload.equals(ProductionEvidence.getProducedItems(producer)),
                "Actual production evidence survives resource reload");
        check(trackedBeforeReload.equals(ProductionEvidence.getTrackedItems()), "Native policy reload retains its exact allowlist");
        verifyPolicyFilter(results, "production_identity_filter_registry_items_after_reload");
        verifyNativeDisk(server, results);
        results.put("production_policy_and_evidence_reload_persistence", true);
    }

    private static void verifyPolicyFilter(Map<String, Object> results, String key) {
        Set<String> tracked = ProductionEvidence.getTrackedItems();
        int compared = 0;
        for (var registered : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(registered);
            String id = BuiltInRegistries.ITEM.getKey(registered).toString();
            check(ProductionEvidence.isTrackedOutput(stack) == (!stack.isEmpty() && tracked.contains(id)),
                    "Item identity filter equals the exact live policy for " + id);
            compared++;
        }
        check(!ProductionEvidence.isTrackedOutput(ItemStack.EMPTY), "Empty outputs never enter the observer");
        // The untracked path must finish before consulting any node/owner state.
        check(!ProductionEvidence.isTrackedOutput(item("minecraft:dirt")), "Negative-control output is untracked");
        ProductionEvidence.ae2Finished(null, null, item("minecraft:dirt"));
        results.put(key, compared);
    }

    private static void testTeamMembership(MinecraftServer server, Map<String, Object> results) throws Exception {
        TeamManagerImpl manager = (TeamManagerImpl) FTBTeamsAPI.api().getManager();
        party = manager.createParty(producer, "Production fixture");
        party.invite(producer, List.of(member.getGameProfile()));
        party.join(member);
        check(ProductionEvidence.hasProduced(member, CHASSIS), "Late joiner reads current party discoveries");
        craft(member, "kubejs:expert/kubejs/dimensional_matrix");
        check(ProductionEvidence.hasProduced(producer, "kubejs:dimensional_matrix"), "Member's real craft extends party union");
        party.leave(member.getUUID());
        check(!ProductionEvidence.hasProduced(member, CHASSIS)
                && ProductionEvidence.hasProduced(member, "kubejs:dimensional_matrix"),
                "Leaving retains own manufacture without copying party discoveries");
        check(ProductionEvidence.hasProduced(producer, "kubejs:dimensional_matrix"), "Party keeps its production history");
        manager.createParty(member, "Isolated production fixture");
        check(!ProductionEvidence.hasProduced(member, CHASSIS)
                && ProductionEvidence.hasProduced(member, "kubejs:dimensional_matrix"),
                "FTB extraData copy cannot unlock an unrelated new party");
        results.put("production_party_join_leave_own_union_and_new_party_isolation", true);
    }

    private static void testStorageUpgrade(MinecraftServer server, Map<String, Object> results) {
        BlockPos pos = ORIGIN.south(8);
        place(server.overworld(), pos, "sophisticatedstorage:barrel");
        ItemStack upgrade = item("sophisticatedstorage:basic_to_iron_tier_upgrade");
        StorageTierUpgradeItem type = (StorageTierUpgradeItem) upgrade.getItem();
        check(!ProductionEvidence.hasProduced(producer, "sophisticatedstorage:iron_barrel"), "Unmade barrel starts locked");
        ItemStack missing = upgrade.copyWithCount(0);
        type.tryUpgradeStorage(missing, server.overworld(), pos, server.overworld().getBlockState(pos), producer);
        check(!ProductionEvidence.hasProduced(producer, "sophisticatedstorage:iron_barrel"), "Insufficient upgrade items are not proof");
        var result = type.tryUpgradeStorage(upgrade, server.overworld(), pos, server.overworld().getBlockState(pos), producer);
        check(result.consumesAction() && upgrade.isEmpty()
                && server.overworld().getBlockState(pos).getBlock().asItem() == item("sophisticatedstorage:iron_barrel").getItem(),
                "Native in-world upgrade consumes its item and changes the barrel tier");
        check(ProductionEvidence.hasProduced(producer, "sophisticatedstorage:iron_barrel"), "In-world tier manufacture is recorded");
        results.put("production_sophisticated_in_world_tier_upgrade_actual_consumption", true);
    }

    private static void testIronFurnacesUpgrade(MinecraftServer server, Map<String, Object> results) {
        ServerLevel level = server.overworld();
        BlockPos pos = ORIGIN.south(12);
        String output = "ironfurnaces:netherite_furnace";
        ItemUpgrade type = (ItemUpgrade) item("ironfurnaces:upgrade_netherite").getItem();
        check(ProductionEvidence.isTrackedOutput(item(output)), "World-upgrade output belongs to the exact production policy");
        check(!ProductionEvidence.hasProduced(producer, output), "Unmade netherite furnace starts locked");
        for (String existing : List.of("ironfurnaces:iron_furnace", output)) {
            place(level, pos, existing);
            ItemStack upgrade = item("ironfurnaces:upgrade_netherite");
            producer.setItemInHand(InteractionHand.MAIN_HAND, upgrade);
            type.useOn(furnaceUpgradeContext(producer, pos));
            check(upgrade.getCount() == 1 && level.getBlockState(pos).getBlock().asItem() == item(existing).getItem()
                    && !ProductionEvidence.hasProduced(producer, output),
                    "Wrong input tier or already-identical output cannot establish manufacture: " + existing);
        }
        // Native ItemUpgrade does not guard empty/wrong held items itself. Invoke
        // the actual method to prove our observer requires its own consumed item.
        for (ItemStack invalid : List.of(ItemStack.EMPTY, new ItemStack(Items.COBBLESTONE))) {
            place(level, pos, "ironfurnaces:obsidian_furnace");
            producer.setItemInHand(InteractionHand.MAIN_HAND, invalid);
            type.useOn(furnaceUpgradeContext(producer, pos));
            check(!ProductionEvidence.hasProduced(producer, output),
                    "No consumed upgrade item means no evidence even if native code replaces the block");
        }
        place(level, pos, "ironfurnaces:obsidian_furnace");
        ItemStack creativeUpgrade = item("ironfurnaces:upgrade_netherite");
        denied.setGameMode(GameType.CREATIVE);
        denied.setItemInHand(InteractionHand.MAIN_HAND, creativeUpgrade);
        type.useOn(furnaceUpgradeContext(denied, pos));
        check(creativeUpgrade.getCount() == 1 && !ProductionEvidence.hasProduced(denied, output),
                "Creative replacement without consumption remains ineligible");
        denied.setGameMode(GameType.SURVIVAL);

        var fake = FakePlayerFactory.get(level, new GameProfile(
                UUID.fromString("c87e6889-3d5a-44d0-adb9-0bd04d9d4d05"), "SOQMFurnaceFake"));
        fake.setGameMode(GameType.SURVIVAL);
        place(level, pos, "ironfurnaces:obsidian_furnace");
        ItemStack fakeUpgrade = item("ironfurnaces:upgrade_netherite");
        fake.setItemInHand(InteractionHand.MAIN_HAND, fakeUpgrade);
        type.useOn(furnaceUpgradeContext(fake, pos));
        check(fakeUpgrade.isEmpty() && !ProductionEvidence.hasProduced(fake, output),
                "FakePlayer actual world-upgrade consumption never records manufacture");

        place(level, pos, "ironfurnaces:obsidian_furnace");
        ItemStack upgrade = item("ironfurnaces:upgrade_netherite");
        producer.setItemInHand(InteractionHand.MAIN_HAND, upgrade);
        type.useOn(furnaceUpgradeContext(producer, pos));
        // Pinned ItemUpgrade returns PASS via super.useOn even on this success.
        check(upgrade.isEmpty() && level.getBlockState(pos).getBlock().asItem() == item(output).getItem(),
                "Native survival upgrade consumes its item and replaces obsidian with netherite furnace");
        check(ProductionEvidence.hasProduced(producer, output), "Actual placed-furnace upgrade records its exact output");
        results.put("production_ironfurnaces_world_upgrade_actual_consumption", true);
        results.put("production_ironfurnaces_wrong_tier_same_output_empty_wrong_item_creative_fake_rejected", true);
    }

    private static UseOnContext furnaceUpgradeContext(ServerPlayer player, BlockPos pos) {
        return new UseOnContext(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }

    private static void verifyNativeDisk(MinecraftServer server, Map<String, Object> results) {
        TeamManagerImpl live = (TeamManagerImpl) FTBTeamsAPI.api().getManager();
        live.saveNow();
        TeamManagerImpl restored = new TeamManagerImpl(server);
        restored.load();
        for (ServerPlayer player : List.of(producer, member, denied)) {
            Team personal = live.getPlayerTeamForPlayerID(player.getUUID()).orElseThrow();
            Team readPersonal = restored.getPlayerTeamForPlayerID(player.getUUID()).orElseThrow();
            check(personal.getExtraData().equals(readPersonal.getExtraData()), "Native personal evidence disk round trip");
            Team current = live.getTeamForPlayerID(player.getUUID()).orElseThrow();
            Team readCurrent = restored.getTeamForPlayerID(player.getUUID()).orElseThrow();
            check(current.getId().equals(readCurrent.getId()) && current.getExtraData().equals(readCurrent.getExtraData()),
                    "Native current-team membership and union disk round trip");
        }
        results.put("production_ftb_native_disk_roundtrip_players", 3);
    }

    @SuppressWarnings("unchecked")
    private static RecipeHolder<CraftingRecipe> craftingRecipe(MinecraftServer server, String id) {
        RecipeHolder<?> recipe = server.getRecipeManager().byKey(ResourceLocation.parse(id)).orElseThrow();
        check(recipe.value() instanceof CraftingRecipe, "Fixture uses a real crafting-grid recipe: " + id);
        return (RecipeHolder<CraftingRecipe>) recipe;
    }

    private static ItemStack[] ingredients(RecipeHolder<CraftingRecipe> holder) {
        var recipe = holder.value();
        var ingredients = recipe.getIngredients();
        ItemStack[] input = new ItemStack[9];
        Arrays.fill(input, ItemStack.EMPTY);
        int width = recipe instanceof ShapedRecipe shaped ? shaped.getWidth() : 3;
        for (int i = 0; i < ingredients.size(); i++) input[(i / width) * 3 + i % width] = first(ingredients.get(i));
        return input;
    }

    private static ItemStack first(Ingredient ingredient) {
        return ingredient == null || ingredient.isEmpty() ? ItemStack.EMPTY : ingredient.getItems()[0].copyWithCount(1);
    }

    private static void fillInscriber(MinecraftServer server, InscriberBlockEntity machine, String processor) {
        InscriberRecipe recipe = (InscriberRecipe) server.getRecipeManager()
                .byKey(ResourceLocation.parse("ae2:inscriber/" + processor)).orElseThrow().value();
        InternalInventory inventory = machine.getInternalInventory();
        inventory.clear();
        inventory.setItemDirect(0, first(recipe.getTopOptional()));
        inventory.setItemDirect(1, first(recipe.getBottomOptional()));
        inventory.setItemDirect(2, first(recipe.getMiddleInput()));
        check(machine.getTask() != null && machine.getTask().getResultItem().is(recipe.getResultItem().getItem()),
                "Native Inscriber recipe resolves " + processor);
    }

    private static void fillAssembler(MinecraftServer server, MolecularAssemblerBlockEntity machine, String id) {
        var recipe = craftingRecipe(server, id);
        ItemStack[] ingredients = ingredients(recipe);
        ItemStack output = recipe.value().assemble(CraftingInput.of(3, 3, Arrays.asList(ingredients)), server.registryAccess());
        ItemStack pattern = PatternDetailsHelper.encodeCraftingPattern(recipe, ingredients, output, false, false);
        check(PatternDetailsHelper.decodePattern(pattern, server.overworld()) != null, "Native AE2 crafting pattern decodes");
        InternalInventory inventory = machine.getSubInventory(MolecularAssemblerBlockEntity.INV_MAIN);
        inventory.setItemDirect(10, pattern);
        for (int i = 0; i < ingredients.length; i++) inventory.setItemDirect(i, ingredients[i].copy());
    }

    @SuppressWarnings("unchecked")
    private static void fillMekanism(MinecraftServer server, TileEntityMekanism machine, String id) throws Exception {
        ItemStackChemicalToItemStackRecipe recipe = (ItemStackChemicalToItemStackRecipe) server.getRecipeManager()
                .byKey(ResourceLocation.parse(id)).orElseThrow().value();
        if (machine instanceof TileEntityMetallurgicInfuser) {
            ((IInventorySlot) field(machine, "inputSlot")).setStack(recipe.getItemInput().getRepresentations().getFirst().copy());
        } else {
            ((List<IInventorySlot>) field(machine, "inputSlots")).getFirst()
                    .setStack(recipe.getItemInput().getRepresentations().getFirst().copy());
        }
        machine.getChemicalTanks(null).getFirst().setStack(recipe.getChemicalInput().getRepresentations().getFirst().copy());
    }

    private static Object field(Object instance, String name) throws Exception {
        for (Class<?> type = instance.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(instance);
            } catch (NoSuchFieldException absent) { /* Continue through native superclass fields. */ }
        }
        throw new NoSuchFieldException(name);
    }

    private static void charge(TileEntityMekanism machine) {
        machine.getEnergyContainers(null).forEach(energy -> energy.setEnergy(energy.getMaxEnergy()));
    }

    private static BlockEntity placeBy(ServerPlayer player, BlockPos pos, String id) {
        ServerLevel level = player.serverLevel();
        level.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, true);
        level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
        ItemStack block = item(id);
        player.setItemInHand(InteractionHand.MAIN_HAND, block);
        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos.below()), Direction.UP, pos.below(), false));
        check(block.useOn(context).consumesAction(), "Native block placement succeeds: " + id);
        BlockEntity result = level.getBlockEntity(pos);
        check(result != null, "Native placement creates machine block entity: " + id);
        return result;
    }

    private static void place(ServerLevel level, BlockPos pos, String id) {
        level.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, true);
        level.setBlockAndUpdate(pos, BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id)).defaultBlockState());
    }

    private static ItemStack item(String id) {
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)));
    }

    private static boolean hasAll(ServerPlayer player, String... items) {
        return Arrays.stream(items).allMatch(id -> ProductionEvidence.hasProduced(player, id));
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
