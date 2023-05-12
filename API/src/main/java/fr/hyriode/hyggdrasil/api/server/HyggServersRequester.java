package fr.hyriode.hyggdrasil.api.server;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.event.HyggEventListener;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerUpdatedEvent;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggRequest;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.content.HyggServerContent;
import fr.hyriode.hyggdrasil.api.server.packet.HyggStartServerPacket;
import fr.hyriode.hyggdrasil.api.server.packet.HyggStopServerPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * The requester implementation for servers.
 */
public class HyggServersRequester {

    /** The Redis key of servers */
    public static final String REDIS_KEY = HyggdrasilAPI.REDIS_KEY + "servers:";

    /** The {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggServersRequester}
     *
     * @param hyggdrasilAPI {@link HyggdrasilAPI} instance
     */
    public HyggServersRequester(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;
    }

    /**
     * Fetch all servers from cache.
     *
     * @return A list of {@link HyggServer}
     */
    public Set<HyggServer> fetchServers() {
        return this.hyggdrasilAPI.redisGet(jedis -> {
            final Set<HyggServer> servers = new HashSet<>();

            for (String key : jedis.keys(REDIS_KEY + "*")) {
                servers.add(HyggdrasilAPI.GSON.fromJson(jedis.get(key), HyggServer.class));
            }
            return Collections.unmodifiableSet(servers);
        });
    }

    /**
     * Fetch a wanted server from cache
     *
     * @param serverName The name of the server to fetch
     * @return The found {@link HyggServer}; or <code>null</code> if nothing was found
     */
    public HyggServer fetchServer(String serverName) {
        return this.hyggdrasilAPI.redisGet(jedis -> {
            final String json = jedis.get(REDIS_KEY + serverName);

            return json == null ? null : HyggdrasilAPI.GSON.fromJson(json, HyggServer.class);
        });
    }

    /**
     * Create a server with a given type by asking Hyggdrasil
     *
     * @param serverInfo The information of the server to create
     * @param onCreated The {@link Consumer} to call when the server will be created
     */
    public void createServer(@NotNull HyggServerCreationInfo serverInfo, @Nullable Consumer<HyggServer> onCreated) {
        this.query(new HyggStartServerPacket(serverInfo))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();

                    if (type == SUCCESS) {
                        if (response.getContent() != null && onCreated != null) {
                            onCreated.accept(response.getContent().as(HyggServerContent.class).getServer());
                        }
                    } else {
                        System.err.println("Couldn't create a server with type: " + serverInfo.getType() + ". Returned message: " + type + ".");
                    }
                }).exec();
    }

    /**
     * Remove a server by giving its name
     *
     * @param serverName The name of the server to remove
     * @param onRemoved The {@link Runnable} to run when the server will be removed
     */
    public void removeServer(@NotNull String serverName, @Nullable Runnable onRemoved) {
        this.query(new HyggStopServerPacket(serverName))
                .withResponseCallback(response -> {
                    final HyggResponse.Type type = response.getType();

                    if (type == SUCCESS) {
                        if (onRemoved != null) {
                            onRemoved.run();
                        }
                    } else {
                        System.err.println("Couldn't remove a server with name: " + serverName + ". Returned message: " + type + ".");
                    }
                }).exec();
    }

    /**
     * Wait for a server to have a given state
     *
     * @param serverName The name of the concerned server
     * @param waitingState The state to wait for
     * @param callback The {@link Consumer} to call when the server has the good state
     */
    public void waitForServerState(@NotNull String serverName, @NotNull HyggServer.State waitingState, @NotNull Consumer<HyggServer> callback) {
        this.hyggdrasilAPI.getEventBus().subscribe(HyggServerUpdatedEvent.class, new WaitingStateListener(serverName, waitingState, callback));
    }

    /**
     * Wait for a server to be at a given state
     *
     * @param serverName The name of the concerned server
     * @param waitingPlayers The amount of players to wait for
     * @param callback The {@link Consumer} to call when the server has the good state
     */
    public void waitForServerPlayers(@NotNull String serverName, int waitingPlayers, @NotNull Consumer<HyggServer> callback) {
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
        private final HyggServer.State waitingState;
        private final Consumer<HyggServer> callback;

        public WaitingStateListener(String serverName, HyggServer.State waitingState, Consumer<HyggServer> callback) {
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
