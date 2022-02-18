package fr.hyriode.hyggdrasil.api.event.model.proxy;

import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxyState;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/02/2022 at 15:53
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
     * @return A {@link HyggProxyState}
     */
    public HyggProxyState getProxyState() {
        return this.proxy.getState();
    }

    /**
     * Get the current amount of players that are on the proxy
     *
     * @return An amount of players
     */
    public int getProxyPlayers() {
        return this.proxy.getPlayers();
    }

}
