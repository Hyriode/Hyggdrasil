package fr.hyriode.hyggdrasil.api.protocol;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.HyggResponsePacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 24/12/2021 at 13:16
 */
public enum HyggProtocol {

    RESPONSE_PACKET(0, HyggResponsePacket.class),

    ;

    /** The character sued to split the packet id and the packet content */
    public static final String CONTENT_SPLIT_CHAR = "&";

    /** Packet's id */
    private final int packetId;
    /** Packet's class (the class needs to inherit of {@link HyggPacket}) */
    private final Class<? extends HyggPacket> packetClass;

    /**
     * Constructor of {@link HyggProtocol}
     *
     * @param packetId Packet's id
     * @param packetClass Packet's class
     */
    HyggProtocol(int packetId, Class<? extends HyggPacket> packetClass) {
        this.packetId = packetId;
        this.packetClass = packetClass;
    }

    /**
     * Get the identifier of the packet
     *
     * @return The identifier
     */
    public int getPacketId() {
        return this.packetId;
    }

    /**
     * Get the class of the packet
     *
     * @return {@link Class} of the packet
     */
    public Class<? extends HyggPacket> getPacketClass() {
        return this.packetClass;
    }

    /**
     * Get packet id by its class
     *
     * @param clazz Packet class
     * @return Packet id
     */
    public static int getPacketIdByClass(Class<? extends HyggPacket> clazz) {
        for (HyggProtocol value : values()) {
            if (value.getPacketClass() == clazz) {
                return value.getPacketId();
            }
        }
        return -1;
    }

    /**
     * Get packet class by its id
     *
     * @param packetId Packet's id
     * @return Packet class
     */
    public static Class<? extends HyggPacket> getPacketClassById(int packetId) {
        for (HyggProtocol value : values()) {
            if (value.getPacketId() == packetId) {
                return value.getPacketClass();
            }
        }
        return null;
    }

}
