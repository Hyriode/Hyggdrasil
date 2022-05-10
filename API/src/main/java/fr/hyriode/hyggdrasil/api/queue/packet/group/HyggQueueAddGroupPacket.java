package fr.hyriode.hyggdrasil.api.queue.packet.group;

import fr.hyriode.hyggdrasil.api.queue.HyggQueueGroup;
import fr.hyriode.hyggdrasil.api.queue.packet.HyggQueueAddPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 15:20
 */
public class HyggQueueAddGroupPacket extends HyggQueueAddPacket {

    private final HyggQueueGroup group;

    public HyggQueueAddGroupPacket(HyggQueueGroup group, String game, String gameType, String map) {
        super(game, gameType, map);
        this.group = group;
    }

    public HyggQueueGroup getGroup() {
        return this.group;
    }

}
