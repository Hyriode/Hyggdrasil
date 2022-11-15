package fr.hyriode.hyggdrasil.api.proxy.packet;

import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 14:18<br>
 *
 * This packet can be used to query a new proxy.<br>
 * After Hyggdrasil received the packet, it will return the created proxy.
 */
public class HyggStartProxyPacket extends HyggPacket {

    /** The data of the proxy to create */
    private final HyggData data;

    /**
     * Create a {@link HyggStartProxyPacket}
     *
     * @param data The data of the proxy
     */
    public HyggStartProxyPacket(HyggData data) {
        this.data = data;
    }

    /**
     * Get the data of the proxy to create
     *
     * @return A {@link HyggData} object
     */
    public HyggData getData() {
        return this.data;
    }

}
