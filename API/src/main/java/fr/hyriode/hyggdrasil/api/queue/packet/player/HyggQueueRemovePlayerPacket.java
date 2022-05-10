package fr.hyriode.hyggdrasil.api.queue.packet.player;

import fr.hyriode.hyggdrasil.api.queue.packet.HyggQueueRemovePacket;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 18:32
 */
public class HyggQueueRemovePlayerPacket extends HyggQueueRemovePacket {

    private final UUID playerId;

    public HyggQueueRemovePlayerPacket(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

}
