package fr.hyriode.hyggdrasil.api.proxy;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:09
 */
public class HyggProxy {

    /** The maximum amount of players that can handle a proxy */
    public static final int MAX_PLAYERS = 300;

    /** Proxy's name */
    protected final String name;

    /** Proxy's opened port */
    protected int port;
    /** Current number of players on the proxy */
    protected int players;
    /** Proxy's state */
    protected HyggProxyState state;

    /** Proxy's started time (a timestamp in millis) */
    protected final long startedTime;
    /** Proxy's last heartbeat */
    protected long lastHeartbeat = -1;

    /**
     * Constructor of {@link HyggProxy}
     *
     * @param startedTime Proxy's started time
     */
    public HyggProxy(long startedTime) {
        this.name = "proxy-" + UUID.randomUUID().toString().substring(0, 5);
        this.state = HyggProxyState.CREATING;
        this.startedTime = startedTime;
    }

    /**
     * Full constructor of {@link HyggProxy}
     *
     * @param name The name of the proxy
     * @param players The amount of players on the proxy
     * @param state The current state of the proxy
     * @param startedTime The time when the proxy started
     */
    public HyggProxy(String name, int players, HyggProxyState state, long startedTime) {
        this.name = name;
        this.players = players;
        this.state = state;
        this.startedTime = startedTime;
    }

    /**
     * Constructor of {@link HyggProxy}
     */
    public HyggProxy() {
        this(System.currentTimeMillis());
    }

    /**
     * Get proxy's name (ex: proxy-ds567)
     *
     * @return Proxy's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get proxy's started time (a timestamp in millis)
     *
     * @return Proxy's started time
     */
    public long getStartedTime() {
        return this.startedTime;
    }

    /**
     * Get proxy's opened port
     *
     * @return Proxy's port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Set proxy's opened port<br>
     * Warning: if the proxy's port is already set, it will throw an exception
     *
     * @param port New proxy's port
     */
    public void setPort(int port) {
        if (this.port == 0) {
            this.port = port;
        } else {
            throw new IllegalStateException("Proxy's opened port is already set!");
        }
    }

    /**
     * Get current number of players on the proxy
     *
     * @return Number of players
     */
    public int getPlayers() {
        return this.players;
    }

    /**
     * Set current number of players on the proxy
     *
     * @param players New number of players
     */
    public void setPlayers(int players) {
        this.players = players;
    }

    /**
     * Get current proxy's state
     *
     * @return {@link HyggProxyState}
     */
    public HyggProxyState getState() {
        return this.state;
    }

    /**
     * Set current proxy's state
     *
     * @param state New {@link HyggProxyState}
     */
    public void setState(HyggProxyState state) {
        this.state = state;
    }

    /**
     * Set the last heartbeat of the proxy
     *
     * @return <code>true</code> if it's the first heartbeat of the proxy
     */
    public boolean heartbeat() {
        final long oldHeartbeat = this.lastHeartbeat;

        if (this.state == HyggProxyState.CREATING) {
            this.state = HyggProxyState.STARTING;
        }
        this.lastHeartbeat = System.currentTimeMillis();

        return oldHeartbeat == -1;
    }

    /**
     * Get the last heartbeat of the proxy
     *
     * @return A timestamp
     */
    public long getLastHeartbeat() {
        return this.lastHeartbeat;
    }

}
