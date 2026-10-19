package qm.performance.mixin.dungeons;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Distinguishes accepted section membership from visibility and rejected joins. */
@Mixin(value = Entity.class, remap = false)
public interface EntityMembershipAccessor {
    @Accessor(value = "levelCallback", remap = false)
    EntityInLevelCallback qm$getLevelCallback();
}
