package fr.hyriode.hyggdrasil.api;

import com.google.gson.Gson;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggEnvironment;
import fr.hyriode.hyggdrasil.api.protocol.heartbeat.HyggHeartbeatTask;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.signature.HyggSignatureAlgorithm;
import fr.hyriode.hyggdrasil.api.pubsub.HyggPubSub;
import fr.hyriode.hyggdrasil.api.scheduler.HyggScheduler;
import fr.hyriode.hyggdrasil.api.util.builder.BuildException;
import fr.hyriode.hyggdrasil.api.util.builder.BuilderEntry;
import fr.hyriode.hyggdrasil.api.util.builder.IBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
    public static final String PREFIX = "Hygg";
    /** The algorithm used to sign/verify messages or create keys */
    public static final HyggSignatureAlgorithm ALGORITHM = HyggSignatureAlgorithm.RS256;
    /** The maximum of time to wait before timing out an application */
    public static final int TIMED_OUT_TIME = 3 * 10000;
    /** The time before sending a heartbeat */
    public static final int HEARTBEAT_TIME = 10000;
    /** {@link Gson} instance */
    public static final Gson GSON = new Gson();

    /** Static instance of the logger */
    private static Logger logger;
    /** {@link JedisPool} object */
    private final JedisPool jedisPool;
    /** The supplier of the running application environment */
    private final Supplier<HyggEnvironment> environmentSupplier;
    /** The running application environment */
    private HyggEnvironment environment;
    /** The task that handles heartbeats with Hyggdrasil */
    private HyggHeartbeatTask heartbeatTask;
    /** The Hyggdrasil scheduler instance */
    private final HyggScheduler scheduler;
    /** The Hyggdrasil PubSub instance */
    private final HyggPubSub pubSub;
    /** The packet processor used to send/receive packets */
    private final HyggPacketProcessor packetProcessor;

    /**
     * Constructor of {@link HyggdrasilAPI}
     * @param logger APIs logger
     * @param jedisPool {@link JedisPool} used for all Redis actions
     * @param environmentSupplier The supplier of the running application environment
     */
    public HyggdrasilAPI(Logger logger, JedisPool jedisPool, Supplier<HyggEnvironment> environmentSupplier) {
        HyggdrasilAPI.logger = logger;
        this.jedisPool = jedisPool;
        this.environmentSupplier = environmentSupplier;
        this.scheduler = new HyggScheduler();
        this.pubSub = new HyggPubSub(this);
        this.packetProcessor = new HyggPacketProcessor(this);
    }

    /**
     * Start {@link HyggdrasilAPI}
     */
    public void start() {
        log("Starting " + API_NAME + "...");

        this.pubSub.start();
        this.environment = this.environmentSupplier.get();

        if (this.environment.getApplication().getType() != HyggApplication.Type.HYGGDRASIL) {
            this.heartbeatTask = new HyggHeartbeatTask(this);
        }
    }

    /**
     * Stop {@link HyggdrasilAPI}
     */
    public void stop(String reason) {
        log("Stopping " + API_NAME + (reason != null ? " (reason: " + reason + ")" : "") + "...");

        this.pubSub.stop();
        this.scheduler.stop();
    }

    /**
     * Stop {@link HyggdrasilAPI}
     */
    public void stop() {
        this.stop(null);
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
     * Get the running application environment
     *
     * @return {@link HyggEnvironment} object
     */
    public HyggEnvironment getEnvironment() {
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
     * Get Hyggdrasil scheduler instance
     *
     * @return {@link HyggScheduler} instance
     */
    public HyggScheduler getScheduler() {
        return this.scheduler;
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
     * Check if the API is only accepting packets signed by Hyggdrasil
     *
     * @return <code>true</code> if yes
     */
    public boolean onlyAcceptingHyggdrasilPackets() {
        return this.environment.getKeys().getPrivate() == null;
    }

    /**
     * {@link HyggdrasilAPI} builder class
     */
    public static class Builder implements IBuilder<HyggdrasilAPI> {

        /** Logger builder option */
        private final BuilderEntry<Logger> loggerEntry = new BuilderEntry<>("Logger", () -> Logger.getLogger(HyggdrasilAPI.class.getName())).optional();
        /** Jedis pool builder option */
        private final BuilderEntry<JedisPool> jedisPoolEntry = new BuilderEntry<JedisPool>("Jedis Pool").required();
        /** {@link HyggEnvironment} builder option */
        private final BuilderEntry<HyggEnvironment> environmentEntry = new BuilderEntry<>("Application environment", HyggEnvironment::loadFromEnvironmentVariables).required();

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
         * @param environment {@link HyggEnvironment} object
         * @return {@link Builder}
         */
        public Builder withEnvironment(HyggEnvironment environment) {
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
            return new HyggdrasilAPI(this.loggerEntry.get(), this.jedisPoolEntry.get(), this.environmentEntry.getAsSupplier());
        }

    }

}
