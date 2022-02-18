package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.HyggHeartbeatPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggServerInfoPacket;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.request.HyggRequestHeader;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.IHyggResponse;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
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
    public IHyggResponse receive(String channel, HyggPacket packet, HyggRequestHeader packetHeader) {
        final HyggApplication sender = packetHeader.getSender();

        if (sender.getType() == HyggApplication.Type.SERVER) {
            final String serverName = sender.getName();
            final HyggServerManager serverManager = this.hyggdrasil.getServerManager();
            HyggServer server = serverManager.getServerByName(serverName);

            if (packet instanceof final HyggServerInfoPacket info) {
                if (server == null) {
                    server = new HyggServer(serverName, info.getState(), info.getPlayers(), info.getStartedTime(), info.getOptions());

                    serverManager.getServers().add(server);
                    serverManager.addServerToProxies(server);
                }

                serverManager.updateServer(server, info);
            }

            if (server != null) {
                if (packet instanceof HyggHeartbeatPacket) {
                    server.heartbeat();
                }
            }
            return HyggResponse.Type.SUCCESS;
        }
        return HyggResponse.Type.NONE;
    }

}
