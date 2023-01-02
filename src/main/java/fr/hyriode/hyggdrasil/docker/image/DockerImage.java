package fr.hyriode.hyggdrasil.docker.image;

import com.github.dockerjava.api.model.AuthConfig;

public class DockerImage {

    public static final String DOCKER_IMAGE_TAG_SEPARATOR = ":";

    private String name;
    private String tag;

    private Auth auth;

    private DockerImage() {}

    public DockerImage(String name, String tag, Auth auth) {
        this.name = name;
        this.tag = tag;
        this.auth = auth;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Auth getAuth() {
        return this.auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Auth {

        private String registry;
        private String username;
        private String password;

        private Auth() {}

        public Auth(String registry, String username, String password) {
            this.registry = registry;
            this.username = username;
            this.password = password;
        }

        public String getRegistry() {
            return this.registry;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        public AuthConfig asDocker() {
            return new AuthConfig()
                    .withRegistryAddress(this.registry)
                    .withUsername(this.username)
                    .withPassword(this.password);
        }

    }

}
