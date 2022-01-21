package fr.hyriode.hyggdrasil.api.protocol.env;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.util.function.Function;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 21/01/2022 at 19:34
 */
public class HyggRedisCredentials {

    /** A simple function that applies a {@link String} value and returns it with a prefix and in uppercase */
    private static final Function<String, String> ENV = value -> (HyggdrasilAPI.PREFIX + "_redis_" + value).toUpperCase();
    /** The hostname environment variable key */
    public static final String HOSTNAME_ENV = ENV.apply("hostname");
    /** The port environment variable key */
    public static final String PORT_ENV = ENV.apply("port");
    /** The password environment variable key */
    public static final String PASSWORD_ENV = ENV.apply("password");

    /** The hostname of the Redis database */
    private final String hostname;
    /** The port of the Redis database */
    private final short port;
    /** The password to use when connecting to Redis database */
    private final String password;

    /**
     * Constructor of {@link HyggRedisCredentials}
     *
     * @param hostname Redis hostname
     * @param port Redis listening port
     * @param password Redis password
     */
    public HyggRedisCredentials(String hostname, short port, String password) {
        this.hostname = hostname;
        this.port = port;
        this.password = password;
    }

    /**
     * Get the Redis hostname.<br>
     * It can be an ip or a Docker service name.<br>
     * Example: with an ip : 127.0.0.1, or with Docker: myhostname
     *
     * @return A hostname
     */
    public String getHostname() {
        return this.hostname;
    }

    /**
     * Get the Redis port.<br>
     * By default, Redis is listening on 6379
     *
     * @return A listening port
     */
    public short getPort() {
        return this.port;
    }

    /**
     * Get the Redis password.<br>
     * This password is used while connecting to Redis database.
     *
     * @return A password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Load Redis credentials from environment variables if they are set.<br>
     * In most cases, Hydra will automatically provide them if the application was started by it.
     *
     * @return {@link HyggRedisCredentials} object
     */
    static HyggRedisCredentials loadFromEnvironmentVariables() {
        System.out.println("Loading Redis credentials from environment variables...");

        final String hostname = System.getenv(HOSTNAME_ENV);
        final short port = Short.parseShort(PORT_ENV);
        final String password = System.getenv(PASSWORD_ENV);

        return new HyggRedisCredentials(hostname, port, password);
    }

}
