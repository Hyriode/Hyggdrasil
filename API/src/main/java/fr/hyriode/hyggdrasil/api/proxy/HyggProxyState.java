package fr.hyriode.hyggdrasil.api.proxy;

import fr.hyriode.hyggdrasil.api.server.HyggServerState;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 15:50
 */
public enum HyggProxyState {

    /** Proxy is in creation (Docker just created it) */
    CREATING(0,"Creating"),
    /** Proxy is starting (onEnable in plugin) */
    STARTING(1, "Starting"),
    /** Proxy is ready to work and manage players */
    READY(2, "Ready"),
    /** Proxy is stopping (onDisable in plugin) */
    SHUTDOWN(4, "Shutdown"),
    /** Proxy is idling (an error occurred or just freezing) */
    IDLE(5, "Freeze");

    /** State's id */
    private final int id;
    /** State's display */
    private final String display;

    /**
     * Constructor of {@link HyggProxyState}
     *
     * @param id State's id
     * @param display State's display
     */
    HyggProxyState(int id, String display) {
        this.id = id;
        this.display = display;
    }

    /**
     * Get proxy state's id
     *
     * @return An id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get proxy's state display
     *
     * @return A display text
     */
    public String getDisplay() {
        return this.display;
    }

    /**
     * Get a proxy state from the id of the state
     *
     * @param id Identifier of the state
     * @return {@link HyggProxyState}
     */
    public static HyggProxyState fromInteger(Integer id) {
        for (HyggProxyState state : HyggProxyState.values())
            if (state.getId() == id)
                return state;

        return null;
    }

}
