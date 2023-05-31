package fr.hyriode.hyggdrasil.config.nested;

import fr.hyriode.hyggdrasil.config.nested.object.Image;

/**
 * Created by AstFaster
 * on 31/12/2022 at 18:00
 */
public class LimbosConfig {

    private final Image image;
    private final String template;

    public LimbosConfig(Image image, String template) {
        this.image = image;
        this.template = template;
    }

    public LimbosConfig() {
        this(new Image(), "limbo");
    }

    public Image getImage() {
        return this.image;
    }

    public String getTemplate() {
        return this.template;
    }

}
