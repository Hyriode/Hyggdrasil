package fr.hyriode.hyggdrasil.template;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by AstFaster
 * on 30/10/2022 at 18:11
 */
public class HyggTemplateDownloader {

    private final Map<String, String> filesHashes = new HashMap<>();

    private final BlobServiceClient azureService;
    private final Map<String, BlobContainerClient> azureContainers;

    private final Hyggdrasil hyggdrasil;
    private final HyggTemplateManager templateManager;

    public HyggTemplateDownloader(Hyggdrasil hyggdrasil, HyggTemplateManager templateManager) {
        this.hyggdrasil = hyggdrasil;
        this.templateManager = templateManager;
        this.azureService = new BlobServiceClientBuilder().connectionString(Hyggdrasil.getConfig().getAzure().getConnectionString()).buildClient();
        this.azureContainers = new HashMap<>();
    }

    public void start() {
        IOUtil.createDirectory(References.TMP_FOLDER);

        this.hyggdrasil.getAPI().getExecutorService().scheduleAtFixedRate(() -> {
            for (HyggTemplate template : this.templateManager.getTemplates().values()) {
                this.process(template);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void process(HyggTemplate template) {
        for (Map.Entry<String, HyggTemplate.Plugin> entry : template.getPlugins().entrySet()) {
            final String name = entry.getKey();
            final HyggTemplate.Plugin plugin = entry.getValue();
            final BlobContainerClient container = this.azureContainers.getOrDefault(plugin.getContainer(), this.azureService.getBlobContainerClient(plugin.getContainer()));

            this.azureContainers.put(plugin.getContainer(), container);

            for (BlobItem blobItem : container.listBlobs()) {
                final String blobName = blobItem.getName();

                if (blobName.matches(plugin.getBlob())) {
                    final String hash = IOUtil.toHexString(blobItem.getProperties().getContentMd5());
                    final String oldHash = this.filesHashes.get(name);

                    if (hash.equals(oldHash)) {
                        break;
                    }

                    final BlobClient blobClient = container.getBlobClient(blobName);

                    blobClient.downloadToFile(Paths.get(References.TMP_FOLDER.toString(), plugin.getName() + ".jar").toString(), true);

                    this.filesHashes.put(name, hash);
                    break;
                }
            }
        }
    }

    public List<Path> getPluginsFiles(HyggTemplate template) {
        final List<Path> files = new ArrayList<>();

        for (HyggTemplate.Plugin plugin : template.getPlugins().values()) {
            files.add(Paths.get(References.TMP_FOLDER.toString(), plugin.getName() + ".jar"));
        }
        return files;
    }

}
