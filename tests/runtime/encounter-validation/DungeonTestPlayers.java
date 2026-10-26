package soqm.validation.encounters;

import com.mojang.authlib.GameProfile;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

/** Actual server players with a deliberately in-memory transport, never fake players. */
final class DungeonTestPlayers implements AutoCloseable {
    private final List<ServerPlayer> players = new ArrayList<>();
    private final List<EmbeddedChannel> channels = new ArrayList<>();

    ServerPlayer create(ServerLevel level, BlockPos origin, WorldTier tier) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "ExpeditionProbe");
        ServerPlayer player = new ServerPlayer(level.getServer(), level, profile, ClientInformation.createDefault());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        channels.add(new EmbeddedChannel(connection));
        player.connection = new ServerGamePacketListenerImpl(level.getServer(), connection, player,
                CommonListenerCookie.createInitial(profile, false)) {
            @Override public void send(Packet<?> packet) { }
            @Override public void send(Packet<?> packet, PacketSendListener listener) { }
        };
        player.moveTo(origin, 0, 0);
        player.setGameMode(GameType.SURVIVAL);
        player.setData(Apoth.Attachments.WORLD_TIER, tier);
        level.addNewPlayer(player);
        players.add(player);
        return player;
    }

    @Override public void close() {
        for (ServerPlayer player : players) player.serverLevel().removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
        for (EmbeddedChannel channel : channels) channel.finishAndReleaseAll();
        players.clear(); channels.clear();
    }
}
