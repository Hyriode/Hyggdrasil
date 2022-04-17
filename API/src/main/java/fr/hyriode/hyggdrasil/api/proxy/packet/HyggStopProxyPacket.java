package fr.hyriode.hyggdrasil.api.proxy.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 14:18
 */
public class HyggStopProxyPacket extends HyggPacket {

    /** The name of the proxy. Example: proxy-1ds42 */
    private final String proxyName;

    /**
     * Constructor of {@link HyggStopProxyPacket}
     *
     * @param proxyName The proxy name
     */
    public HyggStopProxyPacket(String proxyName) {
        this.proxyName = proxyName;
    }

    /**
     * Get the name of the proxy to stop
     *
     * @return A proxy name
     */
    public String getProxyName() {
        return this.proxyName;
    }

}
