package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggHeartbeatPacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggProxyInfoPacket;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.request.HyggRequestHeader;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.IHyggResponse;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
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
    public IHyggResponse receive(String channel, HyggPacket packet, HyggRequestHeader packetHeader) {
        final HyggApplication sender = packetHeader.getSender();

        if (sender.getType() == HyggApplication.Type.PROXY) {
            final String proxyName = sender.getName();
            final HyggProxyManager proxyManager = this.hyggdrasil.getProxyManager();
            HyggProxy proxy = proxyManager.getProxyByName(proxyName);

            if (packet instanceof final HyggProxyInfoPacket info) {
                if (proxy == null) {
                    proxy = new HyggProxy(proxyName, info.getPlayers(), info.getState(), info.getStartedTime());

                    proxyManager.getProxies().add(proxy);
                }

                proxyManager.updateProxy(proxy, info);
            }

            if (proxy != null) {
                if (packet instanceof HyggHeartbeatPacket) {
                    if (proxy.heartbeat()) {
                        this.hyggdrasil.getServerManager().addServersToProxies();
                    }
                }
            }
            return HyggResponse.Type.SUCCESS;
        }
        return HyggResponse.Type.NONE;
    }

}
