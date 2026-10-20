package qm.villageguard.mixin;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.millenaire.entity.MillVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.villageguard.GuardedBrain;
import qm.villageguard.QmVillageGuard;

@Mixin(value=Brain.class, remap=false)
public abstract class BrainTargetMixin implements GuardedBrain {
    @Unique private Mob qm$owner;

    @Override
    public void qm$bindOwner(Mob owner) {
        if(qm$owner==owner) {
            // An explicit join may reuse the same Brain after a real removal.
            QmVillageGuard.observeTargets(owner);
            return;
        }
        qm$owner=owner;
        QmVillageGuard.brainBound(owner);
    }

    // Joining handles existing Brains; this covers native AI Brain replacements.
    // An already bound Brain does no target reads or index queries on this path.
    @Inject(method="tick", at=@At("HEAD"), require=1, remap=false)
    private void qm$bindTickOwner(ServerLevel level, LivingEntity entity, CallbackInfo ci) {
        if(entity instanceof Mob mob && qm$owner!=mob)qm$bindOwner(mob);
    }

    // Plain, optional and expiring memory writes all use this one native setter.
    @Inject(method="setMemoryInternal", at=@At("HEAD"), cancellable=true, require=1, remap=false)
    private void qm$guardAttackMemory(MemoryModuleType<?> type,
            Optional<? extends ExpirableValue<?>> value, CallbackInfo ci) {
        if(type!=MemoryModuleType.ATTACK_TARGET || value.isEmpty())return;
        if(value.get().getValue() instanceof MillVillager target
                && QmVillageGuard.rejectTarget(qm$owner,target))ci.cancel();
    }
}
