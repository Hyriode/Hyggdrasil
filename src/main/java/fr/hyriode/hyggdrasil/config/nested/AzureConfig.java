package fr.hyriode.hyggdrasil.config.nested;

/**
 * Created by AstFaster
 * on 30/10/2022 at 14:34
 */
public class AzureConfig {

    private final String connectionString;
    private final String blobsContainer;
    private final String blobsPrefix;

    public AzureConfig(String connectionString, String blobsContainer, String blobsPrefix) {
        this.connectionString = connectionString;
        this.blobsContainer = blobsContainer;
        this.blobsPrefix = blobsPrefix;
    }

    public String getConnectionString() {
        return this.connectionString;
    }

    public String getBlobsContainer() {
        return this.blobsContainer;
    }

    public String getBlobsPrefix() {
        return this.blobsPrefix;
    }

}
