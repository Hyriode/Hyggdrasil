package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.HyggProtocol;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponsePacket;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 10:34
 */
public class HyggPacketProcessor {

    /** The separator of all parts in a message */
    private static final char MESSAGE_SEPARATOR = '.';

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
     * @return A {@link HyggRequest} object
     */
    public HyggRequest request(HyggChannel channel, HyggPacket packet) {
        return new HyggRequest(this.hyggdrasilAPI)
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
                final DecodingResult result = this.decode(message);

                if (result != null && result.getPacket() != null) {
                    final HyggPacket packet = result.getPacket();
                    final HyggResponse response = packetReceiver.receive(ch, result.getHeader(), packet);

                    if (response != null) {
                        final HyggResponsePacket responsePacket = new HyggResponsePacket(packet.getUniqueId(), response);

                        this.request(channel, responsePacket).exec();
                    }
                }
            };

            this.packetReceivers.put(packetReceiver, receiver);
            this.hyggdrasilAPI.getPubSub().subscribe(channel, receiver);
        }
    }

    /**
     * Unregister a receiver from a given channel
     *
     * @param channel Concerned channel
     * @param packetReceiver {@link IHyggPacketReceiver} to unregister
     */
    public void unregisterReceiver(HyggChannel channel, IHyggPacketReceiver packetReceiver) {
        final IHyggReceiver receiver = this.packetReceivers.get(packetReceiver);

        if (receiver != null) {
            this.hyggdrasilAPI.getPubSub().unsubscribe(channel, receiver);
        }

        this.packetReceivers.remove(packetReceiver);
    }

    /**
     * Encode a packet to string.<br>
     * The packet will be encoded in the following format: xxxxx.yyyyy<br>
     * All the parts are separated by a dot.<br>
     * First part: the header of the packet, with id, sender, etc.<br>
     * Second part: the content of the packet, so the packet in json.<br>
     * All these parts are encoded in Base64.
     *
     * @param packet {@link HyggPacket} to encode
     * @return The encoded packet
     */
    public String encode(HyggPacket packet) {
        final int id = HyggProtocol.getPacketIdByClass(packet.getClass());

        if (id != -1) {
            final Base64.Encoder encoder = Base64.getEncoder();
            final HyggPacketHeader header = new HyggPacketHeader(this.hyggdrasilAPI.getEnvironment().getApplication(), id, System.currentTimeMillis());
            final String encodedHeader = encoder.encodeToString(header.asJson().getBytes(StandardCharsets.UTF_8));
            final String encodedContent = encoder.encodeToString(packet.asJson().getBytes(StandardCharsets.UTF_8));

            return encodedHeader + MESSAGE_SEPARATOR + encodedContent;
        }
        throw new HyggPacketException("Invalid packet (couldn't find its id)! Packet: " + packet.getClass().getSimpleName());
    }

    /**
     * Decode a given message to a {@link HyggPacket}
     *
     * @param message The message to decode
     * @return The result of the decoding process; so the packet and the header.
     */
    public DecodingResult decode(String message) {
        try {
            final String[] splitRaw  = message.split("\\" + MESSAGE_SEPARATOR);

            if (splitRaw.length < 2) {
                throw new HyggPacketException("Invalid packet format! To decode: " + message);
            }

            final Base64.Decoder decoder = Base64.getDecoder();
            final String encodedHeader = splitRaw[0];
            final HyggPacketHeader header = HyggdrasilAPI.GSON.fromJson(new String(decoder.decode(encodedHeader), StandardCharsets.UTF_8), HyggPacketHeader.class);

            if (header == null) {
                throw new HyggPacketException("Invalid packet header! Header: " + encodedHeader);
            }

            final int packetId = header.getPacketId();
            final Class<? extends HyggPacket> packetClass = HyggProtocol.getPacketClassById(packetId);

            if (packetClass == null) {
                throw new HyggPacketException("Invalid packet id (couldn't find the given id)! Packet id: " + packetId);
            }

            final String encodedContent = splitRaw[1];
            final HyggPacket packet = HyggdrasilAPI.GSON.fromJson(new String(decoder.decode(encodedContent), StandardCharsets.UTF_8), packetClass);

            return new DecodingResult(header, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class DecodingResult {

        private final HyggPacketHeader header;
        private final HyggPacket packet;

        public DecodingResult(HyggPacketHeader header, HyggPacket packet) {
            this.header = header;
            this.packet = packet;
        }

        public HyggPacketHeader getHeader() {
            return this.header;
        }

        public HyggPacket getPacket() {
            return this.packet;
        }

    }

}
