package fr.hyriode.hyggdrasil.api.server;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.event.HyggEventListener;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerUpdatedEvent;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.request.HyggRequest;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggResponseContent;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggServerResponse;
import fr.hyriode.hyggdrasil.api.server.packet.HyggFetchServerPacket;
import fr.hyriode.hyggdrasil.api.server.packet.HyggFetchServersPacket;
import fr.hyriode.hyggdrasil.api.server.packet.HyggStartServerPacket;
import fr.hyriode.hyggdrasil.api.server.packet.HyggStopServerPacket;

import java.util.List;
import java.util.function.Consumer;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/02/2022 at 20:53
 */
public class HyggServerRequester {

    /** The {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggServerRequester}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggServerRequester(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
    }

    /**
     * Fetch all servers from Hyggdrasil
     *
     * @param onFetched The {@link Consumer} to call when the servers will be fetched
     */
    public void fetchServers(Consumer<List<HyggServer>> onFetched) {
        this.fetchServers("", onFetched);
    }

    /**
     * Fetch all servers from Hyggdrasil
     *
     * @param serversType The type of the servers to fetch
     * @param onFetched The {@link Consumer} to call when the servers will be fetched
     */
    public void fetchServers(String serversType, Consumer<List<HyggServer>> onFetched) {
        this.query(new HyggFetchServersPacket(serversType))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();
                    final HyggResponseContent content = response.getContent();

                    if (type == SUCCESS && content != null) {
                        if (content instanceof HyggFetchServersPacket.Response && onFetched != null) {
                            onFetched.accept(((HyggFetchServersPacket.Response) content).getServers());
                        }
                    } else {
                        System.err.println("Couldn't fetch servers with type: " + serversType + ". Returned message: " + type + ".");
                    }
                })
                .exec();
    }

    /**
     * Fetch a server from Hyggdrasil by providing its name
     *
     * @param serverName The name of the server to fetch
     * @param onFetched The {@link Consumer} to call when the server will be fetched
     */
    public void fetchServer(String serverName, Consumer<HyggServer> onFetched) {
        this.query(new HyggFetchServerPacket(serverName))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();
                    final HyggResponseContent content = response.getContent();

                    if (type == SUCCESS && content != null) {
                        if (content instanceof HyggServerResponse && onFetched != null) {
                            onFetched.accept(((HyggServerResponse) content).getServer());
                        }
                    } else {
                        System.err.println("Couldn't fetch a server with name: " + serverName + ". Returned message: " + type + ".");
                    }
                })
                .exec();
    }

    /**
     * Create a server with a given type
     *
     * @param request The request to create the server
     * @param onCreated The {@link Consumer} to call when the server will be created
     */
    public void createServer(HyggServerRequest request, Consumer<HyggServer> onCreated) {
        this.query(new HyggStartServerPacket(request))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();
                    final HyggResponseContent content = response.getContent();

                    if (type == SUCCESS && content != null) {
                        if (content instanceof HyggServerResponse && onCreated != null) {
                            onCreated.accept(((HyggServerResponse) content).getServer());
                        }
                    } else {
                        System.err.println("Couldn't create a server with type: " + request.getServerType() + ". Returned message: " + type + ".");
                    }
                }).exec();
    }

    /**
     * Remove a server by giving its name
     *
     * @param serverName The name of the server to remove
     * @param onRemoved The {@link Runnable} to run when the server will be removed
     */
    public void removeServer(String serverName, Runnable onRemoved) {
        this.query(new HyggStopServerPacket(serverName))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();

                    if (type != SUCCESS) {
                        System.err.println("Couldn't remove a server with name: " + serverName + ". Returned message: " + type + ".");
                    }

                    if (onRemoved != null) {
                        onRemoved.run();
                    }
                }).exec();
    }

    /**
     * Wait for a server to be at a given state
     *
     * @param serverName The name of the concerned server
     * @param waitingState The state to wait for
     * @param callback The {@link Consumer} to call when the server has the good state
     */
    public void waitForServerState(String serverName, HyggServerState waitingState, Consumer<HyggServer> callback) {
        this.hyggdrasilAPI.getEventBus().subscribe(HyggServerUpdatedEvent.class, new WaitingStateListener(serverName, waitingState, callback));
    }

    /**
     * Wait for a server to be at a given state
     *
     * @param serverName The name of the concerned server
     * @param waitingPlayers The amount of players to wait for
     * @param callback The {@link Consumer} to call when the server has the good state
     */
    public void waitForServerPlayers(String serverName, int waitingPlayers, Consumer<HyggServer> callback) {
        this.hyggdrasilAPI.getEventBus().subscribe(HyggServerUpdatedEvent.class, new WaitingPlayersListener(serverName, waitingPlayers, callback));
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

    private class WaitingStateListener implements HyggEventListener<HyggServerUpdatedEvent> {

        private final String serverName;
        private final HyggServerState waitingState;
        private final Consumer<HyggServer> callback;

        public WaitingStateListener(String serverName, HyggServerState waitingState, Consumer<HyggServer> callback) {
            this.serverName = serverName;
            this.waitingState = waitingState;
            this.callback = callback;
        }

        @Override
        public void onEvent(HyggServerUpdatedEvent event) {
            final HyggServer server = event.getServer();

            if (server.getName().equals(this.serverName) && event.getServerState() == this.waitingState) {
                this.callback.accept(server);

                this.unsubscribe(hyggdrasilAPI.getEventBus());
            }
        }

    }

    private class WaitingPlayersListener implements HyggEventListener<HyggServerUpdatedEvent> {

        private final String serverName;
        private final int waitingPlayers;
        private final Consumer<HyggServer> callback;

        public WaitingPlayersListener(String serverName, int waitingPlayers, Consumer<HyggServer> callback) {
            this.serverName = serverName;
            this.waitingPlayers = waitingPlayers;
            this.callback = callback;
        }

        @Override
        public void onEvent(HyggServerUpdatedEvent event) {
            final HyggServer server = event.getServer();

            if (server.getName().equals(this.serverName) && event.getServerPlayers().size() == this.waitingPlayers) {
                this.callback.accept(server);

                this.unsubscribe(hyggdrasilAPI.getEventBus());
            }
        }

    }

}
