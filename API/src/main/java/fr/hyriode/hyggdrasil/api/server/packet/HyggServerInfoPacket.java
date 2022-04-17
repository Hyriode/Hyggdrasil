package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 11:28
 */
public class HyggServerInfoPacket extends HyggPacket {

    /** The current state of the server */
    private final HyggServerState state;
    /** The current amount of players on the server */
    private final int players;
    /** The time when the server started */
    private final long startedTime;
    /** The options of the server */
    private final HyggServerOptions options;
    /** The data of the server */
    private final HyggData data;

    /**
     * Constructor of {@link HyggServerInfoPacket}
     * @param state The current state of the server
     * @param players The amount of players on the server
     * @param startedTime The time when the server started
     * @param options The options of the server
     * @param data The data of the server
     */
    public HyggServerInfoPacket(HyggServerState state, int players, long startedTime, HyggServerOptions options, HyggData data) {
        this.state = state;
        this.players = players;
        this.startedTime = startedTime;
        this.options = options;
        this.data = data;
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
    public int getPlayers() {
        return this.players;
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

}
