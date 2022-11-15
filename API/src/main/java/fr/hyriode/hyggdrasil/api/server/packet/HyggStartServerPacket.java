package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServerCreationInfo;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 14:18.<br>
 *
 * The packet used to create a server.
 */
public class HyggStartServerPacket extends HyggPacket {

    /** The information of the server to create */
    private final HyggServerCreationInfo serverInfo;

    /**
     * Constructor of a {@link HyggStartServerPacket}
     *
     * @param serverInfo The information of the server
     */
    public HyggStartServerPacket(@NotNull HyggServerCreationInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    /**
     * Get the information of the server to create
     *
     * @return The {@linkplain  HyggServerCreationInfo server information}
     */
    @NotNull
    public HyggServerCreationInfo getServerInfo() {
        return this.serverInfo;
    }

}