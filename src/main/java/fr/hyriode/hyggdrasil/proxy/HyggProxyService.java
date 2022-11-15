package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggEnv;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.References;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyService extends DockerService {

    public HyggProxyService(HyggProxy proxy) {
        super(proxy.getName(), HyggProxyManager.PROXY_IMAGE, References.NETWORK.get());
        this.hostname = proxy.getName();
        this.publishedPort = proxy.getPort();
        this.targetPort = 25577;

        this.envs.addAll(new HyggEnv(new HyggApplication(HyggApplication.Type.PROXY, proxy.getName(), System.currentTimeMillis())).createEnvironmentVariables());

        this.addLabel(References.STACK_NAME_LABEL, Hyggdrasil.getConfig().getDocker().getStackName());

        final String proxyFolder = Hyggdrasil.getConfig().getDocker().getDataFolder() + "/proxies/" + proxy.getName();

        this.addMount(proxyFolder, "/config");
        this.addMount(proxyFolder, "/server");
    }

}
