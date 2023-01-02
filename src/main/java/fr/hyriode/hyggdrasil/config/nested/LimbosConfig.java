package fr.hyriode.hyggdrasil.config.nested;

/**
 * Created by AstFaster
 * on 31/12/2022 at 18:00
 */
public class LimbosConfig {

    private final String image;

    public LimbosConfig(String image) {
        this.image = image;
    }

    public LimbosConfig() {
        this("limbo");
    }

    public String getImage() {
        return this.image;
    }

}
