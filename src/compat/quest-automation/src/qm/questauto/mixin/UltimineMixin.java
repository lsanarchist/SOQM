package qm.questauto.mixin;

import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.questauto.QmQuestAutomation;

@Mixin(value = FTBUltimine.class, remap = false)
public abstract class UltimineMixin {
    @Inject(method = "tryBreakBlock", at = @At("RETURN"), require = 1)
    private static void qm$observeBreak(ServerPlayer player, BlockPos pos, BlockState state, Shape shape, BlockHitResult hit, CallbackInfoReturnable<Boolean> result) {
        QmQuestAutomation.ultimineBreak(player, pos, result.getReturnValueZ());
    }
}
