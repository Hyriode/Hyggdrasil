package fr.hyriode.hyggdrasil.docker.swarm;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Service;
import fr.hyriode.hyggdrasil.docker.Docker;

import java.util.Collections;
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
        try {
            this.dockerClient.removeServiceCmd(serviceName).exec();
        } catch (Exception ignored) {}

        this.services.remove(serviceName);
    }

    public String replicaId(String serviceName) {
        final List<Container> containers = this.dockerClient.listContainersCmd()
                .withNameFilter(Collections.singletonList(serviceName))
                .exec();

        if (containers == null || containers.size() == 0) {
            return null;
        }
        return containers.get(0).getId();
    }

    public void pauseReplica(String replicaId) {
        this.dockerClient.pauseContainerCmd(replicaId).exec();
    }

    public void unpauseReplica(String replicaId) {
        this.dockerClient.unpauseContainerCmd(replicaId).exec();
    }

    public List<Service> listServices() {
        return this.dockerClient.listServicesCmd().exec();
    }

    public Map<String, DockerService> getServices() {
        return this.services;
    }

}
