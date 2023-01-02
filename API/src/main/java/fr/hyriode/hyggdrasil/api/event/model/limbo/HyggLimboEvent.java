package fr.hyriode.hyggdrasil.api.event.model.limbo;

import fr.hyriode.hyggdrasil.api.event.HyggEvent;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;

/**
 * Created by AstFaster
 * on 31/12/2022 at 18:00
 */
public abstract class HyggLimboEvent extends HyggEvent {

    /** The concerned proxy */
    protected final HyggLimbo limbo;

    /**
     * Constructor of {@link HyggLimboEvent}
     *
     * @param limbo The concerned limbo
     */
    public HyggLimboEvent(HyggLimbo limbo) {
        this.limbo = limbo;
    }

    /**
     * Get the limbo concerned by the event
     *
     * @return A {@link HyggLimbo} object
     */
    public HyggLimbo getLimbo() {
        return this.limbo;
    }

}
