package fr.hyriode.hyggdrasil.api.pubsub;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.receiver.IHyggReceiver;
import fr.hyriode.hyreos.api.HyreosRedisKey;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 24/12/2021 at 10:03
 */
public class HyggPubSub extends JedisPubSub {

    /** PubSub state */
    private boolean running;

    /** Subscriber thread */
    private Thread subscriberThread;

    /** Map of all {@link IHyggReceiver} */
    private final Map<String, Set<IHyggReceiver>> receivers;

    /** {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    private long sent;

    /**
     * Constructor of {@link HyggPubSub}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggPubSub(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
        this.receivers = new HashMap<>();
    }

    /**
     * Start PubSub
     */
    public void start() {
        HyggdrasilAPI.log("Starting PubSub...");

        this.running = true;
        this.subscriberThread = new Thread(() -> {
            while (this.running) {
                this.hyggdrasilAPI.redisProcess(jedis -> {
                    jedis.psubscribe(this, HyggdrasilAPI.PREFIX + "*");

                    HyggdrasilAPI.log(Level.SEVERE, "Redis is no longer responding to the PubSub subscriber!");

                    this.stop();
                });
            }
        }, "PubSub Subscriber");
        this.subscriberThread.start();

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            final String key = HyreosRedisKey.HYGGDRASIL_PACKETS.getKey();

            this.hyggdrasilAPI.redisProcess(jedis -> jedis.incrBy(key, this.sent));
            this.sent = 0;
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * Stop PubSub
     */
    public void stop() {
        HyggdrasilAPI.log("Stopping PubSub...");

        this.running = false;

        if (this.isSubscribed()) {
            this.unsubscribe();
        }

        this.subscriberThread.interrupt();
    }

    /**
     * Send a message on a given channel.
     *
     * @param channel The channel that will be used to send message
     * @param message The message to send
     */
    public void send(HyggChannel channel, String message) {
        this.sent++;
        this.hyggdrasilAPI.redisProcess(jedis -> jedis.publish(channel.getName(), message));
    }

    /**
     * Subscribe a receiver to a channel
     *
     * @param channel The given channel
     * @param receiver The receiver to subscribe
     */
    public void subscribe(HyggChannel channel, IHyggReceiver receiver) {
        final String channelStr = channel.toString();
        final Set<IHyggReceiver> receivers = this.receivers.getOrDefault(channelStr, ConcurrentHashMap.newKeySet());

        receivers.add(receiver);

        this.receivers.put(channelStr, receivers);
    }

    /**
     * Unsubscribe a receiver from a channel
     *
     * @param channel The given channel
     * @param receiver the receiver to unsubscribe
     */
    public void unsubscribe(HyggChannel channel, IHyggReceiver receiver) {
        final String channelStr = channel.toString();
        final Set<IHyggReceiver> receivers = this.receivers.get(channelStr);

        if (receivers != null) {
            receivers.remove(receiver);

            this.receivers.put(channelStr, receivers);
        }
    }

    /**
     * Called when a message is received on PubSub
     *
     * @param pattern Pattern where the message is received
     * @param channel Channel where the message is received
     * @param message The received message
     */
    @Override
    public void onPMessage(String pattern, String channel, String message) {
        final Set<IHyggReceiver> receivers = this.receivers.get(channel);

        if (receivers != null) {
            this.hyggdrasilAPI.getExecutorService().execute(() -> receivers.forEach(receiver -> receiver.receive(channel, message)));
        }
    }

}
