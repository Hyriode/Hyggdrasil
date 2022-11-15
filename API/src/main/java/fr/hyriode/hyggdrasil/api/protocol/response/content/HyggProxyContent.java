package fr.hyriode.hyggdrasil.api.protocol.response.content;

import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;

/**
 * Created by AstFaster
 * on 14/11/2022 at 20:46
 */
public class HyggProxyContent extends HyggResponseContent {

    private final HyggProxy proxy;

    public HyggProxyContent(HyggProxy proxy) {
        this.proxy = proxy;
    }

    public HyggProxy getProxy() {
        return this.proxy;
    }

}
