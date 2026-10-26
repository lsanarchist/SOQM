package qm.dungeonencounters.mixin;

import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.dungeonencounters.QmDungeonEncounters;
import qm.dungeonencounters.QmExpeditions;

@Mixin(value = DungeonPlacement.class, remap = false)
public abstract class DungeonPlacementMixin {
    @Inject(method = "spawnEnemyHere", at = @At("RETURN"), remap = false)
    private static void qm$enrollAfterPlacement(BlockPos pos, String id, ServerLevel level,
            int theme, DungeonType type, CallbackInfoReturnable<Entity> result) {
        QmDungeonEncounters.enrollDungeon(result.getReturnValue(), type == DungeonType.ADVANCED, theme);
    }

    @Redirect(method = "spawnEnemyHere", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"), remap = false, require = 2)
    private static int qm$nativeRandomThemeDropsOnly(List<?> themes) {
        return QmExpeditions.randomThemeCount(themes.size());
    }

    @Inject(method = "handleDataBlock", at = @At("RETURN"), remap = false)
    private static void qm$assignAuthoredExpeditionLoot(String marker, BlockPos pos, ServerLevel level,
            RandomSource random, BoundingBox bounds, DungeonRoom room, CallbackInfo callback) {
        QmExpeditions.afterDataMarker(marker, pos, level, random, room);
    }
}
