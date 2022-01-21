package fr.hyriode.hyggdrasil;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.env.HyggApplication;
import fr.hyriode.hyggdrasil.api.protocol.env.HyggEnvironment;
import fr.hyriode.hyggdrasil.api.protocol.env.HyggKeys;
import fr.hyriode.hyggdrasil.api.protocol.signature.HyggSignatureAlgorithm;
import fr.hyriode.hyggdrasil.configuration.HyggConfiguration;
import fr.hyriode.hyggdrasil.docker.Docker;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.redis.HyggRedis;
import fr.hyriode.hyggdrasil.server.HyggServerManager;
import fr.hyriode.hyggdrasil.util.References;
import fr.hyriode.hyggdrasil.util.logger.HyggLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 10:22
 */
public class Hyggdrasil {

    private HyggConfiguration configuration;
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

        this.configuration = HyggConfiguration.load();
        this.redis = new HyggRedis(this.configuration.getRedisConfiguration());

        if (!this.redis.connect()) {
            System.exit(-1);
        }

        this.keys = this.loadKeys();
        this.environment = new HyggEnvironment(new HyggApplication(HyggApplication.Type.HYDRA, "hydra"), this.redis.getCredentials(), this.keys);
        this.api = new HyggdrasilAPI.Builder()
                .withJedisPool(this.redis.getJedisPool())
                .withEnvironment(this.environment)
                .withLogger(logger)
                .build();
        this.api.start();
        this.docker = new Docker();
        this.proxyManager = new HyggProxyManager(this);
        this.serverManager = new HyggServerManager(this);
        this.running = true;

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        this.proxyManager.startProxy();

        this.api.getScheduler().schedule(() -> {
            System.out.println("Starting servers...");

            final String[] types = new String[] {"lobby", "bw", "rtf", "nexus", "therunner"};
            for (int i = 0; i < 5; i++) {
                this.serverManager.startServer(types[i]);
            }
        }, 15, TimeUnit.SECONDS);
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

    private HyggKeys loadKeys() {
        final HyggSignatureAlgorithm algorithm = HyggSignatureAlgorithm.RS256;

        PrivateKey privateKey = null;
        PublicKey publicKey = null;
        if (Files.exists(References.PRIVATE_KEY_FILE)) {
            try {
                final KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getFamilyName());

                privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Files.readAllBytes(References.PRIVATE_KEY_FILE)));

                System.out.println("Private key read from its file.");

                final RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey) privateKey;
                final RSAPublicKeySpec keySpec = new RSAPublicKeySpec(rsaPrivateCrtKey.getModulus(), rsaPrivateCrtKey.getPublicExponent());

                publicKey = keyFactory.generatePublic(keySpec);

                System.out.println("Public key generated from the private one.");
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
                System.err.println("An error occurred while reading private key file! Deleting file...");

                try {
                    Files.delete(References.PRIVATE_KEY_FILE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                e.printStackTrace();
            }
        } else {
            System.out.println("Generating key pair...");

            try {
                final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm.getFamilyName());

                keyPairGenerator.initialize(algorithm.getMinimalKeyLength());

                final KeyPair keyPair = keyPairGenerator.generateKeyPair();

                privateKey = keyPair.getPrivate();
                publicKey = keyPair.getPublic();

                Files.write(References.PRIVATE_KEY_FILE, privateKey.getEncoded());
            } catch (NoSuchAlgorithmException e) {
                System.err.println("An error occurred while generating new key pair!");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("An error occurred while writing private key in file!");
                e.printStackTrace();
            }
        }

        return new HyggKeys(publicKey, privateKey);
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
