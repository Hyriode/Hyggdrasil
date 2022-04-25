package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.References;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 14:07
 */
public class HyggServerService extends DockerService {

    public HyggServerService(Hyggdrasil hyggdrasil, HyggServer server) {
        super(server.getName(), HyggServerManager.SERVER_IMAGE, References.HYRIODE_NETWORK);

        this.hostname = server.getName();

        this.addLabel(References.STACK_NAME_LABEL, References.STACK_NAME);

        this.envs.addAll(server.getOptions().asEnvs());
        this.envs.addAll(hyggdrasil.createEnvsForClient(new HyggApplication(HyggApplication.Type.SERVER, this.hostname, System.currentTimeMillis()), server.getData()));

        final String serverFolder = References.DATA_HOST_FOLDER + "/servers/" + server.getName();

        this.addMount(serverFolder, "/data");
    }

}
