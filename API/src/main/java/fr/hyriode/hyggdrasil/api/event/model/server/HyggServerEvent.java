package fr.hyriode.hyggdrasil.api.event.model.server;

import fr.hyriode.hyggdrasil.api.event.HyggEvent;
import fr.hyriode.hyggdrasil.api.server.HyggServer;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/02/2022 at 15:52
 */
public abstract class HyggServerEvent extends HyggEvent {

    /** The concerned server */
    protected final HyggServer server;

    /**
     * Constructor of {@link HyggServerStartedEvent}
     *
     * @param server The concerned server
     */
    public HyggServerEvent(HyggServer server) {
        this.server = server;
    }

    /**
     * Get the server concerned by the event
     *
     * @return A {@link HyggServer} object
     */
    public HyggServer getServer() {
        return this.server;
    }

}
