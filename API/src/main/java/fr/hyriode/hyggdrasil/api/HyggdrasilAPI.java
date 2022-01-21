package fr.hyriode.hyggdrasil.api;

import com.google.gson.Gson;
import fr.hyriode.hyggdrasil.api.protocol.env.HyggEnvironment;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.pubsub.HyggPubSub;
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

    /** APIs name constant */
    public static final String NAME = "HyggdrasilAPI";
    /** APIs prefix constant. This is used for channels or to print information */
    public static final String PREFIX = "Hygg";
    /** {@link Gson} instance */
    public static final Gson GSON = new Gson();

    /** Static instance of the logger */
    private static Logger logger;
    /** {@link JedisPool} object */
    private final JedisPool jedisPool;
    /** The running application environment */
    private HyggEnvironment environment;
    /** The supplier of the running application environment */
    private final Supplier<HyggEnvironment> environmentSupplier;
    /** The Hyggdrasil scheduler instance */
    private final HyggScheduler scheduler;
    /** The Hyggdrasil PubSub instance */
    private final HyggPubSub pubSub;
    /** The packet processor used to send/receive packets */
    private final HyggPacketProcessor packetProcessor;

    /**
     * Constructor of {@link HyggdrasilAPI}
     *  @param logger APIs logger
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
        log("Starting " + NAME + "...");

        this.pubSub.start();
        this.environment = this.environmentSupplier.get();
    }

    /**
     * Stop {@link HyggdrasilAPI}
     */
    public void stop(String reason) {
        log("Stopping " + NAME + (reason != null ? " (reason: " + reason + ")" : "") + "...");

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
        logger.log(level, message);
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
