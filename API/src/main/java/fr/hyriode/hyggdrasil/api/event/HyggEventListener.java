package fr.hyriode.hyggdrasil.api.event;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 16:13
 */
@FunctionalInterface
public interface HyggEventListener<E extends HyggEvent> {

    /**
     * Fired when the wanted event is received
     *
     * @param event The event received
     */
    void onEvent(E event);

    /**
     * Unsubscribe the listener
     *
     * @param eventBus The {@link HyggEventBus} instance
     */
    default void unsubscribe(HyggEventBus eventBus) {
        eventBus.unsubscribe(this);
    }

}
