package qm.dungeonencounters;

import com.catastrophe573.dimdungeons.item.BaseItemKey;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/** Exact policies for the two authored, finite expedition themes. */
public final class QmExpeditions {
    public static final int CITADEL = 4;
    public static final int ABYSS = 5;
    public static final String TABLE_PREFIX = "qm:chest/dimensional_dungeons/";
    private QmExpeditions() {}

    public static boolean isExpedition(int theme) { return theme == CITADEL || theme == ABYSS; }

    /** Native random theme keys keep the original three themes. New keys are earned in treasure. */
    public static int randomThemeCount(int configured) { return Math.min(3, configured); }

    /** Used only by native portal validation/diagnostics, never by the saved dungeon type. */
    public static int portalLevel(BaseItemKey key, ItemStack stack) {
        return key instanceof ItemPortalKey && key.isActivated(stack) && isExpedition(key.getDungeonTheme(stack))
            ? 2 : key.getKeyLevel(stack);
    }

    /** Runs after DD sets its native chunk-specific lock and before any player opens the chest. */
    public static void afterDataMarker(String marker, BlockPos pos, ServerLevel level,
            RandomSource random, DungeonRoom room) {
        if (!isExpedition(room.theme)
                || !level.dimension().location().equals(ResourceLocation.parse("dimdungeons:dungeon_dimension"))) return;
        String theme = room.theme == CITADEL ? "citadel" : "abyss";
        if (!room.structure.startsWith("qm:dimensional_dungeons/" + theme + "/")) return;
        boolean treasure = marker.equals("LockWithCode") || marker.equals("ChestLoot2");
        if (!treasure && !marker.equals("ChestLoot1") && !marker.equals("SetTrappedLoot")
                && !marker.equals("BarrelLoot1")) return;
        ResourceLocation table = ResourceLocation.parse(TABLE_PREFIX + theme + (treasure ? "_treasure" : "_supplies"));
        RandomizableContainer.setBlockEntityLootTable(level, random, pos.below(),
            ResourceKey.create(Registries.LOOT_TABLE, table));
        // A data marker above a chest must not become a permanent solid lid.
        if (level.getBlockState(pos).is(Blocks.STRUCTURE_BLOCK)) level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
}
