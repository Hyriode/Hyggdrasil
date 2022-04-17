package fr.hyriode.hyggdrasil.api.server.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import fr.hyriode.hyggdrasil.api.server.HyggServer;

import java.util.Arrays;
import java.util.List;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/02/2022 at 14:30
 */
public class HyggFetchServersPacket extends HyggPacket {

    /** The type of the servers to fetch. If this type is null or empty, it will fetch all the servers */
    private final String serversType;

    /**
     * Constructor of {@link HyggFetchServersPacket}
     *
     * @param serversType The type of the servers to fetch
     */
    public HyggFetchServersPacket(String serversType) {
        this.serversType = serversType;
    }

    /**
     * Constructor of {@link HyggFetchServerPacket}
     */
    public HyggFetchServersPacket() {
        this(null);
    }

    /**
     * Get the type of the servers to fetch
     *
     * @return A server type (ex: lobby, bedwars, etc.)
     */
    public String getServersType() {
        return this.serversType;
    }

    /**
     * The response to send back to {@link HyggFetchServersPacket}
     */
    public static class Response extends HyggResponseContent {

        /** The array of all the servers */
        private final HyggServer[] servers;

        /**
         * Constructor of {@link Response}
         *
         * @param servers A list of {@link HyggServer}
         */
        public Response(List<HyggServer> servers) {
            this.servers = servers.toArray(new HyggServer[0]);
        }

        /**
         * Get all the servers fetched
         *
         * @return A list of {@link HyggServer}
         */
        public List<HyggServer> getServers() {
            return Arrays.asList(this.servers);
        }

    }

}
