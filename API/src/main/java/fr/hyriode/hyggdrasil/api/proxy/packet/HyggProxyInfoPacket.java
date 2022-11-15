package fr.hyriode.hyggdrasil.api.proxy.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.server.packet.HyggServerInfoPacket;
import org.jetbrains.annotations.NotNull;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 11:28.<br>
 *
 * Packet sent to update the information of a proxy.
 */
public class HyggProxyInfoPacket extends HyggPacket {

    /** The representation of the proxy's information */
    private final HyggProxy proxy;

    /**
     * Create a {@link HyggServerInfoPacket}
     *
     * @param proxy The proxy
     */
    public HyggProxyInfoPacket(@NotNull HyggProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Get the proxy containing useful information
     *
     * @return The {@link HyggProxy} object
     */
    @NotNull
    public HyggProxy getProxy() {
        return this.proxy;
    }


}
