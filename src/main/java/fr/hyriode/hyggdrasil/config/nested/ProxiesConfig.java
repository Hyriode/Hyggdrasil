package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/05/2022 at 15:11
 */
public class ProxiesConfig {

    private final int maxProxies;
    private final int startingPort;
    private final String template;

    public ProxiesConfig(int maxProxies, int startingPort, String template) {
        this.maxProxies = maxProxies;
        this.startingPort = startingPort;
        this.template = template;
    }

    public ProxiesConfig() {
        this(1, 20000, "proxy");
    }

    public int getMaxProxies() {
        return this.maxProxies;
    }

    public int getStartingPort() {
        return this.startingPort;
    }

    public String getTemplate() {
        return this.template;
    }

}
