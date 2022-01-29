package fr.hyriode.hyggdrasil.api.protocol.packet.model.server;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 14:18
 */
public class HyggStartServerPacket extends HyggPacket {

    /** The type of the server. Example: lobby */
    private final String serverType;

    /**
     * Constructor of {@link HyggStartServerPacket}
     *
     * @param serverType The server type
     */
    public HyggStartServerPacket(String serverType) {
        this.serverType = serverType;
    }

    /**
     * Get the type of the server to stop
     *
     * @return A server type
     */
    public String getServerType() {
        return this.serverType;
    }

}
