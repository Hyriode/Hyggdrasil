package fr.hyriode.hyggdrasil.api.protocol;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.HyggHeartbeatPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.HyggResponsePacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggProxyServerActionPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggStartProxyPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggStopServerPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggStartServerPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 24/12/2021 at 13:16
 */
public enum HyggProtocol {

    /** Common packets section */
    RESPONSE_PACKET(0, HyggResponsePacket.class),
    HEARTBEAT_PACKET(1, HyggHeartbeatPacket.class),

    /** Proxy packets section */
    PROXY_START_PACKET(30, HyggStartProxyPacket.class),
    PROXY_STOP_PACKET(31, HyggStopProxyPacket.class),
    PROXY_SERVER_ACTION_PACKET(32, HyggProxyServerActionPacket.class),

    /** Server packets section */
    SERVER_START_PACKET(50, HyggStartServerPacket.class),
    SERVER_STOP_PACKET(51, HyggStopServerPacket.class),

    ;

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
