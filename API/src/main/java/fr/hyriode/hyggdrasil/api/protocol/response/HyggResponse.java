package fr.hyriode.hyggdrasil.api.protocol.response;

import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 12:48
 */
public class HyggResponse implements IHyggResponse {

    /** Response's type */
    private Type type;
    /** Response's custom content */
    private HyggResponseContent content;

    /**
     * Constructor of {@link HyggResponse}
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
     * Get the response's custom content
     *
     * @return A {@link HyggResponseContent}
     */
    public HyggResponseContent getContent() {
        return this.content;
    }

    /**
     * Set the custom response's content
     *
     * @param content A {@link HyggResponseContent}
     * @return {@link HyggResponse}
     */
    public HyggResponse withContent(HyggResponseContent content) {
        this.content = content;
        return this;
    }

    public enum Type implements IHyggResponse {

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
