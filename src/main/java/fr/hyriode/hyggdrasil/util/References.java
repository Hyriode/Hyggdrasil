package fr.hyriode.hyggdrasil.util;

import fr.hyriode.hyggdrasil.docker.network.DockerNetwork;
import fr.hyriode.hyggdrasil.docker.network.DockerNetworkDriver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

public class References {

    /** Global */
    public static final String NAME = "Hyggdrasil";
    public static final String USER_DIR = System.getProperty("user.dir");

    /** Files */
    public static final Path LOG_FOLDER = Paths.get(USER_DIR, "logs");
    public static final Path LOG_FILE = Paths.get(LOG_FOLDER.toString(), "hyggdrasil.log");

    public static final Path DATA_FOLDER = Paths.get(USER_DIR, "data");

    public static final Path SERVERS_FOLDER = Paths.get(DATA_FOLDER.toString(), "servers");
    public static final Path SERVERS_COMMON_FOLDER = Paths.get(SERVERS_FOLDER.toString(), "common");
    public static final Path SERVERS_TYPES_FOLDER = Paths.get(SERVERS_FOLDER.toString(), "types");

    public static final Path PROXIES_FOLDER = Paths.get(DATA_FOLDER.toString(), "proxies");
    public static final Path PROXIES_PLUGINS_FOLDER = Paths.get(PROXIES_FOLDER.toString(), "plugins");

    public static final Path PRIVATE_KEY_FILE = Paths.get(DATA_FOLDER.toString(), "private.key");

    /** Redis */
    public static final String REDIS_KEY = "hyggdrasil:";

    /** Docker */
    public static final String STACK_NAME = System.getenv("STACK_NAME");
    public static final String DATA_HOST_FOLDER = System.getenv("DATA_FOLDER");
    public static final String STACK_NAME_LABEL = "com.docker.stack.namespace";
    public static final DockerNetwork HYRIODE_NETWORK = new DockerNetwork(STACK_NAME + "_" + System.getenv("NETWORK_NAME"), DockerNetworkDriver.OVERLAY);

}
