package fr.hyriode.hyggdrasil;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggEnvironment;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggKeys;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;
import fr.hyriode.hyggdrasil.common.HyggHeartbeatsCheck;
import fr.hyriode.hyggdrasil.common.HyriodeNetwork;
import fr.hyriode.hyggdrasil.config.HyggConfig;
import fr.hyriode.hyggdrasil.docker.Docker;
import fr.hyriode.hyggdrasil.lobby.HyggLobbyBalancer;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.queue.HyggQueueManager;
import fr.hyriode.hyggdrasil.receiver.HyggProxiesReceiver;
import fr.hyriode.hyggdrasil.receiver.HyggQueryReceiver;
import fr.hyriode.hyggdrasil.receiver.HyggServersReceiver;
import fr.hyriode.hyggdrasil.redis.HyggRedis;
import fr.hyriode.hyggdrasil.rule.HyggRules;
import fr.hyriode.hyggdrasil.rule.HyggServerRule;
import fr.hyriode.hyggdrasil.server.HyggServerManager;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;
import fr.hyriode.hyggdrasil.util.key.HyggKeyLoader;
import fr.hyriode.hyggdrasil.util.logger.HyggLogger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
    private HyggRules rules;

    private Docker docker;

    private HyggProxyManager proxyManager;
    private HyggServerManager serverManager;
    private HyggQueueManager queueManager;
    private HyggLobbyBalancer lobbyBalancer;

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
        this.environment = new HyggEnvironment(new HyggApplication(HyggApplication.Type.HYGGDRASIL, "hyggdrasil", System.currentTimeMillis()), this.redis.getCredentials(), this.keys, null);
        this.api = new HyggdrasilAPI.Builder()
                .withJedisPool(this.redis.getJedisPool())
                .withEnvironment(this.environment)
                .withLogger(logger)
                .build();
        this.api.start();
        this.rules = HyggRules.load();
        this.docker = new Docker();
        this.docker.getNetworkManager().registerNetwork(new HyriodeNetwork());
        this.proxyManager = new HyggProxyManager(this);
        this.serverManager = new HyggServerManager(this);
        this.queueManager = new HyggQueueManager(this);
        this.lobbyBalancer = new HyggLobbyBalancer(this);
        this.running = true;

        this.initRules();

        new HyggHeartbeatsCheck(this);

        this.registerReceivers();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void setupLogger() {
        IOUtil.createDirectory(References.LOG_FOLDER);

        logger = new HyggLogger(References.NAME, References.LOG_FILE.toString());
    }

    private void initRules() {
        final int startingProxies = this.rules.getProxyRule().getMinProxies();

        System.out.println("Starting proxies from rules (" + startingProxies + ").");

        for (int i = 0; i < startingProxies; i++) {
            this.proxyManager.startProxy();
        }

        this.api.getScheduler().schedule(() -> {
            System.out.println("Starting servers from rules (" + this.rules.getServerRules().size() + ").");

            for (Map.Entry<String, HyggServerRule> entry : this.rules.getServerRules().entrySet()) {
                for (Map.Entry<String, Integer> minimum : entry.getValue().getMinimums().entrySet()) {
                    for (int i = 0; i < minimum.getValue(); i++) {
                        final HyggData data = new HyggData();

                        data.add(HyggServer.GAME_TYPE_KEY, minimum.getKey());

                        this.serverManager.startServer(entry.getKey(), new HyggServerOptions(), data, -1);
                    }
                }
            }
        }, 10, TimeUnit.SECONDS);
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

            this.queueManager.disable();

            this.api.stop(References.NAME + " shutdown called");
            this.redis.disconnect();

            this.running = false;

            System.out.println(References.NAME + " is now down. See you soon!");
        }
    }

    public List<String> createEnvsForClient(HyggApplication application, HyggData data) {
        return new HyggEnvironment(application, this.environment.getRedisCredentials(), new HyggKeys(this.environment.getKeys().getPublic(), null), data).createEnvironmentVariables();
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

    public HyggRules getRules() {
        return this.rules;
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

    public HyggQueueManager getQueueManager() {
        return this.queueManager;
    }

    public HyggLobbyBalancer getLobbyBalancer() {
        return this.lobbyBalancer;
    }

    public boolean isRunning() {
        return this.running;
    }

}
