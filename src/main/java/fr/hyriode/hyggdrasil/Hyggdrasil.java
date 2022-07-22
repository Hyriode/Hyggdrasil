package fr.hyriode.hyggdrasil;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggEnvironment;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggKeys;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.common.HeartbeatsCheck;
import fr.hyriode.hyggdrasil.common.HyriodeNetwork;
import fr.hyriode.hyggdrasil.config.HyggConfig;
import fr.hyriode.hyggdrasil.docker.Docker;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.receiver.HyggProxiesReceiver;
import fr.hyriode.hyggdrasil.receiver.HyggQueryReceiver;
import fr.hyriode.hyggdrasil.receiver.HyggServersReceiver;
import fr.hyriode.hyggdrasil.redis.HyggRedis;
import fr.hyriode.hyggdrasil.server.HyggServerManager;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;
import fr.hyriode.hyggdrasil.util.key.HyggKeyLoader;
import fr.hyriode.hyggdrasil.util.logger.HyggLogger;

import java.util.List;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 10:22
 */
public class Hyggdrasil {

    private static HyggConfig config;
    private HyggRedis redis;
    private HyggKeys keys;
    private HyggEnvironment environment;
    private HyggdrasilAPI api;

    private Docker docker;

    private HyggProxyManager proxyManager;
    private HyggServerManager serverManager;

    private boolean running;

    private static HyggLogger logger;

    public void start() {
        HyggLogger.printHeaderMessage();

        this.setupLogger();

        config = HyggConfig.load();
        this.redis = new HyggRedis(config.getRedis());

        if (!this.redis.connect()) {
            System.exit(-1);
        }

        this.keys = HyggKeyLoader.loadKeys();
        this.environment = new HyggEnvironment(new HyggApplication(HyggApplication.Type.HYGGDRASIL, "hyggdrasil", System.currentTimeMillis()), this.keys, null);
        this.api = new HyggdrasilAPI.Builder()
                .withJedisPool(this.redis.getJedisPool())
                .withEnvironment(this.environment)
                .withLogger(logger)
                .build();
        this.api.start();
        this.docker = new Docker();
        this.docker.getNetworkManager().registerNetwork(new HyriodeNetwork());
        this.proxyManager = new HyggProxyManager(this);
        this.serverManager = new HyggServerManager(this);
        this.running = true;

        new HeartbeatsCheck(this);

        this.registerReceivers();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void setupLogger() {
        IOUtil.createDirectory(References.LOG_FOLDER);

        logger = new HyggLogger(References.NAME, References.LOG_FILE.toString());
    }

    private void registerReceivers() {
        System.out.println("Registering receivers...");

        final HyggPacketProcessor processor = this.api.getPacketProcessor();

        processor.registerReceiver(HyggChannel.SERVERS, new HyggServersReceiver(this));
        processor.registerReceiver(HyggChannel.PROXIES, new HyggProxiesReceiver(this));
        processor.registerReceiver(HyggChannel.QUERY, new HyggQueryReceiver(this));
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

    public List<String> createEnvsForClient(HyggApplication application, HyggData data) {
        return new HyggEnvironment(application, new HyggKeys(this.environment.getKeys().getPublic(), null), data).createEnvironmentVariables();
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

    public static HyggConfig getConfig() {
        return config;
    }

    public HyggRedis getRedis() {
        return this.redis;
    }

    public HyggKeys getKeys() {
        return this.keys;
    }

    public HyggEnvironment getEnvironment() {
        return this.environment;
    }

    public HyggdrasilAPI getAPI() {
        return this.api;
    }

    public Docker getDocker() {
        return this.docker;
    }

    public HyggProxyManager getProxyManager() {
        return this.proxyManager;
    }

    public HyggServerManager getServerManager() {
        return this.serverManager;
    }

    public boolean isRunning() {
        return this.running;
    }

}
