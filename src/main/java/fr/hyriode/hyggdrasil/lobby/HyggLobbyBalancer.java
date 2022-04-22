package fr.hyriode.hyggdrasil.lobby;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.event.HyggEventBus;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerStartedEvent;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerStoppedEvent;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerUpdatedEvent;
import fr.hyriode.hyggdrasil.api.lobby.HyggLobbyAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggEvacuatePacket;
import fr.hyriode.hyggdrasil.api.scheduler.HyggScheduler;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 18/04/2022 at 19:27
 */
public class HyggLobbyBalancer {

    private static final int MINIMUM_LOBBIES = Integer.parseInt(System.getenv("MINIMUM_LOBBIES"));
    private static final int MIN_PLAYERS = 5;

    private final List<HyggServer> lobbies;
    private final List<String> startedLobbies;

    private final Hyggdrasil hyggdrasil;

    public HyggLobbyBalancer(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.lobbies = new CopyOnWriteArrayList<>();
        this.startedLobbies = new ArrayList<>();

        for (int i = 0; i < MINIMUM_LOBBIES; i++) {
            this.startLobby();
        }

        this.startTasks();
        this.registerListeners();
    }

    private void startTasks() {
        System.out.println("Starting lobby balancing tasks...");

        final HyggScheduler scheduler = this.hyggdrasil.getAPI().getScheduler();

        scheduler.schedule(() -> {
            try (final Jedis jedis = hyggdrasil.getRedis().getJedis()) {
                for (HyggServer lobby : this.lobbies) {
                    jedis.zrem(HyggLobbyAPI.REDIS_KEY, lobby.getName());
                    jedis.zadd(HyggLobbyAPI.REDIS_KEY, lobby.getPlayers().size(), lobby.getName());
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        scheduler.schedule(this::process, 500, 500, TimeUnit.MILLISECONDS);
    }

    private void registerListeners() {
        final HyggEventBus eventBus = this.hyggdrasil.getAPI().getEventBus();

        eventBus.subscribe(HyggServerStartedEvent.class, event -> {
            final HyggServer server = event.getServer();

            if (server.getType().equals(HyggLobbyAPI.TYPE)) {
                this.addLobby(server.getName());
            }
        });
        eventBus.subscribe(HyggServerUpdatedEvent.class, event -> {
            final HyggServer server = event.getServer();

            if (server.getType().equals(HyggLobbyAPI.TYPE)) {
                final String serverName = server.getName();

                if (server.getState() == HyggServerState.PLAYING) {
                    this.startedLobbies.remove(serverName);

                    this.addLobby(serverName);
                } else {
                    this.removeLobby(serverName);
                }
            }
        });
        eventBus.subscribe(HyggServerStoppedEvent.class, event -> {
            final HyggServer server = event.getServer();

            if (server.getType().equals(HyggLobbyAPI.TYPE)) {
                this.removeLobby(server.getName());
            }
        });
    }

    private void process() {
        final int lobbiesNumber = this.getLobbiesNumber();
        final int neededLobbies = this.neededLobbies();

        if (lobbiesNumber < neededLobbies) {
            for (int i = neededLobbies - lobbiesNumber; i > 0; i--) {
                this.startLobby();
            }
        } else if (lobbiesNumber > neededLobbies && lobbiesNumber > MINIMUM_LOBBIES) {
            for (HyggServer server : this.lobbies) {
                if (this.getLobbiesNumber() == neededLobbies || this.getLobbiesNumber() <= MINIMUM_LOBBIES) {
                    break;
                }

                if (server.getPlayers().size() <= MIN_PLAYERS) {
                    this.hyggdrasil.getServerManager().stopServer(server.getName(), 30);
                    this.hyggdrasil.getAPI().getPacketProcessor().request(HyggChannel.PROXIES, new HyggEvacuatePacket(server.getName(), this.hyggdrasil.getAPI().getLobbyAPI().getBestLobby()));
                }
            }
        }
    }

    private void startLobby() {
        final HyggServer lobby = this.hyggdrasil.getServerManager().startServer(HyggLobbyAPI.TYPE, new HyggServerOptions(), new HyggData());

        if (lobby != null) {
            lobby.setSlots(HyggLobbyAPI.MAX_PLAYERS);

            this.startedLobbies.add(lobby.getName());
        }
    }

    private int neededLobbies() {
        return (int) Math.ceil((double) this.getPlayersOnLobbies() * 1.1 / HyggLobbyAPI.MAX_PLAYERS);
    }

    public void addLobby(String name) {
        if (!this.existsLobby(name)) {
            this.lobbies.add(this.hyggdrasil.getServerManager().getServerByName(name));
        }
    }

    public void removeLobby(String name) {
        try (final Jedis jedis = hyggdrasil.getRedis().getJedis()) {
            jedis.zrem(HyggLobbyAPI.REDIS_KEY, name);
        }

        this.lobbies.remove(this.hyggdrasil.getServerManager().getServerByName(name));
    }

    private int getPlayersOnLobbies() {
        int players = 0;

        for (HyggServer server : this.lobbies) {
            players += server.getPlayers().size();
        }

        return players;
    }

    private int getLobbiesNumber() {
        return this.lobbies.size() + startedLobbies.size();
    }

    private boolean existsLobby(String name) {
        for (HyggServer server : this.lobbies) {
            if (server.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
