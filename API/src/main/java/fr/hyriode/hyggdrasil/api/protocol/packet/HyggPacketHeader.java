package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;

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
    /** The time when the request was issued */
    private final long issuedAt;

    /**
     * Constructor of {@link HyggPacketHeader}
     *  @param sender The sender
     * @param packetId The packet's id
     * @param issuedAt The request's issued at time
     */
    public HyggPacketHeader(HyggApplication sender, int packetId, long issuedAt) {
        this.sender = sender;
        this.packetId = packetId;
        this.issuedAt = issuedAt;
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
     * Get the time when the request was issued.<br>
     * The time is a timestamp in milliseconds
     *
     * @return A timestamp
     */
    public long getIssuedAt() {
        return this.issuedAt;
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
