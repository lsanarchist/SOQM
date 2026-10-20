package qm.rewards.mixin;

import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.rewards.RecipeRefunds;

/** FTB 2101.1.36 marks a claim before invoking Reward.claim; reject before that. */
@Mixin(value = TeamData.class, remap = false)
public abstract class RefundClaimMixin {
    @Inject(method = "claimReward(Lnet/minecraft/server/level/ServerPlayer;Ldev/ftb/mods/ftbquests/quest/reward/Reward;ZJ)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    private void soqm$beforeClaim(ServerPlayer player, Reward reward, boolean notify, long when, CallbackInfo ci) {
        if (!RecipeRefunds.canClaim(player, reward)) ci.cancel();
    }

    @Redirect(method = "claimReward(Lnet/minecraft/server/level/ServerPlayer;Ldev/ftb/mods/ftbquests/quest/reward/Reward;ZJ)V",
            at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/reward/Reward;claim(Lnet/minecraft/server/level/ServerPlayer;Z)V"),
            remap = false)
    private void soqm$acceptedClaim(Reward reward, ServerPlayer player, boolean notify) {
        RecipeRefunds.invokeClaim(reward, player, notify);
    }
}
