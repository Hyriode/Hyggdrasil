package fr.hyriode.hyggdrasil.api.queue.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 18:33
 */
public class HyggQueueRemoveGroupPacket extends HyggQueueRemovePacket {

    private final String groupId;

    public HyggQueueRemoveGroupPacket(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return this.groupId;
    }

}
