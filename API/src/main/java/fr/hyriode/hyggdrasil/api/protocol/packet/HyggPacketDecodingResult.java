package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.protocol.request.HyggRequestHeader;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 22/01/2022 at 12:14
 */
public class HyggPacketDecodingResult {

    /** The header decoded from the message */
    private final HyggRequestHeader packetHeader;
    /** The packet/content of the message */
    private final HyggPacket packet;
    /** <code>true</code> if the message had a valid signature. It can be <code>false</code> if the message doesn't have a signature */
    private final boolean validSignature;

    /**
     * Constructor of {@link HyggPacketDecodingResult}
     *
     * @param packetHeader The decoded header
     * @param packet The decoded packet
     * @param validSignature Is the signature valid
     */
    public HyggPacketDecodingResult(HyggRequestHeader packetHeader, HyggPacket packet, boolean validSignature) {
        this.packetHeader = packetHeader;
        this.packet = packet;
        this.validSignature = validSignature;
    }

    /**
     * Get the header of the packet.<br>
     * The header can be used to get the id of the packet, the sender of the packet, etc.
     *
     * @return A {@link HyggRequestHeader} object
     */
    public HyggRequestHeader getPacketHeader() {
        return this.packetHeader;
    }

    /**
     * Get the decoded packet from the received message
     *
     * @return A {@link HyggPacket} object
     */
    public HyggPacket getPacket() {
        return this.packet;
    }

    /**
     * Check if the signature of the message was valid or not.<br>
     * Warning: it can be set to <code>false</code> if there was no signature
     *
     * @return <code>true</code> if the signature was valid
     */
    public boolean isValidSignature() {
        return this.validSignature;
    }

}
