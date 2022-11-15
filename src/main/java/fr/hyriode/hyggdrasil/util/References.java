package fr.hyriode.hyggdrasil.util;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.docker.network.DockerNetwork;
import fr.hyriode.hyggdrasil.docker.network.DockerNetworkDriver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class References {

    /** Global */
    public static final String NAME = "Hyggdrasil";
    public static final String USER_DIR = System.getProperty("user.dir");

    /** Files */
    public static final Path LOG_FOLDER = Paths.get(USER_DIR, "logs");
    public static final Path LOG_FILE = Paths.get(LOG_FOLDER.toString(), "hyggdrasil.log");

    /** Files - Data */
    public static final Path DATA_FOLDER = Paths.get(USER_DIR, "data");

    public static final Path CACHE_FOLDER = Paths.get(USER_DIR, ".cache");

    public static final Path TEMPLATES_FOLDER = Paths.get(DATA_FOLDER.toString(), "templates");

    public static final Path SERVERS_FOLDER = Paths.get(USER_DIR, "servers");
    public static final Path PROXIES_FOLDER = Paths.get(USER_DIR, "proxies");

    /** Files - Images */
    public static final Path IMAGES_FOLDER = Paths.get(USER_DIR, "images");
    public static final Path SERVER_IMAGES_FOLDER = Paths.get(IMAGES_FOLDER.toString(), "server");
    public static final Path PROXY_IMAGES_FOLDER = Paths.get(IMAGES_FOLDER.toString(), "proxy");

    /** Docker */
    public static final String STACK_NAME_LABEL = "com.docker.stack.namespace";
    public static final Supplier<DockerNetwork> NETWORK = new Supplier<>() {
        private DockerNetwork network;

        @Override
        public DockerNetwork get() {
            return this.network == null ? this.network = new DockerNetwork(Hyggdrasil.getConfig().getDocker().getStackName() + "_" + Hyggdrasil.getConfig().getDocker().getNetworkName(), DockerNetworkDriver.OVERLAY) : this.network;
        }
    };

}
