package fr.hyriode.hyggdrasil.api.protocol;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggHeartbeatPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggResponsePacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.*;
import fr.hyriode.hyggdrasil.api.queue.packet.*;
import fr.hyriode.hyggdrasil.api.server.packet.*;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 24/12/2021 at 13:16
 */
public enum HyggProtocol {

    /** Common packets section */
    RESPONSE(0, HyggResponsePacket.class),
    HEARTBEAT(1, HyggHeartbeatPacket.class),

    /** Proxy packets section */
    PROXY_START(30, HyggStartProxyPacket.class),
    PROXY_STOP(31, HyggStopProxyPacket.class),
    PROXY_SERVER_ACTION(32, HyggProxyServerActionPacket.class),
    PROXY_INFO(33, HyggProxyInfoPacket.class),
    PROXY_FETCH(34, HyggFetchServerPacket.class),
    PROXIES_FETCH(35, HyggFetchProxiesPacket.class),
    EVACUATE(36, HyggEvacuatePacket.class),

    /** Server packets section */
    SERVER_START(50, HyggStartServerPacket.class),
    SERVER_STOP(51, HyggStopServerPacket.class),
    SERVER_INFO(52, HyggServerInfoPacket.class),
    SERVER_FETCH(53, HyggFetchServerPacket.class),
    SERVERS_FETCH(54, HyggFetchServersPacket.class),

    /** Queue packets section */
    QUEUE_ADD_PLAYER(70, HyggQueueAddPlayerPacket.class),
    QUEUE_ADD_GROUP(71, HyggQueueAddGroupPacket.class),
    QUEUE_REMOVE_PLAYER(72, HyggQueueRemovePlayerPacket.class),
    QUEUE_REMOVE_GROUP(73, HyggQueueRemoveGroupPacket.class),
    QUEUE_UPDATE_GROUP(74, HyggQueueUpdateGroupPacket.class),
    QUEUE_INFO(75, HyggQueueInfoPacket.class),
    QUEUE_TRANSFER(76, HyggQueueTransferPacket.class)

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
