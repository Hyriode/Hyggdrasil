package fr.hyriode.hyggdrasil.api.protocol.response;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 15:22
 */
public class HyggResponsePacket extends HyggPacket {

    /** The unique id of the responded packet */
    private final UUID respondedPacketUniqueId;
    /** The response to send */
    private final HyggResponse response;

    /**
     * Constructor of {@link HyggResponsePacket}
     *
     * @param respondedPacketUniqueId A packet {@link UUID}
     * @param response The response as an object
     */
    public HyggResponsePacket(UUID respondedPacketUniqueId, HyggResponse response) {
        this.respondedPacketUniqueId = respondedPacketUniqueId;
        this.response = response;
    }

    /**
     * Get the unique id of the packet to respond
     *
     * @return An {@link UUID}
     */
    public UUID getRespondedPacketUniqueId() {
        return this.respondedPacketUniqueId;
    }

    /**
     * Get the response to send
     *
     * @return A {@link HyggResponse} object
     */
    public HyggResponse getResponse() {
        return this.response;
    }

}
