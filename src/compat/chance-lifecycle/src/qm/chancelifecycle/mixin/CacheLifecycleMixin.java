package qm.chancelifecycle.mixin;
import chanceCubes.util.RewardBlockCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.chancelifecycle.QmChanceLifecycle;
@Mixin(value=RewardBlockCache.class,remap=false)
public abstract class CacheLifecycleMixin {
    @Shadow @Final private Level level;
    @Inject(method="restoreBlocks",at=@At("HEAD"),cancellable=true)
    private void qmRestoreOnlyOwnedWorld(Entity player, CallbackInfo ci) {
        if (!QmChanceLifecycle.usable(level)) ci.cancel();
    }
    @Inject(method="cacheBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)V",at=@At("HEAD"),cancellable=true)
    private void qmCacheOnlyOwnedWorld(BlockPos pos, BlockState state, int update, CallbackInfo ci) {
        if (!QmChanceLifecycle.usable(level)) ci.cancel();
    }
}

