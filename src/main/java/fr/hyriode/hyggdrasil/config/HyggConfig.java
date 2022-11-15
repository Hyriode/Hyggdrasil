package fr.hyriode.hyggdrasil.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.hyriode.hyggdrasil.config.nested.AzureConfig;
import fr.hyriode.hyggdrasil.config.nested.DockerConfig;
import fr.hyriode.hyggdrasil.config.nested.ProxiesConfig;
import fr.hyriode.hyggdrasil.config.nested.RedisConfig;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 13/05/2022 at 18:31
 */
public class HyggConfig {

    public static final Path CONFIG_FILE = Paths.get(References.DATA_FOLDER.toString(), "config.json");

    private final RedisConfig redis;
    private final DockerConfig docker;
    private final ProxiesConfig proxies;
    private final AzureConfig azure;

    public HyggConfig(RedisConfig redis, DockerConfig docker, ProxiesConfig proxies, AzureConfig azure) {
        this.redis = redis;
        this.docker = docker;
        this.proxies = proxies;
        this.azure = azure;
    }

    public RedisConfig getRedis() {
        return this.redis;
    }

    public DockerConfig getDocker() {
        return this.docker;
    }

    public ProxiesConfig getProxies() {
        return this.proxies;
    }

    public AzureConfig getAzure() {
        return this.azure;
    }

    public static HyggConfig load() {
        System.out.println("Loading configuration...");

        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        final String json = IOUtil.loadFile(CONFIG_FILE);

        if (!json.equals("")) {
            return gson.fromJson(json, HyggConfig.class);
        } else {
            final HyggConfig config = new HyggConfig(new RedisConfig(), new DockerConfig(), new ProxiesConfig(), new AzureConfig("", "", null));

            IOUtil.save(CONFIG_FILE, gson.toJson(config));

            System.err.println("Please fill configuration file before continue!");
            System.exit(0);

            return config;
        }
    }

}
