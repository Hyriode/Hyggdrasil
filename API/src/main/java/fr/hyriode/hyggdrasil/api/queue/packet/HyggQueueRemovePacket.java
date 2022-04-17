package fr.hyriode.hyggdrasil.api.queue.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 21:27
 */
public abstract class HyggQueueRemovePacket extends HyggPacket {

    public enum Response {

        NOT_IN_QUEUE,
        REMOVED,
        UNKNOWN;

        public HyggResponseContent asContent() {
            return new HyggQueueRemovePacket.Response.Content(this);
        }

        private static class Content extends HyggResponseContent {

            private final HyggQueueRemovePacket.Response type;

            public Content(HyggQueueRemovePacket.Response type) {
                this.type = type;
            }

            public HyggQueueRemovePacket.Response getType() {
                return this.type;
            }

        }

    }

}
