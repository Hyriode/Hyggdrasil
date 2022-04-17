package fr.hyriode.hyggdrasil.api.queue;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 12:50
 */
public class HyggQueuePlayer {

    private final UUID uniqueId;
    private final int priority;

    public HyggQueuePlayer(UUID uniqueId, int priority) {
        this.uniqueId = uniqueId;
        this.priority = priority;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public int getPriority() {
        return this.priority;
    }

}
