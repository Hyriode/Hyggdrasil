package fr.hyriode.hyggdrasil.docker.image;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.command.BuildImageCmdImpl;
import com.github.dockerjava.core.exec.BuildImageCmdExec;
import com.github.dockerjava.core.exec.CreateImageCmdExec;
import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.docker.Docker;

import java.io.Closeable;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void buildImage(File dockerFile, String tag) {
        final BuildImageResultCallback callback = new BuildImageResultCallback() {
            @Override
            public void onComplete() {
                System.out.println("Image '" + tag + "' built successfully.");
                super.onComplete();
            }
        };

        this.dockerClient.buildImageCmd(dockerFile).withTags(Stream.of(tag).collect(Collectors.toSet())).exec(callback).awaitImageId();
    }

    public void removeImage(String imageId) {
        this.dockerClient.removeImageCmd(imageId).exec();
    }

    public List<Image> listImages() {
        return this.dockerClient.listImagesCmd().exec();
    }

}
