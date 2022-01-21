package fr.hyriode.hyggdrasil.configuration.nested;

import fr.hyriode.hyggdrasil.util.References;

public record RedisConfiguration(String hostname, short port, String password) {

    public String getHostname() {
        return this.hostname;
    }

    public short getPort() {
        return this.port;
    }

    public String getPassword() {
        return this.password;
    }

    public static RedisConfiguration load() {
        return new RedisConfiguration(References.STACK_NAME + "_" + System.getenv("REDIS_HOST"), Short.parseShort(System.getenv("REDIS_PORT")), System.getenv("REDIS_PASS"));
    }

}
