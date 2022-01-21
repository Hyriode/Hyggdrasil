package fr.hyriode.hyggdrasil.api.protocol.env;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 21/01/2022 at 19:28
 */
public class HyggEnvironment {

    /** The {@link HyggApplication} object */
    private final HyggApplication application;
    /** The {@link HyggRedisCredentials} object */
    private final HyggRedisCredentials redisCredentials;
    /** The {@link HyggKeys} object. The keys are used to sign messages or verify them */
    private final HyggKeys keys;

    /**
     * Constructor of {@link HyggEnvironment}
     *  @param application The application information
     * @param redisCredentials The credentials used for all Redis information
     * @param keys The keys used for messages
     */
    public HyggEnvironment(HyggApplication application, HyggRedisCredentials redisCredentials, HyggKeys keys) {
        this.application = application;
        this.redisCredentials = redisCredentials;
        this.keys = keys;
    }

    /**
     * Get the running application environment
     *
     * @return {@link HyggApplication} object
     */
    public HyggApplication getApplication() {
        return this.application;
    }

    /**
     * Get the Redis credentials environment
     *
     * @return {@link HyggRedisCredentials} object
     */
    public HyggRedisCredentials getRedisCredentials() {
        return this.redisCredentials;
    }

    /**
     * Get the keys environment
     *
     * @return {@link HyggKeys} object
     */
    public HyggKeys getKeys() {
        return this.keys;
    }

    /**
     * Load application information from environment variables if they are set.<br>
     * In most cases, Hydra will automatically provide them if the application was started by it.
     *
     * @return {@link HyggRedisCredentials} object
     */
    public static HyggEnvironment loadFromEnvironmentVariables() {
        System.out.println("Loading application environment variables...");

        return new HyggEnvironment(HyggApplication.loadFromEnvironmentVariables(), HyggRedisCredentials.loadFromEnvironmentVariables(), HyggKeys.loadFromEnvironmentVariables());
    }

}
