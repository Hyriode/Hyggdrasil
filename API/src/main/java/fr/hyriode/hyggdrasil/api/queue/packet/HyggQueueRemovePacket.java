package fr.hyriode.hyggdrasil.api.queue.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueGroup;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueInfo;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 21:27
 */
public abstract class HyggQueueRemovePacket extends HyggPacket {

    public static class Response extends HyggResponseContent {

        private final ResponseType type;
        private final HyggQueueGroup group;
        private final HyggQueueInfo queueInfo;

        public Response(ResponseType type, HyggQueueGroup group, HyggQueueInfo queueInfo) {
            this.type = type;
            this.group = group;
            this.queueInfo = queueInfo;
        }

        public ResponseType getType() {
            return this.type;
        }

        public HyggQueueGroup getGroup() {
            return this.group;
        }

        public HyggQueueInfo getQueueInfo() {
            return this.queueInfo;
        }

    }

    public enum ResponseType {

        NOT_IN_QUEUE,
        REMOVED,
        UNKNOWN

    }

}
