package fr.hyriode.hyggdrasil.template;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Files;
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

        // First, download files synchronously
        for (HyggTemplate template : this.templateManager.getTemplates().values()) {
            this.process(template);
        }

        this.hyggdrasil.getAPI().getExecutorService().scheduleAtFixedRate(() -> {
            for (HyggTemplate template : this.templateManager.getTemplates().values()) {
                this.process(template);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public void process(HyggTemplate template) {
        for (Map.Entry<String, HyggTemplate.File> entry : template.getFiles().entrySet()) {
            final String name = entry.getKey();
            final HyggTemplate.File file = entry.getValue();
            final BlobContainerClient container = this.azureContainers.getOrDefault(file.getContainer(), this.azureService.getBlobContainerClient(file.getContainer()));

            this.azureContainers.put(file.getContainer(), container);

            for (BlobItem blobItem : container.listBlobs()) {
                final String blobName = blobItem.getName();

                if (blobName.matches(file.getBlob())) {
                    final String hash = IOUtil.toHexString(blobItem.getProperties().getContentMd5());
                    final String oldHash = this.filesHashes.get(name);

                    if (hash.equals(oldHash)) {
                        break;
                    }

                    final BlobClient blobClient = container.getBlobClient(blobName);

                    blobClient.downloadToFile(Paths.get(References.TMP_FOLDER.toString(), file.getName()).toString(), true);

                    this.filesHashes.put(name, hash);
                    break;
                }
            }
        }
    }

    public void copyFiles(HyggTemplate template, Path destination) {
        IOUtil.createDirectory(destination);

        for (Map.Entry<HyggTemplate.File, Path> entry : this.getCachedFiles(template).entrySet()) {
            final HyggTemplate.File file = entry.getKey();
            final Path path = entry.getValue();
            final Path destinationFolder = Paths.get(destination.toString(), file.getDestination());

            IOUtil.createDirectory(destinationFolder);
            IOUtil.copy(path, Paths.get(destinationFolder.toString(), path.getFileName().toString()));
        }
    }

    private Map<HyggTemplate.File, Path> getCachedFiles(HyggTemplate template) {
        final Map<HyggTemplate.File, Path> files = new HashMap<>();

        for (HyggTemplate.File file : template.getFiles().values()) {
            final Path path = Paths.get(References.TMP_FOLDER.toString(), file.getName());

            if (Files.exists(path)) {
                files.put(file, path);
            }
        }
        return files;
    }

}
