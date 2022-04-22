package fr.hyriode.hyggdrasil.api.queue.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueGroup;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 19/04/2022 at 15:55
 */
public class HyggQueueUpdateGroupPacket extends HyggPacket {

    private final HyggQueueGroup group;

    public HyggQueueUpdateGroupPacket(HyggQueueGroup group) {
        this.group = group;
    }

    public HyggQueueGroup getGroup() {
        return this.group;
    }

    public enum Response {

        UPDATED,
        NOT_IN_QUEUE;

        public HyggResponseContent asContent() {
            return new HyggQueueUpdateGroupPacket.Response.Content(this);
        }

        public static class Content extends HyggResponseContent {

            private final HyggQueueUpdateGroupPacket.Response type;

            public Content(HyggQueueUpdateGroupPacket.Response type) {
                this.type = type;
            }

            public HyggQueueUpdateGroupPacket.Response getType() {
                return this.type;
            }

        }

    }

}
