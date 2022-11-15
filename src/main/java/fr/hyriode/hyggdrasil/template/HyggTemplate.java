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

        private String blob;

        private Plugin() {}

        public Plugin(String blob) {
            this.blob = blob;
        }

        public String getBlob() {
            return this.blob;
        }

    }

}
