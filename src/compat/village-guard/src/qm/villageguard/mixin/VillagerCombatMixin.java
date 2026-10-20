package qm.villageguard.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.millenaire.entity.MillVillager;
import org.millenaire.entity.VillagerCombat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.villageguard.QmVillageGuard;

@Mixin(value=VillagerCombat.class, remap=false)
public abstract class VillagerCombatMixin {
    @Shadow @Final private MillVillager villager;

    @Inject(method="triggerMobAttacks",at=@At("HEAD"),cancellable=true,require=1,remap=false)
    private void qm$preventForcedVillageAggro(CallbackInfo ci) {
        if(QmVillageGuard.isProtectedVillager(villager))ci.cancel();
    }

    // Millenaire writes its private attackTarget before delegating to Mob.setTarget.
    // Reject before either write; vanilla event cancellation alone is too late.
    @Inject(method="setAttackTarget",at=@At("HEAD"),cancellable=true,require=1,remap=false)
    private void qm$guardDirectCombatTarget(LivingEntity target, CallbackInfo ci) {
        if(QmVillageGuard.rejectTarget(villager,target))ci.cancel();
    }
}
