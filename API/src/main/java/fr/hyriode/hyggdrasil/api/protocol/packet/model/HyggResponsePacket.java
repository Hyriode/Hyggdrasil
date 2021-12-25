package fr.hyriode.hyggdrasil.api.protocol.packet.model;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 15:22
 */
public class HyggResponsePacket extends HyggPacket {

    private final UUID respondedPacketUniqueId;
    private final HyggResponse response;

    public HyggResponsePacket(UUID respondedPacketUniqueId, HyggResponse response) {
        this.respondedPacketUniqueId = respondedPacketUniqueId;
        this.response = response;
    }

    public UUID getRespondedPacketUniqueId() {
        return this.respondedPacketUniqueId;
    }

    public HyggResponse getResponse() {
        return this.response;
    }

}
