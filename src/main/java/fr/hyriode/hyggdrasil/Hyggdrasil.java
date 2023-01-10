package fr.hyriode.hyggdrasil;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggEnv;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.config.HyggConfig;
import fr.hyriode.hyggdrasil.docker.Docker;
import fr.hyriode.hyggdrasil.heartbeat.HeartbeatsCheck;
import fr.hyriode.hyggdrasil.limbo.HyggLimboManager;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.receiver.HyggLimbosReceiver;
import fr.hyriode.hyggdrasil.receiver.HyggProxiesReceiver;
import fr.hyriode.hyggdrasil.receiver.HyggQueryReceiver;
import fr.hyriode.hyggdrasil.receiver.HyggServersReceiver;
import fr.hyriode.hyggdrasil.redis.HyggRedis;
import fr.hyriode.hyggdrasil.server.HyggServerManager;
import fr.hyriode.hyggdrasil.template.HyggTemplateManager;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;
import fr.hyriode.hyggdrasil.util.logger.ColoredLogger;

import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 10:22
 */
public class Hyggdrasil {

    private static HyggConfig config;

    private HyggRedis redis;
    private HyggEnv environment;
    private HyggdrasilAPI api;

    private Docker docker;

    private HyggTemplateManager templateManager;
    private HyggProxyManager proxyManager;
    private HyggServerManager serverManager;
    private HyggLimboManager limboManager;

    private boolean running;

    private static ColoredLogger logger;

    public void start() {
        ColoredLogger.printHeaderMessage();

        this.setupLogger();

        config = HyggConfig.load();
        this.running = true;
        this.redis = new HyggRedis(config.getRedis());

        if (!this.redis.connect()) {
            System.exit(-1);
        }

        this.environment = new HyggEnv(new HyggApplication(HyggApplication.Type.HYGGDRASIL, "hyggdrasil", System.currentTimeMillis()));
        this.api = new HyggdrasilAPI.Builder()
                .withJedisPool(this.redis.getJedisPool())
                .withEnvironment(this.environment)
                .withLogger(logger)
                .build();
        this.api.start();
        this.docker = new Docker(this);
        this.docker.getNetworkManager().registerNetwork(References.NETWORK.get());
        this.templateManager = new HyggTemplateManager(this);
        this.proxyManager = new HyggProxyManager(this);
        this.serverManager = new HyggServerManager(this);
        this.limboManager = new HyggLimboManager(this);

        new HeartbeatsCheck(this);

        this.registerReceivers();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void setupLogger() {
        IOUtil.createDirectory(References.LOG_FOLDER);

        logger = new ColoredLogger(References.NAME, References.LOG_FILE);
    }

    private void registerReceivers() {
        System.out.println("Registering receivers...");

        final HyggPacketProcessor processor = this.api.getPacketProcessor();

        processor.registerReceiver(HyggChannel.SERVERS, new HyggServersReceiver(this));
        processor.registerReceiver(HyggChannel.PROXIES, new HyggProxiesReceiver(this));
        processor.registerReceiver(HyggChannel.LIMBOS, new HyggLimbosReceiver(this));
        processor.registerReceiver(HyggChannel.QUERY, new HyggQueryReceiver(this, limboManager));
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

    public static ColoredLogger getLogger() {
        return logger;
    }

    public static HyggConfig getConfig() {
        return config;
    }

    public HyggRedis getRedis() {
        return this.redis;
    }

    public HyggEnv getEnvironment() {
        return this.environment;
    }

    public HyggdrasilAPI getAPI() {
        return this.api;
    }

    public Docker getDocker() {
        return this.docker;
    }

    public HyggTemplateManager getTemplateManager() {
        return this.templateManager;
    }

    public HyggProxyManager getProxyManager() {
        return this.proxyManager;
    }

    public HyggServerManager getServerManager() {
        return this.serverManager;
    }

    public HyggLimboManager getLimboManager() {
        return this.limboManager;
    }

    public boolean isRunning() {
        return this.running;
    }

}
