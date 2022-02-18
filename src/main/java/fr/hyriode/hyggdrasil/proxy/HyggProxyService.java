package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.PortUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyService extends DockerService {

    private static final int MIN_PORT = 45565;
    private static final int MAX_PORT = 65535;

    public HyggProxyService(Hyggdrasil hyggdrasil, HyggProxy proxy) {
        super(proxy.getName(), HyggProxyManager.PROXY_IMAGE, References.HYRIODE_NETWORK);
        this.hostname = proxy.getName();
        this.targetPort = 25577;
        this.publishedPort = PortUtil.nextAvailablePort(MIN_PORT, MAX_PORT);

        this.envs.addAll(hyggdrasil.createEnvsForClient(new HyggApplication(HyggApplication.Type.PROXY, proxy.getName(), System.currentTimeMillis())));

        this.addLabel(References.STACK_NAME_LABEL, References.STACK_NAME);

        this.addMount(References.DATA_HOST_FOLDER + "/proxies/plugins", "/plugins");

        proxy.setPort(this.publishedPort);
    }

}
