package fr.hyriode.hyggdrasil.api.protocol.pubsub;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 11:18
 */
public class HyggPubSubMessage {

    /** Channel that will be used to send the message */
    private final String channel;
    /** Message's content */
    private final String content;
    /** The callback to call after sending */
    private final Runnable callback;

    /**
     * Constructor of {@link HyggPubSubMessage}
     *
     * @param channel Message's channel
     * @param content Message's content
     * @param callback Message's callback
     */
    public HyggPubSubMessage(String channel, String content, Runnable callback) {
        this.channel = channel;
        this.content = content;
        this.callback = callback;
    }

    /**
     * Get message's channel
     *
     * @return The channel
     */
    public String getChannel() {
        return this.channel;
    }

    /**
     * Get message's content
     *
     * @return The content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Get message's callback
     *
     * @return The callback (a {@link Runnable})
     */
    public Runnable getCallback() {
        return this.callback;
    }

}
