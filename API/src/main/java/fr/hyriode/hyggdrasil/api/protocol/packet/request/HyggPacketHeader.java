package fr.hyriode.hyggdrasil.api.protocol.packet.request;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 21/01/2022 at 22:27
 */
public class HyggPacketHeader {

    /** The sender of the packet */
    private final HyggApplication sender;
    /** The identifier of the packet. All packets in are in {@link fr.hyriode.hyggdrasil.api.protocol.HyggProtocol} */
    private final int packetId;

    /**
     * Constructor of {@link HyggPacketHeader}
     *
     * @param sender The sender
     * @param packetId The packet's id
     */
    public HyggPacketHeader(HyggApplication sender, int packetId) {
        this.sender = sender;
        this.packetId = packetId;
    }

    /**
     * Get the sender of the packet
     *
     * @return {@link HyggApplication} object
     */
    public HyggApplication getSender() {
        return this.sender;
    }

    /**
     * Get the identifier of the packet.<br>
     * This id is written in {@link fr.hyriode.hyggdrasil.api.protocol.HyggProtocol}
     *
     * @return An identifier
     */
    public int getPacketId() {
        return this.packetId;
    }

    /**
     * This method transforms this Java object in a Json format
     *
     * @return A json of the header
     */
    public String asJson() {
        return HyggdrasilAPI.GSON.toJson(this);
    }

}
