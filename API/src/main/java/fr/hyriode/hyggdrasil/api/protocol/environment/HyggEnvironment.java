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
    /** The {@link HyggKeys} object. The keys are used to sign messages or verify them */
    private final HyggKeys keys;
    /** The data to provide to the application */
    private final HyggData data;

    /**
     * Default constructor of a {@link HyggEnvironment}.<br>
     * This constructor is used when the application has data and keys provided by Hyggdrasil itself.
     *
     * @param application The application information
     * @param keys The keys used for messages
     * @param data A data dictionary
     */
    public HyggEnvironment(HyggApplication application, HyggKeys keys, HyggData data) {
        this.application = application;
        this.keys = keys;
        this.data = data;
    }

    /**
     * Second constructor of a {@link HyggEnvironment}.<br>
     * This constructor is used when the application that run Hyggdrasil API doesn't have data or keys.
     *
     * @param application The application information
     */
    public HyggEnvironment(HyggApplication application) {
        this(application, null, null);
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
     * @return {@link HyggEnvironment} object
     */
    public static HyggEnvironment loadFromEnvironmentVariables() {
        HyggdrasilAPI.log("Loading application environment variables...");

        return new HyggEnvironment(HyggApplication.loadFromEnvironmentVariables(), HyggKeys.loadFromEnvironmentVariables(), HyggData.loadFromEnvironmentVariables());
    }

    /**
     * Create environment variables list from environment object
     *
     * @return A list of string
     */
    public List<String> createEnvironmentVariables() {
        final List<String> variables = new ArrayList<>();

        variables.addAll(this.application.createEnvironmentVariables());
        variables.addAll(this.keys.createEnvironmentVariables());
        variables.add(this.data.asEnvironmentVariable());

        return variables;
    }

}
