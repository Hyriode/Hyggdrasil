package fr.hyriode.hyggdrasil.util;

import fr.hyriode.hyggdrasil.docker.network.DockerNetwork;
import fr.hyriode.hyggdrasil.docker.network.DockerNetworkDriver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class References {

    /** Global */
    public static final String NAME = "Hyggdrasil";

    /** Files */
    public static final Path LOG_FOLDER = Paths.get("logs");
    public static final Path LOG_FILE = Paths.get(LOG_FOLDER.toString(), "hyggdrasil.log");

    /** Redis */
    public static final String REDIS_HASH = "hyggdrasil:";

    /** Docker */
    public static final String STACK_NAME = System.getenv("STACK_NAME");
    public static final String STACK_NAME_LABEL = "com.docker.stack.namespace";
    public static final DockerNetwork HYRIODE_NETWORK = new DockerNetwork(STACK_NAME + "_" + System.getenv("NETWORK_NAME"), DockerNetworkDriver.OVERLAY);

}
