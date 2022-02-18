package fr.hyriode.hyggdrasil.api.server;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 13:48
 */
public class HyggServer {

    /** Server's name */
    protected final String name;
    /** Server's type */
    protected final String type;

    /** Current server's state */
    protected HyggServerState state;
    /** Server's options (pvp, nether, etc.) */
    protected final HyggServerOptions options;

    /** Current number of players on the server */
    protected int players;

    /** Server's started time (timestamp) */
    protected final long startedTime;
    /** The last time that the server send a heartbeat */
    protected long lastHeartbeat = -1;

    /**
     * Constructor of {@link HyggServer}
     *
     * @param type Server's type (for example: lobby, nexus, etc.)
     * @param options Server's options (pvp, nether, etc.)
     */
    public HyggServer(String type, HyggServerOptions options) {
        this.name = type + "-" + UUID.randomUUID().toString().substring(0, 5);
        this.type = type;
        this.state = HyggServerState.CREATING;
        this.options = options;
        this.startedTime = System.currentTimeMillis();
    }

    /**
     * Full constructor of {@link HyggServer}
     *
     * @param name The name of the server
     * @param state The current state of the server
     * @param players The current amount of players on the server
     * @param startedTime The time when the server started
     * @param options Server's options (pvp, nether, etc.)
     */
    public HyggServer(String name, HyggServerState state, int players, long startedTime, HyggServerOptions options) {
        this.name = name;
        this.type = getTypeFromName(name);
        this.state = state;
        this.options = options;
        this.players = players;
        this.startedTime = startedTime;
    }

    /**
     * Get the server type from its name
     *
     * @param serverName The name of the server
     * @return The type of the provided server
     */
    public static String getTypeFromName(String serverName) {
        return serverName.split("-")[0];
    }

    /**
     * Get server's name (ex: lobby-5sqf4)
     *
     * @return Server's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get server's type (ex: nexus, rtf, lobby)
     *
     * @return Server's type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Get current server's state
     *
     * @return {@link HyggServerState}
     */
    public HyggServerState getState() {
        return this.state;
    }

    /**
     * Set current server's state
     *
     * @param state New server's state
     */
    public void setState(HyggServerState state) {
        this.state = state;
    }

    /**
     * Get server's options
     *
     * @return {@link HyggServerOptions}
     */
    public HyggServerOptions getOptions() {
        return this.options;
    }

    /**
     * Get current number of players on the server
     *
     * @return Number of players
     */
    public int getPlayers() {
        return this.players;
    }

    /**
     * Set current number of players on the server
     *
     * @param players New player amount
     */
    public void setPlayers(int players) {
        this.players = players;
    }

    /**
     * Get server's started time (a timestamp in millis)
     *
     * @return Server's started time
     */
    public long getStartedTime() {
        return this.startedTime;
    }

    /**
     * Set the last heartbeat of the server
     *
     * @return <code>true</code> if it's the first heartbeat of the server
     */
    public boolean heartbeat() {
        final long oldHeartbeat = this.lastHeartbeat;

        if (this.state == HyggServerState.CREATING) {
            this.state = HyggServerState.STARTING;
        }
        this.lastHeartbeat = System.currentTimeMillis();

        return oldHeartbeat == -1;
    }

    /**
     * Get the last heartbeat of the server
     *
     * @return A timestamp
     */
    public long getLastHeartbeat() {
        return this.lastHeartbeat;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
