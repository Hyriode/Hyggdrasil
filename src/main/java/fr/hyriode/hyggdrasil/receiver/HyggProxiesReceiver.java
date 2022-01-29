package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.HyggHeartbeatPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.request.HyggPacketHeader;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;

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
    public HyggResponse receive(String channel, HyggPacket packet, HyggPacketHeader packetHeader) {
        final HyggApplication sender = packetHeader.getSender();

        if (sender.getType() == HyggApplication.Type.PROXY) {
            final String proxyName = sender.getName();
            final HyggProxy proxy = this.hyggdrasil.getProxyManager().getProxyByName(proxyName);

            if (proxy != null) {
                if (packet instanceof HyggHeartbeatPacket) {
                    proxy.heartbeat();
                    return HyggResponse.Type.SUCCESS.toResponse();
                }
            }
        }
        return HyggResponse.Type.NONE.toResponse();
    }

}
