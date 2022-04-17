package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;
import fr.hyriode.hyggdrasil.api.server.HyggServerRequest;

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
    /** The data to provide to the server */
    private final HyggData serverData;

    /**
     * Constructor of {@link HyggStartServerPacket}
     *
     * @param serverType The server type
     * @param serverOptions The options to set to the server
     * @param serverData The dictionary of data
     */
    public HyggStartServerPacket(String serverType, HyggServerOptions serverOptions, HyggData serverData) {
        this.serverType = serverType;
        this.serverOptions = serverOptions;
        this.serverData = serverData;
    }

    /**
     * Create a {@link HyggStartServerPacket} from a {@link HyggServerRequest} object
     *
     * @param request The request
     */
    public HyggStartServerPacket(HyggServerRequest request) {
        this(request.getServerType(), request.getServerOptions(), request.getServerData());
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

    /**
     * Get the data to provide to the server
     *
     * @return A {@link HyggData} object
     */
    public HyggData getServerData() {
        return this.serverData;
    }

}
