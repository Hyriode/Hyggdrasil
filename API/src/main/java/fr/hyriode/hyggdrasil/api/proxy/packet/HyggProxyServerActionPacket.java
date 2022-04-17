package fr.hyriode.hyggdrasil.api.proxy.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 13:53
 */
public class HyggProxyServerActionPacket extends HyggPacket {

    /**
     * The enum with all actions that can be done on a proxy for a given server
     */
    public enum Action {
        ADD, REMOVE
    }

    /** The action to do */
    private final Action action;
    /** The name of the server */
    private final String serverName;
    /** The listening port of the server */
    private final int serverPort;

    /**
     * Constructor of {@link HyggProxyServerActionPacket}
     *
     * @param action The action to do
     * @param serverName The server's name
     * @param serverPort The server's port
     */
    public HyggProxyServerActionPacket(Action action, String serverName, int serverPort) {
        this.action = action;
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    /**
     * Constructor of {@link HyggProxyServerActionPacket}
     *
     * @param action The action to do
     * @param serverName The server's name
     */
    public HyggProxyServerActionPacket(Action action, String serverName) {
        this(action, serverName, 25565);
    }

    /**
     * Get the action to do on a given server
     *
     * @return An {@link Action}
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Get the name of the server.<br>
     * For example: lobby-cds85s
     *
     * @return A name
     */
    public String getServerName() {
        return this.serverName;
    }

    /**
     * Get the listening port of the server.<br>
     * By default, the port is 25565
     *
     * @return A port
     */
    public int getServerPort() {
        return this.serverPort;
    }

}
