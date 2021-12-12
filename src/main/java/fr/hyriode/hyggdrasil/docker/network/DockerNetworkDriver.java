package fr.hyriode.hyggdrasil.docker.network;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 12:21
 */
public enum DockerNetworkDriver {

    BRIDGE("bridge"),
    HOST("host"),
    OVERLAY("overlay"),
    MAC_VLAN("macvlan");

    private final String name;

    DockerNetworkDriver(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
