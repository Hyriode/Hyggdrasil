package fr.hyriode.hyggdrasil.api.protocol.env;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

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
    public static final String ID_ENV = ENV.apply("id");

    /** The application {@link Type} */
    private final Type type;
    /** The identifier of the application. Ex: server-14sv8df */
    private final String id;

    /**
     * Constructor of {@link HyggApplication}
     *
     * @param type The type of the application
     * @param id The identifier of the application
     */
    public HyggApplication(Type type, String id) {
        this.type = type;
        this.id = id;
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
     * Get the application identifier.<br>
     * Example: proxy-vfd12w
     *
     * @return An identifier
     */
    public String getId() {
        return this.id;
    }

    /**
     * Load application information from environment variables if they are set.<br>
     * In most cases, Hydra will automatically provide them if the application was started by it.
     *
     * @return {@link HyggApplication} object
     */
    static HyggApplication loadFromEnvironmentVariables() {
        System.out.println("Loading application information from environment variables...");

        final Type type = Type.valueOf(System.getenv(TYPE_ENV));
        final String id = System.getenv(ID_ENV);

        return new HyggApplication(type, id);
    }

    /**
     * All the types of an application running with {@link fr.hyriode.hyggdrasil.api.HyggdrasilAPI}
     */
    public enum Type {

        /** The running application is Hydra */
        HYDRA,
        /** The running application is a Minecraft server */
        SERVER,
        /** The running application is a Minecraft proxy: a BungeeCord, a Waterfall etc */
        PROXY,
        /** The running application is not official or not declared */
        OTHER

    }

}
