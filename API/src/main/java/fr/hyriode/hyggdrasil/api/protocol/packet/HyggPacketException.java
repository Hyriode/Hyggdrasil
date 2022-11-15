package fr.hyriode.hyggdrasil.api.protocol.packet;

import fr.hyriode.hyggdrasil.api.HyggException;

import java.util.function.Function;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 10:48.<br>
 *
 * An exception thrown by {@link fr.hyriode.hyggdrasil.api.HyggdrasilAPI} packet processes.
 */
public class HyggPacketException extends HyggException {

    /**
     * Constructor of {@link HyggException}
     *
     * @param message The message of the exception
     */
    public HyggPacketException(String message) {
        super(message);
    }

    /**
     * Constructor of {@link HyggException}
     *
     * @param message The message of the exception
     * @param cause The cause of the exception
     */
    public HyggPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor of {@link HyggException}
     *
     * @param cause The cause of the exception
     */
    public HyggPacketException(Throwable cause) {
        super(cause);
    }

}
