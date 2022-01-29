package fr.hyriode.hyggdrasil.docker.image;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Image;
import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.docker.Docker;

import java.io.Closeable;
import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class DockerImageManager {

    private final DockerClient dockerClient;

    public DockerImageManager(Docker docker) {
        this.dockerClient = docker.getDockerClient();
    }

    public void pullImage(DockerImage image) {
        try {
            final PullImageResultCallback callback = new PullImageResultCallback() {
                @Override
                public void onStart(Closeable stream) {
                    super.onStart(stream);
                    System.out.println("Pulling '" + image.getName() + "' image with '" + image.getTag() + "' tag...");
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                    System.out.println("Successfully pulled '" + image.getName() + "' image with '" + image.getTag() + "' tag.");
                }
            };

            this.dockerClient.pullImageCmd(image.getName()).withTag(image.getTag()).exec(callback).awaitCompletion();
        } catch (InterruptedException e) {
            Hyggdrasil.log(Level.SEVERE, "Couldn't pulled '" + image.getName() + "' image with '" + image.getTag() + "' tag !");
        }
    }

    public void buildImage(File dockerFile) {
        try {
            final BuildImageResultCallback callback = new BuildImageResultCallback() {
                @Override
                public void onComplete() {
                    System.out.println("Successfully build an image from '" + dockerFile.getName() + "' docker file.");
                }
            };

            this.dockerClient.buildImageCmd(dockerFile).exec(callback).awaitCompletion();
        } catch (InterruptedException e) {
            Hyggdrasil.log(Level.SEVERE, "Couldn't build an image from '" + dockerFile.getName() + " docker file !");
        }
    }

    public void removeImage(String imageId) {
        this.dockerClient.removeImageCmd(imageId).exec();
    }

    public List<Image> listImages() {
        return this.dockerClient.listImagesCmd().exec();
    }

}
