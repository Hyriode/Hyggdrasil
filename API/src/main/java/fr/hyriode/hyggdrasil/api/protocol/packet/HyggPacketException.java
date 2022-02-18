package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.HyggException;

import java.util.function.Function;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 10:48
 */
public class HyggPacketException extends HyggException {

    /**
     * Constructor of {@link }
     *
     * @param object The object needed
     * @param type The type of the exception
     * @param <T> A type
     */
    public <T> HyggPacketException(T object, IType<T> type) {
        super(type.getErrorMessage(object));
    }

    /**
     * A simple interface used to create exception types
     */
    private interface IType<T> {

        /**
         * Get the error message
         *
         * @param object The object needed to get information
         * @return An error message
         */
        String getErrorMessage(T object);

    }

    /**
     * The class with all the types
     */
    public static class Type {

        /**
         * The class with all the types that can be used when a packet is received
         */
        public static class Received {

            /** The class of the packet cannot be found from its id */
            public static final IType<Integer> INVALID_CLASS = id -> "Cannot find the class of the received packet from its id. ID: " + id + ".";
            /** The header of the received packet is invalid */
            public static final IType<String> INVALID_HEADER = header -> "Received an invalid header for a message! Header received: " + header + ".";
            /** The received message is not in the good format: xxxx.yyyy.zzzz or xxxx.yyyy.zzzz */
            public static final IType<String> INVALID_FORMAT = message -> "Received an invalid message! Message: " + message + ".";
            /** The received message has an invalid signature */
            public static final IType<String> INVALID_SIGNATURE = signature -> "Received a message with an invalid signature! Signature: " + signature + ".";

        }

        /**
         * The class with all the types that can be used when a packet will be sent
         */
        public enum Send implements IType<HyggPacket> {

            /** The packet is null */
            INVALID_PACKET(packet -> "Packet cannot be null!"),
            /** The id of the packet is invalid */
            INVALID_ID(packet -> "Cannot find the packet id of " + packet.getClass().getName() + " in the protocol!");

            /** A function that return a message from a {@link HyggPacket} */
            private final Function<HyggPacket, String> errorMessage;

            /**
             * Constructor of {@link Send} type
             *
             * @param errorMessage The function
             */
            Send(Function<HyggPacket, String> errorMessage) {
                this.errorMessage = errorMessage;
            }

            @Override
            public String getErrorMessage(HyggPacket packet) {
                return this.errorMessage.apply(packet);
            }

        }

    }

}
