package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.HyggProtocol;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.HyggResponsePacket;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 10:34
 */
public class HyggPacketProcessor {

    /** {@link IHyggReceiver} of each {@link IHyggPacketReceiver} */
    private final Map<IHyggPacketReceiver, IHyggReceiver> packetReceivers;

    /** {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggPacketProcessor}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggPacketProcessor(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
        this.packetReceivers = new HashMap<>();
    }

    /**
     * Send a pocket request on a channel
     *
     * @param channel Channel that will be used to send the packet
     * @param packet Packet to send
     * @return A {@link HyggPacketRequest} object
     */
    public HyggPacketRequest request(HyggChannel channel, HyggPacket packet) {
        return new HyggPacketRequest(this.hyggdrasilAPI)
                .withPacket(packet)
                .withChannel(channel);
    }

    /**
     * Register a receiver on a given channel
     *
     * @param channel Channel to register the receiver
     * @param packetReceiver {@link IHyggPacketReceiver} to register
     */
    public void registerReceiver(HyggChannel channel, IHyggPacketReceiver packetReceiver) {
        if (!this.packetReceivers.containsKey(packetReceiver)) {
            final IHyggReceiver receiver = (ch, message) -> {
                final HyggPacket packet = this.decode(message);

                if (packet != null) {
                    final HyggResponse response = packetReceiver.receive(ch, packet);

                    if (response != null) {
                        final HyggResponse.Type type = response.getType();

                        if (type != HyggResponse.Type.NONE) {
                            final HyggResponsePacket responsePacket = new HyggResponsePacket(packet.getUniqueId(), response);

                            this.request(channel, responsePacket).exec();
                        }
                    }
                }
            };

            this.packetReceivers.put(packetReceiver, receiver);
            this.hyggdrasilAPI.getPubSub().subscribe(channel.toString(), receiver);
        }
    }

    /**
     * Unregister a receiver from a given channel
     *
     * @param channel Concerned channel
     * @param packetReceiver {@link IHyggPacketReceiver} to unregister
     */
    public void unregisterReceiver(String channel, IHyggPacketReceiver packetReceiver) {
        this.hyggdrasilAPI.getPubSub().unsubscribe(channel, this.packetReceivers.remove(packetReceiver));
    }

    /**
     * Encode a packet to string
     *
     * @param packet {@link HyggPacket} to encode
     * @return The encoded packet
     */
    public String encode(HyggPacket packet) {
        final int id = HyggProtocol.getPacketIdByClass(packet.getClass());

        if (id != -1) {
            return id + HyggProtocol.CONTENT_SPLIT_CHAR + Base64.getEncoder().encodeToString(HyggdrasilAPI.GSON.toJson(packet).getBytes());
        }
        throw new HyggPacketException(packet, HyggPacketException.Type.Send.INVALID_ID);
    }

    /**
     * Decode a message to a packet
     *
     * @param message Message to decode
     * @return Decoded {@link HyggPacket}
     */
    public HyggPacket decode(String message) {
        try {
            final Base64.Decoder decoder = Base64.getDecoder();
            final String[] splitedRaw  = message.split(HyggProtocol.CONTENT_SPLIT_CHAR);
            final int id = Integer.parseInt(splitedRaw[0]);
            final String json = new String(decoder.decode(splitedRaw[1]));
            final Class<? extends HyggPacket> packetClass = HyggProtocol.getPacketClassById(id);

            if (packetClass != null) {
                return HyggdrasilAPI.GSON.fromJson(json, packetClass);
            } else {
                throw new HyggPacketException(id, HyggPacketException.Type.Received.INVALID_CLASS);
            }
        } catch (Exception e) {
            return null;
        }
    }

}
