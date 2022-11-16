package fr.hyriode.hyggdrasil.api.server;

import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.util.serializer.HyggSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 13:48.<br>
 *
 * The main object of what a server is.
 */
public class HyggServer implements HyggSerializable {

    /** The name of the server */
    protected final String name;
    /** The type of the server */
    protected final String type;

    /** The type of the game running on the server. Might be <code>null</code> if the server is not running a game. */
    protected @Nullable String gameType;
    /** The map used by the server. */
    protected String map;

    /** The accessibility of the server. */
    protected Accessibility accessibility;
    /** The type of process running on the server */
    protected Process process;

    /** The current state of the server */
    protected State state;
    /** The options of the server (pvp, nether, etc.) */
    protected HyggServerOptions options;
    /** The data provided to the server */
    protected HyggData data;

    /** Current players on the server */
    protected Set<UUID> players;
    /** The players playing on the server (not the moderators, spectators, etc) */
    protected Set<UUID> playingPlayers;
    /** The available slots of the server */
    protected int slots;

    /** The timestamp of when the server started (in milliseconds) */
    protected final long startedTime;
    /** The last time the server sent a heartbeat */
    protected long lastHeartbeat = -1;

    /**
     * Default constructor of a {@link HyggServer}
     *
     * @param name The name of the server
     * @param type The type of the server
     * @param gameType The type of the game running on the server
     * @param map The map used by the server
     * @param accessibility The accessibility of the server
     * @param process The type of process the server is running
     * @param options The options of the server
     * @param data The data of the server
     * @param slots The slots of the server
     */
    public HyggServer(String name, String type, @Nullable String gameType, String map, Accessibility accessibility, Process process, HyggServerOptions options, HyggData data, int slots) {
        this.name = name;
        this.type = type;
        this.gameType = gameType;
        this.map = map;
        this.accessibility = accessibility;
        this.process = process;
        this.state = State.CREATING;
        this.options = options;
        this.data = data;
        this.players = new HashSet<>();
        this.playingPlayers = new HashSet<>();
        this.slots = slots;
        this.startedTime = System.currentTimeMillis();
    }

    /**
     * Secondary constructor of a {@link HyggServer}
     *
     * @param type The type of the server
     * @param gameType The type of the game running on the server
     * @param map The map used by the server
     * @param accessibility The accessibility of the server
     * @param process The type of process the server is running
     * @param options The options of the server
     * @param data The data of the server
     * @param slots The slots of the server
     */
    public HyggServer(String type, @Nullable String gameType, String map, Accessibility accessibility, Process process, HyggServerOptions options, HyggData data, int slots) {
        this(type + "-" + UUID.randomUUID().toString().substring(0, 5), type, gameType, map, accessibility, process, options, data, slots);
    }

    /**
     * Get the name of the server (ex: lobby-5sqf4)
     *
     * @return A server name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the type of the server (ex: rtf, lobby)
     *
     * @return A server type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Get the type of the game running on the server.<br>
     * It can be <code>null</code>, because not all servers run a game!
     *
     * @return A game type. Ex: DEFAULT, FOUR_FOUR, etc.
     */
    @Nullable
    public String getGameType() {
        return this.gameType;
    }

    /**
     * Get the name of the map used on the server
     *
     * @return A map name
     */
    @Nullable
    public String getMap() {
        return this.map;
    }

    /**
     * Set the name of the map used on the server
     *
     * @param map The new map
     */
    public void setMap(String map) {
        this.map = map;
    }

    /**
     * Get the current accessibility of the server.
     *
     * @return A {@link Accessibility}
     */
    @NotNull
    public Accessibility getAccessibility() {
        return this.accessibility;
    }

    /**
     * Set the current accessibility of the server.
     *
     * @param accessibility The new {@link Accessibility} of the server
     */
    public void setAccessibility(@NotNull Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    /**
     * Get the type of process the server is running.
     *
     * @return A {@link Process}
     */
    @NotNull
    public Process getProcess() {
        return this.process;
    }

    /**
     * Set the type of process the server is running.
     *
     * @param process The new {@link Process} of the server
     */
    public void setProcess(@NotNull Process process) {
        this.process = process;
    }

    /**
     * Get the current state of the server
     *
     * @return A {@link State}
     */
    public State getState() {
        return this.state;
    }

    /**
     * Set the new state of the server
     *
     * @param state The new state of the server
     */
    public void setState(State state) {
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
    public Set<UUID> getPlayers() {
        return this.players;
    }

    /**
     * Set all the players that are on the server
     *
     * @param players A set of players
     */
    public void setPlayers(Set<UUID> players) {
        this.players = players;
    }

    /**
     * Get all the players playing on the server
     *
     * @return The list of players playing
     */
    public Set<UUID> getPlayingPlayers() {
        return this.playingPlayers;
    }

    /**
     * Set all the players playing on the server
     *
     * @param playingPlayers A set of players playing
     */
    public void setPlayingPlayers(Set<UUID> playingPlayers) {
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

        if (oldHeartbeat == -1) {
            this.state = State.STARTING;
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

    /** This enum represents the accessibility of the server across network. */
    public enum Accessibility {

        /** The server is public: everyone can access it. */
        PUBLIC,
        /** The server is a host: only whitelisted players and the owner can access it. */
        HOST

    }

    /** This enum represents the type of process a server is running. */
    public enum Process {

        /** The server is running a "permanent" process. The server doesn't stop by itself. E.g. a lobby or FFA server */
        PERMANENT,
        /** The server is running a "temporary" process. The server stops by itself. E.g. a basic game server */
        TEMPORARY

    }

    /** This enum represents the different states a server can have. */
    public enum State {

        /** Server is in creation (Docker just created it) */
        CREATING(0),
        /** Server is starting (onEnable in plugin) */
        STARTING(1),
        /** Server is ready to host players */
        READY(2),
        /** Server is playing a game */
        PLAYING(3),
        /** Server is stopping (onDisable in plugin) */
        SHUTDOWN(4),
        /** Server is idling (an error occurred or just freezing) */
        IDLE(5);

        /** The identifier of the state */
        private final int id;

        /**
         * The constructor of a {@link State}
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
