package fr.hyriode.hyggdrasil.api.event.model.proxy;

import fr.hyriode.hyggdrasil.api.event.HyggEvent;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/02/2022 at 15:52
 */
public abstract class HyggProxyEvent extends HyggEvent {

    /** The concerned proxy */
    protected final HyggProxy proxy;

    /**
     * Constructor of {@link HyggProxyEvent}
     *
     * @param proxy The concerned proxy
     */
    public HyggProxyEvent(HyggProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Get the proxy concerned by the event
     *
     * @return A {@link HyggProxy} object
     */
    public HyggProxy getProxy() {
        return this.proxy;
    }

}
