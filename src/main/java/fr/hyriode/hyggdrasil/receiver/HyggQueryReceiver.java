package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketHeader;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggProxyContent;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggServerContent;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggStartProxyPacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.packet.HyggStartServerPacket;
import fr.hyriode.hyggdrasil.api.server.packet.HyggStopServerPacket;
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

    public HyggQueryReceiver(Hyggdrasil hyggdrasil) {
        this.proxyManager = hyggdrasil.getProxyManager();
        this.serverManager = hyggdrasil.getServerManager();
    }

    @Override
    public HyggResponse receive(String channel, HyggPacketHeader packetHeader, HyggPacket packet) {
        // Proxies
        if (packet instanceof final HyggStartProxyPacket query) {
            final HyggProxy proxy = this.proxyManager.startProxy(query.getData());

            return proxy != null ? new HyggResponse(SUCCESS, new HyggProxyContent(proxy)) : ERROR.toResponse();
        } else if (packet instanceof final HyggStopProxyPacket query) {
            return (this.proxyManager.stopProxy(query.getProxyName()) ? SUCCESS : ERROR).toResponse();
        }

        // Servers
        else if (packet instanceof final HyggStartServerPacket query) {
            final HyggServer server = this.serverManager.startServer(query.getServerInfo());

            return server != null ? new HyggResponse(SUCCESS, new HyggServerContent(server)) : ERROR.toResponse();
        } else if (packet instanceof final HyggStopServerPacket query) {
            return (this.serverManager.stopServer(query.getServerName()) ? SUCCESS : ERROR).toResponse();
        }
        return NONE.toResponse();
    }

}
