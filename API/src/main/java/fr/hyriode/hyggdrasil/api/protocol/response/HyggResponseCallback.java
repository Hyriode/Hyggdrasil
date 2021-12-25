package fr.hyriode.hyggdrasil.api.protocol.response;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 15:25
 */
@FunctionalInterface
public interface HyggResponseCallback {

    /**
     * Fired when a response is received
     *
     * @param response Received response
     */
    void call(HyggResponse response);

}
