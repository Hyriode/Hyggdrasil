package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 11:28.<br>
 * 
 * The information sent by a server on every update.
 */
public class HyggServerInfoPacket extends HyggPacket {

    /** The representation of the server's information */
    private final HyggServer server;

    /**
     * Create a {@link HyggServerInfoPacket}
     *
     * @param server The server
     */
    public HyggServerInfoPacket(@NotNull HyggServer server) {
        this.server = server;
    }

    /**
     * Get the server containing useful information
     *
     * @return The {@link HyggServer} object
     */
    @NotNull
    public HyggServer getServer() {
        return this.server;
    }

}
