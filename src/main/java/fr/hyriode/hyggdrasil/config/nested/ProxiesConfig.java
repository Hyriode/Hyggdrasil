package fr.hyriode.hyggdrasil.config.nested;

import fr.hyriode.hyggdrasil.config.nested.object.Image;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/05/2022 at 15:11
 */
public class ProxiesConfig {

    private final int maxProxies;
    private final int startingPort;
    private final String template;
    private final Image image;

    public ProxiesConfig(int maxProxies, int startingPort, String template, Image image) {
        this.maxProxies = maxProxies;
        this.startingPort = startingPort;
        this.template = template;
        this.image = image;
    }

    public ProxiesConfig() {
        this(1, 20000, "proxy", new Image());
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

    public Image getImage() {
        return this.image;
    }

}
