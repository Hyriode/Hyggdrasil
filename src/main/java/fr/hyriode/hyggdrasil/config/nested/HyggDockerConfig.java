package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 13/05/2022 at 18:32
 */
public class HyggDockerConfig {

    private final String stackName;
    private final String networkName;
    private final String dataFolder;

    public HyggDockerConfig(String stackName, String networkName, String dataFolder) {
        this.stackName = stackName;
        this.networkName = networkName;
        this.dataFolder = dataFolder;
    }

    public HyggDockerConfig() {
        this("hyggdrasil", "hyggdrasil", "/home/");
    }

    public String getStackName() {
        return this.stackName;
    }

    public String getNetworkName() {
        return this.networkName;
    }

    public String getDataFolder() {
        return this.dataFolder;
    }

}
