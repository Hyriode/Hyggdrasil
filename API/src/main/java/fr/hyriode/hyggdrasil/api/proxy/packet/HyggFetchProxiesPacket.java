package fr.hyriode.hyggdrasil.api.proxy.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;

import java.util.Arrays;
import java.util.List;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/02/2022 at 14:30
 */
public class HyggFetchProxiesPacket extends HyggPacket {

    /**
     * The response to send back to {@link HyggFetchProxiesPacket}
     */
    public static class Response extends HyggResponseContent {

        /** The array of all the proxies */
        private final HyggProxy[] proxies;

        /**
         * Constructor of {@link Response}
         *
         * @param proxies A list of {@link HyggProxy}
         */
        public Response(List<HyggProxy> proxies) {
            this.proxies = proxies.toArray(new HyggProxy[0]);
        }

        /**
         * Get all the proxies fetched
         *
         * @return A list of {@link HyggProxy}
         */
        public List<HyggProxy> getProxies() {
            return Arrays.asList(this.proxies);
        }

    }

}
