package fr.hyriode.hyggdrasil.api.event.model.server;

import fr.hyriode.hyggdrasil.api.server.HyggServer;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/02/2022 at 15:50
 */
public class HyggServerStartedEvent extends HyggServerEvent {

    /**
     * Constructor of {@link HyggServerStartedEvent}
     *
     * @param server The concerned server
     */
    public HyggServerStartedEvent(HyggServer server) {
        super(server);
    }

}
