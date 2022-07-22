package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/05/2022 at 15:11
 */
public class HyggProxiesConfig {

    private final int maxProxies;
    private final int minProxies;
    private final int startingPort;

    public HyggProxiesConfig(int maxProxies, int minProxies, int startingPort) {
        this.maxProxies = maxProxies;
        this.minProxies = minProxies;
        this.startingPort = startingPort;
    }

    public HyggProxiesConfig() {
        this(1, 1, 20000);
    }

    public int getMaxProxies() {
        return this.maxProxies;
    }

    public int getMinProxies() {
        return this.minProxies;
    }

    public int getStartingPort() {
        return this.startingPort;
    }

}
