package fr.hyriode.hyggdrasil.api.protocol.environment;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 11:56
 */
public class HyggData {

    /** The environment variable key */
    private static final String KEY = HyggdrasilAPI.PREFIX + "_DATA";

    /** The map of all data */
    private final Map<String, String> data;

    /**
     * Empty constructor of {@link HyggData}
     */
    public HyggData() {
        this.data = new HashMap<>();
    }

    /**
     * Constructor of {@link HyggData}
     *
     * @param data The map of data
     */
    public HyggData(Map<String, String> data) {
        this.data = data;
    }

    /**
     * Add a given data
     *
     * @param key The ky of the data
     * @param data The data as json
     */
    public void add(String key, String data) {
        this.data.put(key, data);
    }

    /**
     * Get a data by its key
     *
     * @param key The key of the data
     * @return The data linked to the key
     */
    public String get(String key) {
        return this.data.get(key);
    }

    /**
     * Get all the data stored
     *
     * @return A map of data
     */
    public Map<String, String> getData() {
        return this.data;
    }

    /**
     * Transform the data dictionary to a json stored in environment variable
     *
     * @return A string that represents the environment variable
     */
    String asEnvironmentVariable() {
        return KEY + "=" + HyggdrasilAPI.GSON.toJson(this);
    }

    /**
     * Load the dictionary of data from environment variables
     *
     * @return The loaded {@link HyggData} object
     */
    static HyggData loadFromEnvironmentVariables() {
        HyggdrasilAPI.log("Loading data dictionary from environment variables...");

        final String json = System.getenv(KEY);

        if (json == null) {
            return new HyggData();
        }
        return HyggdrasilAPI.GSON.fromJson(json, HyggData.class);
    }

}
