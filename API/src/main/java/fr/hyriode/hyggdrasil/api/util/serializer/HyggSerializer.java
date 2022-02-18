package fr.hyriode.hyggdrasil.api.util.serializer;

import com.google.gson.*;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.lang.reflect.Type;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 16:05
 */
public class HyggSerializer<T extends HyggSerializable> implements JsonSerializer<T>, JsonDeserializer<HyggSerializable> {

    private static final String CLASS = "class";
    private static final String CONTENT = "content";

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject object = new JsonObject();

        object.addProperty(CLASS, src.getClass().getName());
        object.addProperty(CONTENT, HyggdrasilAPI.NORMAL_GSON.toJson(src));

        return object;
    }

    @Override
    public HyggSerializable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            final JsonObject object = json.getAsJsonObject();
            final Class<?> clazz = Class.forName(object.get(CLASS).getAsString());
            final String content = object.get(CONTENT).getAsString();

            return (HyggSerializable) HyggdrasilAPI.NORMAL_GSON.fromJson(content, clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
