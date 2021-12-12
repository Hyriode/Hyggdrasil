package fr.hyriode.hyggdrasil.docker.swarm;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Service;
import com.github.dockerjava.api.model.SwarmInfo;
import com.github.dockerjava.api.model.SwarmNode;
import com.github.dockerjava.api.model.SwarmNodeManagerStatus;
import fr.hyriode.hyggdrasil.docker.Docker;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 12:27
 */
public class DockerSwarm {

    private final Set<DockerService> services;

    private final DockerClient dockerClient;

    public DockerSwarm(Docker docker) {
        this.dockerClient = docker.getDockerClient();
        this.services = new HashSet<>();
    }

    public void runService(DockerService service) {
        this.dockerClient.createServiceCmd(service.toSwarmService()).exec();

        this.services.add(service);
    }

    public void removeService(DockerService service) {
        this.dockerClient.removeServiceCmd(service.getName()).exec();

        this.services.remove(service);
    }

    public List<Service> listServices() {
        return this.dockerClient.listServicesCmd().exec();
    }

    public Set<DockerService> getServices() {
        return this.services;
    }

    public boolean isSwarmActive() {
        final SwarmInfo info = this.dockerClient.infoCmd().exec().getSwarm();

        if (info != null) {
            return info.getNodeAddr() != null;
        }
        return false;
    }

    public List<SwarmNode> listSwarmNodes() {
        return this.dockerClient.listSwarmNodesCmd().exec();
    }

    public SwarmNode getSwarmNode(String id) {
        for (SwarmNode node : this.listSwarmNodes()) {
            if (node.getId() != null) {
                if (node.getId().equals(id)) {
                    return node;
                }
            }
        }
        return null;
    }

    public boolean isSwarmNodeManager() {
        final String id = Objects.requireNonNull(this.dockerClient.infoCmd().exec().getSwarm()).getNodeID();
        final SwarmNode node = this.getSwarmNode(id);

        if (node != null) {
            final SwarmNodeManagerStatus managerStatus = node.getManagerStatus();

            if (managerStatus != null) {
                return managerStatus.isLeader();
            }
        }
        return false;
    }

}
