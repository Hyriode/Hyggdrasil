package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.PortUtil;
import fr.hyriode.hyggdrasil.util.References;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyService extends DockerService {

    private static final DockerImage BUNGEECORD_IMAGE = new DockerImage("itzg/bungeecord", "latest");

    private static final int MIN_PORT = 45565;
    private static final int MAX_PORT = 65535;

    public HyggProxyService(HyggProxy proxy) {
        super(proxy.getName(), BUNGEECORD_IMAGE, References.HYRIODE_NETWORK);

        this.hostname = proxy.getName();
        this.targetPort = 25577;
        this.publishedPort = PortUtil.nextAvailablePort(MIN_PORT, MAX_PORT);

        this.addLabel(References.STACK_NAME_LABEL, References.STACK_NAME);
        this.addEnv("TYPE", "BUNGEECORD");
        this.addEnv("ENABLE_RCON", "FALSE");
        this.addEnv("PLUGINS", "https://hyriode.fr/HyggBungee-1.0.0-all.jar");

        proxy.setPort(this.publishedPort);
    }

}
