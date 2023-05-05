package fr.hyriode.hyggdrasil.template;

import java.util.Map;

/**
 * Created by AstFaster
 * on 30/10/2022 at 15:17
 */
public class HyggTemplate {

    private String name;
    private Map<String, File> files;

    private HyggTemplate() {}

    public HyggTemplate(String name, Map<String, File> files) {
        this.name = name;
        this.files = files;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, File> getFiles() {
        return this.files;
    }

    public static class File {

        private String name;
        private String container;
        private String blob;
        private String destination;
        private boolean hot;

        private File() {}

        public File(String name, String container, String blob, String destination, boolean hot) {
            this.name = name;
            this.container = container;
            this.blob = blob;
            this.destination = destination;
            this.hot = hot;
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

        public boolean isHot() {
            return this.hot;
        }

    }

}
