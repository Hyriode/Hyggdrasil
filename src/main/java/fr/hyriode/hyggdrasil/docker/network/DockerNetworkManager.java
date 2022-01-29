package fr.hyriode.hyggdrasil.docker.network;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.model.Network;
import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.docker.Docker;

import java.util.List;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 12:21
 */
public class DockerNetworkManager {

    private final DockerClient dockerClient;

    public DockerNetworkManager(Docker docker) {
        this.dockerClient = docker.getDockerClient();
    }

    public void createNetwork(DockerNetwork network) {
        final List<Network> networks = this.dockerClient.listNetworksCmd().exec();

        for (Network n : networks) {
            if (n.getName().equals(network.getName())) {
                Hyggdrasil.log(Level.SEVERE, "'" + network.getName() + "' network already exists ! Couldn't create it !");
                return;
            }
        }

        final CreateNetworkResponse networkResponse = this.dockerClient
                .createNetworkCmd()
                .withName(network.getName())
                .withAttachable(true)
                .withDriver(network.getNetworkDriver().getName())
                .exec();

        System.out.println("'" + network.getName() + "' network was successfully created with '" + networkResponse.getId() + "' id.");
    }

}
