package fr.hyriode.hyggdrasil.limbo;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
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
public class HyggLimbosProcessor {

    private final Image limbosImage;
    private final Function<String, String> limboDirectory;

    private final KubernetesClient kubernetesClient;

    public HyggLimbosProcessor(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
        this.limbosImage = Hyggdrasil.getConfig().getLimbos().getImage();
        this.limboDirectory = limbo -> Paths.get(Hyggdrasil.getConfig().getKubernetes().getRootDirectory(), "limbos", limbo).toAbsolutePath().toString();
    }

    public void startLimbo(HyggLimbo limbo) {
        final String limboName = limbo.getName();
        final Map<String, String> envs = new HyggEnv(new HyggApplication(HyggApplication.Type.LIMBO, limboName, System.currentTimeMillis())).createEnvironmentVariables();

        final Pod pod = new PodBuilder()
                .withNewMetadata()
                    .withName(limboName)
                    .addToLabels("app", limboName)
                    .addToLabels("type", "limbos")
                .endMetadata()
                .withNewSpec()
                    .withRestartPolicy("Never")
                    .addNewContainer()
                        .withName(limboName)
                        .withImage(this.limbosImage.getName())
                        .withEnv(envs.entrySet().stream().map(entry -> new EnvVarBuilder()
                                .withName(entry.getKey())
                                .withValue(entry.getValue())
                                .build()).collect(Collectors.toList()))
                        .withStdin(true).withTty(true)
                        .addNewPort()
                            .withProtocol("TCP")
                            .withContainerPort(25565)
                        .endPort()
                        .addNewVolumeMount()
                            .withName(limboName + "-volume")
                            .withMountPath("/server")
                        .endVolumeMount()
                        .withImagePullPolicy("Always")
                    .endContainer()
                    .addNewImagePullSecret()
                        .withName(this.limbosImage.getImagePullSecret())
                    .endImagePullSecret()
                    .addNewVolume()
                        .withName(limboName + "-volume")
                        .withNewHostPath()
                            .withPath(this.limboDirectory.apply(limboName))
                        .endHostPath()
                    .endVolume()
                .endSpec()
                .build();

        final Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(limboName)
                    .addToLabels("type", "limbos")
                .endMetadata()
                .withNewSpec()
                    .addNewPort()
                        .withPort(25565)
                        .withNewTargetPort(25565)
                    .endPort()
                    .addToSelector("app", limboName)
                .endSpec()
                .build();

        this.kubernetesClient.pods().resource(pod).create();
        this.kubernetesClient.services().resource(service).create();
    }

    public void stopLimbo(String limboName) {
        try {
            this.kubernetesClient.pods().withName(limboName).delete();
            this.kubernetesClient.services().withName(limboName).delete();
        } catch (Exception e) {
            System.err.println("Couldn't delete '" + limboName + "' from K8s. Error: " + e.getMessage());
        }
    }

}
