package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 13/05/2022 at 18:32
 */
public class DockerConfig {

    private final String serversStack;
    private final String proxiesStack;
    private final String limbosStack;
    private final String networkName;
    private final String rootDirectory;
    private final String servicesPrefix;

    public DockerConfig(String serversStack, String proxiesStack, String limbosStack, String networkName, String rootDirectory, String servicesPrefix) {
        this.serversStack = serversStack;
        this.proxiesStack = proxiesStack;
        this.limbosStack = limbosStack;
        this.networkName = networkName;
        this.rootDirectory = rootDirectory;
        this.servicesPrefix = servicesPrefix;
    }

    public DockerConfig() {
        this("hyggdrasil", "hyggdrasil", "hyggdrasil", "hyggdrasil", "/home/", "");
    }

    public String getServersStack() {
        return this.serversStack;
    }

    public String getProxiesStack() {
        return this.proxiesStack;
    }

    public String getLimbosStack() {
        return this.limbosStack;
    }

    public String getNetworkName() {
        return this.networkName;
    }

    public String getRootDirectory() {
        return this.rootDirectory;
    }

    public String getServicesPrefix() {
        return this.servicesPrefix;
    }

}
