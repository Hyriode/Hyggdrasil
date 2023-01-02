package fr.hyriode.hyggdrasil.template;

import java.util.Map;

/**
 * Created by AstFaster
 * on 30/10/2022 at 15:17
 */
public class HyggTemplate {

    private String name;
    private Map<String, Plugin> plugins;

    private HyggTemplate() {}

    public HyggTemplate(String name, Map<String, Plugin> plugins) {
        this.name = name;
        this.plugins = plugins;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Plugin> getPlugins() {
        return this.plugins;
    }

    public static class Plugin {

        private String name;
        private String container;
        private String blob;

        private Plugin() {}

        public Plugin(String name, String container, String blob) {
            this.name = name;
            this.container = container;
            this.blob = blob;
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

    }

}
