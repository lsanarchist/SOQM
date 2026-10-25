package qm.dungeonencounters.mixin;

import com.catastrophe573.dimdungeons.structure.DungeonDesigner.DungeonType;
import com.catastrophe573.dimdungeons.structure.DungeonPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.dungeonencounters.QmDungeonEncounters;

@Mixin(value = DungeonPlacement.class, remap = false)
public abstract class DungeonPlacementMixin {
    @Inject(method = "spawnEnemyHere", at = @At("RETURN"), remap = false)
    private static void qm$enrollAfterPlacement(BlockPos pos, String id, ServerLevel level,
            int theme, DungeonType type, CallbackInfoReturnable<Entity> result) {
        QmDungeonEncounters.enrollDungeon(result.getReturnValue(), type == DungeonType.ADVANCED, theme);
    }
}
