package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 13/05/2022 at 18:31
 */
public class HyggRedisConfig {

    private final String hostname;
    private final short port;
    private final String password;

    public HyggRedisConfig(String hostname, short port, String password) {
        this.hostname = hostname;
        this.port = port;
        this.password = password;
    }

    public HyggRedisConfig() {
        this("localhost", (short) 6379, "");
    }

    public String getHostname() {
        return this.hostname;
    }

    public short getPort() {
        return this.port;
    }

    public String getPassword() {
        return this.password;
    }

}
