package fr.hyriode.hyggdrasil.api.queue.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 15:19
 */
public abstract class HyggQueueAddPacket extends HyggPacket {

    protected final String game;
    protected final String gameType;
    protected final String map;

    public HyggQueueAddPacket(String game, String gameType, String map) {
        this.game = game;
        this.gameType = gameType;
        this.map = map;
    }

    public String getGame() {
        return this.game;
    }

    public String getGameType() {
        return this.gameType;
    }

    public String getMap() {
        return this.map;
    }

    public enum Response {

        INVALID_TYPE,
        ALREADY_IN,
        ADDED;

        public HyggResponseContent asContent() {
            return new Content(this);
        }

        private static class Content extends HyggResponseContent {

            private final Response type;

            public Content(Response type) {
                this.type = type;
            }

            public Response getType() {
                return this.type;
            }

        }

    }

}
