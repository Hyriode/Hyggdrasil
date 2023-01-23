package fr.hyriode.hyggdrasil.api.limbo;

import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.util.serializer.HyggSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 25/12/2022 at 15:20
 */
public class HyggLimbo {

    /** The maximum amount of players that can handle a limbo */
    public static final int MAX_PLAYERS = 2000;

    /** The name of the limbo */
    protected final String name;

    /** The data of the limbo */
    protected HyggData data;
    /** The current state of the limbo */
    protected State state;
    /** The type of the limbo */
    protected Type type;
    /** The current players handled by the limbo */
    protected Set<UUID> players;

    /** The timestamp when the limbo started (in milliseconds) */
    protected final long startedTime;
    /** The last heartbeat of the limbo */
    protected long lastHeartbeat = -1;

    /**
     * Default constructor of a {@link HyggLimbo}
     *
     * @param type The type of the limbo
     * @param data The data of the limbo
     */
    public HyggLimbo(Type type, HyggData data) {
        this.name = "limbo-" + UUID.randomUUID().toString().substring(0, 5);
        this.data = data;
        this.type = type;
        this.state = State.CREATING;
        this.players = new HashSet<>();
        this.startedTime = System.currentTimeMillis();
    }

    /**
     * Full constructor of a {@link HyggLimbo}
     *
     * @param name The name of the limbo
     * @param data The data of the limbo
     * @param type The type of the limbo
     * @param state The state of the limbo
     * @param players The players connected through the limbo
     * @param startedTime The time when the limbo started (in milliseconds)
     */
    public HyggLimbo(String name, HyggData data, Type type, State state, Set<UUID> players, long startedTime) {
        this.name = name;
        this.data = data;
        this.type = type;
        this.state = state;
        this.players = players;
        this.startedTime = startedTime;
    }

    /**
     * Get the name of the limbo.
     *
     * @return A name. E.g. limbo-ds567
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Get the data of the limbo
     *
     * @return A {@link HyggData} object
     */
    @NotNull
    public HyggData getData() {
        return this.data;
    }

    /**
     * Set the data of the limbo
     *
     * @param data The new data
     */
    public void setData(@NotNull HyggData data) {
        this.data = data;
    }

    /**
     * Get the type of the limbo
     *
     * @return A {@link Type}
     */
    @NotNull
    public Type getType() {
        return this.type;
    }

    /**
     * Set the type of the limbo
     *
     * @param type A new {@link Type}
     */
    public void setType(@NotNull Type type) {
        this.type = type;
    }

    /**
     * Get the current state of the limbo
     *
     * @return A {@link HyggLimbo.State}
     */
    @NotNull
    public HyggLimbo.State getState() {
        return this.state;
    }

    /**
     * Set the current state of the limbo
     *
     * @param state The new {@link HyggLimbo.State}
     */
    public void setState(@NotNull HyggLimbo.State state) {
        this.state = state;
    }

    /**
     * Get the current players handled by the limbo.
     *
     * @return A set of players
     */
    @NotNull
    public Set<UUID> getPlayers() {
        return this.players;
    }

    /**
     * Set the current players handled by the limbo.
     *
     * @param players The new players
     */
    public void setPlayers(@NotNull Set<UUID> players) {
        this.players = players;
    }

    /**
     * Get the time when the limbo started
     *
     * @return A timestamp (in milliseconds)
     */
    public long getStartedTime() {
        return this.startedTime;
    }

    /**
     * Set the last heartbeat of the limbo
     *
     * @return <code>true</code> if it's the first heartbeat of the limbo
     */
    public boolean heartbeat() {
        final long oldHeartbeat = this.lastHeartbeat;

        if (oldHeartbeat == -1) {
            this.state = HyggLimbo.State.STARTING;
        }

        this.lastHeartbeat = System.currentTimeMillis();

        return oldHeartbeat == -1;
    }

    /**
     * Get the last heartbeat of the limbo
     *
     * @return A timestamp (in milliseconds)
     */
    public long getLastHeartbeat() {
        return this.lastHeartbeat;
    }

    /** The different types of limbo that could exist */
    public enum Type {

        /** The limbo is used to handle afk players */
        AFK,
        /** The limbo is used to handle the login of the players */
        LOGIN

    }

    /** The available states a limbo could have */
    public enum State {

        /** The limbo is in creation (Docker just created it) */
        CREATING(0),
        /** The limbo is starting */
        STARTING(1),
        /** The limbo is ready to work and manage players */
        READY(2),
        /** The limbo is stopping */
        SHUTDOWN(3),
        /** limbo is idling (an error occurred or just freezing) */
        IDLE(4);

        /** The identifier of the state */
        private final int id;

        /**
         * Constructor of {@link HyggLimbo.State}
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
