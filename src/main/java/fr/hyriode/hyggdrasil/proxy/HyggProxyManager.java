package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyManager {

    private final DockerSwarm swarm;

    private final Hyggdrasil hyggdrasil;

    public HyggProxyManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
    }

    public void startProxy() {
        final HyggProxy proxy = new HyggProxy();

        this.swarm.runService(new HyggProxyService(proxy));

        System.out.println("Started '" + proxy.getName() + "' (port: " + proxy.getPort() + ").");
    }

}
