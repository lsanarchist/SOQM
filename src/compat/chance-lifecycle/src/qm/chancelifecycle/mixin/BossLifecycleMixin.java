package qm.chancelifecycle.mixin;
import chanceCubes.rewards.biodomeGen.BioDomeGen;
import chanceCubes.rewards.defaultRewards.BossBaseReward;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.chancelifecycle.QmChanceLifecycle;
@Mixin(value=BossBaseReward.class,remap=false)
public abstract class BossLifecycleMixin {
    @Inject(method="trigger",at=@At("HEAD"),cancellable=true)
    private void qmTrigger(ServerLevel level, BlockPos pos, Player player, JsonObject settings, CallbackInfo ci) {
        if (!QmChanceLifecycle.usable(level)) ci.cancel();
    }
    @Redirect(method="trigger",at=@At(value="INVOKE",target="LchanceCubes/rewards/biodomeGen/BioDomeGen;genRandomDome(Lnet/minecraft/core/BlockPos;Lnet/minecraft/server/level/ServerLevel;IZ)V"))
    private void qmRememberArena(BioDomeGen dome, BlockPos domePos, ServerLevel level, int radius,
                                 boolean spawnEntities, ServerLevel triggerLevel, BlockPos origin,
                                 Player player, JsonObject settings) {
        dome.genRandomDome(domePos,level,radius,spawnEntities);
        QmChanceLifecycle.arena((BossBaseReward)(Object)this,dome,level,origin,player);
    }
    @Inject(method="startBossFight",at=@At("HEAD"),cancellable=true)
    private void qmStart(ServerLevel level, BlockPos pos, Player player, JsonObject settings,
                         @Coerce Object state, CallbackInfo ci) {
        if (!QmChanceLifecycle.usable(level)) { ci.cancel(); return; }
        QmChanceLifecycle.battle((BossBaseReward)(Object)this,level,pos,player,state);
    }
    @Inject(method="endBossfight",at=@At("HEAD"),cancellable=true)
    private void qmEnd(boolean resetPlayer, ServerLevel level, BlockPos pos, Player player,
                       @Coerce Object state, CallbackInfo ci) {
        if (!QmChanceLifecycle.usable(level)) ci.cancel();
    }
    @Inject(method="endBossfight",at=@At("RETURN"))
    private void qmFinished(boolean resetPlayer, ServerLevel level, BlockPos pos, Player player,
                            @Coerce Object state, CallbackInfo ci) {
        if (QmChanceLifecycle.usable(level)) QmChanceLifecycle.finished(state);
    }
}

