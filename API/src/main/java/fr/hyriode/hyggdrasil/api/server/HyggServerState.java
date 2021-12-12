package fr.hyriode.hyggdrasil.api.server;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 15:50
 */
public enum HyggServerState {

    /** Server is in creation (Docker just created it) */
    CREATING(0,"Creating"),
    /** Server is starting (onEnable in plugin) */
    STARTING(1, "Starting"),
    /** Server is ready to host players */
    READY(2, "Ready"),
    /** Server is playing a game */
    PLAYING(3, "In Game"),
    /** Server is stopping (onDisable in plugin) */
    SHUTDOWN(4, "Shutdown"),
    /** Server is idling (an error occurred or just freezing) */
    IDLE(5, "Freeze");

    /** State's id */
    private final int id;
    /** State's display */
    private final String display;

    /**
     * Constructor of {@link HyggServerState}
     *
     * @param id State's id
     * @param display State's display
     */
    HyggServerState(int id, String display) {
        this.id = id;
        this.display = display;
    }

    /**
     * Get server state's id
     *
     * @return An id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get server's state display
     *
     * @return A display text
     */
    public String getDisplay() {
        return this.display;
    }

    /**
     * Get a server state from the id of the state
     *
     * @param id Identifier of the state
     * @return {@link HyggServerState}
     */
    public static HyggServerState fromInteger(Integer id) {
        for (HyggServerState state : HyggServerState.values())
            if (state.getId() == id)
                return state;

        return null;
    }

}
