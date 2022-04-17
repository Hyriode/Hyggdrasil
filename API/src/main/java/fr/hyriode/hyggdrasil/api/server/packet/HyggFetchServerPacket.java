package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/02/2022 at 14:29
 */
public class HyggFetchServerPacket extends HyggPacket {

    /** The name of the server to fetch */
    private final String serverName;

    /**
     * Constructor of {@link HyggFetchServerPacket}
     *
     * @param serverName The name of the server to fetch
     */
    public HyggFetchServerPacket(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Get the name of the server to fetch
     *
     * @return A server name
     */
    public String getServerName() {
        return this.serverName;
    }

}
