package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Created by AstFaster
 * on 02/05/2023 at 21:52.<br>
 *
 * This packet is used to pause a server.
 */
public class HyggPauseServerPacket extends HyggPacket {

    private final String serverName;

    public HyggPauseServerPacket(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return this.serverName;
    }

}
