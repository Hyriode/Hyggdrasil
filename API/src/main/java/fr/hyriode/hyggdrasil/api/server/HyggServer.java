package fr.hyriode.hyggdrasil.api.server;

import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 13:48
 */
public class HyggServer {

    public static final String SUB_TYPE_KEY = "subtype";
    public static final String MAP_KEY = "map";

    /** Server's name */
    protected final String name;
    /** Server's type */
    protected final String type;

    /** Current server's state */
    protected HyggServerState state;
    /** Server's options (pvp, nether, etc.) */
    protected HyggServerOptions options;
    /** The data provided to the server */
    protected HyggData data;

    /** Current players on the server */
    protected List<UUID> players;
    /** The players playing on the server (not the moderators, spectators, etc) */
    protected List<UUID> playingPlayers;
    /** The available slots on the network */
    protected int slots = -1;

    /** Server's started time (timestamp) */
    protected final long startedTime;
    /** The last time that the server send a heartbeat */
    protected long lastHeartbeat = -1;

    /** Is the server accessible to normal players (not moderators) */
    protected boolean accessible;

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
        this.players = new ArrayList<>();
        this.playingPlayers = new ArrayList<>();
    }

    /**
     * Full constructor of {@link HyggServer}
     *
     * @param name The name of the server
     * @param state The current state of the server
     * @param players The current of players on the server
     * @param playersPlaying The current of players playing on the server
     * @param startedTime The time when the server started
     * @param options Server's options (pvp, nether, etc.)
     * @param data Server's data
     */
    public HyggServer(String name, HyggServerState state, List<UUID> players, List<UUID> playersPlaying, long startedTime, HyggServerOptions options, HyggData data) {
        this.name = name;
        this.type = getTypeFromName(name);
        this.state = state;
        this.options = options;
        this.data = data;
        this.players = players;
        this.playingPlayers = playersPlaying;
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
     * Get the subtype of the server.<br>
     * It can be null, because not all servers have a subtype!
     *
     * @return A game type. Ex: DEFAULT, FOUR_FOUR, etc.
     */
    public String getSubType() {
        return this.data.get(SUB_TYPE_KEY);
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
     * Set the server's options
     *
     * @param options New {@link HyggServerOptions}
     */
    public void setOptions(HyggServerOptions options) {
        this.options = options;
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
     * Set server's data
     *
     * @param data The {@link HyggData} object
     */
    public void setData(HyggData data) {
        this.data = data;
    }

    /**
     * Get all the players on the server
     *
     * @return The list of players
     */
    public List<UUID> getPlayers() {
        return this.players;
    }

    /**
     * Set all the players that are on the server
     *
     * @param players A list of players
     */
    public void setPlayers(List<UUID> players) {
        this.players = players;
    }

    /**
     * Get all the players playing on the server
     *
     * @return The list of players playing
     */
    public List<UUID> getPlayingPlayers() {
        return this.playingPlayers;
    }

    /**
     * Set all the players playing on the server
     *
     * @param playingPlayers A list of players playing
     */
    public void setPlayingPlayers(List<UUID> playingPlayers) {
        this.playingPlayers = playingPlayers;
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

    /**
     * Set if the server is accessible or not
     *
     * @param accessible The accessibility value of the server
     */
    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    /**
     * Check if the server is accessible
     *
     * @return <code>true</code> if yes
     */
    public boolean isAccessible() {
        return this.accessible;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
