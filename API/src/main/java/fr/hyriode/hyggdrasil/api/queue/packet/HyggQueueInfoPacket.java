package fr.hyriode.hyggdrasil.api.queue.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueGroup;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueInfo;
import fr.hyriode.hyggdrasil.api.queue.HyggQueuePlayer;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 17/04/2022 at 10:16
 */
public class HyggQueueInfoPacket extends HyggPacket {

    private final HyggQueuePlayer player;
    private final HyggQueueGroup group;
    private final HyggQueueInfo queueInfo;

    private final int place;

    public HyggQueueInfoPacket(HyggQueuePlayer player, HyggQueueGroup group, HyggQueueInfo queueInfo, int place) {
        this.player = player;
        this.group = group;
        this.queueInfo = queueInfo;
        this.place = place;
    }

    public HyggQueuePlayer getPlayer() {
        return this.player;
    }

    public HyggQueueGroup getGroup() {
        return this.group;
    }

    public HyggQueueInfo getQueueInfo() {
        return this.queueInfo;
    }

    public int getPlace() {
        return this.place;
    }

}
