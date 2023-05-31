package fr.hyriode.hyggdrasil.api.service;

import java.util.Set;
import java.util.UUID;

/**
 * Represents what a service started by Hyggdrasil is.
 */
public interface IHyggService {


    /**
     * Returns the name of the service
     *
     * @return A name
     */
    String getName();

    /**
     * Get the list of players handled by the service
     *
     * @return A list of player {@link UUID}
     */
    Set<UUID> getPlayers();

    /**
     * Get the time when the service started
     *
     * @return A timestamp (in milliseconds)
     */
    long getStartedTime();

    /**
     * Get the time when the server last heartbeat
     *
     * @return A timestamp (in milliseconds)
     */
    long getLastHeartbeat();

}
