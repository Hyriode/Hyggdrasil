package fr.hyriode.hyggdrasil.api.event.model.server;

import fr.hyriode.hyggdrasil.api.server.HyggServer;

import java.util.List;
import java.util.Set;
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
     * @return A {@link HyggServer.State}
     */
    public HyggServer.State getServerState() {
        return this.server.getState();
    }

    /**
     * Get the current players that are on the server
     *
     * @return A list of players
     */
    public Set<UUID> getServerPlayers() {
        return this.server.getPlayers();
    }

}
