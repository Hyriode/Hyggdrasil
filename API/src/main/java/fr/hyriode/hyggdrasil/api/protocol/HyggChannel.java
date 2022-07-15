package fr.hyriode.hyggdrasil.api.protocol;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 10:35
 */
public enum HyggChannel {

    /** The channel used to send queries to Hyggdrasil */
    QUERY("query"),
    /** The channel used to send/receive events */
    EVENTS("events"),
    /** The channel used by servers to send information to Hyggdrasil */
    SERVERS("servers"),
    /** The channel used by proxies to send information to Hyggdrasil */
    PROXIES("proxies"),
    /** The channel used to interact with queues */
    QUEUE("queue");

    /** The name of the channel */
    private final String name;

    /**
     * Constructor of {@link HyggChannel}
     *
     * @param name The name of the channel
     */
    HyggChannel(String name) {
        this.name = HyggdrasilAPI.PREFIX + "@" + name;
    }

    /**
     * Get the appropriate channel for an application
     *
     * @param application The concerned application
     * @return A {@link HyggChannel}
     */
    public static HyggChannel getForApplication(HyggApplication application) {
        switch (application.getType()) {
            case SERVER:
                return SERVERS;
            case PROXY:
                return PROXIES;
            default:
                return null;
        }
    }

    /**
     * Get the name of the channel
     *
     * @return A name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
