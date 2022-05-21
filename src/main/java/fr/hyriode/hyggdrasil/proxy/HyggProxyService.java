package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.common.HyriodeNetwork;
import fr.hyriode.hyggdrasil.config.HyggConfig;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.References;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyService extends DockerService {

    public HyggProxyService(Hyggdrasil hyggdrasil, HyggProxy proxy, boolean first) {
        super(proxy.getName(), HyggProxyManager.PROXY_IMAGE, HyriodeNetwork.get());
        this.hostname = proxy.getName();
        this.publishedPort = proxy.getPort();
        this.targetPort = 25577;

        final HyggData data = new HyggData();

        if (first) {
            data.add("first-proxy", "true");
        }

        this.envs.addAll(hyggdrasil.createEnvsForClient(new HyggApplication(HyggApplication.Type.PROXY, proxy.getName(), System.currentTimeMillis()), data));

        this.addLabel(References.STACK_NAME_LABEL, Hyggdrasil.getConfig().getDocker().getStackName());

        final String proxyFolder = Hyggdrasil.getConfig().getDocker().getDataFolder() + "/proxies/" + proxy.getName();

        this.addMount(proxyFolder, "/server");
    }

}
