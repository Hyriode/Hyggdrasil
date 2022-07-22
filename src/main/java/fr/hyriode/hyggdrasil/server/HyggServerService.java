package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.common.HyriodeNetwork;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.References;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 14:07
 */
public class HyggServerService extends DockerService {

    public HyggServerService(Hyggdrasil hyggdrasil, HyggServer server) {
        super(server.getName(), HyggServerManager.SERVER_IMAGE, HyriodeNetwork.get());
        this.hostname = server.getName();

        this.addLabel(References.STACK_NAME_LABEL, Hyggdrasil.getConfig().getDocker().getStackName());

        this.envs.addAll(server.getOptions().asEnvs());
        this.envs.addAll(hyggdrasil.createEnvsForClient(new HyggApplication(HyggApplication.Type.SERVER, this.hostname, System.currentTimeMillis()), server.getData()));

        final String serverFolder = Hyggdrasil.getConfig().getDocker().getDataFolder() + "/servers/" + server.getName();

        this.addMount(serverFolder, "/data");
    }

}
