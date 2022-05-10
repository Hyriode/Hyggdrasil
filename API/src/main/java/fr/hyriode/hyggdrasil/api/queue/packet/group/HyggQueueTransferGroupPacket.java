package fr.hyriode.hyggdrasil.api.queue.packet.group;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 17/04/2022 at 10:12
 */
public class HyggQueueTransferGroupPacket extends HyggPacket {

    private final UUID groupId;
    private final String serverName;

    public HyggQueueTransferGroupPacket(UUID groupId, String serverName) {
        this.groupId = groupId;
        this.serverName = serverName;
    }

    public UUID getGroupId() {
        return this.groupId;
    }

    public String getServerName() {
        return this.serverName;
    }

}
