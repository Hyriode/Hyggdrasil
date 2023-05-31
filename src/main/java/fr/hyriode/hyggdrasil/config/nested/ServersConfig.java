package fr.hyriode.hyggdrasil.config.nested;

import fr.hyriode.hyggdrasil.config.nested.object.Image;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/05/2022 at 15:11
 */
public class ServersConfig {

    private final Image image;

    public ServersConfig(Image image) {
        this.image = image;
    }

    public ServersConfig() {
        this(new Image());
    }

    public Image getImage() {
        return this.image;
    }

}
