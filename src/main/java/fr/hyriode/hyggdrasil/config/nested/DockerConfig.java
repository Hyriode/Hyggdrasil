package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 13/05/2022 at 18:32
 */
public class DockerConfig {

    private final String stackName;
    private final String networkName;

    public DockerConfig(String stackName, String networkName) {
        this.stackName = stackName;
        this.networkName = networkName;
    }

    public DockerConfig() {
        this("hyggdrasil", "hyggdrasil");
    }

    public String getStackName() {
        return this.stackName;
    }

    public String getNetworkName() {
        return this.networkName;
    }

}
