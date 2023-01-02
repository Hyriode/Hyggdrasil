package fr.hyriode.hyggdrasil.api.event.model.limbo;

import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;

/**
 * Created by AstFaster
 * on 31/12/2022 at 18:00
 */
public class HyggLimboStoppedEvent extends HyggLimboEvent {

    /**
     * Constructor of {@link HyggLimboStoppedEvent}
     *
     * @param limbo The concerned limbo
     */
    public HyggLimboStoppedEvent(HyggLimbo limbo) {
        super(limbo);
    }

}
