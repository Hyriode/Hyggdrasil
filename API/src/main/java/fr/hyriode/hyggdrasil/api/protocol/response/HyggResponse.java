package fr.hyriode.hyggdrasil.api.protocol.response;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 12:48
 */
public class HyggResponse {

    /** Response's type */
    private Type type;
    /** Response's message */
    private String message;

    /**
     * Constructor of {@link HyggResponse}
     *
     * @param type Response's type
     * @param message Response's message
     */
    public HyggResponse(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Constructor of {@link HyggResponse}
     *
     * @param type Response's type
     */
    public HyggResponse(Type type) {
        this(type, null);
    }

    /**
     * Get the response's type
     *
     * @return {@link Type}
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Set the response's type
     *
     * @param type New {@link Type}
     * @return {@link HyggResponse}
     */
    public HyggResponse withType(Type type) {
        this.type = type;
        return this;
    }

    /**
     * Get the response's message
     *
     * @return The message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Set the response's message
     *
     * @param message New response's message
     * @return {@link HyggResponse}
     */
    public HyggResponse withMessage(String message) {
        this.message = message;
        return this;
    }

    public enum Type {

        /** No response to send back */
        NONE,

        /** The request was successfully taken */
        SUCCESS,

        /** An error occurred while taking the request */
        ERROR,

        /** The request need to be canceled */
        ABORT;

        public HyggResponse toResponse() {
            return new HyggResponse(this);
        }

    }

}
