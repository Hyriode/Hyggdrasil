package fr.hyriode.hyggdrasil.api.protocol.packet;

import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 23/12/2021 at 18:09
 */
public abstract class HyggPacket {

    /** Packet's {@link UUID} */
    private final UUID uniqueId;

    /**
     * Constructor of {@link HyggPacket}
     */
    public HyggPacket() {
        this.uniqueId = UUID.randomUUID();
    }

    /**
     * Get the unique id of the packet
     *
     * @return {@link UUID}
     */
    public UUID getUniqueId() {
        return this.uniqueId;
    }

}
