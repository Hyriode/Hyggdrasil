package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 13/05/2022 at 18:32
 */
public class DockerConfig {

    private final String stackName;
    private final String networkName;
    private final String rootDirectory;

    public DockerConfig(String stackName, String networkName, String rootDirectory) {
        this.stackName = stackName;
        this.networkName = networkName;
        this.rootDirectory = rootDirectory;
    }

    public DockerConfig() {
        this("hyggdrasil", "hyggdrasil", "/home/");
    }

    public String getStackName() {
        return this.stackName;
    }

    public String getNetworkName() {
        return this.networkName;
    }

    public String getRootDirectory() {
        return this.rootDirectory;
    }

}
