package fr.hyriode.hyggdrasil.kubernetes;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

/**
 * Created by AstFaster
 * on 25/05/2023 at 09:37
 */
public class Kubernetes {

    private final KubernetesClient client;

    public Kubernetes() {
        this.client = new KubernetesClientBuilder()
                .withConfig(new ConfigBuilder()
                        .withNamespace(Hyggdrasil.getConfig().getKubernetes().getNamespace())
                        .build())
                .build();
    }

    public void shutdown() {
        this.client.close();
    }

    public KubernetesClient getClient() {
        return this.client;
    }

}
