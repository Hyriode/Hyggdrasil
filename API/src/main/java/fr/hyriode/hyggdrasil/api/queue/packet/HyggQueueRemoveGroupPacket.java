package fr.hyriode.hyggdrasil.api.queue.packet;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 18:33
 */
public class HyggQueueRemoveGroupPacket extends HyggQueueRemovePacket {

    private final UUID groupId;

    public HyggQueueRemoveGroupPacket(UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getGroupId() {
        return this.groupId;
    }

}
