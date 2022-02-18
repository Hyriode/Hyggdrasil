package fr.hyriode.hyggdrasil.api.protocol.response.content;

import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 11:06
 */
public class HyggProxyResponse extends HyggResponseContent {

    /** The {@link HyggProxy} provided with the response */
    private final HyggProxy proxy;

    /**
     * Constructor of {@link HyggProxyResponse}
     *
     * @param proxy The proxy to send with the response
     */
    public HyggProxyResponse(HyggProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Get the proxy provided with the response
     *
     * @return A {@link HyggProxy}
     */
    public HyggProxy getProxy() {
        return this.proxy;
    }

}
