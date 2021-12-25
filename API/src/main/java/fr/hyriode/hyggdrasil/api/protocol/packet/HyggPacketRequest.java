package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseReceiver;

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
     * Execute the request. In our case it will send the packet and manage responses
     */
    public void exec() {
        final HyggPacketProcessor packetProcessor = this.hyggdrasilAPI.getPacketProcessor();

        if (this.packet != null) {
            if (this.responseCallback != null) {
                packetProcessor.registerReceiver(this.channel, new HyggResponseReceiver(this.hyggdrasilAPI, this));
            }

            this.hyggdrasilAPI.getPubSub().send(this.channel.toString(), packetProcessor.encode(this.packet), this.sendingCallback);
        } else {
            throw new HyggPacketException(null, HyggPacketException.Type.Send.INVALID_PACKET);
        }
    }

}
