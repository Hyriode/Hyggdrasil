package fr.hyriode.hyggdrasil.util;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.config.HyggConfig;
import fr.hyriode.hyggdrasil.docker.network.DockerNetwork;
import fr.hyriode.hyggdrasil.docker.network.DockerNetworkDriver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class References {

    /** Global */
    public static final String NAME = "Hyggdrasil";
    public static final String USER_DIR = System.getProperty("user.dir");

    /** Files */
    public static final Path LOG_FOLDER = Paths.get(USER_DIR, "logs");
    public static final Path LOG_FILE = Paths.get(LOG_FOLDER.toString(), "hyggdrasil.log");

    /** Files - Data */
    public static final Path DATA_FOLDER = Paths.get(USER_DIR, "data");

    public static final Path SERVERS_FOLDER = Paths.get(DATA_FOLDER.toString(), "servers");
    public static final Path SERVERS_COMMON_FOLDER = Paths.get(SERVERS_FOLDER.toString(), "common");
    public static final Path SERVERS_TYPES_FOLDER = Paths.get(SERVERS_FOLDER.toString(), "types");

    public static final Path PROXIES_FOLDER = Paths.get(DATA_FOLDER.toString(), "proxies");
    public static final Path PROXIES_COMMON_FOLDER = Paths.get(PROXIES_FOLDER.toString(), "common");

    public static final Path PRIVATE_KEY_FILE = Paths.get(DATA_FOLDER.toString(), "private.key");

    /** Files - Images */
    public static final Path IMAGES_FOLDER = Paths.get(USER_DIR, "images");
    public static final Path SERVER_IMAGES_FOLDER = Paths.get(IMAGES_FOLDER.toString(), "server");
    public static final Path PROXY_IMAGES_FOLDER = Paths.get(IMAGES_FOLDER.toString(), "proxy");

    /** Docker */
    public static final String STACK_NAME_LABEL = "com.docker.stack.namespace";

}
