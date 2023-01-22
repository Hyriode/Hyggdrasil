package fr.hyriode.hyggdrasil.redis;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.config.nested.RedisConfig;
import fr.hyriode.hyggdrasil.util.References;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 15:52
 */
public class HyggRedis {

    private JedisPool jedisPool;

    private Thread reconnectTask;

    private boolean connected;

    private final RedisConfig config;

    public HyggRedis(RedisConfig config) {
        this.config = config;
    }

    public boolean connect() {
        final String hostname = this.config.getHostname();
        final short port = this.config.getPort();
        final String password = this.config.getPassword();
        final int timeout = 2000;
        final JedisPoolConfig config = new JedisPoolConfig();

        config.setJmxEnabled(false);
        config.setMaxTotal(-1);
        config.setMaxIdle(0);

        if (password != null && !password.isEmpty()) {
            this.jedisPool = new JedisPool(config, hostname, port, timeout, password);
        } else {
            this.jedisPool = new JedisPool(config, hostname, port, timeout);
        }

        try {
            this.getJedis().close();

            this.connected = true;

            System.out.println(References.NAME + " is now connected with Redis database.");

            this.startReconnectTask();
            return true;
        } catch (Exception e) {
            Hyggdrasil.log(Level.SEVERE, "Couldn't connect to Redis database !");
            return false;
        }
    }

    private void startReconnectTask() {
        this.reconnectTask = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            while (!Thread.interrupted()) {
                this.reconnect();

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        this.reconnectTask.start();
    }

    private void reconnect() {
        try {
            this.getJedis().close();
        } catch (Exception e) {
            Hyggdrasil.log(Level.SEVERE, "Encountered exception in Redis reconnection task. Error:" + e.getMessage());
            Hyggdrasil.log(Level.SEVERE, "Error in Redis database connection ! Trying to reconnect...");

            this.connected = false;

            this.connect();
        }
    }

    public void disconnect() {
        System.out.println("Disconnecting " + References.NAME + " from Redis database...");

        if (this.reconnectTask != null && this.reconnectTask.isAlive()) {
            this.reconnectTask.interrupt();
        }

        this.connected = false;

        this.jedisPool.close();
    }

    public JedisPool getJedisPool() {
        return this.jedisPool;
    }

    public Jedis getJedis() {
        return this.jedisPool.getResource();
    }

    public boolean isConnected() {
        return this.connected;
    }

}
