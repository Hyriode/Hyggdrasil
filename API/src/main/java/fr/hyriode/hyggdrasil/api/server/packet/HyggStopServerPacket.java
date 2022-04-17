package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 14:18<br>
 *
 * This packet can be used to query a new server with a given type.<br>
 * After Hyggdrasil received the packet, it will return the id of the created server.
 */
public class HyggStopServerPacket extends HyggPacket {

    /** The name of the server. Example: lobby-cxs15 */
    private final String serverName;

    /**
     * Constructor of {@link HyggStopServerPacket}
     *
     * @param serverName The server name
     */
    public HyggStopServerPacket(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Get the name of the server
     *
     * @return A server name
     */
    public String getServerName() {
        return this.serverName;
    }

}
