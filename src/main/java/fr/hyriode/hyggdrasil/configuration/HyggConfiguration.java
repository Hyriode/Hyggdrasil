package fr.hyriode.hyggdrasil.configuration;

import fr.hyriode.hyggdrasil.configuration.nested.RedisConfiguration;

public class HyggConfiguration {

    private RedisConfiguration redisConfiguration;

    public HyggConfiguration(RedisConfiguration redisConfiguration) {
        this.redisConfiguration = redisConfiguration;
    }

    public RedisConfiguration getRedisConfiguration() {
        return this.redisConfiguration;
    }

    public void setRedisConfiguration(RedisConfiguration redisConfiguration) {
        this.redisConfiguration = redisConfiguration;
    }

    public static HyggConfiguration load() {
        System.out.println("Loading configuration from envs...");

        return new HyggConfiguration(RedisConfiguration.load());
    }

}
