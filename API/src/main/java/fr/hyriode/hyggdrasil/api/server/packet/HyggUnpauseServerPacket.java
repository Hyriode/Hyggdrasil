package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Created by AstFaster
 * on 02/05/2023 at 21:52.<br>
 *
 * This packet is used to unpause a server.
 */
public class HyggUnpauseServerPacket extends HyggPacket {

    private final String serverName;

    public HyggUnpauseServerPacket(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return this.serverName;
    }

}
