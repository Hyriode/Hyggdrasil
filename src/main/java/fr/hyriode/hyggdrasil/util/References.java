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

    /** Files */
    public static final Path LOG_FOLDER = Paths.get("logs");
    public static final Path LOG_FILE = Paths.get(LOG_FOLDER.toString(), "hyggdrasil.log");

    public static final Path TMP_FOLDER = Paths.get("tmp");

    public static final Path TEMPLATES_FOLDER = Paths.get("templates");

    public static final Path SERVERS_FOLDER = Paths.get("servers");
    public static final Path PROXIES_FOLDER = Paths.get("proxies");
    public static final Path LIMBOS_FOLDER = Paths.get("limbos");

    /** Files - Images */
    public static final Path IMAGES_FOLDER = Paths.get("images");

    /** Docker */
    public static final String STACK_NAME_LABEL = "com.docker.stack.namespace";
    public static final Supplier<DockerNetwork> NETWORK = new Supplier<>() {
        private DockerNetwork network;

        @Override
        public DockerNetwork get() {
            return this.network == null ? this.network = new DockerNetwork(Hyggdrasil.getConfig().getDocker().getNetworkName(), DockerNetworkDriver.OVERLAY) : this.network;
        }
    };

}
