package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggEnv;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Paths;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyService extends DockerService {

    public HyggProxyService(HyggProxy proxy, DockerImage image) {
        super(proxy.getName(), image, References.NETWORK.get());
        this.hostname = proxy.getName();
        this.publishedPort = proxy.getPort();
        this.targetPort = 25577;

        this.envs.addAll(new HyggEnv(new HyggApplication(HyggApplication.Type.PROXY, proxy.getName(), System.currentTimeMillis())).createEnvironmentVariables());

        this.addLabel(References.STACK_NAME_LABEL, Hyggdrasil.getConfig().getDocker().getStackName());
        this.addMount(Paths.get(Hyggdrasil.getConfig().getDocker().getRootDirectory(), "proxies", proxy.getName()).toAbsolutePath().toString(), "/server");
    }

}
