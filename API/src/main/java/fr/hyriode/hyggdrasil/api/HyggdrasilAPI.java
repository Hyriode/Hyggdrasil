package fr.hyriode.hyggdrasil.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.hyriode.hyggdrasil.api.event.HyggEventBus;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbosRequester;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggEnv;
import fr.hyriode.hyggdrasil.api.protocol.heartbeat.HyggHeartbeatTask;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxiesRequester;
import fr.hyriode.hyggdrasil.api.pubsub.HyggPubSub;
import fr.hyriode.hyggdrasil.api.server.HyggServersRequester;
import fr.hyriode.hyggdrasil.api.util.builder.BuildException;
import fr.hyriode.hyggdrasil.api.util.builder.BuilderEntry;
import fr.hyriode.hyggdrasil.api.util.builder.IBuilder;
import fr.hyriode.hyggdrasil.api.util.serializer.HyggSerializable;
import fr.hyriode.hyggdrasil.api.util.serializer.HyggSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 12/12/2021 at 11:56
 */
public class HyggdrasilAPI {

    /** Application's name constant */
    public static final String NAME = "Hyggdrasil";
    /** APIs name constant */
    public static final String API_NAME = NAME + "API";
    /** APIs prefix constant. This is used for channels or to print information */
    public static final String PREFIX = "hygg";
    /** The Redis key prefix used by the API */
    public static final String REDIS_KEY = "hyggdrasil:";
    /** The maximum of time to wait before timing out an application */
    public static final int TIMED_OUT_TIME = 30 * 1000;
    /** The time before sending a heartbeat */
    public static final int HEARTBEAT_TIME = 10 * 1000;
    /** {@link Gson} instance */
    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(HyggSerializable.class, new HyggSerializer<>())
            .registerTypeHierarchyAdapter(HyggData.class, new HyggData.Serializer())
            .create();
    /** Normal {@link Gson} instance */
    public static final Gson NORMAL_GSON = new Gson();

    /** Static instance of the logger */
    private static Logger logger;
    /** The  {@link JedisPool} instance */
    private final JedisPool jedisPool;
    /** The supplier of the running application environment */
    private final Supplier<HyggEnv> environmentSupplier;
    /** The {@link ScheduledExecutorService} instance */
    private ScheduledExecutorService executorService;
    /** The running application environment */
    private HyggEnv environment;
    /** The task that handles heartbeats with Hyggdrasil */
    private HyggHeartbeatTask heartbeatTask;
    /** The Hyggdrasil PubSub instance */
    private final HyggPubSub pubSub;
    /** The packet processor used to send/receive packets */
    private final HyggPacketProcessor packetProcessor;
    /** The event bus used to receive/call events */
    private final HyggEventBus eventBus;
    /** The server requester used to query or do action on servers */
    private final HyggServersRequester serversRequester;
    /** The proxy requester used to query or do action on proxies */
    private final HyggProxiesRequester proxiesRequester;
    /** The limbo requester used to query or do action on limbos */
    private final HyggLimbosRequester limbosRequester;

    /**
     * Constructor of {@link HyggdrasilAPI}
     * @param logger APIs logger
     * @param jedisPool {@link JedisPool} used for all Redis actions
     * @param environmentSupplier The supplier of the running application environment
     */
    public HyggdrasilAPI(Logger logger, JedisPool jedisPool, Supplier<HyggEnv> environmentSupplier) {
        HyggdrasilAPI.logger = logger;
        this.jedisPool = jedisPool;
        this.environmentSupplier = environmentSupplier;
        this.pubSub = new HyggPubSub(this);
        this.packetProcessor = new HyggPacketProcessor(this);
        this.eventBus = new HyggEventBus(this);
        this.serversRequester = new HyggServersRequester(this);
        this.proxiesRequester = new HyggProxiesRequester(this);
        this.limbosRequester = new HyggLimbosRequester(this);
    }

    /**
     * Start {@link HyggdrasilAPI}
     */
    public void start() {
        log("Starting " + API_NAME + "...");

        this.executorService = Executors.newScheduledThreadPool(6);
        this.pubSub.start();
        this.eventBus.start();
        this.environment = this.environmentSupplier.get();

        if (this.environment.getApplication().getType().isUsingHeartbeats()) {
            this.heartbeatTask = new HyggHeartbeatTask(this);
        }
    }

    /**
     * Stop {@link HyggdrasilAPI}
     *
     * @param reason The reason of the stop
     */
    public void stop(String reason) {
        log("Stopping " + API_NAME + (reason != null ? " (reason: " + reason + ")" : "") + "...");

        this.pubSub.stop();
    }

    /**
     * Stop {@link HyggdrasilAPI}
     */
    public void stop() {
        this.stop(null);
    }

    /**
     * Process an action in Redis cache
     *
     * @param jedisConsumer The action to process
     */
    public void redisProcess(Consumer<Jedis> jedisConsumer) {
        try (final Jedis jedis = this.getJedis()) {
            if (jedis != null) {
                jedisConsumer.accept(jedis);
            }
        }
    }

    /**
     * Process an action in Redis cache
     *
     * @param jedisFunction The action to process
     * @return The result of the process
     * @param <R> The type of the result
     */
    public <R> R redisGet(Function<Jedis, R> jedisFunction) {
        try (final Jedis jedis = this.getJedis()) {
            if (jedis != null) {
                return jedisFunction.apply(jedis);
            }
        }
        return null;
    }

