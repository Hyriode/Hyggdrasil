package fr.hyriode.hyggdrasil.api;

import com.google.gson.Gson;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.pubsub.HyggPubSub;
import fr.hyriode.hyggdrasil.api.scheduler.HyggScheduler;
import fr.hyriode.hyggdrasil.api.util.builder.BuildException;
import fr.hyriode.hyggdrasil.api.util.builder.BuilderOption;
import fr.hyriode.hyggdrasil.api.util.builder.IBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
    /** {@link Gson} instance */
    public static final Gson GSON = new Gson();

    /** Static instance of the logger */
    private static Logger logger;
    /** {@link JedisPool} object */
    private final JedisPool jedisPool;
    /** The Hyggdrasil scheduler instance */
    private final HyggScheduler scheduler;
    /** The Hyggdrasil PubSub instance */
    private final HyggPubSub pubSub;
    /** The packet processor used to send/receive packets */
    private final HyggPacketProcessor packetProcessor;

    /**
     * Constructor of {@link HyggdrasilAPI}
     *
     * @param logger API's logger
     * @param jedisPool API's {@link JedisPool}
     */
    public HyggdrasilAPI(Logger logger, JedisPool jedisPool) {
        HyggdrasilAPI.logger = logger;
        this.jedisPool = jedisPool;
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

        /** Logger option */
        private final BuilderOption<Logger> loggerOption = new BuilderOption<>("Logger", () -> Logger.getLogger(HyggdrasilAPI.class.getName())).optional();
        /** Jedis pool option */
        private final BuilderOption<JedisPool> jedisPoolOption = new BuilderOption<JedisPool>("Jedis Pool").required();

        /**
         * Set logger to provide to {@link HyggdrasilAPI}
         *
         * @param logger {@link Logger} object
         * @return {@link Builder}
         */
        public Builder withLogger(Logger logger) {
            this.loggerOption.set(logger);
            return this;
        }

        /**
         * Set Jedis pool to provide to {@link HyggdrasilAPI}
         *
         * @param jedisPool {@link JedisPool} object
         * @return {@link Builder}
         */
        public Builder withJedisPool(JedisPool jedisPool) {
            this.jedisPoolOption.set(jedisPool);
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
            return new HyggdrasilAPI(this.loggerOption.get(), this.jedisPoolOption.get());
        }

    }

}
