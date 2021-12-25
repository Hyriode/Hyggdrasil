package fr.hyriode.hyggdrasil.api.util.builder;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 12/12/2021 at 12:04
 */
public class BuildException extends RuntimeException {

    /**
     * Constructor of {@link BuildException}
     *
     * @param message Message to send when the exception is thrown
     */
    public BuildException(String message) {
        super(message);
    }

}
