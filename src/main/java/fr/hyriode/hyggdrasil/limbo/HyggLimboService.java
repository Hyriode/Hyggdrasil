package fr.hyriode.hyggdrasil.limbo;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggEnv;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.References;

/**
 * Created by AstFaster
 * on 31/12/2022 at 18:00
 */
public class HyggLimboService extends DockerService {

    public HyggLimboService(HyggLimbo limbo, DockerImage image) {
        super(limbo.getName(), image, References.NETWORK.get());
        this.hostname = limbo.getName();

        this.envs.addAll(new HyggEnv(new HyggApplication(HyggApplication.Type.LIMBO, limbo.getName(), System.currentTimeMillis())).createEnvironmentVariables());

        this.addLabel(References.STACK_NAME_LABEL, Hyggdrasil.getConfig().getDocker().getStackName());
    }

}
