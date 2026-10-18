package qm.mobcompat.mixin;

import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.tonicarter.elitemobs.ApotheosisCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.mobcompat.MobCompat45;

@Mixin(value=ApotheosisCompat.class, remap=false)
public abstract class EliteApotheosisMixin {
    @Inject(method="applyMythicAffix", at=@At("HEAD"), cancellable=true, remap=false)
    private static void qm$currentApotheosisApi(ServerLevel level, ItemStack stack,
            Random random, CallbackInfoReturnable<ItemStack> result) {
        result.setReturnValue(MobCompat45.mythicGear(level, stack, random));
    }
}
