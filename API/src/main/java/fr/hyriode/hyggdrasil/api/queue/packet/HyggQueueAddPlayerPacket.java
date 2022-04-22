package fr.hyriode.hyggdrasil.api.queue.packet;

import fr.hyriode.hyggdrasil.api.queue.HyggQueuePlayer;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 09:03
 */
public class HyggQueueAddPlayerPacket extends HyggQueueAddPacket {

    private final HyggQueuePlayer player;

    public HyggQueueAddPlayerPacket(HyggQueuePlayer player, String game, String gameType, String map) {
        super(game, gameType, map);
        this.player = player;
    }

    public HyggQueuePlayer getPlayer() {
        return this.player;
    }

}
