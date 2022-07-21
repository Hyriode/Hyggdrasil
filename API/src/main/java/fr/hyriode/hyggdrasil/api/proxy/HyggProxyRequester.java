package fr.hyriode.hyggdrasil.api.proxy;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.event.HyggEventListener;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyUpdatedEvent;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.request.HyggRequest;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggProxyResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggFetchProxiesPacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggFetchProxyPacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggStartProxyPacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.api.server.packet.HyggStopServerPacket;

import java.util.List;
import java.util.function.Consumer;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 20:53
 */
public class HyggProxyRequester {

    /** The {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggProxyRequester}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggProxyRequester(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
    }

    /**
     * Fetch all proxies from Hyggdrasil
     *
     * @param onFetched The {@link Consumer} to call when the proxies will be fetched
     */
    public void fetchProxies(Consumer<List<HyggProxy>> onFetched) {
        this.query(new HyggFetchProxiesPacket())
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();
                    final HyggResponseContent content = response.getContent();

                    if (type == SUCCESS && content != null) {
                        if (content instanceof HyggFetchProxiesPacket.Response && onFetched != null) {
                            onFetched.accept(((HyggFetchProxiesPacket.Response) content).getProxies());
                        }
                    } else {
                        System.err.println("Couldn't fetch proxies. Returned message: " + type + ".");
                    }
                })
                .exec();
    }

    /**
     * Fetch a proxy from Hyggdrasil by providing its name
     *
     * @param proxyName The name of the proxy to fetch
     * @param onFetched The {@link Consumer} to call when the proxy will be fetched
     */
    public void fetchProxy(String proxyName, Consumer<HyggProxy> onFetched) {
        this.query(new HyggFetchProxyPacket(proxyName))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();
                    final HyggResponseContent content = response.getContent();

                    if (type == SUCCESS && content != null) {
                        if (content instanceof HyggProxyResponse && onFetched != null) {
                            onFetched.accept(((HyggProxyResponse) content).getProxy());
                        }
                    } else {
                        System.err.println("Couldn't fetch a proxy with name: " + proxyName + ". Returned message: " + type + ".");
                    }
                })
                .exec();
    }

    /**
     * Create a proxy
     *
     * @param onCreated The {@link Consumer} to call when the proxy will be created
     */
    public void createProxy(Consumer<HyggProxy> onCreated) {
        this.query(new HyggStartProxyPacket())
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();
                    final HyggResponseContent content = response.getContent();

                    if (type == SUCCESS && content != null) {
                        if (content instanceof HyggProxyResponse && onCreated != null) {
                            onCreated.accept(((HyggProxyResponse) content).getProxy());
                        }
                    } else {
                        System.err.println("Couldn't create a proxy. Returned message: " + type + ".");
                    }
                }).exec();
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
    public void waitForProxyState(String proxyName, HyggProxyState waitingState, Consumer<HyggProxy> callback) {
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
        private final HyggProxyState waitingState;
        private final Consumer<HyggProxy> callback;

        public WaitingStateListener(String proxyName, HyggProxyState waitingState, Consumer<HyggProxy> callback) {
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

            if (proxy.getName().equals(this.proxyName) && event.getProxyPlayers() == this.waitingPlayers) {
                this.callback.accept(proxy);

                this.unsubscribe(hyggdrasilAPI.getEventBus());
            }
        }

    }

}
