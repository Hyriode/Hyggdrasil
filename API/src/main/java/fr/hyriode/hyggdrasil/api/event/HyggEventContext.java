package fr.hyriode.hyggdrasil.api.event;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 16:12
 */
public class HyggEventContext<E extends HyggEvent> {

    /** Event's type */
    private final Class<E> eventClass;

    /** Event's listener */
    private final HyggEventListener<E> eventListener;

    /**
     * Constructor of {@link HyggEventContext}
     *
     * @param eventClass The class of the desired event
     * @param eventListener The listener of the event
     */
    public HyggEventContext(Class<E> eventClass, HyggEventListener<E> eventListener) {
        this.eventClass = eventClass;
        this.eventListener = eventListener;
    }

    /**
     * Get event's class
     *
     * @return A {@link Class}
     */
    public Class<E> getEventClass() {
        return this.eventClass;
    }

    /**
     * Get event's listener
     *
     * @return {@link HyggEventListener} object
     */
    public HyggEventListener<E> getEventListener() {
        return this.eventListener;
    }


}
