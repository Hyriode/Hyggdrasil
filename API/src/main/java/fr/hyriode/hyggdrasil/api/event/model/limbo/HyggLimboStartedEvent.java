package fr.hyriode.hyggdrasil.api.event.model.limbo;

import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;

/**
 * Created by AstFaster
 * on 31/12/2022 at 18:00
 */
public class HyggLimboStartedEvent extends HyggLimboEvent {

    /**
     * Constructor of {@link HyggLimboStartedEvent}
     *
     * @param limbo The concerned limbo
     */
    public HyggLimboStartedEvent(HyggLimbo limbo) {
        super(limbo);
    }

}
