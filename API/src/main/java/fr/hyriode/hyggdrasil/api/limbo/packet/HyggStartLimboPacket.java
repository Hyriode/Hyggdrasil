package fr.hyriode.hyggdrasil.api.limbo.packet;

import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

import static fr.hyriode.hyggdrasil.api.limbo.HyggLimbo.Type;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 14:18<br>
 *
 * This packet can be used to query a new limbo.<br>
 * After Hyggdrasil received the packet, it will return the created limbo.
 */
public class HyggStartLimboPacket extends HyggPacket {

    /** The type of the limbo to create */
    private final Type type;
    /** The data of the limbo to create */
    private final HyggData data;

    /**
     * Create a {@link HyggStartLimboPacket}
     *
     * @param type The type of the limbo
     * @param data The data of the limbo
     */
    public HyggStartLimboPacket(Type type, HyggData data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Get the type of the limbo to create
     *
     * @return A {@link Type}
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Get the data of the limbo to create
     *
     * @return A {@link HyggData} object
     */
    public HyggData getData() {
        return this.data;
    }

}
