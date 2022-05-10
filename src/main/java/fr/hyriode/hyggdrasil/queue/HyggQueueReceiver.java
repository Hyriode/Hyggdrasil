package fr.hyriode.hyggdrasil.queue;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.request.HyggRequestHeader;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.IHyggResponse;
import fr.hyriode.hyggdrasil.api.queue.packet.group.HyggQueueAddGroupPacket;
import fr.hyriode.hyggdrasil.api.queue.packet.group.HyggQueueRemoveGroupPacket;
import fr.hyriode.hyggdrasil.api.queue.packet.group.HyggQueueUpdateGroupPacket;
import fr.hyriode.hyggdrasil.api.queue.packet.player.HyggQueueAddPlayerPacket;
import fr.hyriode.hyggdrasil.api.queue.packet.player.HyggQueueRemovePlayerPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 09:08
 */
public class HyggQueueReceiver implements IHyggPacketReceiver {

    private final HyggQueueManager queueManager;

    public HyggQueueReceiver(HyggQueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @Override
    public IHyggResponse receive(String channel, HyggPacket packet, HyggRequestHeader header) {
        if (packet instanceof final HyggQueueAddPlayerPacket queuePacket) {
            return this.queueManager.handlePacket(queuePacket);
        } else if (packet instanceof final HyggQueueUpdateGroupPacket queuePacket) {
            return this.queueManager.handlePacket(queuePacket);
        } else if (packet instanceof final HyggQueueAddGroupPacket queuePacket) {
            return this.queueManager.handlePacket(queuePacket);
        } else if (packet instanceof final HyggQueueRemovePlayerPacket queuePacket) {
            return this.queueManager.handlePacket(queuePacket);
        } else if (packet instanceof final HyggQueueRemoveGroupPacket queuePacket) {
            return this.queueManager.handlePacket(queuePacket);
        }
        return HyggResponse.Type.NONE;
    }

}
