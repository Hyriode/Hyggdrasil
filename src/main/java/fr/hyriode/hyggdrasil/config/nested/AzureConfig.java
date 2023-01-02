package fr.hyriode.hyggdrasil.config.nested;

/**
 * Created by AstFaster
 * on 30/10/2022 at 14:34
 */
public class AzureConfig {

    private final String connectionString;

    public AzureConfig(String connectionString) {
        this.connectionString = connectionString;
    }

    public AzureConfig() {
        this("");
    }

    public String getConnectionString() {
        return this.connectionString;
    }

}
