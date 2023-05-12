package fr.hyriode.hyggdrasil.api.proxy;

import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:09.<br>
 *
 * The main object of what a proxy is.
 */
public class HyggProxy {

    /** The maximum amount of players that can handle a proxy */
    public static final int MAX_PLAYERS = 750;

    /** The name of the proxy */
    protected final String name;
    private String containerId;

    /** The data of the proxy */
    protected HyggData data;
    /** The current state of the proxy */
    protected State state;
    /** The current players handled by the proxy */
    protected Set<UUID> players;

    /** The timestamp when the proxy started (in milliseconds) */
    protected final long startedTime;
    /** The last heartbeat of the proxy */
    protected long lastHeartbeat = -1;

    /** The port of the server */
    protected int port = -1;

    /**
     * Default constructor of a {@link HyggProxy}
     *
     * @param name The name of the proxy
     * @param data The data of the proxy
     */
    public HyggProxy(String name, HyggData data) {
        this.name = name;
        this.data = data;
        this.state = State.CREATING;
        this.players = new HashSet<>();
        this.startedTime = System.currentTimeMillis();
    }

    /**
     * Full constructor of a {@link HyggProxy}
     *
     * @param name The name of the proxy
     * @param data The data of the proxy
     * @param state The state of the proxy
     * @param players The players connected through the proxy
     * @param startedTime The time when the proxy started (in milliseconds)
     */
    public HyggProxy(String name, HyggData data, State state, Set<UUID> players, long startedTime) {
        this.name = name;
        this.data = data;
        this.state = state;
        this.players = players;
        this.startedTime = startedTime;
    }

    /**
     * Get the name of the proxy.
     *
     * @return A name. E.g. proxy-ds567
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Get the data of the proxy
     *
     * @return A {@link HyggData} object
     */
    @NotNull
    public HyggData getData() {
        return this.data;
    }

    /**
     * Set the data of the proxy
     *
     * @param data The new data
     */
    public void setData(@NotNull HyggData data) {
        this.data = data;
    }

    /**
     * Get the current state of the proxy
     *
     * @return A {@link State}
     */
    @NotNull
    public State getState() {
        return this.state;
    }

    /**
     * Set the current state of the proxy
     *
     * @param state The new {@link State}
     */
    public void setState(@NotNull State state) {
        this.state = state;
    }

    /**
     * Get the current players handled by the proxy.
     *
     * @return A set of players
     */
    @NotNull
    public Set<UUID> getPlayers() {
        return this.players;
    }

    /**
     * Set the current players handled by the proxy.
     *
     * @param players The new players
     */
    public void setPlayers(@NotNull Set<UUID> players) {
        this.players = players;
    }

    /**
     * Get the time when the proxy started
     *
     * @return A timestamp (in milliseconds)
     */
    public long getStartedTime() {
        return this.startedTime;
    }

    /**
     * Set the last heartbeat of the proxy
     *
     * @return <code>true</code> if it's the first heartbeat of the proxy
     */
    public boolean heartbeat() {
        final long oldHeartbeat = this.lastHeartbeat;

        if (oldHeartbeat == -1) {
            this.state = State.STARTING;
        }

        this.lastHeartbeat = System.currentTimeMillis();

        return oldHeartbeat == -1;
    }

    /**
     * Get the port of the proxy
     *
     * @return A port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Set the port of the proxy
     *
     * @param port A port
     */
    public void setPort(int port) {
        if (this.port == -1) {
            this.port = port;
        }
    }

    /**
     * Get the last heartbeat of the proxy
     *
     * @return A timestamp (in milliseconds)
     */
    public long getLastHeartbeat() {
        return this.lastHeartbeat;
    }

    /**
     * Get the id of the Docker container
     *
     * @return An identifier
     */
    public String getContainerId() {
        return this.containerId;
    }

    /**
     * Set the id of the Docker container
     *
     * @param containerId An identifier
     */
    public void setContainerId(String containerId) {
        if (this.containerId != null) {
            throw new IllegalStateException("The container id of this server is already set!");
        }

        this.containerId = containerId;
    }

    public enum State {

        /** Proxy is in creation (Docker just created it) */
        CREATING(0),
        /** Proxy is starting (onEnable in plugin) */
        STARTING(1),
        /** Proxy is ready to work and manage players */
        READY(2),
        /** Proxy is stopping (onDisable in plugin) */
        SHUTDOWN(3),
        /** Proxy is idling (an error occurred or just freezing) */
        IDLE(4);

        /** The identifier of the state */
        private final int id;

        /**
         * Constructor of {@link State}
         *
         * @param id An id
         */
        State(int id) {
            this.id = id;
        }

        /**
         * Get the identifier of the state
         *
         * @return An id
         */
        public int getId() {
            return this.id;
        }

    }

}
