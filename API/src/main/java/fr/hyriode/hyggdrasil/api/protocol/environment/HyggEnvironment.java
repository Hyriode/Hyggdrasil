package fr.hyriode.hyggdrasil.api.protocol.environment;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.util.ArrayList;
import java.util.List;

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
    /** The data to provide to the application */
    private final HyggData data;

    /**
     * Constructor of {@link HyggEnvironment}
     * @param application The application information
     * @param redisCredentials The credentials used for all Redis information
     * @param keys The keys used for messages
     * @param data A data dictionary
     */
    public HyggEnvironment(HyggApplication application, HyggRedisCredentials redisCredentials, HyggKeys keys, HyggData data) {
        this.application = application;
        this.redisCredentials = redisCredentials;
        this.keys = keys;
        this.data = data;
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
     * Get the data environment
     *
     * @return {@link HyggData} object
     */
    public HyggData getData() {
        return this.data;
    }

    /**
     * Load application information from environment variables if they are set.<br>
     * In most cases, Hyggdrasil will automatically provide them if the application was started by it.
     *
     * @return {@link HyggRedisCredentials} object
     */
    public static HyggEnvironment loadFromEnvironmentVariables() {
        HyggdrasilAPI.log("Loading application environment variables...");

        return new HyggEnvironment(HyggApplication.loadFromEnvironmentVariables(), HyggRedisCredentials.loadFromEnvironmentVariables(), HyggKeys.loadFromEnvironmentVariables(), HyggData.loadFromEnvironmentVariables());
    }

    /**
     * Create environment variables list from environment object
     *
     * @return A list of string
     */
    public List<String> createEnvironmentVariables() {
        final List<String> variables = new ArrayList<>();

        variables.addAll(this.application.createEnvironmentVariables());
        variables.addAll(this.redisCredentials.createEnvironmentVariables());
        variables.addAll(this.keys.createEnvironmentVariables());
        variables.add(this.data.asEnvironmentVariable());

        return variables;
    }

}
