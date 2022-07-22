package fr.hyriode.hyggdrasil.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.hyriode.hyggdrasil.config.nested.HyggDockerConfig;
import fr.hyriode.hyggdrasil.config.nested.HyggProxiesConfig;
import fr.hyriode.hyggdrasil.config.nested.HyggRedisConfig;
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

    private final HyggRedisConfig redis;
    private final HyggDockerConfig docker;
    private final HyggProxiesConfig proxies;

    public HyggConfig(HyggRedisConfig redis, HyggDockerConfig docker, HyggProxiesConfig proxies) {
        this.redis = redis;
        this.docker = docker;
        this.proxies = proxies;
    }

    public HyggRedisConfig getRedis() {
        return this.redis;
    }

    public HyggDockerConfig getDocker() {
        return this.docker;
    }

    public HyggProxiesConfig getProxies() {
        return this.proxies;
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
            final HyggConfig config = new HyggConfig(new HyggRedisConfig(), new HyggDockerConfig(), new HyggProxiesConfig());

            IOUtil.save(CONFIG_FILE, gson.toJson(config));

            System.err.println("Please fill configuration file before continue!");
            System.exit(0);

            return config;
        }
    }

}
