package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggProxyServerPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 14:06
 */
public class HyggServerManager {

    private final DockerSwarm swarm;

    private final Hyggdrasil hyggdrasil;

    public HyggServerManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
    }

    public void startServer(String type) {
        final HyggServer server = new HyggServer(type);
        final HyggProxyServerPacket packet = new HyggProxyServerPacket(HyggProxyServerPacket.Type.ADD, server.getName());

        this.swarm.runService(new HyggServerService(server));

        this.hyggdrasil.getAPI().getPacketProcessor().request(HyggChannel.PROXIES, packet)
                .withSendingCallback(() -> System.out.println("Hook packet sent for " + server.getName()))
                .withResponseCallback(response -> System.out.println("Proxy added server!"))
                .exec();

        System.out.println("Started '" + server.getName() + "'.");
    }

}
