package fr.hyriode.hyggdrasil.api.event.model.proxy;

import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;

import java.util.Set;
import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/02/2022 at 15:53.<br>
 *
 * Event triggered each time a proxy is updated.
 */
public class HyggProxyUpdatedEvent extends HyggProxyEvent {

    /**
     * Constructor of {@link HyggProxyUpdatedEvent}
     *
     * @param proxy The concerned proxy
     */
    public HyggProxyUpdatedEvent(HyggProxy proxy) {
        super(proxy);
    }

    /**
     * Get the current state of the proxy that just updated
     *
     * @return A {@link HyggProxy.State}
     */
    public HyggProxy.State getProxyState() {
        return this.proxy.getState();
    }

    /**
     * Get the current players connected through the proxy
     *
     * @return A list of player {@link UUID}
     */
    public Set<UUID> getProxyPlayers() {
        return this.proxy.getPlayers();
    }

}
