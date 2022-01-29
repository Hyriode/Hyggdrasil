package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.HyggHeartbeatPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.request.HyggPacketHeader;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.server.HyggServer;

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
    public HyggResponse receive(String channel, HyggPacket packet, HyggPacketHeader packetHeader) {
        final HyggApplication sender = packetHeader.getSender();

        if (sender.getType() == HyggApplication.Type.SERVER) {
            final String serverName = sender.getName();
            final HyggServer server = this.hyggdrasil.getServerManager().getServerByName(serverName);

            if (server != null) {
                if (packet instanceof HyggHeartbeatPacket) {
                    server.heartbeat();
                    return HyggResponse.Type.SUCCESS.toResponse();
                }
            }
        }
        return HyggResponse.Type.NONE.toResponse();
    }

}
