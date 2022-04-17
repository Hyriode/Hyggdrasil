package fr.hyriode.hyggdrasil.api.proxy.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/02/2022 at 14:29
 */
public class HyggFetchProxyPacket extends HyggPacket {

    /** The name of the proxy to fetch */
    private final String proxyName;

    /**
     * Constructor of {@link HyggFetchProxyPacket}
     *
     * @param proxyName The name of the proxy to fetch
     */
    public HyggFetchProxyPacket(String proxyName) {
        this.proxyName = proxyName;
    }

    /**
     * Get the name of the proxy to fetch
     *
     * @return A proxy name
     */
    public String getProxyName() {
        return this.proxyName;
    }

}
