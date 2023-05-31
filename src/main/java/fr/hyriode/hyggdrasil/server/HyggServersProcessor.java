package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggEnv;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerCreationInfo;
import fr.hyriode.hyggdrasil.config.nested.object.Image;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by AstFaster
 * on 25/05/2023 at 09:39
 */
public class HyggServersProcessor {

    private final Image serversImage;
    private final Function<String, String> serverDirectory;

    private final KubernetesClient kubernetesClient;

    public HyggServersProcessor(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
        this.serversImage = Hyggdrasil.getConfig().getServers().getImage();
        this.serverDirectory = server -> Paths.get(Hyggdrasil.getConfig().getKubernetes().getRootDirectory(), "servers", server).toAbsolutePath().toString();
    }

    public void startServer(HyggServer server, HyggServerCreationInfo info) {
        final String serverName = server.getName();
        final Map<String, String> envs = new HyggEnv(new HyggApplication(HyggApplication.Type.SERVER, serverName, System.currentTimeMillis())).createEnvironmentVariables();

        if (info.getMaxMemory() != null) {
            envs.put("MAX_MEMORY", info.getMaxMemory());
        }

        if (info.getMinMemory() != null) {
            envs.put("INIT_MEMORY", info.getMinMemory());
        }

        final Pod pod = new PodBuilder()
                .withNewMetadata()
                    .withName(serverName)
                    .addToLabels("app", serverName)
                    .addToLabels("type", "servers")
                .endMetadata()
                .withNewSpec()
                    .withRestartPolicy("Never")
                    .addNewContainer()
                        .withName(serverName)
                        .withImage(this.serversImage.getName())
                        .withEnv(envs.entrySet().stream().map(entry -> new EnvVarBuilder()
                                .withName(entry.getKey())
                                .withValue(entry.getValue())
                                .build()).collect(Collectors.toList()))
                        .withStdin(true).withTty(true)
                        .addNewPort()
                            .withProtocol("TCP")
                            .withContainerPort(25565)
                        .endPort()
                        .withNewResources()
                            .addToLimits("cpu", new Quantity(String.valueOf(info.getCpus())))
                        .endResources()
                        .addNewVolumeMount()
                            .withName(serverName + "-volume")
                            .withMountPath("/data")
                        .endVolumeMount()
                        .withImagePullPolicy("Always")
                    .endContainer()
                    .addNewImagePullSecret()
                        .withName(this.serversImage.getImagePullSecret())
                    .endImagePullSecret()
                    .addNewVolume()
                        .withName(serverName + "-volume")
                        .withNewHostPath()
                            .withPath(this.serverDirectory.apply(serverName))
                        .endHostPath()
                    .endVolume()
                .endSpec()
                .build();

        final Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(serverName)
                    .addToLabels("type", "servers")
                .endMetadata()
                .withNewSpec()
                    .addNewPort()
                        .withPort(25565)
                        .withNewTargetPort(25565)
                    .endPort()
                    .addToSelector("app", serverName)
                .endSpec()
                .build();

        this.kubernetesClient.pods().resource(pod).create();
        this.kubernetesClient.services().resource(service).create();
    }

    public void stopServer(String serverName) {
        try {
            this.kubernetesClient.pods().withName(serverName).delete();
            this.kubernetesClient.services().withName(serverName).delete();
        } catch (Exception e) {
            System.err.println("Couldn't delete '" + serverName + "' from K8s. Error: " + e.getMessage());
        }
    }

}
