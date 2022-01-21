package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerService;
import fr.hyriode.hyggdrasil.util.References;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 14:07
 */
public class HyggServerService extends DockerService {

    private static final DockerImage SERVER_IMAGE = new DockerImage("itzg/minecraft-server", "java8");

    public HyggServerService(HyggServer server) {
        super(server.getName(), SERVER_IMAGE, References.HYRIODE_NETWORK);

        this.hostname = server.getName();

        this.addLabel(References.STACK_NAME_LABEL, References.STACK_NAME);
        this.addEnv("TYPE", "SPIGOT");
        this.addEnv("VERSION", "1.8.8-R0.1-SNAPSHOT-latest");
        this.addEnv("ONLINE_MODE", "FALSE");
        this.addEnv("ENABLE_RCON", "FALSE");
        this.addEnv("EULA", "TRUE");
    }

}
