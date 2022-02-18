package fr.hyriode.hyggdrasil.api.event.model.proxy;

import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/02/2022 at 15:50
 */
public class HyggProxyStartedEvent extends HyggProxyEvent {

    /**
     * Constructor of {@link HyggProxyStartedEvent}
     *
     * @param proxy The concerned proxy
     */
    public HyggProxyStartedEvent(HyggProxy proxy) {
        super(proxy);
    }

}
