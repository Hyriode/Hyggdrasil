package fr.hyriode.hyggdrasil.api.event.model.server;

import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;

import java.util.List;
import java.util.UUID;

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
     * Get the current players that are on the server
     *
     * @return A list of players
     */
    public List<UUID> getServerPlayers() {
        return this.server.getPlayers();
    }

}
