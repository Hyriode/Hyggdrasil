package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;

import java.util.List;
import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 11:28
 */
public class HyggServerInfoPacket extends HyggPacket {

    /** The current state of the server */
    private final HyggServerState state;
    /** The current players on the server */
    private final List<UUID> players;
    /** The current players playing on the server */
    private final List<UUID> playersPlaying;
    /** The time when the server started */
    private final long startedTime;
    /** The options of the server */
    private final HyggServerOptions options;
    /** The data of the server */
    private final HyggData data;
    /** The slots of the server */
    private final int slots;

    /**
     * Constructor of {@link HyggServerInfoPacket}
     * @param state The current state of the server
     * @param players The players on the server
     * @param playersPlaying The players playing on the server
     * @param startedTime The time when the server started
     * @param options The options of the server
     * @param data The data of the server
     * @param slots The slots of the server
     */
    public HyggServerInfoPacket(HyggServerState state, List<UUID> players, List<UUID> playersPlaying, long startedTime, HyggServerOptions options, HyggData data, int slots) {
        this.state = state;
        this.players = players;
        this.playersPlaying = playersPlaying;
        this.startedTime = startedTime;
        this.options = options;
        this.data = data;
        this.slots = slots;
    }

    /**
     * Get the current state of the server
     *
     * @return A {@link HyggServerState}
     */
    public HyggServerState getState() {
        return this.state;
    }

    /**
     * Get the current amount of players that are on the server
     *
     * @return An amount of players
     */
    public List<UUID> getPlayers() {
        return this.players;
    }

    /**
     * Get the current amount of players that are playing on the server
     *
     * @return An amount of players
     */
    public List<UUID> getPlayersPlaying() {
        return this.playersPlaying;
    }

    /**
     * Get the time when the server started (a timestamp in millis)
     *
     * @return A timestamp
     */
    public long getStartedTime() {
        return this.startedTime;
    }

    /**
     * Get the options of the server
     *
     * @return A {@link HyggServerOptions} object
     */
    public HyggServerOptions getOptions() {
        return this.options;
    }

    /**
     * Get all the data provided to the server at its start
     *
     * @return A {@link HyggData} object
     */
    public HyggData getData() {
        return this.data;
    }

    /**
     * Get the slots available on the server
     *
     * @return A maximum amount of players
     */
    public int getSlots() {
        return this.slots;
    }

}
