package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggFetchProxiesPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggFetchProxyPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggStartProxyPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggFetchServerPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggFetchServersPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggStartServerPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggStopServerPacket;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.request.HyggRequestHeader;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.IHyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggProxyResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggServerResponse;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.server.HyggServerManager;

import java.util.ArrayList;

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
    public IHyggResponse receive(String channel, HyggPacket packet, HyggRequestHeader packetHeader) {
        if (packet instanceof HyggStartProxyPacket) {
            final HyggProxy proxy = this.proxyManager.startProxy();

            return proxy != null ? new HyggResponse(SUCCESS, new HyggProxyResponse(proxy)) : ERROR;
        } else if (packet instanceof final HyggStopProxyPacket query) {
            return this.proxyManager.stopProxy(query.getProxyName()) ? SUCCESS : ERROR;
        } else if (packet instanceof final HyggStartServerPacket query) {
            final HyggServer server = this.serverManager.startServer(query.getServerType(), query.getServerOptions());

            return server != null ? new HyggResponse(SUCCESS, new HyggServerResponse(server)) : ERROR;
        } else if (packet instanceof final HyggStopServerPacket query) {
            return this.serverManager.stopServer(query.getServerName()) ? SUCCESS : ERROR;
        } else if (packet instanceof final HyggFetchServerPacket query) {
            final HyggServer server = this.serverManager.getServerByName(query.getServerName());

            return server != null ? new HyggResponse(SUCCESS, new HyggServerResponse(server)) : ERROR;
        } else if (packet instanceof final HyggFetchProxyPacket query) {
            final HyggProxy proxy = this.proxyManager.getProxyByName(query.getProxyName());

            return proxy != null ? new HyggResponse(SUCCESS, new HyggProxyResponse(proxy)) : ERROR;
        } else if (packet instanceof final HyggFetchServersPacket query) {
            final String serversType = query.getServersType();

            if (serversType == null || serversType.isEmpty()) {
                return new HyggResponse(SUCCESS, new HyggFetchServersPacket.Response(new ArrayList<>(this.serverManager.getServers())));
            } else {
                return new HyggResponse(SUCCESS, new HyggFetchServersPacket.Response(this.serverManager.getServersByType(serversType)));
            }
        } else if (packet instanceof HyggFetchProxiesPacket) {
            return new HyggResponse(SUCCESS, new HyggFetchProxiesPacket.Response(new ArrayList<>(this.proxyManager.getProxies())));
        }
        return NONE;
    }

}
