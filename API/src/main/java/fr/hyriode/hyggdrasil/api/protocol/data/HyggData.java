package fr.hyriode.hyggdrasil.api.protocol.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 11:56.<br>
 *
 * Represents the data provided to an application started by Hyggdrasil.
 */
public class HyggData {

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
     * Add a given data to the dictionary.
     *
     * @param key The key of the data to add
     * @param data The data to add
     */
    public void add(String key, String data) {
        this.data.put(key, data);
    }

    /**
     * Add a given data as an object to the dictionary.<br>
     * The object will be serialized in JSON format.
     *
     * @param key The key of the data to add
     * @param data The data to add
     */
    public void addObject(String key, Object data) {
        this.data.put(key, HyggdrasilAPI.GSON.toJson(data));
    }

    /**
     * Get a data by its key from the dictionary.
     *
     * @param key The key of the data to get
     * @return The data linked to the key; or <code>null</code> if nothing was found
     */
    public String get(String key) {
        return this.data.get(key);
    }

    /**
     * Get a data by its key from the dictionary (as an object).<br>
     * The data will be deserialized from JSON format.
     *
     * @param key The key of the data to get
     * @param outputClass The output class of the object to get
     * @return The data linked to the key; or <code>null</code> if nothing was found
     * @param <T> The type of the output
     */
    public <T> T getObject(String key, Class<T> outputClass) {
        return HyggdrasilAPI.GSON.fromJson(this.get(key), outputClass);
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
     * The JSON serializer of {@link HyggData}.
     */
    public static class Serializer implements JsonSerializer<HyggData>, JsonDeserializer<HyggData> {

        private final Type type = new TypeToken<Map<String, String>>(){}.getType();

        @Override
        public JsonElement serialize(HyggData src, Type type, JsonSerializationContext ctx) {
            return ctx.serialize(src.getData());
        }

        @Override
        public HyggData deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            return new HyggData(ctx.deserialize(json, this.type));
        }

    }

}
