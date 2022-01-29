package fr.hyriode.hyggdrasil.api.protocol.packet.request;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketException;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseReceiver;

import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 11:05
 */
public class HyggPacketRequest {

    /** Packet to send */
    private HyggPacket packet;
    /** The channel used to send the packet */
    private HyggChannel channel;
    /** The callback to fire after sending the packet */
    private Runnable sendingCallback;
    /** The callback to fire when a response to the packet is received */
    private HyggResponseCallback responseCallback;
    /** The callback to fire when no responses were received */
    private Runnable responseTimeEndCallback;
    /** The number of responses to handle */
    private int maxResponses = 1;
    /** Maximum time to wait for all responses (in millis) */
    private long responseTime = 5000;

    /** {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggPacketRequest}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggPacketRequest(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
    }

    /**
     * Set the request's packet
     *
     * @param packet New {@link HyggPacket}
     * @return {@link HyggPacketRequest}
     */
    public HyggPacketRequest withPacket(HyggPacket packet) {
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
     * @return {@link HyggPacketRequest}
     */
    public HyggPacketRequest withChannel(HyggChannel channel) {
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
     * Set the request's sending callback
     *
     * @param sendingCallback New callback
     * @return {@link HyggPacketRequest}
     */
    public HyggPacketRequest withSendingCallback(Runnable sendingCallback) {
        this.sendingCallback = sendingCallback;
        return this;
    }

    /**
     * Get request's sending callback
     *
     * @return Callback (a {@link Runnable})
     */
    public Runnable getSendingCallback() {
        return this.sendingCallback;
    }

    /**
     * Set the request's response callback
     *
     * @param responseCallback New {@link HyggResponseCallback}
     * @return {@link HyggPacketRequest}
     */
    public HyggPacketRequest withResponseCallback(HyggResponseCallback responseCallback) {
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
     * Set the request's response time end callback.<br>
     * This callback will be fired
     *
     * @param responseTimeEndCallback The callback
     * @return {@link HyggPacketRequest}
     */
    public HyggPacketRequest withResponseTimeEndCallback(Runnable responseTimeEndCallback) {
        this.responseTimeEndCallback = responseTimeEndCallback;
        return this;
    }

    /**
     * Get request's response time end callback
     *
     * @return A {@link Runnable}
     */
    public Runnable getResponseTimeEndCallback() {
        return this.responseTimeEndCallback;
    }

    /**
     * Set the maximum of responses
     *
     * @param maxResponses New maximum of responses
     * @return {@link HyggPacketRequest}
     */
    public HyggPacketRequest withMaxResponses(int maxResponses) {
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
     * @param responseTime New response time
     * @return {@link HyggPacketRequest}
     */
    public HyggPacketRequest withResponseTime(long responseTime, TimeUnit unit) {
        this.responseTime = unit.toMillis(responseTime);
        return this;
    }

    /**
     * Get the maximum of time to wait for all responses
     *
     * @return The response time
     */
    public long getResponseTime() {
        return this.responseTime;
    }

    /**
     * Execute the request. In this case it will send the packet and manage responses
     */
    public void exec() {
        final int initialMaxResponses = this.maxResponses;
        final HyggPacketProcessor packetProcessor = this.hyggdrasilAPI.getPacketProcessor();

        if (this.packet != null) {
            if (this.responseCallback != null) {
                final HyggResponseReceiver responseReceiver = new HyggResponseReceiver(this.hyggdrasilAPI, this);

                packetProcessor.registerReceiver(this.channel, responseReceiver);

                this.hyggdrasilAPI.getScheduler().schedule(() -> {
                    responseReceiver.unregister(this.channel);

                    if (this.maxResponses == initialMaxResponses) {
                        this.responseTimeEndCallback.run();
                    }
                }, this.responseTime, TimeUnit.MILLISECONDS);
            }

            this.hyggdrasilAPI.getPubSub().send(this.channel, packetProcessor.encode(this.packet), this.sendingCallback);
        } else {
            throw new HyggPacketException(null, HyggPacketException.Type.Send.INVALID_PACKET);
        }
    }

}
