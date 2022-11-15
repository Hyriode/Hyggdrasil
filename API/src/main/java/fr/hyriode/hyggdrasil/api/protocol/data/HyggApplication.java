package fr.hyriode.hyggdrasil.api.protocol.data;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 21/01/2022 at 19:29.<br>
 *
 * Represents what an application running {@link HyggdrasilAPI} is.
 */
public class HyggApplication {

    /** A simple function that applies a {@link String} value and returns it with a prefix and in uppercase */
    private static final Function<String, String> ENV = value -> (HyggdrasilAPI.PREFIX + "_application_" + value).toUpperCase();
    /** The type environment variable key */
    public static final String TYPE_ENV = ENV.apply("type");
    /** The identifier environment variable key */
    public static final String NAME_ENV = ENV.apply("name");
    /** The started time environment variable key */
    public static final String STARTED_TIME_ENV = ENV.apply("started_time");

    /** The application {@link Type} */
    private final Type type;
    /** The name of the application. Ex: server-14sv8df */
    private final String name;
    /** The time when the server started (a timestamp in milliseconds) */
    private final long startedTime;

    /**
     * Constructor of {@link HyggApplication}
     *  @param type The type of the application
     * @param name The name of the application
     * @param startedTime The time when the application started
     */
    public HyggApplication(Type type, String name, long startedTime) {
        this.type = type;
        this.name = name;
        this.startedTime = startedTime;
    }

    /**
     * Get the application type
     *
     * @return A {@link Type}
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Get the application name.<br>
     * Example: proxy-vfd12w
     *
     * @return A name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the time when the application started.<br>
     * The time is in a timestamp format in milliseconds
     *
     * @return A timestamp
     */
    public long getStartedTime() {
        return this.startedTime;
    }

    /**
     * Load application information from environment variables if they are set.<br>
     * In most cases, Hyggdrasil will automatically provide them if the application was started by it.
     *
     * @return {@link HyggApplication} object
     */
    static HyggApplication loadFromEnvironmentVariables() {
        HyggdrasilAPI.log("Loading application information from environment variables...");

        final Type type = Type.valueOf(System.getenv(TYPE_ENV));
        final String id = System.getenv(NAME_ENV);
        final long startedTime = Long.parseLong(System.getenv(STARTED_TIME_ENV));

        return new HyggApplication(type, id, startedTime);
    }

    /**
     * Create environment variables list from the application object
     *
     * @return A list of string
     */
    List<String> createEnvironmentVariables() {
        final List<String> variables = new ArrayList<>();

        variables.add(TYPE_ENV + "=" + this.type);
        variables.add(NAME_ENV + "=" + this.name);
        variables.add(STARTED_TIME_ENV + "=" + this.startedTime);

        return variables;
    }

    /**
     * All the types of an application running with {@link fr.hyriode.hyggdrasil.api.HyggdrasilAPI}
     */
    public enum Type {

        /** The running application is Hyggdrasil */
        HYGGDRASIL(false),
        /** The running application is a Minecraft server */
        SERVER(true),
        /** The running application is a Minecraft proxy: a BungeeCord, a Waterfall etc */
        PROXY(true),
        /** The application is another type */
        OTHER(false);

        /** Is the type using the heartbeat protocol to communicate with Hyggdrasil */
        private final boolean usingHeartbeats;

        /**
        * The constructor of an {@linkplain Type application type}
         *
         * @param usingHeartbeats <cdoe>true</cdoe> if the type is using heartbeats
         */
        Type(boolean usingHeartbeats) {
            this.usingHeartbeats = usingHeartbeats;
        }

        /**
         * Check if the type is using heartbeat protocol
         *
         * @return <code>true</code> if it's using heartbeats
         */
        public boolean isUsingHeartbeats() {
            return this.usingHeartbeats;
        }

    }

}
