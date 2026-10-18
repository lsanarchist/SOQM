package qm.chancecompat.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A spawn-position operation must finish before the level registers the entity.
 * This changes only the registration boundary; the upstream placement, naming,
 * wolf-to-creeper timer and reward selection remain upstream code.
 *
 * Both BlockPos overloads and the named-entity helpers delegate through this
 * double-coordinate method. Lightning already positions before registering.
 */
@Mixin(targets = "chanceCubes.mcwrapper.EntityWrapper", remap = false)
public abstract class EntityWrapperMixin {
    @Redirect(
        method = "spawnEntityAt(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;DDD)Lnet/minecraft/world/entity/Entity;",
        at = @At(value = "INVOKE", target = "LchanceCubes/mcwrapper/EntityWrapper;spawnEntity(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"),
        remap = false,
        require = 1
    )
    private static Entity qm$createBeforeRegistering(EntityType<?> type, Level level) {
        return type.create(level);
    }

    @Inject(
        method = "spawnEntityAt(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;DDD)Lnet/minecraft/world/entity/Entity;",
        at = @At("RETURN"),
        remap = false,
        require = 1
    )
    private static void qm$registerAtDestination(EntityType<?> type, Level level,
                                                double x, double y, double z,
                                                CallbackInfoReturnable<Entity> result) {
        Entity entity = result.getReturnValue();
        if (entity != null) {
            level.addFreshEntity(entity);
        }
    }
}
