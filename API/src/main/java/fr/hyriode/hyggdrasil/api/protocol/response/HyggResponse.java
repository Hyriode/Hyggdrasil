package fr.hyriode.hyggdrasil.api.protocol.response;

import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import fr.hyriode.hyggdrasil.api.util.serializer.HyggSerializable;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 12:48.<br>
 *
 * The response sent after a packet is received.
 */
public class HyggResponse {

    /** The type of the response */
    private Type type;
    /** The content of the response */
    private HyggResponseContent content;

    /**
     * Constructor of a {@link HyggResponse}
     *
     * @param type Response's type
     * @param content Response's content
     */
    public HyggResponse(Type type, HyggResponseContent content) {
        this.type = type;
        this.content = content;
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
     * @return A {@link Type}
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Set the type of the response
     *
     * @param type New {@link Type}
     * @return This {@link HyggResponse}
     */
    public HyggResponse withType(Type type) {
        this.type = type;
        return this;
    }

    /**
     * Get the content of the response
     *
     * @return The content of the response
     * @param <T> The type of the content
     */
    @SuppressWarnings("unchecked")
    public <T extends HyggResponseContent> T getContent() {
        return (T) this.content;
    }

    /**
     * Set the custom content of the response
     *
     * @param content A {@link HyggSerializable}
     * @return This {@link HyggResponse}
     */
    public HyggResponse withContent(HyggResponseContent content) {
        this.content = content;
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
        ABORT,

        ;

        public HyggResponse toResponse() {
            return new HyggResponse(this);
        }

    }

}
