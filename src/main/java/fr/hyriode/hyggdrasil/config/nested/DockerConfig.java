package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 13/05/2022 at 18:32
 */
public class DockerConfig {

    private final String serversStack;
    private final String proxiesStack;
    private final String networkName;
    private final String rootDirectory;

    public DockerConfig(String serversStack, String proxiesStack, String networkName, String rootDirectory) {
        this.serversStack = serversStack;
        this.proxiesStack = proxiesStack;
        this.networkName = networkName;
        this.rootDirectory = rootDirectory;
    }

    public DockerConfig() {
        this("hyggdrasil", "hyggdrasil", "hyggdrasil", "/home/");
    }

    public String getServersStack() {
        return this.serversStack;
    }

    public String getProxiesStack() {
        return this.proxiesStack;
    }

    public String getNetworkName() {
        return this.networkName;
    }

    public String getRootDirectory() {
        return this.rootDirectory;
    }

}
