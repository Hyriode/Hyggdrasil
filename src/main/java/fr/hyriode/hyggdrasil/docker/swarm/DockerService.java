package fr.hyriode.hyggdrasil.docker.swarm;

import com.github.dockerjava.api.model.*;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.network.DockerNetwork;

import java.util.*;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 12:20
 */
public class DockerService {

    protected final String name;

    protected final DockerImage image;
    protected final DockerNetwork network;

    protected String hostname;

    protected List<String> envs;

    protected final Map<String, String> labels;
    protected List<Mount> mounts;

    protected int publishedPort = -1;
    protected int targetPort = -1;

    public DockerService(String name, DockerImage image, DockerNetwork network) {
        this.name = name;
        this.image = image;
        this.network = network;
        this.envs = new ArrayList<>();
        this.labels = new HashMap<>();
        this.mounts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public DockerImage getImage() {
        return this.image;
    }

    public DockerNetwork getNetwork() {
        return this.network;
    }

    public DockerService addEnv(String env) {
        this.envs.add(env);
        return this;
    }

    public DockerService addEnv(String key, String value) {
        this.envs.add(key + "=" + value);
        return this;
    }

    public DockerService removeEnv(String env) {
        this.envs.remove(env);
        return this;
    }

    public List<String> getEnvs() {
        return this.envs;
    }

    public DockerService withEnvs(List<String> envs) {
        this.envs = envs;
        return this;
    }

    public DockerService addLabel(String name, String value) {
        this.labels.put(name, value);
        return this;
    }

    public DockerService removeLabel(String name) {
        this.labels.remove(name);
        return this;
    }

    public Map<String, String> getLabels() {
        return this.labels;
    }

    public DockerService addMount(String source, String target) {
        return this.addMount(source, target, MountType.BIND);
    }

    public DockerService addMount(String source, String target, MountType type) {
        this.mounts.add(new Mount()
                .withSource(source)
                .withTarget(target)
                .withType(type));
        return this;
    }

    public List<Mount> getMounts() {
        return this.mounts;
    }

    public DockerService withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPublishedPort() {
        return this.publishedPort;
    }

    public DockerService withPublishedPort(int publishedPort) {
        this.publishedPort = publishedPort;
        return this;
    }

    public int getTargetPort() {
        return this.targetPort;
    }

    public DockerService withTargetPort(int targetPort) {
        this.targetPort = targetPort;
        return this;
    }

    public ServiceSpec toSwarmService() {
        final ContainerSpec containerSpec = new ContainerSpec()
                .withImage(this.image.getName() + DockerImage.DOCKER_IMAGE_TAG_SEPARATOR + this.image.getTag())
                .withHostname(this.hostname)
                .withMounts(this.mounts)
                .withEnv(this.envs);

        EndpointSpec endpointSpec = null;
        if (this.publishedPort != -1 || this.targetPort != -1) {
            endpointSpec = new EndpointSpec()
                    .withMode(EndpointResolutionMode.VIP)
                    .withPorts(Collections.singletonList(new PortConfig()
                            .withProtocol(PortConfigProtocol.TCP)
                            .withPublishedPort(this.publishedPort)
                            .withTargetPort(this.targetPort)
                    ));
        }

        final ResourceRequirements resourceRequirements = new ResourceRequirements()
                .withLimits(new ResourceSpecs()
                        .withNanoCPUs(1500000000L));

        final TaskSpec taskSpec = new TaskSpec()
                .withContainerSpec(containerSpec)
                .withResources(resourceRequirements)
                .withNetworks(Collections.singletonList(new NetworkAttachmentConfig().withTarget(this.network.getName())));

        return new ServiceSpec()
                .withName(this.name)
                .withTaskTemplate(taskSpec)
                .withEndpointSpec(endpointSpec)
                .withLabels(this.labels);
    }

}
