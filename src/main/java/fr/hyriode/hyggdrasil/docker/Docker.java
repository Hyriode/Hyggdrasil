package fr.hyriode.hyggdrasil.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.docker.image.DockerImageManager;
import fr.hyriode.hyggdrasil.docker.network.DockerNetworkManager;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;
import fr.hyriode.hyggdrasil.util.References;

public class Docker {

    /** Manager */
    private final DockerImageManager imageManager;
    private final DockerNetworkManager networkManager;
    private final DockerSwarm swarm;

    /** Config and Client */
    private final DockerClientConfig config;
    private final ApacheDockerHttpClient httpClient;
    private final DockerClient dockerClient;

    public Docker(Hyggdrasil hyggdrasil) {
        final String url = DockerUrl.get().getUrl();

        this.config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(url).build();
        this.httpClient = new ApacheDockerHttpClient.Builder().dockerHost(this.config.getDockerHost()).sslConfig(this.config.getSSLConfig()).build();
        this.dockerClient = DockerClientImpl.getInstance(this.config, this.httpClient);

        System.out.println(References.NAME + " is now connected to Docker (url: " + url + ").");

        this.imageManager = new DockerImageManager(hyggdrasil, this);
        this.networkManager = new DockerNetworkManager(this);
        this.swarm = new DockerSwarm(this);
    }

    public DockerClientConfig getConfig() {
        return this.config;
    }

    public ApacheDockerHttpClient getHttpClient() {
        return this.httpClient;
    }

    public DockerClient getDockerClient() {
        return this.dockerClient;
    }

    public DockerImageManager getImageManager() {
        return this.imageManager;
    }

    public DockerNetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public DockerSwarm getSwarm() {
        return this.swarm;
    }

}
