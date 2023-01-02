package fr.hyriode.hyggdrasil.config;

import fr.hyriode.hyggdrasil.config.nested.*;
import fr.hyriode.hyggdrasil.util.YamlLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 13/05/2022 at 18:31
 */
public class HyggConfig {

    public static final Path CONFIG_FILE = Paths.get("config.yml");

    private RedisConfig redis;
    private DockerConfig docker;
    private ProxiesConfig proxies;
    private ServersConfig servers;
    private LimbosConfig limbos;
    private AzureConfig azure;

    public HyggConfig(RedisConfig redis, DockerConfig docker, ProxiesConfig proxies, ServersConfig servers, LimbosConfig limbos, AzureConfig azure) {
        this.redis = redis;
        this.docker = docker;
        this.proxies = proxies;
        this.servers = servers;
        this.limbos = limbos;
        this.azure = azure;
    }

    private HyggConfig() {}

    public RedisConfig getRedis() {
        return this.redis;
    }

    public DockerConfig getDocker() {
        return this.docker;
    }

    public ServersConfig getServers() {
        return this.servers;
    }

    public ProxiesConfig getProxies() {
        return this.proxies;
    }

    public LimbosConfig getLimbos() {
        return this.limbos;
    }

    public AzureConfig getAzure() {
        return this.azure;
    }

    public static HyggConfig load() {
        System.out.println("Loading configuration...");

        if (Files.exists(CONFIG_FILE)) {
            return YamlLoader.load(CONFIG_FILE, HyggConfig.class);
        } else {
            final HyggConfig config = new HyggConfig(new RedisConfig(), new DockerConfig(), new ProxiesConfig(), new ServersConfig(), new LimbosConfig(), new AzureConfig());

            YamlLoader.save(CONFIG_FILE, config);

            System.err.println("Please fill configuration file before continue!");
            System.exit(0);

            return config;
        }
    }

}
