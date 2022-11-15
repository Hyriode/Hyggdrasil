package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.heartbeat.HyggHeartbeatPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketHeader;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.packet.HyggServerInfoPacket;
import fr.hyriode.hyggdrasil.server.HyggServerManager;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 22/01/2022 at 18:23
 */
public class HyggServersReceiver implements IHyggPacketReceiver {

    private final Hyggdrasil hyggdrasil;

    public HyggServersReceiver(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
    }

    @Override
    public HyggResponse receive(String channel, HyggPacketHeader packetHeader, HyggPacket packet) {
        final HyggApplication sender = packetHeader.getSender();

        if (sender.getType() == HyggApplication.Type.SERVER) {
            final HyggServerManager serverManager = this.hyggdrasil.getServerManager();
            final HyggServer server = serverManager.getServerByName(sender.getName());

            if (server == null) {
                return HyggResponse.Type.ERROR.toResponse();
            }

            if (packet instanceof final HyggServerInfoPacket info) {
                serverManager.updateServerInfo(server, info);
            }

            if (packet instanceof HyggHeartbeatPacket && server.heartbeat()) {
                serverManager.updateServer(server);
            }
            return HyggResponse.Type.SUCCESS.toResponse();
        }
        return HyggResponse.Type.NONE.toResponse();
    }

}
