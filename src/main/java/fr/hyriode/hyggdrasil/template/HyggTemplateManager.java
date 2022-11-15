package fr.hyriode.hyggdrasil.template;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.util.References;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by AstFaster
 * on 30/10/2022 at 15:20
 */
public class HyggTemplateManager {

    private final Map<String, HyggTemplate> templates = new HashMap<>();

    private final HyggTemplateDownloader downloader;

    public HyggTemplateManager(Hyggdrasil hyggdrasil) {
        this.downloader = new HyggTemplateDownloader(hyggdrasil, this);

        this.loadTemplates();

        this.downloader.start();
    }

    private void loadTemplates() {
        final Yaml yaml = new Yaml();

        yaml.setBeanAccess(BeanAccess.FIELD);

        try (final Stream<Path> stream = Files.list(References.TEMPLATES_FOLDER)) {
            stream.forEach(path -> {
                try {
                    if (path.endsWith(".yml") || path.endsWith(".yaml")) {
                        final HyggTemplate template = yaml.loadAs(Files.newInputStream(path), HyggTemplate.class);

                        this.templates.put(template.getName(), template);
                    }
                } catch (IOException e) {
                    System.err.println("Invalid yaml file in templates directory! Error:" + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HyggTemplate getTemplate(String name) {
        return this.templates.get(name);
    }

    public HyggTemplateDownloader getDownloader() {
        return this.downloader;
    }

    public Map<String, HyggTemplate> getTemplates() {
        return this.templates;
    }

}
