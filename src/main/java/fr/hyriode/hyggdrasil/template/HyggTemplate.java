package fr.hyriode.hyggdrasil.template;

import java.util.Map;

/**
 * Created by AstFaster
 * on 30/10/2022 at 15:17
 */
public class HyggTemplate {

    private String name;
    private String maxMemory;
    private Map<String, File> files;

    private HyggTemplate() {}

    public HyggTemplate(String name, String maxMemory, Map<String, File> files) {
        this.name = name;
        this.maxMemory = maxMemory;
        this.files = files;
    }

    public String getName() {
        return this.name;
    }

    public String getMaxMemory() {
        return this.maxMemory;
    }

    public Map<String, File> getFiles() {
        return this.files;
    }

    public static class File {

        private String name;
        private String container;
        private String blob;
        private String destination;

        private File() {}

        public File(String name, String container, String blob, String destination) {
            this.name = name;
            this.container = container;
            this.blob = blob;
            this.destination = destination;
        }

        public String getName() {
            return this.name;
        }

        public String getContainer() {
            return this.container;
        }

        public String getBlob() {
            return this.blob;
        }

        public String getDestination() {
            return this.destination;
        }

    }

}
