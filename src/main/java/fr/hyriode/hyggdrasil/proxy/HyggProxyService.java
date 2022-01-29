package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.PortUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyService extends DockerService {

    private static final DockerImage BUNGEECORD_IMAGE = new DockerImage("itzg/bungeecord", "latest");

    private static final int MIN_PORT = 45565;
    private static final int MAX_PORT = 65535;

    private final Hyggdrasil hyggdrasil;
    private final HyggProxy proxy;

    public HyggProxyService(Hyggdrasil hyggdrasil, HyggProxy proxy) {
        super(proxy.getName(), BUNGEECORD_IMAGE, References.HYRIODE_NETWORK);
        this.hyggdrasil = hyggdrasil;
        this.proxy = proxy;

        this.hostname = proxy.getName();
        this.targetPort = 25577;
        this.publishedPort = PortUtil.nextAvailablePort(MIN_PORT, MAX_PORT);
        this.envs = this.createEnvs();

        this.addLabel(References.STACK_NAME_LABEL, References.STACK_NAME);

        this.addMount(References.DATA_HOST_FOLDER + "/proxies/plugins", "/plugins");

        proxy.setPort(this.publishedPort);
    }

    private List<String> createEnvs() {
        final List<String> envs = new ArrayList<>(this.hyggdrasil.createEnvsForClient(new HyggApplication(HyggApplication.Type.PROXY, this.proxy.getName())));

        envs.add("TYPE=WATERFALL");
        envs.add("ENABLE_RCON=FALSE");

        return envs;
    }

}
