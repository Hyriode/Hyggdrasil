package fr.hyriode.hyggdrasil.docker.network;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 12:22
 */
public class DockerNetwork {

    private final String name;
    private final DockerNetworkDriver driver;

    public DockerNetwork(String name, DockerNetworkDriver driver) {
        this.name = name;
        this.driver = driver;
    }

    public String getName() {
        return this.name;
    }

    public DockerNetworkDriver getDriver() {
        return this.driver;
    }

}
