package fr.hyriode.hyggdrasil.docker.network;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 12:22
 */
public record DockerNetwork(String name, DockerNetworkDriver networkDriver) {

    public String getName() {
        return this.name;
    }

    public DockerNetworkDriver getNetworkDriver() {
        return this.networkDriver;
    }

}
