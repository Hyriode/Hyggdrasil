package fr.hyriode.hyggdrasil.api.protocol.receiver;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 11:41
 */
@FunctionalInterface
public interface IHyggReceiver {

    /**
     * This method is fired when a message is received on the wanted channel
     *
     * @param channel The wanted channel
     * @param message The message received
     */
    void receive(String channel, String message);

}
