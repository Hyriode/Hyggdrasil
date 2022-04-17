package fr.hyriode.hyggdrasil.api.queue.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 17/04/2022 at 10:12
 */
public class HyggQueueTransferPacket extends HyggPacket {

    private final UUID playerId;
    private final String serverName;

    public HyggQueueTransferPacket(UUID playerId, String serverName) {
        this.playerId = playerId;
        this.serverName = serverName;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public String getServerName() {
        return this.serverName;
    }

}
