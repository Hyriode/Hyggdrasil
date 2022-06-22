package fr.hyriode.hyggdrasil.api.lobby;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 20/04/2022 at 16:32
 */
public class HyggLobbyAPI {

    /** The maximum amount of players on a lobby */
    public static final int MAX_PLAYERS = 50;
    /** The redis key used to balance lobby */
    public static final String REDIS_KEY = "lobby-balancer";
    /** The type of the lobby servers */
    public static final String TYPE = "lobby";
    /** The Redis key of the current lobby map */
    public static final String MAP = "lobby-map";
    /** The game type of lobby */
    public static final String GAME_TYPE = "default";
    /** The default map name of the lobby */
    public static final String DEFAULT_MAP = "normal";

    private final HyggdrasilAPI hyggdrasilAPI;

    public HyggLobbyAPI(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
    }

    /**
     * Get the best lobby on the network
     *
     * @return A server name
     */
    public String getBestLobby() {
        try (final Jedis jedis = this.hyggdrasilAPI.getJedis()) {
            final List<String> lobbies = jedis.zrange(REDIS_KEY, 0, 0);

            if (lobbies != null && lobbies.size() > 0) {
                return lobbies.get(0);
            }
        }
        return null;
    }

    /**
     * Get the current map of the lobby
     *
     * @return A map name
     */
    public String getCurrentMap() {
        try (final Jedis jedis = this.hyggdrasilAPI.getJedis()) {
            final String map = jedis.get(MAP);

            if (map != null) {
                return map;
            }
            return DEFAULT_MAP;
        }
    }

    /**
     * Set the current map
     *
     * @param map The new current map
     */
    public void setCurrentMap(String map) {
        try (final Jedis jedis = this.hyggdrasilAPI.getJedis()) {
            jedis.set(MAP, map);
        }
    }

}
