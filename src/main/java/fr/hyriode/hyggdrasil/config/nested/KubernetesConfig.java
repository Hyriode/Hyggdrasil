package fr.hyriode.hyggdrasil.config.nested;

/**
 * Created by AstFaster
 * on 26/05/2023 at 09:27
 */
public class KubernetesConfig {

    private final String namespace;
    private final String rootDirectory;
    private final String resourcesPrefix;

    public KubernetesConfig(String namespace, String rootDirectory, String resourcesPrefix) {
        this.namespace = namespace;
        this.rootDirectory = rootDirectory;
        this.resourcesPrefix = resourcesPrefix;
    }

    public KubernetesConfig() {
        this("dev", "/home/", "");
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getRootDirectory() {
        return this.rootDirectory;
    }

    public String getResourcesPrefix() {
        return this.resourcesPrefix;
    }

}
