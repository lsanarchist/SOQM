package qm.mobcompat.mixin;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Ymondf 0.2.9-9 omits the inherited Mob/LivingEntity data definitions.
 * Restore the ordinary superclass call before its own six accessors are defined.
 * The target's accessor allocation, values and behavior remain untouched. */
@Mixin(targets="com.mornfire.ymondf.world.MoonAvatarEntity", remap=false)
public abstract class MoonAvatarDataMixin extends Monster {
    protected MoonAvatarDataMixin(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Inject(method="defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V",
            at=@At("HEAD"), remap=false)
    private void qm$defineInheritedData(SynchedEntityData.Builder builder, CallbackInfo callback) {
        super.defineSynchedData(builder);
    }
}
