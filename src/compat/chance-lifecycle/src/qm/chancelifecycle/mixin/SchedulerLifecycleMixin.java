package qm.chancelifecycle.mixin;
import chanceCubes.util.Scheduler;
import chanceCubes.util.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.chancelifecycle.QmChanceLifecycle;
@Mixin(value=Scheduler.class,remap=false)
public abstract class SchedulerLifecycleMixin {
    @Inject(method="scheduleTask",at=@At("HEAD"),cancellable=true)
    private static void qmClaim(Task task, CallbackInfoReturnable<Boolean> cir) {
        if (!QmChanceLifecycle.claim(task)) cir.setReturnValue(false);
    }
    @Inject(method="tickTasks",at=@At("HEAD"),cancellable=true)
    private static void qmOwnThread(CallbackInfo ci) {
        if (!QmChanceLifecycle.mayTick()) ci.cancel();
    }
}

