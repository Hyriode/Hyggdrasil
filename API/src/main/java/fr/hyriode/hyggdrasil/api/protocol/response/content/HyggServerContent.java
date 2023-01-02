package fr.hyriode.hyggdrasil.api.protocol.response.content;

import fr.hyriode.hyggdrasil.api.server.HyggServer;

/**
 * Created by AstFaster
 * on 14/11/2022 at 20:46
 */
public class HyggServerContent extends HyggResponseContent {

    private final HyggServer server;

    public HyggServerContent(HyggServer server) {
        this.server = server;
    }

    public HyggServer getServer() {
        return this.server;
    }

}
