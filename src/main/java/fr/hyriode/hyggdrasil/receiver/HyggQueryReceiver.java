package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggStartProxyPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggStopServerPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggStartServerPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.request.HyggPacketHeader;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.server.HyggServerManager;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.*;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 14:28
 */
public class HyggQueryReceiver implements IHyggPacketReceiver {

    private final HyggProxyManager proxyManager;
    private final HyggServerManager serverManager;

    private final Hyggdrasil hyggdrasil;

    public HyggQueryReceiver(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.proxyManager = this.hyggdrasil.getProxyManager();
        this.serverManager = this.hyggdrasil.getServerManager();
    }

    @Override
    public HyggResponse receive(String channel, HyggPacket packet, HyggPacketHeader packetHeader) {
        if (packet instanceof HyggStartProxyPacket) {
            final String proxyName = this.proxyManager.startProxy();

            return proxyName != null ? new HyggResponse(SUCCESS, proxyName) : new HyggResponse(ERROR);
        } else if (packet instanceof final HyggStopProxyPacket query) {
            return this.proxyManager.stopProxy(query.getProxyName()) ? SUCCESS.toResponse() : ERROR.toResponse();
        } else if (packet instanceof final HyggStartServerPacket query) {
            final String serverName = this.serverManager.startServer(query.getServerType());

            return serverName != null ? new HyggResponse(SUCCESS, serverName) : new HyggResponse(ERROR);
        } else if (packet instanceof final HyggStopServerPacket query) {
            return this.serverManager.stopServer(query.getServerName()) ? SUCCESS.toResponse() : ERROR.toResponse();
        }
        return NONE.toResponse();
    }

}
