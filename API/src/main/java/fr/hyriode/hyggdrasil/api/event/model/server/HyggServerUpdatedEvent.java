package fr.hyriode.hyggdrasil.api.event.model.server;

import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/02/2022 at 15:53
 */
public class HyggServerUpdatedEvent extends HyggServerEvent {

    /**
     * Constructor of {@link HyggServerUpdatedEvent}
     *
     * @param server The concerned server
     */
    public HyggServerUpdatedEvent(HyggServer server) {
        super(server);
    }

    /**
     * Get the current state of the server that just updated
     *
     * @return A {@link HyggServerState}
     */
    public HyggServerState getServerState() {
        return this.server.getState();
    }

    /**
     * Get the current amount of players that are on the server
     *
     * @return An amount of players
     */
    public int getServerPlayers() {
        return this.server.getPlayers();
    }

}