    /**
     * Print a message in the terminal
     *
     * @param level Message's level
     * @param message Message to print
     */
    public static void log(Level level, String message) {
        if (logger != null) {
            logger.log(level, message);
        } else {
            if (level == Level.SEVERE) {
                System.err.println(message);
            } else {
                System.out.println(message);
            }
        }
    }

    /**
     * Print a message in the terminal
     *
     * @param message Message to print
     */
    public static void log(String message) {
        log(Level.INFO, message);
    }

    /**
     * Get {@link JedisPool} instance
     *
     * @return {@link JedisPool}
     */
    public JedisPool getJedisPool() {
        return this.jedisPool;
    }

    /**
     * Get a Redis resource from the pool
     *
     * @return {@link Jedis} instance
     */
    public Jedis getJedis() {
        return this.jedisPool.getResource();
    }

    /**
     * Get the executor service instance
     *
     * @return The {@link ScheduledExecutorService} instance
     */
    public ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }

    /**
     * Get the running application environment
     *
     * @return {@link HyggEnv} object
     */
    public HyggEnv getEnvironment() {
        return this.environment;
    }

    /**
     * Get the heartbeat task instance
     *
     * @return The {@link HyggHeartbeatTask} instance
     */
    public HyggHeartbeatTask getHeartbeatTask() {
        if (this.heartbeatTask != null) {
            return this.heartbeatTask;
        }
        throw new HyggException("Cannot get the heartbeat task instance if you are not a client that can handle the heartbeat protocol!");
    }

    /**
     * Get Hyggdrasil PubSub instance
     *
     * @return {@link HyggPubSub} instance
     */
    public HyggPubSub getPubSub() {
        return this.pubSub;
    }

    /**
     * Get Hyggdrasil packet processor<br>
     * This class is used to send/receive packets
     *
     * @return {@link HyggPacketProcessor} instance
     */
    public HyggPacketProcessor getPacketProcessor() {
        return this.packetProcessor;
    }

    /**
     * Get Hyggdrasil event bus.<br>
     * This class is used to listen for events or call ones
     *
     * @return {@link HyggEventBus} instance
     */
    public HyggEventBus getEventBus() {
        return this.eventBus;
    }

    /**
     * Get the server requester.<br>
     * This class is used to do actions on servers. Like create or remove one, wait for a state, etc.
     *
     * @return {@link HyggServersRequester} instance
     */
    public HyggServersRequester getServersRequester() {
        return this.serversRequester;
    }

    /**
     * Get the proxy requester.<br>
     * This class is used to do actions on proxies. Like create or remove one, wait for a state, etc.
     *
     * @return {@link HyggProxiesRequester} instance
     */
    public HyggProxiesRequester getProxiesRequester() {
        return this.proxiesRequester;
    }

    /**
     * Get the limbo requester.<br>
     * This class is used to do actions on limbos. Like create or remove one.
     *
     * @return {@link HyggLimbosRequester} instance
     */
    public HyggLimbosRequester getLimbosRequester() {
        return this.limbosRequester;
    }


    /**
     * {@link HyggdrasilAPI} builder class
     */
    public static class Builder implements IBuilder<HyggdrasilAPI> {

        /** Logger builder option */
        private final BuilderEntry<Logger> loggerEntry = new BuilderEntry<>("Logger", () -> Logger.getLogger(HyggdrasilAPI.class.getName())).optional();
        /** Jedis pool builder option */
        private final BuilderEntry<JedisPool> jedisPoolEntry = new BuilderEntry<JedisPool>("Jedis Pool").required();
        /** {@link HyggEnv} builder option */
        private final BuilderEntry<HyggEnv> environmentEntry = new BuilderEntry<>("Application environment", HyggEnv::loadFromEnvironmentVariables).required();

        /**
         * Set logger to provide to {@link HyggdrasilAPI}
         *
         * @param logger {@link Logger} object
         * @return {@link Builder}
         */
        public Builder withLogger(Logger logger) {
            this.loggerEntry.set(() -> logger);
            return this;
        }

        /**
         * Set Jedis pool to provide to {@link HyggdrasilAPI}
         *
         * @param jedisPool {@link JedisPool} object
         * @return {@link Builder}
         */
        public Builder withJedisPool(JedisPool jedisPool) {
            this.jedisPoolEntry.set(() -> jedisPool);
            return this;
        }

        /**
         * Set the application environment to provide to {@link HyggdrasilAPI}.<br>
         * If this method is not called, the builder with automatically load them from environment variables.
         *
         * @param environment {@link HyggEnv} object
         * @return {@link Builder}
         */
        public Builder withEnvironment(HyggEnv environment) {
            this.environmentEntry.set(() -> environment);
            return this;
        }

        /**
         * Build all the option to get {@link HyggdrasilAPI} instance
         *
         * @return {@link HyggdrasilAPI} instance
         * @throws BuildException if an error occurred during building
         */
        @Override
        public HyggdrasilAPI build() throws BuildException {
            return new HyggdrasilAPI(this.loggerEntry.get(), this.jedisPoolEntry.get(), this.environmentEntry.asSupplier());
        }

    }

}
