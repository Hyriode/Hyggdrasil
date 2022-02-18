package fr.hyriode.hyggdrasil.api.event;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 16:02
 */
public class HyggEventBus {

    private final Queue<HyggEventContext<?>> contexts;

    /** The {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggEventBus}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggEventBus(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
        this.contexts = new ConcurrentLinkedQueue<>();
    }

    /**
     * Start the event bus by subscribing on event channel
     */
    public void start() {
        HyggdrasilAPI.log("Starting event bus...");

        this.hyggdrasilAPI.getPubSub().subscribe(HyggChannel.EVENTS, (channel, message) -> {
            final HyggEvent event = this.decode(message);

            if (event != null) {
                for (HyggEventContext<?> context : this.contexts) {
                    if (context.getEventClass().isAssignableFrom(event.getClass())) {
                        try {
                            final Method method = context.getEventListener().getClass().getMethod("onEvent", HyggEvent.class);

                            method.setAccessible(true);
                            method.invoke(context.getEventListener(), event);
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * Subscribe a listener to listen for a given event
     *
     * @param eventClass The {@link Class} of the event of listen
     * @param eventListener The {@link HyggEventListener} to subscribe
     * @param <E> The type of the class and the listener
     */
    public <E extends HyggEvent> void subscribe(Class<E> eventClass, HyggEventListener<E> eventListener) {
        this.contexts.add(new HyggEventContext<>(eventClass, eventListener));
    }

    /**
     * Unsubscribe a listener
     *
     * @param eventListener The listener to unregister
     */
    public void unsubscribe(HyggEventListener<?> eventListener) {
        this.contexts.removeIf(context -> context.getEventListener() == eventListener);
    }

    /**
     * Publish a given event to events channel.<br>
     * If listeners exist for this event, they will be triggered
     *
     * @param event The event to publish
     */
    public void publish(HyggEvent event) {
        this.hyggdrasilAPI.getPubSub().send(HyggChannel.EVENTS, HyggdrasilAPI.GSON.toJson(event));
    }

    private HyggEvent decode(String message) {
        try {
            return HyggdrasilAPI.GSON.fromJson(message, HyggEvent.class);
        } catch (Exception e) {
            return null;
        }
    }

}
