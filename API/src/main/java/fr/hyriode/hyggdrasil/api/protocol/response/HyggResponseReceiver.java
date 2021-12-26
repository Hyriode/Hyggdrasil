package fr.hyriode.hyggdrasil.api.protocol.response;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketRequest;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.HyggResponsePacket;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 15:33
 */
public class HyggResponseReceiver implements IHyggPacketReceiver {

    /** {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;
    /** The original request */
    private final HyggPacketRequest request;
    /** Number of responses received */
    private int responses;

    /**
     * Constructor of {@link HyggResponseReceiver}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     * @param request The original request
     */
    public HyggResponseReceiver(HyggdrasilAPI hyggdrasilAPI, HyggPacketRequest request) {
        this.hyggdrasilAPI = hyggdrasilAPI;
        this.request = request;
    }

    @Override
    public HyggResponse receive(String channel, HyggPacket packet) {
        if (packet instanceof HyggResponsePacket) {
            final HyggResponsePacket responsePacket = (HyggResponsePacket) packet;

            if (responsePacket.getRespondedPacketUniqueId().equals(this.request.getPacket().getUniqueId())) {
                final HyggResponseCallback callback = this.request.getResponseCallback();

                this.responses++;

                if (this.responses >= this.request.getMaxResponses()) {
                    this.unregister(channel);
                }

                if (callback != null) {
                    callback.call(responsePacket.getResponse());
                }
            }
        }
        return HyggResponse.Type.NONE.toResponse();
    }

    /**
     * Unregister the response receiver
     *
     * @param channel The original channel
     */
    public void unregister(String channel) {
        this.hyggdrasilAPI.getPacketProcessor().unregisterReceiver(channel, this);
    }
    
}
