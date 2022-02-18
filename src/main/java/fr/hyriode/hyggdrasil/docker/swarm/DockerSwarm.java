package fr.hyriode.hyggdrasil.docker.swarm;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Service;
import com.github.dockerjava.api.model.SwarmNode;
import fr.hyriode.hyggdrasil.docker.Docker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 12:27
 */
public class DockerSwarm {

    private final Map<String, DockerService> services;

    private final DockerClient dockerClient;

    public DockerSwarm(Docker docker) {
        this.dockerClient = docker.getDockerClient();
        this.services = new HashMap<>();
    }

    public void runService(DockerService service) {
        this.dockerClient.createServiceCmd(service.toSwarmService()).exec();

        this.services.put(service.getName(), service);
    }

    public void removeService(String serviceName) {
        this.dockerClient.removeServiceCmd(serviceName).exec();

        this.services.remove(serviceName);
    }

    public List<Service> listServices() {
        return this.dockerClient.listServicesCmd().exec();
    }

    public Map<String, DockerService> getServices() {
        return this.services;
    }

    public List<SwarmNode> listSwarmNodes() {
        return this.dockerClient.listSwarmNodesCmd().exec();
    }

}
