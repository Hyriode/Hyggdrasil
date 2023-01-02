package fr.hyriode.hyggdrasil.config.nested;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/05/2022 at 15:11
 */
public class ServersConfig {

    private final String image;

    public ServersConfig(String image) {
        this.image = image;
    }

    public ServersConfig() {
        this("server");
    }

    public String getImage() {
        return this.image;
    }

}
