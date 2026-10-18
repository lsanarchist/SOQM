package qm.questauto.mixin;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.millenaire.commerce.TradeGood;
import org.millenaire.commerce.TradeMenu;
import org.millenaire.village.PlayerCultureReputation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.questauto.QmQuestAutomation;

@Mixin(value = TradeMenu.class, remap = false)
public abstract class TradeMenuMixin {
    @Redirect(method = "executeBuy", at = @At(value = "INVOKE", target = "Lorg/millenaire/village/PlayerCultureReputation;addLanguageKnowledge(Ljava/util/UUID;Lnet/minecraft/resources/ResourceLocation;I)V"), require = 1)
    private void qm$observeBuy(PlayerCultureReputation reputation, UUID id, ResourceLocation culture, int accepted, Player player, TradeGood good, int requested) {
        reputation.addLanguageKnowledge(id, culture, accepted);
        QmQuestAutomation.tradeSuccess(player, good, accepted, false);
    }

    @Redirect(method = "executeSell", at = @At(value = "INVOKE", target = "Lorg/millenaire/village/PlayerCultureReputation;addLanguageKnowledge(Ljava/util/UUID;Lnet/minecraft/resources/ResourceLocation;I)V"), require = 1)
    private void qm$observeSell(PlayerCultureReputation reputation, UUID id, ResourceLocation culture, int accepted, Player player, TradeGood good, int requested) {
        reputation.addLanguageKnowledge(id, culture, accepted);
        QmQuestAutomation.tradeSuccess(player, good, accepted, true);
    }
}
