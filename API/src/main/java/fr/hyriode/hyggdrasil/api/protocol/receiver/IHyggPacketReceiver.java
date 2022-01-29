package fr.hyriode.hyggdrasil.api.protocol.receiver;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.request.HyggPacketHeader;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 24/12/2021 at 10:11
 */
@FunctionalInterface
public interface IHyggPacketReceiver {

    /**
     * This method is called when a packet is received on the wanted channel
     *
     * @param channel Wanted channel
     * @param packet Received packet
     * @param packetHeader The header of the received packet
     * @return {@link HyggResponse} to send back
     */
    HyggResponse receive(String channel, HyggPacket packet, HyggPacketHeader packetHeader);

}
