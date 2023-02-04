package fr.hyriode.hyggdrasil.api.proxy;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.event.HyggEventListener;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyUpdatedEvent;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggRequest;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggProxyContent;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggStartProxyPacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggStopProxyPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 20:53.<br>
 *
 * The requester implementation for proxies.
 */
public class HyggProxiesRequester {

    /** The Redis key of proxies */
    public static final String REDIS_KEY = HyggdrasilAPI.REDIS_KEY + "proxies:";

    /** The {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggProxiesRequester}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggProxiesRequester(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
    }

    /**
     * Fetch all proxies from cache.
     *
     * @return A list of {@link HyggProxy}
     */
    public Set<HyggProxy> fetchProxies() {
        return this.hyggdrasilAPI.redisGet(jedis -> {
            final Set<HyggProxy> proxies = new HashSet<>();

            for (String key : jedis.keys(REDIS_KEY + "*")) {
                proxies.add(HyggdrasilAPI.GSON.fromJson(jedis.get(key), HyggProxy.class));
            }
            return Collections.unmodifiableSet(proxies);
        });
    }

    /**
     * Fetch a wanted proxy from cache
     *
     * @param proxyName The name of the proxy to fetch
     * @return The found {@link HyggProxy}; or <code>null</code> if nothing was found
     */
    public HyggProxy fetchProxy(String proxyName) {
        return this.hyggdrasilAPI.redisGet(jedis -> {
            final String json = jedis.get(REDIS_KEY + proxyName);

            return json == null ? null : HyggdrasilAPI.GSON.fromJson(json, HyggProxy.class);
        });
    }

    /**
     * Create a proxy by asking Hyggdrasil.
     *
     * @param onCreated The {@link Consumer} to call when the proxy will be created
     * @param proxyData The data of the proxy to create
     */
    public void createProxy(Consumer<HyggProxy> onCreated, @NotNull HyggData proxyData) {
        this.query(new HyggStartProxyPacket(proxyData))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();
                    final HyggResponseContent content = response.getContent();

                    if (type == SUCCESS) {
                        if (content != null && onCreated != null) {
                            onCreated.accept(content.as(HyggProxyContent.class).getProxy());
                        }
                    } else {
                        System.err.println("Couldn't create a proxy. Returned message: " + type + ".");
                    }
                }).exec();
    }

    /**
     * Create a proxy by asking Hyggdrasil (but without any data).
     *
     * @param onCreated The {@link Consumer} to call when the proxy will be created
     */
    public void createProxy(Consumer<HyggProxy> onCreated) {
        this.createProxy(onCreated, new HyggData());
    }

    /**
     * Remove a proxy by giving its name
     *
     * @param proxyName The name of the proxy to remove
     * @param onRemoved The {@link Runnable} to run when the proxy will be removed
     */
    public void removeProxy(String proxyName, Runnable onRemoved) {
        this.query(new HyggStopProxyPacket(proxyName))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();

                    if (type == SUCCESS && onRemoved != null) {
                        onRemoved.run();
                    }
                }).exec();
    }

    /**
     * Wait for a proxy to be at a given state
     *
     * @param proxyName The name of the concerned proxy
     * @param waitingState The state to wait for
     * @param callback The {@link Consumer} to call when the proxy has the good state
     */
    public void waitForProxyState(String proxyName, HyggProxy.State waitingState, Consumer<HyggProxy> callback) {
        this.hyggdrasilAPI.getEventBus().subscribe(HyggProxyUpdatedEvent.class, new WaitingStateListener(proxyName, waitingState, callback));
    }

    /**
     * Wait for a proxy to be at a given state
     *
     * @param proxyName The name of the concerned proxy
     * @param waitingPlayers The amount of players to wait for
     * @param callback The {@link Consumer} to call when the proxy has the good state
     */
    public void waitForProxyPlayers(String proxyName, int waitingPlayers, Consumer<HyggProxy> callback) {
        this.hyggdrasilAPI.getEventBus().subscribe(HyggProxyUpdatedEvent.class, new WaitingPlayersListener(proxyName, waitingPlayers, callback));
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

    private class WaitingStateListener implements HyggEventListener<HyggProxyUpdatedEvent> {

        private final String proxyName;
        private final HyggProxy.State waitingState;
        private final Consumer<HyggProxy> callback;

        public WaitingStateListener(String proxyName, HyggProxy.State waitingState, Consumer<HyggProxy> callback) {
            this.proxyName = proxyName;
            this.waitingState = waitingState;
            this.callback = callback;
        }

        @Override
        public void onEvent(HyggProxyUpdatedEvent event) {
            final HyggProxy proxy = event.getProxy();

            if (proxy.getName().equals(this.proxyName) && event.getProxyState() == this.waitingState) {
                this.callback.accept(proxy);

                this.unsubscribe(hyggdrasilAPI.getEventBus());
            }
        }

    }

    private class WaitingPlayersListener implements HyggEventListener<HyggProxyUpdatedEvent> {

        private final String proxyName;
        private final int waitingPlayers;
        private final Consumer<HyggProxy> callback;

        public WaitingPlayersListener(String proxyName, int waitingPlayers, Consumer<HyggProxy> callback) {
            this.proxyName = proxyName;
            this.waitingPlayers = waitingPlayers;
            this.callback = callback;
        }

        @Override
        public void onEvent(HyggProxyUpdatedEvent event) {
            final HyggProxy proxy = event.getProxy();

            if (proxy.getName().equals(this.proxyName) && event.getProxyPlayers().size() == this.waitingPlayers) {
                this.callback.accept(proxy);

                this.unsubscribe(hyggdrasilAPI.getEventBus());
            }
        }

    }

}
