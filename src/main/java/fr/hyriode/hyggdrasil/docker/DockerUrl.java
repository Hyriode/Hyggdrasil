package fr.hyriode.hyggdrasil.docker;

public enum DockerUrl {

    UNIX("unix:///var/run/docker.sock"),
    WINDOWS("tcp://localhost:2375");

    private final String url;

    DockerUrl(String url) {
        this.url = url;
    }

    public static DockerUrl get() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? WINDOWS : UNIX;
    }

    public String getUrl() {
        return this.url;
    }

}
