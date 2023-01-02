package fr.hyriode.hyggdrasil.api.limbo.packet;

import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 11:28.<br>
 *
 * Packet sent to update the information of a limbo.
 */
public class HyggLimboInfoPacket extends HyggPacket {

    /** The representation of the limbo's information */
    private final HyggLimbo limbo;

    /**
     * Create a {@link HyggLimboInfoPacket}
     *
     * @param limbo The limbo
     */
    public HyggLimboInfoPacket(@NotNull HyggLimbo limbo) {
        this.limbo = limbo;
    }

    /**
     * Get the limbo object containing useful information
     *
     * @return The {@link HyggLimbo} object
     */
    @NotNull
    public HyggLimbo getLimbo() {
        return this.limbo;
    }


}
