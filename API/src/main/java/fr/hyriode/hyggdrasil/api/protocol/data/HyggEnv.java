package fr.hyriode.hyggdrasil.api.protocol.data;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 21/01/2022 at 19:28.<br>
 *
 * The environment variables given to an application started by Hyggdrasil.
 */
public class HyggEnv {

    /** The {@link HyggApplication} object */
    private final HyggApplication application;

    /**
     * Default constructor of a {@link HyggEnv}.
     *
     * @param application The application information
     */
    public HyggEnv(HyggApplication application) {
        this.application = application;
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
     * Load application information from environment variables if they are set.<br>
     * In most cases, Hyggdrasil will automatically provide them if the application was started by it.
     *
     * @return {@link HyggEnv} object
     */
    public static HyggEnv loadFromEnvironmentVariables() {
        HyggdrasilAPI.log("Loading application environment variables...");

        return new HyggEnv(HyggApplication.loadFromEnvironmentVariables());
    }

    /**
     * Create environment variables list from environment object
     *
     * @return A list of string
     */
    public List<String> createEnvironmentVariables() {
        return new ArrayList<>(this.application.createEnvironmentVariables());
    }

}
