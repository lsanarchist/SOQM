package qm.mobcompat.mixin;

import net.minecraft.world.entity.PathfinderMob;
import net.tonicarter.elitemobs.DifficultyLevel;
import net.tonicarter.elitemobs.EliteTier;
import net.tonicarter.elitemobs.spawn.EliteSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.mobcompat.MobCompat45;

@Mixin(value=EliteSpawner.class, remap=false)
public abstract class EliteSpawnerMixin {
    @Inject(method="applyStats", at=@At("TAIL"), remap=false)
    private static void qm$preserveEncounter(PathfinderMob mob, EliteTier tier,
            DifficultyLevel difficulty, CallbackInfo info) {
        MobCompat45.markElite(mob);
    }
}
