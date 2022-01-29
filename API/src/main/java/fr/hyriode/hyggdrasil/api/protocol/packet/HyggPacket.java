package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

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

    /**
     * This method transforms this Java object in a Json format
     *
     * @return A json of the packet
     */
    public String asJson() {
        return HyggdrasilAPI.GSON.toJson(this);
    }

}
