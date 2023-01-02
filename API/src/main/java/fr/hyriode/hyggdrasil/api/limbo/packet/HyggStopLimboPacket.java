package fr.hyriode.hyggdrasil.api.limbo.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 14:18.<br>
 *
 * The packet used to stop a limbo.
 */
public class HyggStopLimboPacket extends HyggPacket {

    /** The name of the limbo. Example: limbo-s1vm5 */
    private final String limboName;

    /**
     * Constructor of {@link HyggStopLimboPacket}
     *
     * @param limboName The limbo name
     */
    public HyggStopLimboPacket(String limboName) {
        this.limboName = limboName;
    }

    /**
     * Get the name of the limbo to stop
     *
     * @return A limbo name
     */
    public String getLimboName() {
        return this.limboName;
    }

}
