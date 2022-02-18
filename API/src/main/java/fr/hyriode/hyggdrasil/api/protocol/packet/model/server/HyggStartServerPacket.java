package fr.hyriode.hyggdrasil.api.protocol.packet.model.server;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 14:18
 */
public class HyggStartServerPacket extends HyggPacket {

    /** The type of the server. Example: lobby */
    private final String serverType;
    /** The options to set to the server */
    private final HyggServerOptions serverOptions;

    /**
     * Constructor of {@link HyggStartServerPacket}
     *
     * @param serverType The server type
     * @param serverOptions The options to set to the server
     */
    public HyggStartServerPacket(String serverType, HyggServerOptions serverOptions) {
        this.serverType = serverType;
        this.serverOptions = serverOptions;
    }

    /**
     * Constructor of {@link HyggStartServerPacket}
     *
     * @param serverType The server type
     */
    public HyggStartServerPacket(String serverType) {
        this.serverType = serverType;
        this.serverOptions = new HyggServerOptions();
    }

    /**
     * Get the type of the server to stop
     *
     * @return A server type
     */
    public String getServerType() {
        return this.serverType;
    }

    /**
     * Get the options of the server to start
     *
     * @return A {@link HyggServerOptions} object
     */
    public HyggServerOptions getServerOptions() {
        return this.serverOptions;
    }

}
