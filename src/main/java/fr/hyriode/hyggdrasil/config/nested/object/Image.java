package fr.hyriode.hyggdrasil.config.nested.object;

/**
 * Created by AstFaster
 * on 26/05/2023 at 09:42
 */
public class Image {

    private final String name;
    private final String imagePullSecret;

    public Image(String name, String imagePullSecret) {
        this.name = name;
        this.imagePullSecret = imagePullSecret;
    }

    public Image() {
        this("image:latest", "");
    }

    public String getName() {
        return this.name;
    }

    public String getImagePullSecret() {
        return this.imagePullSecret;
    }
}
