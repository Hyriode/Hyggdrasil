package fr.hyriode.hyggdrasil.api.event.model.limbo;

import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;

import java.util.Set;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 31/12/2022 at 18:00
 */
public class HyggLimboUpdatedEvent extends HyggLimboEvent {

    /**
     * Constructor of {@link HyggLimboUpdatedEvent}
     *
     * @param limbo The concerned limbo
     */
    public HyggLimboUpdatedEvent(HyggLimbo limbo) {
        super(limbo);
    }

    /**
     * Get the current state of the limbo that just updated
     *
     * @return A {@link HyggLimbo.State}
     */
    public HyggLimbo.State getProxyState() {
        return this.limbo.getState();
    }

    /**
     * Get the current players connected through the limbo
     *
     * @return A list of player {@link UUID}
     */
    public Set<UUID> getLimboPlayers() {
        return this.limbo.getPlayers();
    }

}
