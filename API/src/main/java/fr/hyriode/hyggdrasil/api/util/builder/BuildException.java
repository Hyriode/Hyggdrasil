package fr.hyriode.hyggdrasil.api.util.builder;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 12/12/2021 at 12:04
 */
public class BuildException extends RuntimeException {

    public BuildException(String message) {
        super(message);
    }

    public BuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuildException(Throwable cause) {
        super(cause);
    }

}
