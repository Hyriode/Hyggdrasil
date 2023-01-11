package fr.hyriode.hyggdrasil.docker.image;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.PullImageResultCallback;
import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.docker.Docker;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;
import fr.hyriode.hyggdrasil.util.YamlLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DockerImageManager {

    private final Set<DockerImage> images = new HashSet<>();

    private final DockerClient dockerClient;

    public DockerImageManager(Hyggdrasil hyggdrasil, Docker docker) {
        this.dockerClient = docker.getDockerClient();

        this.loadImages();

        System.out.println("Pulling images...");

        this.pullImages(); // First, load them synchronously

        hyggdrasil.getAPI().getExecutorService().scheduleAtFixedRate(this::pullImages, 1, 1, TimeUnit.MINUTES);
    }

    private void loadImages() {
        IOUtil.createDirectory(References.IMAGES_FOLDER);

        try (final Stream<Path> stream = Files.list(References.IMAGES_FOLDER)) {
            stream.forEach(this::loadImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImage(Path path) {
        if (path.toString().endsWith(".yml") || path.toString().endsWith(".yaml")) {
            final DockerImage image = YamlLoader.load(path, DockerImage.class);

            if (image != null) {
                this.images.add(image);

                System.out.println("Loaded '" + image.getName() + ":" + image.getTag() + "' image.");
            }
        }
    }

    private void pullImages() {
        for (DockerImage image : this.images) {
            this.pullImage(image);
        }
    }

    public void pullImage(DockerImage image) {
        try {
            this.dockerClient.pullImageCmd(image.getName())
                    .withTag(image.getTag())
                    .withAuthConfig(image.getAuth().asDocker())
                    .exec(new PullImageResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            System.err.println("Couldn't pulled '" + image.getName() + "' image with '" + image.getTag() + "' tag !");
        }
    }

    public void buildImage(File dockerFile, String tag) {
        final BuildImageResultCallback callback = new BuildImageResultCallback() {
            @Override
            public void onComplete() {
                super.onComplete();

                System.out.println("Image '" + tag + "' built successfully.");
            }
        };

        this.dockerClient.buildImageCmd(dockerFile).withTags(Stream.of(tag).collect(Collectors.toSet())).exec(callback).awaitImageId();
    }

    public DockerImage getImage(String name, String tag) {
        for (DockerImage image : this.images) {
            if (image.getName().equals(name) && image.getTag().equals(tag)) {
                return image;
            }
        }
        return null;
    }

    public DockerImage getImage(String name) {
        final String[] split = name.split(":");

        return this.getImage(split[0], split.length == 2 ? split[1] : "latest");
    }

}
