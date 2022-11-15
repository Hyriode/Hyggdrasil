package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponsePacket;

import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 11:05
 */
public class HyggRequest {

    /** The {@linkplain HyggPacket packet} to send */
    private HyggPacket packet;
    /** The channel used to send the packet */
    private HyggChannel channel;
    /** The callback to fire when a response to the packet is received */
    private HyggResponseCallback responseCallback;
    /** The callback to fire when no responses were received */
    private Runnable timeoutCallback;
    /** The number of responses to handle */
    private int maxResponses = 1;
    /** Maximum time to wait for all responses (in millis) */
    private long timeout = 5000;

    /** {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggRequest}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggRequest(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
    }

    /**
     * Set the request's packet
     *
     * @param packet New {@link HyggPacket}
     * @return {@link HyggRequest}
     */
    public HyggRequest withPacket(HyggPacket packet) {
        this.packet = packet;
        return this;
    }

    /**
     * Get request's packet
     *
     * @return {@link HyggPacket}
     */
    public HyggPacket getPacket() {
        return this.packet;
    }

    /**
     * Set the request's channel
     *
     * @param channel New {@link HyggChannel}
     * @return {@link HyggRequest}
     */
    public HyggRequest withChannel(HyggChannel channel) {
        this.channel = channel;
        return this;
    }

    /**
     * Get request's channel
     *
     * @return {@link HyggChannel}
     */
    public HyggChannel getChannel() {
        return this.channel;
    }

    /**
     * Set the request's response callback
     *
     * @param responseCallback New {@link HyggResponseCallback}
     * @return {@link HyggRequest}
     */
    public HyggRequest withResponseCallback(HyggResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
        return this;
    }

    /**
     * Get request's response callback
     *
     * @return {@link HyggResponseCallback}
     */
    public HyggResponseCallback getResponseCallback() {
        return this.responseCallback;
    }

    /**
     * Set the callback to trigger on timeout.
     *
     * @param timeoutCallback The callback
     * @return {@link HyggRequest}
     */
    public HyggRequest withResponseTimeEndCallback(Runnable timeoutCallback) {
        this.timeoutCallback = timeoutCallback;
        return this;
    }

    /**
     * Get the timeout callback
     *
     * @return A {@link Runnable}
     */
    public Runnable getTimeoutCallback() {
        return this.timeoutCallback;
    }

    /**
     * Set the maximum of responses
     *
     * @param maxResponses New maximum of responses
     * @return {@link HyggRequest}
     */
    public HyggRequest withMaxResponses(int maxResponses) {
        this.maxResponses = maxResponses;
        return this;
    }

    /**
     * Get the maximum of responses
     *
     * @return The maximum of responses
     */
    public int getMaxResponses() {
        return this.maxResponses;
    }

    /**
     * Set the time to wait for all responses
     *
     * @param timeout New timeout
     * @param unit The unit of time to wait
     * @return {@link HyggRequest}
     */
    public HyggRequest withTimeout(long timeout, TimeUnit unit) {
        this.timeout = unit.toMillis(timeout);
        return this;
    }

    /**
     * Get the timeout of all response
     *
     * @return A timeout (in millis)
     */
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * Execute the request. In this case it will send the packet and manage responses
     */
    public void exec() {
        final HyggPacketProcessor packetProcessor = this.hyggdrasilAPI.getPacketProcessor();

        if (this.packet != null) {
            if (this.responseCallback != null) {
                packetProcessor.registerReceiver(this.channel, new ResponseReceiver());
            }

            this.hyggdrasilAPI.getPubSub().send(this.channel, packetProcessor.encode(this.packet));
        } else {
            throw new HyggPacketException("Packet is null!");
        }
    }

    private class ResponseReceiver implements IHyggPacketReceiver {

        public ResponseReceiver() {
            hyggdrasilAPI.getExecutorService().schedule(() -> {
                hyggdrasilAPI.getPacketProcessor().unregisterReceiver(channel, this);
                timeoutCallback.run();
            }, timeout, TimeUnit.MILLISECONDS);
        }

        @Override
        public HyggResponse receive(String channel, HyggPacketHeader packetHeader, HyggPacket packet) {
            if (packet instanceof HyggResponsePacket) {
                final HyggResponsePacket responsePacket = (HyggResponsePacket) packet;

                if (responsePacket.getRespondedPacketUniqueId().equals(packet.getUniqueId())) {
                    if (responseCallback != null) {
                        responseCallback.call(responsePacket.getResponse());
                    }
                }
            }
            return HyggResponse.Type.NONE.toResponse();
        }

    }

}
