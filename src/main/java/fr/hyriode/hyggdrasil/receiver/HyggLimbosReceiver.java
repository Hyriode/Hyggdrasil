package fr.hyriode.hyggdrasil.receiver;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
import fr.hyriode.hyggdrasil.api.limbo.packet.HyggLimboInfoPacket;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.heartbeat.HyggHeartbeatPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketHeader;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.limbo.HyggLimboManager;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 22/01/2022 at 18:23
 */
public class HyggLimbosReceiver implements IHyggPacketReceiver {

    private final Hyggdrasil hyggdrasil;

    public HyggLimbosReceiver(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
    }

    @Override
    public HyggResponse receive(String channel, HyggPacketHeader packetHeader, HyggPacket packet) {
        final HyggApplication sender = packetHeader.getSender();

        if (sender.getType() == HyggApplication.Type.LIMBO) {
            final HyggLimboManager limboManager = this.hyggdrasil.getLimboManager();
            final HyggLimbo limbo = limboManager.getLimbo(sender.getName());

            if (limbo == null) {
                return HyggResponse.Type.ERROR.toResponse();
            }

            if (packet instanceof final HyggLimboInfoPacket info) {
                limboManager.updateLimboInfo(limbo, info);
            }

            if (packet instanceof HyggHeartbeatPacket && limbo.heartbeat()) {
                limboManager.updateLimbo(limbo);
            }
            return HyggResponse.Type.SUCCESS.toResponse();
        }
        return HyggResponse.Type.NONE.toResponse();
    }

}
