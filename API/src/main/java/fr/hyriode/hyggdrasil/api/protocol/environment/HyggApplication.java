package fr.hyriode.hyggdrasil.api.protocol.environment;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 21/01/2022 at 19:29
 */
public class HyggApplication {

    /** A simple function that applies a {@link String} value and returns it with a prefix and in uppercase */
    private static final Function<String, String> ENV = value -> (HyggdrasilAPI.PREFIX + "_application_" + value).toUpperCase();
    /** The type environment variable key */
    public static final String TYPE_ENV = ENV.apply("type");
    /** The identifier environment variable key */
    public static final String NAME_ENV = ENV.apply("name");

    /** The application {@link Type} */
    private final Type type;
    /** The name of the application. Ex: server-14sv8df */
    private final String name;

    /**
     * Constructor of {@link HyggApplication}
     *
     * @param type The type of the application
     * @param name The name of the application
     */
    public HyggApplication(Type type, String name) {
        this.type = type;
        this.name = name;
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
     * Load application information from environment variables if they are set.<br>
     * In most cases, Hyggdrasil will automatically provide them if the application was started by it.
     *
     * @return {@link HyggApplication} object
     */
    static HyggApplication loadFromEnvironmentVariables() {
        HyggdrasilAPI.log("Loading application information from environment variables...");

        final Type type = Type.valueOf(System.getenv(TYPE_ENV));
        final String id = System.getenv(NAME_ENV);

        return new HyggApplication(type, id);
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

        return variables;
    }

    /**
     * All the types of an application running with {@link fr.hyriode.hyggdrasil.api.HyggdrasilAPI}
     */
    public enum Type {

        /** The running application is Hyggdrasil */
        HYGGDRASIL,
        /** The running application is a Minecraft server */
        SERVER,
        /** The running application is a Minecraft proxy: a BungeeCord, a Waterfall etc */
        PROXY,

    }

}
