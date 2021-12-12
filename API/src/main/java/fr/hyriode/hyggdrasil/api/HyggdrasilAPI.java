package fr.hyriode.hyggdrasil.api;

import fr.hyriode.hyggdrasil.api.util.builder.BuildException;
import fr.hyriode.hyggdrasil.api.util.builder.BuilderOption;
import fr.hyriode.hyggdrasil.api.util.builder.IBuilder;
import redis.clients.jedis.JedisPool;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 12/12/2021 at 11:56
 */
public class HyggdrasilAPI {

    /** Static instance of the logger */
    private static Logger logger;
    /** {@link JedisPool} object */
    private final JedisPool jedisPool;

    /**
     * Constructor of {@link HyggdrasilAPI}
     *
     * @param logger API's logger
     * @param jedisPool API's {@link JedisPool}
     */
    public HyggdrasilAPI(Logger logger, JedisPool jedisPool) {
        HyggdrasilAPI.logger = logger;
        this.jedisPool = jedisPool;
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
