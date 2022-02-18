package fr.hyriode.hyggdrasil.api.protocol.response.content;

import fr.hyriode.hyggdrasil.api.server.HyggServer;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 11:06
 */
public class HyggServerResponse extends HyggResponseContent {

    /** The {@link HyggServer} provided with the response */
    private final HyggServer server;

    /**
     * Constructor of {@link HyggServerResponse}
     *
     * @param server The server to send with the response
     */
    public HyggServerResponse(HyggServer server) {
        this.server = server;
    }

    /**
     * Get the server provided with the response
     *
     * @return A {@link HyggServer}
     */
    public HyggServer getServer() {
        return this.server;
    }

}
