package qm.rewards;

import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/** Event-driven common/server code; no world scans and no client class loading. */
@Mod(QmProgressiveRewards.MOD_ID)
public final class QmProgressiveRewards {
    public static final String MOD_ID = "qm_progressive_rewards";

    public QmProgressiveRewards() {
        NeoForge.EVENT_BUS.addListener(QmProgressiveRewards::reloadListeners);
        NeoForge.EVENT_BUS.addListener(QmProgressiveRewards::starting);
        NeoForge.EVENT_BUS.addListener(QmProgressiveRewards::stopped);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmProgressiveRewards::crafted);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmProgressiveRewards::placed);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmProgressiveRewards::login);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmProgressiveRewards::logout);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, QmProgressiveRewards::gameMode);
        TeamEvent.PLAYER_JOINED_PARTY.register(event -> ProductionEvidence.joined(event.getPlayer()));
        TeamEvent.PLAYER_LEFT_PARTY.register(event -> ProductionEvidence.dirtyQuestTeams(event.getPlayerId()));
    }

    private static void reloadListeners(AddReloadListenerEvent event) { event.addListener(new ProductionPolicy()); }
    private static void starting(ServerStartingEvent event) { ProductionPolicy.loadNow(); }
    private static void stopped(ServerStoppedEvent event) { ProductionPolicy.clear(); }
    private static void crafted(PlayerEvent.ItemCraftedEvent event) {
        ProductionEvidence.playerCrafted(event.getEntity(), event.getCrafting(), event.getInventory());
    }

    private static void placed(BlockEvent.EntityPlaceEvent event) {
        if (!event.isCanceled()) {
            ProductionEvidence.placed(event.getLevel().getBlockEntity(event.getPos()),
                    event.getEntity() instanceof Player player ? player : null);
        }
    }

    private static void login(PlayerEvent.PlayerLoggedInEvent event) { remember(event.getEntity()); }
    private static void logout(PlayerEvent.PlayerLoggedOutEvent event) { remember(event.getEntity()); }
    private static void remember(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            ProductionEvidence.rememberActor(serverPlayer, ProductionEvidence.realSurvivalPlayer(player));
        }
    }

    private static void gameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (!event.isCanceled() && event.getEntity() instanceof ServerPlayer player) {
            ProductionEvidence.rememberActor(player,
                    event.getNewGameMode() != GameType.CREATIVE && event.getNewGameMode() != GameType.SPECTATOR);
        }
    }
}
