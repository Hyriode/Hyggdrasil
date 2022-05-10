package fr.hyriode.hyggdrasil.api.queue.packet.group;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueGroup;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueInfo;

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

    public static class Response extends HyggResponseContent {

        private final ResponseType type;
        private final HyggQueueInfo queueInfo;

        public Response(ResponseType type, HyggQueueInfo queueInfo) {
            this.type = type;
            this.queueInfo = queueInfo;
        }

        public ResponseType getType() {
            return this.type;
        }

        public HyggQueueInfo getQueueInfo() {
            return this.queueInfo;
        }

    }

    public enum ResponseType {

        UPDATED,
        NOT_IN_QUEUE

    }

}
