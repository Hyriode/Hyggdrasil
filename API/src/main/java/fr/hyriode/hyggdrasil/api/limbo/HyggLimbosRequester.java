package fr.hyriode.hyggdrasil.api.limbo;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.limbo.packet.HyggStartLimboPacket;
import fr.hyriode.hyggdrasil.api.limbo.packet.HyggStopLimboPacket;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggRequest;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggLimboContent;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Created by AstFaster
 * on 25/12/2022 at 15:28.<br>
 *
 * The requester implementation for limbos.
 */
public class HyggLimbosRequester {

    /** The Redis key of limbos */
    public static final String REDIS_KEY = HyggdrasilAPI.REDIS_KEY + "limbos:";

    /** The {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggLimbosRequester}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggLimbosRequester(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
    }

    /**
     * Fetch all limbos from cache
     *
     * @return A list of {@link HyggLimbo}
     */
    public Set<HyggLimbo> fetchLimbos() {
        return this.hyggdrasilAPI.redisGet(jedis -> {
            final Set<HyggLimbo> proxies = new HashSet<>();

            for (String key : jedis.keys(REDIS_KEY + "*")) {
                proxies.add(HyggdrasilAPI.GSON.fromJson(jedis.get(key), HyggLimbo.class));
            }
            return Collections.unmodifiableSet(proxies);
        });
    }

    /**
     * Fetch a wanted limbo from cache
     *
     * @param limboName The name of the limbo to fetch
     * @return The found {@link HyggLimbo}; or <code>null</code> if nothing was found
     */
    public HyggLimbo fetchLimbo(String limboName) {
        return this.hyggdrasilAPI.redisGet(jedis -> {
            final String json = jedis.get(REDIS_KEY + limboName);

            return json == null ? null : HyggdrasilAPI.GSON.fromJson(json, HyggLimbo.class);
        });
    }

    /**
     * Create a limbo by asking Hyggdrasil.
     *
     * @param limboType The type of the limbo to create
     * @param limboData The data of the limbo to create
     * @param onCreated The {@link Consumer} to call when the limbo will be created
     */
    public void createLimbo(@NotNull HyggLimbo.Type limboType, @NotNull HyggData limboData, Consumer<HyggLimbo> onCreated) {
        this.query(new HyggStartLimboPacket(limboType, limboData))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();
                    final HyggResponseContent content = response.getContent();

                    if (type == SUCCESS) {
                        if (content != null && onCreated != null) {
                            onCreated.accept(content.as(HyggLimboContent.class).getLimbo());
                        }
                    } else {
                        System.err.println("Couldn't create a limbo. Returned message: " + type + ".");
                    }
                }).exec();
    }

    /**
     * Create a limbo by asking Hyggdrasil (but without any data).
     *
     * @param type The type of the limbo to create
     * @param onCreated The {@link Consumer} to call when the limbo will be created
     */
    public void createLimbo(@NotNull HyggLimbo.Type type, Consumer<HyggLimbo> onCreated) {
        this.createLimbo(type, new HyggData(), onCreated);
    }

    /**
     * Remove a limbo by giving its name
     *
     * @param limboName The name of the limbo to remove
     * @param onRemoved The {@link Runnable} to run when the limbo will be removed
     */
    public void removeLimbo(String limboName, Runnable onRemoved) {
        this.query(new HyggStopLimboPacket(limboName))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();

                    if (type == SUCCESS && onRemoved != null) {
                        onRemoved.run();
                    }
                }).exec();
    }

    /**
     * Private method used to simplify query
     *
     * @param packet The packet to send
     * @return The created {@link HyggRequest}
     */
    private HyggRequest query(HyggPacket packet) {
        return this.hyggdrasilAPI.getPacketProcessor().request(HyggChannel.QUERY, packet);
    }

}
