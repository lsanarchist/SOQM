package qm.dungeonencounters.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.dungeonencounters.QmDungeonEncounters;

@Mixin(value = Entity.class, remap = false)
public abstract class EntitySaveMixin {
    @Inject(method = "saveWithoutId", at = @At("HEAD"), remap = false)
    private void qm$checkpointAbility(CompoundTag output, CallbackInfoReturnable<CompoundTag> result) {
        QmDungeonEncounters.checkpoint((Entity) (Object) this);
    }
}
