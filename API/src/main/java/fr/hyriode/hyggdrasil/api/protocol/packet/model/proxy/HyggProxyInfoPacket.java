package fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxyState;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 11:28
 */
public class HyggProxyInfoPacket extends HyggPacket {

    /** The current state of the proxy */
    private final HyggProxyState state;
    /** The current amount of players on the proxy */
    private final int players;
    /** The time when the proxy started */
    private final long startedTime;

    /**
     * Constructor of {@link HyggProxyInfoPacket}
     *  @param state The current state of the proxy
     * @param players The amount of players on the proxy
     * @param startedTime The time when the proxy started
     */
    public HyggProxyInfoPacket(HyggProxyState state, int players, long startedTime) {
        this.state = state;
        this.players = players;
        this.startedTime = startedTime;
    }

    /**
     * Get the current state of the proxy
     *
     * @return A {@link HyggProxyState}
     */
    public HyggProxyState getState() {
        return this.state;
    }

    /**
     * Get the current amount of players that are on the proxy
     *
     * @return An amount of players
     */
    public int getPlayers() {
        return this.players;
    }

    /**
     * Get the time when the proxy started (a timestamp in millis)
     *
     * @return A timestamp
     */
    public long getStartedTime() {
        return this.startedTime;
    }

}
