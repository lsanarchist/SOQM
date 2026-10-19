package soqm.validation;

import java.util.List;
import java.util.Map;
import mekanism.common.item.block.ItemBlockCardboardBox;
import mekanism.common.tags.MekanismTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Uses the live data-pack tag and Mekanism's real capture/unpack interaction. */
final class CardboardTransportProbe {
    private static final List<String> CPU_BLOCKS = List.of(
            "ae2:crafting_unit", "ae2:crafting_accelerator", "ae2:crafting_monitor",
            "ae2:1k_crafting_storage", "ae2:4k_crafting_storage", "ae2:16k_crafting_storage",
            "ae2:64k_crafting_storage", "ae2:256k_crafting_storage");

    static void run(MinecraftServer server, ServerPlayer player, Map<String, Object> results) {
        ServerLevel level = server.overworld();
        BlockPos spawn = level.getSharedSpawnPos();
        // Keep the positive controls outside vanilla's spawn-protection radius.
        BlockPos origin = new BlockPos(spawn.getX() + 64, 200, spawn.getZ() + 64);
        Vec3 previousPosition = player.position();
        ItemStack previousHand = player.getMainHandItem().copy();
        boolean previousSneaking = player.isShiftKeyDown();
        var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse("mekanism:cardboard_box"));
        check(item instanceof ItemBlockCardboardBox, "Pinned native cardboard-box item is loaded");
        ItemBlockCardboardBox box = (ItemBlockCardboardBox) item;
        try {
            player.setShiftKeyDown(false);
            player.setPos(origin.getX() + 0.5, origin.getY(), origin.getZ() - 2);
            level.getChunkAt(origin);
            for (int i = 0; i < CPU_BLOCKS.size(); i++) {
                String id = CPU_BLOCKS.get(i);
                BlockPos pos = origin.offset(i * 2, 0, 0);
                BlockState state = registeredBlock(id).defaultBlockState();
                check(state.is(MekanismTags.Blocks.CARDBOARD_BLACKLIST), "Live blacklist contains " + id);
                level.setBlockAndUpdate(pos, state);
                var originalEntity = level.getBlockEntity(pos);
                ItemStack stack = new ItemStack(box);
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                // Capturing is implemented in onItemUseFirst, before ordinary useOn placement.
                InteractionResult interaction = box.onItemUseFirst(stack, context(player, pos));
                check(interaction == InteractionResult.FAIL, "Native capture refuses " + id);
                check(level.getBlockState(pos).equals(state), "Refused CPU block remains intact: " + id);
                check(level.getBlockEntity(pos) == originalEntity, "Refusal preserves the CPU block entity: " + id);
                check(stack.getCount() == 1, "Refused capture does not consume a box: " + id);
            }
            for (Block protectedBlock : List.of(Blocks.OAK_DOOR, Blocks.RED_BED, Blocks.TRIAL_SPAWNER, Blocks.VAULT)) {
                check(protectedBlock.defaultBlockState().is(MekanismTags.Blocks.CARDBOARD_BLACKLIST),
                        "Merged tag retains upstream protection: " + BuiltInRegistries.BLOCK.getKey(protectedBlock));
            }
            results.put("cardboard_native_cpu_capture_rejections", CPU_BLOCKS.size());
            results.put("cardboard_native_blacklist_merges_upstream", true);

            BlockPos chestPos = origin.south(4);
            level.setBlockAndUpdate(chestPos, Blocks.CHEST.defaultBlockState());
            ChestBlockEntity chest = (ChestBlockEntity) level.getBlockEntity(chestPos);
            chest.setItem(0, new ItemStack(Items.IRON_INGOT, 23));
            chest.setItem(17, new ItemStack(Items.DIRT, 7));
            captureAndUnpack(level, player, box, chestPos);
            check(level.getBlockEntity(chestPos) instanceof ChestBlockEntity, "Ordinary chest is restored");
            ChestBlockEntity restored = (ChestBlockEntity) level.getBlockEntity(chestPos);
            check(restored.getItem(0).is(Items.IRON_INGOT) && restored.getItem(0).getCount() == 23,
                    "Boxing and unpacking preserves the first chest stack");
            check(restored.getItem(17).is(Items.DIRT) && restored.getItem(17).getCount() == 7,
                    "Boxing and unpacking preserves another chest slot");
            results.put("cardboard_native_chest_inventory_preserved", true);

            BlockPos quartzPos = origin.south(6);
            level.setBlockAndUpdate(quartzPos, registeredBlock("ae2:flawless_budding_quartz").defaultBlockState());
            captureAndUnpack(level, player, box, quartzPos);
            results.put("cardboard_native_flawless_budding_quartz_preserved", true);
        } finally {
            for (int i = 0; i < CPU_BLOCKS.size(); i++) level.removeBlock(origin.offset(i * 2, 0, 0), false);
            level.removeBlock(origin.south(4), false);
            level.removeBlock(origin.south(6), false);
            player.setItemInHand(InteractionHand.MAIN_HAND, previousHand);
            player.setShiftKeyDown(previousSneaking);
            player.setPos(previousPosition.x, previousPosition.y, previousPosition.z);
        }
    }

    private static void captureAndUnpack(ServerLevel level, ServerPlayer player,
                                         ItemBlockCardboardBox box, BlockPos pos) {
        BlockState original = level.getBlockState(pos);
        check(!original.is(MekanismTags.Blocks.CARDBOARD_BLACKLIST), "Ordinary transport target remains permitted");
        player.setShiftKeyDown(false);
        ItemStack stack = new ItemStack(box);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        check(box.onItemUseFirst(stack, context(player, pos)) == InteractionResult.SUCCESS,
                "Native cardboard capture succeeds for permitted block");
        check(stack.isEmpty() && level.getBlockState(pos).is(box.getBlock()), "Capture creates a packed box");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setShiftKeyDown(true);
        check(level.getBlockState(pos).useWithoutItem(level, player, hit(pos)).consumesAction(),
                "Native sneak interaction unpacks the box");
        player.setShiftKeyDown(false);
        check(level.getBlockState(pos).equals(original), "Unpacking preserves the original block state");
    }

    private static Block registeredBlock(String id) {
        ResourceLocation key = ResourceLocation.parse(id);
        check(BuiltInRegistries.BLOCK.containsKey(key), "Transport fixture block exists: " + id);
        return BuiltInRegistries.BLOCK.get(key);
    }

    private static UseOnContext context(ServerPlayer player, BlockPos pos) {
        return new UseOnContext(player, InteractionHand.MAIN_HAND, hit(pos));
    }

    private static BlockHitResult hit(BlockPos pos) {
        return new BlockHitResult(pos.getCenter(), Direction.UP, pos, false);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
