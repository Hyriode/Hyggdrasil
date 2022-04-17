package fr.hyriode.hyggdrasil.api.server;

import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 13:48
 */
public class HyggServer {

    public static final String GAME_TYPE_KEY = "game-type";
    public static final String MAP_KEY = "map";

    /** Server's name */
    protected final String name;
    /** Server's type */
    protected final String type;

    /** Current server's state */
    protected HyggServerState state;
    /** Server's options (pvp, nether, etc.) */
    protected final HyggServerOptions options;
    /** The data provided to the server */
    protected final HyggData data;

    /** Current number of players on the server */
    protected int players;
    /** The real amount of players connected on the server. It includes moderators, and spectators */
    protected int realPlayers;
    /** The available slots on the network */
    protected int slots = -1;

    /** Server's started time (timestamp) */
    protected final long startedTime;
    /** The last time that the server send a heartbeat */
    protected long lastHeartbeat = -1;

    /**
     * Constructor of {@link HyggServer}
     *
     * @param type Server's type (for example: lobby, nexus, etc.)
     * @param options Server's options (pvp, nether, etc.)
     * @param data Server's data
     */
    public HyggServer(String type, HyggServerOptions options, HyggData data) {
        this.name = type + "-" + UUID.randomUUID().toString().substring(0, 5);
        this.type = type;
        this.state = HyggServerState.CREATING;
        this.options = options;
        this.data = data;
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
     * @param data Server's data
     */
    public HyggServer(String name, HyggServerState state, int players, long startedTime, HyggServerOptions options, HyggData data) {
        this.name = name;
        this.type = getTypeFromName(name);
        this.state = state;
        this.options = options;
        this.data = data;
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
     * Get the type of the game.<br>
     * It can be null, because not all servers are game servers!
     *
     * @return A game type. Ex: DEFAULT, FOUR_FOUR, etc.
     */
    public String getGameType() {
        return this.data.get(GAME_TYPE_KEY);
    }

    /**
     * Get the name of the map used on the server
     *
     * @return A map name
     */
    public String getMap() {
        return this.data.get(MAP_KEY);
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
     * Get server's data
     *
     * @return A {@link HyggData} object
     */
    public HyggData getData() {
        return this.data;
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
     * Get the real amount of players on the server
     *
     * @return An amount of players
     */
    public int getRealPlayers() {
        return this.realPlayers;
    }

    /**
     * Set the real amount of players on the server
     *
     * @param realPlayers New real players amount
     */
    public void setRealPlayers(int realPlayers) {
        this.realPlayers = realPlayers;
    }

    /**
     * Get the available slots on the server
     *
     * @return A number
     */
    public int getSlots() {
        return this.slots;
    }

    /**
     * Set the server slots
     *
     * @param slots New slots
     */
    public void setSlots(int slots) {
        this.slots = slots;
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
