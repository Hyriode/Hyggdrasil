package fr.hyriode.hyggdrasil.api;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 21/01/2022 at 22:12.<br>
 *
 * An exception thrown by {@link HyggdrasilAPI} processes.
 */
public class HyggException extends RuntimeException {

    /**
     * Constructor of {@link HyggException}
     *
     * @param message The message of the exception
     */
    public HyggException(String message) {
        super(message);
    }

    /**
     * Constructor of {@link HyggException}
     *
     * @param message The message of the exception
     * @param cause The cause of the exception
     */
    public HyggException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor of {@link HyggException}
     *
     * @param cause The cause of the exception
     */
    public HyggException(Throwable cause) {
        super(cause);
    }

}
