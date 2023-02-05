package fr.hyriode.hyggdrasil.template;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;
import fr.hyriode.hyggdrasil.util.YamlLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/**
 * Created by AstFaster
 * on 30/10/2022 at 15:20
 */
public class HyggTemplateManager {

    private final Map<String, HyggTemplate> templates = new HashMap<>();

    private final HyggTemplateDownloader downloader;

    public HyggTemplateManager(Hyggdrasil hyggdrasil) {
        this.loadTemplates();

        this.downloader = new HyggTemplateDownloader(hyggdrasil, this);
        this.downloader.start();
    }

    private void loadTemplates() {
        IOUtil.createDirectory(References.TEMPLATES_FOLDER);

        try (final Stream<Path> stream = Files.list(References.TEMPLATES_FOLDER)) {
            stream.forEach(this::loadTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CompletableFuture<HyggTemplate> loadTemplate(Path path) {
        if (path.toString().endsWith(".yml") || path.toString().endsWith(".yaml")) {
            final HyggTemplate template = YamlLoader.load(path, HyggTemplate.class);

            if (template != null) {
                this.templates.put(template.getName(), template);

                if (this.downloader != null) {
                    this.downloader.process(template);
                }

                System.out.println("Loaded '" + template.getName() + "' template.");
            }
            return CompletableFuture.completedFuture(template);
        }
        return CompletableFuture.completedFuture(null);
    }

    public HyggTemplate getTemplate(String name) {
        final HyggTemplate template = this.templates.get(name);

        if (template == null) { // Try to load it from file
            Path path = Paths.get(References.TEMPLATES_FOLDER.toString(), name + ".yaml");

            if (!Files.exists(path)) {
                path = Paths.get(References.TEMPLATES_FOLDER.toString(), name + ".yml");
            }
            try {
                return this.loadTemplate(path).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return template;
    }

    public HyggTemplateDownloader getDownloader() {
        return this.downloader;
    }

    public Map<String, HyggTemplate> getTemplates() {
        return this.templates;
    }

}
