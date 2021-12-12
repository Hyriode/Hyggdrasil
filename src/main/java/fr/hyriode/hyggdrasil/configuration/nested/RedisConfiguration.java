package fr.hyriode.hyggdrasil.configuration.nested;

public record RedisConfiguration(String hostName, int port, String password) {

    public String getHostName() {
        return this.hostName;
    }

    public int getPort() {
        return this.port;
    }

    public String getPassword() {
        return this.password;
    }

    public static RedisConfiguration load() {
        return new RedisConfiguration(System.getenv("REDIS_HOST"), Integer.parseInt(System.getenv("REDIS_PORT")), System.getenv("REDIS_PASS"));
    }

}
