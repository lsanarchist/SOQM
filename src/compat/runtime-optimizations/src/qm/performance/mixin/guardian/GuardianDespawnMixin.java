package qm.performance.mixin.guardian;

import net.minecraft.world.entity.Mob;
import qm.performance.guardian.GuardianLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mob.class, remap = false)
public abstract class GuardianDespawnMixin {
    // In the pinned 1.21.1 / NeoForge 21.1.262 bytecode, ordinal 0 is the
    // Peaceful branch, after EventHooks.checkMobDespawn and the mob's own
    // shouldDespawnInPeaceful check. Later discards are distance despawns.
    @Inject(method = "checkDespawn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;discard()V", ordinal = 0),
            remap = false, require = 1, allow = 1)
    private void qm$retirePeacefulGuardian(CallbackInfo ci) {
        GuardianLifecycle.peacefulDespawn((Mob) (Object) this);
    }
}
