package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/05/2022 at 15:11
 */
public class ProxiesConfig {

    private final int maxProxies;
    private final String template;
    private final String image;

    public ProxiesConfig(int maxProxies, String template, String image) {
        this.maxProxies = maxProxies;
        this.template = template;
        this.image = image;
    }

    public ProxiesConfig() {
        this(1, "proxy", "proxy");
    }

    public int getMaxProxies() {
        return this.maxProxies;
    }

    public String getTemplate() {
        return this.template;
    }

    public String getImage() {
        return this.image;
    }

}
