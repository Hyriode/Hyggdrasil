package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggEnv;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
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
public class HyggProxiesProcessor {

    private final Image proxiesImage;
    private final Function<String, String> proxyDirectory;

    private final KubernetesClient kubernetesClient;

    public HyggProxiesProcessor(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
        this.proxiesImage = Hyggdrasil.getConfig().getProxies().getImage();
        this.proxyDirectory = proxy -> Paths.get(Hyggdrasil.getConfig().getKubernetes().getRootDirectory(), "proxies", proxy).toAbsolutePath().toString();
    }

    public void startProxy(HyggProxy proxy) {
        final String proxyName = proxy.getName();
        final Map<String, String> envs = new HyggEnv(new HyggApplication(HyggApplication.Type.PROXY, proxyName, System.currentTimeMillis())).createEnvironmentVariables();

        final Pod pod = new PodBuilder()
                .withNewMetadata()
                    .withName(proxyName)
                    .addToLabels("app", proxyName)
                    .addToLabels("type", "proxies")
                .endMetadata()
                .withNewSpec()
                    .withRestartPolicy("Never")
                    .addNewContainer()
                        .withName(proxyName)
                        .withImage(this.proxiesImage.getName())
                        .withEnv(envs.entrySet().stream().map(entry -> new EnvVarBuilder()
                                .withName(entry.getKey())
                                .withValue(entry.getValue())
                                .build()).collect(Collectors.toList()))
                        .withStdin(true).withTty(true)
                        .addNewPort()
                            .withProtocol("TCP")
                            .withContainerPort(25577)
                        .endPort()
                        .addNewVolumeMount()
                            .withName(proxyName + "-volume")
                            .withMountPath("/server")
                        .endVolumeMount()
                        .withImagePullPolicy("Always")
                    .endContainer()
                    .addNewImagePullSecret()
                        .withName(this.proxiesImage.getImagePullSecret())
                    .endImagePullSecret()
                    .addNewVolume()
                        .withName(proxyName + "-volume")
                        .withNewHostPath()
                            .withPath(this.proxyDirectory.apply(proxyName))
                        .endHostPath()
                    .endVolume()
                .endSpec()
                .build();

        final Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(proxyName)
                    .addToLabels("type", "proxies")
                .endMetadata()
                .withNewSpec()
                    .addToSelector("app", proxyName)
                    .withType("NodePort")
                    .addNewPort()
                        .withPort(25577)
                        .withNewTargetPort(25577)
                        .withNodePort(proxy.getPort())
                    .endPort()
                .endSpec()
                .build();

        this.kubernetesClient.pods().resource(pod).create();
        this.kubernetesClient.services().resource(service).create();
    }

    public void stopProxy(String proxyName) {
        try {
            this.kubernetesClient.pods().withName(proxyName).delete();
            this.kubernetesClient.services().withName(proxyName).delete();
        } catch (Exception e) {
            System.err.println("Couldn't delete '" + proxyName + "' from K8s. Error: " + e.getMessage());
        }
    }

}
