package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggEnv;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Paths;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 14:07
 */
public class HyggServerService extends DockerService {

    public HyggServerService(HyggServer server, DockerImage image) {
        super(server.getName(), image, References.NETWORK.get());
        this.hostname = server.getName();

        this.envs.addAll(new HyggEnv(new HyggApplication(HyggApplication.Type.SERVER, this.hostname, System.currentTimeMillis())).createEnvironmentVariables());

        this.addLabel(References.STACK_NAME_LABEL, Hyggdrasil.getConfig().getDocker().getStackName());
        this.addMount(Paths.get(Hyggdrasil.getConfig().getDocker().getRootDirectory(), "servers", this.hostname).toAbsolutePath().toString(), "/server");
    }

}
