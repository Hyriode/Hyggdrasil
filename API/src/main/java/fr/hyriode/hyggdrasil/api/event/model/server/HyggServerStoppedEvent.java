package fr.hyriode.hyggdrasil.api.event.model.server;

import fr.hyriode.hyggdrasil.api.server.HyggServer;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/02/2022 at 15:50
 */
public class HyggServerStoppedEvent extends HyggServerEvent {

    /**
     * Constructor of {@link HyggServerStoppedEvent}
     *
     * @param server The concerned server
     */
    public HyggServerStoppedEvent(HyggServer server) {
        super(server);
    }

}
