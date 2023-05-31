package fr.hyriode.hyggdrasil.api.service;

import java.util.Set;
import java.util.UUID;

public interface IHyggService {

    /** The name of the service */
    String getName();

    /** The container id of the service */
    String getContainerId();

    /** The current players handled by the service */
    Set<UUID> getPlayers();

    /** The timestamp when the service started (in milliseconds) */
    long getStartedTime();
    /** The last heartbeat of the service */
    long getLastHeartbeat();
}
