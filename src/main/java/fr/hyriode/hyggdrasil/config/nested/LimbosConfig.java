package fr.hyriode.hyggdrasil.config.nested;

/**
 * Created by AstFaster
 * on 31/12/2022 at 18:00
 */
public class LimbosConfig {

    private final String image;
    private final String template;

    public LimbosConfig(String image, String template) {
        this.image = image;
        this.template = template;
    }

    public LimbosConfig() {
        this("limbo", "limbo");
    }

    public String getImage() {
        return this.image;
    }

    public String getTemplate() {
        return this.template;
    }

}
