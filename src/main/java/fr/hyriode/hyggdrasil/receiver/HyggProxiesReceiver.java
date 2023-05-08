package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.heartbeat.HyggHeartbeatPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketHeader;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggProxyInfoPacket;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 22/01/2022 at 18:23
 */
public class HyggProxiesReceiver implements IHyggPacketReceiver {

    private final Hyggdrasil hyggdrasil;

    public HyggProxiesReceiver(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
    }

    @Override
    public HyggResponse receive(String channel, HyggPacketHeader packetHeader, HyggPacket packet) {
        final HyggApplication sender = packetHeader.getSender();

        if (sender.getType() == HyggApplication.Type.PROXY) {
            final HyggProxyManager proxyManager = this.hyggdrasil.getProxyManager();
            final HyggProxy proxy = proxyManager.getProxy(sender.getName());

            if (proxy == null) {
                return HyggResponse.Type.ERROR.toResponse();
            }

            if (packet instanceof final HyggProxyInfoPacket info) {
                proxyManager.updateProxyInfo(proxy, info);
            }

            if (packet instanceof HyggHeartbeatPacket && proxy.heartbeat()) {
                proxyManager.firstHeartbeat(proxy);
            }
            return HyggResponse.Type.SUCCESS.toResponse();
        }
        return HyggResponse.Type.NONE.toResponse();
    }

}
