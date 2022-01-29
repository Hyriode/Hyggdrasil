package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.HyggProtocol;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.HyggResponsePacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.request.HyggPacketHeader;
import fr.hyriode.hyggdrasil.api.protocol.packet.request.HyggPacketRequest;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggPacketReceiver;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggReceiver;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.logging.Level;

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
                final HyggPacketDecodingResult result = this.decode(message);

                if (result != null && result.getPacket() != null) {
                    final HyggPacket packet = result.getPacket();
                    final HyggResponse response = packetReceiver.receive(ch, packet, result.getPacketHeader());

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
     * The packet will be encoded in the following format: xxxxx.yyyyy.zzzzz<br>
     * All the parts are separated by a dot.<br>
     * First part: the header of the packet, with id, sender etc<br>
     * Second part: the content of the packet, so the packet in json<br>
     * Third part: the signature of the packet. This part will only be added if a {@link java.security.PrivateKey} has been provided in {@link fr.hyriode.hyggdrasil.api.protocol.environment.HyggEnvironment}<br>
     * All of these parts (except the signature) are encoded in Base64
     *
     * @param packet {@link HyggPacket} to encode
     * @return The encoded packet
     */
    public String encode(HyggPacket packet) {
        final int id = HyggProtocol.getPacketIdByClass(packet.getClass());

        if (id != -1) {
            final Base64.Encoder encoder = Base64.getEncoder();
            final Charset charset = StandardCharsets.UTF_8;
            final HyggPacketHeader header = new HyggPacketHeader(this.hyggdrasilAPI.getEnvironment().getApplication(), id);
            final String encodedHeader = encoder.encodeToString(header.asJson().getBytes(charset));
            final String encodedContent = encoder.encodeToString(packet.asJson().getBytes(charset));
            final String encodedHeaderAndContent = encodedHeader + MESSAGE_SEPARATOR + encodedContent;
            final String signature = this.sign(encodedHeaderAndContent);

            if (!signature.isEmpty()) {
                return encodedHeaderAndContent + MESSAGE_SEPARATOR + signature;
            }
            return encodedHeaderAndContent;
        }
        throw new HyggPacketException(packet, HyggPacketException.Type.Send.INVALID_ID);
    }

    /**
     * Create a signature for a given content
     *
     * @param content The content to sign
     * @return A signature
     */
    private String sign(String content) {
        final PrivateKey privateKey = this.hyggdrasilAPI.getEnvironment().getKeys().getPrivate();

        if (privateKey != null) {
            try {
                final Signature signature = Signature.getInstance(HyggdrasilAPI.ALGORITHM.getJcaName());

                signature.initSign(privateKey);
                signature.update(content.getBytes(StandardCharsets.UTF_8));

                return Base64.getEncoder().encodeToString(signature.sign());
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                HyggdrasilAPI.log(Level.SEVERE, "An error occurred while signing a message with the key and the algorithm!");
                e.printStackTrace();
            } catch (SignatureException e) {
                HyggdrasilAPI.log(Level.SEVERE, "An error occurred while signing a message! Message: " + content);
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Decode a given message to a packet
     *
     * @param message Message to decode
     * @return The result of the decoding. So the packet, the header, etc
     */
    public HyggPacketDecodingResult decode(String message) {
        try {
            final Base64.Decoder decoder = Base64.getDecoder();
            final String[] splitedRaw  = message.split("\\" + MESSAGE_SEPARATOR);

            if (splitedRaw.length >= 2) {
                final String encodedHeader = splitedRaw[0];
                final String encodedContent = splitedRaw[1];
                final String headerJson = new String(decoder.decode(encodedHeader));
                final HyggPacketHeader header = HyggdrasilAPI.GSON.fromJson(headerJson, HyggPacketHeader.class);

                if (header != null) {
                    final int packetId = header.getPacketId();
                    final String content = new String(decoder.decode(encodedContent));
                    final Class<? extends HyggPacket> packetClass = HyggProtocol.getPacketClassById(packetId);

                    if (packetClass != null) {
                        final HyggPacket packet = HyggdrasilAPI.GSON.fromJson(content, packetClass);

                        if (this.hyggdrasilAPI.onlyAcceptingHyggdrasilPackets()) {
                            if (splitedRaw.length == 3) {
                                final String signature = splitedRaw[2];

                                if (this.isValidSignature(encodedHeader + MESSAGE_SEPARATOR + encodedContent, signature)) {
                                    return new HyggPacketDecodingResult(header, packet, true);
                                } else {
                                    throw new HyggPacketException(signature, HyggPacketException.Type.Received.INVALID_SIGNATURE);
                                }
                            } else {
                                return null;
                            }
                        } else {
                            return new HyggPacketDecodingResult(header, packet, false);
                        }
                    } else {
                        throw new HyggPacketException(packetId, HyggPacketException.Type.Received.INVALID_CLASS);
                    }
                } else {
                    throw new HyggPacketException(headerJson, HyggPacketException.Type.Received.INVALID_HEADER);
                }
            } else {
                throw new HyggPacketException(message, HyggPacketException.Type.Received.INVALID_FORMAT);
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verify if a message has a good signature
     *
     * @param content The content of the message
     * @param signature The signature of the message
     * @return <code>true</code> if the message is valid
     */
    private boolean isValidSignature(String content, String signature) {
        final Signature publicSignature;
        try {
            publicSignature = Signature.getInstance(HyggdrasilAPI.ALGORITHM.getJcaName());

            publicSignature.initVerify(this.hyggdrasilAPI.getEnvironment().getKeys().getPublic());
            publicSignature.update(content.getBytes(StandardCharsets.UTF_8));

            return publicSignature.verify(Base64.getDecoder().decode(signature));
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            HyggdrasilAPI.log(Level.SEVERE, "An error occurred while verifying a received message!");
            e.printStackTrace();
        }
        return false;
    }

}
