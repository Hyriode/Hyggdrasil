package fr.hyriode.hyggdrasil.common;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.docker.network.DockerNetwork;
import fr.hyriode.hyggdrasil.docker.network.DockerNetworkDriver;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/05/2022 at 14:53
 */
public class HyriodeNetwork extends DockerNetwork {

    private static HyriodeNetwork instance;

    public HyriodeNetwork() {
        super(Hyggdrasil.getConfig().getDocker().getStackName() + "_" + Hyggdrasil.getConfig().getDocker().getNetworkName(), DockerNetworkDriver.OVERLAY);
        instance = this;

        System.out.println("Created Hyriode Docker network...");
    }

    public static HyriodeNetwork get() {
        return instance;
    }

}
