package fr.hyriode.hyggdrasil.api.protocol.pubsub;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggReceiver;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 24/12/2021 at 10:03
 */
public class HyggPubSub extends JedisPubSub {

    /** PubSub state */
    private boolean running;

    /** The sender that send messages */
    private final Sender sender;
    /** Sender thread */
    private Thread senderThread;
    /** Subscriber thread */
    private Thread subscriberThread;

    /** Map of all {@link IHyggReceiver} */
    private final Map<String, Set<IHyggReceiver>> receivers;

    /** {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggPubSub}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggPubSub(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
        this.receivers = new ConcurrentHashMap<>();
        this.sender = new Sender();
    }

    /**
     * Start PubSub
     */
    public void start() {
        HyggdrasilAPI.log("Starting PubSub...");

        this.running = true;

        this.senderThread = new Thread(this.sender, "PubSub Sender");
        this.senderThread.start();

        this.subscriberThread = new Thread(() -> {
            while (this.running) {
                try (final Jedis jedis = this.hyggdrasilAPI.getJedis()) {
                    final String[] channels = this.receivers.keySet().toArray(new String[0]);

                    if (channels.length > 0) {
                        jedis.subscribe(this, channels);
                    }
                }
            }
        }, "PubSub Subscriber");
        this.subscriberThread.start();
    }

    /**
     * Stop PubSub
     */
    public void stop() {
        HyggdrasilAPI.log("Stopping PubSub...");

        this.running = false;
        this.sender.running = false;

        if (this.isSubscribed()) {
            this.unsubscribe();
        }

        this.senderThread.interrupt();
        this.subscriberThread.interrupt();
    }

    /**
     * Send a message on a channel
     *
     * @param channel Channel that will be used to send message
     * @param message Message to send
     * @param callback Callback to fire after sending message
     */
    public void send(String channel, String message, Runnable callback) {
        this.sender.messages.add(new HyggPubSubMessage(channel, message, callback));
    }

    /**
     * Send a message on a channel
     *
     * @param channel Channel that will be used to send message
     * @param message Message to send
     */
    public void send(String channel, String message) {
        this.send(channel, message, null);
    }

    /**
     * Subscribe a receiver to a channel
     *
     * @param channel The given channel
     * @param receiver The receiver to subscribe
     */
    public void subscribe(String channel, IHyggReceiver receiver) {
        final Set<IHyggReceiver> receivers = this.receivers.get(channel) != null ? this.receivers.get(channel) : ConcurrentHashMap.newKeySet();

        receivers.add(receiver);

        this.receivers.put(channel, receivers);

        if (this.isSubscribed()) {
            this.unsubscribe();
        }
    }

    /**
     * Unsubscribe a receiver from a channel
     *
     * @param channel The given channel
     * @param receiver the receiver to unsubscribe
     */
    public void unsubscribe(String channel, IHyggReceiver receiver) {
        final Set<IHyggReceiver> receivers = this.receivers.get(channel) != null ? this.receivers.get(channel) : ConcurrentHashMap.newKeySet();

        receivers.remove(receiver);

        this.receivers.put(channel, receivers);

        if (this.isSubscribed()) {
            this.unsubscribe();
        }
    }

    /**
     * Called when a message is received on PubSub
     *
     * @param channel Channel where the message is received
     * @param message The received message
     */
    @Override
    public void onMessage(String channel, String message) {
        final Set<IHyggReceiver> receivers = this.receivers.get(channel);

        if (receivers != null) {
            receivers.forEach(receiver -> receiver.receive(channel, message));
        }
    }

    /**
     * PubSub sender class
     */
    private class Sender implements Runnable {

        /** Queue of all messages to send */
        private final LinkedBlockingQueue<HyggPubSubMessage> messages = new LinkedBlockingQueue<>();

        /** {@link Jedis} instance */
        private Jedis jedis;
        /** Sender state */
        private boolean running = true;

        @Override
        public void run() {
            this.checkRedis();

            while (this.running) {
                try {
                    final HyggPubSubMessage message = this.messages.take();
                    final Runnable callback = message.getCallback();

                    boolean published = false;

                    while (!published) {
                        try {
                            this.jedis.publish(message.getChannel(), message.getContent());

                            published = true;

                            if (callback != null) {
                                callback.run();
                            }
                        } catch (Exception e) {
                            this.checkRedis();
                        }
                    }
                } catch (InterruptedException e) {
                    this.jedis.close();
                    e.printStackTrace();
                    return;
                }
            }
        }

        /**
         * Check if Redis connection is fine
         */
        private void checkRedis() {
            try {
                this.jedis = hyggdrasilAPI.getJedis();
            } catch (Exception e) {
                HyggdrasilAPI.log(Level.SEVERE, "An error occurred in Redis connection! The PubSub can no longer send messages!");
            }
        }

    }

}
