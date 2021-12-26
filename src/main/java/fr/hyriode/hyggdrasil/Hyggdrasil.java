package fr.hyriode.hyggdrasil;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.configuration.HyggConfiguration;
import fr.hyriode.hyggdrasil.docker.Docker;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.redis.HyggRedis;
import fr.hyriode.hyggdrasil.util.References;
import fr.hyriode.hyggdrasil.util.logger.HyggLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 10:22
 */
public class Hyggdrasil {

    /** Configuration */
    private HyggConfiguration configuration;

    /** Redis */
    private HyggRedis redis;

    /** API */
    private HyggdrasilAPI api;

    /** Docker */
    private Docker docker;

    /** Proxy */
    private HyggProxyManager proxyManager;

    /** State */
    private boolean running;

    /** Logger */
    private static HyggLogger logger;

    public void start() {
        HyggLogger.printHeaderMessage();

        this.setupLogger();

        this.configuration = HyggConfiguration.load();
        this.redis = new HyggRedis(this.configuration.getRedisConfiguration());
        this.redis.connect();
        this.api = new HyggdrasilAPI.Builder()
                .withJedisPool(this.redis.getJedisPool())
                .withLogger(logger)
                .build();
        this.api.start();
        this.docker = new Docker();
        this.proxyManager = new HyggProxyManager(this);

        this.running = true;

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        this.proxyManager.startProxy();
    }

    private void setupLogger() {
        try {
            if (!Files.exists(References.LOG_FOLDER)) {
                Files.createDirectory(References.LOG_FOLDER);
            }

            logger = new HyggLogger(References.NAME, References.LOG_FILE.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if (this.running) {
            System.out.println("Stopping " + References.NAME + "...");

            this.api.stop(References.NAME + " shutdown called");
            this.redis.disconnect();

            this.running = false;

            System.out.println(References.NAME + " is now down. See you soon!");
        }
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static HyggLogger getLogger() {
        return logger;
    }

    public HyggConfiguration getConfiguration() {
        return this.configuration;
    }

    public HyggdrasilAPI getAPI() {
        return this.api;
    }

    public HyggRedis getRedis() {
        return this.redis;
    }

    public Docker getDocker() {
        return this.docker;
    }

    public HyggProxyManager getProxyManager() {
        return this.proxyManager;
    }

    public boolean isRunning() {
        return this.running;
    }

}
